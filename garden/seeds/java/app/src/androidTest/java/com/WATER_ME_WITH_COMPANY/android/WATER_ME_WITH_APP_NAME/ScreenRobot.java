package {{company_name}}.android.{{app_package_name_prefix}};

import android.app.Activity;
import android.app.Instrumentation;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.{{company_name}}.android.{{app_package_name_prefix}}.activity.LoginActivityTest.LoginScreenRobot;
import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView;
import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType;
import com.squareup.spoon.Spoon;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Base robot class, provides a thin wrapper over Espresso calls, for a nicer
 * test interface.
 * <p/>
 * Note most core methods are protected as they should be accessed
 * via a concrete ScreenRobot subclass (e.g. {@link LoginScreenRobot}).
 * Checking enabled and visibility states are public however as providing a
 * wrapper around those provides no extra clarity or readability and adds unnecessary
 * boilerplate.
 * <p/>
 * NOTE: you do not need to provide an activity context for all usages of a screen robot
 * this is needed only for calls that will access resources or components like the
 * decor view. e.g. checking if a toast is displayed.
 */
@SuppressWarnings("unchecked")
public abstract class ScreenRobot<T extends ScreenRobot, M> {

    private static final long WAIT_FOR_ACTIVITY_TIMEOUT = TimeUnit.SECONDS.toMillis(1);
    private Activity mActivityContext; // Only required for some calls, deliberately accessible only via getContext to throw a useful exception if null
    private Fragment mFragmentUnderTest; // Only required for some calls, deliberately accessible only via getFragmentUnderTest to throw a useful exception if null
    private View mViewUnderTest;  // Only required for some calls, deliberately accessible only via getViewUnderTest to throw a useful exception if null

    public static <T extends ScreenRobot> T withRobot(Class<T> screenRobotClass) {
        if (screenRobotClass == null) {
            throw new IllegalArgumentException("Instance class == null");
        }
        try {
            return screenRobotClass.newInstance();
        } catch (IllegalAccessException iae) {
            throw new RuntimeException("IllegalAccessException", iae);
        } catch (InstantiationException ie) {
            throw new RuntimeException("InstantiationException", ie);
        }
    }

    public T screenshot(String screenshotName) {
        Spoon.screenshot(getContext(), screenshotName);
        return (T) this;
    }

    public T checkIsChecked(int... viewIds) {
        for (int viewId : viewIds) {
            getViewInteraction(viewId).check(matches(isChecked()));
        }
        return (T) this;
    }

    public T checkIsNotChecked(int... viewIds) {
        for (int viewId : viewIds) {
            getViewInteraction(viewId).check(matches(isNotChecked()));
        }
        return (T) this;
    }

    public T checkIsEnabled(int... viewIds) {
        for (int viewId : viewIds) {
            getViewInteraction(viewId).check(matches(isEnabled()));
        }
        return (T) this;
    }

    public T checkIsDisabled(@IdRes int... viewIds) {
        for (int viewId : viewIds) {
            getViewInteraction(viewId).check(matches(not(isEnabled())));
        }
        return (T) this;
    }

    public T checkIsDisplayed(@IdRes int... viewIds) {
        for (int viewId : viewIds) {
            getViewInteraction(viewId).check(matches(isDisplayed()));
        }
        return (T) this;
    }

    public T checkIsHidden(@IdRes int... viewIds) {
        for (int viewId : viewIds) {
            getViewInteraction(viewId).check(matches(not(isDisplayed())));
        }
        return (T) this;
    }

    public T checkViewHasText(@IdRes int viewId, String expected) {
        getViewInteraction(viewId).check(matches(withText(expected)));

        return (T) this;
    }

    public T checkViewContainsText(@IdRes int viewId, String expected) {
        getViewInteraction(viewId).check(matches(withText(containsString(expected))));

        return (T) this;
    }

