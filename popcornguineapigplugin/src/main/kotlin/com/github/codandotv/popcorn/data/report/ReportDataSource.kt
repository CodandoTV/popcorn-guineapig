package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.ReportDto
import java.io.File

internal class ReportDataSource {
    fun export(fullPath: String, reportDto: ReportDto) {
        val reportPath = fullPath + File.separator + "popcornguineapig" + File.separator + System.currentTimeMillis()

        val reportDir = File(reportPath)

        if (!reportDir.exists()) {
            reportDir.mkdirs()
        }

        val reportFile = File(reportPath + File.separator + "report.md")

        val reportContent = reportDto.toMarkDownFormat()

        reportFile.bufferedWriter().use {
            it.write(reportContent)
        }
    }
}
