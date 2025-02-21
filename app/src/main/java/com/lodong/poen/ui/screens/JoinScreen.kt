package com.lodong.poen.ui.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lodong.poen.R
import com.lodong.poen.SeverRequestResponse.SignUpViewModel
import com.lodong.poen.ui.CustomBrownButton
import com.lodong.poen.ui.Header
import com.lodong.poen.ui.InfoInputField
import com.lodong.poen.ui.SelectorField
import com.lodong.poen.ui.SettingsCategory
import com.lodong.poen.ui.SettingsEntry
import com.lodong.poen.ui.SettingsHeader
import com.lodong.poen.ui.theme.lightSelector
import com.lodong.poen.ui.theme.primaryColor
import com.lodong.poen.ui.theme.primaryLight
import com.lodong.poen.ui.theme.primaryLighter
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lodong.apis.SignUpApis
import com.lodong.poen.SeverRequestResponse.SignUpViewModelFactory
import com.lodong.poen.ui.CustomBrownButton2
import com.lodong.poen.ui.navigation.Routes
import com.lodong.poen.viewmodel.LoginViewModel
import kotlinx.serialization.json.Json

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive







private fun formatBusinessDate(input: String): String {
    // 하이픈 제거 및 숫자만 추출
    val digits = input.replace("-", "").filter { it.isDigit() }

    return if (digits.length >= 8) {
        val year = digits.substring(0, 4)
        val month = digits.substring(4, 6)
        val day = digits.substring(6, 8)
        "$year-$month-$day"
    } else {
        // 8자리가 안되면 그대로 원본 숫자 반환 (입력 도중에는 변경하지 않음)
        digits
    }
}



@Composable
fun SignUpDialog(
    show: Boolean,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("알림") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("확인", color = primaryColor)
                }
            },
            containerColor = Color.White
        )
    }
}


@Composable
fun SignUpScreen(
    context: Context, // 추가

    api: SignUpApis,
    navController: NavController // NavController 추가
    ,    loginViewModel: LoginViewModel // 추가된 파라미터


) {
    val labelTextStyle = TextStyle(
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp

    )


    val signupViewModel: SignUpViewModel = viewModel(
        factory = SignUpViewModelFactory(api)
    )

    // Dialog 상태 추가
    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }
    val isSeller = remember { mutableStateOf(false) }


    val labelSize = 96.dp
    val inputSize = 168.dp

    val inputHeight = 40.dp
    val password = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val emailCode = remember { mutableStateOf("") }
    val zipCode = remember { mutableStateOf("") }
    val defaultAddress = remember { mutableStateOf("") }
    val detailAddress = remember { mutableStateOf("") }
    val userId = remember { mutableStateOf("") } // 선언 추가
    val businessNumber = remember { mutableStateOf("") }
    val bossName = remember { mutableStateOf("") }
    val businessOpenDate = remember { mutableStateOf("") }
    val businessName = remember { mutableStateOf("") }
    val businessZipCode = remember { mutableStateOf("") }
    val businessAddress = remember { mutableStateOf("") }
    val businessDetailAddress = remember { mutableStateOf("") }
    val businessAccountNumber = remember { mutableStateOf("") }

    var confirmPassword = remember { mutableStateOf("") }  //비번 확인 추가

    val isBusinessValidated = remember { mutableStateOf(false) } //더이상 입력 x


    val selectedBank = remember { mutableStateOf("") } // 은행 선택 상태 추가
    var showZipDialog by remember { mutableStateOf(false) }
    var selectedAddress by remember { mutableStateOf("") }