    public T callShowErrorMethod(final ErrorType errorType, final int requestCode, final int errorCode) {
        callMethod(new Runnable() {
            @Override
            public void run() {
                getMvpView().showError(errorType, requestCode, errorCode);
            }
        });

        return (T) this;
    }

    public T callShowLoadingMethod() {
        callMethod(new Runnable() {
            @Override
            public void run() {
                getMvpView().showLoading();
            }
        });

        return (T) this;
    }

    public T callShowDataMethod(final M m) {
        callMethod(new Runnable() {
            @Override
            public void run() {
                getMvpView().showData(m);
            }
        });

        return (T) this;
    }

    public T clickOkOnDialog() {
        onView(withId(android.R.id.button2)).perform(click());

        return (T) this;
    }

    public T clickCancelOnDialog() {
        onView(withId(android.R.id.button2)).perform(click());

        return (T) this;
    }

    public T clickCloseOnDialog() {
        onView(withId(android.R.id.button2)).perform(click());

        return (T) this;
    }

    public T provideActivityContext(Activity activityContext) {
        mActivityContext = activityContext;
        return (T) this;
    }

    /**
     * Loads the provided Fragment as the subject of the test
     * @param fragment The fragment to test, will pull the activity context from this Fragment
     * @param idleProof A view that will be visible when the Fragment is loaded and idle (such as after any loading from an automatic network call)
     */
    public T provideFragmentUnderTest(Fragment fragment, @IdRes int idleProof) {
        mActivityContext = fragment.getActivity();
        mFragmentUnderTest = fragment;

        // Espresso waits until everything is idle before performing a check like this, so we will know the frag is loaded and ready
        checkIsDisplayed(idleProof);

        return (T) this;
    }

    public T provideViewUnderTest(@IdRes int viewId) {
        if (mFragmentUnderTest != null) {
            mViewUnderTest = mFragmentUnderTest.getView().findViewById(viewId);
        } else {
            mViewUnderTest = mActivityContext.findViewById(viewId);
        }

        assertThat(mViewUnderTest).isNotNull();

        return (T) this;
    }

    public T pressBack() {
        Espresso.pressBack();
        return (T) this;
    }

    public T log(String message) {
        Log.d("ScreenRobotLog", message);
        return (T) this;
    }

    /**
     *
     * For debugging only! Do not use in actual test cases
     * @param ms to sleep in milliseconds.
     * @return
     */
    public T waitFor(long ms) {
        SystemClock.sleep(ms);
        return (T) this;
    }

    protected <A extends Activity> A getContext() {
        if (mActivityContext == null && (mFragmentUnderTest == null || (mActivityContext = mFragmentUnderTest.getActivity()) == null)) {
            throw new IllegalStateException("Robot has no Activity Context, have you called provideActivityContext() or provideFragmentUnderTest()?");
        }
        return (A) mActivityContext;
    }

    protected <F extends Fragment> F getFragmentUnderTest() {
        if (mFragmentUnderTest == null) {
            throw new IllegalStateException("Robot has no fragment currently under test, have you called provideFragmentUnderTest()?");
        }
        return (F) mFragmentUnderTest;
    }

    protected <V extends View> V getViewUnderTest() {
        if (mViewUnderTest == null) {
            throw new IllegalStateException("Robot has no view currently under test, have you called provideViewUnderTest()?");
        }
        return (V) mViewUnderTest;
    }

    /**
     * Returns the MvpView starting searching from the current fragment under test first
     * then the activity.
     *
     * @return the MvpView
     */
    protected MvpView<M> getMvpView() {
        MvpView<M> mvpView;
        if (mFragmentUnderTest instanceof MvpView) {
            mvpView = (MvpView<M>) mFragmentUnderTest;
        } else if (mActivityContext instanceof MvpView) {
            mvpView = (MvpView<M>) mActivityContext;
        } else {
            throw new IllegalStateException("Robot has no MvpView instance currently under test, have you called provideActivityContext() or provideFragmentUnderTest()?");
        }

        return mvpView;
    }

