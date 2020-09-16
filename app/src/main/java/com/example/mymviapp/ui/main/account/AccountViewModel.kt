package com.example.mymviapp.ui.main.account

import androidx.lifecycle.LiveData
import com.example.mymviapp.models.AccountProperties
import com.example.mymviapp.repository.main.AccountRepository
import com.example.mymviapp.session.SessionManager
import com.example.mymviapp.ui.BaseViewModel
import com.example.mymviapp.ui.DataState
import com.example.mymviapp.ui.main.account.state.AccountStateEvent
import com.example.mymviapp.ui.main.account.state.AccountViewState
import com.example.mymviapp.util.AbsentLiveData
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
) : BaseViewModel<AccountStateEvent, AccountViewState>() {

    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when (stateEvent) {
            is AccountStateEvent.GetAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.getAccountProperties(authToken)
                } ?: AbsentLiveData.create()
            }
            is AccountStateEvent.ChangePasswordEvent -> {
                return AbsentLiveData.create()
            }
            is AccountStateEvent.UpdateAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    authToken.account_pk?.let { pk ->
                        accountRepository.saveAccountProperties(
                            authToken, AccountProperties(
                                pk, stateEvent.email, stateEvent.username
                            )
                        )
                    }
                } ?: AbsentLiveData.create()
            }
            is AccountStateEvent.None -> {
                return AbsentLiveData.create()
            }
        }
    }

    override fun initViewState(): AccountViewState {
        return AccountViewState()
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties) {
        val update = getCurrentViewStateOrNew()
        if (update.accountProperties == accountProperties) {
            return
        }
        update.accountProperties = accountProperties
        _viewState.value = update
    }

    fun logout() {
        sessionManager.logout()
    }

}