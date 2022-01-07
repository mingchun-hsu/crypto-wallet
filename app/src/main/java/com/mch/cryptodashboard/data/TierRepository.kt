package com.mch.cryptodashboard.data

import com.mch.cryptodashboard.data.source.TierDao
import com.mch.cryptodashboard.data.source.WebService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TierRepository(
    private val service: WebService,
    private val dao: TierDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getTiers(supportedList: List<String>) = dao.getTiers(supportedList)

    suspend fun refresh() = withContext(ioDispatcher) {
        val list = service.getTires().tiers
        dao.insertAll(list)
    }
}