package com.example.mymviapp.di

import com.example.mymviapp.di.auth.AuthFragmentBuildersModule
import com.example.mymviapp.di.auth.AuthModule
import com.example.mymviapp.di.auth.AuthScope
import com.example.mymviapp.di.auth.AuthViewModelModule
import com.example.mymviapp.ui.auth.AuthActivity
import com.example.mymviapp.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

}