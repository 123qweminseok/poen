package com.lodong.poen.ui.navigation

import BluetoothViewModel
import LoginViewModelFactory
import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lodong.poen.factory.BluetoothViewModelFactory
import com.lodong.poen.repository.BinaryBleRepository
import com.lodong.poen.repository.SignUpRepository

import com.lodong.poen.service.BluetoothForegroundService
import com.lodong.poen.ui.screens.*
import com.lodong.poen.viewmodel.LoginViewModel

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    bluetoothService: BluetoothForegroundService,
    binaryBleRepository: BinaryBleRepository,
    signUpRepository: SignUpRepository
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val loginViewModelFactory = LoginViewModelFactory(signUpRepository, application)

    val preferencesHelper = PreferencesHelper.getInstance(context)
    val bluetoothViewModel = remember {
        BluetoothViewModel.getInstance(
            service = bluetoothService,
            repository = binaryBleRepository,
            preferencesHelper = preferencesHelper
        )
    }

    NavHost(
        navController = navController,
        startDestination = Routes.PermissionRequestScreen.route
    ) {
        composable(Routes.PermissionRequestScreen.route) {
            PermissionRequestScreen(navController = navController)
        }
        composable(Routes.LoadingScreen.route) {
            LoadingScreen(
            )
        }
        composable(Routes.LoginScreen.route) {
            val loginViewModel: LoginViewModel = viewModel(factory = loginViewModelFactory)
            LoginScreen(
                loginViewModel = loginViewModel,
                onLoginSuccess = { navController.navigate(Routes.MainScreen.route) },
                onSignUpNavigation = { navController.navigate(Routes.SignUpScreen.route) }
            )
        }

        composable(Routes.MainScreen.route) {
            MainScreen(
                onSettingsNavigation = { navController.navigate(Routes.SettingsScreen.route) },
                onBatteryInfoNavigation = { navController.navigate(Routes.BatteryInfoScreen.route) },
                onBluetoothNavigation = { navController.navigate(Routes.BluetoothScreen.route) },
                onDiagnoseNavigation = { navController.navigate(Routes.DiagnoseScreen.route) }
            )
        }

        composable(Routes.SignUpScreen.route) {
            SignUpScreen(        api = signUpRepository.getSignUpApis()  // SignUpRepository에서 SignUpApis 인스턴스를 가져옴
            )
        }
        composable(Routes.SettingsScreen.route) {
            SettingsScreen(
                onAccountSettingNavigation = { navController.navigate(Routes.AccountSettingsScreen.route) },
                onNoticeNavigation = { navController.navigate(Routes.NoticeScreen.route) },
                onSupportNavigation = { navController.navigate(Routes.SupportScreen.route) },
                onVersionInfoNavigation = { navController.navigate(Routes.VersionInfoScreen.route) },
                onBackButtonPressed = { navController.popBackStack() }
            )
        }
        composable(Routes.AccountSettingsScreen.route) {
            AccountSettingScreen(
                onBackButtonPressed = { navController.popBackStack() },
                onInfoEditNavigation = { navController.navigate(Routes.UserInfoEditScreen.route) },
                onLogoutNavigation = { navController.navigate(Routes.LogoutScreen.route) },
                onPasswordChangeNavigation = { navController.navigate(Routes.PasswordChangeScreen.route) },
                onAccountDeletionNavigation = { navController.navigate(Routes.AccountDeletionScreen.route) }
            )
        }
        composable(Routes.BluetoothScreen.route) {
            BluetoothScreen(
                bluetoothViewModel = bluetoothViewModel,
                onBackButtonPressed = { navController.popBackStack() }
            )
        }
        composable(Routes.BatteryInfoScreen.route) {
            BatteryInfoInputScenario(onExitScenario = { navController.popBackStack() })
        }
        composable(Routes.DiagnoseScreen.route) {
            DiagnoseScreen(
                context = LocalContext.current,
                onBackButtonPressed = { navController.popBackStack() },
                bluetoothViewModel = bluetoothViewModel,
                preferencesHelper = preferencesHelper
            )
        }

        composable(Routes.NoticeScreen.route) {
            NoticeScreen(onBackButtonPressed = { navController.popBackStack() })
        }
        composable(Routes.SupportScreen.route) {
            InquiryScreen(onBackButtonPressed = { navController.popBackStack() })
        }
        composable(Routes.VersionInfoScreen.route) {
            VersionInfoScreen(onBackButtonPressed = { navController.popBackStack() })
        }
        composable(Routes.UserInfoEditScreen.route) {
            UserInfoEditScreen(
                isSeller = false,
                onBackButtonPressed = { navController.popBackStack() })
        }
        composable(Routes.PasswordChangeScreen.route) {
            PasswordChangeScreen(onBackButtonPressed = { navController.popBackStack() })
        }
        composable(Routes.LogoutScreen.route) {
            LogoutScreen()
        }
        composable(Routes.AccountDeletionScreen.route) {
            DeleteAccountScreen()
        }
    }
}
