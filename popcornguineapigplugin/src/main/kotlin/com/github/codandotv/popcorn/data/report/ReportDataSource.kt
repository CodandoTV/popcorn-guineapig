package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.ReportDto
import java.io.File
import java.util.Calendar

internal class ReportDataSource {

    fun export(fullPath: String, reportDto: ReportDto) {
        val result = runCatching {
            val reportPath = fullPath +
                    File.separator +
                    "reports" +
                    File.separator +
                    "popcornguineapig"

            val reportDir = File(reportPath)

            if (!reportDir.exists()) {
                reportDir.mkdirs()
            }

            val dateTimestamp = Calendar.getInstance().dateTimestamp()
            val reportFile = File(reportPath + File.separator + dateTimestamp + "_" + reportDto.moduleName + ".md")

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

internal fun Calendar.dateTimestamp(): String {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)

    return "$year-$month-$dayOfMonth" + "_" + "$hourOfDay-$minute-$second"
}
