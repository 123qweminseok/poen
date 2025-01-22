import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lodong.poen.repository.BatteryInfoRepository
import com.lodong.poen.viewmodel.BatteryInfoViewModel

class BatteryInfoViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BatteryInfoViewModel::class.java)) {
            val repository = BatteryInfoRepository(context)
            val preferencesHelper = PreferencesHelper.getInstance(context) // PreferencesHelper 초기화
            return BatteryInfoViewModel(repository, preferencesHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
