package com.earthquakealert.app.ui.components

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import com.earthquakealert.app.ui.theme.EarthquakeTheme

private const val TAG = "EarthquakeMapView"

private class MapViewState(initialScript: String) {
    var isLoaded: Boolean = false
    var latestScript: String = initialScript
    var lastEvaluatedScript: String? = null
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun EarthquakeMapView(
    latitude: Double,
    longitude: Double,
    magnitude: Double,
    modifier: Modifier = Modifier,
    isDark: Boolean = EarthquakeTheme.colors.isDark
) {
    val context = LocalContext.current
    val currentScript = remember(latitude, longitude, magnitude, isDark) {
        "updateEpicenter($latitude, $longitude, $magnitude, $isDark);"
    }

    val assetLoader = remember(context) {
        WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
            .build()
    }

    val state = remember { MapViewState(currentScript) }
    state.latestScript = currentScript

    val bgColor = if (isDark) 0xFF141414.toInt() else 0xFFE2EDF7.toInt()

    Box(
        modifier = modifier.background(if (isDark) Color(0xFF141414) else Color(0xFFE2EDF7))
    ) {
        // Native fallback backdrop while WebView initializes
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val gridColor = if (isDark) Color(0x1AFFFFFF) else Color(0x1A000000)
            val ringColor = Color(0x33E53935)

            // Radar rings
            drawCircle(color = ringColor, radius = 40f, center = center)
            drawCircle(color = ringColor.copy(alpha = 0.15f), radius = 80f, center = center)
            drawCircle(color = Color(0xFFE53935), radius = 7f, center = center)

            // Crosshair guidelines
            drawLine(gridColor, Offset(0f, center.y), Offset(size.width, center.y), strokeWidth = 1f)
            drawLine(gridColor, Offset(center.x, 0f), Offset(center.x, size.height), strokeWidth = 1f)
        }

        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    setBackgroundColor(bgColor)
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        cacheMode = WebSettings.LOAD_DEFAULT
                        userAgentString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 EarthquakeAlert/1.0"
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            Log.d(TAG, "[WebView Console] ${consoleMessage?.message()} (line ${consoleMessage?.lineNumber()})")
                            return true
                        }
                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldInterceptRequest(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): WebResourceResponse? {
                            val url = request?.url ?: return null
                            return assetLoader.shouldInterceptRequest(url)
                        }

                        @Suppress("DEPRECATION")
                        override fun shouldInterceptRequest(
                            view: WebView?,
                            url: String?
                        ): WebResourceResponse? {
                            val uri = url?.let { Uri.parse(it) } ?: return null
                            return assetLoader.shouldInterceptRequest(uri)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            state.isLoaded = true
                            state.lastEvaluatedScript = state.latestScript
                            view?.evaluateJavascript(state.latestScript, null)
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            Log.w(TAG, "WebView error loading: ${request?.url} -> ${error?.description}")
                        }
                    }

                    loadUrl("https://appassets.androidplatform.net/assets/leaflet/map.html")
                }
            },
            update = { webView ->
                if (state.isLoaded && state.lastEvaluatedScript != currentScript) {
                    state.lastEvaluatedScript = currentScript
                    webView.evaluateJavascript(currentScript, null)
                }
            },
            onRelease = { webView ->
                webView.stopLoading()
                webView.destroy()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
