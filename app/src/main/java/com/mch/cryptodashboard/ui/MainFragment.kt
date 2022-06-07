package com.mch.cryptodashboard.ui

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.text.scale
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.mch.cryptodashboard.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.NumberFormat

class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel by viewModels<MainViewModel>()

    private val adapter by lazy { ItemAdapter(Glide.with(this)) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout).apply {
            setOnRefreshListener { viewModel.refresh() }
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.spinner.collectLatest {
                        isRefreshing = it
                    }
                }
            }
        }

        view.findViewById<RecyclerView>(R.id.recycler_view).adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collectLatest {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.balanceFlow.collectLatest {
                    Log.d(TAG, "balance collectLatest: $it")
                    view.findViewById<TextView>(R.id.balance).text = getMarkUpTotalBalance(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listFlow.collectLatest {
                    Log.d(TAG, "list collectLatest: ${it.size}")
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun getMarkUpTotalBalance(walletBalance: BigDecimal?): SpannableStringBuilder {
        val whiteColor = ContextCompat.getColor(requireContext(), R.color.white)
        val formatWalletBalanceInString = walletBalance?.let { NumberFormat.getNumberInstance().format(it) } ?: "--"
        return SpannableStringBuilder()
            .append("$ ")
            .bold { scale(1.2f) { color(whiteColor) { append(formatWalletBalanceInString) } } }
            .append(" USD")
    }


    companion object {
        private const val TAG = "MainFragment"

        fun newInstance() = MainFragment()
    }
}