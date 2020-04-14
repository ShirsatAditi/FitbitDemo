package com.example.fitbitdemo

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitbitdemo.Model.AccessToken
import com.example.fitbitdemo.Model.UserData
import com.example.fitbitdemo.lib.APIService
import com.example.fitbitdemo.lib.Constants
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_AUTH_URL =
            "https://www.fitbit.com/oauth2/authorize?response_type=token&client_id=22BNZ7&redirect_uri=https%3A%2F%2Fmagnetoitsolutions.com&scope=activity%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social&expires_in=604800"
        const val REDIRECT_URL = "https://magnetoitsolutions.com"
        const val CONST_CODE = "code"
        const val CONST_ACCESS_TOKEN = "access_token"
        const val CONST_USER_ID = "user_id"
        const val CONST_TOKEN_TYPE = "token_type"
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

                    if (code.isNotBlank()) {
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
                                    Log.e("SUCESSSSSs", response.body()?.access_token ?: "")
                                }
                            })
                    } else {
                        val accessToken = uri.getQueryParameter(CONST_ACCESS_TOKEN) ?: ""
                        val tokenType = uri.getQueryParameter(CONST_TOKEN_TYPE) ?: ""

                        val userId = uri.getQueryParameter(CONST_USER_ID) ?: ""
                        val cal = Calendar.getInstance()
                        val format = SimpleDateFormat("yyyy-mm-dd")
                        val date = format.format(cal.time)

                        val url = String.format(
                            "%s%s%s%s%s",
                            "https://api.fitbit.com/1/user/", userId, "/activities/date/", date,"/"
                        )

                        APIService.getBaseUrl(url)
                            .getUserData(url,String.format("%s %s", tokenType, accessToken))
                            .enqueue(object : Callback<UserData> {
                                override fun onFailure(call: Call<UserData>, t: Throwable) {
                                }

                                override fun onResponse(
                                    call: Call<UserData>,
                                    response: Response<UserData>
                                ) {
                                    Log.e("USER_DATA", response.body().toString())
                                    
                                    setUserData(response.body())
                                }
                            })
                    }
                } else {
                    web_View?.loadUrl(request)
                }
                return true
            }
        }

        web_View.loadUrl(EXTRA_AUTH_URL)
    }

    private fun setUserData(body: UserData?) {

        web_View.visibility = View.GONE
        sv_userData.visibility = View.VISIBLE

        tv_activeScore.text = body?.summary?.activeScore
        tv_activityCalories.text = body?.summary?.activityCalories
        tv_caloriesBMR.text = body?.summary?.caloriesBMR
        tv_caloriesOut.text = body?.summary?.caloriesOut

        tv_fairlyActiveMinutes.text = body?.fairlyActiveMinutes
        tv_lightlyActiveMinutes.text = body?.lightlyActiveMinutes
        tv_marginalCalories.text = body?.marginalCalories
        tv_sedentaryMinutes.text = body?.sedentaryMinutes
        tv_steps.text = body?.steps
        tv_veryActiveMinutes.text = body?.veryActiveMinutes

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DistanceAdapter(body?.summary?.distances ?: arrayListOf())
    }
}
