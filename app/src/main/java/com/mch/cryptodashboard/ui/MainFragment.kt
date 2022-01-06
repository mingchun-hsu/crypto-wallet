package com.mch.cryptodashboard.ui

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.text.scale
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.mch.cryptodashboard.R
import java.math.BigDecimal
import java.text.NumberFormat

class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel by viewModels<MainViewModel>()

    private val adapter by lazy { ItemAdapter(Glide.with(this)) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout).apply {
            viewModel.spinner.observe(viewLifecycleOwner) { isRefreshing = it }
            setOnRefreshListener { viewModel.refresh() }
        }

        view.findViewById<RecyclerView>(R.id.recycler_view).adapter = adapter

        viewModel.listLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.balanceLiveData.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.balance).text = getMarkUpTotalBalance(it)
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
        fun newInstance() = MainFragment()
    }
}