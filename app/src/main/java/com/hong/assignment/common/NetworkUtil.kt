package com.hong.assignment.common

import com.hong.assignment.BuildConfig
import com.hong.assignment.data.remote.CentersApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory


@OptIn(ExperimentalSerializationApi::class)
class NetworkUtil private constructor() {

    companion object {
        @Volatile
        private var instance: NetworkUtil? = null

        @JvmStatic
        fun getInstance(): NetworkUtil =
            instance ?: synchronized(this) {
                instance ?: NetworkUtil().also {
                    instance = it
                }
            }
    }

    private var retrofit: Retrofit
    private var apiService: CentersApiService? = null

    init {
        retrofit = createRetrofit(createHttpClient(), json())
    }

    private fun json(): Json = Json {
        isLenient = true // Json 큰따옴표 느슨하게 체크.
        ignoreUnknownKeys = true // Field 값이 없는 경우 무시
        coerceInputValues = true // "null" 이 들어간경우 default Argument 값으로 대체
    }

    private fun createHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .retryOnConnectionFailure(true)
        .build()

    @ExperimentalSerializationApi
    private fun createRetrofit(httpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(httpClient)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io())) // 작업 쓰레드 자동으로 io 해주는 타입
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    fun apiService(): CentersApiService {
        if (apiService == null) {
            apiService = retrofit.create(CentersApiService::class.java)
        }
        return apiService!!
    }
}