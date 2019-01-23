package com.tourismwhere.tourismwhere.ui.fragment


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tourismwhere.tourismwhere.R
import kotlinx.android.synthetic.main.fragment_radius_dialog.*

class RadiusDialogFragment : DialogFragment() {
    private var radius = 25
    private val minRadius = 1
    private val maxRadius = 50
    private var callback: OnRadiusSetting? = null

    interface OnRadiusSetting {
        fun updateRadius(newRadius: Int)
    }

    fun setOnRadiusSetting(callback: OnRadiusSetting) {
        this.callback = callback
    }

    companion object {
        val Tag = RadiusDialogFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_radius_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.also {
            radius = it.getInt("RADIUS", 25)
        }
        tvRadius.text = "$radius"

        btnDecrease.setOnClickListener {
            if (radius > minRadius) {
                radius -= 1
                tvRadius.text = "$radius"
            }
        }

        btnIncrease.setOnClickListener {
            if (radius < maxRadius) {
                radius += 1
                tvRadius.text = "$radius"
            }
        }

        btnSetRadius.setOnClickListener {
            callback?.updateRadius(radius)
            this.dismiss()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.MoveFromBottom
    }


}
