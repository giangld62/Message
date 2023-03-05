package vn.tapbi.message.ui.dialog

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import vn.tapbi.message.R
import vn.tapbi.message.common.Constant
import vn.tapbi.message.databinding.DialogRateAppBinding
import vn.tapbi.message.ui.adapter.RateAppAdapter
import vn.tapbi.message.ui.base.BaseBindingDialogFragment


class RateAppDialogFragment(private val listener: RateAppDialogListener) :
    BaseBindingDialogFragment<DialogRateAppBinding>() {
    private var _rateAppAdapter: RateAppAdapter? = null
    private val rateAppAdapter get() = _rateAppAdapter!!
    private var numberOfRatedStars = Constant.DEFAULT_RATED_STARS

    override val layoutId: Int
        get() = R.layout.dialog_rate_app

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        evenClick()
        initData()
    }

    private fun initView() {
        _rateAppAdapter = RateAppAdapter(Constant.STAR_LIST)
        binding.rvStar.adapter = rateAppAdapter
        binding.rvStar.setHasFixedSize(true)
    }

    private fun evenClick() {
        binding.btnRateNow.setOnClickListener {
            listener.onRateAppClick(numberOfRatedStars)
            openMarket()
            dismiss()
        }

        binding.ivCancel.setOnClickListener {
            dismiss()
        }

        rateAppAdapter.setOnItemClickListener {
            numberOfRatedStars = it
        }
    }

    private fun openMarket() {
        try {
            context?.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + Constant.PACKAGE_NAME)
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            context?.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        Constant.link_share_app + Constant.PACKAGE_NAME
                    )
                )
            )
        }
    }


    private fun initData() {
        rateAppAdapter.setCheckedStar(listener.getRatedStars())
    }

    interface RateAppDialogListener {
        fun onRateAppClick(numberOfRatedStars: Int)
        fun getRatedStars(): Int
    }

    override fun onDestroyView() {
        _rateAppAdapter = null
        super.onDestroyView()
    }
}