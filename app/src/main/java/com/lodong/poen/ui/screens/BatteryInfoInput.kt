package com.lodong.poen.ui.screens
import android.Manifest
import BatteryInfoViewModelFactory
import PreferencesHelper
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lodong.poen.ui.Popup
import com.lodong.poen.R
import com.lodong.poen.dto.batteryinfo.BatteryRequest
import com.lodong.poen.ui.FieldLabel
import com.lodong.poen.ui.Header
import com.lodong.poen.ui.SelectorField
import com.lodong.poen.ui.theme.lightSelector
import com.lodong.poen.ui.theme.primaryColor
import com.lodong.poen.ui.theme.primaryLight
import com.lodong.poen.viewmodel.BatteryInfoViewModel
import com.lodong.utils.InputField
import com.lodong.utils.InputFieldWithDatePicker
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.lodong.poen.ui.navigation.Routes
import com.lodong.utils.KeyboardAwareLayout

private enum class BatteryInfoInputStage {
    BASIC_INFO, QR_CODE, PHOTO
}

@Composable
fun BatteryInfoInputScenario(onExitScenario: () -> Unit,    navController: NavController   // context를 appContext로 변경
    // NavController 파라미터 추가
) {
    val currentStage = remember { mutableStateOf(BatteryInfoInputStage.BASIC_INFO) }
    val onPopupDismiss = remember { mutableStateOf<() -> Unit>({}) }





    BackHandler {
        when (currentStage.value) {
            BatteryInfoInputStage.BASIC_INFO -> onExitScenario()
            BatteryInfoInputStage.QR_CODE -> currentStage.value = BatteryInfoInputStage.BASIC_INFO
            BatteryInfoInputStage.PHOTO -> currentStage.value = BatteryInfoInputStage.QR_CODE
        }
    }

    val isPopupShown = remember { mutableStateOf(false) }
    val popupText = remember { mutableStateOf("") }

    fun showPopup(text: String, onDismiss: () -> Unit = {}) {
        popupText.value = text
        onPopupDismiss.value = onDismiss
        isPopupShown.value = true
    }



    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        when (currentStage.value) {
            BatteryInfoInputStage.BASIC_INFO -> {
                val context = LocalContext.current
                KeyboardAwareLayout {
                    BatteryInfoInput0(
                        context = context,
                        onBackButtonPressed = onExitScenario,
                        onNavigateToQRScreen = {
                            currentStage.value = BatteryInfoInputStage.QR_CODE
                        }
                    )
                }
            }

            BatteryInfoInputStage.QR_CODE -> {
                val context = LocalContext.current // Context를 가져옴
                BatteryInfoInput1(
                    context = context, // context 전달
                    onBackButtonPressed = { currentStage.value = BatteryInfoInputStage.BASIC_INFO },
                    onNext = {
                        currentStage.value = BatteryInfoInputStage.PHOTO
                        showPopup("QR코드 업로드가 완료되었습니다.") // 저장 완료 후 팝업 표시
                    }
                )
            }


            BatteryInfoInputStage.PHOTO -> BatteryInfoInput2(
                onBackButtonPressed = { currentStage.value = BatteryInfoInputStage.QR_CODE },
                onSave = {
                    showPopup("사진 업로드가 완료되었습니다.") {
                        // 팝업의 확인 버튼 클릭 시 메인 화면으로 이동
                        navController.navigate(Routes.MainScreen.route) {
                            popUpTo(Routes.BatteryInfoScreen.route) { inclusive = true }
                        }
                    }
                },
                navController = navController
            )
        }

        if (isPopupShown.value) {
            Box(
                modifier = Modifier
                    .background(Color(0x80000000))
                    .fillMaxSize()
                    .clickable { }
            )
            Popup(
                text = popupText.value,
                onDismiss = { isPopupShown.value = false
                    onPopupDismiss.value()  // 이 부분이 빠져있었습니다
                }
            )
        }
    }
}


