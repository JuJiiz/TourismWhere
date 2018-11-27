package com.tourismwhere.tourismwhere.ui.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tourismwhere.tourismwhere.R
import com.tourismwhere.tourismwhere.adapter.SearchAdapter
import com.tourismwhere.tourismwhere.model.AttractionModel
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment(), SearchAdapter.OnItemClick {
    private var venues: ArrayList<AttractionModel>? = null
    private val adapter = SearchAdapter()

    interface OnItemClick {
        fun showAttraction(attraction: AttractionModel)
    }

    companion object {
        val Tag = SearchFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        venues = arguments?.getParcelableArrayList("ATTRACTION")
        venues = venues?.filter {
            it.name != null
        } as ArrayList<AttractionModel>
        init()


    }

    private fun init() {
        btnClose.setOnClickListener {
            fragmentManager?.popBackStack()
        }
        adapter.setOnItemClickListener(this)

        venues?.also { venues ->
            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable) {}

                override fun beforeTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                    val result = venues.filter {
                        it.name!!.contains(s, true)
                    }

                    if (p3 > 0) {
                        adapter.mSearchList = result
                    } else {
                        adapter.mSearchList = listOf()
                    }
                    rvSearch.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    rvSearch.adapter = adapter
                }
            })
        }
    }

    override fun showAttraction(attraction: AttractionModel) {
        if (context is OnItemClick) (context as OnItemClick).showAttraction(attraction)
    }
}
