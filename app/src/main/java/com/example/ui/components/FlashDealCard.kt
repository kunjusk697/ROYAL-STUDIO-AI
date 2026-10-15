package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.FlashDeal
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.DeepEmeraldDark
import kotlinx.coroutines.delay

/**
 * Compact "Flash Offers & Deals" Banner Strip
 * Height: ~150-165dp, compact, luxury Deep Forest Green & Warm Gold styling.
 */
@Composable
fun FlashDealCard(
    deal: FlashDeal,
    onClaimDeal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!deal.isEnabled) return

    val context = LocalContext.current
    var isClaimed by remember { mutableStateOf(false) }

    // Live ticking countdown simulation (e.g. 03h : 42m : 18s)
    var totalSecondsRemaining by remember { mutableIntStateOf(3 * 3600 + 42 * 60 + 18) }
    LaunchedEffect(Unit) {
        while (totalSecondsRemaining > 0) {
            delay(1000L)
            totalSecondsRemaining--
        }
    }

    val hours = totalSecondsRemaining / 3600
    val minutes = (totalSecondsRemaining % 3600) / 60
    val seconds = totalSecondsRemaining % 60
    val countdownText = remember(hours, minutes, seconds) {
        String.format("%02dh : %02dm : %02ds", hours, minutes, seconds)
    }

    val bgColor = remember(deal.backgroundColorHex) {
        try {
            Color(android.graphics.Color.parseColor(deal.backgroundColorHex))
        } catch (e: Exception) {
            Color(0xFF00473E)
        }
    }

    val accentColor = remember(deal.accentColorHex) {
        try {
            Color(android.graphics.Color.parseColor(deal.accentColorHex))
        } catch (e: Exception) {
            Color(0xFFE8B84A)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 20.dp)
            .testTag("flash_deal_card"),
        shape = RoundedCornerShape(24.dp), // Exact luxury rounded card borders
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background subtle gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                bgColor,
                                bgColor.copy(alpha = 0.95f),
                                bgColor.copy(alpha = 0.70f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Badge, Countdown, Title, Subtitle, CTA
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Row 1: Flash Sale Badge + Countdown Timer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = accentColor
                        ) {
                            Text(
                                text = "⚡ ${deal.discountPercent}% OFF",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = DeepEmeraldDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.35f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, CleanWhite.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = countdownText,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CleanWhite
                                )
                            }
                        }
                    }

                    // Row 2: Title & Subtitle
                    Column {
                        Text(
                            text = deal.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CleanWhite,
                            fontFamily = FontFamily.Serif,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = deal.subtitle,
                            fontSize = 10.5.sp,
                            color = CleanWhite.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Row 3: Claim Button + Coupon Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = {
                                isClaimed = true
                                onClaimDeal(deal.couponCode)
                            },
                            shape = RoundedCornerShape(50), // fully rounded pill
                            color = if (isClaimed) Color(0xFF15803D) else accentColor,
                            shadowElevation = 2.dp,
                            modifier = Modifier.testTag("btn_claim_flash_deal")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isClaimed) Icons.Default.Check else Icons.Default.LocalOffer,
                                    contentDescription = null,
                                    tint = if (isClaimed) CleanWhite else DeepEmeraldDark,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isClaimed) "Applied!" else deal.ctaText,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isClaimed) CleanWhite else DeepEmeraldDark
                                )
                            }
                        }

                        // Coupon Code Chip
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CleanWhite.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CleanWhite.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = deal.couponCode,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CleanWhite,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Right Column: Image with "25% OFF" circular badge
                Box(
                    modifier = Modifier
                        .size(width = 105.dp, height = 132.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    if (deal.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(deal.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = deal.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        RoyolOpticalsLogo(
                            height = 28.dp,
                            tint = Color(0xFF00473E),
                            showWordmark = false
                        )
                    }

                    // Floating circular discount badge overlay (matches the uploaded posters)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(5.dp)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(CleanWhite)
                            .border(1.dp, Color(0xFF00473E).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${deal.discountPercent}%",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF00473E)
                            )
                            Text(
                                text = "OFF",
                                fontSize = 6.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }
        }
    }
}
