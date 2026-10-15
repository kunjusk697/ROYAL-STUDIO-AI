package com.example.model

import java.util.UUID

/**
 * Data model for Home Screen hero carousel banners.
 * Supports web URLs, local file URIs, custom color themes, and catalog redirection.
 */
data class HomeBanner(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val subtitle: String,
    val tagText: String = "NEW ARRIVAL",
    val imageUrl: String = "",
    val backgroundColorHex: String = "#00473E",
    val accentColorHex: String = "#E8B84A",
    val ctaText: String = "Shop Now",
    val categoryTarget: FrameCategory = FrameCategory.ALL,
    val order: Int = 0,
    val isActive: Boolean = true
)

object DefaultHomeBanners {
    val initialBanners = listOf(
        HomeBanner(
            id = "banner-1",
            title = "SEE MORE. LIVE MORE.",
            subtitle = "Sun-protective glasses protect your eyes from bright light & UV rays with a stylish detail.",
            tagText = "ROYOL OPTICALS",
            imageUrl = "https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=800&q=80",
            backgroundColorHex = "#00473E",
            accentColorHex = "#E8B84A",
            ctaText = "Shop Now",
            categoryTarget = FrameCategory.SUNGLASSES,
            order = 0,
            isActive = true
        ),
        HomeBanner(
            id = "banner-2",
            title = "STYLISH Eyes.. Rimberio",
            subtitle = "Professional Style Advice & High-Quality Materials for everyday luxury.",
            tagText = "NEW ARRIVAL",
            imageUrl = "https://images.unsplash.com/photo-1572635196237-14b3f281503f?auto=format&fit=crop&w=800&q=80",
            backgroundColorHex = "#8B1E1E",
            accentColorHex = "#FFFFFF",
            ctaText = "Explore Collection",
            categoryTarget = FrameCategory.CLASSIC_EYEGLASSES,
            order = 1,
            isActive = true
        ),
        HomeBanner(
            id = "banner-3",
            title = "AUTUMN / WINTER '26",
            subtitle = "Chic lightweight rose-gold & pastel tinted round frames for effortless elegance.",
            tagText = "TRENDING NOW",
            imageUrl = "https://images.unsplash.com/photo-1508296695146-257a814070b4?auto=format&fit=crop&w=800&q=80",
            backgroundColorHex = "#4A1525",
            accentColorHex = "#F4B400",
            ctaText = "Shop Now",
            categoryTarget = FrameCategory.SUNGLASSES,
            order = 2,
            isActive = true
        ),
        HomeBanner(
            id = "banner-4",
            title = "BLUE-CUT LENS SHIELD",
            subtitle = "Block 99% harmful digital blue light from monitors & phones. Zero eye strain.",
            tagText = "30% OFF COMBO",
            imageUrl = "https://images.unsplash.com/photo-1591076482161-42ce6da69f67?auto=format&fit=crop&w=800&q=80",
            backgroundColorHex = "#09332B",
            accentColorHex = "#E8B84A",
            ctaText = "Configure Lens",
            categoryTarget = FrameCategory.BLUE_CUT,
            order = 3,
            isActive = true
        ),
        HomeBanner(
            id = "banner-5",
            title = "EYE WEAR FOR OCCASIONS",
            subtitle = "Discover stylish, aerospace-grade titanium eyewear. See clearly, look amazing!",
            tagText = "AEROSPACE TITANIUM",
            imageUrl = "https://images.unsplash.com/photo-1577803645773-f96470509666?auto=format&fit=crop&w=800&q=80",
            backgroundColorHex = "#20232A",
            accentColorHex = "#E8B84A",
            ctaText = "Shop Now",
            categoryTarget = FrameCategory.CLASSIC_EYEGLASSES,
            order = 4,
            isActive = true
        )
    )
}
