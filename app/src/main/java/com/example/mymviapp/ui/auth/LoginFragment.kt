package com.example.mymviapp.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.mymviapp.R
import com.example.mymviapp.ui.auth.state.AuthStateEvent
import com.example.mymviapp.ui.auth.state.LoginFields
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class LoginFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "LoginFragment: ${viewModel.hashCode()}")
        subscribeObservers()


        login_button.setOnClickListener {
            login()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setLoginFileds(
            LoginFields(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.loginFields?.let {
                it.loginEmail?.let { input_email.setText(it) }
                it.loginPassword?.let { input_password.setText(it) }
            }
        })
    }

    fun login() {
        viewModel.setStateEvent(
            AuthStateEvent.LoginAttempEvent(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }

}