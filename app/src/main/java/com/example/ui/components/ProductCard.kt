package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConfiguredLens
import com.example.model.FrameColor
import com.example.model.LensType
import com.example.model.Product
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.RedSale
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SoftGoldContainer
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmGoldDark

@Composable
fun ProductCard(
    product: Product,
    onConfigureClick: (Product) -> Unit,
    onQuickAddFrame: (Product, FrameColor) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedColor by remember(product.id) {
        mutableStateOf(product.colors.firstOrNull { it.isDefault } ?: product.colors.first())
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CleanWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Top badges (Bestseller / New / Discount)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (product.isBestSeller) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SoftGoldContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = WarmGoldDark,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "BESTSELLER",
                                    color = ForestGreenDark,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    } else if (product.isNew) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ForestGreenPrimary
                        ) {
                            Text(
                                text = "NEW ARRIVAL",
                                color = CleanWhite,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (product.discountPercent > 0) {
                    Text(
                        text = "${product.discountPercent}% OFF",
                        color = RedSale,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Eyewear vector illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color(0xFFF9FAF9), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                EyewearIllustration(
                    shape = product.shape,
                    category = product.category,
                    frameColor = Color(selectedColor.colorHex),
                    size = 140.dp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Color Swatches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                product.colors.forEach { colorVariant ->
                    val isSelected = selectedColor.name == colorVariant.name
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(colorVariant.colorHex))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) WarmGold else BorderLight,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = colorVariant }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = selectedColor.name,
                    fontSize = 11.sp,
                    color = SlateMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Brand & Product Title
            Text(
                text = product.brand.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = WarmGoldDark,
                letterSpacing = 0.8.sp
            )
            Text(
                text = product.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CharcoalText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Shape and dimensions info
            Text(
                text = "${product.shape.displayName} • ${product.dimensions}",
                fontSize = 11.sp,
                color = SlateMuted
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Rating & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = WarmGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${product.rating} (${product.reviewsCount})",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CharcoalText
                    )
                }

                // Price in INR (₹)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "₹${product.basePrice}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ForestGreenPrimary
                    )
                    if (product.originalPrice > product.basePrice) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "₹${product.originalPrice}",
                            fontSize = 12.sp,
                            color = SlateMuted,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions: Configure Lenses (Primary) & Quick Add Frame (Secondary)
            Button(
                onClick = { onConfigureClick(product) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("configure_lenses_button_${product.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestGreenPrimary,
                    contentColor = CleanWhite
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = WarmGold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Configure Lenses",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedButton(
                onClick = { onQuickAddFrame(product, selectedColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .testTag("quick_add_frame_${product.id}"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreenPrimary)
            ) {
                Text(
                    text = "Add Frame Only (₹${product.basePrice})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
