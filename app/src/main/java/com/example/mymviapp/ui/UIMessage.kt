package com.example.mymviapp.ui

data class UIMessage(
    val message: String,
    val uiMessageType: UIMessageType
)

sealed class UIMessageType {

    class Toast : UIMessageType()
    class Dialog : UIMessageType()
    class AreYouSureDialog(
        val callBack: AreYouSureCallback
    ) : UIMessageType()

    class None : UIMessageType()

}