// 약관 동의 상태 추가
    val termsAgreed = remember { mutableStateOf(false) }
    val privacyAgreed = remember { mutableStateOf(false) }
    val marketingAgreed = remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    val currentTermsTitle = remember { mutableStateOf("") }
    val currentTermsContent = remember { mutableStateOf("") }

    val onShowTermsDialogChange: (Boolean) -> Unit = { newValue ->
        showTermsDialog = newValue
    }


    val showPersonalZipDialog = remember { mutableStateOf(false) } // 개인 주소 우편번호 검색 다이얼로그 상태

    val showBusinessZipDialog = remember { mutableStateOf(false) } // 사업자 주소 우편번호 검색 다이얼로그 상태


    val termsContent = """
'(주)포엔'은 (이하 '회사'는) 고객님의 개인정보를 중요시하며, "개인정보 보호법 및 관련 법령“을 준수하고 있습니다.
회사는 개인정보처리방침을 통하여 고객님께서 제공하시는 개인정보가 어떠한 용도와 방식으로 이용되고 있으며, 개인정보보호를 위해 어떠한 조치가 취해지고 있는지 알려드립니다.
회사의 개인정보처리방침은 다음과 같은 내용을 담고 있습니다.

(1) 수집하는 개인정보의 항목 및 수집방법
(2) 개인정보의 수집 및 이용목적
(3) 개인정보의 보유 및 이용기간
(4) 개인정보의 처리위탁
(5) 개인정보 파기절차 및 방법
(6) 이용자의 권리와 그 행사방법
(7) 개인정보의 기술적/관리적 보호 대책
(8) 개인정보보호책임자 및 담당자의 연락처

(1) 수집하는 개인정보의 항목 및 수집방법
     - 수집항목: 사용자 구분(구매자, 판매자), 이름, 아이디, 비밀번호, 전화번호, 
        주소, 이메일 주소, 사업자등록번호, 대표자명, 사업장명, 개업일자, 사업장 주소, 
        계좌번호
     - 개인정보 수집 방법: 서비스 이용
        회사는 이용자의 소중한 인권을 침해할 우려가 있는 민감한 정보(인종, 사상 및 신조, 
        정치적성향 이나 범죄기록, 의료정보 등)는 어떠한 경우에도 수집하지 않으며, 만약 
        법령에서 정한 의무에 따라 불가피하게 수집하는 경우 에는 반드시 이용자에게 사전 
        동의를 거치겠습니다.
        다만, 해당 정보는 이용자가 확인한 시점을 기준으로 한 정보이며, 이용자의 개인정보를
        추가 수집하는 경우에는 반드시 사전에 이용자에게 해당 사실을 알리고 동의를 
        거치겠습니다.

(2) 개인정보의 수집 및 이용목적
      회사는 수집한 개인정보를 다음의 목적을 위해 활용합니다.
      - 회원관리
        회원제 서비스 이용 및 제한적 본인 확인제에 따른 본인확인, 개인식별, 불량회원의 부정
        이용방지와 비인가 사용방지, 가입의사 확인, 가입 및 가입횟수 제한, 분쟁 조정을 위한
        기록보존, 불만처리 등 민원처리, 고지사항 전달
      - 마케팅 및 광고에 활용
         신규 서비스(제품) 개발 및 특화 , 이벤트 등 광고성 정보 전달 , 접속 빈도 파악 또는 
         회원의 서비스, 이용에 대한 통계

(3) 개인정보의 보유 및 이용기간
      회사는 이용자가 회원으로서 서비스를 이용하는 동안 이용자의 개인정보를 보유 및 이용
      하며, 이용자가 회원탈퇴를 요청한 경우나 개인정보의 수집 및 이용목적을 달성하거나 
      보유 및 이용기간이 종료한 경우 또는 사업폐지 등의 사유가 발생한 경우 해당 정보를 
      지체 없이 파기합니다. 단, 다음의 정보에 대해서는 아래의 이유로 명시한 기간 동안 보존
      합니다.
      - 계약 또는 청약철회 등에 관한 기록
         보존 이유 : 전자상거래 등에서의 소비자보호에 관한 법률
         보존 기간 : 5년
      - 소비자의 불만 또는 분쟁처리에 관한 기록
     
         보존 이유 : 전자상거래 등에서의 소비자보호에 관한 법률
         보존 기간 : 3년
      - 본인확인에 관한 기록
         보존 이유 : 정보통신 이용촉진 및 정보보호 등에 관한 법률
         보존 기간 : 6개월

(4) 개인정보의 처리위탁 및 제3자 제공
     1) 처리위탁
         ※ 위탁업체가 변경 될 경우, 변경된 내용은 개인정보처리방침을 통해 사전 
             공지하겠습니다.
     2) 제3자 제공
          현재 회사는 이용자의 개인정보를 제3자에게 제공하지 않습니다.
          다만, 법률에 따라 요청이 있을 경우나 이용자의 명시적 동의가 있는 경우에 한해 
          제공될 수 있습니다. 이 경우 제공받는 자, 제공 목적, 항목, 보유 기간 등을 이용자에게
          사전 안내하고 동의를 받습니다.

(5) 개인정보 파기절차 및 방법
      회사는 다른 법률에 따라 개인정보를 보존하여야 하는 경우가 아닌 한, 수집한 이용자의
      개인정보의 수집 및 이용목적이 달성되거나, 이용자가 회원탈퇴를 요청한 경우 지체 없이 
      파기하여 향후 어떠한 용도로도 열람 또는 이용할 수 없도록 처리합니다. 
      단, “3.개인정보의 보유 및 이용기간”과 같은 예외 경우를 두고 있습니다.
      회사의 개인정보 파기절차 및 방법은 다음과 같습니다.
      - 파기절차
         이용자가 회원가입 등을 위해 입력한 정보는 목적이 달성된 후 별도의 DB로 옮겨져
        (종이의 경우 별도의 서류함) 내부 방침 및 기타 관련 법령에 의한 정보보호 사유에 따라
        (3. 보유 및 이용기간 참조)일정 기간 저장된 후 파기됩니다. 동 개인정보는 법률에 의한
        경우가 아니고서는 보유되는 이외의 다른 목적으로 이용되지 않습니다.
     - 파기방법
        전자적 파일형태로 저장된 개인정보는 기록을 재생할 수 없는 기술적 방법을 사용하여 
        삭제합니다.

(6) 이용자의 권리와 그 행사방법
      이용자는 언제든지 등록되어 있는 자신의 개인정보를 조회하고 삭제할 수 있습니다. 
      다만, 그러한 경우 해당 서비스의 일부 또는 전부 이용이 어려울 수 있습니다.
      이용자는 언제든지 개인정보 제공에 관한 동의 철회를 요청할 수 있습니다.
      이용자가 개인정보의 오류에 대한 정정을 요청하신 경우에는 정정을 완료하기 전까지 
      당해 개인정보를 이용 또는 제공하지 않습니다. 
      또한 잘못된 개인정보를 제3자에게 이미 제공한 경우에는 정정 처리결과를 제3자에게 
      지체 없이 통지하여 정정이 이루어지도록 하겠습니다.
      이용자의 가입해지(동의철회)를 위해서는 ’회원탈퇴’를 클릭하여 본인 확인 절차를 거치
      신 후
직접 열람, 정정 또는 탈퇴가 가능합니다.
      혹은 개인정보보호책임자에게 서면, 전화 또는 이메일로 연락하시면 지체 없이 
      조치하겠습니다.
      회사는 이용자 혹은 법정 대리인의 요청에 의해 해지 또는 삭제된 개인정보는 
      "3. 개인정보의 보유 및 이용기간"에 명시된 바에 따라 처리하고 그 외의 용도로 열람 
      또는 이용할 수 없도록
처리하고 있습니다.

(7) 개인정보의 기술적/관리적 보호 대책
      회사는 이용자들의 개인정보가 분실, 도난, 유출, 변조 또는 훼손되지 않도록 안전성을 
      확보하기 위해 다음과 같은 조치를 취하고 있습니다.
      - 개인정보 암호화
         이용자의 비밀번호는 암호화하여 저장하고 있으며, 파일 및 전송데이터를 암호화하여
         혹시 발생할 수 있는 사고 시라도 이용자의 개인정보가 유출되지 않도록 관리되고 
         있습니다.
     - 접속 기록 관리 및 보관
        개인정보처리시스템의 접속 기록을 최소 1년 이상 보관하며, 위ㆍ변조 방지를 위한 
        보안 기술을 적용하고 있습니다.
     - 내부 보안 점검 및 외부 침입 방지
        회사는 개인정보의 안전한 처리를 위해 주기적으로 시스템 설정을 검토하며,
        무단 접근을 방지하기 위해 접근 권한을 엄격히 관리하고 있습니다.
     - 개인정보 접근 제한
        개인정보에 대한 접근 권한은 최소한으로 부여하며, 권한 관리 체계를 통해 무단 접근을
        방지하고 있습니다.
        이용자 본인의 부주의나 인터넷상의 문제로 ID, 비밀번호, 전화번호 등 개인정보가 
        유출되어 발생한 문제에 대해 회사는 일체의 책임을 지지 않습니다.

(8) 개인정보보호책임자 및 담당자의 연락처
      회사는 개인정보에 대한 의견수렴 및 불만처리를 담당하는 개인정보 보호책임자 및 
      담당자를 지정하고 있고, 연락처는 아래와 같습니다.
      - 개인정보관리책임자 성명 : 김우재
      - 직책 : 매니저
      - 전화번호 : 070-7797-8423
      - 이메일 : wjkim@poen.co.kr

      귀하께서는 회사의 서비스를 이용하시며 발생하는 모든 개인정보보호 관련 민원을 
      개인정보관리책임자 혹은 담당부서로 신고하실 수 있습니다. 회사는 이용자들의 
      신고사항에 대해 신속하게 충분한 답변을 드릴 것입니다.
      기타 개인정보침해에 대한 신 고나 상담이 필요하신 경우에는 아래 기관에 문의하시기 
      바랍니다.

      - 개인분쟁조정위원회 ( https://www.kopico.go.kr / 1833-6972 )
      - 정보보호마크인증위원회 ( https://www.eprivacy.or.kr / 02-550-9532~4 )
      - 대검찰청 인터넷범죄수사센터 ( https://www.spo.go.kr / 1301 )
      - 경찰청 사이버테러대응센터 ( https://ecrm.police.go.kr / 02-3150-2659 )
"""







    val nita ="""
    제1조 ( 목 적 )
이 약관은 (주)포엔이 운영하는 BERI-Link 웹사이트(https://beri-link.co.kr/, 이하
"BERI-Link"라 한다)에서 제공하는 인터넷 관련 서비스 및 기타 부대서비스를 이용함에 
있어 (주)포엔과 이용자의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.

제2조 ( 정 의 )
   ① "BERI-Link"란 (주)포엔이 운영하는 가상의 사업장(https://beri-link.co.kr/)을 
        말하며, 아울러 가상의 사업장을 운영하는 사업자의 의미로도 사용합니다.
   ② “회원”이란 “BERI-Link”에 접속하여 이 약관에 동의하고 회원 가입을 완료한 이용자를
        말하며, “판매자”와 “구매자”로 나뉩니다.
   ③ "판매자"란 "BERI-Link"를 통해 주로 보유하고 있는 전기차 배터리 매각 등의 서비스를
        이용하려는 자를 말합니다.
   ④ “구매자”란 “회원” 중 “판매자”가 아닌 자로서, "BERI-Link"를 통해 주로 전기차 
        배터리매입 관련, 전기차 배터리 영업 정보 획득 등의 서비스를 이용하려는 자를 
        말합니다.
 

제3조 ( 서비스의 제공 및 중단 )
   ① "회사"는 다음과 같은 업무를 수행합니다.
        1. 전기차 배터리 중개 서비스
        2. 기타 "회사"가 추가 개발하거나 다른 회사와의 제휴계약 등을 통해 "회원"에게 
             제공하는 일체의 서비스
   ②"서비스"는 연중무휴, 1일 24시간 제공함을 원칙으로 합니다.
   ③ "회사"는 다음 각호에 해당하는 경우 서비스의 제공을 일시적으로 중단할 수 있습니다.
        1. "회사"가 사전에 이용자에게 공지하거나 통지한 경우
        2. 컴퓨터등 정보통신설비의 보수점검 교체 및 고장, 통신의 두절 등의 경우
        3. 이용자의 서비스 이용 폭주 등으로 서비스 제공이 불가능한 경우
        4. 설비 등을 긴급 복구하여야 할 경우
        5. 기간통신사업자가 전기통신서비스를 중지한 경우
        6. 기타 "회사"가 합리적으로 제어할 수 없는 경우 등
   ④"회사"는 무료로 제공되는 서비스의 일부 또는 전부를 회사의 정책 및 운영의 필요에 
       따라 수정, 중단, 변경할 수 있으며, 사전에 “회원”에게 고지합니다. 
       다만, 긴급한 사유가 있는 경우에는 사후 통지할 수 있습니다.

제4조 ( 회원가입 )
   ① 이용자는 "회사"가 정한 가입양식에 따라 회원정보를 기입한 후 이 약관에 동의한다는     
        의사표시를 함으로서 회원가입을 신청합니다.
   ② "회사"는 제1항과 같이 회원으로 가입할 것을 신청한 이용자 중 다음 각호에 해당하지 
        않는 한 회원으로 등록합니다.
        1. 제5조 제1항에 의거 회원탈퇴한 경우 탈퇴일로부터 30일 이내
        2. 가입신청자가 이 약관 제5조 제3항에 의하여 이전에 회원자격을 상실한 적이 있는 
            경우, 다만 제5조 제3항에 의한 회원자격 상실 후 3년이 경과한 자로서 "회사"의 
            회원재가입 승낙을 얻은 경우에는 예외로 한다.
        3. 등록 내용에 허위, 기재누락, 오기가 있는 경우
        4. 회원으로 등록하는 것이 "회사"의 기술상 현저히 지장이 있다고 판단되는 경우
   ③ 회원은 회원가입시 등록한 사항에 변경이 있는 경우, 즉시 전자우편 또는 기타 방법으로 
       "회사"에 대하여 그 변경사항을 알려야 합니다.

제5조 ( 회원 탈퇴 및 자격상실 등 )
   ① 회원은 언제든지 "회사"에 탈퇴를 요청할 수 있으며 "회사"는 즉시 회원탈퇴를 
       처리합니다.
   ② 회원이 다음 각호의 사유에 해당하는 경우, "회사"는 회원자격을 제한 및 정지시킬 수 
       있습니다.
        1. 가입 신청시에 허위 내용을 등록한 경우
        2. 다른 사람의 "회사"이용을 방해하거나 그 정보를 도용하는 등 전자거래질서를 
            위협하는 경우
        3. "회사"를 이용하여 법령과 이 약관이 금지하거나 공서양속에 반하는 행위를 하는 
            경우
   ③ "회사"가 회원 자격을 제한 또는 정지 시킨 후, 동일한 행위가 2회이상 반복되거나 30일
        이내에 그 사유가 시정되지 아니하는 경우 "회사"는 회원자격을 상실시킬 수 있습니다.
   ④ "회사"가 회원자격을 상실시키는 경우에는 회원등록을 말소합니다. 이 경우 회원에게 
        이를
통지하고, 회원등록 말소전에 소명할 기회를 부여합니다.

제6조 ( 회원에 대한 통지 )
   ① "회사"가 회원에 대한 통지를 하는 경우, 회원이 "회사"에 제출한 전자우편 주소로 할 수
        있습니다.
   ② "회사"는 불특정다수 회원에 대한 통지의 경우 1주일이상 "회사"에 게시함으로서 개별 
        통지에 갈음할 수 있습니다.

제7조 ( 개인정보보호 및 개인정보 제공 동의 )
   ① "회사"는 이용자의 정보수집시 구매계약 이행에 필요한 최소한의 정보를 수집합니다. 
        다음 사항을 필수사항으로 하며 그 외 사항은 선택사항으로 합니다.
        수집 항목: 이름, 전자우편주소, 희망ID, 비밀번호, 사용자 구분(구매자, 판매자), 
        생년월일, 연락처 (휴대전화번호), 주소, 사업자등록번호, 대표자 성명, 사업장명, 
        개업 일자, 사업장 주소,
계좌번호
   ② "회사"가 이용자의 개인식별이 가능한 개인정보를 수집한 때에는 반드시 당해 이용자의
        동의를 받습니다.
   ③ 제공된 개인정보는 당해 이용자의 동의없이 목적 외의 이용이나 제3자에게 제공할 수 
       없으며, 이에 대한 모든 책임은 "회사"가 집니다. 다만, 다음의 경우에는 예외로 합니다.
        1. 서비스 제공을 위해 필요한 최소한의 정보(예: 이름, 연락처)를 제휴업체에 제공하는
           경우, 이 경우 “회사”는 제공받는 자, 제공 목적, 제공 항목 등을 사전에 고지하고 
           동의를 받습니다.
        2. 통계작성, 학술연구 또는 시장조사를 위해 특정 개인을 식별할 수 없는 형태로 
            제공하는 경우.
        3. 관계법령에 따라 제공이 요구되는 경우
   ④ "회사"가 제2항과 제3항에 의해 이용자의 동의를 받아야 하는 경우에는 개인정보
        관리책임자의 신원(소속, 성명 및 전화번호 기타 연락처), 정보의 수집목적 및 이용목적,
        제3자에 대한 정보제공 관련사항(제공받는자, 제공목적 및 제공할 정보의 내용)등 
        "개인정보보호법"이 규정한 사항을 미리 명시하거나 고지해야 하며 이용자는 언제든지
        이 동의를 철회할 수 있습니다.
   ⑤ 이용자는 언제든지 "회사"가 가지고 있는 자신의 개인정보에 대해 열람 및 오류정정을 
       요구할 수 있으며 "회사"는 이에 대해 지체없이 필요한 조치를 취할 의무를 집니다. 
       이용자가 오류의 정정을 요구한 경우에는 "회사"는 그 오류를 정정할 때까지 당해 
       개인정보를 이용하지
않습니다.
   ⑥ "회사"는 개인정보 보호를 위하여 관리자를 한정하여 그 수를 최소화하며 은행계좌 등
       을
포함한 이용자의 개인정보의 분실, 도난, 유출, 변조 등으로 인한 이용자의 손해에 
       대하여 모든 책임을 집니다. 
       단, “회사”의 고의 또는 과실이 없는 경우에는 그러하지 않습니다.
   ⑦ "회사" 또는 그로부터 개인정보를 제공받은 제3자는 개인정보의 수집목적 또는 
        제공받은
목적을 달성한 때에는 당해 개인정보를 지체없이 파기합니다.

제7조의2(데이터 사용)
“회사”는 회원이 업로드한 데이터(사진, 진단 결과 등)를 회사의 연구ㆍ개발, 서비스 개선 및 마케팅 목적으로 활용하려는 경우, 해당 사용 목적과 사용 방식을 회원에게 사전 고지하고 
동의를 얻습니다. 
동의는 언제든지 철회할 수 있으며, 철회 시 해당 데이터는 사용되지 않습니다.

제8조(“회사”의 의무 )
   1. “회사”는 법령과 이 약관이 금지하거나 공서양속에 반하는 행위를 하지 않으며, 
      이 약관이
정하는 바에 따라 지속적이고 안정적으로 재화·용역을 제공하는데 최선을 
      다하여야 합니다.
   2. “회사”는 “회원”이 안전하게 인터넷 서비스를 이용할 수 있도록 “회원”의 개인정보
      (신용정보 포함) 보호를 위한 보안 시스템을 갖추어야 합니다.
   3. “회사”는 “회원”이 원하지 않는 영리목적의 광고성 전자우편을 발송하지 않습니다.

제9조(“회원”의 의무)
“회원”은 다음 각 호의 행위를 하여서는 안됩니다.
   1. 서비스의 이용 신청, 정보 변경 기타 서비스의 이용과 관련한 허위 내용의 등록·제공
   2. 타인의 정보 도용
   3. “회사” 또는 제3자가 “BERI-Link”에서 작성, 게시한 정보를 변경·삭제하는 행위
   4. “회사”가 정한 정보 이외의 정보(컴퓨터 프로그램 등)의 송신 또는 게시
   5. “회사” 또는 기타 제3자의 저작권, 상표권, 특허권 등 지적재산권, 프라이버시 기타 
       법령·계약상 권리를 침해하는 행위
   6. “회사” 또는 제3자의 명예를 손상시키거나 업무를 방해하는 행위
   7. 외설적이거나 폭력적인 문언, 화상, 음성 기타 공서양속에 반하는 정보를
      “BERI-Link”에 공개, 게시 또는 작성하는 행위
   8. 법령, 법원의 판결, 결정 혹은 명령 또는 법령상 구속력을 가지는 행정조치에 
       위반되거나
공공질서 또는 미풍양속을 저해할 우려가 있는 행위
   9. 기타 “회사”가 공지한 정책을 위반하는 행위로서, 서비스의 안정성과 이용자의 권리를
       침해할 우려가 있는 행위

제10조(“회원”의 ID·비밀번호에 대한 의무)
   1. ID와 비밀번호의 관리책임은 “회원”에게 있습니다.
   2. “회원”은 자신의 ID 및 비밀번호를 제3자에게 이용하게 해서는 안됩니다.
   3. “회원”이 자신의 ID 및 비밀번호를 도난당하거나 제3자가 사용하고 있음을 인지한 경우
       에는 곧바로 “회사”에 통보하고 “회사”의 안내가 있는 경우에는 그에 따라야 합니다.

제11조(“이용자의 의무)
이용자는 다음 행위를 하여서는 안되며, 이에 대한 법률적인 책임은 이용자에게 있습니다.
   1. 신청 또는 변경시 허위내용의 등록
   2. ”BERI-Link“에 게시된 정보의 변경
   3. "회사"가 정한 정보 이외의 정보(컴퓨터 프로그램 등)를 송신 또는 게시
   4. "회사" 기타 제3자의 저작권 등 지적재산권에 대한 침해
   5. "회사" 기타 제3자의 명예를 손상시키거나 업무를 방해하는 행위
   6. 외설 또는 폭력적인 메시지,화상,음성 기타 공서양속에 반하는 정보를 "회사"에 공개 
       또는 게시하는 행위
   7. 서비스와 관련된 설비의 오동작이나 정보 등의 파괴 및 혼란을 유발시키는 컴퓨터 
      바이러스, 기타 다른 컴퓨터 코드, 파일, 프로그램 자료를 등록 또는 유포하는 행위
   8. “회사” 또는 서비스의 비공개 영역, “회사”의 컴퓨터 시스템에 접근하거나 이를 
       무단으로
변경하거나 혹은 이용하는 행위
   9. ”회사”의 시스템이나 네트워크의 취약점을 검사, 조사 또는 테스트하거나 보안 조치 
       내지 인증 조치를 위반하거나 우회하는 행위
   10. 자동 접속 프로그램을 사용하는 등 정상적인 용법과 다른 방법으로 서비스를 이용하여
     
“회사”의 서버에 부하를 일으켜 “회사”의 정상적인 서비스를 방해하는 행위
   11. ”회사”와 체결한 별도의 계약에 따라 특별히 허용된 경우가 아닌 한, “회사”가 제공한 
       것으로서 현재 이용 가능한 정식 인터페이스 이외의 모든 수단(자동화 여부에 관계없음)
       으로
“회사” 또는 서비스, 
       시스템에 접근하거나 검색하는 행위 또는 그러한 접근이나 검색을 시도하는 행위
   12. “회사” 또는 서비스상에서 오버로딩(overloading), 플러딩(floading) 또는 스패밍
        (spamming)을 실행하거나 폭탄 메일(mail-bombing)을 전송하는 행위 등을 포함하
        여 사용자, 호스트 또는 네트워크의 접근을 방해하거나 중단시키는 행위 혹은 그러한 
        방해나 중단을 시도하는 행위
   13. 다른 회원의 개인정보를 개인정보 주체의 동의없이 수집, 저장, 공개하는 행위

제12조(연결된 웹사이트와의 관계)
“회사”의 “BERI-Link”에서 다른 웹사이트가 하이퍼링크(그 대상에는 문자, 이미지, 영상 등이 포함됩니다) 방식으로 연결된 경우, “회사”는 연결된 웹사이트가 “회원”과 독자적으로 행하는 재화 등의 거래에 대하여 보증 책임을 지지 않습니다.

제13조 ( 저작권의 귀속 및 이용제한 )
   ① "회사"와 하위 "웹사이트"가 하이퍼 링크(예: 하이퍼 링크의 대상에는 문자, 그림 및 
       동화상등이 포함됨)방식 등으로 연결된 경우, 전자를 연결 웹사이트라고 하고 후자를 
       피연결 웹사이트라고 합니다.
   ② 연결 웹사이트는 피연결 웹사이트가 독자적으로 제공하는 재화 용역에 의하여 이용자와
       행하는 거래에 대해서 보증책임을 지지 않는다는 뜻을 연결웹사이트의 사이트에서 
       명시한 경우에는 그 거래에 대한 보증책임을 지지 않습니다.

제14조 ( 분쟁처리 및 분쟁조정 )
   ① "회사"는 이용자가 제기하는 정당한 의견이나 불만을 반영하고 그 피해를 보상처리하기
       위하여 최선을 다합니다.
   ② "회사"는 이용자로부터 제출되는 불만사항 및 의견은 우선적으로 그 사항을 처리합니다.     
       다만, 신속한 처리가 곤란한 경우에는 이용자에게 그 사유와 처리일정을 즉시 통보해 
       드립니다.
   ③ "회사"와 이용자간에 발생한 분쟁은 “전자문서 및 전자거래 기본법” 제32조 및 동 
        시행령 제16조에 의해 설치된 전자거래분쟁조정위원회의 조정을 받을 수 있습니다. 
        다만 이는 이용자의 선택 사항이며, 분쟁 해결을 위해 별도의 법적 절차를 진행할 수 
         있습니다.

제15조 ( 재판권 및 준거법 )
   ① "회사"와 이용자간에 발생한 분쟁에 관한 소송은 민사소송법에 따라 관할 법원에 
       제기합니다.
   ② "회사"와 이용자간에 제기된 소송에는 한국법을 적용합니다.
    """
















    // 입력 검증 함수
