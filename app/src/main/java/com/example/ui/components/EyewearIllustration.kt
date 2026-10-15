package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.FrameCategory
import com.example.model.FrameShape
import com.example.ui.theme.BlueCutLensTint
import com.example.ui.theme.SunLensDark
import com.example.ui.theme.WarmGold

@Composable
fun EyewearIllustration(
    shape: FrameShape,
    category: FrameCategory,
    frameColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val strokeWidth = when (shape) {
                FrameShape.RIMLESS -> 3f
                FrameShape.AVIATOR -> 4.5f
                else -> 8f
            }

            drawEyewear(
                w = w,
                h = h,
                shape = shape,
                category = category,
                frameColor = frameColor,
                strokeWidth = strokeWidth
            )
        }
    }
}

private fun DrawScope.drawEyewear(
    w: Float,
    h: Float,
    shape: FrameShape,
    category: FrameCategory,
    frameColor: Color,
    strokeWidth: Float
) {
    val centerY = h * 0.5f
    val lensWidth = w * 0.36f
    val lensHeight = when (shape) {
        FrameShape.AVIATOR -> h * 0.44f
        FrameShape.ROUND -> w * 0.34f
        FrameShape.RECTANGLE -> h * 0.32f
        FrameShape.CAT_EYE -> h * 0.38f
        else -> h * 0.38f
    }

    val leftLensCenterX = w * 0.28f
    val rightLensCenterX = w * 0.72f
    val bridgeWidth = rightLensCenterX - leftLensCenterX - lensWidth

    // Lens Tint based on Category
    val lensFillBrush = when (category) {
        FrameCategory.SUNGLASSES -> Brush.verticalGradient(
            colors = listOf(
                SunLensDark.copy(alpha = 0.90f),
                SunLensDark.copy(alpha = 0.75f),
                SunLensDark.copy(alpha = 0.60f)
            )
        )
        FrameCategory.BLUE_CUT -> Brush.linearGradient(
            colors = listOf(
                BlueCutLensTint.copy(alpha = 0.16f),
                Color(0xFF8054FF).copy(alpha = 0.22f),
                Color.Transparent
            ),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        )
        else -> Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.20f),
                Color.Transparent,
                Color.White.copy(alpha = 0.10f)
            )
        )
    }

    // Temples (side arms extending outwards)
    val templeY = centerY - lensHeight * 0.22f
    drawLine(
        color = frameColor.copy(alpha = 0.75f),
        start = Offset(leftLensCenterX - lensWidth / 2 + 4f, templeY),
        end = Offset(w * 0.04f, templeY - 12f),
        strokeWidth = strokeWidth * 0.8f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = frameColor.copy(alpha = 0.75f),
        start = Offset(rightLensCenterX + lensWidth / 2 - 4f, templeY),
        end = Offset(w * 0.96f, templeY - 12f),
        strokeWidth = strokeWidth * 0.8f,
        cap = StrokeCap.Round
    )

    // Nose Bridge
    val bridgePath = Path().apply {
        moveTo(leftLensCenterX + lensWidth / 2 - 2f, centerY - 6f)
        quadraticTo(
            w * 0.5f,
            centerY - 16f,
            rightLensCenterX - lensWidth / 2 + 2f,
            centerY - 6f
        )
    }
    drawPath(
        path = bridgePath,
        color = if (shape == FrameShape.RIMLESS) WarmGold else frameColor,
        style = Stroke(width = strokeWidth * 0.85f, cap = StrokeCap.Round)
    )

    // Aviator double bridge top bar
    if (shape == FrameShape.AVIATOR) {
        drawLine(
            color = frameColor,
            start = Offset(leftLensCenterX + lensWidth * 0.2f, centerY - lensHeight * 0.52f),
            end = Offset(rightLensCenterX - lensWidth * 0.2f, centerY - lensHeight * 0.52f),
            strokeWidth = strokeWidth * 0.7f,
            cap = StrokeCap.Round
        )
    }

    // Left & Right Lenses Drawing
    drawSingleLens(
        centerX = leftLensCenterX,
        centerY = centerY,
        lensWidth = lensWidth,
        lensHeight = lensHeight,
        shape = shape,
        frameColor = frameColor,
        strokeWidth = strokeWidth,
        lensFillBrush = lensFillBrush,
        isLeft = true
    )

    drawSingleLens(
        centerX = rightLensCenterX,
        centerY = centerY,
        lensWidth = lensWidth,
        lensHeight = lensHeight,
        shape = shape,
        frameColor = frameColor,
        strokeWidth = strokeWidth,
        lensFillBrush = lensFillBrush,
        isLeft = false
    )

    // Subtle Anti-Reflective Glare Streak across lenses
    drawGlareReflection(leftLensCenterX, centerY, lensWidth, lensHeight, category)
    drawGlareReflection(rightLensCenterX, centerY, lensWidth, lensHeight, category)
}

