package com.mch.cryptodashboard.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mch.cryptodashboard.CryptoApp
import com.mch.cryptodashboard.Event
import com.mch.cryptodashboard.data.Tier
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import kotlin.system.measureTimeMillis

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val currencyRepository = getApplication<CryptoApp>().getCurrencyRepository()
    private val tierRepository = getApplication<CryptoApp>().getTierRepository()
    private val walletRepository = getApplication<CryptoApp>().getWalletRepository()

    private val currencies = currencyRepository.getCurrencies()
    private val tires = tierRepository.getTiers()
    private val wallets = walletRepository.getWallets()

    /**
     * Total balance at Top
     */
    private val _balanceLiveData = combine(wallets, tires) { wallets, tiers ->
        if (wallets.isEmpty() || tiers.isEmpty()) {
            null
        } else {
            var sum = BigDecimal(0)
            wallets.forEach { wallet ->
                tiers.find { wallet.currency == it.fromCurrency }?.rates?.maxByOrNull { it.amount }?.rate?.also {
                    sum += wallet.amount.multiply(it)
                }
            }
            sum
        }
    }.flowOn(Dispatchers.Default)
    val balanceLiveData = _balanceLiveData.asLiveData()

    /**
     * List item
     */
    private val listFlow = combine(currencies, tires, wallets,) { currencies, tires, wallets ->
        mutableListOf<CurrencyItem>().apply {
            currencies.forEach { currency ->
                val amount = wallets.find { currency.coinId == it.currency }?.amount
                // TODO: 2022/1/9 Not sure if max amount meet requirement
                val maxAmountRate = tires.find { currency.coinId == it.fromCurrency }?.rates?.maxByOrNull { it.amount }?.rate
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
            }
        }.sortedByDescending { it.balance }
    }.flowOn(Dispatchers.Default)
    val listLiveData = listFlow.asLiveData()

    /**
     * For swipe refresh
     */
    private val _spinner = MutableLiveData(false)
    val spinner: LiveData<Boolean> = _spinner

    /**
     * Display swipe refresh message while data not ready
     */
    val empty: LiveData<Boolean> = Transformations.map(listLiveData) {
        it.isEmpty() && _spinner.value == false
    }

    private val _errorMessage = MutableLiveData<Event<String?>>()
    val errorMessage: LiveData<Event<String?>> = _errorMessage

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, "handleException: $coroutineContext, $throwable")
    }


    init {
        refresh()
    }


    /**
     * Data fetch entry, launch in async and handle exception individually
     */
    fun refresh() {
        viewModelScope.launch(exceptionHandler) {
            _spinner.postValue(true)
            val time = measureTimeMillis {
                listOf(
                    async { runCatching { currencyRepository.refresh() }.onFailure { showErrorMessage(it) } },
                    async { runCatching { tierRepository.refresh() }.onFailure { showErrorMessage(it) } },
                    async { runCatching { walletRepository.refresh() }.onFailure { showErrorMessage(it) } }
                ).awaitAll()
            }
            Log.d(TAG, "refresh: spent: $time")
            _spinner.postValue(false)
        }
    }

    private fun showErrorMessage(throwable: Throwable) {
        Log.e(TAG, "showErrorMessage: $throwable")
        _errorMessage.value = Event(throwable.message)
    }


    companion object {
        private const val TAG = "MainViewModel"
    }
}