package com.example.mymviapp.ui

interface DataStateChangeListener {

    fun onDataStateChange(dataState: DataState<*>?)
    fun expandAppBar()
    fun hideSoftKeyboard()

}