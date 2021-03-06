package com.example.mymviapp.ui.main.account.state

import android.os.Parcelable
import com.example.mymviapp.models.AccountProperties
import kotlinx.android.parcel.Parcelize

const val ACCOUNT_VIEW_STATE_BUNDLE_KEY = "com.example.mymviapp.ui.main.account.state.AccountViewState"

@Parcelize
class AccountViewState(
    var accountProperties: AccountProperties? = null
) : Parcelable