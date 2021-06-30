import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main() {
    val size = 128
    val bits = QRCodeWriter().encode(
        "1:12345678", BarcodeFormat.QR_CODE, size, size,
        mapOf(EncodeHintType.MARGIN to 2, EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.Q)
    )

    saveQrImage(bits, "qr_binary.png")
}

fun saveQrImage(bits: BitMatrix, path: String) {
    val img = BufferedImage(bits.width, bits.height, BufferedImage.TYPE_BYTE_BINARY)
    val bitToColor = mapOf(false to 0xFFFFFF, true to 0)
    for (x in 0 until bits.width) {
        for (y in 0 until bits.height) {
            val b = bits[x, y]
            img.setRGB(x, y, bitToColor[b]!!)
        }
    }
    ImageIO.write(img, "PNG", File(path))
}