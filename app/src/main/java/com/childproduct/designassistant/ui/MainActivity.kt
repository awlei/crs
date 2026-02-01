package com.childproduct.designassistant.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.childproduct.designassistant.database.entity.StandardReference
import com.childproduct.designassistant.engineering.EngineeringOutputGenerator
import com.childproduct.designassistant.engineering.MarkdownFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DesignAssistantApp()
        }
    }
}

@Composable
fun DesignAssistantApp() {
    var minHeight by remember { mutableStateOf("40") }
    var maxHeight by remember { mutableStateOf("150") }
    var minWeight by remember { mutableStateOf("3") }
    var maxWeight by remember { mutableStateOf("36") }
    var selectedStandards by remember { mutableStateOf(setOf(StandardReference.UN_R129_REV4)) }
    var installMethod by remember { mutableStateOf("ISOFIX 3 pts + Top-tether") }
    var outputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CRS设计助手 · 工程师版", fontSize = 18.sp) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("1. 选择标准", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            item {
                StandardSelector(
                    selectedStandards = selectedStandards,
                    onSelectionChange = { selectedStandards = it }
                )
            }
            item {
                Text("2. 输入参数", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minHeight,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) minHeight = it },
                        label = { Text("最小身高(cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maxHeight,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) maxHeight = it },
                        label = { Text("最大身高(cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minWeight,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) minWeight = it },
                        label = { Text("最小重量(kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maxWeight,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) maxWeight = it },
                        label = { Text("最大重量(kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                Text("3. 安装方式", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            item {
                InstallMethodSelector(
                    selectedMethod = installMethod,
                    onSelectionChange = { installMethod = it }
                )
            }
            item {
                Button(
                    onClick = {
                        isLoading = true
                        // Generate output in background
                        val output = EngineeringOutputGenerator.generateEngineeringOutput(
                            minHeightCm = minHeight.toDoubleOrNull() ?: 40.0,
                            maxHeightCm = maxHeight.toDoubleOrNull() ?: 150.0,
                            minWeightKg = minWeight.toDoubleOrNull() ?: 3.0,
                            maxWeightKg = maxWeight.toDoubleOrNull() ?: 36.0,
                            selectedStandards = selectedStandards.toList(),
                            installMethod = installMethod
                        )
                        outputText = MarkdownFormatter.format(output)
                        isLoading = false
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("生成工程设计方案")
                    }
                }
            }
            item {
                if (outputText.isNotEmpty()) {
                    Text(
                        text = outputText,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StandardSelector(
    selectedStandards: Set<StandardReference>,
    onSelectionChange: (Set<StandardReference>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        StandardCheckbox(
            standard = StandardReference.UN_R129_REV4,
            selected = selectedStandards.contains(StandardReference.UN_R129_REV4),
            onCheckedChange = { checked ->
                val newSet = if (checked) {
                    selectedStandards + StandardReference.UN_R129_REV4
                } else {
                    selectedStandards - StandardReference.UN_R129_REV4
                }
                onSelectionChange(newSet)
            }
        )
        StandardCheckbox(
            standard = StandardReference.FMVSS_213_CURRENT,
            selected = selectedStandards.contains(StandardReference.FMVSS_213_CURRENT),
            onCheckedChange = { checked ->
                val newSet = if (checked) {
                    selectedStandards + StandardReference.FMVSS_213_CURRENT
                } else {
                    selectedStandards - StandardReference.FMVSS_213_CURRENT
                }
                onSelectionChange(newSet)
            }
        )
        StandardCheckbox(
            standard = StandardReference.FMVSS_213A,
            selected = selectedStandards.contains(StandardReference.FMVSS_213A),
            onCheckedChange = { checked ->
                val newSet = if (checked) {
                    selectedStandards + StandardReference.FMVSS_213A
                } else {
                    selectedStandards - StandardReference.FMVSS_213A
                }
                onSelectionChange(newSet)
            }
        )
        StandardCheckbox(
            standard = StandardReference.FMVSS_213B,
            selected = selectedStandards.contains(StandardReference.FMVSS_213B),
            onCheckedChange = { checked ->
                val newSet = if (checked) {
                    selectedStandards + StandardReference.FMVSS_213B
                } else {
                    selectedStandards - StandardReference.FMVSS_213B
                }
                onSelectionChange(newSet)
            }
        )
    }
}

@Composable
fun StandardCheckbox(
    standard: StandardReference,
    selected: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!selected) }
            .padding(8.dp)
    ) {
        Checkbox(
            checked = selected,
            onCheckedChange = onCheckedChange
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = "${standard.regulationNumber} ${standard.currentVersion}",
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = "生效日期: ${standard.effectiveDate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (standard.status == StandardReference.StandardStatus.FUTURE) {
                Text(
                    text = "⚠️ ${standard.complianceDate}生效",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun InstallMethodSelector(
    selectedMethod: String,
    onSelectionChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        InstallMethodOption(
            label = "ISOFIX 3点 + 支撑腿（后向）",
            selected = selectedMethod == "ISOFIX 3 pts + Support leg",
            onClick = { onSelectionChange("ISOFIX 3 pts + Support leg") }
        )
        InstallMethodOption(
            label = "ISOFIX 3点 + Top-tether（前向）",
            selected = selectedMethod == "ISOFIX 3 pts + Top-tether",
            onClick = { onSelectionChange("ISOFIX 3 pts + Top-tether") }
        )
        InstallMethodOption(
            label = "车辆安全带 + Top-tether",
            selected = selectedMethod == "Vehicle seat belt + Top-tether",
            onClick = { onSelectionChange("Vehicle seat belt + Top-tether") }
        )
    }
}

@Composable
fun InstallMethodOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                   else MaterialTheme.colorScheme.onSurface
        )
    }
}
