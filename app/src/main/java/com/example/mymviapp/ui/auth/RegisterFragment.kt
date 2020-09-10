package com.example.mymviapp.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.mymviapp.R
import com.example.mymviapp.ui.auth.state.AuthStateEvent
import com.example.mymviapp.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.input_email
import kotlinx.android.synthetic.main.fragment_register.input_password
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class RegisterFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "RegisterFragment: ${viewModel.hashCode()}")
        subscribeObservers()
        register_button.setOnClickListener {
            register()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setRegistrationFields(
            RegistrationFields(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.registrationFileds?.let {
                it.registrationEmail?.let { input_email.setText(it) }
                it.registrationUsername?.let { input_username.setText(it) }
                it.registrationPassword?.let { input_password.setText(it) }
                it.registrationConfirmPassword?.let { input_password_confirm.setText(it) }
            }
        })
    }

    fun register(){
        viewModel.setStateEvent(
            AuthStateEvent.RegisterAttempEvent(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }

}