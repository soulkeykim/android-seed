package com.{{company_name}}.android.{{app_package_name_prefix}}.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.trello.rxlifecycle.ActivityEvent;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Activity which exposes useful lifecycle callbacks as an {@link Observable}
 */
abstract class RxActivity extends AppCompatActivity {

    private final BehaviorSubject<ActivityEvent> mLifecycleSubject = BehaviorSubject.create();

    public Observable<ActivityEvent> lifecycle() {
        return mLifecycleSubject.asObservable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLifecycleSubject.onNext(ActivityEvent.CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    protected void onPause() {
        mLifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        mLifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mLifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
    }
}
