package com.example.model

data class CartItem(
    val id: String,
    val product: Product,
    val selectedColor: FrameColor,
    val lensConfig: ConfiguredLens,
    val quantity: Int = 1
) {
    val frameUnitPrice: Int
        get() = product.basePrice

    val lensUnitPrice: Int
        get() = lensConfig.totalLensPrice

    val unitPrice: Int
        get() = frameUnitPrice + lensUnitPrice

    val frameTotalPrice: Int
        get() = frameUnitPrice * quantity

    val lensTotalPrice: Int
        get() = lensUnitPrice * quantity

    val totalPrice: Int
        get() = unitPrice * quantity
}

enum class PaymentMethod(
    val title: String,
    val subtitle: String,
    val tag: String
) {
    CASH_ON_DELIVERY(
        title = "Cash on Delivery (Pay by Cash)",
        subtitle = "Pay securely via cash or doorstep UPI upon delivery",
        tag = "COD"
    ),
    UPI(
        title = "UPI Payment (Google Pay, PhonePe, Paytm, UPI ID)",
        subtitle = "Instant payment via UPI apps or your UPI ID (VPA)",
        tag = "UPI"
    ),
    ONLINE_CARD_NETBANKING(
        title = "Online Card / NetBanking",
        subtitle = "Credit/Debit Cards (Visa, MasterCard, RuPay) & NetBanking",
        tag = "Card/NetBanking"
    ),
    UPI_QR(
        title = "Instant UPI QR Code",
        subtitle = "Scan & pay instantly with Google Pay, PhonePe, Paytm, or BHIM",
        tag = "Instant UPI"
    ),
    WHATSAPP_DIRECT(
        title = "Order via WhatsApp",
        subtitle = "Instant order confirmation & concierge support at +91 99467 32958",
        tag = "WhatsApp"
    )
}

enum class OrderStatus(val label: String, val stepIndex: Int) {
    ORDER_SUBMITTED("Submitted via WhatsApp", 0),
    PAYMENT_CONFIRMED("Payment Confirmed", 1),
    PRESCRIPTION_VERIFYING("Prescription Verified", 2),
    LAB_CRAFTING("In Optical Lab", 3),
    DISPATCHED("Shipped / Out for Delivery", 4),
    DELIVERED("Delivered", 5);

    val isDelivered: Boolean
        get() = this == DELIVERED

    val isShipped: Boolean
        get() = this == DISPATCHED

    val isPending: Boolean
        get() = this != DELIVERED && this != DISPATCHED
}

data class Order(
    val orderId: String,
    val customerName: String,
    val phone: String,
    val address: String,
    val pincode: String,
    val items: List<CartItem>,
    val framesSubtotal: Int,
    val lensSubtotal: Int,
    val taxes: Int,
    val discount: Int,
    val deliveryFee: Int,
    val totalAmount: Int,
    val paymentMethod: PaymentMethod = PaymentMethod.WHATSAPP_DIRECT,
    val upiTransactionRef: String? = null,
    val timestampFormatted: String,
    val status: OrderStatus = OrderStatus.ORDER_SUBMITTED,
    val estimatedDeliveryDate: String = "18 Oct 2026",
    val deliveryCountdownText: String = "Estimated in 3 days",
    val trackingNumber: String = "RS-EXPRESS-9946",
    val courierPartner: String = "Royal Express Delivery",
    val prescriptionPhotoNote: String = "Customer will share photo on WhatsApp"
)
