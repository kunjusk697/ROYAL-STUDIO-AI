package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.DeepEmeraldDark
import com.example.ui.theme.DeepEmeraldSurface
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SoftGoldContainer
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmGoldDark
import com.example.ui.theme.WarmYellowAccent
import com.example.util.WhatsAppHelper

@Composable
fun RoyalHeader(
    cartCount: Int,
    searchQuery: String = "",
    onSearchQueryChanged: (String) -> Unit = {},
    onSearchFocus: () -> Unit = {},
    onCartClick: () -> Unit,
    onAiChatClick: () -> Unit,
    onTryOnClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("royal_header"),
        color = DeepEmerald,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Brand & Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Official "ROYOL OPTICALS" Spectacles Brand Logo (44dp height)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    RoyolOpticalsLogo(
                        height = 44.dp,
                        tint = WarmYellowAccent,
                        wordmarkColor = CleanWhite,
                        showWordmark = true
                    )
                }

                // AI Stylist Concierge button
                Surface(
                    onClick = onAiChatClick,
                    shape = RoundedCornerShape(18.dp),
                    color = SoftGoldContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmGoldDark),
                    modifier = Modifier.testTag("ai_stylist_header_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Stylist",
                            tint = DeepEmeraldDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI Stylist",
                            color = DeepEmeraldDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // WhatsApp quick contact badge button
                Surface(
                    onClick = {
                        WhatsAppHelper.openWhatsAppChat(
                            context = context,
                            message = "Hello Royal Specs / OPTICS, I would like to inquire about frames, lens types, and eye power orders."
                        )
                    },
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF25D366),
                    modifier = Modifier.testTag("whatsapp_header_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "WhatsApp Order",
                            tint = CleanWhite,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Virtual Mirror Try-On Quick Button
                IconButton(
                    onClick = onTryOnClick,
                    modifier = Modifier.size(34.dp).testTag("header_try_on_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Virtual Mirror Try-On",
                        tint = WarmYellowAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(2.dp))

                // Cart badge button
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier.size(36.dp).testTag("cart_header_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
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
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Shopping Cart",
                            tint = CleanWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Top-Level Search Field (Always accessible at top)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    onSearchQueryChanged(it)
                    onSearchFocus()
                },
                placeholder = {
                    Text(
                        text = "Search by name, category, or frame type...",
                        fontSize = 12.sp,
                        color = CleanWhite.copy(alpha = 0.65f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = WarmYellowAccent,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Search",
                                tint = CleanWhite.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("top_level_search_field"),
                shape = RoundedCornerShape(50), // Modern pill shape
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WarmYellowAccent,
                    unfocusedBorderColor = CleanWhite.copy(alpha = 0.25f),
                    focusedContainerColor = DeepEmeraldDark.copy(alpha = 0.6f),
                    unfocusedContainerColor = DeepEmeraldDark.copy(alpha = 0.45f),
                    focusedTextColor = CleanWhite,
                    unfocusedTextColor = CleanWhite,
                    cursorColor = WarmYellowAccent
                )
            )
        }
    }
}
