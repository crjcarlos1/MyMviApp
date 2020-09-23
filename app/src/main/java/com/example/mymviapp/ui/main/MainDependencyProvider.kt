package com.example.mymviapp.ui.main

import com.bumptech.glide.RequestManager
import com.example.mymviapp.viewmodels.ViewModelProviderFactory

interface MainDependencyProvider {
    fun getVMProviderFactory(): ViewModelProviderFactory
    fun getGlideRequestManager(): RequestManager
}