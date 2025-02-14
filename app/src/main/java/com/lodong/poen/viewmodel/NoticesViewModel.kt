package com.lodong.poen.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lodong.apis.NoticeApis
import com.lodong.apis.NoticeApis.NoticeItem
import com.lodong.poen.network.BASE_URL
import com.lodong.poen.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoticesViewModel(
    private val noticeApi: NoticeApis
) : ViewModel() {

    private val _notices = MutableStateFlow<List<NoticeItem>>(emptyList())
    val notices: StateFlow<List<NoticeItem>> = _notices.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()


    private val _selectedNoticeContent = MutableStateFlow<String?>(null)
    val selectedNoticeContent: StateFlow<String?> = _selectedNoticeContent.asStateFlow()

    init {
        loadNotices()
    }


    suspend fun getNoticeDetail(noticeId: String) {
        try {
            _isLoading.value = true
            val response = noticeApi.getNoticeDetail(noticeId)
            if (response.isSuccessful) {
                _selectedNoticeContent.value = response.body()?.data?.content
            } else {
                _error.value = "상세 내용을 불러오는데 실패했습니다."
            }
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }



    fun loadNotices(page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("NoticesViewModel", "공지사항 목록 로딩 시작")
                val response = noticeApi.getNotices(page = page, size = size)
                Log.d("NoticesViewModel", "공지사항 목록 응답 코드: ${response.code()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("NoticesViewModel", "응답 데이터: $responseBody")

                    val content = responseBody?.data?.content
                    if (content != null) {
                        _notices.value = content
                        Log.d("NoticesViewModel", "공지사항 로딩 성공 - ${content.size}개")
                    } else {
                        _error.value = "공지사항을 불러올 수 없습니다."
                    }
                } else {
                    Log.e("NoticesViewModel", "공지사항 로딩 실패 - HTTP ${response.code()}")
                    _error.value = "공지사항을 불러오는데 실패했습니다."
                }
            } catch (e: Exception) {
                Log.e("NoticesViewModel", "공지사항 로딩 중 예외 발생", e)
                _error.value = "오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    class NoticesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val preferencesHelper = PreferencesHelper.getInstance(context)
            val noticeApi = RetrofitClient.getInstance(BASE_URL, preferencesHelper)
                .getApiService(NoticeApis::class.java)
            return NoticesViewModel(noticeApi) as T
        }
    }
}