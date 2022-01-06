package com.mch.cryptodashboard.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class TierRepository(
    private val service: WebService,
    private val dao: TierDao
) {

    fun getTiers() = dao.getTiers()

    suspend fun refresh() = withContext(Dispatchers.IO) {
        delay((100L..2000L).random())
        val list = service.getRate().tiers
        dao.insertAll(list)
    }
}