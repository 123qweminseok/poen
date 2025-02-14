package com.lodong.poen.ui.screens

import android.os.Message
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebSettings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.serialization.json.Json


// 회원가입쪽 우편번호 검색 파트임!!!
@Composable
fun DaumPostcodeDialog(
    onAddressSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "주소 검색",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
//                Text(
//                    "(검색 후 입력)",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color(0xFF757575)
//                )
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)  // heightIn을 height로 변경하고 값을 400dp로 설정
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        Color(0xFFE0E0E0),
                        RoundedCornerShape(12.dp)
                    )
                    .background(Color.White)
            ) {
                DaumPostcodeWebView(onAddressSelected = onAddressSelected)
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF2196F3)
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    "닫기",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF757575)
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
//                Text(
//                    "닫기",
//                    style = MaterialTheme.typography.labelLarge.copy(
//                        fontWeight = FontWeight.Medium
//                    )
//                )
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.95f)  // 화면 가로의 95% 사용
            .padding(8.dp),  // 패딩 감소
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp,
        properties = DialogProperties(
            usePlatformDefaultWidth = false  // 플랫폼 기본 가로 크기 제한 해제
        )
    )
}



@Composable
fun DaumPostcodeWebView(
    onAddressSelected: (String) -> Unit
) {
    val context = LocalContext.current

    // WebView 초기화
    val webView = remember {
        WebView(context).apply {
            // WebSettings를 따로 꺼내서 설정해주면 명확
            val webSettings: WebSettings = settings
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.setSupportMultipleWindows(true)

            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                    Log.d("WebView", consoleMessage.message())
                    return true
                }
            }

            // JS -> 안드로이드로 전달
            addJavascriptInterface(WebAppInterface2(onAddressSelected), "MyApp")
        }
    }

    // Compose에서 WebView 배치
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { webView },
        update = { webViewView ->
            val htmlData = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
        <style>
            body { 
                margin: 0; 
                padding: 0; 
                background: transparent; 
                width: 100%;
                height: 100%;
            }
            #postcode-container { 
                border: none;
                width: 100%;    // 90%에서 100%로 변경
                height: 100%;
                overflow: hidden;
                margin: 0;      // auto 마진 제거
                padding: 0;
            }
                </style>
            </head>
            <body>
            <div id="postcode-container" 
                 style="border:1px solid #ccc; width:100%; height:450px; overflow:auto; margin:0 auto;">
            </div>

    <script>
        function execDaumPostcode() {
            new daum.Postcode({
                width: '100%',
                height: '100%',    // 높이도 100%로 설정
                maxSuggestItems: 5,
                        oncomplete: function(data) {
                            var addressJson = JSON.stringify({
                                zonecode: data.zonecode,
                                address: data.roadAddress || data.jibunAddress,
                                extraAddress: data.buildingName ? "(" + data.buildingName + ")" : ""
                            });
                            MyApp.postMessage(addressJson);
                        }
                    }).embed(document.getElementById('postcode-container'));
                }

                window.onload = function() {
                    execDaumPostcode();
                }
            </script>
            </body>
            </html>
        """.trimIndent()

            webViewView.loadDataWithBaseURL(
                "https://t1.daumcdn.net",
                htmlData,
                "text/html",
                "UTF-8",
                null
            )
        }
    )}

/** 자바스크립트 -> 안드로이드(Compose)로 값을 전달하기 위한 브리지 */
class WebAppInterface2(
    private val onAddressSelected: (String) -> Unit
) {
    @JavascriptInterface
    fun postMessage(addressJson: String) {
        onAddressSelected(addressJson)
    }
}