// 검증 함수 수정
    val validateInputs = remember {
        {
            when {
                // 아이디 입력 여부
                userId.value.isEmpty() -> {
                    dialogMessage.value = "아이디를 입력해주세요."
                    false
                }

                // 비밀번호 검증
                password.value.isEmpty() -> {
                    dialogMessage.value = "비밀번호를 입력해주세요."
                    false
                }
                password.value.length < 8 -> {
                    dialogMessage.value = "비밀번호는 최소 8자리 이상이어야 합니다."
                    false
                }
                !password.value.matches(Regex(".*[A-Za-z].*")) || !password.value.matches(Regex(".*[0-9].*")) -> {
                    dialogMessage.value = "비밀번호는 영문과 숫자를 포함해야 합니다."
                    false
                }
                confirmPassword.value.isEmpty() || confirmPassword.value != password.value -> {
                    dialogMessage.value = "비밀번호 확인이 일치하지 않습니다."
                    false
                }

                // 이름 입력 여부
                name.value.isEmpty() -> {
                    dialogMessage.value = "이름을 입력해주세요."
                    false
                }

                // 이메일 검증
                email.value.isEmpty() -> {
                    dialogMessage.value = "이메일을 입력해주세요."
                    false
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches() -> {
                    dialogMessage.value = "올바른 이메일 형식을 입력해주세요."
                    false
                }

                // 전화번호 검증
                phoneNumber.value.isEmpty() -> {
                    dialogMessage.value = "전화번호를 입력해주세요."
                    false
                }
                !phoneNumber.value.matches(Regex("^01[0-9]{8,9}$")) -> {
                    dialogMessage.value = "올바른 휴대폰 번호를 입력해주세요. (예: 01012345678)"
                    false
                }

                // 이메일 인증 여부
                emailCode.value.isEmpty() -> {
                    dialogMessage.value = "이메일 인증을 완료해주세요."
                    false
                }

                // 우편번호 검증
                zipCode.value.isEmpty() -> {
                    dialogMessage.value = "우편번호를 입력해주세요."
                    false
                }
                zipCode.value.length != 5 -> {
                    dialogMessage.value = "우편번호는 5자리 숫자로 입력해야 합니다."
                    false
                }

                // 주소 입력 여부
                defaultAddress.value.isEmpty() -> {
                    dialogMessage.value = "기본 주소를 입력해주세요."
                    false
                }

                // 약관 동의 체크
                !(termsAgreed.value && privacyAgreed.value) -> {
                    dialogMessage.value = "약관에 모두 동의해야 회원가입이 가능합니다."
                    false
                }

                // 판매자 전용 검증
                isSeller.value && (businessNumber.value.length != 10 || !businessNumber.value.all { it.isDigit() }) -> {
                    dialogMessage.value = "사업자 등록번호는 10자리 숫자로 입력해야 합니다."
                    false
                }
                isSeller.value && businessNumber.value.isEmpty() -> {
                    dialogMessage.value = "사업자 등록번호를 입력해주세요."
                    false
                }
                isSeller.value && businessName.value.isEmpty() -> {
                    dialogMessage.value = "사업장명을 입력해주세요."
                    false
                }
                isSeller.value && bossName.value.isEmpty() -> {
                    dialogMessage.value = "대표자 성명을 입력해주세요."
                    false
                }
                isSeller.value && businessOpenDate.value.isEmpty() -> {
                    dialogMessage.value = "개업일자를 입력해주세요."
                    false
                }
                isSeller.value && !businessOpenDate.value.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) -> {
                    dialogMessage.value = "개업일자는 YYYY-MM-DD 형식으로 입력해야 합니다."
                    false
                }
                isSeller.value && businessAddress.value.isEmpty() -> {
                    dialogMessage.value = "사업장 주소를 입력해주세요."
                    false
                }
                isSeller.value && selectedBank.value.isEmpty() -> {
                    dialogMessage.value = "거래 은행을 선택해주세요."
                    false
                }
                isSeller.value && !isBusinessValidated.value -> {
                    dialogMessage.value = "사업자 인증을 먼저 완료해주세요."
                    showDialog.value = true
                    false
                }

                isSeller.value && businessAccountNumber.value.isEmpty() -> {
                    dialogMessage.value = "계좌번호를 입력해주세요."
                    false
                }

                isSeller.value && !businessAccountNumber.value.matches(Regex("^[0-9]+$")) -> {
                    dialogMessage.value = "계좌번호는 숫자만 입력해야 합니다."
                    false
                }
                else -> true
            }
        }
    }



    val bankList = listOf(
        "한국씨티은행", "우리은행", "전북은행", "NH농협은행", "하나은행",
        "카카오뱅크", "KB국민은행", "IBK기업은행", "경남은행", "대구은행",
        "부산은행", "케이뱅크", "수협은행", "신한은행", "KDB산업은행",
        "제주은행", "광주은행", "SC제일은행"
    )


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // 상단 헤더 (회원가입 제목)

        Header(text = "회원가입", onBackButtonPressed = {})


        Divider(
            modifier = Modifier
                .fillMaxWidth(),
            color = lightSelector,
            thickness = 2.dp
        )


        // 본문 입력 섹션
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 이름 입력 필드
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "이름", style = labelTextStyle)
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    value = name.value,
                    hint = "홍길동",
                    onValueChange = { name.value = it }
                )
            }

            // 아이디 입력 필드
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "아이디", style = labelTextStyle)
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    hint = "아이디를 입력하세요",
                    value = userId.value,
                    onValueChange = { userId.value = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                CustomBrownButton(
                    onClick = {
                        loginViewModel.checkIdentifierDuplicate(
                            identifier = userId.value,
                            onSuccess = {
                                dialogMessage.value = "사용 가능한 아이디입니다"
                                showDialog.value = true
                            },
                            onError = { errorMessage ->
                                dialogMessage.value = errorMessage
                                showDialog.value = true
                            }
                        )
                    },
                    text = "중복확인"
                )
            }

            // 비밀번호 입력 필드
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "비밀번호", style = labelTextStyle)
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    hint = "비밀번호를 입력하세요",
                    value = password.value,
                    onValueChange = { password.value = it }
                )
            }

            // 비밀번호 확인 필드 (새로 추가)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "비밀번호 확인", style = labelTextStyle)
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    hint = "비밀번호를 다시 입력하세요",
                    value = confirmPassword.value,
                    onValueChange = { confirmPassword.value = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                CustomBrownButton(
                    onClick = {
                        if (password.value == confirmPassword.value) {
                            dialogMessage.value = "비밀번호가 일치합니다"
                            showDialog.value = true
                        } else {
                            dialogMessage.value = "비밀번호가 일치하지 않습니다"
                            showDialog.value = true
                        }
                    },
                    text = "확인"
                )
            }

            // 전화번호 입력 필드
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.width(labelSize), text = "전화번호", style = labelTextStyle)
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    value = phoneNumber.value,
                    onValueChange = {phoneNumber.value = it}
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // 이메일 입력 필드
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "이메일 주소",
                    style = labelTextStyle
                )
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    hint = "test@gmail.com",
                    value = email.value,
                    onValueChange = { email.value = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                CustomBrownButton(
                    onClick = {
                        println("인증하기 버튼 클릭됨")
                        signupViewModel.sendEmailVerificationCode(
                            email = email.value,
                            onSuccess = { expiresAt ->
                                println("이메일 인증 코드 전송 성공")
                                dialogMessage.value = "전송 성공"
                                showDialog.value = true
                            },
                            onError = { errorMessage ->
                                println("이메일 인증 코드 전송 실패: $errorMessage")
                                dialogMessage.value = errorMessage
                                showDialog.value = true
                            }
                        )
                    },
                    text = "인증하기"
                )



           }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "이메일 인증 코드",
                    style = labelTextStyle
                )
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    hint = "인증 코드를 입력하세요",
                    value = emailCode.value,
                    onValueChange = { emailCode.value = it }
                )

                Spacer(modifier = Modifier.width(8.dp))
                CustomBrownButton(
                    onClick = {
                        signupViewModel.verifyEmailCode(
                            email = email.value,
                            code = emailCode.value,
                            onSuccess = {
                                dialogMessage.value = "인증 완료되었습니다"
                                showDialog.value = true
                                println("이메일 인증 성공")
                            },
                            onError = { errorMessage ->
                                dialogMessage.value = errorMessage
                                showDialog.value = true
                                println("이메일 인증 실패: $errorMessage")
                            }
                        )
                    },
                    text = "인증확인"
                )





            }


