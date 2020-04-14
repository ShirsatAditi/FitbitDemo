package com.example.fitbitdemo

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
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

        swipe_refresh.setOnRefreshListener {
            if (!Constants.isNetworkAvailable(this)) {
                showNoNetworkDialog()
            } else {
                web_View.loadUrl(EXTRA_AUTH_URL)
            }
        }

        web_View.settings.javaScriptEnabled = true

        web_View.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: String?
            ): Boolean {
                if ((request ?: "").startsWith(REDIRECT_URL)) {
                    var uri = Uri.parse(request)

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

                                    val cal = Calendar.getInstance()
                                    val format = SimpleDateFormat("yyyy-MM-dd")
                                    val date = format.format(cal.time)
                                    val url = String.format(
                                        "%s%s%s%s%s",
                                        "https://api.fitbit.com/1/user/",
                                        response.body()?.user_id,
                                        "/activities/date/",
                                        date,
                                        ".json"
                                    )

                                    APIService.getBaseUrl("https://api.fitbit.com/1/user/")
                                        .getUserData(url, String.format("%s %s", response.body()?.token_type, response.body()?.access_token))
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
                            })
                    } else {
                        if (uri.toString().contains("#"))
                            uri = Uri.parse(uri.toString().replace("#", "?"))
                        val accessToken = uri.getQueryParameter(CONST_ACCESS_TOKEN) ?: ""
                        val tokenType = uri.getQueryParameter(CONST_TOKEN_TYPE) ?: ""

                        val userId = uri.getQueryParameter(CONST_USER_ID) ?: ""
                        val cal = Calendar.getInstance()
                        val format = SimpleDateFormat("yyyy-MM-dd")
                        val date = format.format(cal.time)

                        val url = String.format(
                            "%s%s%s%s%s",
                            "https://api.fitbit.com/1/user/",
                            userId,
                            "/activities/date/",
                            date,
                            ".json"
                        )

                        APIService.getBaseUrl("https://api.fitbit.com/1/user/")
                            .getUserData(url, String.format("%s %s", tokenType, accessToken))
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
                }else{
                    if (!Constants.isNetworkAvailable(applicationContext)) {
                        showNoNetworkDialog()
                    } else {
                        web_View.loadUrl(request)
                    }
                }
                return true
            }
        }

        if (!Constants.isNetworkAvailable(applicationContext)) {
            showNoNetworkDialog()
        } else {
            web_View.loadUrl(EXTRA_AUTH_URL)
        }
    }

    private fun setUserData(body: UserData?) {

        rl_webView.visibility = View.GONE
        sv_userData.visibility = View.VISIBLE

        tv_activeScore.text = String.format("%s %s", "Activity Score", body?.summary?.activeScore)
        tv_activityCalories.text =
            String.format("%s %s", "Activity Calories", body?.summary?.activityCalories)
        tv_caloriesBMR.text =
            String.format("%s %s", "Activity Calories BMR", body?.summary?.caloriesBMR)
        tv_caloriesOut.text =
            String.format("%s %s", "Activity Calories Out", body?.summary?.caloriesOut)

        tv_fairlyActiveMinutes.text =
            String.format("%s %s", "Faily Activity Minutes", body?.fairlyActiveMinutes)
        tv_lightlyActiveMinutes.text =
            String.format("%s %s", "Lightly Activity Minutes", body?.lightlyActiveMinutes)
        tv_marginalCalories.text =
            String.format("%s %s", "Marginal calories", body?.marginalCalories)
        tv_sedentaryMinutes.text =
            String.format("%s %s", "sendentary  Minutes", body?.sedentaryMinutes)
        tv_steps.text = String.format("%s %s", "Steps", body?.steps)
        tv_veryActiveMinutes.text =
            String.format("%s %s", "very Activity Minutes", body?.veryActiveMinutes)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DistanceAdapter(body?.summary?.distances ?: arrayListOf())
    }

    fun showNoNetworkDialog() {
        AlertDialog.Builder(this)
            .setTitle("Network")
            .setMessage("Please check your network connection?")
            // Specifying a listener allows you to take an action before dismissing the dialog.
// The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, which ->
                    // Continue with delete operation
                }) // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton("Retry", DialogInterface.OnClickListener { dialog, which ->
                if (!Constants.isNetworkAvailable(this)) {
                    showNoNetworkDialog()
                } else {
                    web_View.loadUrl(EXTRA_AUTH_URL)
                }
            })
            .show()
    }
}
