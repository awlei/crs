package com.childproduct.designassistant.engineering

import com.childproduct.designassistant.database.entity.CrashTestDummy
import com.childproduct.designassistant.database.entity.StandardReference
import java.text.SimpleDateFormat
import java.util.*

data class EngineeringOutput(
    val metadata: OutputMetadata,
    val basicInfo: BasicInfoSection,
    val standardMapping: StandardMappingSection,
    val anthropometryParams: AnthropometryParams,
    val safetyThresholds: List<SafetyThresholdSection>,
    val testMatrix: List<TestConfiguration>
)

data class OutputMetadata(
    val generatedAt: Long,
    val appVersion: String,
    val standards: List<String>,
    val dataSource: String
)

data class BasicInfoSection(
    val productType: String,
    val heightRange: String,
    val weightRange: String,
    val ageRange: String,
    val dummyCoverage: String,
    val installMethod: String
)

data class StandardMappingSection(
    val sections: List<StandardMapping>
)

data class StandardMapping(
    val standardName: String,
    val dummyMappings: List<DummyMapping>
)

data class DummyMapping(
    val dummyCode: String,
    val minHeightCm: Double,
    val maxHeightCm: Double,
    val ageRange: String,
    val installDirection: CrashTestDummy.InstallDirection,
    val standardClause: String
)

data class AnthropometryParams(
    val seatWidthMin: Double,
    val seatWidthIdeal: Double,
    val seatWidthMax: Double,
    val shoulderBeltHeightMin: Double,
    val shoulderBeltHeightMax: Double,
    val dataSource: String
)

data class SafetyThresholdSection(
    val standardName: String,
    val thresholds: List<SafetyThreshold>
)

data class SafetyThreshold(
    val testItem: String,
    val parameterName: String,
    val dummyRange: String,
    val maxValue: Double,
    val unit: String,
    val standardSource: String
)

data class TestConfiguration(
    val configId: String,
    val standard: String,
    val pulseType: String,
    val impact: String,
    val dummy: String,
    val position: String,
    val installation: String,
    val testSpeedKmh: Double
)

object EngineeringOutputGenerator {

    fun generateEngineeringOutput(
        minHeightCm: Double,
        maxHeightCm: Double,
        minWeightKg: Double,
        maxWeightKg: Double,
        selectedStandards: List<StandardReference>,
        installMethod: String
    ): EngineeringOutput {
        // 1. Get applicable dummies (with Q3s fix)
        val dummies = CrashTestDummy.STANDARD_DUMMIES.filter { 
            it.maxHeightCm >= minHeightCm && it.minHeightCm <= maxHeightCm &&
            it.maxWeightKg >= minWeightKg && it.minWeightKg <= maxWeightKg
        }

        // 2. Generate anthropometry params (GPS-028 based)
        val minHipWidth = dummies.minOfOrNull { it.hipWidthMm } ?: 180.0
        val maxHipWidth = dummies.maxOfOrNull { it.hipWidthMm } ?: 410.0
        val minShoulderWidth = dummies.minOfOrNull { it.shoulderWidthMm } ?: 145.0
        val maxShoulderWidth = dummies.maxOfOrNull { it.shoulderWidthMm } ?: 370.0

        // 3. Generate standard mappings (isolated per standard)
        val standardMappings = selectedStandards.map { standard ->
            StandardMapping(
                standardName = "${standard.regulationNumber} ${standard.currentVersion}",
                dummyMappings = dummies.filter { it.applicableStandards.contains(standard.regulationNumber) }.map { dummy ->
                    DummyMapping(
                        dummyCode = dummy.dummyCode,
                        minHeightCm = dummy.minHeightCm,
                        maxHeightCm = dummy.maxHeightCm,
                        ageRange = dummy.ageRange,
                        installDirection = dummy.installDirection,
                        standardClause = dummy.standardClause
                    )
                }
            )
        }

        // 4. Generate safety thresholds (isolated per standard)
        val safetyThresholds = selectedStandards.map { standard ->
            SafetyThresholdSection(
                standardName = "${standard.regulationNumber} ${standard.currentVersion}",
                thresholds = getThresholdsForStandard(standard.regulationNumber)
            )
        }

        // 5. Generate test matrix
        val testMatrix = generateTestMatrix(minHeightCm, maxHeightCm, installMethod, selectedStandards)

        return EngineeringOutput(
            metadata = OutputMetadata(
                generatedAt = System.currentTimeMillis(),
                appVersion = "1.0.0",
                standards = selectedStandards.map { "${it.regulationNumber} ${it.currentVersion} (Effective: ${it.effectiveDate})" },
                dataSource = "UNECE WP.29 + NHTSA Federal Register + GPS-028 Anthropometry 11-28-2018"
            ),
            basicInfo = BasicInfoSection(
                productType = "儿童安全座椅",
                heightRange = "${minHeightCm}-${maxHeightCm}cm",
                weightRange = "${minWeightKg}-${maxWeightKg}kg",
                ageRange = getAgeRange(minHeightCm, maxHeightCm),
                dummyCoverage = dummies.joinToString(" → ") { it.dummyCode },
                installMethod = installMethod
            ),
            standardMapping = StandardMappingSection(sections = standardMappings),
            anthropometryParams = AnthropometryParams(
                seatWidthMin = minHipWidth * 1.1,
                seatWidthIdeal = (minHipWidth + maxHipWidth) / 2 * 1.15,
                seatWidthMax = maxHipWidth * 1.25,
                shoulderBeltHeightMin = minShoulderWidth * 0.6,
                shoulderBeltHeightMax = maxShoulderWidth * 0.7,
                dataSource = "GPS-028 Anthropometry 11-28-2018"
            ),
            safetyThresholds = safetyThresholds,
            testMatrix = testMatrix
        )
    }

