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

    fun getTiers() = dao.getTiers()

    suspend fun refresh() = withContext(ioDispatcher) {
        try {
            val list = service.getTires().tiers
            dao.insertAll(list)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}