package com.childproduct.designassistant.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crash_test_dummy")
data class CrashTestDummy(
    @PrimaryKey val dummyId: String,
    val dummyCode: String,
    val dummySeries: DummySeries,
    val minHeightCm: Double,
    val maxHeightCm: Double,
    val minWeightKg: Double,
    val maxWeightKg: Double,
    val ageRange: String,
    val installDirection: InstallDirection,
    val hipWidthMm: Double,
    val shoulderWidthMm: Double,
    val applicableStandards: List<String>,
    val standardClause: String
) {
    enum class DummySeries {
        EU_Q_SERIES,
        US_CRABI,
        US_HIII,
        US_Q3S_SIDE_IMPACT
    }

    enum class InstallDirection {
        REARWARD,
        FORWARD
    }

    companion object {
        val STANDARD_DUMMIES = listOf(
            // EU Q-Series (UN R129)
            CrashTestDummy("DUMMY_Q0", "Q0", DummySeries.EU_Q_SERIES, 40.0, 50.0, 3.47, 5.53, "0-6月", InstallDirection.REARWARD, 180.0, 145.0, listOf("UN R129"), "UN R129 Annex 19 §4.1"),
            CrashTestDummy("DUMMY_Q0_PLUS", "Q0+", DummySeries.EU_Q_SERIES, 50.0, 60.0, 5.53, 8.15, "6-12月", InstallDirection.REARWARD, 210.0, 165.0, listOf("UN R129"), "UN R129 Annex 19 §4.1"),
            CrashTestDummy("DUMMY_Q1", "Q1", DummySeries.EU_Q_SERIES, 60.0, 75.0, 8.15, 10.5, "1-2岁", InstallDirection.REARWARD, 245.0, 195.0, listOf("UN R129"), "UN R129 Annex 19 §4.1"),
            CrashTestDummy("DUMMY_Q1_5", "Q1.5", DummySeries.EU_Q_SERIES, 75.0, 87.0, 10.5, 14.0, "2-3岁", InstallDirection.REARWARD, 275.0, 225.0, listOf("UN R129"), "UN R129 Annex 19 §4.1"),
            CrashTestDummy("DUMMY_Q3", "Q3", DummySeries.EU_Q_SERIES, 87.0, 105.0, 14.0, 18.0, "3-4岁", InstallDirection.REARWARD, 310.0, 260.0, listOf("UN R129"), "UN R129 Annex 19 §4.1"),
            // ⚠️ CRITICAL FIX: Q3s exists for 105-125cm (Forward facing)
            CrashTestDummy("DUMMY_Q3S", "Q3s", DummySeries.EU_Q_SERIES, 105.0, 125.0, 18.0, 24.0, "4-6岁", InstallDirection.FORWARD, 345.0, 295.0, listOf("UN R129"), "UN R129 Annex 19 §4.2"),
            CrashTestDummy("DUMMY_Q6", "Q6", DummySeries.EU_Q_SERIES, 125.0, 145.0, 24.0, 35.58, "6-10岁", InstallDirection.FORWARD, 380.0, 335.0, listOf("UN R129"), "UN R129 Annex 19 §4.2"),
            CrashTestDummy("DUMMY_Q10", "Q10", DummySeries.EU_Q_SERIES, 145.0, 150.0, 35.58, 35.58, "10-12岁", InstallDirection.FORWARD, 410.0, 370.0, listOf("UN R129"), "UN R129 Annex 19 §4.2"),
            
            // US ATDs (FMVSS 213)
            CrashTestDummy("DUMMY_NEWBORN", "Newborn", DummySeries.US_CRABI, 40.0, 65.0, 3.0, 5.0, "0-6月", InstallDirection.REARWARD, 175.0, 140.0, listOf("FMVSS 213", "FMVSS 213a"), "FMVSS 213 S7.1.2"),
            CrashTestDummy("DUMMY_CRABI_12MO", "CRABI_12MO", DummySeries.US_CRABI, 65.0, 75.0, 5.0, 10.0, "12月", InstallDirection.REARWARD, 205.0, 160.0, listOf("FMVSS 213", "FMVSS 213a"), "FMVSS 213 S7.1.2"),
            // ⚠️ FMVSS 213a Side Impact specific dummy
            CrashTestDummy("DUMMY_Q3S_SIDE", "Q3s", DummySeries.US_Q3S_SIDE_IMPACT, 87.0, 110.0, 13.6, 18.2, "3岁", InstallDirection.FORWARD, 285.0, 250.0, listOf("FMVSS 213a"), "FMVSS 213a S5.1.2"),
            CrashTestDummy("DUMMY_HIII_3YO", "HIII_3YO", DummySeries.US_HIII, 85.0, 110.0, 10.0, 18.2, "3岁", InstallDirection.FORWARD, 285.0, 250.0, listOf("FMVSS 213", "FMVSS 213b"), "FMVSS 213 S7.1.2"),
            CrashTestDummy("DUMMY_HIII_6YO", "HIII_6YO", DummySeries.US_HIII, 110.0, 125.0, 18.2, 22.7, "6岁", InstallDirection.FORWARD, 320.0, 290.0, listOf("FMVSS 213", "FMVSS 213b"), "FMVSS 213 S7.1.2"),
            CrashTestDummy("DUMMY_HIII_10YO", "HIII_10YO", DummySeries.US_HIII, 125.0, 150.0, 22.7, 36.0, "10岁", InstallDirection.FORWARD, 360.0, 330.0, listOf("FMVSS 213", "FMVSS 213b"), "FMVSS 213 S7.1.2")
        )
    }
}
