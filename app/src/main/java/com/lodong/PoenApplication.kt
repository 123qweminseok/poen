package com.lodong

import android.app.Application
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig

class PoenApplication : Application(), CameraXConfig.Provider {
    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}
//어플리케이션단에서 프리퍼런스에 토큰 존재할경우 토큰 존재하면 자동로그인처리. 그거 가지고 진단정보  뒤로가기 눌렀을때
//ble링크는 웹사이트로 넘어가는거임. 웹에서 처리 안되니까 웹으로 인트주면 토큰값을 전달해줄수 있는지 확인.