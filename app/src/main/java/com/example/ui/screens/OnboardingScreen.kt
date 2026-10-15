package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FrameCategory
import com.example.model.FrameShape
import com.example.ui.components.EyewearIllustration
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.DeepEmeraldDark
import com.example.ui.theme.TerracottaAccent
import com.example.ui.theme.WarmYellowAccent

@Composable
fun OnboardingScreen(
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepEmerald)
            .testTag("onboarding_screen")
    ) {
        // Full bleed background visual composition with model & eyewear aesthetic
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top section: Big editorial statement "Find Your Signature Frame"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Find Your",
                    fontSize = 46.sp,
                    lineHeight = 50.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CleanWhite,
                    letterSpacing = (-0.5).sp,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Signature",
                    fontSize = 50.sp,
                    lineHeight = 54.sp,
                    fontWeight = FontWeight.Black,
                    color = CleanWhite,
                    letterSpacing = (-1).sp,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Frame",
                    fontSize = 52.sp,
                    lineHeight = 56.sp,
                    fontWeight = FontWeight.Black,
                    color = CleanWhite,
                    letterSpacing = (-1).sp,
                    fontFamily = FontFamily.Serif
                )
            }

            // Center: High-fashion lifestyle portrait composition (Terracotta & Emerald)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(340.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF135C50),
                                TerracottaAccent.copy(alpha = 0.9f),
                                Color(0xFFC74A25)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Background artistic halos
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = WarmYellowAccent.copy(alpha = 0.25f),
                        radius = size.width * 0.45f,
                        center = Offset(size.width * 0.5f, size.height * 0.4f)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Stylized model portrait avatar wearing oversized luxury tinted specs
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE89872)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Curly hair top silhouette
                            Box(
                                modifier = Modifier
                                    .size(60.dp, 30.dp)
                                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                                    .background(Color(0xFF4A1F12))
                            )
                            // Tinted round / aviator frames
                            EyewearIllustration(
                                shape = FrameShape.ROUND,
                                category = FrameCategory.SUNGLASSES,
                                frameColor = WarmYellowAccent,
                                size = 150.dp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            // Terracotta turtleneck collar
                            Box(
                                modifier = Modifier
                                    .size(90.dp, 35.dp)
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                    .background(TerracottaAccent)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = "OPTICS AUTUMN / WINTER '26",
                            color = WarmYellowAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Bottom section: Floating White Pill Button "Continue"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onContinueClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("onboarding_continue_button"),
                    shape = RoundedCornerShape(50), // Fully rounded pill shape
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CleanWhite,
                        contentColor = DeepEmerald
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepEmerald
                    )
                }
            }
        }
    }
}
