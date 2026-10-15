package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SampleEyewearData
import com.example.data.StoreRepository
import com.example.model.ConfiguredLens
import com.example.model.LensType
import com.example.model.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Royal Specs", appName)
  }

  @Test
  fun `verify cart pricing and transparent order breakdown`() {
    val repository = StoreRepository()
    val product = SampleEyewearData.defaultProducts[0] // Windsor Round: ₹1899
    val color = product.colors.first()
    val lensConfig = ConfiguredLens(lensType = LensType.ZERO_POWER_BLUE_CUT) // ₹699

    repository.addToCart(product, color, lensConfig, 1)
    val cartItems = repository.cartItems.value
    assertEquals(1, cartItems.size)

    val item = cartItems.first()
    assertEquals(1899, item.frameTotalPrice)
    assertEquals(699, item.lensTotalPrice)
    assertEquals(2598, item.totalPrice)

    val (order, message, total) = repository.placeOrder(
        customerName = "Vikram Sharma",
        phone = "+91 99467 32958",
        address = "14 Marine Drive, Kochi",
        pincode = "682001",
        paymentMethod = PaymentMethod.UPI_QR
    )

    assertNotNull(order)
    assertTrue(message.contains("Vikram Sharma"))
    assertTrue(message.contains("The Windsor Round"))
    assertTrue(message.contains("Zero Power Blue-Cut"))
    assertTrue(message.contains("Instant UPI QR Code"))
    assertTrue(repository.cartItems.value.isEmpty())
  }

  @Test
  fun `verify initial admin data for orders, stock, and expenses`() {
    val repository = StoreRepository()

    // 1. Verify Orders
    val orders = repository.adminOrders.value
    assertEquals(2, orders.size)
    assertEquals("ORD-1001", orders[0].id)
    assertEquals("Rahul K", orders[0].customer)
    assertEquals("+919876543210", orders[0].phone)
    assertEquals(2799, orders[0].amount)
    assertEquals("Delivered", orders[0].status)

    assertEquals("ORD-1002", orders[1].id)
    assertEquals("Ananya M", orders[1].customer)
    assertEquals("Processing", orders[1].status)

    // 2. Verify Stock Inventory
    val stock = repository.stockItems.value
    assertEquals(4, stock.size)
    assertEquals("Classic Aviator Gold", stock[0].name)
    assertEquals("Frame", stock[0].category)
    assertEquals(14, stock[0].qty)
    assertTrue(stock[0].inStock)

    assertEquals("Progressive Sun-Tint", stock[3].name)
    assertEquals(0, stock[3].qty)
    assertTrue(!stock[3].inStock)

    // 3. Verify Expenses & Ledger
    val expenses = repository.expenseItems.value
    assertEquals(2, expenses.size)
    assertEquals("Courier & Packaging", expenses[0].description)
    assertEquals(450, expenses[0].amount)
    assertEquals("Shipping", expenses[0].category)

    assertEquals("Frame Wholesale Restock", expenses[1].description)
    assertEquals(12500, expenses[1].amount)
    assertEquals("Stock", expenses[1].category)

    // Test Stock adjustments
    repository.updateStockQty(1, 2)
    assertEquals(16, repository.stockItems.value.first { it.id == 1 }.qty)

    repository.toggleStockItemStatus(4)
    assertTrue(repository.stockItems.value.first { it.id == 4 }.inStock)

    repository.addExpense("Lab Lens Edger Tooling", 1200, "Lab", "2026-10-16")
    val updatedExpenses = repository.expenseItems.value
    assertEquals(3, updatedExpenses.size)
    assertEquals(1200, updatedExpenses[0].amount)
  }

  @Test
  fun `verify home banner carousel and admin management`() {
    val repository = StoreRepository()

    // 1. Verify default banners loaded
    val initialBanners = repository.homeBanners.value
    assertTrue(initialBanners.size >= 5)
    assertEquals("SEE MORE. LIVE MORE.", initialBanners[0].title)
    assertEquals("STYLISH Eyes.. Rimberio", initialBanners[1].title)
    assertTrue(initialBanners[0].isActive)

    // 2. Add new banner
    val newBanner = com.example.model.HomeBanner(
      id = "test-banner-custom",
      title = "FESTIVE GOLD OFFER",
      subtitle = "Special 40% OFF on all Gold Wire Frames",
      tagText = "LIMITED EDITION",
      imageUrl = "https://example.com/festive.jpg",
      backgroundColorHex = "#00473E",
      accentColorHex = "#E8B84A"
    )
    repository.addHomeBanner(newBanner)
    val afterAdd = repository.homeBanners.value
    assertEquals(initialBanners.size + 1, afterAdd.size)
    assertEquals("FESTIVE GOLD OFFER", afterAdd.last().title)

    // 3. Move banner
    val lastId = afterAdd.last().id
    repository.moveHomeBanner(lastId, -1) // move up
    val afterMove = repository.homeBanners.value
    assertEquals("FESTIVE GOLD OFFER", afterMove[afterMove.size - 2].title)

    // 4. Update banner
    val updatedBanner = afterMove.first { it.id == "test-banner-custom" }.copy(
      title = "FESTIVE GOLD LUXURY 50% OFF"
    )
    repository.updateHomeBanner(updatedBanner)
    assertEquals("FESTIVE GOLD LUXURY 50% OFF", repository.homeBanners.value.first { it.id == "test-banner-custom" }.title)

    // 5. Toggle active
    repository.toggleHomeBannerActive("test-banner-custom")
    assertTrue(!repository.homeBanners.value.first { it.id == "test-banner-custom" }.isActive)

    // 6. Delete banner
    repository.deleteHomeBanner("test-banner-custom")
    assertEquals(initialBanners.size, repository.homeBanners.value.size)
  }

  @Test
  fun `verify flash deal state and coupon claiming`() {
    val repository = StoreRepository()

    // 1. Verify initial flash deal
    val deal = repository.flashDeal.value
    assertTrue(deal.isEnabled)
    assertEquals(25, deal.discountPercent)
    assertEquals("FLASH25", deal.couponCode)

    // 2. Claim flash deal coupon
    val appliedCode = repository.applyFlashDealCoupon()
    assertEquals("FLASH25", appliedCode)
    assertEquals("FLASH25", repository.appliedCoupon.value)

    // 3. Update flash deal in Admin
    val updated = deal.copy(
      discountPercent = 30,
      couponCode = "FLASH30",
      title = "Diwali Mega Eyewear Sale"
    )
    repository.updateFlashDeal(updated)
    assertEquals(30, repository.flashDeal.value.discountPercent)
    assertEquals("FLASH30", repository.flashDeal.value.couponCode)
    assertEquals("Diwali Mega Eyewear Sale", repository.flashDeal.value.title)

    // 4. Toggle flash deal off/on
    repository.toggleFlashDeal(false)
    assertTrue(!repository.flashDeal.value.isEnabled)
    repository.toggleFlashDeal(true)
    assertTrue(repository.flashDeal.value.isEnabled)
  }
}
