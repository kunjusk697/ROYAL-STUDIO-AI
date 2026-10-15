package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdminOrder
import com.example.model.ExpenseItem
import com.example.model.FlashDeal
import com.example.model.FrameCategory
import com.example.model.FrameColor
import com.example.model.FrameShape
import com.example.model.HomeBanner
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.model.Product
import com.example.model.StockItem
import com.example.ui.components.AdminBannersPanel
import com.example.ui.components.EyewearIllustration
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.DeepEmeraldDark
import com.example.ui.theme.DeepEmeraldSurface
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.ForestGreenSurface
import com.example.ui.theme.RedSale
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SoftCardBg
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmYellowAccent
import com.example.ui.theme.WarmYellowLight
import com.example.util.WhatsAppHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// Colors matching React spec: #00473E, #E8B84A, Slate-50, Green-700, Red-600
val RoyalForestGreen = Color(0xFF00473E)
val RoyalWarmGold = Color(0xFFE8B84A)
val SlateBackground = Color(0xFFF8FAFC)
val IncomeGreen = Color(0xFF15803D) // text-green-700
val ExpenseRed = Color(0xFFDC2626)   // text-red-600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    products: List<Product>,
    orders: List<Order>,
    adminOrders: List<AdminOrder>,
    stockItems: List<StockItem>,
    expenseItems: List<ExpenseItem>,
    homeBanners: List<HomeBanner> = emptyList(),
    flashDeal: FlashDeal? = null,
    onAddProduct: (Product) -> Unit,
    onToggleStock: (String) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onResetCatalog: () -> Unit,
    onUpdateOrderStatus: (String, OrderStatus) -> Unit,
    onUpdateAdminOrderStatus: (String, String) -> Unit,
    onAddStockItem: (String, String, Int, Boolean) -> Unit,
    onUpdateStockQty: (Int, Int) -> Unit,
    onToggleStockItemStatus: (Int) -> Unit,
    onDeleteStockItem: (Int) -> Unit,
    onAddExpense: (String, Int, String, String) -> Unit,
    onDeleteExpense: (Int) -> Unit,
    onAddHomeBanner: (HomeBanner) -> Unit = {},
    onUpdateHomeBanner: (HomeBanner) -> Unit = {},
    onDeleteHomeBanner: (String) -> Unit = {},
    onMoveHomeBanner: (String, Int) -> Unit = { _, _ -> },
    onToggleHomeBannerActive: (String) -> Unit = {},
    onResetHomeBanners: () -> Unit = {},
    onUpdateFlashDeal: (FlashDeal) -> Unit = {},
    onToggleFlashDeal: (Boolean) -> Unit = {},
    onBackToStore: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf(0) } // 0: 'orders', 1: 'stock', 2: 'ledger', 3: 'catalog'
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }

    // Ledger Calculations
    val totalIncome = adminOrders.sumOf { it.amount }
    val totalExpense = expenseItems.sumOf { it.amount }
    val netBalance = totalIncome - totalExpense

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .testTag("admin_screen")
    ) {
        // Admin Top Bar: bg-[#00473E] text-[#E8B84A]
        Surface(
            color = RoyalForestGreen,
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Royal Specs Admin",
                        color = RoyalWarmGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Control Center & Ledger",
                        color = CleanWhite.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }

                // "View Store" button
                Button(
                    onClick = onBackToStore,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoyalWarmGold,
                        contentColor = RoyalForestGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_view_store")
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = RoyalForestGreen
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "View Store",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Quick Stats Summary (3-card grid)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Income (വരവ്)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Income (വരവ്)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹$totalIncome",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = IncomeGreen
                    )
                }
            }

            // Expense (ചിലവ്)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Expense (ചിലവ്)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹$totalExpense",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExpenseRed
                    )
                }
            }

            // Net Profit
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = RoyalForestGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Net Profit",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalWarmGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹$netBalance",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalWarmGold
                    )
                }
            }
        }

        // Tabs: Orders | Stock & Inventory | Ledger Entry | Catalog
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = CleanWhite,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = RoyalForestGreen,
                    height = 2.5.dp
                )
            },
            divider = {}
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Text(
                        text = "Orders (${adminOrders.size})",
                        fontSize = 13.sp,
                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == 0) RoyalForestGreen else Color(0xFF6B7280)
                    )
                },
                modifier = Modifier.testTag("admin_tab_orders")
            )

            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Text(
                        text = "Stock & Inventory",
                        fontSize = 13.sp,
                        fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == 1) RoyalForestGreen else Color(0xFF6B7280)
                    )
                },
                modifier = Modifier.testTag("admin_tab_stock")
            )

            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = {
                    Text(
                        text = "Ledger Entry",
                        fontSize = 13.sp,
                        fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == 2) RoyalForestGreen else Color(0xFF6B7280)
                    )
                },
                modifier = Modifier.testTag("admin_tab_ledger")
            )

            Tab(
                selected = activeTab == 3,
                onClick = { activeTab = 3 },
                text = {
                    Text(
                        text = "Banners (${homeBanners.size})",
                        fontSize = 13.sp,
                        fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == 3) RoyalForestGreen else Color(0xFF6B7280)
                    )
                },
                modifier = Modifier.testTag("admin_tab_banners")
            )

            Tab(
                selected = activeTab == 4,
                onClick = { activeTab = 4 },
                text = {
                    Text(
                        text = "Frames Catalog",
                        fontSize = 13.sp,
                        fontWeight = if (activeTab == 4) FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == 4) RoyalForestGreen else Color(0xFF6B7280)
                    )
                },
                modifier = Modifier.testTag("admin_tab_catalog")
            )
        }

        // Tab Panels
        Box(modifier = Modifier.fillMaxSize()) {
            when (activeTab) {
                0 -> OrdersPanel(
                    orders = adminOrders,
                    onUpdateStatus = onUpdateAdminOrderStatus
                )
                1 -> StockPanel(
                    stock = stockItems,
                    onToggleStock = onToggleStockItemStatus,
                    onUpdateQty = onUpdateStockQty,
                    onAddNewStock = { showAddStockDialog = true },
                    onDeleteStock = onDeleteStockItem
                )
                2 -> LedgerPanel(
                    expenses = expenseItems,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    netBalance = netBalance,
                    onAddExpense = onAddExpense,
                    onDeleteExpense = onDeleteExpense
                )
                3 -> AdminBannersPanel(
                    banners = homeBanners,
                    flashDeal = flashDeal,
                    onAddBanner = onAddHomeBanner,
                    onUpdateBanner = onUpdateHomeBanner,
                    onDeleteBanner = onDeleteHomeBanner,
                    onMoveBanner = onMoveHomeBanner,
                    onToggleActive = onToggleHomeBannerActive,
                    onResetDefaults = onResetHomeBanners,
                    onUpdateFlashDeal = onUpdateFlashDeal,
                    onToggleFlashDeal = onToggleFlashDeal
                )
                4 -> AdminCatalogView(
                    products = products,
                    onAddNewClick = { showAddProductDialog = true },
                    onToggleStock = onToggleStock,
                    onDeleteProduct = onDeleteProduct,
                    onResetCatalog = onResetCatalog
                )
            }
        }
    }

    if (showAddStockDialog) {
        AddNewStockDialog(
            onDismiss = { showAddStockDialog = false },
            onSave = { name, cat, qty, inStock ->
                onAddStockItem(name, cat, qty, inStock)
                showAddStockDialog = false
                Toast.makeText(context, "Added '$name' to stock!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddProductDialog) {
        AddNewProductDialog(
            onDismiss = { showAddProductDialog = false },
            onSave = { newProduct ->
                onAddProduct(newProduct)
                showAddProductDialog = false
                Toast.makeText(context, "Added '${newProduct.name}' to catalog!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// -------------------------------------------------------------
// PANEL 1: ORDERS PANEL
// -------------------------------------------------------------
@Composable
private fun OrdersPanel(
    orders: List<AdminOrder>,
    onUpdateStatus: (String, String) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header: ID, Customer, Phone, Amount
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = order.id,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalForestGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = order.customer,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF111827)
                            )
                            Text(
                                text = order.phone,
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${order.amount}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalForestGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val isDelivered = order.status.equals("Delivered", ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isDelivered) Color(0xFFDCFCE7) else Color(0xFFFEF9C3)
                            ) {
                                Text(
                                    text = order.status,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDelivered) Color(0xFF15803D) else Color(0xFF854D0E),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Items Container: bg-gray-50 p-2 rounded text-xs text-gray-600
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF9FAFB),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = order.items,
                            fontSize = 12.sp,
                            color = Color(0xFF4B5563),
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    // Action buttons
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Date: ${order.date}",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // WhatsApp Ping
                            OutlinedButton(
                                onClick = {
                                    val msg = "Hello ${order.customer}, this is Royal Specs regarding your order #${order.id} (${order.items}). Current status: ${order.status}. Total: ₹${order.amount}. Thank you!"
                                    WhatsAppHelper.openWhatsAppChat(context, msg)
                                },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = Color(0xFF25D366),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WhatsApp", fontSize = 11.sp, color = RoyalForestGreen)
                            }

                            // Advance Status Toggle
                            Button(
                                onClick = {
                                    val next = if (order.status.equals("Delivered", ignoreCase = true)) "Processing" else "Delivered"
                                    onUpdateStatus(order.id, next)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalForestGreen),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (order.status.equals("Delivered", ignoreCase = true)) "Set Processing" else "Set Delivered ✓",
                                    fontSize = 11.sp,
                                    color = RoyalWarmGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// PANEL 2: STOCK & INVENTORY PANEL
// -------------------------------------------------------------
@Composable
private fun StockPanel(
    stock: List<StockItem>,
    onToggleStock: (Int) -> Unit,
    onUpdateQty: (Int, Int) -> Unit,
    onAddNewStock: () -> Unit,
    onDeleteStock: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Add Stock Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inventory Items (${stock.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF374151)
                )

                Button(
                    onClick = onAddNewStock,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalForestGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = RoyalWarmGold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Stock Item",
                        fontSize = 11.5.sp,
                        color = RoyalWarmGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(stock, key = { it.id }) { item ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF111827)
                        )
                        Text(
                            text = "Category: ${item.category} • In stock: ${item.qty}",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Qty Adjusters
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                .padding(horizontal = 2.dp)
                        ) {
                            IconButton(
                                onClick = { onUpdateQty(item.id, -1) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Minus",
                                    tint = Color(0xFF374151),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                text = "${item.qty}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827),
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                            IconButton(
                                onClick = { onUpdateQty(item.id, 1) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Plus",
                                    tint = RoyalForestGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Toggle button: Active / Out of Stock
                        Surface(
                            onClick = { onToggleStock(item.id) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (item.inStock) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                            modifier = Modifier.testTag("btn_toggle_stock_${item.id}")
                        ) {
                            Text(
                                text = if (item.inStock) "Active" else "Out of Stock",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.inStock) Color(0xFF15803D) else Color(0xFFDC2626),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        IconButton(
                            onClick = { onDeleteStock(item.id) },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// PANEL 3: LEDGER ENTRY PANEL
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LedgerPanel(
    expenses: List<ExpenseItem>,
    totalIncome: Int,
    totalExpense: Int,
    netBalance: Int,
    onAddExpense: (String, Int, String, String) -> Unit,
    onDeleteExpense: (Int) -> Unit
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Shop") } // 'Shop' | 'Shipping' | 'Stock'
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    val categoryLabels = mapOf(
        "Shop" to "Shop Utility",
        "Shipping" to "Shipping",
        "Stock" to "Stock Purchase"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Record New Expense Form Card: bg-white p-4 rounded-xl border border-gray-100 shadow-sm space-y-3
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Record New Expense (ചിലവ് ചേർക്കുക)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalForestGreen
                    )

                    // Description input
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Expense Description (e.g., Courier fees)", fontSize = 12.sp, color = Color(0xFF9CA3AF)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalForestGreen,
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Row with Amount & Category Dropdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Amount input (50% width)
                        OutlinedTextField(
                            value = amountStr,
                            onValueChange = { amountStr = it },
                            placeholder = { Text("Amount (₹)", fontSize = 12.sp, color = Color(0xFF9CA3AF)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalForestGreen,
                                unfocusedBorderColor = Color(0xFFE5E7EB)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        // Category Dropdown (50% width)
                        ExposedDropdownMenuBox(
                            expanded = isCategoryDropdownExpanded,
                            onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = categoryLabels[category] ?: category,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RoyalForestGreen,
                                    unfocusedBorderColor = Color(0xFFE5E7EB)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = isCategoryDropdownExpanded,
                                onDismissRequest = { isCategoryDropdownExpanded = false }
                            ) {
                                listOf("Shop", "Shipping", "Stock").forEach { catKey ->
                                    DropdownMenuItem(
                                        text = { Text(categoryLabels[catKey] ?: catKey, fontSize = 12.sp) },
                                        onClick = {
                                            category = catKey
                                            isCategoryDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Save Entry button: w-full py-2 bg-[#00473E] text-[#E8B84A] font-bold text-xs rounded-lg shadow
                    Button(
                        onClick = {
                            val parsedAmt = amountStr.toIntOrNull()
                            if (description.isNotBlank() && parsedAmt != null && parsedAmt > 0) {
                                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                onAddExpense(description.trim(), parsedAmt, category, today)
                                description = ""
                                amountStr = ""
                                category = "Shop"
                                Toast.makeText(context, "Expense saved successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Please enter valid description and amount", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoyalForestGreen,
                            contentColor = RoyalWarmGold
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_save_expense")
                    ) {
                        Text(
                            text = "Save Entry",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Recent Expenses Card: bg-white p-4 rounded-xl border border-gray-100 shadow-sm space-y-2
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT EXPENSES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B7280)
                        )

                        // WhatsApp Share
                        IconButton(
                            onClick = {
                                val msg = "Royal Specs Financial Summary:\n" +
                                    "• Income: ₹$totalIncome\n" +
                                    "• Expense: ₹$totalExpense\n" +
                                    "• Net Balance: ₹$netBalance\n" +
                                    "Generated on ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())}."
                                WhatsAppHelper.openWhatsAppChat(context, msg)
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = RoyalForestGreen,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (expenses.isEmpty()) {
                        Text(
                            text = "No expenses recorded yet.",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        expenses.forEachIndexed { index, exp ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = exp.description,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF1F2937)
                                    )
                                    Text(
                                        text = "${exp.category} • ${exp.date}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "-₹${exp.amount}",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ExpenseRed
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { onDeleteExpense(exp.id) },
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFD1D5DB),
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                            if (index < expenses.size - 1) {
                                HorizontalDivider(color = Color(0xFFF9FAFB), thickness = 1.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// PANEL 4: FRAMES CATALOG VIEW
// -------------------------------------------------------------
@Composable
private fun AdminCatalogView(
    products: List<Product>,
    onAddNewClick: () -> Unit,
    onToggleStock: (String) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onResetCatalog: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onAddNewClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoyalForestGreen,
                        contentColor = RoyalWarmGold
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_add_frame_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = RoyalWarmGold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New Frame", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onResetCatalog,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Default", fontSize = 11.5.sp)
                }
            }
        }

        items(products, key = { it.id }) { product ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(0xFFEDF3F1), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        EyewearIllustration(
                            shape = product.shape,
                            category = product.category,
                            frameColor = Color(product.colors.first().colorHex),
                            size = 46.dp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color(0xFF111827)
                        )
                        Text(
                            text = "${product.category.displayName} • ${product.shape.displayName}",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                        Text(
                            text = "Price: ₹${product.basePrice} (MRP: ₹${product.originalPrice})",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RoyalForestGreen
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            onClick = { onToggleStock(product.id) },
                            shape = RoundedCornerShape(6.dp),
                            color = if (product.inStock) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                        ) {
                            Text(
                                text = if (product.inStock) "Active" else "Out of Stock",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (product.inStock) Color(0xFF15803D) else Color(0xFFDC2626),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        IconButton(
                            onClick = { onDeleteProduct(product.id) },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOGS: ADD STOCK & ADD FRAME
// -------------------------------------------------------------
@Composable
private fun AddNewStockDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, category: String, qty: Int, inStock: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Frame") }
    var qtyStr by remember { mutableStateOf("10") }
    var inStock by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Inventory Stock Item", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name * (e.g. Aviator Gold)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Frame", "Lens").forEach { cat ->
                        Surface(
                            modifier = Modifier.clickable { category = cat },
                            shape = RoundedCornerShape(6.dp),
                            color = if (category == cat) RoyalForestGreen else Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (category == cat) RoyalWarmGold else Color(0xFF111827),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = qtyStr,
                    onValueChange = { qtyStr = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Stock Status:", fontSize = 12.sp)
                    Surface(
                        modifier = Modifier.clickable { inStock = !inStock },
                        shape = RoundedCornerShape(6.dp),
                        color = if (inStock) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                    ) {
                        Text(
                            text = if (inStock) "Active ✓" else "Out of Stock ✗",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (inStock) Color(0xFF15803D) else Color(0xFFDC2626),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val q = qtyStr.toIntOrNull() ?: 0
                        onSave(name.trim(), category, q, inStock)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalForestGreen)
            ) {
                Text("Save Item", color = RoyalWarmGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun AddNewProductDialog(
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("Royal Reserve") }
    var category by remember { mutableStateOf(FrameCategory.CLASSIC_EYEGLASSES) }
    var shape by remember { mutableStateOf(FrameShape.ROUND) }
    var basePriceStr by remember { mutableStateOf("1999") }
    var originalPriceStr by remember { mutableStateOf("2999") }
    var material by remember { mutableStateOf("Italian Acetate") }
    var description by remember { mutableStateOf("Handcrafted luxury spectacles frame.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Eyewear Frame",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Frame Model Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = { Text("Brand / Collection") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text("Category:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(FrameCategory.CLASSIC_EYEGLASSES, FrameCategory.SUNGLASSES, FrameCategory.BLUE_CUT).forEach { cat ->
                            Surface(
                                modifier = Modifier.clickable { category = cat },
                                shape = RoundedCornerShape(6.dp),
                                color = if (category == cat) RoyalForestGreen else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = cat.displayName,
                                    fontSize = 10.sp,
                                    color = if (category == cat) RoyalWarmGold else Color(0xFF111827),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    Text("Shape:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(FrameShape.ROUND, FrameShape.AVIATOR, FrameShape.WAYFARER, FrameShape.RECTANGLE).forEach { sh ->
                            Surface(
                                modifier = Modifier.clickable { shape = sh },
                                shape = RoundedCornerShape(6.dp),
                                color = if (shape == sh) RoyalForestGreen else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = sh.displayName,
                                    fontSize = 10.sp,
                                    color = if (shape == sh) RoyalWarmGold else Color(0xFF111827),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = basePriceStr,
                            onValueChange = { basePriceStr = it },
                            label = { Text("Selling Price ₹") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = originalPriceStr,
                            onValueChange = { originalPriceStr = it },
                            label = { Text("MRP ₹") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = material,
                        onValueChange = { material = it },
                        label = { Text("Material") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Short Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val base = basePriceStr.toIntOrNull() ?: 1999
                        val orig = originalPriceStr.toIntOrNull() ?: (base + 1000)
                        val newProduct = Product(
                            id = "RS-CUSTOM-" + UUID.randomUUID().toString().take(6).uppercase(),
                            name = name.trim(),
                            brand = brand.trim(),
                            category = category,
                            shape = shape,
                            basePrice = base,
                            originalPrice = orig,
                            colors = listOf(
                                FrameColor("Classic Black", 0xFF1A1A1A, isDefault = true),
                                FrameColor("Warm Gold", 0xFFE8B84A),
                                FrameColor("Royal Emerald", 0xFF00473E)
                            ),
                            description = description,
                            frameMaterial = material,
                            isNew = true
                        )
                        onSave(newProduct)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalForestGreen)
            ) {
                Text("Add Frame", color = RoyalWarmGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
