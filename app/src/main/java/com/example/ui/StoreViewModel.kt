package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.StoreRepository
import com.example.model.AdminOrder
import com.example.model.CartItem
import com.example.model.ConfiguredLens
import com.example.model.ExpenseItem
import com.example.model.FlashDeal
import com.example.model.FrameCategory
import com.example.model.FrameColor
import com.example.model.FrameShape
import com.example.model.HomeBanner
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.PaymentMethod
import com.example.model.Product
import com.example.model.StockItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class StoreViewModel(
    private val repository: StoreRepository = StoreRepository()
) : ViewModel() {

    val selectedCategory: StateFlow<FrameCategory> = repository.selectedCategory
    val selectedShape: StateFlow<FrameShape> = repository.selectedShape
    val searchQuery: StateFlow<String> = repository.searchQuery
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
    val orders: StateFlow<List<Order>> = repository.orders
    val appliedCoupon: StateFlow<String?> = repository.appliedCoupon
    val configuringProduct: StateFlow<Product?> = repository.configuringProduct
    val allProducts: StateFlow<List<Product>> = repository.products
    val homeBanners: StateFlow<List<HomeBanner>> = repository.homeBanners
    val flashDeal: StateFlow<FlashDeal> = repository.flashDeal

    val adminOrders: StateFlow<List<AdminOrder>> = repository.adminOrders
    val stockItems: StateFlow<List<StockItem>> = repository.stockItems
    val expenseItems: StateFlow<List<ExpenseItem>> = repository.expenseItems

    val filteredProducts: StateFlow<List<Product>> = combine(
        repository.products,
        repository.selectedCategory,
        repository.selectedShape,
        repository.searchQuery
    ) { products, category, shape, query ->
        products.filter { product ->
            val matchesCategory = (category == FrameCategory.ALL) || (product.category == category)
            val matchesShape = (shape == FrameShape.ALL) || (product.shape == shape)
            val matchesQuery = query.isBlank() ||
                product.name.contains(query, ignoreCase = true) ||
                product.category.displayName.contains(query, ignoreCase = true) ||
                product.shape.displayName.contains(query, ignoreCase = true) ||
                product.brand.contains(query, ignoreCase = true) ||
                product.frameMaterial.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true)

            matchesCategory && matchesShape && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.products.value)

    val cartItemCount: StateFlow<Int> = combine(repository.cartItems) { itemsArray ->
        itemsArray[0].sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val framesSubtotal: StateFlow<Int> = combine(repository.cartItems) { itemsArray ->
        itemsArray[0].sumOf { it.frameTotalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val lensSubtotal: StateFlow<Int> = combine(repository.cartItems) { itemsArray ->
        itemsArray[0].sumOf { it.lensTotalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotal: StateFlow<Int> = combine(repository.cartItems) { itemsArray ->
        itemsArray[0].sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun selectCategory(category: FrameCategory) = repository.selectCategory(category)
    fun selectShape(shape: FrameShape) = repository.selectShape(shape)
    fun setSearchQuery(query: String) = repository.setSearchQuery(query)

    fun openConfigurator(product: Product) = repository.openConfigurator(product)
    fun closeConfigurator() = repository.closeConfigurator()

    fun addToCart(product: Product, color: FrameColor, lensConfig: ConfiguredLens, quantity: Int = 1) {
        repository.addToCart(product, color, lensConfig, quantity)
        repository.closeConfigurator()
    }

    fun updateCartQuantity(itemId: String, delta: Int) = repository.updateCartQuantity(itemId, delta)
    fun removeFromCart(itemId: String) = repository.removeFromCart(itemId)
    fun applyCoupon(code: String): Pair<Boolean, String> = repository.applyCoupon(code)
    fun removeCoupon() = repository.removeCoupon()
    fun clearCart() = repository.clearCart()

    fun calculateDiscount(subtotal: Int, coupon: String?): Int = repository.calculateDiscount(subtotal, coupon)

    fun placeOrder(
        customerName: String,
        phone: String,
        address: String,
        pincode: String,
        paymentMethod: PaymentMethod = PaymentMethod.WHATSAPP_DIRECT,
        upiTransactionRef: String? = null
    ): Triple<Order, String, Int> {
        return repository.placeOrder(
            customerName = customerName,
            phone = phone,
            address = address,
            pincode = pincode,
            paymentMethod = paymentMethod,
            upiTransactionRef = upiTransactionRef
        )
    }

    // Admin API
    fun addProduct(product: Product) = repository.addProduct(product)
    fun updateProduct(product: Product) = repository.updateProduct(product)
    fun toggleStock(productId: String) = repository.toggleStock(productId)
    fun deleteProduct(productId: String) = repository.deleteProduct(productId)
    fun resetDefaultCatalog() = repository.resetDefaultCatalog()
    fun updateOrderStatus(orderId: String, status: OrderStatus) = repository.updateOrderStatus(orderId, status)

    // Admin Orders, Stock & Ledger API
    fun updateAdminOrderStatus(orderId: String, status: String) = repository.updateAdminOrderStatus(orderId, status)
    fun addStockItem(name: String, category: String, qty: Int, inStock: Boolean) = repository.addStockItem(name, category, qty, inStock)
    fun updateStockQty(stockId: Int, delta: Int) = repository.updateStockQty(stockId, delta)
    fun toggleStockItemStatus(stockId: Int) = repository.toggleStockItemStatus(stockId)
    fun deleteStockItem(stockId: Int) = repository.deleteStockItem(stockId)
    fun addExpense(description: String, amount: Int, category: String, date: String) = repository.addExpense(description, amount, category, date)
    fun deleteExpense(expenseId: Int) = repository.deleteExpense(expenseId)

    // Admin Banner / Slideshow Management API
    fun addHomeBanner(banner: HomeBanner) = repository.addHomeBanner(banner)
    fun updateHomeBanner(banner: HomeBanner) = repository.updateHomeBanner(banner)
    fun deleteHomeBanner(bannerId: String) = repository.deleteHomeBanner(bannerId)
    fun moveHomeBanner(bannerId: String, direction: Int) = repository.moveHomeBanner(bannerId, direction)
    fun toggleHomeBannerActive(bannerId: String) = repository.toggleHomeBannerActive(bannerId)
    fun resetHomeBannersToDefault() = repository.resetHomeBannersToDefault()

    // Flash Offer & Deals API
    fun updateFlashDeal(deal: FlashDeal) = repository.updateFlashDeal(deal)
    fun toggleFlashDeal(enabled: Boolean) = repository.toggleFlashDeal(enabled)
    fun claimFlashDeal(): String = repository.applyFlashDealCoupon()

    fun simulateOrderStatusUpdate(orderId: String) {
        viewModelScope.launch {
            delay(30_000)
            repository.updateOrderStatus(orderId, OrderStatus.DISPATCHED)
        }
    }
}
