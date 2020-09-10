package com.example.mymviapp.di.auth

import com.example.mymviapp.ui.auth.ForgotPasswordFragment
import com.example.mymviapp.ui.auth.LauncherFragment
import com.example.mymviapp.ui.auth.LoginFragment
import com.example.mymviapp.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment

}