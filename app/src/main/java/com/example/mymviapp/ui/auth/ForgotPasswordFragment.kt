package com.example.mymviapp.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.example.mymviapp.R
import com.example.mymviapp.ui.DataState
import com.example.mymviapp.ui.DataStateChangeListener
import com.example.mymviapp.ui.Response
import com.example.mymviapp.ui.ResponseType
import com.example.mymviapp.util.Constants
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class ForgotPasswordFragment : BaseAuthFragment() {

    lateinit var webView: WebView
    lateinit var stateChangeListener: DataStateChangeListener

    val webInteractionCallBack = object : WebAppInterface.OnWebInteractionCallBack {
        override fun onSuccess(email: String) {
            Log.e(TAG, "onSuccess: a reset link will be  sent to $email")
            onPasswordResetLinnkSet()
        }

        override fun onError(errorMessage: String) {
            Log.e(TAG, "onError: $errorMessage")
            val dataState = DataState.error<Any>(
                response = Response(errorMessage, ResponseType.Dialog())
            )
            stateChangeListener.onDataStateChange(dataState)
        }

        override fun onLoading(isLoading: Boolean) {
            Log.d(TAG, "onLoading...")
            GlobalScope.launch(Main) {
                DataState.loading(isLoading = isLoading, cachedData = null)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "ForgotPasswordFragment: ${viewModel.hashCode()}")
        webView = view.findViewById(R.id.webview)
        loadPasswordResetWebView()
        return_to_launcher_fragment.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement DataStateChangeListener")
        }
    }

    @SuppressLint("setJavaScriptEnabled")
    fun loadPasswordResetWebView() {

        stateChangeListener.onDataStateChange(
            DataState.loading(isLoading = true, cachedData = null)
        )

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                stateChangeListener.onDataStateChange(
                    DataState.loading(isLoading = false, cachedData = null)
                )
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.loadUrl(Constants.PASWORD_RESET_URL)
        webView.addJavascriptInterface(
            WebAppInterface(webInteractionCallBack),
            "AndroidTextListener"
        )
    }

    private fun onPasswordResetLinnkSet() {
        GlobalScope.launch(Main) {
            parent_view.removeView(webView)
            webView.destroy()
            val animation = TranslateAnimation(
                password_reset_done_container.width.toFloat(),
                0f, 0f, 0f
            )
            animation.duration = 500
            password_reset_done_container.startAnimation(animation)
            password_reset_done_container.visibility = View.VISIBLE
        }
    }

    class WebAppInterface constructor(private val callBack: OnWebInteractionCallBack) {
        private val TAG: String = "AppDebug"

        @JavascriptInterface
        fun onSuccess(email: String) {
            callBack.onSuccess(email)
        }

        @JavascriptInterface
        fun onError(errorMessage: String) {
            callBack.onError(errorMessage)
        }

        @JavascriptInterface
        fun onLoading(isLoading: Boolean) {
            callBack.onLoading(isLoading)
        }

        interface OnWebInteractionCallBack {
            fun onSuccess(email: String)
            fun onError(errorMessage: String)
            fun onLoading(isLoading: Boolean)
        }
    }

}