package com.example.model

/**
 * Data model for the compact Home Screen "Flash Offers & Deals" promotional card.
 */
data class FlashDeal(
    val isEnabled: Boolean = true,
    val badgeText: String = "⚡ FLASH SALE",
    val title: String = "Royal Opticals — Stylish Eyewear",
    val subtitle: String = "Limited-Time Special on Luxury Frames & Tinted Lenses",
    val discountPercent: Int = 25,
    val couponCode: String = "FLASH25",
    val ctaText: String = "Claim 25% OFF",
    val imageUrl: String = "https://images.unsplash.com/photo-1508296695146-257a814070b4?auto=format&fit=crop&w=800&q=80",
    val backgroundColorHex: String = "#00473E",
    val accentColorHex: String = "#E8B84A",
    val targetCategory: FrameCategory = FrameCategory.ALL
)
