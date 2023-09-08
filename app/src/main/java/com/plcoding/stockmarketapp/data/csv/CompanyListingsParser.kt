package com.plcoding.stockmarketapp.data.csv

import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.domain.model.CompanyListingModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanyListingsParser @Inject constructor() : CsvParser<CompanyListingModel> {
    override suspend fun parse(stream: InputStream): List<CompanyListingModel> {
        val csvReader = CSVReader(InputStreamReader(stream))
        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull {it ->
                    val symbol = it.getOrNull(0)
                    val name = it.getOrNull(1)
                    val exchange = it.getOrNull(2)
                    CompanyListingModel(
                        symbol = symbol ?: return@mapNotNull null,
                        name = name ?: return@mapNotNull null,
                        exchange = exchange ?: return@mapNotNull null,
                    )
                }
                .also {
                    csvReader.close()
                }
        }
    }
}