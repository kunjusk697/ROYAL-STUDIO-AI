package com.example.model

enum class LensType(
    val title: String,
    val subtitle: String,
    val priceInr: Int,
    val isBlueCutIncluded: Boolean = false
) {
    FRAME_ONLY(
        title = "Frame Only",
        subtitle = "No prescription lenses. Comes with demo display lenses.",
        priceInr = 0
    ),
    ZERO_POWER_BLUE_CUT(
        title = "Zero Power Blue-Cut (Screen Shield)",
        subtitle = "Zero prescription. Blocks 98% harmful digital blue light & glare.",
        priceInr = 699,
        isBlueCutIncluded = true
    ),
    SINGLE_VISION(
        title = "Single Vision (Distance or Reading)",
        subtitle = "Anti-reflective & scratch-resistant corrective prescription lenses.",
        priceInr = 899
    ),
    BLUE_CUT_SINGLE_VISION(
        title = "Blue-Cut + Single Vision",
        subtitle = "Complete digital protection with your custom power prescription.",
        priceInr = 1299,
        isBlueCutIncluded = true
    ),
    PROGRESSIVE_MULTIFOCAL(
        title = "Progressive / Multifocal (Zero Line)",
        subtitle = "Seamless distance, intermediate computer & near reading in one lens.",
        priceInr = 2499
    ),
    PHOTOCHROMIC_TRANSITION(
        title = "Photochromic Transitions",
        subtitle = "Smart light-reactive: crystal clear indoors, darkens into sunglasses outdoors.",
        priceInr = 1999
    )
}

enum class LensIndex(
    val title: String,
    val thicknessDescription: String,
    val priceInr: Int
) {
    INDEX_156(
        title = "Standard 1.56 Index",
        thicknessDescription = "Standard thickness. Ideal for power up to ±2.00 SPH.",
        priceInr = 0
    ),
    INDEX_161(
        title = "Thin 1.61 High-Index",
        thicknessDescription = "30% thinner and lighter. Ideal for power ±2.25 to ±4.50 SPH.",
        priceInr = 699
    ),
    INDEX_167(
        title = "Ultra-Thin 1.67 High-Index",
        thicknessDescription = "45% ultra-slim profile. Best for high power above ±4.50 SPH.",
        priceInr = 1499
    )
}

enum class LensCoating(
    val title: String,
    val description: String,
    val priceInr: Int
) {
    HYDROPHOBIC(
        title = "Hydrophobic & Oleophobic Shield",
        description = "Water-repellent, anti-smudge, and dust-resistant coating.",
        priceInr = 299
    ),
    NIGHT_DRIVING(
        title = "DriveSafe Night Vision Glare Shield",
        description = "Reduces blinding headlight reflections for nighttime driving.",
        priceInr = 499
    )
}

enum class PrescriptionMethod(val title: String) {
    WHATSAPP_PHOTO("Send Prescription photo on WhatsApp after ordering"),
    MANUAL_ENTRY("Enter Eye Power Manually")
}

data class EyePrescription(
    val sph: String = "0.00",
    val cyl: String = "0.00",
    val axis: String = "0",
    val add: String = "0.00"
)

data class PrescriptionDetails(
    val rightEyeOD: EyePrescription = EyePrescription(),
    val leftEyeOS: EyePrescription = EyePrescription(),
    val pupillaryDistanceMm: String = "63"
)

data class ConfiguredLens(
    val lensType: LensType = LensType.ZERO_POWER_BLUE_CUT,
    val lensIndex: LensIndex = LensIndex.INDEX_156,
    val selectedCoatings: Set<LensCoating> = emptySet(),
    val prescriptionMethod: PrescriptionMethod = PrescriptionMethod.WHATSAPP_PHOTO,
    val prescriptionDetails: PrescriptionDetails = PrescriptionDetails()
) {
    val totalLensPrice: Int
        get() = lensType.priceInr + lensIndex.priceInr + selectedCoatings.sumOf { it.priceInr }
}
