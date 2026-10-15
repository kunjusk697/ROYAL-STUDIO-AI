package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ConfiguredLens
import com.example.model.FrameCategory
import com.example.model.FrameColor
import com.example.model.LensType
import com.example.model.PaymentMethod
import com.example.model.Product
import com.example.ui.AiChatViewModel
import com.example.ui.StoreViewModel
import com.example.ui.components.AiChatDialog
import com.example.ui.components.LensConfiguratorDialog
import com.example.ui.components.RoyalHeader
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.VirtualTryOnScreen
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.DeepEmeraldDark
import com.example.ui.theme.DeepEmeraldSurface
import com.example.ui.theme.RoyalSpecsTheme
import com.example.ui.theme.WarmYellowAccent

enum class RoyalScreen(val title: String, val icon: ImageVector, val tag: String) {
    HOME("Home", Icons.Default.Home, "bottom_nav_home"),
    CATALOG("Search", Icons.Default.Search, "bottom_nav_catalog"),
    CART("Cart", Icons.Default.ShoppingBag, "bottom_nav_cart"),
    ORDERS("Orders", Icons.Default.LocalShipping, "bottom_nav_orders"),
    ADMIN("Profile", Icons.Default.Person, "bottom_nav_admin")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoyalSpecsTheme {
                RoyalSpecsApp()
            }
        }
    }
}

