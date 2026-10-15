package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FrameColor
import com.example.model.LensType
import com.example.model.Product
import com.example.ui.components.EyewearIllustration
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SoftCardBg
import com.example.ui.theme.WarmYellowAccent
import com.example.util.WhatsAppHelper

@Composable
fun ProductDetailScreen(
    product: Product,
    onBackClick: () -> Unit,
    onBuyNowClick: (Product, FrameColor, LensType, Int) -> Unit,
    onConfigureLensesClick: (Product) -> Unit,
    onVirtualTryOnClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBackClick() }

    val angles = listOf("Front", "45° Angle", "Side View", "Top View", "Perspective")
    var selectedAngleIndex by remember { mutableStateOf(0) }
    var selectedColor by remember {
        mutableStateOf(product.colors.firstOrNull { it.isDefault } ?: product.colors.first())
    }
    var selectedLensType by remember { mutableStateOf(LensType.ZERO_POWER_BLUE_CUT) }
    var quantity by remember { mutableStateOf(1) }

    // Direct link to the item formatted for sharing
    val directItemLink = "https://optics.royol.store/product?id=${product.id}"

    // Formatted WhatsApp message dynamically incorporating product name, selected lens type, and direct link
    val shareMessage = remember(product, selectedLensType, selectedColor) {
        WhatsAppHelper.buildProductShareMessage(
            product = product,
            selectedLensType = selectedLensType,
            selectedColor = selectedColor,
            directItemLink = directItemLink
        )
    }

    val unitPrice = product.basePrice + selectedLensType.priceInr
    val totalPrice = unitPrice * quantity

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CleanWhite)
            .statusBarsPadding()
            .testTag("product_detail_screen")
    ) {
        // Top Bar: Back button, "Product Details", WhatsApp Share Button, More
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = CharcoalText
                )
            }

            Text(
                text = "Product Details",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalText
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Top WhatsApp Share Action Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF25D366),
                    modifier = Modifier
                        .clickable {
                            WhatsAppHelper.shareViaWhatsApp(context, shareMessage)
                            Toast.makeText(context, "Opening WhatsApp to share ${product.name}...", Toast.LENGTH_SHORT).show()
                        }
                        .testTag("top_share_whatsapp_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share via WhatsApp",
                            tint = CleanWhite,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Share",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CleanWhite
                        )
                    }
                }
            }
        }

        // Scrollable Body
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Multi-Angle Thumbnail Carousel
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(angles) { idx, angleName ->
                        val isSelected = selectedAngleIndex == idx
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) SoftCardBg else CleanWhite)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) DeepEmerald else BorderLight,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedAngleIndex = idx },
                            contentAlignment = Alignment.Center
                        ) {
                            EyewearIllustration(
                                shape = product.shape,
                                category = product.category,
                                frameColor = Color(selectedColor.colorHex),
                                size = 44.dp
                            )
                        }
                    }
                }
            }

            // Main Showcase Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        EyewearIllustration(
                            shape = product.shape,
                            category = product.category,
                            frameColor = Color(selectedColor.colorHex),
                            size = 230.dp
                        )
                    }
                }
            }

            // Product Title & Rating
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = WarmYellowAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${product.rating}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Timeless frame with UV protection and a sleek, comfortable fit. Handcrafted in ${product.frameMaterial}.",
                    fontSize = 13.5.sp,
                    color = SlateMuted,
                    lineHeight = 19.sp
                )
            }

            // Price Display & Quantity Selector Pill
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "₹$totalPrice",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = DeepEmerald
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "₹${(product.originalPrice + selectedLensType.priceInr) * quantity}",
                                fontSize = 15.sp,
                                color = SlateMuted,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                        if (selectedLensType.priceInr > 0) {
                            Text(
                                text = "Includes Frame ₹${product.basePrice * quantity} + Lens ₹${selectedLensType.priceInr * quantity}",
                                fontSize = 11.5.sp,
                                color = SlateMuted
                            )
                        }
                    }

                    // Quantity Selector Pill (- 1 +)
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = SoftCardBg,
                        modifier = Modifier.height(40.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = CharcoalText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = quantity.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                            IconButton(
                                onClick = { quantity++ },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = CharcoalText,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Color Palette Swatch Picker
            item {
                Column {
                    Text(
                        text = "Color Options: ${selectedColor.name}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        product.colors.forEach { c ->
                            val isChosen = selectedColor.name == c.name
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color(c.colorHex))
                                    .border(
                                        width = if (isChosen) 2.5.dp else 1.dp,
                                        color = if (isChosen) DeepEmerald else BorderLight,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = c },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChosen) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (c.name.contains("Gold", true) || c.name.contains("Clear", true)) DeepEmerald else CleanWhite,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 1: Interactive Lens Type Selection
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Lens Type",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Text(
                            text = selectedLensType.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepEmerald
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        LensType.values().forEach { lt ->
                            val isSelected = selectedLensType == lt
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) Color(0xFFF0FDF4) else SoftCardBg,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) DeepEmerald else BorderLight
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedLensType = lt }
                                    .testTag("lens_option_${lt.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    width = 2.dp,
                                                    color = if (isSelected) DeepEmerald else SlateMuted.copy(alpha = 0.5f),
                                                    shape = CircleShape
                                                )
                                                .background(if (isSelected) DeepEmerald else Color.Transparent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = CleanWhite,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = lt.title,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) DeepEmerald else CharcoalText
                                            )
                                            Text(
                                                text = lt.subtitle,
                                                fontSize = 11.sp,
                                                color = SlateMuted,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (lt.priceInr == 0) BorderLight else Color(0xFFE8F5E9)
                                    ) {
                                        Text(
                                            text = if (lt.priceInr == 0) "Free" else "+₹${lt.priceInr}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (lt.priceInr == 0) CharcoalText else DeepEmerald,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Lens Configurator Option Banner
            item {
                Surface(
                    onClick = { onConfigureLensesClick(product) },
                    shape = RoundedCornerShape(16.dp),
                    color = SoftCardBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Have an Eye Prescription (SPH/CYL)?",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepEmerald
                            )
                            Text(
                                text = "Enter custom eye powers, 1.61/1.67 high index & DriveSafe coatings",
                                fontSize = 11.sp,
                                color = SlateMuted
                            )
                        }
                        Text(
                            text = "Configure →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepEmerald
                        )
                    }
                }
            }

            // Section 2: Prominent "Share via WhatsApp" Feature Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("whatsapp_share_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FCF9)),
                    border = BorderStroke(1.5.dp, Color(0xFF25D366).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF25D366)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = null,
                                        tint = CleanWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Share via WhatsApp",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText
                                    )
                                    Text(
                                        text = "Share frame, active lens & link with friends",
                                        fontSize = 11.sp,
                                        color = SlateMuted
                                    )
                                }
                            }

                            // Copy Direct Link Icon Button
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Product Link", directItemLink)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Direct link copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("btn_copy_product_link")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Link",
                                    tint = SlateMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Formatted preview box showing live message content
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CleanWhite,
                            border = BorderStroke(1.dp, BorderLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Preview Formatted Message:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "👓 ${product.name}\n🔍 Selected Lens: ${selectedLensType.title}\n🎨 Color: ${selectedColor.name} | Price: ₹$totalPrice\n🔗 $directItemLink",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = CharcoalText,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions Row: Primary Share on WhatsApp & Concierge Order
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    WhatsAppHelper.shareViaWhatsApp(context, shareMessage)
                                    Toast.makeText(context, "Opening WhatsApp to share frame...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("btn_share_via_whatsapp"),
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF25D366),
                                    contentColor = CleanWhite
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Share on WhatsApp",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    WhatsAppHelper.openWhatsAppChat(context, shareMessage)
                                },
                                modifier = Modifier
                                    .height(44.dp)
                                    .testTag("btn_whatsapp_concierge"),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, DeepEmerald)
                            ) {
                                Text(
                                    text = "Inquire",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepEmerald
                                )
                            }
                        }
                    }
                }
            }

            // Virtual Mirror & Try-On Option Banner
            item {
                Surface(
                    onClick = onVirtualTryOnClick,
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF00473E),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8B84A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF00473E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Virtual Mirror & Try-On",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE8B84A)
                                )
                                Text(
                                    text = "See how this frame fits your face shape",
                                    fontSize = 11.sp,
                                    color = CleanWhite.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Text(
                            text = "Try On 📷",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE8B84A)
                        )
                    }
                }
            }
        }

        // Sticky Bottom Bar: Quick WhatsApp Share Pill + "Buy Now" Button
        Surface(
            color = CleanWhite,
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick WhatsApp Share Pill Button
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFF25D366),
                    modifier = Modifier
                        .height(52.dp)
                        .clickable {
                            WhatsAppHelper.shareViaWhatsApp(context, shareMessage)
                            Toast.makeText(context, "Opening WhatsApp...", Toast.LENGTH_SHORT).show()
                        }
                        .testTag("bottom_whatsapp_share_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = CleanWhite,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CleanWhite
                        )
                    }
                }

                // Buy Now Primary Pill Button
                Button(
                    onClick = { onBuyNowClick(product, selectedColor, selectedLensType, quantity) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("product_detail_buy_now_button"),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepEmerald,
                        contentColor = CleanWhite
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                ) {
                    Text(
                        text = "Buy Now • ₹$totalPrice",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Backward-compatible overload without LensType parameter
@Composable
fun ProductDetailScreen(
    product: Product,
    onBackClick: () -> Unit,
    onBuyNowClick: (Product, FrameColor, Int) -> Unit,
    onConfigureLensesClick: (Product) -> Unit,
    onVirtualTryOnClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ProductDetailScreen(
        product = product,
        onBackClick = onBackClick,
        onBuyNowClick = { prod, color, _, qty -> onBuyNowClick(prod, color, qty) },
        onConfigureLensesClick = onConfigureLensesClick,
        onVirtualTryOnClick = onVirtualTryOnClick,
        modifier = modifier
    )
}
