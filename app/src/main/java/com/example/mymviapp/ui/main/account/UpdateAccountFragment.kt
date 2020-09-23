package com.example.mymviapp.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import com.example.mymviapp.R
import com.example.mymviapp.models.AccountProperties
import com.example.mymviapp.ui.main.account.state.AccountStateEvent
import kotlinx.android.synthetic.main.fragment_update_account.*
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class UpdateAccountFragment : BaseAccountFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            Log.d(TAG, "UpdateAccountFragment: $dataState")
            if (dataState!=null){
                stateChangeListener.onDataStateChange(dataState)
            }
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.accountProperties?.let {
                    Log.d(TAG, "UpdateAccountFragment: viewState: $it ")
                    setAccountDataFileds(it)
                }
            }
        })
    }

    private fun setAccountDataFileds(accountProperties: AccountProperties) {
        input_email?.let {
            input_email.setText(accountProperties.email)
        }
        input_username?.let {
            input_username.setText(accountProperties.username)
        }
    }

    private fun saveChanges() {
        viewModel.setStateEvent(
            AccountStateEvent.UpdateAccountPropertiesEvent(
                input_email.text.toString(),
                input_username.text.toString()
            )
        )
        stateChangeListener.hideSoftKeyboard()
    }

}