    private fun getThresholdsForStandard(standard: String): List<SafetyThreshold> {
        return when (standard) {
            "UN R129" -> listOf(
                SafetyThreshold("头部伤害准则", "HIC15/HIC36", "Q0-Q1.5 / Q3-Q10", 390.0, "-", "UN R129 §7.1.2"),
                SafetyThreshold("胸部合成加速度", "ChestAcc3ms", "Q0-Q1.5 / Q3-Q10", 60.0, "g", "UN R129 §7.1.3"),
                SafetyThreshold("头部位移", "HeadExcursion", "Q0-Q10", 550.0, "mm", "UN R129 §7.1.5")
            )
            "FMVSS 213" -> listOf(
                SafetyThreshold("Head Injury Criterion", "HIC", "All ATDs", 1000.0, "-", "FMVSS 213 S5.1.2"),
                SafetyThreshold("Chest Acceleration", "ChestAcc3ms", "All ATDs", 60.0, "g", "FMVSS 213 S5.1.2"),
                SafetyThreshold("Head Excursion", "HeadExcursion", "Forward Facing", 813.0, "mm", "FMVSS 213 S5.1.3")
            )
            "FMVSS 213a" -> listOf(
                SafetyThreshold("Head Injury Criterion", "HIC570", "Q3s", 570.0, "-", "FMVSS 213a S5.1.2"),
                SafetyThreshold("Chest Deflection", "ChestDeflection", "Q3s", 23.0, "mm", "FMVSS 213a S5.1.2")
            )
            "FMVSS 213b" -> listOf(
                SafetyThreshold("Head Injury Criterion", "HIC", "All ATDs", 1000.0, "-", "FMVSS 213b S5.1.2"),
                SafetyThreshold("Minimum Weight", "Weight", "Forward Facing", 12.0, "kg", "FMVSS 213b S5.3.1"),
                SafetyThreshold("Minimum Weight", "Weight", "Booster Seats", 18.0, "kg", "FMVSS 213b S5.3.1")
            )
            else -> emptyList()
        }
    }

