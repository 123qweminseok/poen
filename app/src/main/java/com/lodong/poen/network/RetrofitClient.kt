package com.lodong.poen.network

import PreferencesHelper
import android.util.Log
import org.greenrobot.eventbus.EventBus
import com.lodong.utils.ApiResponse
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://beri-link.co.kr"

// API 인터페이스 추가
interface AuthApis {
    @POST("/api/auth/token/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<ApiResponse<TokenResponse>>
}

data class RefreshTokenRequest(
    val accessToken: String,
    val refreshToken: String
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val loginType: String
)

data class ServerErrorEvent(val message: String = "서버 응답 오류")
data class ServerSuccessEvent(val message: String = "전송 성공")











//ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ상태감지후 메시지 띄우기ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ


class RetrofitClient(baseUrl: String, private val preferencesHelper: PreferencesHelper) {
    private val mutex = Mutex()



    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder().apply {
            val token = preferencesHelper.getAccessToken()
            Log.d("RetrofitClient", "Token from preferences: $token")

            token?.let {
                val authHeader = "Bearer $it"
                Log.d("RetrofitClient", "Adding auth header: $authHeader")
                addHeader("Authorization", "Bearer $it")
            } ?: Log.e("RetrofitClient", "Token is null!")
        }.build()

        Log.d("RetrofitClient", "Final request: ${request.url}")
        Log.d("RetrofitClient", "Final headers: ${request.headers}")

        var response = chain.proceed(request)



        Log.d("RetrofitClient", "Response code: ${response.code}")


        // 401 또는 403 에러 시 토큰 갱신 시도
        if (response.code == 401 || response.code == 403) {
            Log.d("RetrofitClient", "Token unauthorized or forbidden (${response.code}), attempting refresh")
            runBlocking {
                mutex.withLock {
                    val newTokens = refreshToken()
                    if (newTokens != null) {
                        Log.d("RetrofitClient", "Token refresh successful, retrying request")
                        response.close()
                        val newRequest = request.newBuilder()
                            .header("Authorization", "Bearer ${newTokens.accessToken}")
                            .build()
                        response = chain.proceed(newRequest)
                    } else {
                        Log.e("RetrofitClient", "Token refresh failed")
                    }
                }
            }
        }
        // 응답 코드가 200이 아닌 모든 경우에 대해 에러 처리  해당 부분이 이제 전송시 에러 코드 띄움 ㅇㅇ
        if (response.code != 200) {
            EventBus.getDefault().post(ServerErrorEvent())
            Log.e("RetrofitClient", "Error: Response code is not 200 (${response.code})")
        }


        response
    }
//ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ상태감지

//    private val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(authInterceptor)
//        .build()

//2025.02.21 수정. 문제시 위에 부분 가져오면 됨.
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)  // 연결 타임아웃
        .writeTimeout(60, TimeUnit.SECONDS)    // 쓰기 타임아웃
        .readTimeout(60, TimeUnit.SECONDS)     // 읽기 타임아웃
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private suspend fun refreshToken(): TokenResponse? {
        return try {
            val authApi = retrofit.create(AuthApis::class.java)
            val accessToken = preferencesHelper.getAccessToken()
            val refreshToken = preferencesHelper.getRefreshToken()

            if (accessToken != null && refreshToken != null) {
                val response = authApi.refreshToken(
                    RefreshTokenRequest(accessToken, refreshToken)
                )

                if (response.isSuccessful && response.body()?.status == 200) {
                    response.body()?.data?.let { tokens ->
                        preferencesHelper.saveTokens(
                            tokens.accessToken,
                            tokens.refreshToken
                        )
                        return tokens
                    }
                }
            }
            null
        } catch (e: Exception) {
            Log.e("RetrofitClient", "Token refresh failed", e)
            null
        }
    }











    fun <T> getApiService(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    companion object {
        @Volatile private var instance: RetrofitClient? = null

        fun getInstance(baseUrl: String, preferencesHelper: PreferencesHelper): RetrofitClient {
            return instance ?: synchronized(this) {
                instance ?: RetrofitClient(baseUrl, preferencesHelper).also { instance = it }
            }
        }
    }
}