//package site.freelike.twa
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.widget.*
//import androidx.activity.ComponentActivity
//import com.google.android.gms.wallet.*
//import org.json.JSONArray
//import org.json.JSONObject
//// Thêm import này nếu chưa có
//import com.google.android.gms.wallet.Wallet
//import com.google.android.gms.wallet.WalletConstants
//
//
//class GooglePayActivity : ComponentActivity() {
//    private lateinit var loadPaymentDataLauncher: ActivityResultLauncher<IntentSenderRequest>
//    private lateinit var paymentsClient: PaymentsClient
//    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Tạo layout
//        val layout = LinearLayout(this).apply {
//            orientation = LinearLayout.VERTICAL
//            setPadding(50, 150, 50, 50)
//        }
//
//        val amountInput = EditText(this).apply {
//            hint = "Nhập số tiền (VD: 100000)"
//            inputType = android.text.InputType.TYPE_CLASS_NUMBER
//        }
//
//        val payButton = Button(this).apply {
//            text = "THANH TOÁN BẰNG GOOGLE PAY"
//        }
//
//        layout.addView(amountInput)
//        layout.addView(payButton)
//        setContentView(layout)
//
//        // Dùng môi trường TEST (sandbox)
//        val walletOptions = Wallet.WalletOptions.Builder()
//            .setEnvironment(WalletConstants.ENVIRONMENT_TEST) // đổi từ PRODUCTION sang TEST
//            .build()
//
//        val paymentsClient = Wallet.getPaymentsClient(this, walletOptions)
//
//        payButton.setOnClickListener {
//            val amount = amountInput.text.toString().trim()
//            if (amount.isEmpty()) {
//                Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            try {
//                val paymentDataRequestJson = createPaymentRequest(amount)
//                val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
//
//                // Gọi popup Google Pay
//                val task = paymentsClient.loadPaymentData(request)
//                AutoResolveHelper.resolveTask(task, this, LOAD_PAYMENT_DATA_REQUEST_CODE)
//            } catch (e: Exception) {
//                Toast.makeText(this, "Lỗi tạo thanh toán: ${e.message}", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//    private fun createPaymentRequest(amount: String): JSONObject {
//        val allowedAuthMethods = JSONArray().put("PAN_ONLY").put("CRYPTOGRAM_3DS")
//        val allowedCardNetworks = JSONArray().put("VISA").put("MASTERCARD")
//
//        val cardPaymentMethod = JSONObject()
//            .put("type", "CARD")
//            .put(
//                "parameters", JSONObject()
//                    .put("allowedAuthMethods", allowedAuthMethods)
//                    .put("allowedCardNetworks", allowedCardNetworks)
//                    .put("billingAddressRequired", false)
//            )
//            .put(
//                "tokenizationSpecification", JSONObject()
//                    .put("type", "PAYMENT_GATEWAY")
//                    .put(
//                        "parameters", JSONObject()
//                            .put("gateway", "example") // Demo thôi, không gửi tiền thật
//                            .put("gatewayMerchantId", "exampleMerchantId")
//                    )
//            )
//
//        val transactionInfo = JSONObject()
//            .put("totalPrice", amount)
//            .put("totalPriceStatus", "FINAL")
//            .put("countryCode", "VN")
//            .put("currencyCode", "VND")
//
//        val merchantInfo = JSONObject()
//            .put("merchantName", "LikeTik Demo")
//
//        return JSONObject()
//            .put("apiVersion", 2)
//            .put("apiVersionMinor", 0)
//            .put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod))
//            .put("transactionInfo", transactionInfo)
//            .put("merchantInfo", merchantInfo)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
//            when (resultCode) {
//                Activity.RESULT_OK -> {
//                    Toast.makeText(this, "✅ Thanh toán thành công!", Toast.LENGTH_LONG).show()
//                }
//                Activity.RESULT_CANCELED -> {
//                    Toast.makeText(this, "❌ Người dùng hủy thanh toán", Toast.LENGTH_LONG).show()
//                }
//                AutoResolveHelper.RESULT_ERROR -> {
//                    val status = AutoResolveHelper.getStatusFromIntent(data)
//                    Toast.makeText(this, "⚠️ Lỗi: ${status?.statusMessage}", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }
//}
package site.freelike.twa

