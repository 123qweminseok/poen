package com.lodong.poen.ui.navigation

import BluetoothViewModel
import LoginViewModelFactory
import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lodong.poen.factory.BluetoothViewModelFactory
import com.lodong.poen.factory.FindIdViewModelFactory
import com.lodong.poen.factory.FindPasswordViewModelFactory
import com.lodong.poen.factory.UserInfoViewModelFactory
import com.lodong.poen.repository.BatteryInfoRepository
import com.lodong.poen.repository.BinaryBleRepository
import com.lodong.poen.repository.SignUpRepository

import com.lodong.poen.service.BluetoothForegroundService
import com.lodong.poen.ui.screens.*
import com.lodong.poen.viewmodel.FindIdViewModel
import com.lodong.poen.viewmodel.FindPasswordViewModel
import com.lodong.poen.viewmodel.LoginViewModel
import com.lodong.poen.viewmodel.UserInfoViewModel

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    bluetoothService: BluetoothForegroundService,
    binaryBleRepository: BinaryBleRepository,
    signUpRepository: SignUpRepository,
    batteryInfoRepository: BatteryInfoRepository // 추가

) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val loginViewModelFactory = LoginViewModelFactory(signUpRepository, application)
    val findIdViewModelFactory = FindIdViewModelFactory(signUpRepository.getSignUpApis())
    val findPasswordViewModelFactory = FindPasswordViewModelFactory(signUpRepository.getSignUpApis())

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
                onSignUpNavigation = { navController.navigate(Routes.SignUpScreen.route) },
                navController = navController // NavController 전달
            )

        }


        composable(Routes.MainScreen.route) {
            MainScreen(
                onSettingsNavigation = { navController.navigate(Routes.SettingsScreen.route) },
                onBatteryInfoNavigation = { navController.navigate(Routes.BatteryInfoScreen.route) },
                onBluetoothNavigation = { navController.navigate(Routes.BluetoothScreen.route) },
                onDiagnoseNavigation = { navController.navigate(Routes.DiagnoseScreen.route) },
                navController = navController  // NavController 전달
            )
        }

        composable("find_account_password") {
            val findIdViewModel: FindIdViewModel = viewModel(factory = findIdViewModelFactory)
            val findPasswordViewModel: FindPasswordViewModel =
                viewModel(factory = findPasswordViewModelFactory)
            FindAccountPasswordScreen(
                navController = navController,
                findIdViewModel = findIdViewModel,
                findPasswordViewModel = findPasswordViewModel
            )
        }
        composable(
            route = "find_id_result/{identifier}/{regDate}/{name}/{email}",  // name과 email 추가
            arguments = listOf(
                navArgument("identifier") { type = NavType.StringType },
                navArgument("regDate") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            FindIdResultScreen(
                navController = navController,
                identifier = backStackEntry.arguments?.getString("identifier") ?: "",
                regDate = backStackEntry.arguments?.getString("regDate") ?: "",
                name = backStackEntry.arguments?.getString("name") ?: "",
                email = backStackEntry.arguments?.getString("email") ?: ""
            )
        }
        composable(
            route = "find_password_result/{identifier}",
            arguments = listOf(
                navArgument("identifier") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            FindPasswordResultScreen(
                navController = navController,
                identifier = backStackEntry.arguments?.getString("identifier") ?: ""
            )
        }


        composable(Routes.PasswordChangeScreen.route) {
            val memberApi = signUpRepository.createMemberApi()
            PasswordChangeScreen(
                api = memberApi,
                preferencesHelper = preferencesHelper,
                navController = navController,
                onBackButtonPressed = { navController.popBackStack() }
            )
        }

        composable(Routes.SignUpScreen.route) {
            val loginViewModel: LoginViewModel = viewModel(factory = loginViewModelFactory)  // 추가
            SignUpScreen(
                api = signUpRepository.getSignUpApis(),
                navController = navController,
                context = LocalContext.current,
                loginViewModel = loginViewModel
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
                onInfoEditNavigation = {
                    navController.navigate(Routes.UserInfoEditScreen.route)
                },
                onLogoutNavigation = {
                    // 로그아웃 시 메인화면까지 스택 제거
                    navController.navigate(Routes.LogoutScreen.route) {
                        popUpTo(Routes.MainScreen.route) { inclusive = true }
                    }
                },
                onPasswordChangeNavigation = {
                    navController.navigate(Routes.PasswordChangeScreen.route)
                },
                onAccountDeletionNavigation = {
                    navController.navigate(Routes.AccountDeletionScreen.route)
                }
            )
        }

        composable(Routes.BluetoothScreen.route) {
            BluetoothScreen(
                bluetoothViewModel = bluetoothViewModel,
                onBackButtonPressed = { navController.popBackStack() }
            )
        }
        composable(Routes.BatteryInfoScreen.route) {
            BatteryInfoInputScenario(
                onExitScenario = { navController.popBackStack() },
                navController = navController  // navController 전달
            )
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
            val preferencesHelper = PreferencesHelper.getInstance(context)
            val token = preferencesHelper.getAccessToken() ?: "" // token 값 가져오기
            val memberApi = signUpRepository.createMemberApi()
            val userInfoViewModel: UserInfoViewModel = viewModel(
                factory = UserInfoViewModelFactory(memberApi, token) // token 전달
            )
            val loginViewModel: LoginViewModel = viewModel(factory = loginViewModelFactory)

            UserInfoEditScreen(
                isSeller = false,
                api = memberApi,
                viewModel = userInfoViewModel,
                navController = navController, // 전달

                preferencesHelper = preferencesHelper, // 전달
                onBackButtonPressed = { navController.popBackStack() },
                api2 = signUpRepository.getSignUpApis(),  // SignUpApis 인스턴스 전달
                loginViewModel = loginViewModel  // LoginViewModel 인스턴스 전달

            )
        }


//        composable(Routes.PasswordChangeScreen.route) {
//            PasswordChangeScreen(onBackButtonPressed = { navController.popBackStack() })
//        }
        composable(Routes.LogoutScreen.route) {
            LogoutScreen(
                navController = navController,
                preferencesHelper = preferencesHelper
            )
        }
        composable(Routes.AccountDeletionScreen.route) {
            val memberApi = signUpRepository.createMemberApi()
            DeleteAccountScreen(
                api = memberApi,
                preferencesHelper = preferencesHelper,
                navController = navController,
                onBackButtonPressed = { navController.popBackStack() }
            )
        }
    }

}
