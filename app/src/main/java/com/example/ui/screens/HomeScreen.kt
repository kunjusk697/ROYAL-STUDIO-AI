package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FlashDeal
import com.example.model.FrameCategory
import com.example.model.FrameColor
import com.example.model.FrameShape
import com.example.model.HomeBanner
import com.example.model.Product
import com.example.ui.components.EyewearIllustration
import com.example.ui.components.FlashDealCard
import com.example.ui.components.HomeBannerCarousel
import com.example.ui.components.RoyolOpticalsLogo
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.DeepEmeraldDark
import com.example.ui.theme.DeepEmeraldSurface
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SoftCardBg
import com.example.ui.theme.TerracottaAccent
import com.example.ui.theme.WarmYellowAccent
import com.example.ui.theme.WarmYellowLight
import com.example.util.WhatsAppHelper

data class OpticsCategoryItem(
    val name: String,
    val category: FrameCategory,
    val bgColor: Color,
    val shape: FrameShape
)

@Composable
fun HomeScreen(
    featuredProducts: List<Product>,
    banners: List<HomeBanner> = emptyList(),
    flashDeal: FlashDeal? = null,
    onClaimFlashDeal: (String) -> Unit = {},
    onNavigateToCatalog: (FrameCategory) -> Unit,
    onProductClick: (Product) -> Unit,
    onConfigureProduct: (Product) -> Unit,
    onQuickAddFrame: (Product, FrameColor) -> Unit,
    onOpenVirtualTryOn: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val categoryAvatars = listOf(
        OpticsCategoryItem("Women", FrameCategory.SUNGLASSES, Color(0xFFC04E74), FrameShape.CAT_EYE),
        OpticsCategoryItem("Men", FrameCategory.CLASSIC_EYEGLASSES, Color(0xFFD95A2B), FrameShape.WAYFARER),
        OpticsCategoryItem("Classic", FrameCategory.CLASSIC_EYEGLASSES, Color(0xFFE88A68), FrameShape.ROUND),
        OpticsCategoryItem("Kids", FrameCategory.BLUE_CUT, Color(0xFFF4B400), FrameShape.ROUND),
        OpticsCategoryItem("Baby", FrameCategory.BLUE_CUT, Color(0xFFB57252), FrameShape.ROUND)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CleanWhite)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Promotional Slideshow Carousel (Auto-playing with swipe & dots)
        item {
            if (banners.isNotEmpty()) {
                HomeBannerCarousel(
                    banners = banners,
                    onBannerClick = { banner ->
                        onNavigateToCatalog(banner.categoryTarget)
                    }
                )
            } else {
                OpticsHeroBanner(
                    onOrderNowClick = { onNavigateToCatalog(FrameCategory.ALL) }
                )
            }
        }

        // Compact Flash Offers & Deals Card (Height: ~160dp)
        flashDeal?.let { deal ->
            if (deal.isEnabled) {
                item {
                    FlashDealCard(
                        deal = deal,
                        onClaimDeal = { coupon ->
                            onClaimFlashDeal(coupon)
                        }
                    )
                }
            }
        }

        // Shop By Category Section with circular avatars
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shop By Category",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )
                    Text(
                        text = "See All",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateMuted,
                        modifier = Modifier.clickable { onNavigateToCatalog(FrameCategory.ALL) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categoryAvatars) { catItem ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onNavigateToCatalog(catItem.category) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .background(catItem.bgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                EyewearIllustration(
                                    shape = catItem.shape,
                                    category = catItem.category,
                                    frameColor = CleanWhite,
                                    size = 46.dp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = catItem.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CharcoalText
                            )
                        }
                    }
                }
            }
        }

        // "Curated For You" Section (2-Column editorial cards with model portraits)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Curated For You",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )
                    Text(
                        text = "See All",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateMuted,
                        modifier = Modifier.clickable { onNavigateToCatalog(FrameCategory.ALL) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Curated Cards Horizontal Row / Grid
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(featuredProducts.take(4)) { product ->
                        CuratedProductCard(
                            product = product,
                            onCardClick = { onProductClick(product) },
                            onBuyNowClick = {
                                onProductClick(product)
                            }
                        )
                    }
                }
            }
        }

        // Virtual Mirror & Try-On Feature Card
        item {
            Surface(
                onClick = onOpenVirtualTryOn,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                color = DeepEmeraldDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, WarmYellowAccent.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(WarmYellowAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = DeepEmeraldDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Virtual Mirror & Try-On",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CleanWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = WarmYellowAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "NEW",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = WarmYellowAccent,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Preview frames on your face & find your signature fit",
                            fontSize = 11.5.sp,
                            color = CleanWhite.copy(alpha = 0.8f)
                        )
                    }
                    Button(
                        onClick = onOpenVirtualTryOn,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarmYellowAccent,
                            contentColor = DeepEmeraldDark
                        ),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Try On", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // WhatsApp Direct Optometrist Consultation Card
        item {
            Surface(
                onClick = {
                    WhatsAppHelper.openWhatsAppChat(
                        context = context,
                        message = "Hello OPTICS team, I'd like help choosing frames and lenses."
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                color = SoftCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF25D366)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            tint = CleanWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Consult an Optometrist on WhatsApp",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Text(
                            text = "Direct advice & order confirmation (+91 99467 32958)",
                            fontSize = 11.sp,
                            color = SlateMuted
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = DeepEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OpticsHeroBanner(
    onOrderNowClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .testTag("optics_hero_card"),
        shape = RoundedCornerShape(28.dp), // Exact 28dp radius from mockup
        colors = CardDefaults.cardColors(containerColor = DeepEmerald),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Wavy graphic line
            Canvas(modifier = Modifier.matchParentSize()) {
                val wavePath = Path().apply {
                    moveTo(size.width * 0.45f, size.height * 0.85f)
                    quadraticTo(
                        size.width * 0.52f, size.height * 0.45f,
                        size.width * 0.62f, size.height * 0.7f
                    )
                }
                drawPath(
                    path = wavePath,
                    color = WarmYellowAccent,
                    style = Stroke(width = 4f, cap = StrokeCap.Round)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: 30% OFF sticker, NEW COLLECTIONS, Order Now pill
                Column(modifier = Modifier.weight(1f)) {
                    // Vibrant yellow sticker "30% OFF"
                    Text(
                        text = "30% OFF",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = WarmYellowAccent,
                        letterSpacing = (-0.5).sp,
                        fontFamily = FontFamily.Serif
                    )

                    Text(
                        text = "NEW COLLECTIONS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CleanWhite.copy(alpha = 0.9f),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // White Pill Button "Order Now"
                    Button(
                        onClick = onOrderNowClick,
                        shape = RoundedCornerShape(50), // Fully rounded pill
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CleanWhite,
                            contentColor = DeepEmerald
                        ),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = "Order Now",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepEmerald
                        )
                    }
                }

                // Right Column: Model wearing chic sunglasses portrait
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFE89872)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(45.dp, 20.dp)
                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                .background(Color(0xFF2B140E))
                        )
                        EyewearIllustration(
                            shape = FrameShape.CAT_EYE,
                            category = FrameCategory.SUNGLASSES,
                            frameColor = CleanWhite,
                            size = 110.dp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(60.dp, 24.dp)
                                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                                .background(TerracottaAccent)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CuratedProductCard(
    product: Product,
    onCardClick: () -> Unit,
    onBuyNowClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(28.dp), // Deep 28dp radius
        colors = CardDefaults.cardColors(containerColor = SoftCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Lifestyle Portrait / Eyewear Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        if (product.shape == FrameShape.CAT_EYE) Color(0xFFC04E74).copy(alpha = 0.85f)
                        else if (product.shape == FrameShape.WAYFARER) Color(0xFFD95A2B).copy(alpha = 0.85f)
                        else Color(0xFF135C50).copy(alpha = 0.85f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                EyewearIllustration(
                    shape = product.shape,
                    category = product.category,
                    frameColor = CleanWhite,
                    size = 130.dp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Model / Frame Name
            Text(
                text = product.name,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalText,
                maxLines = 1
            )
            Text(
                text = product.brand,
                fontSize = 11.sp,
                color = SlateMuted,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom row: Price Pill & "Buy Now" Pill Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price Pill
                Surface(
                    shape = RoundedCornerShape(50),
                    color = CleanWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Text(
                        text = "₹${product.basePrice}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Buy Now Pill Button
                Button(
                    onClick = onBuyNowClick,
                    shape = RoundedCornerShape(50), // Fully rounded pill
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepEmerald,
                        contentColor = CleanWhite
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = "Buy Now",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
