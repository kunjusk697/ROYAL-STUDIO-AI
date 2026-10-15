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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ConfiguredLens
import com.example.model.EyePrescription
import com.example.model.FrameColor
import com.example.model.LensCoating
import com.example.model.LensIndex
import com.example.model.LensType
import com.example.model.PrescriptionDetails
import com.example.model.PrescriptionMethod
import com.example.model.Product
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CharcoalText
import com.example.ui.theme.CleanWhite
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.ForestGreenSurface
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SoftGoldContainer
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmGoldDark

@Composable
fun LensConfiguratorDialog(
    product: Product,
    onDismiss: () -> Unit,
    onAddToCart: (Product, FrameColor, ConfiguredLens) -> Unit
) {
    var selectedColor by remember {
        mutableStateOf(product.colors.firstOrNull { it.isDefault } ?: product.colors.first())
    }
    var selectedLensType by remember {
        // If it's a Blue-Cut frame, default to Zero Power Blue-Cut or Blue-Cut Single Vision
        mutableStateOf(
            if (product.category == com.example.model.FrameCategory.BLUE_CUT) {
                LensType.ZERO_POWER_BLUE_CUT
            } else {
                LensType.SINGLE_VISION
            }
        )
    }
    var selectedLensIndex by remember { mutableStateOf(LensIndex.INDEX_156) }
    var selectedCoatings by remember { mutableStateOf(setOf<LensCoating>()) }
    var prescriptionMethod by remember { mutableStateOf(PrescriptionMethod.WHATSAPP_PHOTO) }

    // Manual prescription fields
    var odSph by remember { mutableStateOf("-1.25") }
    var odCyl by remember { mutableStateOf("-0.50") }
    var odAxis by remember { mutableStateOf("90") }

    var osSph by remember { mutableStateOf("-1.00") }
    var osCyl by remember { mutableStateOf("0.00") }
    var osAxis by remember { mutableStateOf("0") }

    var pdValue by remember { mutableStateOf("63") }

    val lensTotal = selectedLensType.priceInr + selectedLensIndex.priceInr + selectedCoatings.sumOf { it.priceInr }
    val grandTotal = product.basePrice + lensTotal

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .clip(RoundedCornerShape(20.dp))
                .testTag("lens_configurator_dialog"),
            color = CleanWhite,
            shadowElevation = 10.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ForestGreenPrimary)
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = WarmGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LENS CONFIGURATOR",
                                color = WarmGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = product.name,
                            color = CleanWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CleanWhite
                        )
                    }
                }

                // Scrollable Body
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Item Preview & Color Chooser
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ForestGreenSurface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                EyewearIllustration(
                                    shape = product.shape,
                                    category = product.category,
                                    frameColor = Color(selectedColor.colorHex),
                                    size = 72.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Frame: ${product.name}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = CharcoalText
                                    )
                                    Text(
                                        text = "Base Frame Price: ₹${product.basePrice}",
                                        fontSize = 12.sp,
                                        color = ForestGreenPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Color:", fontSize = 11.sp, color = SlateMuted)
                                        product.colors.forEach { c ->
                                            Box(
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(c.colorHex))
                                                    .border(
                                                        width = if (selectedColor.name == c.name) 2.dp else 1.dp,
                                                        color = if (selectedColor.name == c.name) WarmGold else BorderLight,
                                                        shape = CircleShape
                                                    )
                                                    .clickable { selectedColor = c }
                                            )
                                        }
                                        Text(
                                            selectedColor.name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = CharcoalText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Step 1: Lens Type
                    item {
                        SectionHeader(stepNumber = "1", title = "Select Lens Type")
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            LensType.values().forEach { type ->
                                val isSelected = selectedLensType == type
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedLensType = type },
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) ForestGreenPrimary else BorderLight
                                    ),
                                    color = if (isSelected) SoftGoldContainer.copy(alpha = 0.5f) else CleanWhite
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedLensType = type },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = ForestGreenPrimary
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = type.title,
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CharcoalText
                                                )
                                                Text(
                                                    text = if (type.priceInr == 0) "Included" else "+₹${type.priceInr}",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = if (type.priceInr == 0) ForestGreenPrimary else WarmGoldDark
                                                )
                                            }
                                            Text(
                                                text = type.subtitle,
                                                fontSize = 11.5.sp,
                                                color = SlateMuted
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Step 2: Lens Index / Thinness (only if not Frame Only)
                    if (selectedLensType != LensType.FRAME_ONLY) {
                        item {
                            SectionHeader(stepNumber = "2", title = "Lens Index & Thinness")
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                LensIndex.values().forEach { idx ->
                                    val isSelected = selectedLensIndex == idx
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedLensIndex = idx },
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) ForestGreenPrimary else BorderLight
                                        ),
                                        color = if (isSelected) SoftGoldContainer.copy(alpha = 0.5f) else CleanWhite
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { selectedLensIndex = idx },
                                                colors = RadioButtonDefaults.colors(selectedColor = ForestGreenPrimary)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = idx.title,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = CharcoalText
                                                    )
                                                    Text(
                                                        text = if (idx.priceInr == 0) "+₹0" else "+₹${idx.priceInr}",
                                                        fontSize = 12.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = ForestGreenPrimary
                                                    )
                                                }
                                                Text(
                                                    text = idx.thicknessDescription,
                                                    fontSize = 11.sp,
                                                    color = SlateMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Step 3: Premium Coatings
                        item {
                            SectionHeader(stepNumber = "3", title = "Premium Protective Coatings")
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                LensCoating.values().forEach { coating ->
                                    val isChecked = selectedCoatings.contains(coating)
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedCoatings = if (isChecked) {
                                                    selectedCoatings - coating
                                                } else {
                                                    selectedCoatings + coating
                                                }
                                            },
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isChecked) 2.dp else 1.dp,
                                            color = if (isChecked) ForestGreenPrimary else BorderLight
                                        ),
                                        color = if (isChecked) SoftGoldContainer.copy(alpha = 0.5f) else CleanWhite
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { checked ->
                                                    selectedCoatings = if (checked) selectedCoatings + coating else selectedCoatings - coating
                                                },
                                                colors = CheckboxDefaults.colors(checkedColor = ForestGreenPrimary)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = coating.title,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = CharcoalText
                                                    )
                                                    Text(
                                                        text = "+₹${coating.priceInr}",
                                                        fontSize = 12.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = WarmGoldDark
                                                    )
                                                }
                                                Text(
                                                    text = coating.description,
                                                    fontSize = 11.sp,
                                                    color = SlateMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Step 4: Prescription Details
                        item {
                            SectionHeader(stepNumber = "4", title = "Prescription Details")
                            Spacer(modifier = Modifier.height(8.dp))

                            // Method choice: WhatsApp Photo vs Manual Entry
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { prescriptionMethod = PrescriptionMethod.WHATSAPP_PHOTO },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (prescriptionMethod == PrescriptionMethod.WHATSAPP_PHOTO) SoftGoldContainer else CleanWhite,
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (prescriptionMethod == PrescriptionMethod.WHATSAPP_PHOTO) 2.dp else 1.dp,
                                        color = if (prescriptionMethod == PrescriptionMethod.WHATSAPP_PHOTO) WarmGoldDark else BorderLight
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            tint = ForestGreenPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "WhatsApp Photo",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CharcoalText
                                        )
                                        Text(
                                            text = "(Recommended)",
                                            fontSize = 10.sp,
                                            color = ForestGreenPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { prescriptionMethod = PrescriptionMethod.MANUAL_ENTRY },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (prescriptionMethod == PrescriptionMethod.MANUAL_ENTRY) SoftGoldContainer else CleanWhite,
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (prescriptionMethod == PrescriptionMethod.MANUAL_ENTRY) 2.dp else 1.dp,
                                        color = if (prescriptionMethod == PrescriptionMethod.MANUAL_ENTRY) WarmGoldDark else BorderLight
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = ForestGreenPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Enter Manually",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CharcoalText
                                        )
                                        Text(
                                            text = "SPH / CYL / AXIS",
                                            fontSize = 10.sp,
                                            color = SlateMuted
                                        )
                                    }
                                }
                            }

                            if (prescriptionMethod == PrescriptionMethod.WHATSAPP_PHOTO) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFF1F8F5),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = ForestGreenPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "No need to calculate powers! Simply click a photo of your doctor's prescription slip and send it in our WhatsApp order chat.",
                                            fontSize = 11.5.sp,
                                            color = ForestGreenDark,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            } else {
                                // Manual fields table
                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = CleanWhite),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "Right Eye (OD)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ForestGreenPrimary
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = odSph,
                                                onValueChange = { odSph = it },
                                                label = { Text("SPH", fontSize = 10.sp) },
                                                modifier = Modifier.weight(1f),
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                            OutlinedTextField(
                                                value = odCyl,
                                                onValueChange = { odCyl = it },
                                                label = { Text("CYL", fontSize = 10.sp) },
                                                modifier = Modifier.weight(1f),
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                            OutlinedTextField(
                                                value = odAxis,
                                                onValueChange = { odAxis = it },
                                                label = { Text("AXIS", fontSize = 10.sp) },
                                                modifier = Modifier.weight(1f),
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Left Eye (OS)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ForestGreenPrimary
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = osSph,
                                                onValueChange = { osSph = it },
                                                label = { Text("SPH", fontSize = 10.sp) },
                                                modifier = Modifier.weight(1f),
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                            OutlinedTextField(
                                                value = osCyl,
                                                onValueChange = { osCyl = it },
                                                label = { Text("CYL", fontSize = 10.sp) },
                                                modifier = Modifier.weight(1f),
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                            OutlinedTextField(
                                                value = osAxis,
                                                onValueChange = { osAxis = it },
                                                label = { Text("AXIS", fontSize = 10.sp) },
                                                modifier = Modifier.weight(1f),
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = pdValue,
                                            onValueChange = { pdValue = it },
                                            label = { Text("Pupillary Distance (PD in mm)", fontSize = 10.sp) },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = BorderLight)

                // Bottom Price Bar & Add to Cart
                Surface(
                    color = CleanWhite,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Total Configured Price",
                                    fontSize = 11.5.sp,
                                    color = SlateMuted
                                )
                                Text(
                                    text = "₹$grandTotal",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ForestGreenPrimary
                                )
                            }

                            Button(
                                onClick = {
                                    val config = ConfiguredLens(
                                        lensType = selectedLensType,
                                        lensIndex = selectedLensIndex,
                                        selectedCoatings = selectedCoatings,
                                        prescriptionMethod = prescriptionMethod,
                                        prescriptionDetails = PrescriptionDetails(
                                            rightEyeOD = EyePrescription(sph = odSph, cyl = odCyl, axis = odAxis),
                                            leftEyeOS = EyePrescription(sph = osSph, cyl = osCyl, axis = osAxis),
                                            pupillaryDistanceMm = pdValue
                                        )
                                    )
                                    onAddToCart(product, selectedColor, config)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ForestGreenPrimary,
                                    contentColor = CleanWhite
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("add_configured_specs_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = WarmGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Add to Cart",
                                    fontSize = 14.sp,
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

@Composable
private fun SectionHeader(stepNumber: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(WarmGold),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                color = ForestGreenDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CharcoalText
        )
    }
}
