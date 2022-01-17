package com.mch.cryptodashboard.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mch.cryptodashboard.CryptoApp
import com.mch.cryptodashboard.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import kotlin.system.measureTimeMillis

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val currencyRepository = getApplication<CryptoApp>().getCurrencyRepository()
    private val tierRepository = getApplication<CryptoApp>().getTierRepository()
    private val walletRepository = getApplication<CryptoApp>().getWalletRepository()

    private val currencies = currencyRepository.getCurrencies()
    private val tiers = tierRepository.getTiers()
    private val wallets = walletRepository.getWallets()

    /**
     * Total balance at Top
     */
    private val _balanceFlow = combine(wallets, tiers) { wallets, tiers ->
        Log.d(TAG, "balance combine: ${wallets.size}, ${tiers.size}")
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
    val balanceFlow = _balanceFlow

    /**
     * List item
     */
    private val _listFlow = combine(currencies, tiers, wallets) { currencies, tires, wallets ->
        Log.d(TAG, "list combine: ${currencies.size}, ${tires.size}, ${wallets.size}")
        mutableListOf<CurrencyItem>().apply {
            currencies.forEach { currency ->
                val amount = wallets.find { currency.coinId == it.currency }?.amount
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
    val listFlow = _listFlow

    /**
     * For swipe refresh
     */
    private val _spinner = MutableLiveData(false)
    val spinner: LiveData<Boolean> = _spinner

    private val _errorMessage = MutableLiveData<Event<String?>>()
    val errorMessage: LiveData<Event<String?>> = _errorMessage


    init {
        refresh()
    }


    /**
     * Data fetch entry, launch in async and handle exception individually
     */
    fun refresh() {
        viewModelScope.launch {
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