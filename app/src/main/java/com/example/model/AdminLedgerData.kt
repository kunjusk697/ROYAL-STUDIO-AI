package com.example.model

data class AdminOrder(
    val id: String,
    val customer: String,
    val phone: String,
    val items: String,
    val amount: Int,
    val status: String,
    val date: String
)

data class StockItem(
    val id: Int,
    val name: String,
    val category: String, // "Frame" or "Lens"
    val qty: Int,
    val inStock: Boolean = qty > 0
)

data class ExpenseItem(
    val id: Int,
    val description: String,
    val amount: Int,
    val category: String, // "Shipping", "Stock", "Lab", "Utilities", "Marketing", etc.
    val date: String
)

object InitialAdminData {
    val initialOrders = listOf(
        AdminOrder(
            id = "ORD-1001",
            customer = "Rahul K",
            phone = "+919876543210",
            items = "Classic Aviator Gold (Blue-Cut Lens)",
            amount = 2799,
            status = "Delivered",
            date = "2026-10-14"
        ),
        AdminOrder(
            id = "ORD-1002",
            customer = "Ananya M",
            phone = "+919447123456",
            items = "Cat-Eye Titanium (Zero Power)",
            amount = 2200,
            status = "Processing",
            date = "2026-10-15"
        )
    )

    val initialStock = listOf(
        StockItem(id = 1, name = "Classic Aviator Gold", category = "Frame", qty = 14, inStock = true),
        StockItem(id = 2, name = "Matte Black Wayfarer", category = "Frame", qty = 3, inStock = true),
        StockItem(id = 3, name = "Blue-Cut Lens (0.00)", category = "Lens", qty = 25, inStock = true),
        StockItem(id = 4, name = "Progressive Sun-Tint", category = "Lens", qty = 0, inStock = false)
    )

    val initialExpenses = listOf(
        ExpenseItem(id = 1, description = "Courier & Packaging", amount = 450, category = "Shipping", date = "2026-10-15"),
        ExpenseItem(id = 2, description = "Frame Wholesale Restock", amount = 12500, category = "Stock", date = "2026-10-13")
    )
}
