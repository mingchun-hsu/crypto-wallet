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

    private val currencies = currencyRepository.getCurrencies().asLiveData()

    /**
     * Query with supported currency only
     */
    private val tires = currencies.switchMap { currencies ->
        val supportedList = currencies.map { it.coinId }
        tierRepository.getTiers(supportedList).asLiveData()
    }

    private val wallets = walletRepository.getWallets().asLiveData()

    /**
     * Total balance at Top
     */
    val balanceLiveData = MutableLiveData<BigDecimal?>()

    /**
     * List item of supported currency with calculated data, observe of related data
     */
    val listLiveData = MediatorLiveData<List<CurrencyItem>>().apply {
        addSource(currencies) { composeData() }
        addSource(tires) { composeData() }
        addSource(wallets) { composeData() }
    }

    /**
     * For swipe refresh
     */
    val spinner = MutableLiveData(false)

    /**
     * For cancel unnecessary jobs
     */
    private var job: Job? = null


    init {
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

    /**
     * Trigger when data changes
     */
    private fun composeData() {
        job?.cancel()
        job = viewModelScope.launch {
            val time = measureTimeMillis {
                var hasBalance = false
                var total = BigDecimal(0)
                val list = mutableListOf<CurrencyItem>().apply {
                    currencies.value?.forEach { currency ->
                        val amount = findWalletAmount(currency.coinId)
                        val maxAmountRate = findRate(currency.coinId)
                        val balance = amount?.let { maxAmountRate?.multiply(it) }

                        val item = CurrencyItem(
                            currency.coinId,
                            currency.colorfulImageURL,
                            currency.name,
                            currency.symbol,
                            amount,
                            balance
                        )
                        add(item)

                        // Calc total balance
                        balance?.also {
                            total += it
                            hasBalance = true
                        }
                    }
                }
                listLiveData.postValue(list)
                balanceLiveData.postValue(if (hasBalance) total else null)
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

    /**
     * Not sure how to choose if there are more than one
     */
    private suspend fun findRate(currency: String): BigDecimal? = withContext(Dispatchers.Default) {
        tires.value?.find {
            ensureActive()
            it.fromCurrency == currency
        }?.rates?.maxByOrNull { it.amount }?.rate
    }



    companion object {
        private const val TAG = "MainViewModel"
    }
}