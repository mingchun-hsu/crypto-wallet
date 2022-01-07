package com.mch.cryptodashboard.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mch.cryptodashboard.CryptoApp
import com.mch.cryptodashboard.Event
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
    private val _balanceLiveData = MutableLiveData<BigDecimal?>()
    val balanceLiveData: LiveData<BigDecimal?> = _balanceLiveData

    /**
     * List item of supported currency with calculated data, trigger by other related data
     */
    private val _listLiveData = MediatorLiveData<List<CurrencyItem>>().apply {
        addSource(currencies) { composeData() }
        addSource(tires) { composeData() }
        addSource(wallets) { composeData() }
    }
    val listLiveData: LiveData<List<CurrencyItem>> = _listLiveData

    /**
     * Display swipe refresh message while data not ready
     */
    val empty: LiveData<Boolean> = Transformations.map(_listLiveData) {
        it.isEmpty() && _spinner.value == false
    }

    /**
     * For swipe refresh
     */
    private val _spinner = MutableLiveData(false)
    val spinner: LiveData<Boolean> = _spinner

    private val _errorMessage = MutableLiveData<Event<String?>>()
    val errorMessage: LiveData<Event<String?>> = _errorMessage

    /**
     * For cancel unnecessary jobs
     */
    private var job: Job? = null

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
                    async { kotlin.runCatching { currencyRepository.refresh() }.onFailure { showErrorMessage(it) } },
                    async { kotlin.runCatching { tierRepository.refresh() }.onFailure { showErrorMessage(it) } },
                    async { kotlin.runCatching { walletRepository.refresh() }.onFailure { showErrorMessage(it) } },
                ).awaitAll()
            }
            Log.d(TAG, "refresh: spent: $time")
            _spinner.postValue(false)
        }
    }

    /**
     * Trigger when data changes, dispatch to default
     */
    private fun composeData() {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.Default + exceptionHandler) {
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

                list.sortByDescending { it.balance }
                _listLiveData.postValue(list)
                _balanceLiveData.postValue(if (hasBalance) total else null)
            }
            Log.d(TAG, "composeData: spent $time | ${currencies.value?.size}, ${tires.value?.size}, ${wallets.value?.size}")
        }
    }

    private suspend fun findWalletAmount(currency: String): BigDecimal? = coroutineScope {
        wallets.value?.find {
            ensureActive()
            it.currency == currency
        }?.amount
    }

    /**
     * TODO Not sure how to choose if there are more than one
     */
    private suspend fun findRate(currency: String): BigDecimal? = coroutineScope {
        tires.value?.find {
            ensureActive()
            it.fromCurrency == currency
        }?.rates?.maxByOrNull { it.amount }?.rate
    }

    private fun showErrorMessage(throwable: Throwable) {
        Log.e(TAG, "showErrorMessage: $throwable")
        _errorMessage.value = Event(throwable.message)
    }


    companion object {
        private const val TAG = "MainViewModel"
    }
}