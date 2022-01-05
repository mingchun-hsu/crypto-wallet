package com.mch.cryptodashboard

import android.app.Application
import com.mch.cryptodashboard.data.CurrencyRepository
import com.mch.cryptodashboard.data.FakeService

class CryptoApp : Application() {

    private fun createWebService() = FakeService()

    fun getRepository() = CurrencyRepository(createWebService())
}