    /**
     * Returns the ViewInteraction specific to the fragment under test if defined.
     * @param viewId
     * @return
     */
    protected ViewInteraction getViewInteraction(@IdRes int viewId) {
        View parent;

        if (mViewUnderTest != null) {
            parent = mViewUnderTest;
        } else if (mFragmentUnderTest != null) {
            parent = mFragmentUnderTest.getView();
        } else {
            parent = null;
        }

        return parent == null ? onView(withId(viewId)) : onView(allOf(withId(viewId), isDescendantOfA(is(parent))));
    }

    protected T checkDialogWithTextIsDisplayed(@StringRes int errorMessageResId) {
        onView(withText(errorMessageResId)).inRoot(isDialog()).check(matches(isDisplayed()));
        return (T) this;
    }

    protected T checkToastWithTextIsDisplayed(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text == null");
        }
        onView(withText(text)).inRoot(withDecorView(not(getContext().getWindow().getDecorView()))).check(matches(isDisplayed()));
        return (T) this;
    }

    protected T checkToastWithTextIsNotDisplayed(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text == null");
        }
        onView(withText(text)).inRoot(withDecorView(not(getContext().getWindow().getDecorView()))).check(matches(not(isDisplayed())));
        return (T) this;
    }

    protected T checkActivityDisplayed(Class<? extends Activity> activityClass) {
        Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(activityClass.getName(), null, false);
        monitor.waitForActivityWithTimeout(WAIT_FOR_ACTIVITY_TIMEOUT);
        getInstrumentation().removeMonitor(monitor);

        assertThat(getActivityInstance().getClass()).isEqualTo(activityClass);
        return (T) this;
    }

    protected T checkFragmentIsDisplayedInPager(FragmentManager fragmentManager, @IdRes int viewPagerId, ViewPager viewPager, Class displayClass) {
        assertThat(getFragmentInViewPagerAtPosition(fragmentManager, viewPagerId, viewPager.getCurrentItem())).isExactlyInstanceOf(displayClass);
        return (T) this;
    }

    protected T performClickOnView(@IdRes int viewId) {
        getViewInteraction(viewId).perform(click());
        return (T) this;
    }

    protected T clearTextInViewWithId(@IdRes int viewId) {
        getViewInteraction(viewId).perform(clearText());
        return (T) this;
    }

    protected T typeTextInViewWithId(@IdRes int viewId, String text) {
        if (text == null) {
            throw new IllegalArgumentException("text == null");
        }
        getViewInteraction(viewId).perform(typeText(text));
        return (T) this;
    }

    protected T swipeLeftOnView(@IdRes int viewId) {
        onView(withId(viewId)).perform(swipeLeft());
        return (T) this;
    }

    protected T swipeRightOnView(@IdRes int viewId) {
        onView(withId(viewId)).perform(swipeRight());
        return (T) this;
    }

    protected T swipeUpOnView(@IdRes int viewId) {
        onView(withId(viewId)).perform(swipeUp());
        return (T) this;
    }

    protected T swipeDownOnView(@IdRes int viewId) {
        onView(withId(viewId)).perform(swipeDown());
        return (T) this;
    }

    protected T callMethod(Runnable runnable) {
        getInstrumentation().runOnMainSync(runnable);
        return (T) this;
    }

    protected <F extends Fragment> F getFragmentInViewPagerAtPosition(FragmentManager fragmentManager, int viewPagerId, int pos) {
        return (F) fragmentManager.findFragmentByTag("android:switcher:" + viewPagerId + ":" + pos);
    }

    private Activity getActivityInstance() {
        final Activity[] activityArray = new Activity[1];

        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection<Activity> resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()) {
                    activityArray[0] = resumedActivities.iterator().next();
                }
            }
        });

        return activityArray[0];
    }
}
