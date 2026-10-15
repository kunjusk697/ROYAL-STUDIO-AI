package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.model.CartItem
import com.example.model.FrameColor
import com.example.model.LensType
import com.example.model.PaymentMethod
import com.example.model.PrescriptionMethod
import com.example.model.Product
import java.net.URLEncoder

object WhatsAppHelper {

    const val STORE_WHATSAPP_NUMBER = "919946732958"
    const val STORE_DISPLAY_PHONE = "+91 99467 32958"
    const val STORE_NAME = "Royal Specs"
    const val STORE_UPI_ID = "9946732958@upi"
    const val STORE_UPI_NAME = "ROYAL SPECS OPTICALS"

    fun buildOrderMessage(
        orderId: String,
        customerName: String,
        phone: String,
        address: String,
        pincode: String,
        items: List<CartItem>,
        framesSubtotal: Int,
        lensSubtotal: Int,
        taxes: Int,
        discount: Int,
        deliveryFee: Int,
        total: Int,
        paymentMethod: PaymentMethod,
        upiTransactionRef: String?,
        couponCode: String?
    ): String {
        val sb = StringBuilder()
        sb.append("👑 *ROYAL SPECS — NEW ORDER* 👓\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("📋 *Order ID:* #$orderId\n")
        sb.append("💳 *Payment Mode:* ${paymentMethod.title}\n")
        if (!upiTransactionRef.isNullOrBlank()) {
            sb.append("🔖 *UPI Ref/UTR:* $upiTransactionRef\n")
        }
        sb.append("\n")

        sb.append("👤 *SHIPPING & CUSTOMER DETAILS*\n")
        sb.append("• Name: $customerName\n")
        sb.append("• Contact Phone: $phone\n")
        sb.append("• Delivery Address: $address\n")
        sb.append("• Pincode: $pincode\n\n")

        sb.append("🛍️ *SELECTED EYEWEAR & LENSES (${items.sumOf { it.quantity }} items)*\n")
        items.forEachIndexed { index, item ->
            val p = item.product
            val lc = item.lensConfig
            sb.append("────────────────────\n")
            sb.append("${index + 1}. *${p.name}* (${p.category.displayName})\n")
            sb.append("   • Frame Color: ${item.selectedColor.name}\n")
            sb.append("   • Frame Price: ₹${p.basePrice}\n")
            sb.append("   • Selected Lens Type: *${lc.lensType.title}* (+₹${lc.lensType.priceInr})\n")
            if (lc.lensIndex.priceInr > 0) {
                sb.append("   • Lens Thickness: ${lc.lensIndex.title} (+₹${lc.lensIndex.priceInr})\n")
            }
            if (lc.selectedCoatings.isNotEmpty()) {
                val coatingNames = lc.selectedCoatings.joinToString(", ") { "${it.title} (+₹${it.priceInr})" }
                sb.append("   • Premium Coatings: $coatingNames\n")
            }

            if (lc.prescriptionMethod == PrescriptionMethod.WHATSAPP_PHOTO) {
                sb.append("   • Prescription: 📸 *Will share prescription photo in this chat*\n")
            } else {
                val od = lc.prescriptionDetails.rightEyeOD
                val os = lc.prescriptionDetails.leftEyeOS
                sb.append("   • *Prescription (Manual):*\n")
                sb.append("     - OD (Right): SPH: ${od.sph} | CYL: ${od.cyl} | AXIS: ${od.axis}°\n")
                sb.append("     - OS (Left): SPH: ${os.sph} | CYL: ${os.cyl} | AXIS: ${os.axis}°\n")
                sb.append("     - Pupillary Distance (PD): ${lc.prescriptionDetails.pupillaryDistanceMm} mm\n")
            }

            sb.append("   • Qty: ${item.quantity} (Frame ₹${item.frameTotalPrice} + Lens ₹${item.lensTotalPrice}) = *₹${item.totalPrice}*\n")
        }

        sb.append("────────────────────\n")
        sb.append("💰 *TRANSPARENT PRICE BREAKDOWN*\n")
        sb.append("• Frames Subtotal: ₹$framesSubtotal\n")
        sb.append("• Lens Customization: ₹$lensSubtotal\n")
        sb.append("• GST / Taxes (12%): ₹$taxes\n")
        if (discount > 0) {
            val couponStr = if (!couponCode.isNullOrBlank()) " ($couponCode)" else ""
            sb.append("• Privilege Discount$couponStr: -₹$discount\n")
        }
        sb.append("• Shipping: ${if (deliveryFee == 0) "FREE Delivery (Royal Express)" else "₹$deliveryFee"}\n")
        sb.append("• *FINAL AMOUNT PAYABLE: ₹$total*\n\n")

        when (paymentMethod) {
            PaymentMethod.UPI, PaymentMethod.UPI_QR -> {
                sb.append("📲 *Payment Status:* Paid via UPI ($STORE_UPI_ID)\n")
            }
            PaymentMethod.CASH_ON_DELIVERY -> {
                sb.append("💵 *Payment Status:* Cash on Delivery (Pay ₹$total in cash on arrival)\n")
            }
            PaymentMethod.ONLINE_CARD_NETBANKING -> {
                sb.append("💳 *Payment Status:* Paid Online (Card / NetBanking)\n")
            }
            PaymentMethod.WHATSAPP_DIRECT -> {
                sb.append("💬 *Payment Status:* Please share payment link or confirm dispatch on WhatsApp\n")
            }
        }

        sb.append("\n_Hello Royal Specs team, please verify my prescription details and process this order. Thank you!_")

        return sb.toString()
    }

    fun openWhatsAppChat(
        context: Context,
        message: String = "Hello Royal Specs, I would like to inquire about your eyeglasses collection and lens options."
    ) {
        val encodedMessage = try {
            URLEncoder.encode(message, "UTF-8")
        } catch (e: Exception) {
            message
        }
        val url = "https://api.whatsapp.com/send?phone=$STORE_WHATSAPP_NUMBER&text=$encodedMessage"

        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                setPackage("com.whatsapp")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e1: Exception) {
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
            } catch (e2: Exception) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Royal Specs Order", message)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(
                    context,
                    "Order text copied to clipboard! Please open WhatsApp and send to $STORE_DISPLAY_PHONE",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Formats a rich WhatsApp share message for a product including:
     * - Product Name
     * - Selected Lens Type and pricing
     * - Frame color and specifications
     * - Direct link to the item
     */
    fun buildProductShareMessage(
        product: Product,
        selectedLensType: LensType,
        selectedColor: FrameColor? = null,
        directItemLink: String = "https://optics.royol.store/product?id=${product.id}"
    ): String {
        val totalEstimate = product.basePrice + selectedLensType.priceInr
        val sb = StringBuilder()
        sb.append("👓 *Check out this frame on Royal Specs Opticals!* ✨\n\n")
        sb.append("🏷️ *Product Name:* ${product.name}\n")
        sb.append("🔍 *Selected Lens Type:* ${selectedLensType.title}")
        if (selectedLensType.priceInr > 0) {
            sb.append(" (+₹${selectedLensType.priceInr})")
        }
        sb.append("\n")
        if (selectedColor != null) {
            sb.append("🎨 *Frame Color:* ${selectedColor.name}\n")
        }
        sb.append("💰 *Price:* ₹$totalEstimate (Frame ₹${product.basePrice}")
        if (selectedLensType.priceInr > 0) {
            sb.append(" + Lens ₹${selectedLensType.priceInr}")
        }
        sb.append(")\n")
        sb.append("📐 *Dimensions:* ${product.dimensions}\n")
        sb.append("✨ *Material:* ${product.frameMaterial}\n\n")
        sb.append("🔗 *Direct Link to Item:*\n$directItemLink\n\n")
        sb.append("💬 Order or inquire directly on WhatsApp: $STORE_DISPLAY_PHONE\n")
        sb.append("_ROYOL OPTICALS • Handcrafted Luxury Eyewear & Screen Protection_")
        return sb.toString()
    }

    /**
     * Shares formatted text via WhatsApp.
     * Tries:
     * 1. Direct Intent with package "com.whatsapp"
     * 2. WhatsApp universal web/app URI api.whatsapp.com/send?text=...
     * 3. System share chooser
     * 4. Copies to clipboard with helpful toast
     */
    fun shareViaWhatsApp(
        context: Context,
        message: String,
        sendToConciergeOnly: Boolean = false
    ) {
        if (sendToConciergeOnly) {
            openWhatsAppChat(context, message)
            return
        }

        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                setPackage("com.whatsapp")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(sendIntent)
        } catch (e1: Exception) {
            val encodedMessage = try {
                URLEncoder.encode(message, "UTF-8")
            } catch (e: Exception) {
                message
            }
            val url = "https://api.whatsapp.com/send?text=$encodedMessage"
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
            } catch (e2: Exception) {
                try {
                    val chooserIntent = Intent.createChooser(
                        Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, message)
                        },
                        "Share Frame via WhatsApp"
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(chooserIntent)
                } catch (e3: Exception) {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Eyewear Details", message)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(
                        context,
                        "Product details & link copied to clipboard!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

