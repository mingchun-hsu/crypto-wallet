package com.mch.cryptodashboard.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.mch.cryptodashboard.CryptoApp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = getApplication<CryptoApp>().getRepository()

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(TAG, "handleException: $throwable")
    }

    private val currency =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO + exceptionHandler) {
            emit(repository.getCurrency())
        }

    private val rate =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO + exceptionHandler) {
            emit(repository.getRate())
        }

    private val wallet =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO + exceptionHandler) {
            emit(repository.getWallet())
        }


    val list = MediatorLiveData<List<CurrencyItem>>()

    private var job: Job? = null


    init {
        list.addSource(currency) { composeData() }
        list.addSource(rate) { composeData() }
        list.addSource(wallet) { composeData() }
    }


    private fun composeData() {
        job?.cancel()
        job = viewModelScope.launch {
            list.postValue(mutableListOf<CurrencyItem>().apply {
                currency.value?.forEach { currency ->
                    val amount = wallet.value?.find { it.currency == currency.coinId }?.amount
                    val usd =
                        rate.value?.find { it.fromCurrency == currency.coinId }?.rates?.first()?.rate?.multiply(
                            amount
                        )
                    val item = CurrencyItem(
                        currency.coinId,
                        currency.colorfulImageURL,
                        currency.name,
                        currency.symbol,
                        amount,
                        usd
                    )
                    add(item)
                }
            })
        }
    }


    companion object {
        private const val TAG = "MainViewModel"
    }
}