import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.wallet.*
import org.json.JSONArray
import org.json.JSONObject

class GooglePayActivity : ComponentActivity() {

    private lateinit var paymentsClient: PaymentsClient
    private lateinit var googlePayLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // UI nhập tiền + nút thanh toán
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 150, 60, 60)
        }

        val amountInput = EditText(this).apply {
            hint = "Nhập số tiền (VD: 100000)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        val payButton = Button(this).apply {
            text = "THANH TOÁN BẰNG GOOGLE PAY"
        }

        layout.addView(amountInput)
        layout.addView(payButton)
        setContentView(layout)

        // Google Pay TEST MODE
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
            .build()

        paymentsClient = Wallet.getPaymentsClient(this, walletOptions)

        // Đăng ký launcher xử lý kết quả Google Pay
        googlePayLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                when (result.resultCode) {
                    Activity.RESULT_OK -> {
                        Toast.makeText(this, "✅ Thanh toán thành công!", Toast.LENGTH_LONG).show()
                    }
                    Activity.RESULT_CANCELED -> {
                        Toast.makeText(this, "❌ Người dùng hủy thanh toán", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(this, "⚠️ Thanh toán thất bại!", Toast.LENGTH_LONG).show()
                    }
                }
            }

        // Xử lý nút
        payButton.setOnClickListener {
            val amount = amountInput.text.toString().trim()
            if (amount.isBlank()) {
                Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startGooglePayFlow(amount)
        }
    }

    private fun startGooglePayFlow(amount: String) {
        try {
            val paymentDataJson = createPaymentRequest(amount)
            val request = PaymentDataRequest.fromJson(paymentDataJson.toString())

            val task = paymentsClient.loadPaymentData(request)

            task.addOnCompleteListener { completedTask ->
                try {
                    // Nếu load thành công → popup sẽ hiện luôn
                    completedTask.getResult(ApiException::class.java)
                } catch (exception: ApiException) {
                    when (exception.statusCode) {
                        CommonStatusCodes.RESOLUTION_REQUIRED -> {
                            // Bắt buộc phải mở popup Google Pay
                            val intent = exception.status.resolution
                            if (intent != null) {
                                val senderRequest =
                                    IntentSenderRequest.Builder(intent).build()
                                googlePayLauncher.launch(senderRequest)
                            }
                        }
                        CommonStatusCodes.CANCELED -> {
                            Toast.makeText(this, "❌ Người dùng hủy thanh toán", Toast.LENGTH_SHORT)
                                .show()
                        }
                        else -> {
                            Toast.makeText(
                                this,
                                "⚠️ Lỗi: ${exception.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi tạo payment request: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // JSON cấu hình cho Google Pay
    private fun createPaymentRequest(amount: String): JSONObject {
        val allowedAuthMethods = JSONArray().put("PAN_ONLY").put("CRYPTOGRAM_3DS")
        val allowedCardNetworks = JSONArray().put("VISA").put("MASTERCARD")

        val cardPaymentMethod = JSONObject()
            .put("type", "CARD")
            .put(
                "parameters", JSONObject()
                    .put("allowedAuthMethods", allowedAuthMethods)
                    .put("allowedCardNetworks", allowedCardNetworks)
            )
            .put(
                "tokenizationSpecification", JSONObject()
                    .put("type", "PAYMENT_GATEWAY")
                    .put(
                        "parameters", JSONObject()
                            .put("gateway", "example") // DEMO — không thật
                            .put("gatewayMerchantId", "exampleMerchantId")
                    )
            )

        val transactionInfo = JSONObject()
            .put("totalPrice", amount)
            .put("totalPriceStatus", "FINAL")
            .put("countryCode", "VN")
            .put("currencyCode", "VND")

        val merchantInfo = JSONObject()
            .put("merchantName", "LikeTik Demo")

        return JSONObject()
            .put("apiVersion", 2)
            .put("apiVersionMinor", 0)
            .put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod))
            .put("transactionInfo", transactionInfo)
            .put("merchantInfo", merchantInfo)
    }
}


