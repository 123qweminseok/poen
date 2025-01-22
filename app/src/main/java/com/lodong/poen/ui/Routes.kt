package com.lodong.poen.ui.navigation

sealed class Routes(val route: String) {
    object LoginScreen : Routes("login")
    object MainScreen : Routes("main")
    object SignUpScreen : Routes("signup")
    object SettingsScreen : Routes("settings")
    object AccountSettingsScreen : Routes("account_settings")
    object NoticeScreen : Routes("notice")
    object SupportScreen : Routes("support")
    object VersionInfoScreen : Routes("version_info")
    object UserInfoEditScreen : Routes("user_info_edit")
    object PasswordChangeScreen : Routes("password_change")
    object LogoutScreen : Routes("logout")
    object AccountDeletionScreen : Routes("account_deletion")
    object BluetoothScreen : Routes("bluetooth")
    object BatteryInfoScreen : Routes("battery_info")
    object DiagnoseScreen : Routes("diagnose")
    object LoadingScreen : Routes("loading_screen")
    object PermissionRequestScreen : Routes("permission_request_screen")
}