    private fun generateTestMatrix(
        minHeightCm: Double,
        maxHeightCm: Double,
        installMethod: String,
        standards: List<StandardReference>
    ): List<TestConfiguration> {
        val configs = mutableListOf<TestConfiguration>()

        standards.forEach { standard ->
            when (standard.regulationNumber) {
                "UN R129" -> {
                    // Frontal 50km/h
                    configs.add(TestConfiguration(
                        configId = "R129_FRONTAL",
                        standard = "UN R129 Rev.4",
                        pulseType = "Frontal",
                        impact = getDummyCodeForRange(minHeightCm, maxHeightCm, "EU"),
                        dummy = getDummyCodeForRange(minHeightCm, maxHeightCm, "EU"),
                        position = if (minHeightCm < 105) "Rearward facing" else "Forward facing",
                        installation = installMethod,
                        testSpeedKmh = 50.0
                    ))
                    // Lateral 32km/h
                    configs.add(TestConfiguration(
                        configId = "R129_LATERAL",
                        standard = "UN R129 Rev.4",
                        pulseType = "Lateral",
                        impact = getDummyCodeForRange(minHeightCm, maxHeightCm, "EU"),
                        dummy = getDummyCodeForRange(minHeightCm, maxHeightCm, "EU"),
                        position = if (minHeightCm < 105) "Rearward facing" else "Forward facing",
                        installation = installMethod,
                        testSpeedKmh = 32.0
                    ))
                }
                "FMVSS 213" -> {
                    // Configuration I 48km/h (30mph)
                    configs.add(TestConfiguration(
                        configId = "FMVSS213_CONFIG_I",
                        standard = "FMVSS 213",
                        pulseType = "Frontal",
                        impact = getDummyCodeForRange(minHeightCm, maxHeightCm, "US"),
                        dummy = getDummyCodeForRange(minHeightCm, maxHeightCm, "US"),
                        position = if (minWeightKgForRange(minHeightCm) < 13.6) "Rearward facing" else "Forward facing",
                        installation = installMethod,
                        testSpeedKmh = 48.0
                    ))
                }
                "FMVSS 213a" -> {
                    // Side impact 32km/h (only if ≤1100mm)
                    if (maxHeightCm <= 110.0) {
                        configs.add(TestConfiguration(
                            configId = "FMVSS213A_SIDE",
                            standard = "FMVSS 213a",
                            pulseType = "Side Impact",
                            impact = "Q3s",
                            dummy = "Q3s",
                            position = "Forward facing",
                            installation = "LATCH + Top tether",
                            testSpeedKmh = 32.0
                        ))
                    }
                }
            }
        }
        return configs
    }

    private fun getDummyCodeForRange(minHeightCm: Double, maxHeightCm: Double, region: String): String {
        return when (region) {
            "EU" -> when {
                maxHeightCm <= 50 -> "Q0"
                maxHeightCm <= 60 -> "Q0+"
                maxHeightCm <= 75 -> "Q1"
                maxHeightCm <= 87 -> "Q1.5"
                maxHeightCm <= 105 -> "Q3"
                maxHeightCm <= 125 -> "Q3s"  // ⚠️ CRITICAL: Q3s for 105-125cm
                maxHeightCm <= 145 -> "Q6"
                else -> "Q10"
            }
            "US" -> when {
                maxHeightCm <= 65 -> "Newborn"
                maxHeightCm <= 75 -> "CRABI_12MO"
                maxHeightCm <= 110 -> "HIII_3YO"
                maxHeightCm <= 125 -> "HIII_6YO"
                else -> "HIII_10YO"
            }
            else -> "Unknown"
        }
    }

    private fun minWeightKgForRange(minHeightCm: Double): Double {
        return when {
            minHeightCm < 60 -> 3.0
            minHeightCm < 75 -> 5.0
            minHeightCm < 110 -> 10.0
            minHeightCm < 125 -> 18.2
            else -> 22.7
        }
    }

    private fun getAgeRange(minHeightCm: Double, maxHeightCm: Double): String {
        val minAge = when {
            minHeightCm < 60 -> 0
            minHeightCm < 75 -> 1
            minHeightCm < 87 -> 2
            minHeightCm < 105 -> 3
            minHeightCm < 125 -> 4
            minHeightCm < 145 -> 6
            else -> 10
        }
        val maxAge = when {
            maxHeightCm <= 60 -> 1
            maxHeightCm <= 75 -> 2
            maxHeightCm <= 87 -> 3
            maxHeightCm <= 105 -> 4
            maxHeightCm <= 125 -> 6
            maxHeightCm <= 145 -> 10
            else -> 12
        }
        return "$minAge-$maxAge岁"
    }
}
