package database_creation.QR

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.tavrida.energysales.data_access.models.Organization
import com.tavrida.energysales.data_access.models.Counter
import com.tavrida.energysales.data_access.models.DataContext
import com.tavrida.energysales.data_access.models.transaction
import database_creation.DbInstance
import database_creation.utils.println
import saveQrImage

fun main() {
    // return
    val dc = DbInstance("./databases/", "ENERGY_SALES_MOBILE")
        .get(recreate = false)
        .let { DataContext(it) }

    val organizations = transaction(dc) {
        dc.selectAllOrganizations()
    }
    val counters = organizations.flatMap { it.counters }.sortedBy { it.importOrder }

    val size = 256
    val qrCodeWriter = QRCodeWriter()
    counters.forEach {
        val bits = qrCodeWriter.encode(it, size)
        saveQrImage(bits, "html/${it.serialNumber}.png")
    }

    counters.forEach { counter ->
        val organization = organizations.first { org -> org.id == counter.organizationId }
        toHtmlSticker(counter, organization).println()
    }

}

private fun toHtmlSticker(counter: Counter, organization: Organization): String {
return "<div class=\"sticker\">" +
        "<div class=\"organization\">" +
        organization.name +
        "</div>" +
        "<img class=\"qr\" src=\"qr/${counter.serialNumber}.png\" alt=\"123\"/>" +
        "<div class=\"id\">" +
        "${counter.serialNumber} (${counter.importOrder})" +
        "</div>" +
        "</div>"
}
/*
<div class="sticker">   
        <div class="organization">
            Organization Organization Organization
        </div>
        <img class="qr" src="qr/00227.png" alt="123"/>
        <div class="id">
            00227 (189)
        </div>
    </div>
*/


private const val COUNTER_PREFIX = "1:"
private val hints = mapOf(EncodeHintType.MARGIN to 2, EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.Q)
private fun QRCodeWriter.encode(counter: Counter, size: Int) =
    encode("$COUNTER_PREFIX${counter.serialNumber}", BarcodeFormat.QR_CODE, size, size, hints)


