package br.com.mapeamentosufoco.app.util

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceGenerator {

    private const val API_BASE_URL_DEVELOPMENT = "https://674e5ufk78.execute-api.us-east-1.amazonaws.com/prod/"

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .setLenient()
        .create()
    private val httpClient = OkHttpClient.Builder()
    private val builder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson)).baseUrl(API_BASE_URL_DEVELOPMENT)

    fun <S> createService(serviceClass: Class<S>): S {

        httpClient.connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
        httpClient.addInterceptor { chain ->
            val original = chain.request()

            // Request customization: add request headers
            val requestBuilder = original.newBuilder()
                .method(original.method(), original.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }

        val client = httpClient.build()
        val retrofit = builder.client(client).build()
        return retrofit.create(serviceClass)
    }
}