@Composable
fun BatteryInfoInput0(
    context: Context,
    onBackButtonPressed: () -> Unit,
    onNavigateToQRScreen: () -> Unit // QR 화면으로 이동 콜백
) {
    val viewModel: BatteryInfoViewModel = viewModel(factory = BatteryInfoViewModelFactory(context))

    val preferencesHelper = PreferencesHelper.getInstance(context)
    val savedBatteryInfo = preferencesHelper.getBatteryInfo()

    val manufacturers = viewModel.manufacturers.collectAsState()
    val models = viewModel.models.collectAsState()
    val loading = viewModel.loading.collectAsState()
    val error = viewModel.error.collectAsState()
    val navigateToQRScreen by viewModel.navigateToQRScreen.collectAsState()




//2025.02.21수정사항. -초기화 한다고 했음. 6개 변수 추가 이부분 오류시ㅋ
//    val selectedManufacturer = remember {
//        mutableStateOf<Pair<String, String?>?>(
//            savedBatteryInfo["car_manufacturer_name"]?.let { name ->
//                Pair(name, savedBatteryInfo["car_manufacturer_id"])
//            }
//        )
//    }
//
//    val selectedModel = remember {
//        mutableStateOf<Pair<String, String?>?>(
//            savedBatteryInfo["car_model_name"]?.let { name ->
//                Pair(name, savedBatteryInfo["car_model_id"])
//            }
//        )
//    }
//
//
//    val vehicleNumber = remember { mutableStateOf(savedBatteryInfo["car_no"] ?: "") }
//    val productNumber = remember { mutableStateOf(savedBatteryInfo["product_no"] ?: "") }
//    val romId = remember { mutableStateOf(savedBatteryInfo["rom_id"] ?: "") }
//    val productionDate = remember { mutableStateOf(savedBatteryInfo["production_date"] ?: "") }



    val selectedManufacturer = remember { mutableStateOf<Pair<String, String?>?>(null) }
    val selectedModel = remember { mutableStateOf<Pair<String, String?>?>(null) }
    val vehicleNumber = remember { mutableStateOf("") }
    val productNumber = remember { mutableStateOf("") }
    val romId = remember { mutableStateOf("") }
    val productionDate = remember { mutableStateOf("") }

    val saveError = remember { mutableStateOf<String?>(null) }

    // QR 화면 이동 상태 감지
    LaunchedEffect(navigateToQRScreen) {
        if (navigateToQRScreen) {
            viewModel.onNavigatedToQRScreen() // 상태 초기화
            onNavigateToQRScreen() // QR 화면으로 이동
        }
    }

    // 초기 제조사 데이터 로드
    LaunchedEffect(Unit) {
        viewModel.fetchManufacturers()
    }

    // 제조사 선택 변경 시 모델 로드
    LaunchedEffect(selectedManufacturer.value) {
        val manufacturerId = selectedManufacturer.value?.second // 선택된 제조사의 ID
        manufacturerId?.let { viewModel.fetchModels(it) }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(text = "배터리 정보 입력", onBackButtonPressed = onBackButtonPressed)
        BatteryInfoProgress(progressIndex = 0)

        Spacer(modifier = Modifier.size(16.dp))

        // 로딩 상태 표시
        if (loading.value) {
            Text("Loading...", color = Color.Gray)
        } else if (error.value != null) {
            // 에러 상태 표시
            Text("Error: ${error.value}", color = Color.Red)
        } else {
            // 정상 상태 UI
            Column(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SelectorField(
                    label = { FieldLabel(text = "제조사", required = true) },
                    selections = manufacturers.value.map { it.carManufacturerName },
                    selected = selectedManufacturer.value?.first ?: "제조사를 선택해주세요",
                    onSelected = { selectedName ->
                        val manufacturerId =
                            manufacturers.value.firstOrNull { it.carManufacturerName == selectedName }?.carManufacturerId
                        selectedManufacturer.value = Pair(selectedName, manufacturerId)
                        //다른 필드 초기화
                        selectedModel.value = null
                        vehicleNumber.value = ""
                        productNumber.value = ""
                        romId.value = ""
                        productionDate.value = ""

                    }
                )

                SelectorField(
                    label = { FieldLabel(text = "차종", required = true) },
                    selections = models.value.map { it.carModelName },
                    selected = selectedModel.value?.first ?: "차종을 선택해주세요",
                    onSelected = { selectedName ->
                        val carModelId =
                            models.value.firstOrNull { it.carModelName == selectedName }?.carModelId
                        selectedModel.value = Pair(selectedName, carModelId)
                        vehicleNumber.value = ""
                        productNumber.value = ""
                        romId.value = ""
                        productionDate.value = ""

                    }
                )

                InputField(
                    label = { FieldLabel(text = "차량번호") },
                    value = vehicleNumber.value,
                    onValueChange = { vehicleNumber.value = it }
                )
                InputField(
                    label = { FieldLabel(text = "제품번호") },
                    value = productNumber.value,
                    onValueChange = { productNumber.value = it }
                )
                InputField(
                    label = { FieldLabel(text = "ROM ID") },
                    value = romId.value,
                    onValueChange = { romId.value = it }
                )
                InputFieldWithDatePicker(
                    label = { FieldLabel(text = "생산일") },
                    value = productionDate.value,
                    onDateSelected = { productionDate.value = it }
                )
            }
        }











        Spacer(modifier = Modifier.size(32.dp))

        if (saveError.value != null) {
            Text("저장 실패: ${saveError.value}", color = Color.Red)
            Spacer(modifier = Modifier.size(16.dp))
        }

        // 저장하기 버튼
        Button(
            onClick = {
                // 제조사 ID와 이름 추출
                val manufacturerId = selectedManufacturer.value?.second
                val manufacturerName = selectedManufacturer.value?.first ?: ""

                // 차종 ID와 이름 추출
                val modelId = selectedModel.value?.second
                val modelName = selectedModel.value?.first ?: ""

                val isSameAsSaved = savedBatteryInfo["car_manufacturer_id"] == manufacturerId &&
                        savedBatteryInfo["car_manufacturer_name"] == manufacturerName &&
                        savedBatteryInfo["car_model_id"] == modelId &&
                        savedBatteryInfo["car_model_name"] == modelName &&
                        savedBatteryInfo["car_no"] == vehicleNumber.value &&
                        savedBatteryInfo["product_no"] == productNumber.value &&
                        savedBatteryInfo["production_date"] == productionDate.value &&
                        savedBatteryInfo["rom_id"] == romId.value
                if (isSameAsSaved) {
                    // 저장된 정보와 동일하므로 바로 다음 화면으로 이동
                    Log.d("BatteryInfoInput0", "정보가 동일하므로 저장하지 않고 바로 이동합니다.")
                    onNavigateToQRScreen()
                } else {
                    if (manufacturerId != null && modelId != null) {
                        val request = BatteryRequest(
                            carManufacturerId = manufacturerId,
                            carModelId = modelId,
                            carNo = vehicleNumber.value,
                            productNo = productNumber.value,
                            productionDate = productionDate.value,
                            romId = romId.value
                        )
                        // 제조사 이름과 차종 이름을 함께 전달
                        viewModel.saveBatteryInfo(request, manufacturerName, modelName)
                    } else {
                        saveError.value = "필수 입력값이 누락되었습니다."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(primaryLight)
        ) {
            Text("저장하기", color = Color.White)
        }
    }
}



@Composable
fun BatteryInfoProgress(progressIndex: Int) {
    Box(
        modifier = Modifier
            .border(1.dp, Color.LightGray)
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Box(
                modifier = Modifier.padding(horizontal = 8.dp), contentAlignment = Alignment.Center
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = if (progressIndex == 0) 0.0f else if (progressIndex == 1) 0.5f else 1.0f,
                    color = Color.Red
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = if (progressIndex == 0) R.drawable.progress_checked else R.drawable.progress_complete),
                        contentDescription = "checked",
                        modifier = Modifier.size(16.dp)
                    )
                    Image(
                        painter = painterResource(id = if (progressIndex < 1) R.drawable.progress_unchecked else if (progressIndex == 1) R.drawable.progress_checked else R.drawable.progress_complete),
                        contentDescription = "unchecked",
                        modifier = Modifier.size(16.dp)
                    )
                    Image(
                        painter = painterResource(id = if (progressIndex < 2) R.drawable.progress_unchecked else R.drawable.progress_checked),
                        contentDescription = "unchecked",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                val fontSize = 12.sp
                Text(
                    modifier = Modifier.weight(1f),
                    text = "배터리 정보입력",
                    color = Color.Black,
                    fontSize = fontSize,
                    textAlign = TextAlign.Start
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = "QR코드 입력",
                    color = Color.Black,
                    fontSize = fontSize,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = "사진 선택",
                    color = Color.Black,
                    fontSize = fontSize,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
@Composable
fun BatteryInfoInput1(
    context: Context,
    onBackButtonPressed: () -> Unit,
    onNext: () -> Unit
) {
    val preferencesHelper = PreferencesHelper.getInstance(context)
    val viewModel: BatteryInfoViewModel = viewModel(factory = BatteryInfoViewModelFactory(context))

    val isScanning = remember { mutableStateOf(false) } // 스캐닝 여부 상태
    val scannedResult = remember { mutableStateOf("BS09POE2N240422A0078") } // 스캔된 결과 저장
    val loading = viewModel.loading.collectAsState()
    val error = viewModel.error.collectAsState()


    // 카메라 권한 체크 및 요청
    val hasCameraPermission = remember { mutableStateOf(false) }


    var showPermissionDialog by remember { mutableStateOf(false) }



    // 권한 요청 런처 추가
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            hasCameraPermission.value = true
            isScanning.value = true
        } else {
            showPermissionDialog = true
        }
    }


    fun checkCameraPermission(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // 안드로이드 13 이상
                ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
            }
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.P -> {
                // 안드로이드 9 이하
                ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                // 안드로이드 10-12
                ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            }
        }
    }



    // 권한 설정 화면으로 이동하는 함수
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }


    LaunchedEffect(Unit) {
        if (!checkCameraPermission()) {
            val permissions = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                }
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.P -> {
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
                else -> {
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
            requestPermissionLauncher.launch(permissions)
        }
    }


    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("카메라 권한 필요") },
            text = { Text("설정에서 권한을 허용해주세요.") },
            confirmButton = {
                TextButton(onClick = {
                    openAppSettings()
                    showPermissionDialog = false
                }) {
                    Text("설정으로 이동")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("취소")
                }
            }
        )
    }






    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1500)
        isScanning.value = true
    }

    if (isScanning.value) {
        QrCodeScannerScreen(
            onQrCodeScanned = { scannedCode ->
                isScanning.value = false // 스캔 완료 후 스캔 상태 해제
                scannedResult.value = scannedCode // 스캔 결과 저장
            },
            onCancel = {
                isScanning.value = false // 스캔 취소
            }
        )
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Header(text = "QRcode", onBackButtonPressed = onBackButtonPressed)
            Box(modifier = Modifier.fillMaxWidth(0.8f)) {
                BatteryInfoProgress(progressIndex = 1)
            }

            Spacer(modifier = Modifier.size(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .border(1.dp, primaryColor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.qr_sample),
                    contentDescription = "QR 예시 이미지",
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Box(
                modifier = Modifier
                    .background(lightSelector)
                    .border(1.dp, Color.LightGray)
                    .padding(8.dp)
                    .fillMaxWidth(0.8f)
            ) {
                Text(text = scannedResult.value) // 스캔된 결과 표시
            }

            Spacer(modifier = Modifier.size(32.dp))

            Button(
                onClick = {
                    if (checkCameraPermission()) {
                        viewModel.registerQRCode(
                            qrCode = scannedResult.value,
                            onSuccess = onNext
                        )
                    } else {
                        showPermissionDialog = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
                shape = RoundedCornerShape(8.dp),
            ) {
                if (loading.value) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(text = "저장하기", color = Color.White)
                }
            }

            if (error.value != null) {
                Text(
                    text = "Error: ${error.value}",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Image(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
                painter = painterResource(id = R.drawable.logo_transparant),
                contentDescription = "transparent logo"
            )
        }
    }
}
@Composable
fun BatteryInfoInput2(
    onBackButtonPressed: () -> Unit,
    onSave: () -> Unit,
    navController: NavController  // NavController 추가

) {
    val viewModel: BatteryInfoViewModel = viewModel()
//객체를 새로 만드는 게 아니라, 기존 ViewModel이 있으면 재사용하고, 없으면 새로 생성" 하는 방식

    val context = LocalContext.current
    val batteryId = viewModel.batteryId.collectAsState().value ?: ""
    Log.d("BatteryInfoInput2", "batteryId: '$batteryId', batteryId.isEmpty: ${batteryId.isEmpty()}")


    // 이미지 URI 목록을 ViewModel로 이동
    val imageUris = viewModel.imageUris.collectAsState().value


    var showDialog by remember { mutableStateOf(false) }
    // 서버 에러 발생 시 표시할 다이얼로그 상태 변수 추가
    var showServerErrorDialog by remember { mutableStateOf(false) }



    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color.White,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 12.dp,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val logoPainter = painterResource(id = R.drawable.logo)
                    Image(
                        painter = logoPainter,
                        contentDescription = "로고 이미지",
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "알림",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            text = {
                Text(
                    text = "다시 들어가주세요",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.DarkGray
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        // MainScreen으로 이동하며 스택 정리
                        navController.navigate(Routes.MainScreen.route) {
                            popUpTo(Routes.BatteryInfoScreen.route) { inclusive = true }
                        }
                    }
                ) {
                    Text("확인")
                }
            }
        )
    }


    // 서버 에러 다이얼로그 (이미지 업로드 실패 시)
    if (showServerErrorDialog) {
        AlertDialog(
            onDismissRequest = { showServerErrorDialog = false },
            title = { Text("오류") },
            text = { Text("정보를 다시 확인해주세요") },
            confirmButton = {
                TextButton(
                    onClick = { showServerErrorDialog = false }
                ) {
                    Text("확인")
                }
            }
        )
    }


    // 갤러리에서 이미지 선택
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri>? ->
        uris?.let {
            viewModel.addImages(it) // ViewModel의 함수를 통해 이미지 추가
        }
    }

    val loading by viewModel.loading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            Header(text = "사진 업로드", onBackButtonPressed = onBackButtonPressed)
            Box(modifier = Modifier.fillMaxWidth(0.8f)) {
                BatteryInfoProgress(progressIndex = 2)
            }

            Spacer(modifier = Modifier.size(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(Color.Transparent),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "촬영 예시")
                Text(text = "사진 촬영 위치 : 제품번호, 전체, 커넥터 부분, 파손, 이상부분", fontSize = 12.sp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f)
                        .background(lightSelector),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "이미지 미전달")
                }
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Text(text = "1. 필수 업로드 되어야 할 이미지 항목", fontSize = 12.sp)
                    Text(text = "     -제품번호, 전체 외관, 커넥터부분, 파손.이상 부분", fontSize = 12.sp)
                    Text(text = "2. 제품 이미지가 항목별로 자세히 보이도록 촬영해 주세요", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.size(16.dp))

                // 사진 선택 버튼
                Button(
                    onClick = { galleryLauncher.launch(arrayOf("image/*")) },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "camera",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "사진 선택", color = Color.White)
                }

                Spacer(modifier = Modifier.size(16.dp))

                // 이미지 미리보기
                ImagePreviews(imageUris = imageUris)

                Spacer(modifier = Modifier.size(32.dp))



                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
                    onClick = {
                        if (batteryId == null) {
                            showDialog = true
                        } else {

                            val currentImages = imageUris.toList() // 현재 이미지들 복사
                            viewModel.clearImages() // 즉시 이미지 목록 초기화

                            viewModel.uploadMultipleImages(
                                context = context,
                                imageUris = currentImages, // 복사해둔 이미지들로 업로드
                                onSuccess = {
                                    Log.d("ImageUpload", "Images uploaded successfully!")
                                    onSave()
                                },
                                onError = { error ->
                                    Log.e("ImageUpload", "Error: $error")
                                    viewModel.addImages(currentImages) // 실패 시 이미지들 복원
                                    showServerErrorDialog = true
                                }
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "저장", color = Color.White)
                }
            }




            Spacer(modifier = Modifier.weight(1f))

            Image(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
                painter = painterResource(id = R.drawable.logo_transparant),
                contentDescription = "transparent logo"
            )
        }
        // 로딩 상태일 때 전체 화면 오버레이에 큰 로딩 인디케이터 표시
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(100.dp),
                    strokeWidth = 8.dp,
                    color = primaryLight
                )
            }
        }


    }
}



@Composable
fun ImagePreviews(imageUris: List<Uri>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(primaryLight.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.chevron_down),
                contentDescription = "chevron up",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .size(24.dp)
                    .scale(1f, -1f)
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(imageUris.size) { index ->
                val uri = imageUris[index] // URI 가져오기
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(lightSelector),
                    contentAlignment = Alignment.Center
                ) {
                    // Coil AsyncImage를 사용하여 이미지 로드
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected image",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(primaryLight.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.chevron_down),
                contentDescription = "chevron down",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
