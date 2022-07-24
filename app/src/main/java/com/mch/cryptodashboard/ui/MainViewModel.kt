package com.mch.cryptodashboard.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mch.cryptodashboard.data.CurrencyRepository
import com.mch.cryptodashboard.data.TierRepository
import com.mch.cryptodashboard.data.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class MainViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val tierRepository: TierRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

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
                    sum += BigDecimal(wallet.amount.toString()).multiply(BigDecimal(it))
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
                val amount = wallets.find { currency.coinId == it.currency }?.run { BigDecimal(amount.toString()) }
                val maxAmountRate = tires.find { currency.coinId == it.fromCurrency }?.rates?.maxByOrNull { it.amount.toInt() }?.run { BigDecimal(rate) }
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
    val spinner = MutableStateFlow(false)

    val error = MutableSharedFlow<String>()

    init {
        refresh()
    }


    /**
     * Data fetch entry, launch in async and handle exception individually
     */
    fun refresh() {
        viewModelScope.launch {
            spinner.value = true
            val time = measureTimeMillis {
                listOf(
                    async { currencyRepository.refresh() },
                    async { tierRepository.refresh() },
                    async { walletRepository.refresh() }
                ).awaitAll().forEach {
                    if (it.isFailure) showErrorMessage(it.exceptionOrNull())
                }
            }
            Log.d(TAG, "refresh spent: $time ms")
            spinner.value = false
        }
    }

    private fun showErrorMessage(throwable: Throwable?) {
        Log.e(TAG, "showErrorMessage: $throwable")
        viewModelScope.launch {
            throwable?.message?.also { error.emit(it) }
        }
    }


    companion object {
        private const val TAG = "MainViewModel"
    }
}