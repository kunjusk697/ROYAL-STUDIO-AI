package com.example.model

enum class FrameCategory(val displayName: String) {
    ALL("All Specs"),
    CLASSIC_EYEGLASSES("Classic Eyeglasses"),
    SUNGLASSES("Sunglasses"),
    BLUE_CUT("Blue-Cut")
}

enum class FrameShape(val displayName: String) {
    ALL("All Shapes"),
    ROUND("Round"),
    AVIATOR("Aviator"),
    WAYFARER("Wayfarer"),
    CAT_EYE("Cat-Eye"),
    RECTANGLE("Rectangle"),
    RIMLESS("Rimless")
}

enum class FrameGender(val displayName: String) {
    UNISEX("Unisex"),
    MEN("Men"),
    WOMEN("Women")
}

data class FrameColor(
    val name: String,
    val colorHex: Long,
    val isDefault: Boolean = false
)

data class Product(
    val id: String,
    val name: String,
    val brand: String = "Royal Specs Reserve",
    val category: FrameCategory,
    val shape: FrameShape,
    val gender: FrameGender = FrameGender.UNISEX,
    val basePrice: Int, // in INR (₹)
    val originalPrice: Int, // in INR (₹)
    val rating: Float = 4.8f,
    val reviewsCount: Int = 124,
    val colors: List<FrameColor>,
    val description: String,
    val frameMaterial: String = "Handcrafted Italian Acetate",
    val dimensions: String = "52-18-140 mm (Medium)",
    val inStock: Boolean = true,
    val isBestSeller: Boolean = false,
    val isNew: Boolean = false
) {
    val discountPercent: Int
        get() = if (originalPrice > basePrice) {
            (((originalPrice - basePrice).toDouble() / originalPrice) * 100).toInt()
        } else 0
}
