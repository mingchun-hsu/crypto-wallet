package com.mch.cryptodashboard.ui

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.mch.cryptodashboard.CryptoApp
import kotlinx.coroutines.*
import java.math.BigDecimal
import kotlin.system.measureTimeMillis

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val currencyRepository = getApplication<CryptoApp>().getCurrencyRepository()
    private val tierRepository = getApplication<CryptoApp>().getTierRepository()
    private val walletRepository = getApplication<CryptoApp>().getWalletRepository()

    private var job: Job? = null

    private val currencies = currencyRepository.getCurrency().asLiveData()

    private val tires = tierRepository.getTiers().asLiveData()

    private val wallets = walletRepository.getWallets().asLiveData()

    val listLiveData = MediatorLiveData<List<CurrencyItem>>()

    val spinner = MutableLiveData(false)


    init {
        listLiveData.addSource(currencies) { composeData() }
        listLiveData.addSource(tires) { composeData() }
        listLiveData.addSource(wallets) { composeData() }

        refresh()
    }


    fun refresh() {
        viewModelScope.launch {
            try {
                spinner.postValue(true)
                val time = measureTimeMillis {
                    listOf(
                        async { currencyRepository.refresh() },
                        async { tierRepository.refresh() },
                        async { walletRepository.refresh() }
                    ).awaitAll()
                }
                Log.d(TAG, "refresh: spent: $time")
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                spinner.postValue(false)
            }
        }
    }

    private fun composeData() {
        job?.cancel()
        job = viewModelScope.launch {
            val time = measureTimeMillis {
                val list = mutableListOf<CurrencyItem>().apply {
                    currencies.value?.forEach { currency ->
                        val amount = findWalletAmount(currency.coinId)
                        val maxAmountRate = findMaxRate(currency.coinId)
                        val item = CurrencyItem(
                            currency.coinId,
                            currency.colorfulImageURL,
                            currency.name,
                            currency.symbol,
                            amount,
                            amount?.let { maxAmountRate?.multiply(it) }
                        )
                        add(item)
                    }
                }
                listLiveData.postValue(list)
            }
            Log.d(TAG, "composeData: spent $time")
        }
    }

    private suspend fun findWalletAmount(currency: String): BigDecimal? = withContext(Dispatchers.Default) {
        wallets.value?.find {
            ensureActive()
            it.currency == currency
        }?.amount
    }

    private suspend fun findMaxRate(currency: String): BigDecimal? = withContext(Dispatchers.Default) {
        tires.value?.find {
            ensureActive()
            it.fromCurrency == currency
        }?.rates?.maxByOrNull { it.amount }?.rate
    }



    companion object {
        private const val TAG = "MainViewModel"
    }
}