private fun DrawScope.drawSingleLens(
    centerX: Float,
    centerY: Float,
    lensWidth: Float,
    lensHeight: Float,
    shape: FrameShape,
    frameColor: Color,
    strokeWidth: Float,
    lensFillBrush: Brush,
    isLeft: Boolean
) {
    val halfW = lensWidth / 2
    val halfH = lensHeight / 2

    val lensPath = Path()
    when (shape) {
        FrameShape.ROUND -> {
            lensPath.addOval(
                Rect(
                    left = centerX - halfW,
                    top = centerY - halfH,
                    right = centerX + halfW,
                    bottom = centerY + halfH
                )
            )
        }
        FrameShape.AVIATOR -> {
            // Teardrop aviator shape
            lensPath.moveTo(centerX - halfW * 0.8f, centerY - halfH)
            lensPath.lineTo(centerX + halfW * 0.8f, centerY - halfH)
            lensPath.quadraticTo(
                centerX + halfW,
                centerY + halfH * 0.3f,
                centerX + halfW * 0.5f,
                centerY + halfH
            )
            lensPath.quadraticTo(
                centerX - halfW * 0.2f,
                centerY + halfH * 1.05f,
                centerX - halfW * 0.8f,
                centerY + halfH * 0.5f
            )
            lensPath.close()
        }
        FrameShape.CAT_EYE -> {
            val wingX = if (isLeft) centerX - halfW * 1.15f else centerX + halfW * 1.15f
            lensPath.moveTo(centerX, centerY - halfH * 0.7f)
            lensPath.lineTo(wingX, centerY - halfH)
            lensPath.quadraticTo(
                if (isLeft) centerX - halfW else centerX + halfW,
                centerY + halfH * 0.5f,
                centerX,
                centerY + halfH
            )
            lensPath.quadraticTo(
                if (isLeft) centerX + halfW * 0.8f else centerX - halfW * 0.8f,
                centerY + halfH * 0.7f,
                if (isLeft) centerX + halfW * 0.8f else centerX - halfW * 0.8f,
                centerY - halfH * 0.6f
            )
            lensPath.close()
        }
        FrameShape.RECTANGLE -> {
            lensPath.addRoundRect(
                RoundRect(
                    rect = Rect(centerX - halfW, centerY - halfH, centerX + halfW, centerY + halfH),
                    cornerRadius = CornerRadius(14f, 14f)
                )
            )
        }
        else -> { // Wayfarer & others
            lensPath.addRoundRect(
                RoundRect(
                    rect = Rect(centerX - halfW, centerY - halfH, centerX + halfW, centerY + halfH),
                    topLeft = CornerRadius(16f, 16f),
                    topRight = CornerRadius(16f, 16f),
                    bottomLeft = CornerRadius(28f, 28f),
                    bottomRight = CornerRadius(28f, 28f)
                )
            )
        }
    }

    // Fill the lens
    drawPath(path = lensPath, brush = lensFillBrush)

    // Draw the frame border rim
    val rimColor = if (shape == FrameShape.RIMLESS) frameColor.copy(alpha = 0.3f) else frameColor
    drawPath(
        path = lensPath,
        color = rimColor,
        style = Stroke(width = strokeWidth)
    )

    // Browline extra accent
    if (shape == FrameShape.RECTANGLE) {
        // Subtle top browline accent
        val browPath = Path().apply {
            moveTo(centerX - halfW - 2f, centerY - halfH - 2f)
            lineTo(centerX + halfW + 2f, centerY - halfH - 2f)
        }
        drawPath(browPath, color = frameColor, style = Stroke(width = strokeWidth * 1.4f, cap = StrokeCap.Round))
    }
}

private fun DrawScope.drawGlareReflection(
    centerX: Float,
    centerY: Float,
    lensWidth: Float,
    lensHeight: Float,
    category: FrameCategory
) {
    val glareColor = when (category) {
        FrameCategory.BLUE_CUT -> BlueCutLensTint.copy(alpha = 0.40f)
        FrameCategory.SUNGLASSES -> Color.White.copy(alpha = 0.25f)
        else -> Color.White.copy(alpha = 0.35f)
    }

    drawLine(
        color = glareColor,
        start = Offset(centerX - lensWidth * 0.25f, centerY - lensHeight * 0.35f),
        end = Offset(centerX + lensWidth * 0.15f, centerY - lensHeight * 0.15f),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
}
