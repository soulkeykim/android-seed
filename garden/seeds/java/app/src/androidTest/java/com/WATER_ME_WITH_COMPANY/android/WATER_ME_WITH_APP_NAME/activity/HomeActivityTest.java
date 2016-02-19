package com.{{company_name}}.android.{{app_package_name_prefix}}.activity;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.{{company_name}}.android.{{app_package_name_prefix}}.R;
import com.{{company_name}}.android.{{app_package_name_prefix}}.ScreenRobot;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.{{company_name}}.android.{{app_package_name_prefix}}.ScreenRobot.withRobot;
import static com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.presenter.HomePresenter.SUBMIT_MESSAGE_REQUEST;
import static com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.view.MvpView.ErrorType.SERVER;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeActivityTest {

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<HomeActivity>(HomeActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            return new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), HomeActivity.class);
        }
    };

    @Test
    public void testShowError() {
        withRobot(HomeScreenRobot.class)
                .provideActivityContext(mActivityRule.getActivity())
                .callShowErrorMethod(SERVER, SUBMIT_MESSAGE_REQUEST)
                .checkIsInSystemErrorState()
                .clickOkOnDialog();
    }

    @Test
    public void testShowLoading() {
        withRobot(HomeScreenRobot.class)
                .provideActivityContext(mActivityRule.getActivity())
                .callShowLoadingMethod()
                .checkIsInLoadingState();
    }

    @Test
    public void testShowData() {
        withRobot(HomeScreenRobot.class)
                .provideActivityContext(mActivityRule.getActivity())
                .callShowData()
                .checkIsDataShown();
    }

    public static class HomeScreenRobot extends ScreenRobot<HomeScreenRobot, String> {

        public HomeScreenRobot checkIsInLoadingState() {
            return true;
        }

        public HomeScreenRobot checkIsInSystemErrorState() {
            return true;
        }

        public HomeScreenRobot checkIsDataShown() {
            return true;
        }

        public HomeScreenRobot callShowData() {
            return callShowDataMethod("Test");
        }
    }
}

