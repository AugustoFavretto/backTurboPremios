package com.turbopremios.integrations.pix

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.turbopremios.config.PixProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Base64
import java.util.UUID

@Component
@ConditionalOnProperty(name = ["app.pix.gateway"], havingValue = "mock", matchIfMissing = true)
class MockPaymentGateway(private val pixProperties: PixProperties) : PaymentGateway {

    private val log = LoggerFactory.getLogger(MockPaymentGateway::class.java)

    override fun generatePix(request: PixPaymentRequest): PixPaymentResult {
        log.info("MockPaymentGateway: generating PIX for purchase {} amount {}", request.purchaseId, request.amount)

        val pixCode = buildEmvPixCode(
            pixKey = pixProperties.pixKey,
            merchantName = pixProperties.merchantName,
            merchantCity = pixProperties.merchantCity,
            amount = request.amount,
            txId = request.purchaseId.take(25).replace("-", "")
        )
        val qrCodeBase64 = generateQrCode(pixCode)

        return PixPaymentResult(
            pixCode = pixCode,
            pixQrCodeBase64 = "data:image/png;base64,$qrCodeBase64",
            expiresAt = LocalDateTime.now().plusMinutes(30),
            gatewayPaymentId = UUID.randomUUID().toString()
        )
    }

    override fun confirmPayment(gatewayPaymentId: String): Boolean {
        log.info("MockPaymentGateway: confirming payment for {}", gatewayPaymentId)
        return true
    }

    private fun buildEmvPixCode(
        pixKey: String,
        merchantName: String,
        merchantCity: String,
        amount: BigDecimal,
        txId: String
    ): String {
        val merchantAccountInfo = buildTlv("00", "BR.GOV.BCB.PIX") + buildTlv("01", pixKey)
        val merchantAccountInfoField = buildTlv("26", merchantAccountInfo)
        val additionalDataField = buildTlv("05", txId.take(25))
        val additionalDataFieldWrapped = buildTlv("62", additionalDataField)

        val amountStr = "%.2f".format(amount)
        val merchantNameTrimmed = merchantName.take(25)
        val merchantCityTrimmed = merchantCity.take(15)

        val payload = "000201" +
            "010212" +
            merchantAccountInfoField +
            "52040000" +
            "5303986" +
            "54${amountStr.length.toString().padStart(2, '0')}$amountStr" +
            "5802BR" +
            "59${merchantNameTrimmed.length.toString().padStart(2, '0')}$merchantNameTrimmed" +
            "60${merchantCityTrimmed.length.toString().padStart(2, '0')}$merchantCityTrimmed" +
            additionalDataFieldWrapped +
            "6304"

        val crc = crc16(payload)
        return payload + crc
    }

    private fun buildTlv(tag: String, value: String): String {
        val length = value.length.toString().padStart(2, '0')
        return "$tag$length$value"
    }

    private fun crc16(data: String): String {
        var crc = 0xFFFF
        for (char in data) {
            crc = crc xor (char.code shl 8)
            repeat(8) {
                crc = if ((crc and 0x8000) != 0) (crc shl 1) xor 0x1021 else crc shl 1
                crc = crc and 0xFFFF
            }
        }
        return crc.toString(16).uppercase().padStart(4, '0')
    }

    private fun generateQrCode(content: String): String {
        val hints = mapOf(EncodeHintType.MARGIN to 1)
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300, hints)
        val outputStream = ByteArrayOutputStream()
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream)
        return Base64.getEncoder().encodeToString(outputStream.toByteArray())
    }
}
