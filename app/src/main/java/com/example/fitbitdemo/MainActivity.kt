package com.example.fitbitdemo

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.fitbitdemo.Model.AccessToken
import com.example.fitbitdemo.lib.APIService
import com.example.fitbitdemo.lib.Constants
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_AUTH_URL =
            "https://www.fitbit.com/oauth2/authorize?response_type=token&client_id=22BNZ7&redirect_uri=https%3A%2F%2Fmagnetoitsolutions.com&scope=activity%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social&expires_in=604800"
        const val REDIRECT_URL = "https://magnetoitsolutions.com"
        const val CONST_CODE = "code"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        web_View.settings.javaScriptEnabled = true

        web_View.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: String?
            ): Boolean {
                if ((request ?: "").startsWith(REDIRECT_URL)) {
                    val uri = Uri.parse(request)

                    val code = uri.getQueryParameter(CONST_CODE) ?: ""

                    Log.e("CODE_AUTH", code)

                    APIService.getAuthBaseUrl("https://api.fitbit.com/oauth2/")
                        .getAccessToken(
                            Constants.CLIENT_ID,
                            Constants.GRANT_TYPE,
                            REDIRECT_URL,
                            code
                        ).enqueue(object : Callback<AccessToken> {
                            override fun onFailure(call: Call<AccessToken>, t: Throwable) {

                            }

                            override fun onResponse(
                                call: Call<AccessToken>,
                                response: Response<AccessToken>
                            ) {
                                Log.e("SUCESSSSSs", response.body()?.access_token ?:"")
                            }
                        })
                } else {
                    web_View?.loadUrl(request)
                }
                return true
            }
        }

        web_View.loadUrl(EXTRA_AUTH_URL)
    }
}
