import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lodong.poen.viewmodel.LoginViewModel
import com.lodong.poen.repository.SignUpRepository

class LoginViewModelFactory(
    private val signUpRepository: SignUpRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(signUpRepository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