@Composable
fun RoyalSpecsApp(
    viewModel: StoreViewModel = viewModel(),
    aiViewModel: AiChatViewModel = viewModel()
) {
    val context = LocalContext.current
    var showOnboarding by remember { mutableStateOf(false) } // Can be opened or toggled
    var showVirtualTryOn by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf(RoyalScreen.HOME) }
    var isCheckingOut by remember { mutableStateOf(false) }
    var selectedProductForDetail by remember { mutableStateOf<Product?>(null) }
    var showAiChat by remember { mutableStateOf(false) }

    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedShape by viewModel.selectedShape.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartCount by viewModel.cartItemCount.collectAsStateWithLifecycle()
    val framesSubtotal by viewModel.framesSubtotal.collectAsStateWithLifecycle()
    val lensSubtotal by viewModel.lensSubtotal.collectAsStateWithLifecycle()
    val cartSubtotal by viewModel.cartSubtotal.collectAsStateWithLifecycle()
    val appliedCoupon by viewModel.appliedCoupon.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val configuringProduct by viewModel.configuringProduct.collectAsStateWithLifecycle()

    val adminOrders by viewModel.adminOrders.collectAsStateWithLifecycle()
    val stockItems by viewModel.stockItems.collectAsStateWithLifecycle()
    val expenseItems by viewModel.expenseItems.collectAsStateWithLifecycle()
    val homeBanners by viewModel.homeBanners.collectAsStateWithLifecycle()
    val flashDeal by viewModel.flashDeal.collectAsStateWithLifecycle()

    val aiMessages by aiViewModel.messages.collectAsStateWithLifecycle()
    val isAiLoading by aiViewModel.isLoading.collectAsStateWithLifecycle()

    // Handle back button
    if (showVirtualTryOn) {
        BackHandler { showVirtualTryOn = false }
    } else if (selectedProductForDetail != null) {
        BackHandler { selectedProductForDetail = null }
    } else if (isCheckingOut) {
        BackHandler { isCheckingOut = false }
    } else if (currentScreen != RoyalScreen.HOME) {
        BackHandler { currentScreen = RoyalScreen.HOME }
    }

    if (showVirtualTryOn) {
        VirtualTryOnScreen(
            onBack = { showVirtualTryOn = false }
        )
    } else if (showOnboarding) {
        OnboardingScreen(
            onContinueClick = { showOnboarding = false }
        )
    } else if (selectedProductForDetail != null) {
        ProductDetailScreen(
            product = selectedProductForDetail!!,
            onBackClick = { selectedProductForDetail = null },
            onBuyNowClick = { prod, color, lensType, qty ->
                viewModel.addToCart(
                    product = prod,
                    color = color,
                    lensConfig = ConfiguredLens(lensType = lensType),
                    quantity = qty
                )
                selectedProductForDetail = null
                currentScreen = RoyalScreen.CART
            },
            onConfigureLensesClick = { prod ->
                viewModel.openConfigurator(prod)
            },
            onVirtualTryOnClick = {
                showVirtualTryOn = true
            }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (!isCheckingOut) {
                    RoyalHeader(
                        cartCount = cartCount,
                        searchQuery = searchQuery,
                        onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                        onSearchFocus = {
                            if (currentScreen != RoyalScreen.CATALOG) {
                                currentScreen = RoyalScreen.CATALOG
                            }
                        },
                        onCartClick = {
                            isCheckingOut = false
                            currentScreen = RoyalScreen.CART
                        },
                        onAiChatClick = { showAiChat = true },
                        onTryOnClick = { showVirtualTryOn = true }
                    )
                }
            },
            bottomBar = {
                if (!isCheckingOut) {
                    // Floating Dark Emerald Navigation Dock (Exact match from reference image)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(36.dp), // Floating pill dock
                            color = DeepEmerald,
                            shadowElevation = 8.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RoyalScreen.values().forEach { screen ->
                                    val isSelected = currentScreen == screen
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable {
                                                isCheckingOut = false
                                                currentScreen = screen
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                            .testTag(screen.tag)
                                    ) {
                                        if (screen == RoyalScreen.CART && cartCount > 0) {
                                            BadgedBox(
                                                badge = {
                                                    Badge(
                                                        containerColor = WarmYellowAccent,
                                                        contentColor = DeepEmeraldDark
                                                    ) {
                                                        Text(
                                                            text = cartCount.toString(),
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 10.sp
                                                        )
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = screen.icon,
                                                    contentDescription = screen.title,
                                                    tint = if (isSelected) WarmYellowAccent else CleanWhite.copy(alpha = 0.75f),
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        } else {
                                            Icon(
                                                imageVector = screen.icon,
                                                contentDescription = screen.title,
                                                tint = if (isSelected) WarmYellowAccent else CleanWhite.copy(alpha = 0.75f),
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Text(
                                            text = screen.title,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) WarmYellowAccent else CleanWhite.copy(alpha = 0.75f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isCheckingOut) {
                    CheckoutScreen(
                        cartItems = cartItems,
                        framesSubtotal = framesSubtotal,
                        lensSubtotal = lensSubtotal,
                        appliedCoupon = appliedCoupon,
                        onBackToCart = { isCheckingOut = false },
                        onPlaceOrder = { name, phone, address, pincode, method, upiRef ->
                            viewModel.placeOrder(name, phone, address, pincode, method, upiRef)
                        },
                        onOrderFinished = {
                            isCheckingOut = false
                            currentScreen = RoyalScreen.ORDERS
                        }
                    )
                } else {
                    when (currentScreen) {
                        RoyalScreen.HOME -> {
                            HomeScreen(
                                featuredProducts = allProducts,
                                banners = homeBanners,
                                flashDeal = flashDeal,
                                onClaimFlashDeal = { coupon ->
                                    val code = viewModel.claimFlashDeal()
                                    Toast.makeText(context, "Applied $code for ${flashDeal.discountPercent}% OFF!", Toast.LENGTH_SHORT).show()
                                    currentScreen = RoyalScreen.CATALOG
                                },
                                onNavigateToCatalog = { category ->
                                    viewModel.selectCategory(category)
                                    currentScreen = RoyalScreen.CATALOG
                                },
                                onProductClick = { product ->
                                    selectedProductForDetail = product
                                },
                                onConfigureProduct = { product ->
                                    viewModel.openConfigurator(product)
                                },
                                onQuickAddFrame = { product, color ->
                                    viewModel.addToCart(
                                        product = product,
                                        color = color,
                                        lensConfig = ConfiguredLens(lensType = LensType.FRAME_ONLY)
                                    )
                                    currentScreen = RoyalScreen.CART
                                },
                                onOpenVirtualTryOn = { showVirtualTryOn = true }
                            )
                        }

                        RoyalScreen.CATALOG -> {
                            CatalogScreen(
                                products = filteredProducts,
                                selectedCategory = selectedCategory,
                                selectedShape = selectedShape,
                                searchQuery = searchQuery,
                                onCategorySelected = { viewModel.selectCategory(it) },
                                onShapeSelected = { viewModel.selectShape(it) },
                                onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                                onConfigureProduct = { product ->
                                    selectedProductForDetail = product
                                },
                                onQuickAddFrame = { product, color ->
                                    viewModel.addToCart(
                                        product = product,
                                        color = color,
                                        lensConfig = ConfiguredLens(lensType = LensType.FRAME_ONLY)
                                    )
                                    currentScreen = RoyalScreen.CART
                                }
                            )
                        }

                        RoyalScreen.ORDERS -> {
                            OrdersScreen(
                                orders = orders,
                                onReorder = { items ->
                                    items.forEach { itm ->
                                        viewModel.addToCart(
                                            product = itm.product,
                                            color = itm.selectedColor,
                                            lensConfig = itm.lensConfig,
                                            quantity = itm.quantity
                                        )
                                    }
                                    currentScreen = RoyalScreen.CART
                                },
                                onNavigateToCatalog = { currentScreen = RoyalScreen.CATALOG },
                                onSimulateTracking = { id -> viewModel.simulateOrderStatusUpdate(id) }
                            )
                        }

                        RoyalScreen.CART -> {
                            CartScreen(
                                cartItems = cartItems,
                                appliedCoupon = appliedCoupon,
                                framesSubtotal = framesSubtotal,
                                lensSubtotal = lensSubtotal,
                                onQuantityChange = { id, delta -> viewModel.updateCartQuantity(id, delta) },
                                onRemoveItem = { id -> viewModel.removeFromCart(id) },
                                onApplyCoupon = { code -> viewModel.applyCoupon(code) },
                                onRemoveCoupon = { viewModel.removeCoupon() },
                                onProceedToCheckout = { isCheckingOut = true },
                                onNavigateToCatalog = { currentScreen = RoyalScreen.CATALOG }
                            )
                        }

                        RoyalScreen.ADMIN -> {
                            AdminScreen(
                                products = allProducts,
                                orders = orders,
                                adminOrders = adminOrders,
                                stockItems = stockItems,
                                expenseItems = expenseItems,
                                homeBanners = homeBanners,
                                flashDeal = flashDeal,
                                onAddProduct = { viewModel.addProduct(it) },
                                onToggleStock = { viewModel.toggleStock(it) },
                                onDeleteProduct = { viewModel.deleteProduct(it) },
                                onResetCatalog = { viewModel.resetDefaultCatalog() },
                                onUpdateOrderStatus = { id, status -> viewModel.updateOrderStatus(id, status) },
                                onUpdateAdminOrderStatus = { id, status -> viewModel.updateAdminOrderStatus(id, status) },
                                onAddStockItem = { name, cat, qty, inStock -> viewModel.addStockItem(name, cat, qty, inStock) },
                                onUpdateStockQty = { id, delta -> viewModel.updateStockQty(id, delta) },
                                onToggleStockItemStatus = { id -> viewModel.toggleStockItemStatus(id) },
                                onDeleteStockItem = { id -> viewModel.deleteStockItem(id) },
                                onAddExpense = { desc, amt, cat, date -> viewModel.addExpense(desc, amt, cat, date) },
                                onDeleteExpense = { id -> viewModel.deleteExpense(id) },
                                onAddHomeBanner = { viewModel.addHomeBanner(it) },
                                onUpdateHomeBanner = { viewModel.updateHomeBanner(it) },
                                onDeleteHomeBanner = { viewModel.deleteHomeBanner(it) },
                                onMoveHomeBanner = { id, dir -> viewModel.moveHomeBanner(id, dir) },
                                onToggleHomeBannerActive = { viewModel.toggleHomeBannerActive(it) },
                                onResetHomeBanners = { viewModel.resetHomeBannersToDefault() },
                                onUpdateFlashDeal = { viewModel.updateFlashDeal(it) },
                                onToggleFlashDeal = { viewModel.toggleFlashDeal(it) },
                                onBackToStore = { currentScreen = RoyalScreen.HOME }
                            )
                        }
                    }
                }

                // Lens Configurator Dialog Overlay
                configuringProduct?.let { product ->
                    LensConfiguratorDialog(
                        product = product,
                        onDismiss = { viewModel.closeConfigurator() },
                        onAddToCart = { prod, color, lensConfig ->
                            viewModel.addToCart(prod, color, lensConfig)
                            selectedProductForDetail = null
                            isCheckingOut = false
                            currentScreen = RoyalScreen.CART
                        }
                    )
                }

                // Gemini Optical Stylist Chatbot Dialog Overlay
                if (showAiChat) {
                    AiChatDialog(
                        messages = aiMessages,
                        isLoading = isAiLoading,
                        onSendMessage = { aiViewModel.sendMessage(it) },
                        onDismiss = { showAiChat = false }
                    )
                }
            }
        }
    }
}
