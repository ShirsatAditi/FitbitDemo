package com.example.fitbitdemo.lib

import com.example.fitbitdemo.Model.AccessToken
import com.example.fitbitdemo.Model.UserData
import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 *
 *
 * @author Aditi Shirsat
 */
interface APIService {

    /**
     * Get the instance of Base URL.
     */
    companion object {
        fun getBaseUrl(BASE_URL: String): APIService {
            val retrofit = Builder()
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .client(getClient())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(APIService::class.java)
        }

        fun getAuthBaseUrl(BASE_URL: String): APIService {
            val retrofit = Builder()
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .client(getAuthClient())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(APIService::class.java)
        }

        //to ghet unsafeclient call UnsafeOkHttpClient.getUnsafeOkHttpClient()
        /**
         * @return Instance of OkHttpClient class with modified timeout
         */
        private fun getClient(): OkHttpClient {
            val httpTimeout: Long = 20
            val okHttpClientBuilder = OkHttpClient.Builder()
            okHttpClientBuilder.connectTimeout(httpTimeout, TimeUnit.SECONDS)
            okHttpClientBuilder.readTimeout(httpTimeout, TimeUnit.SECONDS)
            return okHttpClientBuilder.build()
        }

        /**
         * @return Instance of OkHttpClient class with modified timeout
         */
        private fun getAuthClient(): OkHttpClient {
            val httpTimeout: Long = 20
            val okHttpClientBuilder = OkHttpClient.Builder()
            okHttpClientBuilder.addInterceptor(
                BasicAuthInterceptor(
                    Constants.CLIENT_ID,
                    Constants.CLIENT_SECRET
                )
            )
            okHttpClientBuilder.connectTimeout(httpTimeout, TimeUnit.SECONDS)
            okHttpClientBuilder.readTimeout(httpTimeout, TimeUnit.SECONDS)
            return okHttpClientBuilder.build()
        }
    }

    @FormUrlEncoded
    @POST("token")
    fun getAccessToken(
        @Field("client_id") client_id: String,
        @Field("grant_type") grant_type: String,
        @Field("redirect_uri") redirect_uri: String,
        @Field("code") code: String
    ): Call<AccessToken>

    @GET
    fun getUserData(@Url url:String,@Header("Authorization") Authorization: String): Call<UserData>
}

class BasicAuthInterceptor(user: String?, password: String?) :
    Interceptor {
    private val credentials: String = Credentials.basic(user, password)
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }
}