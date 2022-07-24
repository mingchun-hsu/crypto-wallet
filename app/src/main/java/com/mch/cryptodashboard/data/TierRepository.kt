package com.mch.cryptodashboard.data

import com.mch.cryptodashboard.data.source.TierDao
import com.mch.cryptodashboard.data.source.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TierRepository @Inject constructor(
    private val service: WebService,
    private val dao: TierDao,
) {

    fun getTiers() = dao.getTiers()

    suspend fun refresh() = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            val list = service.getTires().tiers
            dao.insertAll(list)
        }
    }
}