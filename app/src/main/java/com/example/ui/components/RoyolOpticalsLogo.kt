package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.WarmYellowAccent

/**
 * Official "ROYOL OPTICALS" Brand Logo
 * Recreates the exact spectacles-integrated lettering where:
 * - 'R': Left temple & stem + upper loop connecting to the brow line
 * - First 'O': Left spectacles lens rim
 * - 'Y': Bridge & nose arch connecting the two eye rims
 * - Second 'O': Right spectacles lens rim
 * - 'L': Right stem & horizontal foot + right temple hinge
 * - Below: "OPTICALS" in clean, bold, spaced modern sans-serif typography.
 */
@Composable
fun RoyolOpticalsLogo(
    modifier: Modifier = Modifier,
    tint: Color = WarmYellowAccent,
    height: Dp = 44.dp,
    showWordmark: Boolean = true,
    wordmarkColor: Color = tint
) {
    // Width-to-height ratio for the spectacles portion is ~ 3.1 : 1
    val spectaclesHeight = if (showWordmark) height * 0.70f else height
    val spectaclesWidth = spectaclesHeight * 3.1f
    val fontSize = (height.value * 0.26f).coerceIn(9f, 22f).sp
    val letterSpacing = (height.value * 0.10f).coerceIn(2.5f, 7f).sp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. Spectacles Frame Logo with "ROYOL" integrated
        Canvas(
            modifier = Modifier
                .width(spectaclesWidth)
                .height(spectaclesHeight)
        ) {
            val w = size.width
            val h = size.height
            val strokeThick = w * 0.038f
            val strokeMedium = w * 0.032f

            // --- A. Left Temple Hinge Protrusion ---
            val leftHinge = Path().apply {
                moveTo(w * 0.045f, h * 0.20f)
                lineTo(w * 0.080f, h * 0.20f)
                lineTo(w * 0.080f, h * 0.44f)
                lineTo(w * 0.045f, h * 0.36f)
                close()
            }
            drawPath(path = leftHinge, color = tint, style = Fill)

            // Top Left Connecting Temple Bar
            drawLine(
                color = tint,
                start = Offset(w * 0.075f, h * 0.26f),
                end = Offset(w * 0.25f, h * 0.26f),
                strokeWidth = strokeThick,
                cap = StrokeCap.Square
            )

            // --- B. Letter 'R' ---
            // R's vertical stem
            drawLine(
                color = tint,
                start = Offset(w * 0.098f, h * 0.20f),
                end = Offset(w * 0.098f, h * 0.82f),
                strokeWidth = strokeThick,
                cap = StrokeCap.Round
            )

            // R's upper bowl
            val rBowl = Path().apply {
                moveTo(w * 0.098f, h * 0.22f)
                cubicTo(
                    w * 0.22f, h * 0.20f,
                    w * 0.22f, h * 0.52f,
                    w * 0.098f, h * 0.52f
                )
            }
            drawPath(
                path = rBowl,
                color = tint,
                style = Stroke(width = strokeThick, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // R's diagonal kick leg
            drawLine(
                color = tint,
                start = Offset(w * 0.125f, h * 0.50f),
                end = Offset(w * 0.235f, h * 0.82f),
                strokeWidth = strokeThick,
                cap = StrokeCap.Round
            )

            // --- C. Left 'O' (Left Spectacles Rim) ---
            // Squarish-rounded eyewear rim
            val leftRimOuter = RoundRect(
                rect = androidx.compose.ui.geometry.Rect(
                    left = w * 0.255f,
                    top = h * 0.16f,
                    right = w * 0.465f,
                    bottom = h * 0.84f
                ),
                cornerRadius = CornerRadius(w * 0.055f, h * 0.22f)
            )
            val leftRimPath = Path().apply {
                addRoundRect(leftRimOuter)
            }
            drawPath(
                path = leftRimPath,
                color = tint,
                style = Stroke(width = strokeThick, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // --- D. Letter 'Y' (The Bridge & Nose Arch) ---
            // Left upper arm of 'Y' (from top of left rim)
            val yLeftArm = Path().apply {
                moveTo(w * 0.448f, h * 0.17f)
                lineTo(w * 0.500f, h * 0.38f)
            }
            drawPath(
                path = yLeftArm,
                color = tint,
                style = Stroke(width = strokeThick, cap = StrokeCap.Round)
            )

            // Right upper arm of 'Y' (from top of right rim)
            val yRightArm = Path().apply {
                moveTo(w * 0.552f, h * 0.17f)
                lineTo(w * 0.500f, h * 0.38f)
            }
            drawPath(
                path = yRightArm,
                color = tint,
                style = Stroke(width = strokeThick, cap = StrokeCap.Round)
            )

            // Arched nose bridge connecting rims under the Y's upper V
            val bridgeArch = Path().apply {
                moveTo(w * 0.460f, h * 0.38f)
                quadraticBezierTo(
                    w * 0.500f, h * 0.30f,
                    w * 0.540f, h * 0.38f
                )
            }
            drawPath(
                path = bridgeArch,
                color = tint,
                style = Stroke(width = strokeMedium, cap = StrokeCap.Round)
            )

            // Y's vertical center stem extending down between lenses
            drawLine(
                color = tint,
                start = Offset(w * 0.500f, h * 0.38f),
                end = Offset(w * 0.500f, h * 0.84f),
                strokeWidth = strokeThick,
                cap = StrokeCap.Round
            )

            // --- E. Right 'O' (Right Spectacles Rim) ---
            // Symmetrical to left rim
            val rightRimOuter = RoundRect(
                rect = androidx.compose.ui.geometry.Rect(
                    left = w * 0.535f,
                    top = h * 0.16f,
                    right = w * 0.745f,
                    bottom = h * 0.84f
                ),
                cornerRadius = CornerRadius(w * 0.055f, h * 0.22f)
            )
            val rightRimPath = Path().apply {
                addRoundRect(rightRimOuter)
            }
            drawPath(
                path = rightRimPath,
                color = tint,
                style = Stroke(width = strokeThick, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // --- F. Letter 'L' & Right Temple ---
            // Top Right Connecting Temple Bar
            drawLine(
                color = tint,
                start = Offset(w * 0.740f, h * 0.26f),
                end = Offset(w * 0.925f, h * 0.26f),
                strokeWidth = strokeThick,
                cap = StrokeCap.Square
            )

            // Right Temple Hinge Protrusion
            val rightHinge = Path().apply {
                moveTo(w * 0.955f, h * 0.20f)
                lineTo(w * 0.920f, h * 0.20f)
                lineTo(w * 0.920f, h * 0.44f)
                lineTo(w * 0.955f, h * 0.36f)
                close()
            }
            drawPath(path = rightHinge, color = tint, style = Fill)

            // L's vertical stem
            drawLine(
                color = tint,
                start = Offset(w * 0.820f, h * 0.20f),
                end = Offset(w * 0.820f, h * 0.82f),
                strokeWidth = strokeThick,
                cap = StrokeCap.Round
            )

            // L's bottom horizontal foot
            drawLine(
                color = tint,
                start = Offset(w * 0.820f, h * 0.82f),
                end = Offset(w * 0.940f, h * 0.82f),
                strokeWidth = strokeThick,
                cap = StrokeCap.Round
            )
        }

        // 2. "O P T I C A L S" Wordmark Text
        if (showWordmark) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "OPTICALS",
                color = wordmarkColor,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                letterSpacing = letterSpacing,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}
