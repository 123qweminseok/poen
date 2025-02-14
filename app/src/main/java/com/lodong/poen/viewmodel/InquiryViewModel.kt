package com.lodong.poen.viewmodel
import retrofit2.http.Query
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lodong.apis.MemberApi
import com.lodong.apis.MemberApi.InquiryRequest
import com.lodong.apis.MemberApi.InquiryResponse
import com.lodong.apis.ServiceLocator
import com.lodong.apis.ServiceLocator.memberApi
import com.lodong.poen.network.BASE_URL
import com.lodong.poen.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.lodong.apis.MemberApi.InquiryDetail


class InquiryViewModel(
    private val memberApi: MemberApi
) : ViewModel() {

    class InquiryViewModelFactory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val preferencesHelper = PreferencesHelper.getInstance(context)
            val memberApi = RetrofitClient.getInstance(BASE_URL, preferencesHelper)
                .getApiService(MemberApi::class.java)
            return InquiryViewModel(memberApi) as T
        }
    }


    private val _inquiries = MutableStateFlow<List<InquiryResponse>>(emptyList())
    val inquiries: StateFlow<List<InquiryResponse>> = _inquiries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        Log.d("InquiryViewModel", "ViewModel 초기화 - loadInquiries 호출")

        loadInquiries()
    }




    suspend fun getInquiryDetail(questionId: String): InquiryDetail? {
        return try {
            _isLoading.value = true
            Log.d("InquiryViewModel", "문의 상세 조회 시작 - questionId: $questionId")

            val response = memberApi.getInquiryDetail(questionId)
            Log.d("InquiryViewModel", "문의 상세 조회 응답 코드: ${response.code()}")
            Log.d("InquiryViewModel", "문의 상세 조회 원본 응답: ${response.body()}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("InquiryViewModel", "응답 데이터 구조: $responseBody")

                responseBody?.data.also {
                    Log.d("InquiryViewModel", "문의 상세 조회 성공")
                }
            } else {
                Log.e("InquiryViewModel", "문의 상세 조회 실패 - HTTP ${response.code()}")
                val errorBody = response.errorBody()?.string()
                Log.e("InquiryViewModel", "에러 응답: $errorBody")
                throw Exception("문의 상세 내용을 불러오는데 실패했습니다.")
            }
        } catch (e: Exception) {
            Log.e("InquiryViewModel", "문의 상세 조회 중 예외 발생", e)
            Log.e("InquiryViewModel", "상세 에러: ${e.stackTraceToString()}")
            throw e
        } finally {
            _isLoading.value = false
        }
    }


    fun createInquiry(title: String, content: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("InquiryViewModel", "문의 작성 시작 - 제목: $title, 내용: $content")

                val request = InquiryRequest(title, content)
                Log.d("InquiryViewModel", "요청 데이터: $request")

                val response = memberApi.createInquiry(request)
                Log.d("InquiryViewModel", "서버 응답 코드: ${response.code()}")
                Log.d("InquiryViewModel", "서버 응답 바디: ${response.body()}")

                if (response.isSuccessful) {
                    Log.d("InquiryViewModel", "문의 작성 성공")
                    onSuccess()
                    loadInquiries()
                } else {
                    Log.e("InquiryViewModel", "문의 작성 실패 - HTTP ${response.code()}")
                    Log.e("InquiryViewModel", "에러 메시지: ${response.errorBody()?.string()}")
                    _error.value = "문의 작성에 실패했습니다. (${response.code()})"
                }
            } catch (e: Exception) {
                Log.e("InquiryViewModel", "문의 작성 중 예외 발생", e)
                Log.e("InquiryViewModel", "상세 에러: ${e.stackTraceToString()}")
                _error.value = "문의 작성 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadInquiries(page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("InquiryViewModel", "문의 목록 로딩 시작")
                val response = memberApi.getInquiries(page = page, size = size)
                Log.d("InquiryViewModel", "문의 목록 응답 코드: ${response.code()}")
                Log.d("InquiryViewModel", "문의 목록 원본 응답: ${response.body()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("InquiryViewModel", "응답 데이터 구조: $responseBody")

                    // InquiriesResponse의 data 필드에서 content를 가져옴
                    val content = responseBody?.data?.content
                    Log.d("InquiryViewModel", "파싱된 문의 목록: $content")

                    if (content != null) {
                        _inquiries.value = content
                        Log.d("InquiryViewModel", "문의 목록 로딩 성공 - ${content.size}개 항목")
                    } else {
                        Log.e("InquiryViewModel", "문의 목록이 null입니다")
                        _error.value = "문의내역을 불러올 수 없습니다."
                    }
                } else {
                    Log.e("InquiryViewModel", "문의 목록 로딩 실패 - HTTP ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("InquiryViewModel", "에러 응답: $errorBody")
                    _error.value = "문의내역을 불러오는데 실패했습니다."
                }
            } catch (e: Exception) {
                Log.e("InquiryViewModel", "문의 목록 로딩 중 예외 발생", e)
                Log.e("InquiryViewModel", "상세 에러: ${e.stackTraceToString()}")
                _error.value = "문의 목록을 불러오는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteInquiry(questionId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("InquiryViewModel", "문의 삭제 시작 - questionId: $questionId")

                val response = memberApi.deleteInquiry(questionId)
                Log.d("InquiryViewModel", "문의 삭제 응답 코드: ${response.code()}")

                if (response.isSuccessful) {
                    Log.d("InquiryViewModel", "문의 삭제 성공")
                    loadInquiries() // getInquiries() 대신 loadInquiries() 사용
                    onSuccess()
                } else {
                    Log.e("InquiryViewModel", "문의 삭제 실패 - HTTP ${response.code()}")
                    _error.value = "삭제에 실패했습니다."
                }
            } catch (e: Exception) {
                Log.e("InquiryViewModel", "문의 삭제 중 예외 발생", e)
                _error.value = e.message ?: "알 수 없는 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun updateInquiry(questionId: String, title: String, content: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("InquiryViewModel", "문의 수정 시작 - questionId: $questionId")

                val request = InquiryRequest(title, content)
                val response = memberApi.updateInquiry(questionId, request)

                if (response.isSuccessful) {
                    Log.d("InquiryViewModel", "문의 수정 성공")
                    loadInquiries()
                    onSuccess()
                } else {
                    Log.e("InquiryViewModel", "문의 수정 실패 - HTTP ${response.code()}")
                    _error.value = "수정에 실패했습니다."
                }
            } catch (e: Exception) {
                Log.e("InquiryViewModel", "문의 수정 중 예외 발생", e)
                _error.value = e.message ?: "알 수 없는 오류가 발생했습니다."
            } finally {
                _isLoading.value = false
            }
        }
    }


}
