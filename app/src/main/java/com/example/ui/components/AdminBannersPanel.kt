package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.FlashDeal
import com.example.model.FrameCategory
import com.example.model.HomeBanner
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.DeepEmeraldDark
import com.example.ui.theme.RedSale
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SoftCardBg
import com.example.ui.theme.WarmYellowAccent
import java.util.UUID

@Composable
fun AdminBannersPanel(
    banners: List<HomeBanner>,
    flashDeal: FlashDeal? = null,
    onAddBanner: (HomeBanner) -> Unit,
    onUpdateBanner: (HomeBanner) -> Unit,
    onDeleteBanner: (String) -> Unit,
    onMoveBanner: (String, Int) -> Unit,
    onToggleActive: (String) -> Unit,
    onResetDefaults: () -> Unit,
    onUpdateFlashDeal: (FlashDeal) -> Unit = {},
    onToggleFlashDeal: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var showFlashDealDialog by remember { mutableStateOf(false) }
    var editingBanner by remember { mutableStateOf<HomeBanner?>(null) }
    var bannerToDelete by remember { mutableStateOf<HomeBanner?>(null) }

    val sortedBanners = remember(banners) { banners.sortedBy { it.order } }
    val activeCount = remember(banners) { banners.count { it.isActive } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_banners_panel")
    ) {
        // Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Slides", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${banners.size}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DeepEmerald)
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Active in Home", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("$activeCount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                }
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Auto-Play", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("4.2s Loop", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE8B84A))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Flash Offers & Deals Manager Card
        flashDeal?.let { deal ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CleanWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8B84A).copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_flash_deal_manager_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE8B84A)
                            ) {
                                Text(
                                    text = "⚡ FLASH DEAL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF00473E),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${deal.discountPercent}% OFF • ${deal.couponCode}",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        // ON/OFF Switch
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (deal.isEnabled) "ACTIVE" else "OFF",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (deal.isEnabled) Color(0xFF15803D) else SlateMuted,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Switch(
                                checked = deal.isEnabled,
                                onCheckedChange = { onToggleFlashDeal(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CleanWhite,
                                    checkedTrackColor = Color(0xFF00473E),
                                    uncheckedThumbColor = CleanWhite,
                                    uncheckedTrackColor = Color.LightGray
                                ),
                                modifier = Modifier.size(width = 36.dp, height = 24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${deal.title} — ${deal.subtitle}",
                        fontSize = 11.5.sp,
                        color = SlateMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showFlashDealDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00473E),
                                contentColor = Color(0xFFE8B84A)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 5.dp),
                            modifier = Modifier.testTag("btn_edit_flash_deal")
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Flash Deal Settings", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Action Bar: Add New Banner + Reset Defaults
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    editingBanner = null
                    showDialog = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00473E),
                    contentColor = Color(0xFFE8B84A)
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier.testTag("btn_add_banner")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add New Slide", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onResetDefaults,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = SlateMuted)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset Posters", fontSize = 11.5.sp, color = SlateMuted)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Banner List
        if (sortedBanners.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No banners configured. Click 'Add New Slide' or 'Reset Posters'.",
                    color = SlateMuted,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                itemsIndexed(sortedBanners, key = { _, b -> b.id }) { index, banner ->
                    BannerAdminItem(
                        banner = banner,
                        index = index,
                        totalCount = sortedBanners.size,
                        onMoveUp = { onMoveBanner(banner.id, -1) },
                        onMoveDown = { onMoveBanner(banner.id, 1) },
                        onEdit = {
                            editingBanner = banner
                            showDialog = true
                        },
                        onToggleActive = { onToggleActive(banner.id) },
                        onDelete = { bannerToDelete = banner }
                    )
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showDialog) {
        BannerEditDialog(
            banner = editingBanner,
            onDismiss = {
                showDialog = false
                editingBanner = null
            },
            onSave = { updated ->
                if (editingBanner != null) {
                    onUpdateBanner(updated)
                } else {
                    onAddBanner(updated)
                }
                showDialog = false
                editingBanner = null
            }
        )
    }

    // Flash Deal Edit Dialog
    if (showFlashDealDialog && flashDeal != null) {
        FlashDealEditDialog(
            deal = flashDeal,
            onDismiss = { showFlashDealDialog = false },
            onSave = { updatedDeal ->
                onUpdateFlashDeal(updatedDeal)
                showFlashDealDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (bannerToDelete != null) {
        AlertDialog(
            onDismissRequest = { bannerToDelete = null },
            title = { Text("Delete Banner Slide?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove '${bannerToDelete?.title}' from the Home slideshow?") },
            confirmButton = {
                Button(
                    onClick = {
                        bannerToDelete?.let { onDeleteBanner(it.id) }
                        bannerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedSale)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { bannerToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BannerAdminItem(
    banner: HomeBanner,
    index: Int,
    totalCount: Int,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onEdit: () -> Unit,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    val bgColor = remember(banner.backgroundColorHex) {
        try {
            Color(android.graphics.Color.parseColor(banner.backgroundColorHex))
        } catch (e: Exception) {
            Color(0xFF00473E)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_banner_item_${banner.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reorder buttons & Position badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    IconButton(
                        onClick = onMoveUp,
                        enabled = index > 0,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Move Up",
                            tint = if (index > 0) Color(0xFF00473E) else Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE8B84A),
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00473E)
                            )
                        }
                    }

                    IconButton(
                        onClick = onMoveDown,
                        enabled = index < totalCount - 1,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Move Down",
                            tint = if (index < totalCount - 1) Color(0xFF00473E) else Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Thumbnail preview
                Box(
                    modifier = Modifier
                        .size(width = 80.dp, height = 54.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (banner.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = banner.imageUrl,
                            contentDescription = banner.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        RoyolOpticalsLogo(
                            height = 20.dp,
                            tint = Color(0xFFE8B84A),
                            showWordmark = false
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Title, Subtitle & Badges
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (banner.tagText.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFE8B84A).copy(alpha = 0.25f),
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text(
                                    text = banner.tagText.uppercase(),
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8B6800),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = banner.title,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = banner.subtitle,
                        fontSize = 11.sp,
                        color = SlateMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = "Target: ${banner.categoryTarget.displayName}",
                                fontSize = 9.5.sp,
                                color = Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }

                        // Color swatch
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(bgColor)
                                .border(1.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
                        )
                    }
                }

                // Action buttons: Active Switch, Edit, Delete
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Switch(
                        checked = banner.isActive,
                        onCheckedChange = { onToggleActive() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CleanWhite,
                            checkedTrackColor = Color(0xFF00473E),
                            uncheckedThumbColor = CleanWhite,
                            uncheckedTrackColor = Color.LightGray
                        ),
                        modifier = Modifier.size(width = 36.dp, height = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Slide", tint = DeepEmerald, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Slide", tint = RedSale, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BannerEditDialog(
    banner: HomeBanner?,
    onDismiss: () -> Unit,
    onSave: (HomeBanner) -> Unit
) {
    var title by remember { mutableStateOf(banner?.title ?: "") }
    var subtitle by remember { mutableStateOf(banner?.subtitle ?: "") }
    var tagText by remember { mutableStateOf(banner?.tagText ?: "NEW ARRIVAL") }
    var imageUrl by remember { mutableStateOf(banner?.imageUrl ?: "") }
    var ctaText by remember { mutableStateOf(banner?.ctaText ?: "Shop Now") }
    var selectedCategory by remember { mutableStateOf(banner?.categoryTarget ?: FrameCategory.ALL) }
    var selectedColorHex by remember { mutableStateOf(banner?.backgroundColorHex ?: "#00473E") }
    var selectedAccentHex by remember { mutableStateOf(banner?.accentColorHex ?: "#E8B84A") }
    var isActive by remember { mutableStateOf(banner?.isActive ?: true) }

    // Android zero-permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUrl = uri.toString()
        }
    }

    val presetColors = listOf(
        "#00473E" to "Deep Forest Green",
        "#8B1E1E" to "Rimberio Red",
        "#4A1525" to "Studio Rose",
        "#09332B" to "Blue-Cut Teal",
        "#20232A" to "Titanium Charcoal",
        "#111827" to "Midnight Black"
    )

    val presetImages = listOf(
        "Green Sun-Specs" to "https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=800&q=80",
        "Red Acetate" to "https://images.unsplash.com/photo-1572635196237-14b3f281503f?auto=format&fit=crop&w=800&q=80",
        "Round Fashion" to "https://images.unsplash.com/photo-1508296695146-257a814070b4?auto=format&fit=crop&w=800&q=80",
        "Digital Blue" to "https://images.unsplash.com/photo-1591076482161-42ce6da69f67?auto=format&fit=crop&w=800&q=80"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (banner == null) "Add Banner Slide" else "Edit Banner Slide",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF00473E)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Banner Headline / Title") },
                    placeholder = { Text("e.g., STYLISH Eyes.. Rimberio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Subtitle
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Subtitle / Details") },
                    placeholder = { Text("e.g., Professional Style Advice & High-Quality Materials") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // Tag & CTA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = tagText,
                        onValueChange = { tagText = it },
                        label = { Text("Tag Badge") },
                        placeholder = { Text("NEW ARRIVAL") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = ctaText,
                        onValueChange = { ctaText = it },
                        label = { Text("Button CTA") },
                        placeholder = { Text("Shop Now") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Image Source Section
                Text("Image Source", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))

                // Button to launch Photo Picker
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE8B84A),
                        contentColor = Color(0xFF00473E)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Select Local Photo from Device", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }

                // Image URL input
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Or Image URL (Web / Local URI)") },
                    placeholder = { Text("https://... or content://...") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick image presets
                Text("Quick Preset Images:", fontSize = 11.sp, color = SlateMuted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetImages.forEach { (name, url) ->
                        Surface(
                            onClick = { imageUrl = url },
                            shape = RoundedCornerShape(6.dp),
                            color = if (imageUrl == url) Color(0xFFE8B84A) else Color(0xFFF1F5F9),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (imageUrl == url) Color(0xFF00473E) else Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Background Color Selection
                Text("Theme Background Color", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetColors.forEach { (hex, _) ->
                        val col = Color(android.graphics.Color.parseColor(hex))
                        val isSelected = selectedColorHex.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(col)
                                .clickable { selectedColorHex = hex }
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color(0xFFE8B84A) else Color.LightGray,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CleanWhite, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Target Category
                Text("Target Frame Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        FrameCategory.ALL,
                        FrameCategory.SUNGLASSES,
                        FrameCategory.CLASSIC_EYEGLASSES,
                        FrameCategory.BLUE_CUT
                    ).forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            onClick = { selectedCategory = cat },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) Color(0xFF00473E) else Color(0xFFF1F5F9),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = cat.displayName,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFFE8B84A) else Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val updated = banner?.copy(
                            title = title,
                            subtitle = subtitle,
                            tagText = tagText,
                            imageUrl = imageUrl,
                            ctaText = ctaText,
                            backgroundColorHex = selectedColorHex,
                            accentColorHex = selectedAccentHex,
                            categoryTarget = selectedCategory,
                            isActive = isActive
                        ) ?: HomeBanner(
                            id = "banner-${UUID.randomUUID()}",
                            title = title,
                            subtitle = subtitle,
                            tagText = tagText,
                            imageUrl = imageUrl,
                            ctaText = ctaText,
                            backgroundColorHex = selectedColorHex,
                            accentColorHex = selectedAccentHex,
                            categoryTarget = selectedCategory,
                            isActive = isActive
                        )
                        onSave(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00473E),
                    contentColor = Color(0xFFE8B84A)
                ),
                enabled = title.isNotBlank()
            ) {
                Text(if (banner == null) "Add Slide" else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun FlashDealEditDialog(
    deal: FlashDeal,
    onDismiss: () -> Unit,
    onSave: (FlashDeal) -> Unit
) {
    var isEnabled by remember { mutableStateOf(deal.isEnabled) }
    var discountPercent by remember { mutableIntStateOf(deal.discountPercent) }
    var title by remember { mutableStateOf(deal.title) }
    var subtitle by remember { mutableStateOf(deal.subtitle) }
    var couponCode by remember { mutableStateOf(deal.couponCode) }
    var imageUrl by remember { mutableStateOf(deal.imageUrl) }
    var selectedColorHex by remember { mutableStateOf(deal.backgroundColorHex) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUrl = uri.toString()
        }
    }

    val presetColors = listOf(
        "#00473E" to "Deep Forest Green",
        "#8B1E1E" to "Ruby Crimson",
        "#1E2925" to "Dark Onyx",
        "#09332B" to "Teal Green",
        "#20232A" to "Titanium Charcoal"
    )

    val dealPresets = listOf(
        "3 Luxury Frames" to "https://images.unsplash.com/photo-1508296695146-257a814070b4?auto=format&fit=crop&w=800&q=80",
        "RE Pilot Aviator" to "https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=800&q=80",
        "Square Metal Eyewear" to "https://images.unsplash.com/photo-1577803645773-f96470509666?auto=format&fit=crop&w=800&q=80"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Flash Offer Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF00473E)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Active toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Flash Offer Status", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isEnabled) "ACTIVE ON HOME" else "OFF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isEnabled) Color(0xFF15803D) else SlateMuted,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { isEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CleanWhite,
                                checkedTrackColor = Color(0xFF00473E)
                            )
                        )
                    }
                }

                // Discount Percent Selection
                Text("Discount Percentage", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(15, 20, 25, 30, 40, 50).forEach { pct ->
                        val isSel = discountPercent == pct
                        Surface(
                            onClick = {
                                discountPercent = pct
                                couponCode = "FLASH$pct"
                            },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) Color(0xFFE8B84A) else Color(0xFFF1F5F9),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
                                Text(
                                    text = "$pct%",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color(0xFF00473E) else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                // Title & Coupon
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Offer Headline") },
                    placeholder = { Text("e.g. Royal Opticals — Stylish Eyewear") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Offer Subtitle") },
                    placeholder = { Text("e.g. Limited-Time Festival Special") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = couponCode,
                    onValueChange = { couponCode = it.uppercase() },
                    label = { Text("Coupon Code (Auto-Applied)") },
                    placeholder = { Text("e.g. FLASH25") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Image Source
                Text("Promo Image Source", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))

                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE8B84A),
                        contentColor = Color(0xFF00473E)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Select Photo from Gallery", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Or Image URL (Web / Local URI)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // Preset Images
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    dealPresets.forEach { (name, url) ->
                        Surface(
                            onClick = { imageUrl = url },
                            shape = RoundedCornerShape(6.dp),
                            color = if (imageUrl == url) Color(0xFFE8B84A) else Color(0xFFF1F5F9),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = name,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (imageUrl == url) Color(0xFF00473E) else Color(0xFF475569),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Theme color
                Text("Card Theme Color", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetColors.forEach { (hex, _) ->
                        val col = Color(android.graphics.Color.parseColor(hex))
                        val isSel = selectedColorHex.equals(hex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(col)
                                .clickable { selectedColorHex = hex }
                                .border(
                                    width = if (isSel) 3.dp else 1.dp,
                                    color = if (isSel) Color(0xFFE8B84A) else Color.LightGray,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSel) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CleanWhite, modifier = Modifier.size(15.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        deal.copy(
                            isEnabled = isEnabled,
                            discountPercent = discountPercent,
                            title = title,
                            subtitle = subtitle,
                            couponCode = couponCode,
                            imageUrl = imageUrl,
                            backgroundColorHex = selectedColorHex,
                            ctaText = "Claim $discountPercent% OFF"
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00473E),
                    contentColor = Color(0xFFE8B84A)
                )
            ) {
                Text("Save Flash Deal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
