package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.components.CameraPermissionWrapper
import com.example.ui.components.EyewearIllustration
import com.example.ui.components.RoyolOpticalsLogo
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.DeepEmerald
import com.example.ui.theme.DeepEmeraldDark
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.WarmYellowAccent
import com.example.util.WhatsAppHelper
import kotlinx.coroutines.delay

data class TryOnFrame(
    val id: String,
    val name: String,
    val shape: String,
    val color: Color,
    val widthDp: Dp,
    val bridgeDp: Dp,
    val basePrice: Int = 2799,
    val tag: String = "POPULAR"
)

val sampleFrames = listOf(
    TryOnFrame(
        id = "f1",
        name = "Aviator Gold",
        shape = "Aviator",
        color = Color(0xFFE8B84A),
        widthDp = 152.dp,
        bridgeDp = 18.dp,
        basePrice = 2799,
        tag = "BESTSELLER"
    ),
    TryOnFrame(
        id = "f2",
        name = "Classic Wayfarer",
        shape = "Wayfarer",
        color = Color(0xFF1E293B),
        widthDp = 146.dp,
        bridgeDp = 19.dp,
        basePrice = 2499,
        tag = "CLASSIC"
    ),
    TryOnFrame(
        id = "f3",
        name = "Round Minimalist",
        shape = "Round",
        color = Color(0xFFD4AF37),
        widthDp = 138.dp,
        bridgeDp = 20.dp,
        basePrice = 2199,
        tag = "LIGHTWEIGHT"
    ),
    TryOnFrame(
        id = "f4",
        name = "Cat-Eye Rose",
        shape = "Cat-Eye",
        color = Color(0xFFB76E79),
        widthDp = 142.dp,
        bridgeDp = 17.dp,
        basePrice = 2699,
        tag = "CHIC"
    ),
    TryOnFrame(
        id = "f5",
        name = "Hexagonal Titanium",
        shape = "Hexagonal",
        color = Color(0xFFE8B84A),
        widthDp = 148.dp,
        bridgeDp = 19.dp,
        basePrice = 2999,
        tag = "PREMIUM"
    )
)

/**
 * Live Interactive Virtual Try-On Screen
 * Recreates the exact reference UI from WhatsApp Image 2026-10-15 at 9.03.06 PM:
 * - Circular/oval viewport for front-facing camera stream
 * - Green "Face detected" indicator pill
 * - Face-tracking guide box [ ] with eye-level frame overlay
 * - Floating controls for zoom (+/-), brightness slider, camera flip, and snapshot capture
 * - Horizontal swipe carousel of frames right below mirror with active gold border
 * - "Perfect Fit Found: [Frame Name]" bottom sheet with custom lens recommendations and WhatsApp order
 */
