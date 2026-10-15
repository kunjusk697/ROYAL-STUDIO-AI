package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CartItem
import com.example.model.PrescriptionMethod
import com.example.ui.components.EyewearIllustration
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

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    appliedCoupon: String?,
    framesSubtotal: Int,
    lensSubtotal: Int,
    onQuantityChange: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onApplyCoupon: (String) -> Pair<Boolean, String>,
    onRemoveCoupon: () -> Unit,
    onProceedToCheckout: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var couponInput by remember { mutableStateOf("") }
    var couponMessage by remember { mutableStateOf<String?>(null) }
    var isCouponSuccess by remember { mutableStateOf(false) }

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
    val finalTotal = maxOf(0, discountedSubtotal + taxes + deliveryFee)

    if (cartItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .padding(24.dp)
                .testTag("empty_cart_view"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(ForestGreenSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = ForestGreenPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your Shopping Bag is Empty",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Choose your favorite frames and configure custom prescription lenses.",
                        fontSize = 13.sp,
                        color = SlateMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onNavigateToCatalog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForestGreenPrimary,
                            contentColor = CleanWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("explore_catalog_from_empty_cart")
                    ) {
                        Text(
                            text = "Browse Optical Catalog",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(OffWhiteBackground)
                .testTag("cart_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ROYAL SHOPPING BAG",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarmGoldDark,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${cartItems.sumOf { it.quantity }} Eyewear Selected",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CharcoalText
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SoftGoldContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = ForestGreenDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (rawSubtotal >= 999) "FREE Express Delivery" else "Add ₹${999 - rawSubtotal} for FREE Delivery",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenDark
                            )
                        }
                    }
                }
            }

            // Cart Items with transparent unit breakdown & quantity controls
            items(cartItems, key = { it.id }) { item ->
                CartItemRow(
                    item = item,
                    onIncrease = { onQuantityChange(item.id, 1) },
                    onDecrease = { onQuantityChange(item.id, -1) },
                    onRemove = { onRemoveItem(item.id) }
                )
            }

            // Coupon Code Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ConfirmationNumber,
                                contentDescription = null,
                                tint = WarmGoldDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Apply Privilege Coupon",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (appliedCoupon != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SoftGoldContainer, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Coupon Applied: $appliedCoupon",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenDark
                                )
                                Text(
                                    text = "Remove",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RedSale,
                                    modifier = Modifier.clickable { onRemoveCoupon() }
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it },
                                    placeholder = { Text("e.g. ROYAL10", fontSize = 12.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )
                                Button(
                                    onClick = {
                                        val result = onApplyCoupon(couponInput)
                                        isCouponSuccess = result.first
                                        couponMessage = result.second
                                        if (result.first) couponInput = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Apply", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Try:", fontSize = 11.sp, color = SlateMuted)
                                Text(
                                    "ROYAL10 (10% off)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenPrimary,
                                    modifier = Modifier.clickable { couponInput = "ROYAL10" }
                                )
                                Text(
                                    "FIRSTSPECS (₹300 off)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreenPrimary,
                                    modifier = Modifier.clickable { couponInput = "FIRSTSPECS" }
                                )
                            }

                            couponMessage?.let { msg ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg,
                                    fontSize = 11.sp,
                                    color = if (isCouponSuccess) ForestGreenPrimary else RedSale,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Transparent Price Breakdown (Frame + Lens + Taxes)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Transparent Price Breakdown",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        PriceLine("Frames Subtotal", "₹$framesSubtotal")
                        PriceLine("Selected Lens Configurations", "+₹$lensSubtotal")
                        PriceLine("Taxes (GST 12% on Opticals)", "+₹$taxes")

                        if (discount > 0) {
                            PriceLine("Privilege Coupon Discount", "-₹$discount", isDiscount = true)
                        }

                        PriceLine(
                            "Express Insured Shipping",
                            if (deliveryFee == 0) "FREE" else "+₹$deliveryFee",
                            isFree = deliveryFee == 0
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = BorderLight
                        )

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
                                    text = "₹$finalTotal",
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
                                    text = "Inclusive of all Taxes",
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

            // Checkout Action Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Primary: Proceed to Checkout (Shipping & Payment)
                    Button(
                        onClick = onProceedToCheckout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("proceed_to_checkout_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForestGreenPrimary,
                            contentColor = CleanWhite
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Proceed to Checkout (₹$finalTotal)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = WarmGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Eyewear vector illustration
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(ForestGreenSurface, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    EyewearIllustration(
                        shape = item.product.shape,
                        category = item.product.category,
                        frameColor = Color(item.selectedColor.colorHex),
                        size = 76.dp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.product.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                color = CharcoalText
                            )
                            Text(
                                text = "Frame Color: ${item.selectedColor.name} (₹${item.frameUnitPrice})",
                                fontSize = 11.5.sp,
                                color = SlateMuted
                            )
                        }

                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Item",
                                tint = SlateMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Configured Lens Specs & Price Badge
                    Surface(
                        color = SoftGoldContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text(
                                text = "Lens: ${item.lensConfig.lensType.title} (+₹${item.lensConfig.lensType.priceInr})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenDark
                            )
                            if (item.lensConfig.lensIndex.priceInr > 0) {
                                Text(
                                    text = "Thickness: ${item.lensConfig.lensIndex.title} (+₹${item.lensConfig.lensIndex.priceInr})",
                                    fontSize = 10.sp,
                                    color = SlateMuted
                                )
                            }
                            if (item.lensConfig.selectedCoatings.isNotEmpty()) {
                                val coatings = item.lensConfig.selectedCoatings.joinToString(", ") { "${it.title} (+₹${it.priceInr})" }
                                Text(
                                    text = "Coatings: $coatings",
                                    fontSize = 10.sp,
                                    color = SlateMuted
                                )
                            }
                            if (item.lensConfig.prescriptionMethod == PrescriptionMethod.WHATSAPP_PHOTO) {
                                Text(
                                    text = "📸 Prescription: Photo on WhatsApp",
                                    fontSize = 10.sp,
                                    color = ForestGreenPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                val od = item.lensConfig.prescriptionDetails.rightEyeOD
                                val os = item.lensConfig.prescriptionDetails.leftEyeOS
                                Text(
                                    text = "Power: OD ${od.sph} | OS ${os.sph}",
                                    fontSize = 10.sp,
                                    color = ForestGreenPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Transparent item breakdown line & quantity controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quantity selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(ForestGreenSurface, RoundedCornerShape(8.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            IconButton(onClick = onDecrease, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = item.quantity.toString(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(onClick = onIncrease, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${item.totalPrice}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ForestGreenPrimary
                            )
                            Text(
                                text = "(${item.quantity} × ₹${item.unitPrice})",
                                fontSize = 10.sp,
                                color = SlateMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceLine(
    label: String,
    value: String,
    isDiscount: Boolean = false,
    isFree: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.5.sp, color = SlateMuted)
        Text(
            text = value,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = when {
                isDiscount -> RedSale
                isFree -> ForestGreenPrimary
                else -> CharcoalText
            }
        )
    }
}
