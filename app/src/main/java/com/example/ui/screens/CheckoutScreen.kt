package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CartItem
import com.example.model.Order
import com.example.model.PaymentMethod
import com.example.model.PrescriptionMethod
import com.example.ui.components.EyewearIllustration
import com.example.ui.components.UpiQrDialog
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.ForestGreenSurface
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.RedSale
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SoftGoldContainer
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmGoldDark
import com.example.util.WhatsAppHelper

@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    framesSubtotal: Int,
    lensSubtotal: Int,
    appliedCoupon: String?,
    onBackToCart: () -> Unit,
    onPlaceOrder: (String, String, String, String, PaymentMethod, String?) -> Triple<Order, String, Int>,
    onOrderFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBackToCart() }

    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var addressLine by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var deliveryInstructions by remember { mutableStateOf("") }

    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.UPI_QR) }
    var showUpiQrModal by remember { mutableStateOf(false) }
    var upiTransactionRef by remember { mutableStateOf<String?>(null) }

    var showOrderSuccessDialog by remember { mutableStateOf(false) }
    var completedOrder by remember { mutableStateOf<Order?>(null) }

    val rawSubtotal = framesSubtotal + lensSubtotal
    val discount = when (appliedCoupon) {
        "ROYAL10" -> (rawSubtotal * 0.10).toInt()
        "FIRSTSPECS" -> minOf(300, rawSubtotal)
        "GOLDVIP" -> minOf(500, rawSubtotal)
        else -> 0
    }
    val discountedSubtotal = (rawSubtotal - discount).coerceAtLeast(0)
    val taxes = (discountedSubtotal * 0.12).toInt() // 12% GST
    val deliveryFee = if (rawSubtotal >= 999 || rawSubtotal == 0) 0 else 99
    val finalGrandTotal = maxOf(0, discountedSubtotal + taxes + deliveryFee)

    val tempOrderId = remember { "RS" + (1000 + (Math.random() * 9000).toInt()) }

    // Instant UPI QR Modal Prompt
    if (showUpiQrModal) {
        UpiQrDialog(
            totalAmount = finalGrandTotal,
            orderId = tempOrderId,
            onDismiss = { showUpiQrModal = false },
            onPaymentConfirmed = { ref ->
                upiTransactionRef = ref
                showUpiQrModal = false
                // Submit order with UPI confirmation
                val (order, message, _) = onPlaceOrder(
                    fullName,
                    phoneNumber,
                    "$addressLine, $city",
                    pincode,
                    PaymentMethod.UPI_QR,
                    ref
                )
                completedOrder = order
                showOrderSuccessDialog = true
                WhatsAppHelper.openWhatsAppChat(context, message)
            }
        )
    }

    // Success confirmation dialog
    if (showOrderSuccessDialog && completedOrder != null) {
        AlertDialog(
            onDismissRequest = {
                showOrderSuccessDialog = false
                onOrderFinished()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF25D366),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Order Placed Successfully!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = ForestGreenPrimary
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Order ID: #${completedOrder?.orderId}",
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenDark,
                        fontSize = 14.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Payment Mode: ${completedOrder?.paymentMethod?.title}",
                        fontWeight = FontWeight.SemiBold,
                        color = CharcoalText,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Total Amount: ₹${completedOrder?.totalAmount} (Taxes & Shipping included)",
                        fontWeight = FontWeight.ExtraBold,
                        color = ForestGreenPrimary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your order summary has been transmitted to Royal Specs WhatsApp (+91 99467 32958). Our opticians will verify your prescription and begin lens crafting.",
                        fontSize = 12.sp,
                        color = SlateMuted,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showOrderSuccessDialog = false
                        onOrderFinished()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                ) {
                    Text("Return to Store")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .testTag("checkout_screen")
    ) {
        // Checkout Header
        Surface(
            color = ForestGreenPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToCart, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to Bag",
                        tint = CleanWhite
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "SECURE CHECKOUT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarmGold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Shipping & Payment",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = CleanWhite
                    )
                }
            }
        }

        // Checkout Body
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Shipping Address Form
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(ForestGreenSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    tint = ForestGreenPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "1. Delivery & Shipping Address",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name *", fontSize = 12.sp) },
                            placeholder = { Text("e.g. Arjun Menon") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_name_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Mobile Phone (+91 WhatsApp) *", fontSize = 12.sp) },
                            placeholder = { Text("e.g. 98451 22390") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_phone_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = addressLine,
                            onValueChange = { addressLine = it },
                            label = { Text("House/Flat No., Building & Street Address *", fontSize = 12.sp) },
                            placeholder = { Text("e.g. 402, Palm Heights, MG Road") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_address_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City *", fontSize = 12.sp) },
                                placeholder = { Text("e.g. Kochi / Bengaluru") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("checkout_city_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = pincode,
                                onValueChange = { pincode = it },
                                label = { Text("Pincode (6-digits) *", fontSize = 12.sp) },
                                placeholder = { Text("e.g. 682016") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("checkout_pincode_input"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = deliveryInstructions,
                            onValueChange = { deliveryInstructions = it },
                            label = { Text("Delivery Landmark / Notes (Optional)", fontSize = 12.sp) },
                            placeholder = { Text("e.g. Near Metro Pillar 124, Call before delivery") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Section 2: Order Items & Selected Lens Review
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "2. Eyewear & Lens Summary (${cartItems.sumOf { it.quantity }})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                            Text(
                                text = "Edit in Bag",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenPrimary,
                                modifier = Modifier.clickable { onBackToCart() }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        cartItems.forEachIndexed { idx, item ->
                            if (idx > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderLight)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                EyewearIllustration(
                                    shape = item.product.shape,
                                    category = item.product.category,
                                    frameColor = Color(item.selectedColor.colorHex),
                                    size = 46.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.quantity}x ${item.product.name} (${item.selectedColor.name})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = CharcoalText
                                    )
                                    Text(
                                        text = "Frame: ₹${item.frameUnitPrice} • Lens: ${item.lensConfig.lensType.title} (+₹${item.lensConfig.totalLensPrice})",
                                        fontSize = 11.sp,
                                        color = ForestGreenPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = if (item.lensConfig.prescriptionMethod == PrescriptionMethod.WHATSAPP_PHOTO) {
                                            "📸 Prescription photo via WhatsApp"
                                        } else {
                                            "Prescription manual entry"
                                        },
                                        fontSize = 10.5.sp,
                                        color = SlateMuted
                                    )
                                }
                                Text(
                                    text = "₹${item.totalPrice}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.5.sp,
                                    color = ForestGreenDark
                                )
                            }
                        }
                    }
                }
            }

            // Section 3: Transparent Price Breakdown
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "3. Transparent Price Breakdown",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CheckoutPriceLine("Frames Subtotal", "₹$framesSubtotal")
                        CheckoutPriceLine("Lens Type & Coatings", "+₹$lensSubtotal")
                        CheckoutPriceLine("Taxes (GST 12% on Opticals)", "+₹$taxes")

                        if (discount > 0) {
                            CheckoutPriceLine("Coupon Privilege Discount", "-₹$discount", isDiscount = true)
                        }

                        CheckoutPriceLine(
                            "Express Insured Shipping",
                            if (deliveryFee == 0) "FREE" else "+₹$deliveryFee",
                            isHighlight = deliveryFee == 0
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BorderLight)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Payable Amount",
                                    fontSize = 12.sp,
                                    color = SlateMuted
                                )
                                Text(
                                    text = "₹$finalGrandTotal",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ForestGreenPrimary
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SoftGoldContainer
                            ) {
                                Text(
                                    text = "All Taxes & Shipping Included",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarmGoldDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Section 4: Payment Options
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "4. Select Payment Option",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Option 1: Instant UPI QR Modal Prompt
                        PaymentOptionRow(
                            title = "Instant UPI QR Code",
                            subtitle = "Scan & pay with GPay, PhonePe, Paytm or BHIM",
                            badge = "Instant",
                            badgeColor = WarmGold,
                            icon = Icons.Default.QrCode2,
                            isSelected = selectedPaymentMethod == PaymentMethod.UPI_QR,
                            onClick = { selectedPaymentMethod = PaymentMethod.UPI_QR }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Option 2: Cash on Delivery (COD)
                        PaymentOptionRow(
                            title = "Cash on Delivery (COD)",
                            subtitle = "Pay cash or UPI upon home delivery",
                            badge = "Doorstep",
                            badgeColor = ForestGreenSurface,
                            icon = Icons.Default.Money,
                            isSelected = selectedPaymentMethod == PaymentMethod.CASH_ON_DELIVERY,
                            onClick = { selectedPaymentMethod = PaymentMethod.CASH_ON_DELIVERY }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Option 3: Direct WhatsApp Order
                        PaymentOptionRow(
                            title = "Order via WhatsApp",
                            subtitle = "Direct concierge ordering & WhatsApp pay at +91 99467 32958",
                            badge = "Official",
                            badgeColor = Color(0xFF25D366),
                            icon = Icons.Default.Chat,
                            isSelected = selectedPaymentMethod == PaymentMethod.WHATSAPP_DIRECT,
                            onClick = { selectedPaymentMethod = PaymentMethod.WHATSAPP_DIRECT }
                        )
                    }
                }
            }

            // Final Action CTA
            item {
                when (selectedPaymentMethod) {
                    PaymentMethod.UPI_QR -> {
                        Button(
                            onClick = {
                                if (fullName.isBlank() || phoneNumber.isBlank() || addressLine.isBlank() || pincode.isBlank()) {
                                    Toast.makeText(context, "Please complete your Shipping Address first", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                showUpiQrModal = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("pay_with_upi_qr_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreenPrimary,
                                contentColor = CleanWhite
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = WarmGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Open UPI QR & Pay ₹$finalGrandTotal",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    PaymentMethod.UPI -> {
                        Button(
                            onClick = {
                                if (fullName.isBlank() || phoneNumber.isBlank() || addressLine.isBlank() || pincode.isBlank()) {
                                    Toast.makeText(context, "Please complete your Shipping Address first", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                // Placeholder for UPI integration
                                Toast.makeText(context, "UPI Payment not yet implemented", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreenPrimary,
                                contentColor = CleanWhite
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Pay ₹$finalGrandTotal via UPI")
                        }
                    }

                    PaymentMethod.ONLINE_CARD_NETBANKING -> {
                        Button(
                            onClick = {
                                if (fullName.isBlank() || phoneNumber.isBlank() || addressLine.isBlank() || pincode.isBlank()) {
                                    Toast.makeText(context, "Please complete your Shipping Address first", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                // Placeholder for Card/Netbanking integration
                                Toast.makeText(context, "Online Payment not yet implemented", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreenPrimary,
                                contentColor = CleanWhite
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Pay ₹$finalGrandTotal via Card/NetBanking")
                        }
                    }

                    PaymentMethod.CASH_ON_DELIVERY -> {
                        Button(
                            onClick = {
                                if (fullName.isBlank() || phoneNumber.isBlank() || addressLine.isBlank() || pincode.isBlank()) {
                                    Toast.makeText(context, "Please complete your Shipping Address first", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val fullAddress = "$addressLine, $city, PIN: $pincode${if (deliveryInstructions.isNotBlank()) " (Note: $deliveryInstructions)" else ""}"
                                val (order, message, _) = onPlaceOrder(
                                    fullName,
                                    phoneNumber,
                                    fullAddress,
                                    pincode,
                                    PaymentMethod.CASH_ON_DELIVERY,
                                    null
                                )
                                completedOrder = order
                                showOrderSuccessDialog = true
                                WhatsAppHelper.openWhatsAppChat(context, message)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("place_order_cod_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForestGreenPrimary,
                                contentColor = CleanWhite
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Money,
                                contentDescription = null,
                                tint = WarmGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Confirm Cash on Delivery (₹$finalGrandTotal)",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    PaymentMethod.WHATSAPP_DIRECT -> {
                        Button(
                            onClick = {
                                if (fullName.isBlank() || phoneNumber.isBlank() || addressLine.isBlank() || pincode.isBlank()) {
                                    Toast.makeText(context, "Please complete your Shipping Address first", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val fullAddress = "$addressLine, $city, PIN: $pincode${if (deliveryInstructions.isNotBlank()) " (Note: $deliveryInstructions)" else ""}"
                                val (order, message, _) = onPlaceOrder(
                                    fullName,
                                    phoneNumber,
                                    fullAddress,
                                    pincode,
                                    PaymentMethod.WHATSAPP_DIRECT,
                                    null
                                )
                                completedOrder = order
                                showOrderSuccessDialog = true
                                WhatsAppHelper.openWhatsAppChat(context, message)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("checkout_order_via_whatsapp_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25D366),
                                contentColor = CleanWhite
                            ),
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Order via WhatsApp (+91 99467 32958)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentOptionRow(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) ForestGreenPrimary else BorderLight
        ),
        color = if (isSelected) SoftGoldContainer.copy(alpha = 0.4f) else CleanWhite
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = ForestGreenPrimary)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ForestGreenSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ForestGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = badgeColor
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (badgeColor == Color(0xFF25D366)) CleanWhite else ForestGreenDark,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = SlateMuted
                )
            }
        }
    }
}

@Composable
private fun CheckoutPriceLine(
    label: String,
    value: String,
    isDiscount: Boolean = false,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = SlateMuted)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = when {
                isDiscount -> RedSale
                isHighlight -> ForestGreenPrimary
                else -> CharcoalText
            }
        )
    }
}