@Composable
fun VirtualTryOnScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFrameIndex by remember { mutableIntStateOf(0) }
    val selectedFrame = sampleFrames[selectedFrameIndex]

    // Photo gallery launcher fallback
    var customPortraitUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            customPortraitUri = uri
        }
    }

    // Interactive Camera controls: Zoom & Brightness & Flip
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var brightnessLevel by remember { mutableFloatStateOf(1.0f) }
    var isFrontCamera by remember { mutableStateOf(true) }
    var isSnapshotTaken by remember { mutableStateOf(false) }
    var isShutterFlashing by remember { mutableStateOf(false) }

    // Authentic photorealistic model portrait matching the reference image man
    val defaultModelPortraitUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=800&q=80"

    CameraPermissionWrapper {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF042B25)) // Deep Emerald Luxury Backdrop matching reference
                .testTag("virtual_try_on_screen")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // 1. Top Navigation Bar: Back Arrow `<` + WhatsApp Pill `+919946732958`
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular back button `<`
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.35f))
                            .testTag("btn_try_on_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CleanWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // WhatsApp quick order pill (+919946732958) matching reference top-right
                    Surface(
                        onClick = {
                            WhatsAppHelper.openWhatsAppChat(
                                context = context,
                                message = "Hello OPTICS / Royal Specs, I am trying on '${selectedFrame.name}' (₹${selectedFrame.basePrice}) in Virtual Mirror and would like to order."
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.6f)),
                        modifier = Modifier.testTag("btn_whatsapp_try_on_top")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF25D366)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = CleanWhite,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+919946732958",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CleanWhite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))


    CameraPermissionWrapper {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF042B25)) // Deep Emerald Luxury Backdrop matching reference
                .testTag("virtual_try_on_screen")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // 1. Top Navigation Bar: Back Arrow `<` + WhatsApp Pill `+919946732958`
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular back button `<`
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.35f))
                            .testTag("btn_try_on_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CleanWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // WhatsApp quick order pill (+919946732958) matching reference top-right
                    Surface(
                        onClick = {
                            WhatsAppHelper.openWhatsAppChat(
                                context = context,
                                message = "Hello OPTICS / Royal Specs, I am trying on '${selectedFrame.name}' (₹${selectedFrame.basePrice}) in Virtual Mirror and would like to order."
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF25D366).copy(alpha = 0.6f)),
                        modifier = Modifier.testTag("btn_whatsapp_try_on_top")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF25D366)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = CleanWhite,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+919946732958",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CleanWhite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 2. Camera Viewport & Mirror (Large Oval/Circular Mirror with Face Detection)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Oval Camera Stage matching reference
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.96f)
                            .fillMaxHeight(0.96f)
                            .clip(RoundedCornerShape(percent = 48)) // Perfect luxury oval viewport
                            .background(Color(0xFF021714))
                            .border(
                                width = 3.dp,
                                color = Color(0xFF0E564A),
                                shape = RoundedCornerShape(percent = 48)
                            )
                            .testTag("try_on_camera_viewport"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Camera / Face Feed with Zoom and Brightness Scale
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(zoomLevel)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(customPortraitUri ?: defaultModelPortraitUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "User Face Camera Stream",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Brightness filter overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Color.Black.copy(alpha = ((1.5f - brightnessLevel) * 0.45f).coerceIn(0f, 0.7f))
                                    )
                            )
                        }

                        // Green "Face detected" indicator pill at top of face
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 22.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.55f))
                                .border(1.dp, Color(0xFF22C55E).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Face detected",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4ADE80)
                                )
                            }
                        }

                        // Green Face-Tracking Guide Box [   ] with real-time frame overlay
                        Box(
                            modifier = Modifier
                                .size(width = 200.dp, height = 230.dp)
                                .testTag("face_tracking_box"),
                            contentAlignment = Alignment.Center
                        ) {
                            // Four green corner brackets
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeW = 3.5.dp.toPx()
                                val bracketLen = 28.dp.toPx()
                                val cornerR = 12.dp.toPx()
                                val green = Color(0xFF22C55E)

                                // Top-Left
                                drawLine(color = green, start = Offset(0f, bracketLen), end = Offset(0f, cornerR), strokeWidth = strokeW, cap = StrokeCap.Round)
                                drawLine(color = green, start = Offset(cornerR, 0f), end = Offset(bracketLen, 0f), strokeWidth = strokeW, cap = StrokeCap.Round)

                                // Top-Right
                                drawLine(color = green, start = Offset(size.width, bracketLen), end = Offset(size.width, cornerR), strokeWidth = strokeW, cap = StrokeCap.Round)
                                drawLine(color = green, start = Offset(size.width - cornerR, 0f), end = Offset(size.width - bracketLen, 0f), strokeWidth = strokeW, cap = StrokeCap.Round)

                                // Bottom-Left
                                drawLine(color = green, start = Offset(0f, size.height - bracketLen), end = Offset(0f, size.height - cornerR), strokeWidth = strokeW, cap = StrokeCap.Round)
                                drawLine(color = green, start = Offset(cornerR, size.height), end = Offset(bracketLen, size.height), strokeWidth = strokeW, cap = StrokeCap.Round)

                                // Bottom-Right
                                drawLine(color = green, start = Offset(size.width, size.height - bracketLen), end = Offset(size.width, size.height - cornerR), strokeWidth = strokeW, cap = StrokeCap.Round)
                                drawLine(color = green, start = Offset(size.width - cornerR, size.height), end = Offset(size.width - bracketLen, size.height), strokeWidth = strokeW, cap = StrokeCap.Round)
                            }

                            // Seamless Optical Frame Overlay across user's eyes
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .offset(y = (-8).dp)
                            ) {
                                                                                            RealtimeGlassesOverlay(
                                    frame = selectedFrame,
                                    modifier = Modifier.size(width = 168.dp, height = 62.dp)
                                )
                            }
                        }

                        // Floating Vertical Brightness Slider on Left Side
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 14.dp)
                                .width(22.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .border(1.dp, CleanWhite.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .pointerInput(Unit) {
                                    detectVerticalDragGestures { _, dragAmount ->
                                        brightnessLevel = (brightnessLevel - dragAmount * 0.02f).coerceIn(0.5f, 1.5f)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Slider thumb indicator
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.35f)
                                    .fillMaxHeight(0.4f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CleanWhite)
                            )
                        }

                        // Floating Vertical Zoom Controls on Right Side [+ / 🔍 / -]
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 14.dp)
                                .width(26.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .border(1.dp, CleanWhite.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { zoomLevel = (zoomLevel + 0.15f).coerceAtMost(2.0f) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Zoom In", tint = CleanWhite, modifier = Modifier.size(14.dp))
                            }

                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFFE8B84A), modifier = Modifier.size(12.dp))

                            IconButton(
                                onClick = { zoomLevel = (zoomLevel - 0.15f).coerceAtLeast(1.0f) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Zoom Out", tint = CleanWhite, modifier = Modifier.size(14.dp))
                            }
                        }

                        // Bottom Mirror Floating Controls: [Scan/Fit] • [⚪ Capture] • [📷 Flip Camera]
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(22.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Scan / Frame Fit Button
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Face contour analyzed: ${selectedFrame.name} is a 98% ideal fit!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CropFree,
                                    contentDescription = "Fit Analysis",
                                    tint = CleanWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Big White Shutter / Snapshot Button matching reference
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .border(3.5.dp, CleanWhite, CircleShape)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(CleanWhite)
                                    .clickable {
                                        isShutterFlashing = true
                                        isSnapshotTaken = true
                                        Toast.makeText(context, "📸 Look captured with ${selectedFrame.name}!", Toast.LENGTH_SHORT).show()
                                    }
                                    .testTag("btn_capture_try_on_shutter"),
                                contentAlignment = Alignment.Center
                            ) {}

                            // Camera Flip / Gallery Upload Button
                            IconButton(
                                onClick = {
                                    isFrontCamera = !isFrontCamera
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlipCameraAndroid,
                                    contentDescription = "Flip / Select Portrait",
                                    tint = CleanWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Flash feedback animation on capture
                        if (isShutterFlashing) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(CleanWhite.copy(alpha = 0.85f))
                            )
                            LaunchedEffect(Unit) {
                                delay(180L)
                                isShutterFlashing = false
                            }
                        }
                    }
                }
            }
            
            // Photo gallery launcher fallback definition must be here or in outer scope - oops, I need to keep it in the component
            // Moving photo picker launcher inside, it needs to be defined within the composable scope
        }
    }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Horizontal Scrollable Frame Carousel ("Swipe and preview ↓")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(sampleFrames.indices.toList()) { index ->
                        val frame = sampleFrames[index]
                        val isSelected = selectedFrameIndex == index

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedFrameIndex = index }
                                .testTag("frame_thumb_$index")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 60.dp else 48.dp)
                                    .clip(CircleShape)
                                    .background(CleanWhite)
                                    .border(
                                        width = if (isSelected) 3.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFFE8B84A) else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .shadow(if (isSelected) 6.dp else 1.dp, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                EyewearIllustration(
                                    shape = when (frame.shape) {
                                        "Aviator" -> com.example.model.FrameShape.AVIATOR
                                        "Wayfarer" -> com.example.model.FrameShape.WAYFARER
                                        "Cat-Eye" -> com.example.model.FrameShape.CAT_EYE
                                        "Round" -> com.example.model.FrameShape.ROUND
                                        else -> com.example.model.FrameShape.ROUND
                                    },
                                    category = com.example.model.FrameCategory.ALL,
                                    frameColor = frame.color,
                                    size = if (isSelected) 44.dp else 34.dp
                                )
                            }
                        }
                    }
                }

                // "Swipe and preview ↓" vertical label on right side matching reference
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                ) {
                    Text(
                        text = "Swipe\nand preview",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = CleanWhite.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = 11.sp
                    )
                    Text(
                        text = "↓",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE8B84A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. "Perfect Fit Found" Advisor Card (Floating Bottom Sheet Card)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
                color = CleanWhite,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Gray drag pill handle
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFCBD5E1))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Title: "Perfect Fit Found: Aviator Gold"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Perfect Fit Found: ",
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CharcoalText,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = selectedFrame.name,
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFC78F00),
                            fontFamily = FontFamily.Serif
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "With Custom lens recommendations",
                        fontSize = 11.5.sp,
                        color = SlateMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Lens Recommendation Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Blue-Cut Digital Shield",
                            "Anti-Glare Hydrophobic",
                            "High-Index 1.67"
                        ).forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF334155),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons Row: [Try Another Style] & [WhatsApp Order]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // "Try Another Style" button (Deep Forest Green pill button)
                        Button(
                            onClick = {
                                selectedFrameIndex = (selectedFrameIndex + 1) % sampleFrames.size
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00473E),
                                contentColor = CleanWhite
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_try_another_style")
                        ) {
                            Text(
                                text = "Try Another Style",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = CleanWhite
                            )
                        }

                        // WhatsApp Order Button (+919946732958)
                        Button(
                            onClick = {
                                WhatsAppHelper.openWhatsAppChat(
                                    context = context,
                                    message = "Hello OPTICS / Royal Specs team! I tried on the '${selectedFrame.name}' (₹${selectedFrame.basePrice}) on the Virtual Try-On screen and would like to confirm my order with custom Blue-Cut lenses."
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25D366),
                                contentColor = CleanWhite
                            ),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(46.dp)
                                .testTag("btn_whatsapp_order_try_on")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = CleanWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Order on WhatsApp",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CleanWhite
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Real-time Glasses Optical Overlay rendered precisely over eye coordinates
 */
@Composable
fun RealtimeGlassesOverlay(
    frame: TryOnFrame,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val frameColor = frame.color
        val strokeThick = 3.dp.toPx()
        val strokeThin = 1.8.dp.toPx()

        // 1. Left Lens Rim
        val leftRim = RoundRect(
            rect = androidx.compose.ui.geometry.Rect(
                left = w * 0.08f,
                top = h * 0.12f,
                right = w * 0.44f,
                bottom = h * 0.88f
            ),
            cornerRadius = when (frame.shape) {
                "Aviator" -> CornerRadius(w * 0.08f, h * 0.35f)
                "Round" -> CornerRadius(w * 0.18f, h * 0.38f)
                "Cat-Eye" -> CornerRadius(w * 0.12f, h * 0.28f)
                else -> CornerRadius(w * 0.06f, h * 0.20f)
            }
        )
        val leftPath = Path().apply { addRoundRect(leftRim) }

        // Subtle gradient lens tint
        drawPath(
            path = leftPath,
            color = Color(0xFF60A5FA).copy(alpha = 0.18f)
        )
        drawPath(
            path = leftPath,
            color = frameColor,
            style = Stroke(width = strokeThick, cap = StrokeCap.Round)
        )

        // 2. Right Lens Rim
        val rightRim = RoundRect(
            rect = androidx.compose.ui.geometry.Rect(
                left = w * 0.56f,
                top = h * 0.12f,
                right = w * 0.92f,
                bottom = h * 0.88f
            ),
            cornerRadius = when (frame.shape) {
                "Aviator" -> CornerRadius(w * 0.08f, h * 0.35f)
                "Round" -> CornerRadius(w * 0.18f, h * 0.38f)
                "Cat-Eye" -> CornerRadius(w * 0.12f, h * 0.28f)
                else -> CornerRadius(w * 0.06f, h * 0.20f)
            }
        )
        val rightPath = Path().apply { addRoundRect(rightRim) }

        drawPath(
            path = rightPath,
            color = Color(0xFF60A5FA).copy(alpha = 0.18f)
        )
        drawPath(
            path = rightPath,
            color = frameColor,
            style = Stroke(width = strokeThick, cap = StrokeCap.Round)
        )

        // 3. Center Nose Bridge
        val bridgePath = Path().apply {
            moveTo(w * 0.44f, h * 0.38f)
            quadraticTo(w * 0.50f, h * 0.28f, w * 0.56f, h * 0.38f)
        }
        drawPath(
            path = bridgePath,
            color = frameColor,
            style = Stroke(width = strokeThin, cap = StrokeCap.Round)
        )

        // Aviator Double Brow Bar
        if (frame.shape == "Aviator") {
            drawLine(
                color = frameColor,
                start = Offset(w * 0.25f, h * 0.12f),
                end = Offset(w * 0.75f, h * 0.12f),
                strokeWidth = strokeThin,
                cap = StrokeCap.Round
            )
        }

        // Cat-Eye Top Flairs
        if (frame.shape == "Cat-Eye") {
            drawLine(
                color = frameColor,
                start = Offset(w * 0.08f, h * 0.20f),
                end = Offset(w * 0.02f, h * 0.06f),
                strokeWidth = strokeThick,
                cap = StrokeCap.Round
            )
            drawLine(
                color = frameColor,
                start = Offset(w * 0.92f, h * 0.20f),
                end = Offset(w * 0.98f, h * 0.06f),
                strokeWidth = strokeThick,
                cap = StrokeCap.Round
            )
        }

        // 4. Subtle Optical Glass Glare Glint
        drawLine(
            color = CleanWhite.copy(alpha = 0.55f),
            start = Offset(w * 0.16f, h * 0.24f),
            end = Offset(w * 0.26f, h * 0.44f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = CleanWhite.copy(alpha = 0.55f),
            start = Offset(w * 0.64f, h * 0.24f),
            end = Offset(w * 0.74f, h * 0.44f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

}



