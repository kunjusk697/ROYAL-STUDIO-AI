package com.example.data

import com.example.model.AdminOrder
import com.example.model.CartItem
import com.example.model.ConfiguredLens
import com.example.model.DefaultHomeBanners
import com.example.model.ExpenseItem
import com.example.model.FlashDeal
import com.example.model.FrameCategory
import com.example.model.FrameColor
import com.example.model.FrameShape
import com.example.model.HomeBanner
import com.example.model.InitialAdminData
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.PaymentMethod
import com.example.model.Product
import com.example.model.StockItem
import com.example.util.WhatsAppHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class StoreRepository {

    private val _products = MutableStateFlow<List<Product>>(SampleEyewearData.defaultProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _homeBanners = MutableStateFlow<List<HomeBanner>>(DefaultHomeBanners.initialBanners)
    val homeBanners: StateFlow<List<HomeBanner>> = _homeBanners.asStateFlow()

    private val _flashDeal = MutableStateFlow(FlashDeal())
    val flashDeal: StateFlow<FlashDeal> = _flashDeal.asStateFlow()

    private val _adminOrders = MutableStateFlow<List<AdminOrder>>(InitialAdminData.initialOrders)
    val adminOrders: StateFlow<List<AdminOrder>> = _adminOrders.asStateFlow()

    private val _stockItems = MutableStateFlow<List<StockItem>>(InitialAdminData.initialStock)
    val stockItems: StateFlow<List<StockItem>> = _stockItems.asStateFlow()

    private val _expenseItems = MutableStateFlow<List<ExpenseItem>>(InitialAdminData.initialExpenses)
    val expenseItems: StateFlow<List<ExpenseItem>> = _expenseItems.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FrameCategory.ALL)
    val selectedCategory: StateFlow<FrameCategory> = _selectedCategory.asStateFlow()

    private val _selectedShape = MutableStateFlow(FrameShape.ALL)
    val selectedShape: StateFlow<FrameShape> = _selectedShape.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(createInitialSampleOrders())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<String?>(null)
    val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

    private val _configuringProduct = MutableStateFlow<Product?>(null)
    val configuringProduct: StateFlow<Product?> = _configuringProduct.asStateFlow()

    fun selectCategory(category: FrameCategory) {
        _selectedCategory.value = category
    }

    fun selectShape(shape: FrameShape) {
        _selectedShape.value = shape
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openConfigurator(product: Product) {
        _configuringProduct.value = product
    }

    fun closeConfigurator() {
        _configuringProduct.value = null
    }

    fun addToCart(product: Product, color: FrameColor, lensConfig: ConfiguredLens, quantity: Int = 1) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst {
            it.product.id == product.id &&
            it.selectedColor.name == color.name &&
            it.lensConfig.lensType == lensConfig.lensType &&
            it.lensConfig.lensIndex == lensConfig.lensIndex &&
            it.lensConfig.selectedCoatings == lensConfig.selectedCoatings
        }

        if (existingIndex >= 0) {
            val existing = current[existingIndex]
            current[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            val newItem = CartItem(
                id = UUID.randomUUID().toString(),
                product = product,
                selectedColor = color,
                lensConfig = lensConfig,
                quantity = quantity
            )
            current.add(newItem)
        }
        _cartItems.value = current
    }

    fun updateCartQuantity(itemId: String, delta: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.id == itemId }
        if (index >= 0) {
            val updated = current[index].quantity + delta
            if (updated <= 0) {
                current.removeAt(index)
            } else {
                current[index] = current[index].copy(quantity = updated)
            }
            _cartItems.value = current
        }
    }

    fun removeFromCart(itemId: String) {
        _cartItems.value = _cartItems.value.filterNot { it.id == itemId }
    }

    fun applyCoupon(code: String): Pair<Boolean, String> {
        val cleaned = code.trim().uppercase()
        return when (cleaned) {
            "ROYAL10" -> {
                _appliedCoupon.value = cleaned
                Pair(true, "ROYAL10 applied! 10% Royal Privilege discount.")
            }
            "FIRSTSPECS" -> {
                _appliedCoupon.value = cleaned
                Pair(true, "FIRSTSPECS applied! ₹300 welcome discount.")
            }
            "GOLDVIP" -> {
                _appliedCoupon.value = cleaned
                Pair(true, "GOLDVIP applied! ₹500 VIP club discount.")
            }
            else -> {
                Pair(false, "Invalid coupon code. Try 'ROYAL10' or 'FIRSTSPECS'.")
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _appliedCoupon.value = null
    }

    fun placeOrder(
        customerName: String,
        phone: String,
        address: String,
        pincode: String,
        paymentMethod: PaymentMethod = PaymentMethod.WHATSAPP_DIRECT,
        upiTransactionRef: String? = null
    ): Triple<Order, String, Int> {
        val items = _cartItems.value
        val framesSubtotal = items.sumOf { it.frameTotalPrice }
        val lensSubtotal = items.sumOf { it.lensTotalPrice }
        val subtotal = framesSubtotal + lensSubtotal

        val coupon = _appliedCoupon.value
        val discount = calculateDiscount(subtotal, coupon)
        val discountedSubtotal = (subtotal - discount).coerceAtLeast(0)
        val taxes = (discountedSubtotal * 0.12).toInt() // 12% GST on optical frames & lenses
        val deliveryFee = if (subtotal >= 999) 0 else 99
        val totalAmount = maxOf(0, discountedSubtotal + taxes + deliveryFee)

        val orderId = "RS" + (1000 + (Math.random() * 9000).toInt())
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())

        // Calculate estimated delivery: 3 days from now
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 3)
        val estDateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.time)
        val trackNum = "RS-EXP-" + (100000 + (Math.random() * 899999).toInt())

        val order = Order(
            orderId = orderId,
            customerName = customerName,
            phone = phone,
            address = address,
            pincode = pincode,
            items = items,
            framesSubtotal = framesSubtotal,
            lensSubtotal = lensSubtotal,
            taxes = taxes,
            discount = discount,
            deliveryFee = deliveryFee,
            totalAmount = totalAmount,
            paymentMethod = paymentMethod,
            upiTransactionRef = upiTransactionRef,
            timestampFormatted = dateStr,
            status = if (paymentMethod == PaymentMethod.UPI_QR) OrderStatus.PAYMENT_CONFIRMED else OrderStatus.ORDER_SUBMITTED,
            estimatedDeliveryDate = estDateStr,
            deliveryCountdownText = "Estimated in 3 days",
            trackingNumber = trackNum
        )

        val newOrders = _orders.value.toMutableList()
        newOrders.add(0, order)
        _orders.value = newOrders

        // Automatically record in Admin Orders & Ledger
        val adminOrder = AdminOrder(
            id = "ORD-" + (2000 + (Math.random() * 8000).toInt()),
            customer = customerName,
            phone = phone,
            items = items.joinToString { "${it.quantity}x ${it.product.name} (${it.lensConfig.lensType.title})" },
            amount = totalAmount,
            status = if (paymentMethod == PaymentMethod.UPI_QR) "Delivered" else "Processing",
            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
        val updatedAdminOrders = _adminOrders.value.toMutableList()
        updatedAdminOrders.add(0, adminOrder)
        _adminOrders.value = updatedAdminOrders

        val message = WhatsAppHelper.buildOrderMessage(
            orderId = orderId,
            customerName = customerName,
            phone = phone,
            address = address,
            pincode = pincode,
            items = items,
            framesSubtotal = framesSubtotal,
            lensSubtotal = lensSubtotal,
            taxes = taxes,
            discount = discount,
            deliveryFee = deliveryFee,
            total = totalAmount,
            paymentMethod = paymentMethod,
            upiTransactionRef = upiTransactionRef,
            couponCode = coupon
        )

        clearCart()
        return Triple(order, message, totalAmount)
    }

    fun calculateDiscount(subtotal: Int, coupon: String?): Int {
        return when (coupon) {
            "ROYAL10" -> (subtotal * 0.10).toInt()
            "FIRSTSPECS" -> minOf(300, subtotal)
            "GOLDVIP" -> minOf(500, subtotal)
            else -> 0
        }
    }

    // Admin methods
    fun addProduct(product: Product) {
        val list = _products.value.toMutableList()
        list.add(0, product)
        _products.value = list
    }

    fun updateProduct(product: Product) {
        val list = _products.value.toMutableList()
        val index = list.indexOfFirst { it.id == product.id }
        if (index >= 0) {
            list[index] = product
            _products.value = list
        }
    }

    fun toggleStock(productId: String) {
        val list = _products.value.toMutableList()
        val index = list.indexOfFirst { it.id == productId }
        if (index >= 0) {
            val current = list[index]
            list[index] = current.copy(inStock = !current.inStock)
            _products.value = list
        }
    }

    fun deleteProduct(productId: String) {
        _products.value = _products.value.filterNot { it.id == productId }
    }

    fun resetDefaultCatalog() {
        _products.value = SampleEyewearData.defaultProducts
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val list = _orders.value.toMutableList()
        val index = list.indexOfFirst { it.orderId == orderId }
        if (index >= 0) {
            list[index] = list[index].copy(status = newStatus)
            _orders.value = list
        }
    }

    // Admin Orders, Stock & Expense Ledger
    fun updateAdminOrderStatus(orderId: String, newStatus: String) {
        val list = _adminOrders.value.toMutableList()
        val index = list.indexOfFirst { it.id == orderId }
        if (index >= 0) {
            list[index] = list[index].copy(status = newStatus)
            _adminOrders.value = list
        }
    }

    fun addStockItem(name: String, category: String, qty: Int, inStock: Boolean) {
        val current = _stockItems.value.toMutableList()
        val newId = (current.maxOfOrNull { it.id } ?: 0) + 1
        current.add(StockItem(id = newId, name = name, category = category, qty = qty, inStock = inStock))
        _stockItems.value = current
    }

    fun updateStockQty(stockId: Int, delta: Int) {
        val current = _stockItems.value.toMutableList()
        val index = current.indexOfFirst { it.id == stockId }
        if (index >= 0) {
            val old = current[index]
            val newQty = (old.qty + delta).coerceAtLeast(0)
            current[index] = old.copy(qty = newQty, inStock = newQty > 0)
            _stockItems.value = current
        }
    }

    fun toggleStockItemStatus(stockId: Int) {
        val current = _stockItems.value.toMutableList()
        val index = current.indexOfFirst { it.id == stockId }
        if (index >= 0) {
            val old = current[index]
            current[index] = old.copy(inStock = !old.inStock)
            _stockItems.value = current
        }
    }

    fun deleteStockItem(stockId: Int) {
        _stockItems.value = _stockItems.value.filterNot { it.id == stockId }
    }

    fun addExpense(description: String, amount: Int, category: String, date: String) {
        val current = _expenseItems.value.toMutableList()
        val newId = (current.maxOfOrNull { it.id } ?: 0) + 1
        current.add(0, ExpenseItem(id = newId, description = description, amount = amount, category = category, date = date))
        _expenseItems.value = current
    }

    fun deleteExpense(expenseId: Int) {
        _expenseItems.value = _expenseItems.value.filterNot { it.id == expenseId }
    }

    // Home Banner Carousel Management
    fun addHomeBanner(banner: HomeBanner) {
        val list = _homeBanners.value.toMutableList()
        val newOrder = if (list.isEmpty()) 0 else (list.maxOfOrNull { it.order } ?: 0) + 1
        list.add(banner.copy(order = newOrder))
        _homeBanners.value = list
    }

    fun updateHomeBanner(banner: HomeBanner) {
        val list = _homeBanners.value.toMutableList()
        val index = list.indexOfFirst { it.id == banner.id }
        if (index >= 0) {
            list[index] = banner
            _homeBanners.value = list
        }
    }

    fun deleteHomeBanner(bannerId: String) {
        _homeBanners.value = _homeBanners.value.filterNot { it.id == bannerId }
    }

    fun moveHomeBanner(bannerId: String, direction: Int) {
        val list = _homeBanners.value.toMutableList()
        val index = list.indexOfFirst { it.id == bannerId }
        if (index < 0) return
        val targetIndex = index + direction
        if (targetIndex in 0 until list.size) {
            val item = list.removeAt(index)
            list.add(targetIndex, item)
            val updated = list.mapIndexed { idx, b -> b.copy(order = idx) }
            _homeBanners.value = updated
        }
    }

    fun toggleHomeBannerActive(bannerId: String) {
        val list = _homeBanners.value.toMutableList()
        val index = list.indexOfFirst { it.id == bannerId }
        if (index >= 0) {
            list[index] = list[index].copy(isActive = !list[index].isActive)
            _homeBanners.value = list
        }
    }

    fun resetHomeBannersToDefault() {
        _homeBanners.value = DefaultHomeBanners.initialBanners
    }

    // Flash Offer & Deals Management
    fun updateFlashDeal(deal: FlashDeal) {
        _flashDeal.value = deal
    }

    fun toggleFlashDeal(enabled: Boolean) {
        _flashDeal.value = _flashDeal.value.copy(isEnabled = enabled)
    }

    fun applyFlashDealCoupon(): String {
        val code = _flashDeal.value.couponCode
        _appliedCoupon.value = code
        return code
    }

    private fun createInitialSampleOrders(): List<Order> {
        val defaultProd1 = SampleEyewearData.defaultProducts[0]
        val defaultProd2 = SampleEyewearData.defaultProducts[4]
        val defaultProd3 = SampleEyewearData.defaultProducts[7]
        return listOf(
            Order(
                orderId = "RS8842",
                customerName = "Arjun Menon",
                phone = "+91 98451 22390",
                address = "Flat 402, Palm Heights, MG Road, Ernakulam",
                pincode = "682016",
                items = listOf(
                    CartItem(
                        id = "item_1",
                        product = defaultProd1,
                        selectedColor = defaultProd1.colors[0],
                        lensConfig = ConfiguredLens(
                            lensType = com.example.model.LensType.ZERO_POWER_BLUE_CUT
                        ),
                        quantity = 1
                    )
                ),
                framesSubtotal = 1899,
                lensSubtotal = 699,
                taxes = 280,
                discount = 259,
                deliveryFee = 0,
                totalAmount = 2619,
                paymentMethod = PaymentMethod.UPI_QR,
                upiTransactionRef = "UPI-4829103982",
                timestampFormatted = "14 Oct 2026, 04:30 PM",
                status = OrderStatus.LAB_CRAFTING,
                estimatedDeliveryDate = "18 Oct 2026",
                deliveryCountdownText = "Estimated in 3 days",
                trackingNumber = "RS-EXP-482910"
            ),
            Order(
                orderId = "RS8790",
                customerName = "Priya Nambiar",
                phone = "+91 97400 11234",
                address = "12/B Royal Crest, Indiranagar, Bengaluru",
                pincode = "560038",
                items = listOf(
                    CartItem(
                        id = "item_2",
                        product = defaultProd2,
                        selectedColor = defaultProd2.colors[0],
                        lensConfig = ConfiguredLens(
                            lensType = com.example.model.LensType.FRAME_ONLY
                        ),
                        quantity = 1
                    )
                ),
                framesSubtotal = 2699,
                lensSubtotal = 0,
                taxes = 323,
                discount = 0,
                deliveryFee = 0,
                totalAmount = 3022,
                paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
                timestampFormatted = "13 Oct 2026, 11:15 AM",
                status = OrderStatus.DISPATCHED,
                estimatedDeliveryDate = "16 Oct 2026",
                deliveryCountdownText = "Out for delivery tomorrow",
                trackingNumber = "RS-EXP-772911"
            ),
            Order(
                orderId = "RS8612",
                customerName = "Kavita Nair",
                phone = "+91 99460 77812",
                address = "7A, Skyline Horizon, Panampilly Nagar, Kochi",
                pincode = "682036",
                items = listOf(
                    CartItem(
                        id = "item_3",
                        product = defaultProd3,
                        selectedColor = defaultProd3.colors[0],
                        lensConfig = ConfiguredLens(
                            lensType = com.example.model.LensType.BLUE_CUT_SINGLE_VISION
                        ),
                        quantity = 1
                    )
                ),
                framesSubtotal = 1499,
                lensSubtotal = 1299,
                taxes = 335,
                discount = 300,
                deliveryFee = 0,
                totalAmount = 2833,
                paymentMethod = PaymentMethod.WHATSAPP_DIRECT,
                timestampFormatted = "09 Oct 2026, 02:45 PM",
                status = OrderStatus.DELIVERED,
                estimatedDeliveryDate = "12 Oct 2026",
                deliveryCountdownText = "Delivered on 12 Oct 2026",
                trackingNumber = "RS-EXP-319022"
            )
        )
    }
}
