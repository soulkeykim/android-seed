package com.{{company_name}}.android.{{app_package_name_prefix}}.module;

import com.{{company_name}}.android.{{app_package_name_prefix}}.mvp.presenter.HomePresenter;
import com.{{company_name}}.android.{{app_package_name_prefix}}.activity.BaseActivity;
import com.{{company_name}}.android.{{app_package_name_prefix}}.fragment.BaseFragment;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger component to provide dependency injection
 */
@Singleton
@Component(modules = {{app_class_prefix}}Module.class)
public interface AppServicesComponent {

	// Base Classes
	void inject(BaseActivity activity);
	void inject(BaseFragment fragment);

	// Presenters
	void inject(HomePresenter presenter);
}
