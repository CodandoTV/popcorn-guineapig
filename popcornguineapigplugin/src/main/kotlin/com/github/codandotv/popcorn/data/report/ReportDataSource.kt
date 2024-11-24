package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.ReportDto
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class ReportDataSource {
    suspend fun export(fullPath: String, reportDto: ReportDto) {
        suspendCoroutine { continuation ->
            val result = runCatching {
                val reportPath = fullPath +
                        File.separator +
                        "popcornguineapig" +
                        File.separator +
                        System.currentTimeMillis()

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

            if (result.isSuccess) {
                continuation.resume(Unit)
            } else {
                continuation.resumeWithException(PopcornGuineapigReportException(fullPath = fullPath))
            }
        }
    }
}

class PopcornGuineapigReportException(private val fullPath: String) : Throwable() {
    override val message: String
        get() = "Something went wrong to generate your report.Is there something wrong with the path $fullPath?"
}