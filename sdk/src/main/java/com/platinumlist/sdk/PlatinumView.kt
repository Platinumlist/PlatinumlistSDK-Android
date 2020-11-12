package com.platinumlist.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.webkit.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.platinumlist.sdk.common.InvalidDataException
import com.platinumlist.sdk.common.Storage
import com.platinumlist.sdk.common.WebInterface
import com.platinumlist.sdk.di.ViewModule
import com.platinumlist.sdk.models.BasketRequest
import com.platinumlist.sdk.models.UserRequest
import com.platinumlist.sdk.remote.Api
import kotlinx.android.synthetic.main.view_platinum.view.*
import kotlinx.coroutines.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.KodeinTrigger
import org.kodein.di.generic.instance


class PlatinumView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), KodeinAware {

    //region Kodein

    override val kodein: Kodein = Kodein.lazy {
        import(ViewModule.get(this@PlatinumView))
    }

    override val kodeinTrigger = KodeinTrigger()

    init {
        kodeinTrigger.trigger()
    }

    //endregion

    private var uploadMessage: ValueCallback<Array<Uri>>? = null

    private val api: Api by instance<Api>()

    private val webInterface: WebInterface by lazy {
        WebInterface(
            mCheckOut = this::checkOut,
            mGoBack = this::goBack,
            mOrderStatusResolver = this::orderStatusResolver
        )
    }

    var adapter: Adapter? = null
        set(value) {
            field = value
            field?.view = this
        }

    init {
        inflate(context, R.layout.view_platinum, this)
    }

    private inner class MyChromeClient : WebChromeClient() {

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {

            if (uploadMessage != null) {
                uploadMessage!!.onReceiveValue(null)
                uploadMessage = null
            }

            uploadMessage = filePathCallback

            val intent = fileChooserParams!!.createIntent()
            try {
                adapter?.startActivity(intent, REQUEST_SELECT_FILE)
            } catch (e: Exception) {
                uploadMessage = null
                Toast.makeText(
                    context,
                    "Something went wrong",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }

            return true
        }
    }

    private inner class MyClient: WebViewClient(){
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            adapter?.progressSrc?.postValue(false)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onFinishInflate() {
        super.onFinishInflate()
        WebView.setWebContentsDebuggingEnabled(true);
        webView.addJavascriptInterface(webInterface, "JSInterface")
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = MyClient()
        webView.webChromeClient = MyChromeClient()
    }

    fun clear() {
        adapter?.clear()
    }

    internal suspend fun check(id: Long) {
        adapter?.progressSrc?.postValue(true)
        api.checkAsync(id, "partner-sale.event-tickets").await().let {
            when (it.code()) {
                401 -> {
                    adapter?.progressSrc?.postValue(false)
                    throw InvalidDataException(InvalidDataException.Data.TOKEN)
                }
                400 -> {
                    adapter?.progressSrc?.postValue(false)
                    throw InvalidDataException(InvalidDataException.Data.ID_EVENT_SHOW)
                }
                else -> {
                    //all ok
                }
            }
        }
    }

    fun updateUrl(id: Long) {
        CookieManager.getInstance()
            .setCookie("http://api.platinumlist.net/", "Api-Authorization=Bearer ${Storage.token}")

        "https://api.platinumlist.net/v/6/event-shows/$id/ticket-office".let {
            webView.loadUrl(
                it,
                mapOf(
                    "Api-Authorization" to "Bearer ${Storage.token}"
                )
            )
        }
    }

    private fun checkOut(link: String) {
        CoroutineScope(Dispatchers.IO).launch {
            api.changeBasketAsync(
                id = Storage.showId,
                request = BasketRequest(
                    scope = "partner-sale.event-tickets",
                    include = "user",
                    operation = "set.user",
                    user = Storage.user!!
                )
            ).await().let {
                when (it.code()) {
                    401 -> InvalidDataException(InvalidDataException.Data.TOKEN).let {
                        adapter?.checkOutError(it)
                    }
                    400 -> InvalidDataException(InvalidDataException.Data.ID_EVENT_SHOW).let {
                        adapter?.checkOutError(it)
                    }
                    else -> {
                        withContext(Dispatchers.Main) {
                            webView.loadUrl(link)
                        }
                    }
                }
            }
        }
    }

    private fun orderStatusResolver(orderId: Int, status: String) {
        adapter?.orderStatusResolver(orderId, status)
    }

    private fun goBack() {
        GlobalScope.launch {
            try {
                api.clearBasketAsync(Storage.showId, "partner-sale.event-tickets").await()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                adapter?.goBack()
            }
        }
    }

    internal fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if (requestCode == REQUEST_SELECT_FILE) {
            if (uploadMessage == null) {
                return
            }
            uploadMessage?.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(
                    resultCode,
                    data
                )
            )
            uploadMessage = null
        }
    }

    abstract class Adapter {

        internal var view: PlatinumView? = null

        internal val progressSrc = MutableLiveData<Boolean>(false)
        val progress: LiveData<Boolean> = progressSrc

        /**
         * @throws InvalidDataException in case of errors during data validation
         */
        suspend fun invalidate() {
            Storage.token = provideToken()
            Storage.showId = provideIdEventShow()
            Storage.user = provideUser()

            view?.check(Storage.showId)

            withContext(Dispatchers.Main) {
                view?.updateUrl(Storage.showId)
            }
        }

        abstract fun checkOutError(error: Exception)

        abstract fun goBack()

        abstract suspend fun provideToken(): String
        abstract suspend fun provideIdEventShow(): Long
        abstract suspend fun provideUser(): UserRequest

        abstract fun orderStatusResolver(orderId: Int, status: String)

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
            view?.onActivityResult(requestCode, resultCode, data)
        }

        abstract fun startActivity(intent: Intent, requestCode: Int)

        internal fun clear() {
            view = null
        }
    }

    companion object{
        private const val REQUEST_SELECT_FILE = 189
    }
}