package com.platinumlist.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.AttributeSet
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import com.platinumlist.sdk.common.*
import com.platinumlist.sdk.common.Storage
import com.platinumlist.sdk.common.WebInterface
import java.text.SimpleDateFormat
import java.util.*


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
            mOrderStatusResolver = this::orderStatusResolver,
            mCloseWebview = this::closeWebview,
            mTriggerError = this::triggerError,
            mProcessTicketDownload = this::processTicketDownload
        )
    }

    var adapter: Adapter? = null
        set(value) {
            field = value
            field?.view = this
        }

    init {
        inflate(context, R.layout.view_platinum, this)

        btnBack.setOnClickListener{
            adapter?.progressSrc?.postValue(true)
            webView.goBack()
        }
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

    private inner class MyClient : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            adapter?.progressSrc?.postValue(false)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            if (url?.contains("platinumlist") == true) {
                toolbar.visibility = View.GONE
            } else {
                toolbar.visibility = View.VISIBLE
            }
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
            .setCookie("https://api.platinumlist.net/", "Api-Authorization=Bearer ${Storage.token}")
        CookieManager.getInstance()
            .setCookie("https://.platinumlist.net/", "user_lang=${Storage.lang}")

        when {
            Storage.showId != -1L -> {
                startEvent(Storage.showId)
            }
            Storage.eventShowId != -1L -> {
                startEventShow(Storage.eventShowId, Storage.eventShowSetting)
            }
            else -> {
                adapter?.triggerError("Incorrect event id", 1)
            }
        }
    }

    private fun startEvent(idEvent: Long) {
        "https://api.platinumlist.net/v/7/events/$idEvent/ticket-office?sdk_type=${adapter?.getSdkType()?.ordinal}".let {
            webView.loadUrl(
                it,
                mapOf(
                    "Api-Authorization" to "Bearer ${Storage.token}",
                    "user_lang" to "${Storage.lang}"
                )
            )
        }
    }

    private fun startEventShow(idEventShow: Long, eventShowSetting: Int) {
        "https://api.platinumlist.net/v/7/event-shows/$idEventShow/ticket-office?sdk_type=${adapter?.getSdkType()?.ordinal}&event_show_setting=$eventShowSetting".let {
            webView.loadUrl(
                it,
                mapOf(
                    "Api-Authorization" to "Bearer ${Storage.token}",
                    "user_lang" to "${Storage.lang}"
                )
            )
        }
    }

    private fun checkOut(link: String, showId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = adapter?.provideUser() ?: Storage.user

            if (user == null) {
                adapter?.registerUser()
                return@launch
            }

            api.changeBasketAsync(
                id = showId,
                request = BasketRequest(
                    scope = "partner-sale.event-tickets",
                    include = "user",
                    operation = "set.user",
                    user = user
                )
            ).await().let {
                withContext(Dispatchers.Main) {
                    webView.loadUrl(link)
                }
            }
        }
    }

    private fun orderStatusResolver(orderId: Long, status: String) {
        if (adapter?.getSdkType() == SdkType.TICKET_OFFICE) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    webView.loadUrl("javascript:triggerApiEvent('checkOrderStatus')")
                }
            }
        }

        adapter?.orderStatusResolver(orderId, status)

        if (adapter?.getSdkType() == SdkType.PURCHASE || adapter?.getSdkType() == SdkType.SELECT_TICKETS) {
            if (adapter?.getSdkType() == SdkType.SELECT_TICKETS) {
                adapter?.setOrderId(orderId)

                getOrderAmount(orderId)
            }
            clear()
            goBack()
        }
    }

    private fun getOrderAmount(orderId: Long) {
        GlobalScope.launch {
            try {
                api.getOrderAsync(
                    id = orderId,
                    scope = "partner-sale.receive-payments"
                ).await().let {
                    Storage.amount = it.body()?.data?.amount ?: 0f
                    adapter?.setOrderAmount(Storage.amount)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun closeWebview() {
        clear()
        goBack()

        adapter?.closeWebview()
    }

    private fun triggerError(message: String, code: Int) {
        adapter?.triggerError(message, code)
    }

    private fun processTicketDownload(link: String, type: String) {

        when (type) {
            LinkType.PDF.typeName -> {
                downloadPDFTicket(link)
            }
            LinkType.GOOGLE_WALLET.typeName -> {
                addToGoogleWallet(link)
            }
            LinkType.APPLE_WALLET.typeName -> {

            }
        }
    }

    private fun downloadPDFTicket(link: String) {
        adapter?.progressSrc?.postValue(true)

        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val fileName = "ticket $currentDate.pdf"

        val extStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val file = File(extStorageDirectory)
        file.mkdir()

        val pdfFile = File(file, fileName)

        try {
            pdfFile.createNewFile()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        downloadTicket(link, pdfFile)

        adapter?.progressSrc?.postValue(false)

        Toast.makeText(context,"File downloaded to $pdfFile",Toast.LENGTH_LONG).show()
    }

    private fun addToGoogleWallet(link: String) {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(context, browserIntent, null)
    }

    private fun downloadTicket(link: String, directory: File) {
        try {
            val url = URL(link)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()

            val inputStream = urlConnection.inputStream
            val fileOutputStream = FileOutputStream(directory)

            val buffer = ByteArray(1024 * 1024)
            var bufferLength: Int
            while (inputStream.read(buffer).also { bufferLength = it } > 0) {
                fileOutputStream.write(buffer, 0, bufferLength)
            }

            fileOutputStream.close()
            urlConnection.disconnect()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
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

    internal fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
            Storage.showId = provideIdEvent()
            Storage.user = provideUser()
            Storage.eventShowId = provideIdEventShow()
            Storage.eventShowSetting = provideEventShowSetting()
            Storage.lang = provideLanguage()

            progressSrc.postValue(true)

            withContext(Dispatchers.Main) {
                view?.updateUrl(Storage.showId)
            }
        }

        abstract fun checkOutError(error: Exception)

        abstract fun goBack()

        abstract suspend fun provideToken(): String
        abstract suspend fun provideIdEvent(): Long
        abstract suspend fun provideIdEventShow(): Long
        abstract suspend fun provideEventShowSetting(): Int
        abstract suspend fun provideUser(): UserRequest?
        abstract suspend fun provideLanguage(): String

        abstract fun orderStatusResolver(orderId: Long, status: String)

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            view?.onActivityResult(requestCode, resultCode, data)
        }

        abstract fun startActivity(intent: Intent, requestCode: Int)

        internal fun clear() {
            view = null
        }

        abstract fun closeWebview()
        abstract fun getSdkType(): SdkType
        abstract fun triggerError(message: String, code: Int)
        abstract fun setOrderId(orderId: Long)
        abstract fun setOrderAmount(amount: Float)
        abstract fun registerUser()
    }

    companion object {
        private const val REQUEST_SELECT_FILE = 189
    }
}