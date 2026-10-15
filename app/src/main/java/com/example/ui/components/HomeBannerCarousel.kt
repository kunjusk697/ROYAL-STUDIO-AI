package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
import com.example.model.FrameCategory
import com.example.model.FrameShape
import com.example.model.HomeBanner
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.DeepEmeraldDark
import com.example.ui.theme.WarmYellowAccent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Interactive Auto-Playing Home Banner Slideshow (Carousel)
 * Features:
 * - Auto-playing carousel with smooth pause-on-drag
 * - Dot indicators with animated active pill
 * - Swipe gestures & left/right quick jump buttons
 * - Coil image support with luxury fallback art & gradient overlays
 * - Prominent "ROYOL OPTICALS" spectacles brand integration
 */
@Composable
fun HomeBannerCarousel(
    banners: List<HomeBanner>,
    onBannerClick: (HomeBanner) -> Unit,
    modifier: Modifier = Modifier,
    autoPlayIntervalMillis: Long = 4200L
) {
    val activeBanners = remember(banners) { banners.filter { it.isActive } }
    if (activeBanners.isEmpty()) return

    val pagerState = rememberPagerState(initialPage = 0) { activeBanners.size }
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-advance loop when user is not manually dragging
    LaunchedEffect(activeBanners.size, isDragged) {
        if (activeBanners.size > 1 && !isDragged) {
            while (true) {
                delay(autoPlayIntervalMillis)
                val next = (pagerState.currentPage + 1) % activeBanners.size
                pagerState.animateScrollToPage(
                    page = next,
                    animationSpec = tween(durationMillis = 650)
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_banner_slideshow"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(205.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .testTag("banner_pager")
            ) { page ->
                val banner = activeBanners[page]
                BannerCard(
                    banner = banner,
                    onClick = { onBannerClick(banner) }
                )
            }

            // Left / Right quick nav arrows (visible when multiple slides exist)
            if (activeBanners.size > 1) {
                // Left Arrow
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            val prev = if (pagerState.currentPage == 0) activeBanners.size - 1 else pagerState.currentPage - 1
                            pagerState.animateScrollToPage(prev)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 6.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.35f))
                        .testTag("banner_prev_arrow")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Slide",
                        tint = CleanWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Right Arrow
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            val next = (pagerState.currentPage + 1) % activeBanners.size
                            pagerState.animateScrollToPage(next)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 6.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.35f))
                        .testTag("banner_next_arrow")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Slide",
                        tint = CleanWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dot & Pill Indicators
        if (activeBanners.size > 1) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .testTag("banner_indicators_row")
            ) {
                activeBanners.indices.forEach { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 7.dp,
                        animationSpec = tween(durationMillis = 300),
                        label = "dot_width"
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(6.dp)
                            .width(width)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (isSelected) Color(0xFFE8B84A) else Color(0xFFCBD5E1)
                            )
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun BannerCard(
    banner: HomeBanner,
    onClick: () -> Unit
) {
    val bgColor = remember(banner.backgroundColorHex) {
        try {
            Color(android.graphics.Color.parseColor(banner.backgroundColorHex))
        } catch (e: Exception) {
            Color(0xFF00473E) // fallback to Deep Forest Green
        }
    }

    val accentColor = remember(banner.accentColorHex) {
        try {
            Color(android.graphics.Color.parseColor(banner.accentColorHex))
        } catch (e: Exception) {
            Color(0xFFE8B84A) // fallback to Warm Gold
        }
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() }
            .testTag("banner_card_${banner.id}"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Background image (if provided)
            if (banner.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(banner.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = banner.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // High-fashion tint & text readability gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    bgColor.copy(alpha = 0.94f),
                                    bgColor.copy(alpha = 0.70f),
                                    Color.Black.copy(alpha = 0.35f)
                                )
                            )
                        )
                )
            } else {
                // Procedural luxury background pattern with subtle light halos
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = accentColor.copy(alpha = 0.12f),
                        radius = size.width * 0.45f,
                        center = Offset(size.width * 0.85f, size.height * 0.25f)
                    )
                }
            }

            // 2. Banner Content Overlay
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column: Tag, Title, Subtitle, CTA Button
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Tag Badge
                    if (banner.tagText.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = accentColor,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = banner.tagText.uppercase(),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black,
                                color = if (accentColor == CleanWhite) Color(0xFF00473E) else DeepEmeraldDark,
                                letterSpacing = 0.8.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Main Title
                    Text(
                        text = banner.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CleanWhite,
                        lineHeight = 24.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = FontFamily.Serif
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Subtitle
                    Text(
                        text = banner.subtitle,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = CleanWhite.copy(alpha = 0.88f),
                        lineHeight = 15.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Pill Button CTA
                    Surface(
                        onClick = onClick,
                        shape = RoundedCornerShape(50), // Fully rounded luxury pill
                        color = CleanWhite,
                        shadowElevation = 3.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = banner.ctaText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepEmerald
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = DeepEmerald,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                // Right Column: Official Spectacles Brand Emblem
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.28f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            RoyolOpticalsLogo(
                                height = 36.dp,
                                tint = accentColor,
                                wordmarkColor = CleanWhite,
                                showWordmark = true
                            )
                        }
                    }
                }
            }
        }
    }
}
