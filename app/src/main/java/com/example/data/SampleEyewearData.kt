package com.example.data

import com.example.model.FrameCategory
import com.example.model.FrameColor
import com.example.model.FrameGender
import com.example.model.FrameShape
import com.example.model.Product

object SampleEyewearData {

    val sampleColors = listOf(
        FrameColor("Classic Black", 0xFF1A1A1A, isDefault = true),
        FrameColor("Royal Emerald", 0xFF00473E),
        FrameColor("Warm Gold", 0xFFE8B84A),
        FrameColor("Tortoise Shell", 0xFF4A2F1D),
        FrameColor("Gunmetal Grey", 0xFF4E5754)
    )

    val defaultProducts = listOf(
        // Classic Eyeglasses
        Product(
            id = "RS-CLS-01",
            name = "The Windsor Round",
            brand = "Royal Heritage",
            category = FrameCategory.CLASSIC_EYEGLASSES,
            shape = FrameShape.ROUND,
            gender = FrameGender.UNISEX,
            basePrice = 1899,
            originalPrice = 2999,
            rating = 4.9f,
            reviewsCount = 312,
            colors = listOf(
                FrameColor("Tortoise Shell", 0xFF4A2F1D, isDefault = true),
                FrameColor("Warm Gold", 0xFFE8B84A),
                FrameColor("Classic Black", 0xFF1A1A1A)
            ),
            description = "Timeless round silhouette featuring vintage keyhole nose-bridge and hand-polished Italian acetate rims with gold temple cores.",
            frameMaterial = "Handcrafted Italian Acetate",
            dimensions = "49-20-145 mm (Medium)",
            isBestSeller = true,
            isNew = false
        ),
        Product(
            id = "RS-CLS-02",
            name = "Regal Sovereign Wayfarer",
            brand = "Royal Reserve",
            category = FrameCategory.CLASSIC_EYEGLASSES,
            shape = FrameShape.WAYFARER,
            gender = FrameGender.MEN,
            basePrice = 1999,
            originalPrice = 3299,
            rating = 4.8f,
            reviewsCount = 184,
            colors = listOf(
                FrameColor("Classic Black", 0xFF1A1A1A, isDefault = true),
                FrameColor("Royal Emerald", 0xFF00473E),
                FrameColor("Gunmetal Grey", 0xFF4E5754)
            ),
            description = "Bold, authoritative wayfarer geometry crafted with sculpted acetate, dual 5-barrel German hinges, and deep gloss finish.",
            frameMaterial = "Reinforced Mazzucchelli Acetate",
            dimensions = "53-18-142 mm (Wide)",
            isBestSeller = false,
            isNew = true
        ),
        Product(
            id = "RS-CLS-03",
            name = "Monarch Browline Club",
            brand = "Royal Heritage",
            category = FrameCategory.CLASSIC_EYEGLASSES,
            shape = FrameShape.RECTANGLE,
            gender = FrameGender.UNISEX,
            basePrice = 2499,
            originalPrice = 3999,
            rating = 4.9f,
            reviewsCount = 205,
            colors = listOf(
                FrameColor("Warm Gold & Black", 0xFFE8B84A, isDefault = true),
                FrameColor("Gunmetal & Smoke", 0xFF4E5754)
            ),
            description = "Distinguished 1950s inspired clubmaster browline with brushed 18K warm gold metal rims and adjustable titanium silicone nose pads.",
            frameMaterial = "Titanium Alloy + Acetate Brow",
            dimensions = "51-21-140 mm (Medium)",
            isBestSeller = true,
            isNew = false
        ),
        Product(
            id = "RS-CLS-04",
            name = "Kensington Titanium Rimless",
            brand = "Royal Atelier",
            category = FrameCategory.CLASSIC_EYEGLASSES,
            shape = FrameShape.RIMLESS,
            gender = FrameGender.UNISEX,
            basePrice = 2899,
            originalPrice = 4500,
            rating = 4.9f,
            reviewsCount = 92,
            colors = listOf(
                FrameColor("Warm Gold", 0xFFE8B84A, isDefault = true),
                FrameColor("Platinum Silver", 0xFFB4C2BC)
            ),
            description = "Ultra-minimalist 7.8-gram frameless design. Precision laser-drilled mounts, flex temples that wrap seamlessly without ear pressure.",
            frameMaterial = "Beta-Titanium 7.8g",
            dimensions = "53-17-145 mm (Adjustable)",
            isBestSeller = false,
            isNew = true
        ),

        // Sunglasses
        Product(
            id = "RS-SUN-01",
            name = "Crown Aviator Polarized",
            brand = "Royal Reserve",
            category = FrameCategory.SUNGLASSES,
            shape = FrameShape.AVIATOR,
            gender = FrameGender.UNISEX,
            basePrice = 2699,
            originalPrice = 4200,
            rating = 5.0f,
            reviewsCount = 440,
            colors = listOf(
                FrameColor("Warm Gold / Green Lens", 0xFFE8B84A, isDefault = true),
                FrameColor("Gunmetal / Smoke Lens", 0xFF4E5754),
                FrameColor("Rose Gold / Amber Lens", 0xFFD4961D)
            ),
            description = "The quintessential high-flying pilot aviator with double bridge, 100% UV400 HD polarized lenses, and anti-glare hydrophobic coating.",
            frameMaterial = "Aviation-Grade Monel Metal",
            dimensions = "58-14-140 mm (Large)",
            isBestSeller = true,
            isNew = false
        ),
        Product(
            id = "RS-SUN-02",
            name = "Riviera Classic Sun",
            brand = "Royal Heritage",
            category = FrameCategory.SUNGLASSES,
            shape = FrameShape.WAYFARER,
            gender = FrameGender.UNISEX,
            basePrice = 2199,
            originalPrice = 3500,
            rating = 4.7f,
            reviewsCount = 158,
            colors = listOf(
                FrameColor("Tortoise Shell", 0xFF4A2F1D, isDefault = true),
                FrameColor("Jet Black", 0xFF1A1A1A),
                FrameColor("Royal Emerald", 0xFF00473E)
            ),
            description = "Effortless coastal European glamour. Deep polarized lenses that eliminate harsh glare on water and road tarmac.",
            frameMaterial = "Gloss Italian Acetate",
            dimensions = "52-19-145 mm (Medium)",
            isBestSeller = false,
            isNew = false
        ),
        Product(
            id = "RS-SUN-03",
            name = "Duchess Cat-Eye Luxe",
            brand = "Royal Atelier",
            category = FrameCategory.SUNGLASSES,
            shape = FrameShape.CAT_EYE,
            gender = FrameGender.WOMEN,
            basePrice = 2499,
            originalPrice = 3800,
            rating = 4.9f,
            reviewsCount = 276,
            colors = listOf(
                FrameColor("Classic Black / Gold Tip", 0xFF1A1A1A, isDefault = true),
                FrameColor("Ruby Tortoise", 0xFF6B2D1B)
            ),
            description = "Sensational upturned cat-eye architecture with hand-carved chamfered borders and gradient UV400 sun protection lenses.",
            frameMaterial = "Sculpted Bio-Acetate",
            dimensions = "54-17-140 mm (Medium)",
            isBestSeller = true,
            isNew = true
        ),

        // Blue-Cut Lenses
        Product(
            id = "RS-BLU-01",
            name = "CyberShield Blue-Cut Pro",
            brand = "Royal OptiTech",
            category = FrameCategory.BLUE_CUT,
            shape = FrameShape.RECTANGLE,
            gender = FrameGender.UNISEX,
            basePrice = 1499,
            originalPrice = 2499,
            rating = 4.9f,
            reviewsCount = 520,
            colors = listOf(
                FrameColor("Matte Black", 0xFF1A1A1A, isDefault = true),
                FrameColor("Royal Emerald", 0xFF00473E),
                FrameColor("Steel Grey", 0xFF4E5754)
            ),
            description = "Engineered for software developers, designers, and gamers. Premium blue-light filter lenses with anti-reflective AR coating.",
            frameMaterial = "TR90 Ultra-Flex Polymer (12g)",
            dimensions = "53-17-142 mm (Medium)",
            isBestSeller = true,
            isNew = false
        ),
        Product(
            id = "RS-BLU-02",
            name = "Vanguard Round Blue-Guard",
            brand = "Royal OptiTech",
            category = FrameCategory.BLUE_CUT,
            shape = FrameShape.ROUND,
            gender = FrameGender.UNISEX,
            basePrice = 1699,
            originalPrice = 2699,
            rating = 4.8f,
            reviewsCount = 190,
            colors = listOf(
                FrameColor("Warm Gold Rim", 0xFFE8B84A, isDefault = true),
                FrameColor("Gloss Black", 0xFF1A1A1A),
                FrameColor("Gunmetal Grey", 0xFF4E5754)
            ),
            description = "Chic round spectacles fitted with medical-grade blue light absorbing resin that combats digital eye strain and headache triggers.",
            frameMaterial = "Stainless Steel + Acetate Tips",
            dimensions = "50-20-145 mm (Medium)",
            isBestSeller = false,
            isNew = true
        ),
        Product(
            id = "RS-BLU-03",
            name = "AeroLite Titanium Blue-Screen",
            brand = "Royal Atelier",
            category = FrameCategory.BLUE_CUT,
            shape = FrameShape.WAYFARER,
            gender = FrameGender.UNISEX,
            basePrice = 2399,
            originalPrice = 3800,
            rating = 4.9f,
            reviewsCount = 145,
            colors = listOf(
                FrameColor("Midnight Black", 0xFF1A1A1A, isDefault = true),
                FrameColor("Royal Forest Green", 0xFF00473E)
            ),
            description = "Zero-fatigue all-day screen specs with memory-metal hinges and clear high-transmission blue-violet blocking lenses.",
            frameMaterial = "Japanese Beta-Titanium (9g)",
            dimensions = "52-18-142 mm (Medium)",
            isBestSeller = false,
            isNew = false
        )
    )
}