// 우편번호 입력
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "우편번호",
                    style = labelTextStyle
                )
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    hint = "우편번호 입력",
                    value = zipCode.value,
                    onValueChange = { zipCode.value = it }
                )
                Spacer(modifier = Modifier.width(8.dp))




//ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ


                CustomBrownButton(
                    onClick = {
                        println("우편번호 검색 버튼 클릭됨") // 로그 추가
                        showPersonalZipDialog.value = true  // 개인 주소 검색 다이얼로그 열기
                    },
                    text = "우편번호 검색"
                )
// 기존 dialog 호출 부분 수정
                if (showPersonalZipDialog.value) {
                    DaumPostcodeDialog(
                        onAddressSelected = { addressJson ->
                            println("다음 우편번호 서비스에서 반환된 JSON: $addressJson")
                            try {
                                val jsonElement = kotlinx.serialization.json.Json.parseToJsonElement(addressJson)

                                // 우편번호 파싱 및 설정
                                val zonecode = jsonElement.jsonObject["zonecode"]?.jsonPrimitive?.content ?: ""
                                zipCode.value = zonecode

                                // 기본 주소 파싱 및 설정
                                val address = jsonElement.jsonObject["address"]?.jsonPrimitive?.content ?: ""
                                defaultAddress.value = address

                                // 상세 주소(건물명 등) 파싱 및 설정
                                val extraAddress = jsonElement.jsonObject["extraAddress"]?.jsonPrimitive?.content ?: ""
                                detailAddress.value = extraAddress

                                println("파싱된 주소 정보: 우편번호=$zonecode, 주소=$address, 상세=$extraAddress")
                            } catch (e: Exception) {
                                println("JSON 파싱 에러: $e")
                            }
                            showPersonalZipDialog.value = false  // 다이얼로그 닫기
                        },
                        onDismissRequest = {
                            showPersonalZipDialog.value = false
                        }
                    )
                }

            }


//ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ







// 기본 주소 입력
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "기본 주소",
                    style = labelTextStyle
                )
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    hint = "기본 주소 입력",
                    value = defaultAddress.value,
                    onValueChange = { defaultAddress.value = it }
                )
            }





// 상세 주소 입력
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "상세 주소",
                    style = labelTextStyle
                )
                InfoInputField(
                    modifier = Modifier.width(inputSize),
                    value = detailAddress.value,
                    onValueChange = { detailAddress.value = it }
                )
            }

// 상세 주소 입력 Row (기존 그대로)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),  // 양옆 패딩
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 왼쪽 라벨
                Text(
                    text = "약관 동의",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier
                        .width(60.dp) // 라벨 폭을 고정해 배치 깔끔
                )

                // [전체 동의] 체크박스
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = termsAgreed.value && privacyAgreed.value,
                        onCheckedChange = { checked ->
                            termsAgreed.value = checked
                            privacyAgreed.value = checked
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = primaryColor,
                            uncheckedColor = Color.Gray
                        ),
                        modifier = Modifier.size(16.dp) // 체크박스 크기 축소
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "전체 동의",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // 약관 상세 리스트 박스 (Surface/카드)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 8.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF9F9F9),  // 흰색보다 살짝 밝은 회색톤 배경
                shadowElevation = 0.dp      // 기본 그림자 제거(원하면 늘릴 수 있음)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // 이용약관 동의
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = termsAgreed.value,
                                onCheckedChange = { termsAgreed.value = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = primaryColor,
                                    uncheckedColor = Color.Gray
                                ),
                                modifier = Modifier.size(16.dp)  // 체크박스 크기 축소
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "이용약관 동의",
                                style = TextStyle(
                                    fontSize = 12.sp
                                )
                            )
                        }
                        TextButton(
                            onClick = {
                                currentTermsTitle.value = "이용약관"
                                currentTermsContent.value = termsContent
                                onShowTermsDialogChange(true)
                            },
                            contentPadding = PaddingValues(2.dp) // 버튼 자체 패딩 축소
                        ) {
                            Text(
                                "전문보기",
                                style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                            )
                        }
                    }

                    // 개인정보 수집 · 이용 동의
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = privacyAgreed.value,
                                onCheckedChange = { privacyAgreed.value = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = primaryColor,
                                    uncheckedColor = Color.Gray
                                ),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "개인정보 수집 · 이용 동의",
                                style = TextStyle(
                                    fontSize = 12.sp
                                )
                            )
                        }
                        TextButton(
                            onClick = {
                                currentTermsTitle.value = "개인정보 수집·이용"
                                currentTermsContent.value = nita
                                onShowTermsDialogChange(true)
                            },
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text(
                                "전문보기",
                                style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                            )
                        }
                    }
                }
            }

            // 전문보기 다이얼로그
            if (showTermsDialog) {
                AlertDialog(
                    onDismissRequest = { onShowTermsDialogChange(false) },
                    title = {
                        Text(
                            text = currentTermsTitle.value,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = currentTermsContent.value,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { onShowTermsDialogChange(false) }) {
                            Text("확인", color = primaryColor)
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }







            // 구매자/판매자 선택 (라디오 버튼)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(inputHeight),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.width(labelSize),
                    text = "구매자/판매자\n관리 선택",
                    style = labelTextStyle
                )
                val radioButtonColor = Color(0xFFBAC784)
                RadioButton(
                    modifier = Modifier.size(24.dp),
                    selected = !isSeller.value,
                    onClick = { isSeller.value = false },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = radioButtonColor,
                        unselectedColor = radioButtonColor
                    )
                )
                Text(text = "매물정보(구매자)")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    modifier = Modifier.size(24.dp),
                    selected = isSeller.value,
                    onClick = { isSeller.value = true },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = radioButtonColor,
                        unselectedColor = radioButtonColor
                    )
                )
                Text(text = "진단정보(판매자)")
            }





            // 판매자로 선택된 경우 추가 입력 필드 표시
            if (isSeller.value) {
                // 사업자 등록번호 입력 필드
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "사업자 등록번호",
                        style = labelTextStyle,

                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessNumber.value,
                        onValueChange = { businessNumber.value = it },
                        hint = "-없이 입력",
                        enabled = !isBusinessValidated.value // 수정 불가 처리


                    )
                    Spacer(modifier = Modifier.width(8.dp))


                    CustomBrownButton2(
                        onClick = {
                            signupViewModel.validateBusiness(
                                businessNumber = businessNumber.value,
                                businessRepresentativeName = bossName.value,
                                businessOpenDate = businessOpenDate.value,
                                onSuccess = {
                                    // 성공 처리
                                    dialogMessage.value = "사업자 확인 완료"
                                    showDialog.value = true
                                    isBusinessValidated.value = true

                                },
                                onError = { error ->
                                    // 실패 처리
                                    dialogMessage.value = error
                                    showDialog.value = true
                                }
                            )
                        },
                        text = stringResource(id = R.string.business_button_text)
                    )
                }

                // 대표자 성명
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "대표자 성명",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = bossName.value,
                        onValueChange = { bossName.value = it },
                        enabled = !isBusinessValidated.value // 수정 불가 처리

                    )
                }

                // 사업장명
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "사업장명",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessName.value,
                        onValueChange = { businessName.value = it },
                        enabled = !isBusinessValidated.value // 수정 불가 처리

                    )
                }

                // 개업일자
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "개업일자",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessOpenDate.value,
                        onValueChange = { input ->
                            // 입력값을 포맷팅하여 저장
                            businessOpenDate.value = formatBusinessDate(input)
                        },
                        hint = "YYYY-MM-DD 형식",
                        enabled = !isBusinessValidated.value
                    )
                }
