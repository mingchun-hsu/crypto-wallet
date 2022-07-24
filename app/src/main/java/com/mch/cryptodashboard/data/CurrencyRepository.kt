package com.mch.cryptodashboard.data

import com.mch.cryptodashboard.data.source.CurrencyDao
import com.mch.cryptodashboard.data.source.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val service: WebService,
    private val dao: CurrencyDao,
) {

    fun getCurrencies() = dao.getCurrencies()

    suspend fun refresh() = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            val list = service.getCurrencies().currencies
            dao.insertAll(list)
        }
    }
}