package com.tourismwhere.tourismwhere.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tourismwhere.tourismwhere.R
import javax.inject.Inject


class SliderAdapter @Inject constructor() : PagerAdapter() {
    var mImageList: List<String> = listOf()

    override fun getCount(): Int {
        return mImageList.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = container.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.image_recycler_view_item, null)
        val imvImage: ImageView = view.findViewById(R.id.imvImage)

        Glide.with(container.context)
            .load(mImageList[position])
            .apply(RequestOptions().dontAnimate())
            .into(imvImage)

        val viewPager = container as ViewPager
        viewPager.addView(view, 0)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val viewPager = container as ViewPager
        val view = obj as View
        viewPager.removeView(view)
    }
}