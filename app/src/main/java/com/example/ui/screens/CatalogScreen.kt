package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FrameCategory
import com.example.model.FrameColor
import com.example.model.FrameShape
import com.example.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.DeepEmeraldDark
import com.example.ui.theme.DeepEmeraldSurface
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SoftCardBg
import com.example.ui.theme.WarmYellowAccent
import com.example.ui.theme.WarmYellowLight

@Composable
fun CatalogScreen(
    products: List<Product>,
    selectedCategory: FrameCategory,
    selectedShape: FrameShape,
    searchQuery: String,
    onCategorySelected: (FrameCategory) -> Unit,
    onShapeSelected: (FrameShape) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onConfigureProduct: (Product) -> Unit,
    onQuickAddFrame: (Product, FrameColor) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        FrameCategory.ALL,
        FrameCategory.CLASSIC_EYEGLASSES,
        FrameCategory.SUNGLASSES,
        FrameCategory.BLUE_CUT
    )

    val shapes = listOf(
        FrameShape.ALL,
        FrameShape.ROUND,
        FrameShape.AVIATOR,
        FrameShape.WAYFARER,
        FrameShape.CAT_EYE,
        FrameShape.RECTANGLE,
        FrameShape.RIMLESS
    )

    val popularSearchKeywords = listOf("Aviator", "Blue-Cut", "Cat-Eye", "Titanium", "Round", "Wayfarer", "Acetate")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CleanWhite)
            .testTag("catalog_screen")
    ) {
        // Top Search & Category Filter Section
        Surface(
            color = CleanWhite,
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)) {
                // Secondary In-Catalog Search Bar for convenience
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = {
                            Text(
                                "Filter by name, category, or frame type...",
                                fontSize = 12.5.sp,
                                color = SlateMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = DeepEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChanged("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = SlateMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("catalog_search_input"),
                        shape = RoundedCornerShape(50), // Pill search bar
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepEmerald,
                            unfocusedBorderColor = BorderLight,
                            focusedContainerColor = SoftCardBg,
                            unfocusedContainerColor = SoftCardBg
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Keyword Suggestion Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(popularSearchKeywords) { keyword ->
                        val isMatched = searchQuery.equals(keyword, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isMatched) DeepEmerald else SoftCardBg,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isMatched) DeepEmerald else BorderLight
                            ),
                            modifier = Modifier.clickable {
                                if (isMatched) onSearchQueryChanged("") else onSearchQueryChanged(keyword)
                            }
                        ) {
                            Text(
                                text = keyword,
                                fontSize = 11.sp,
                                fontWeight = if (isMatched) FontWeight.Bold else FontWeight.Medium,
                                color = if (isMatched) CleanWhite else CharcoalText,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 1. Category Selector Tabs (All, Classic, Sunglasses, Blue-Cut)
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                    edgePadding = 16.dp,
                    containerColor = CleanWhite,
                    indicator = { tabPositions ->
                        val index = categories.indexOf(selectedCategory).coerceAtLeast(0)
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                            color = DeepEmerald,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Tab(
                            selected = isSelected,
                            onClick = { onCategorySelected(cat) },
                            text = {
                                Text(
                                    text = cat.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (isSelected) DeepEmerald else SlateMuted
                                )
                            },
                            modifier = Modifier.testTag("catalog_tab_${cat.name}")
                        )
                    }
                }
            }
        }

        // 2. Frame Type / Shape Selector Filter Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(CleanWhite)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = DeepEmerald,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Frame Type:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepEmerald
                    )
                }
            }

            items(shapes) { shape ->
                val isSelected = selectedShape == shape
                FilterChip(
                    selected = isSelected,
                    onClick = { onShapeSelected(shape) },
                    label = {
                        Text(
                            text = shape.displayName,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepEmerald,
                        selectedLabelColor = CleanWhite,
                        containerColor = SoftCardBg,
                        labelColor = CharcoalText
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) DeepEmerald else BorderLight
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.testTag("shape_chip_${shape.name}")
                )
            }
        }

        // Active Search & Filter Result Banner
        val hasActiveFilters = searchQuery.isNotEmpty() || selectedCategory != FrameCategory.ALL || selectedShape != FrameShape.ALL

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${products.size} Frames Found",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText
                )
                if (searchQuery.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = WarmYellowLight
                    ) {
                        Text(
                            text = "“$searchQuery”",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepEmerald,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (hasActiveFilters) {
                Text(
                    text = "Clear All Filters",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepEmerald,
                    modifier = Modifier.clickable {
                        onCategorySelected(FrameCategory.ALL)
                        onShapeSelected(FrameShape.ALL)
                        onSearchQueryChanged("")
                    }
                )
            }
        }

        // Products List or Empty Search Results
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftCardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(CleanWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = DeepEmerald,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No Eyewear Found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No frames matched your search \"$searchQuery\". Try checking the spelling or resetting category and frame type filters.",
                            fontSize = 12.5.sp,
                            color = SlateMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                onCategorySelected(FrameCategory.ALL)
                                onShapeSelected(FrameShape.ALL)
                                onSearchQueryChanged("")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepEmerald,
                                contentColor = CleanWhite
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Reset All Filters", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onConfigureClick = onConfigureProduct,
                        onQuickAddFrame = onQuickAddFrame
                    )
                }
            }
        }
    }
}
