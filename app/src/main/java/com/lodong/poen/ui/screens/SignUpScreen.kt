package com.lodong.poen.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties

@Composable
fun ZipCodeSearchDialog(
    onClose: () -> Unit,
    onAddressSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onClose() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxSize(),
        text = {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true

                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.setSupportMultipleWindows(true)
                        settings.javaScriptCanOpenWindowsAutomatically = true

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                println("페이지 로딩 시작: $url")
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                println("페이지 로드 완료: $url")
                                view?.evaluateJavascript("console.log('WebView Loaded');") {}
                            }

                            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                                println("에러 발생: ${error?.description}, 코드: ${error?.errorCode}")
                            }
                        }

                        addJavascriptInterface(WebAppInterface(onAddressSelected), "Android")
                        loadUrl("file:///android_asset/zipcode.html")
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                println("onClose 호출 전")
                onClose()
                println("onClose 호출 후")
            }) {
                Text("닫기")
            }
        }
    )

    @Composable
    fun ParentScreen() {
        var isDialogVisible by remember { mutableStateOf(true) }

        if (isDialogVisible) {
            ZipCodeSearchDialog(
                onClose = {
                    println("다이얼로그 닫기 상태 변경")
                    isDialogVisible = false
                },
                onAddressSelected = { address ->
                    println("선택된 주소: $address")
                    isDialogVisible = false
                }
            )
        }
    }

}