// 사업장 우편번호
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "사업장 우편번호",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessZipCode.value,
                        hint = "주소입력",
                        onValueChange = { businessZipCode.value = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    CustomBrownButton(
                        onClick = {
                            println("우편번호 검색 버튼 클릭됨")
                            showBusinessZipDialog.value = true  // 사업자 주소 검색 다이얼로그 열기
                        },
                        text = "우편번호 검색"
                    )

                    if (showBusinessZipDialog.value) {
                        DaumPostcodeDialog(
                            onAddressSelected = { addressJson ->
                                println("다음 우편번호 서비스에서 반환된 JSON: $addressJson")
                                try {
                                    val jsonElement = kotlinx.serialization.json.Json.parseToJsonElement(addressJson)

                                    // 우편번호 파싱 및 설정
                                    val zonecode = jsonElement.jsonObject["zonecode"]?.jsonPrimitive?.content ?: ""
                                    businessZipCode.value = zonecode

                                    // 기본 주소 파싱 및 설정
                                    val address = jsonElement.jsonObject["address"]?.jsonPrimitive?.content ?: ""
                                    businessAddress.value = address

                                    // 상세 주소(건물명 등) 파싱 및 설정
                                    val extraAddress = jsonElement.jsonObject["extraAddress"]?.jsonPrimitive?.content ?: ""
                                    businessDetailAddress.value = extraAddress

                                    println("파싱된 사업장 주소 정보: 우편번호=$zonecode, 주소=$address, 상세=$extraAddress")
                                } catch (e: Exception) {
                                    println("JSON 파싱 에러: $e")
                                }
                                showBusinessZipDialog.value = false  // 다이얼로그 닫기
                            },
                            onDismissRequest = {
                                showBusinessZipDialog.value = false
                            }
                        )
                    }
                }
                // 사업장 기본주소
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "사업장 주소",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessAddress.value,
                        onValueChange = { businessAddress.value = it }
                    )
                }

                // 사업장 상세주소
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "사업장 상세주소",
                        style = labelTextStyle
                    )
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessDetailAddress.value,
                        onValueChange = { businessDetailAddress.value = it }
                    )
                }

                // 계좌 은행
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.width(labelSize),
                        text = "계좌번호",
                        style = labelTextStyle
                    )
                    SelectorField(
                        label = {},
                        selections = bankList,
                        selected = selectedBank.value,
                        borderColor = primaryLighter,
                        backgroundColor = lightSelector,
                        modifier = Modifier.width(inputSize)
                    ) { newSelection ->
                        selectedBank.value = newSelection
                    }
                }

                // 계좌번호
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(labelSize))
                    InfoInputField(
                        modifier = Modifier.width(inputSize),
                        value = businessAccountNumber.value,
                        onValueChange = { businessAccountNumber.value = it },
                        hint = "계좌번호를 입력해주세요"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            // 회원가입 버튼
            Spacer(modifier = Modifier.size(32.dp))

            Button(
                onClick = {
                    if (validateInputs()){
                    if (isSeller.value) {
                        signupViewModel.sellerSignup(
                            identifier = userId.value,
                            password = password.value,
                            name = name.value,
                            email = email.value,
                            phoneNumber = phoneNumber.value,
                            emailCode = emailCode.value,
                            zipCode = zipCode.value,
                            defaultAddress = defaultAddress.value,
                            detailAddress = detailAddress.value,
                            businessNumber = businessNumber.value,
                            businessRepresentativeName = bossName.value,
                            businessOpenDate = businessOpenDate.value,
                            businessName = businessName.value,
                            businessZipCode = businessZipCode.value,
                            businessDefaultAddress = businessAddress.value,
                            businessDetailAddress = businessDetailAddress.value,
                            businessAccountBank = selectedBank.value,  // 선택된 은행으로 변경
                            businessAccountNumber = businessAccountNumber.value,
                            onSuccess = {
                                dialogMessage.value = "회원가입이 완료되었습니다"
                                showDialog.value = true
                            },
                            onError = { errorMessage ->
                                dialogMessage.value = errorMessage
                                showDialog.value = true
                            }
                        )
                    } else {
                        // 기존 구매자 회원가입 코드 유지
                        signupViewModel.register(
                            identifier = userId.value,
                            password = password.value,
                            name = name.value,
                            email = email.value,
                            phoneNumber = phoneNumber.value,
                            emailCode = emailCode.value,
                            zipCode = zipCode.value,
                            defaultAddress = defaultAddress.value,
                            detailAddress = detailAddress.value,
                            isSeller = isSeller.value,
                            onSuccess = {
                                dialogMessage.value = "회원가입 성공"
                                showDialog.value = true
                            },
                            onError = { errorMessage ->
                                dialogMessage.value = errorMessage
                                showDialog.value = true
                            }
                        )}}else{
                        showDialog.value = true  // 검증 실패시 다이얼로그 표시


                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryLight),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, primaryLight),
                modifier = Modifier
                    .height(36.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "회원가입하기", color = Color.White)
            }









            // 하단 로고 이미지

            Spacer(modifier = Modifier.size(32.dp))

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

    SignUpDialog(
        show = showDialog.value,
        message = dialogMessage.value,
        onDismiss = { showDialog.value = false },
        onConfirm = {
            showDialog.value = false
            if (dialogMessage.value == "회원가입 성공") {
                // LoginScreen으로 이동하며 SignUpScreen까지의 백스택 제거
                navController.navigate(Routes.LoginScreen.route) {
                    popUpTo(Routes.SignUpScreen.route) { inclusive = true }
                }
            }
        }
    )


}
