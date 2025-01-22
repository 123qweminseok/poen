import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

class PreferencesHelper private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var instance: PreferencesHelper? = null

        fun getInstance(context: Context): PreferencesHelper {
            return instance ?: synchronized(this) {
                instance ?: PreferencesHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            apply()
        }
    }

    fun saveStayLoggedIn(stayLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("stayLoggedIn", stayLoggedIn).apply()
    }

    fun isStayLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("stayLoggedIn", false)
    }


    fun getAccessToken(): String? = sharedPreferences.getString("access_token", null)
    fun getRefreshToken(): String? = sharedPreferences.getString("refresh_token", null)

    // QR 코드 저장
    fun saveQRCode(qrCode: String) {
        sharedPreferences.edit().apply {
            putString("qr_code", qrCode)
            apply()
        }
    }

    // QR 코드 가져오기
    fun getQRCode(): String? {
        return sharedPreferences.getString("qr_code", null)
    }

    fun putString(key: String, value: String?) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    fun saveBatteryInfo(
        batteryId: String,
        carManufacturerId: String,
        carModelId: String,
        carNo: String,
        productNo: String,
        productionDate: String,
        romId: String,
        carManufacturerName: String,
        carModelName:String

    ) {
        sharedPreferences.edit().apply {
            putString("battery_id", batteryId)
            putString("car_manufacturer_id", carManufacturerId.toString())
            putString("car_manufacturer_name", carManufacturerName.toString())
            putString("car_model_id", carModelId)
            putString("car_model_name", carModelName)
            putString("car_no", carNo)
            putString("product_no", productNo)
            putString("production_date", productionDate)
            putString("rom_id", romId)
            apply()
        }
    }

    // 저장된 배터리 정보 가져오기
    fun getBatteryInfo(): Map<String, String?> {
        return mapOf(
            "battery_id" to sharedPreferences.getString("battery_id", null),
            "car_manufacturer_id" to sharedPreferences.getString("car_manufacturer_id", null),
            "car_manufacturer_name" to sharedPreferences.getString("car_manufacturer_name", null),
            "car_model_id" to sharedPreferences.getString("car_model_id", null),
            "car_model_name" to sharedPreferences.getString("car_model_name", null),
            "car_no" to sharedPreferences.getString("car_no", null),
            "product_no" to sharedPreferences.getString("product_no", null),
            "production_date" to sharedPreferences.getString("production_date", null),
            "rom_id" to sharedPreferences.getString("rom_id", null)
        )
    }
}
