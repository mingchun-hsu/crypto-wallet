package com.mch.cryptodashboard.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class TierRepository(
    private val service: WebService,
    private val dao: TierDao
) {

    fun getTiers(supportedList: List<String>) = dao.getTiers(supportedList)

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val list = service.getRate().tiers
        dao.insertAll(list)
    }
}