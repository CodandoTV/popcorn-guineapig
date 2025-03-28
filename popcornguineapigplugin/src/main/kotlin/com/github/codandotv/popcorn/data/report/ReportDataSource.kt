package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.ReportDto
import java.io.File

internal class ReportDataSource {

    fun export(fullPath: String, reportDto: ReportDto) {
        val result = runCatching {
            val reportPath = fullPath +
                    File.separator +
                    "reports" +
                    File.separator +
                    "popcornguineapig"

            val reportFile = File(
                reportPath +
                        File.separator +
                        reportDto.dateTimestamp +
                        "_" +
                        reportDto.moduleName +
                        ".md"
            )

            if (!reportFile.exists()) {
                reportFile.createNewFile()
            }

            val reportContent = reportDto.toMarkDownFormat()

            reportFile.bufferedWriter().use {
                it.write(reportContent)
            }
        }

        if (result.isFailure) {
            throw PopcornGuineapigReportException(fullPath = fullPath)
        }
    }
}

class PopcornGuineapigReportException(private val fullPath: String) : Throwable() {
    override val message: String
        get() = "Something went wrong to generate your report.Is there something wrong with the path $fullPath?"
}
