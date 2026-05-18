package com.github.codandotv.popcorn.data.report

import java.io.File

internal class ReportDataSource {

    fun exportErrorReportInMarkdown(fullPath: String, reportContent: String) {
        val result = runCatching {
            val reportPath = File(
                fullPath.plus(File.separator)
                    .plus("reports")
                    .plus(File.separator)
                    .plus("popcornguineapig")
            )
            if (reportPath.exists().not()) {
                reportPath.mkdirs()
            }

            val reportFile = File(
                reportPath.path
                    .plus(File.separator)
                    .plus("errorReport.md")
            )

            // If exists replace it
            if (reportFile.exists()) {
                reportFile.delete()
            }

            reportFile.createNewFile()

            reportFile.bufferedWriter().use {
                it.write(reportContent)
            }
        }

        if (result.isFailure) {
            throw PopcornGuineapigReportException(fullPath = fullPath)
        }
    }

    fun exportMetricsReportInCsv(fullPath: String, reportContent: String) {
        val result = runCatching {
            val reportPath = File(
                fullPath.plus(File.separator)
                    .plus("reports")
                    .plus(File.separator)
                    .plus("metrics")
            )
            if (reportPath.exists().not()) {
                reportPath.mkdirs()
            }

            val reportFile = File(
                reportPath.path
                    .plus(File.separator)
                    .plus("metrics.csv")
            )

            // If exists replace it
            if (reportFile.exists()) {
                reportFile.delete()
            }

            reportFile.createNewFile()

            reportFile.bufferedWriter().use {
                it.write(reportContent)
            }
        }

        if (result.isFailure) {
            throw PopcornGuineapigReportException(fullPath = fullPath)
        }
    }
}

internal class PopcornGuineapigReportException(private val fullPath: String) : Throwable() {
    override val message: String
        get() = "Something went wrong to generate your report.Is there something wrong with the path $fullPath?"
}
