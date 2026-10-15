package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CartItem
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.PrescriptionMethod
import com.example.ui.components.EyewearIllustration
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.ForestGreenSurface
import com.example.ui.theme.OffWhiteBackground
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SoftGoldContainer
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmGoldDark
import com.example.util.WhatsAppHelper

@Composable
fun OrdersScreen(
    orders: List<Order>,
    onReorder: (List<CartItem>) -> Unit,
    onNavigateToCatalog: () -> Unit,
    onSimulateTracking: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0: All, 1: In Progress, 2: Delivered
    var invoiceOrder by remember { mutableStateOf<Order?>(null) }

    val filteredOrders = when (selectedTab) {
        1 -> orders.filter { !it.status.isDelivered }
        2 -> orders.filter { it.status.isDelivered }
        else -> orders
    }

    if (invoiceOrder != null) {
        OrderInvoiceDialog(
            order = invoiceOrder!!,
            onDismiss = { invoiceOrder = null }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBackground)
            .testTag("orders_screen")
    ) {
        // Top Header
        Surface(
            color = CleanWhite,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 4.dp)) {
                Text(
                    text = "PURCHASE HISTORY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarmGoldDark,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "My Optical Orders & Tracking",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CharcoalText
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CleanWhite,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = ForestGreenPrimary,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "All (${orders.size})",
                                fontSize = 12.5.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 0) ForestGreenPrimary else SlateMuted
                            )
                        },
                        modifier = Modifier.testTag("orders_tab_all")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "In Progress (${orders.count { !it.status.isDelivered }})",
                                fontSize = 12.5.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 1) ForestGreenPrimary else SlateMuted
                            )
                        },
                        modifier = Modifier.testTag("orders_tab_inprogress")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                text = "Delivered (${orders.count { it.status.isDelivered }})",
                                fontSize = 12.5.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 2) ForestGreenPrimary else SlateMuted
                            )
                        },
                        modifier = Modifier.testTag("orders_tab_delivered")
                    )
                }
            }
        }

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(ForestGreenSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = ForestGreenPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No Orders in this Category",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Explore our handcrafted frames with custom blue-cut and prescription lenses.",
                            fontSize = 12.5.sp,
                            color = SlateMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToCatalog,
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                        ) {
                            Text("Browse Specs Catalog", fontSize = 13.sp)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredOrders, key = { it.orderId }) { order ->
                    OrderHistoryCard(
                        order = order,
                        onTrackWhatsApp = {
                            val msg = "Hello Royal Specs, I would like to check the tracking status of my Order #${order.orderId} (Estimated Delivery: ${order.estimatedDeliveryDate}). Thank you!"
                            WhatsAppHelper.openWhatsAppChat(context, msg)
                        },
                        onViewInvoice = { invoiceOrder = order },
                        onBuyAgain = { onReorder(order.items) },
                        onSimulateTracking = { onSimulateTracking(order.orderId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryCard(
    order: Order,
    onTrackWhatsApp: () -> Unit,
    onViewInvoice: () -> Unit,
    onBuyAgain: () -> Unit,
    onSimulateTracking: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.orderId}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Order ID, Timestamp & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Order #${order.orderId}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreenPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ForestGreenSurface
                        ) {
                            Text(
                                text = order.paymentMethod.tag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Placed on ${order.timestampFormatted}",
                        fontSize = 11.sp,
                        color = SlateMuted
                    )
                }

                // Status chip
                val statusBg = when (order.status) {
                    OrderStatus.DELIVERED -> Color(0xFFE8F5E9)
                    OrderStatus.DISPATCHED -> Color(0xFFE3F2FD)
                    OrderStatus.LAB_CRAFTING -> SoftGoldContainer
                    else -> ForestGreenSurface
                }
                val statusText = when (order.status) {
                    OrderStatus.DELIVERED -> Color(0xFF2E7D32)
                    OrderStatus.DISPATCHED -> Color(0xFF1565C0)
                    OrderStatus.LAB_CRAFTING -> WarmGoldDark
                    else -> ForestGreenPrimary
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusBg
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (order.status == OrderStatus.DELIVERED) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = statusText,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = order.status.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prominent Estimated Delivery Date Section
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (order.status.isDelivered) Color(0xFFF1F8F4) else SoftGoldContainer,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (order.status.isDelivered) EmeraldAccent.copy(alpha = 0.3f) else WarmGold
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (order.status.isDelivered) EmeraldAccent else ForestGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (order.status.isDelivered) Icons.Default.Verified else Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = if (order.status.isDelivered) CleanWhite else WarmGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (order.status.isDelivered) "Delivered on ${order.estimatedDeliveryDate}" else "Estimated Delivery: ${order.estimatedDeliveryDate}",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${order.courierPartner} • AWB: ${order.trackingNumber}",
                                fontSize = 10.5.sp,
                                color = SlateMuted
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CleanWhite
                    ) {
                        Text(
                            text = order.deliveryCountdownText,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (order.status.isDelivered) Color(0xFF2E7D32) else ForestGreenPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Tracking Progress Bar
            OrderTrackingProgressBar(currentStep = order.status.stepIndex)

            Spacer(modifier = Modifier.height(12.dp))

            // Itemized Eyewear
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ForestGreenSurface, RoundedCornerShape(10.dp))
                            .padding(8.dp),
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
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                            Text(
                                text = "Lens: ${item.lensConfig.lensType.title} (+₹${item.lensConfig.lensType.priceInr})",
                                fontSize = 11.sp,
                                color = ForestGreenPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (item.lensConfig.prescriptionMethod == PrescriptionMethod.WHATSAPP_PHOTO) {
                                    "📸 Prescription photo on WhatsApp"
                                } else {
                                    "Prescription manually entered"
                                },
                                fontSize = 10.sp,
                                color = SlateMuted
                            )
                        }
                        Text(
                            text = "₹${item.totalPrice}",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreenDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Transparent Price Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Frames ₹${order.framesSubtotal} + Lens ₹${order.lensSubtotal} + Taxes ₹${order.taxes}",
                    fontSize = 11.sp,
                    color = SlateMuted
                )
                Text(
                    text = "Total Paid: ₹${order.totalAmount}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ForestGreenPrimary
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BorderLight)

            // Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // WhatsApp Tracking button
                Button(
                    onClick = onTrackWhatsApp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366),
                        contentColor = CleanWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Track on WhatsApp", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }

                // Invoice button
                OutlinedButton(
                    onClick = onViewInvoice,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(0.9f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Invoice", fontSize = 11.5.sp)
                }

                // Buy Again button
                OutlinedButton(
                    onClick = onBuyAgain,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier.weight(0.9f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reorder", fontSize = 11.5.sp)
                }

                // Simulate Tracking
                if (order.status.isPending) {
                    OutlinedButton(
                        onClick = onSimulateTracking,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                        modifier = Modifier.weight(0.8f)
                    ) {
                        Text("Simulate", fontSize = 10.5.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderTrackingProgressBar(currentStep: Int) {
    val steps = listOf("Placed", "Verified", "Lab", "Shipped", "Delivered")

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, stepName ->
                val isCompleted = index <= currentStep
                val isCurrent = index == currentStep

                // Dot
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCurrent -> ForestGreenPrimary
                                isCompleted -> ForestGreenPrimary
                                else -> BorderLight
                            }
                        )
                        .border(
                            width = if (isCurrent) 2.dp else 0.dp,
                            color = if (isCurrent) WarmGold else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = CleanWhite,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }

                // Connecting Line between dots
                if (index < steps.size - 1) {
                    val lineActive = index < currentStep
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(if (lineActive) ForestGreenPrimary else BorderLight)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Step Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEachIndexed { index, stepName ->
                Text(
                    text = stepName,
                    fontSize = 9.sp,
                    fontWeight = if (index <= currentStep) FontWeight.Bold else FontWeight.Normal,
                    color = if (index <= currentStep) ForestGreenPrimary else SlateMuted
                )
            }
        }
    }
}

@Composable
private fun OrderInvoiceDialog(
    order: Order,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    tint = ForestGreenPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Royal Specs Optical Receipt",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = ForestGreenSurface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "ROYAL SPECS OPTICAL LABS PVT. LTD.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = ForestGreenDark,
                            fontFamily = FontFamily.Serif
                        )
                        Text(text = "GSTIN: 32AABCR9946M1Z8 • HSN: 9003 / 9004", fontSize = 10.sp, color = SlateMuted)
                        Text(text = "Official WhatsApp: +91 99467 32958", fontSize = 10.sp, color = SlateMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Invoice #: INV-${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                Text(text = "Date: ${order.timestampFormatted}", fontSize = 11.sp, color = SlateMuted)
                Text(text = "Billed to: ${order.customerName} (${order.phone})", fontSize = 11.sp, color = CharcoalText)
                Text(text = "Address: ${order.address}", fontSize = 10.5.sp, color = SlateMuted)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderLight)

                order.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "${item.quantity}x ${item.product.name}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Lens: ${item.lensConfig.lensType.title}", fontSize = 10.sp, color = SlateMuted)
                        }
                        Text(text = "₹${item.totalPrice}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderLight)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Frames Subtotal", fontSize = 11.sp, color = SlateMuted)
                    Text(text = "₹${order.framesSubtotal}", fontSize = 11.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Lens Customization", fontSize = 11.sp, color = SlateMuted)
                    Text(text = "₹${order.lensSubtotal}", fontSize = 11.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "GST (12%)", fontSize = 11.sp, color = SlateMuted)
                    Text(text = "₹${order.taxes}", fontSize = 11.sp)
                }
                if (order.discount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Discount", fontSize = 11.sp, color = Color.Red)
                        Text(text = "-₹${order.discount}", fontSize = 11.sp, color = Color.Red)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Shipping", fontSize = 11.sp, color = ForestGreenPrimary)
                    Text(text = if (order.deliveryFee == 0) "FREE" else "₹${order.deliveryFee}", fontSize = 11.sp)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = BorderLight)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Total Paid", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                    Text(text = "₹${order.totalAmount}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = ForestGreenPrimary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
            ) {
                Text("Close Receipt")
            }
        }
    )
}
