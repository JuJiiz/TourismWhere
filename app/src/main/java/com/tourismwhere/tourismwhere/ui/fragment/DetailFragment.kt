package com.tourismwhere.tourismwhere.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tourismwhere.tourismwhere.R
import com.tourismwhere.tourismwhere.adapter.SliderAdapter
import com.tourismwhere.tourismwhere.model.AttractionModel
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment() {
    private lateinit var attractionModel: AttractionModel

    companion object {
        val Tag = DetailFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.also { arg ->
            attractionModel = arg.getParcelable("ATTRACTION")
        }

        tvName.text = "${attractionModel.name}"

        attractionModel.location?.also { location ->
            tvAddress.text =
                    "${if (location.amphur != null) location.amphur else "-"}, ${if (location.province != null) location.province else "-"}"
        }

        tvHistory.text = "${attractionModel.history}"

        attractionModel.contact?.also { contact ->
            contact_tel.text = if (contact.telnumber != null) contact.telnumber else "-"
            contact_website.text = if (contact.website != null) contact.website else "-"
            contact_email.text = if (contact.email != null) contact.email else "-"
        }

        val mAdapter = SliderAdapter()
        mAdapter.mImageList = attractionModel.images
        viewPager.adapter = mAdapter
        indicator.setupWithViewPager(viewPager, true)

    }
}
