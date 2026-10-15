package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.ForestGreenSurface
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SoftGoldContainer
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmGoldDark
import com.example.util.WhatsAppHelper

@Composable
fun UpiQrDialog(
    totalAmount: Int,
    orderId: String,
    onDismiss: () -> Unit,
    onPaymentConfirmed: (upiRef: String) -> Unit
) {
    val context = LocalContext.current
    var transactionRef by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .testTag("upi_qr_modal"),
            color = CleanWhite,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ForestGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = WarmGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Instant UPI Payment",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = CharcoalText
                            )
                            Text(
                                text = "Royal Specs Official VPA",
                                fontSize = 11.sp,
                                color = SlateMuted
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SlateMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount Highlight
                Surface(
                    color = SoftGoldContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Amount Payable",
                                fontSize = 11.sp,
                                color = SlateMuted
                            )
                            Text(
                                text = "₹$totalAmount",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ForestGreenPrimary
                            )
                        }

                        Surface(
                            color = CleanWhite,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WarmGoldDark)
                        ) {
                            Text(
                                text = "Order #$orderId",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stylized Vector QR Code Canvas
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                    border = androidx.compose.foundation.BorderStroke(2.dp, ForestGreenPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(170.dp)) {
                            val w = size.width
                            val h = size.height

                            // Draw QR Finder Patterns (3 corners)
                            drawQrCorner(0f, 0f, 44f, ForestGreenPrimary)
                            drawQrCorner(w - 44f, 0f, 44f, ForestGreenPrimary)
                            drawQrCorner(0f, h - 44f, 44f, ForestGreenPrimary)

                            // Draw stylized inner data blocks
                            val step = 14f
                            for (x in 3..9) {
                                for (y in 1..10) {
                                    val px = x * step
                                    val py = y * step
                                    val isCenter = (x in 5..7 && y in 5..7)
                                    val isTiming = (x == 4 || y == 4 || x == 8)
                                    val isPseudoRandom = (x * 7 + y * 13) % 3 != 0

                                    if (!isCenter && (isTiming || isPseudoRandom)) {
                                        drawRoundRect(
                                            color = if ((x + y) % 5 == 0) WarmGoldDark else ForestGreenPrimary,
                                            topLeft = Offset(px, py),
                                            size = Size(10f, 10f),
                                            cornerRadius = CornerRadius(2f, 2f)
                                        )
                                    }
                                }
                            }

                            // Center Royal Emblem Badge
                            drawCircle(
                                color = CleanWhite,
                                radius = 24f,
                                center = Offset(w / 2, h / 2)
                            )
                            drawCircle(
                                color = ForestGreenPrimary,
                                radius = 20f,
                                center = Offset(w / 2, h / 2)
                            )
                            drawCircle(
                                color = WarmGold,
                                radius = 17f,
                                center = Offset(w / 2, h / 2)
                            )
                        }

                        // Center rupee / royal symbol overlay
                        Text(
                            text = "₹",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ForestGreenDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Payee Details & Copy
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ForestGreenSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "UPI ID: ${WhatsAppHelper.STORE_UPI_ID}",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = ForestGreenPrimary
                            )
                            Text(
                                text = WhatsAppHelper.STORE_UPI_NAME,
                                fontSize = 10.5.sp,
                                color = SlateMuted
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Royal Specs UPI", WhatsAppHelper.STORE_UPI_ID)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "UPI ID copied: ${WhatsAppHelper.STORE_UPI_ID}", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Supported UPI Apps badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Supported: Google Pay • PhonePe • Paytm • BHIM • CRED",
                        fontSize = 10.sp,
                        color = SlateMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Transaction Ref Input (Optional)
                OutlinedTextField(
                    value = transactionRef,
                    onValueChange = { transactionRef = it },
                    label = { Text("UPI Ref / UTR No. (Optional)", fontSize = 11.sp) },
                    placeholder = { Text("e.g. 423985112344", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Confirm Button
                Button(
                    onClick = {
                        onPaymentConfirmed(transactionRef.ifBlank { "UPI-PAID-${System.currentTimeMillis() % 100000}" })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_upi_payment_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreenPrimary,
                        contentColor = CleanWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = WarmGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I Have Paid ₹$totalAmount",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawQrCorner(
    x: Float,
    y: Float,
    size: Float,
    color: Color
) {
    // Outer square
    drawRoundRect(
        color = color,
        topLeft = Offset(x, y),
        size = Size(size, size),
        cornerRadius = CornerRadius(6f, 6f),
        style = Stroke(width = 7f)
    )
    // Inner center dot
    drawRoundRect(
        color = color,
        topLeft = Offset(x + 11f, y + 11f),
        size = Size(size - 22f, size - 22f),
        cornerRadius = CornerRadius(4f, 4f)
    )
}
