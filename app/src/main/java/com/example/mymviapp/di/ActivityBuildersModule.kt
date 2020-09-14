package com.example.mymviapp.di

import com.example.mymviapp.di.auth.AuthFragmentBuildersModule
import com.example.mymviapp.di.auth.AuthModule
import com.example.mymviapp.di.auth.AuthScope
import com.example.mymviapp.di.auth.AuthViewModelModule
import com.example.mymviapp.di.main.MainFragmentBuildersModule
import com.example.mymviapp.di.main.MainModule
import com.example.mymviapp.di.main.MainScope
import com.example.mymviapp.di.main.MainViewModelModule
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

    @MainScope
    @ContributesAndroidInjector(
        modules = [MainModule::class, MainFragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}