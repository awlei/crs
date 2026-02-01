package com.childproduct.designassistant.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "standard_reference")
data class StandardReference(
    @PrimaryKey val referenceId: String,
    val regulationNumber: String,
    val currentVersion: String,
    val effectiveDate: String,
    val complianceDate: String,
    val status: StandardStatus,
    val scope: String,
    val officialUrl: String
) {
    enum class StandardStatus { ACTIVE, FUTURE, DRAFT, OBSOLETE }

    companion object {
        val UN_R129_REV4 = StandardReference(
            referenceId = "REF_UN_R129_REV4",
            regulationNumber = "UN R129",
            currentVersion = "Rev.4",
            effectiveDate = "29 December 2018",
            complianceDate = "15 March 2023",
            status = StandardStatus.ACTIVE,
            scope = "40-150cm stature range, Q0-Q10 dummy series",
            officialUrl = "https://unece.org/sites/default/files/2023-03/R129r4e.pdf"
        )

        val FMVSS_213_CURRENT = StandardReference(
            referenceId = "REF_FMVSS_213_CURRENT",
            regulationNumber = "FMVSS 213",
            currentVersion = "Current Standard",
            effectiveDate = "01 September 2002",
            complianceDate = "01 September 2002",
            status = StandardStatus.ACTIVE,
            scope = "Child restraint systems - frontal impact",
            officialUrl = "https://www.ecfr.gov/current/title-49/chapter-V/subchapter-B/part-571/subpart-B/section-571.213"
        )

        val FMVSS_213A = StandardReference(
            referenceId = "REF_FMVSS_213A",
            regulationNumber = "FMVSS 213a",
            currentVersion = "Final Rule",
            effectiveDate = "01 August 2022",
            complianceDate = "30 June 2025",
            status = StandardStatus.FUTURE,
            scope = "Child restraint systems - side impact protection (≤40 lb / ≤1100mm)",
            officialUrl = "https://www.ecfr.gov/current/title-49/chapter-V/subchapter-B/part-571/subpart-B/section-571.213a"
        )

        val FMVSS_213B = StandardReference(
            referenceId = "REF_FMVSS_213B",
            regulationNumber = "FMVSS 213b",
            currentVersion = "Final Rule",
            effectiveDate = "05 December 2024",
            complianceDate = "05 December 2026",
            status = StandardStatus.FUTURE,
            scope = "Enhanced frontal impact requirements, weight rating changes",
            officialUrl = "https://www.ecfr.gov/current/title-49/chapter-V/subchapter-B/part-571/subpart-B/section-571.213b"
        )
    }
}
