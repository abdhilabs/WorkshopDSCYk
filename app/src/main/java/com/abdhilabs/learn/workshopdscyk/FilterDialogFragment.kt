package com.abdhilabs.learn.workshopdscyk

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.abdhilabs.learn.workshopdscyk.model.Restaurant
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.dialog_filters.*

class FilterDialogFragment : DialogFragment(), View.OnClickListener {

    companion object {
        const val TAG = "FilterDialog"
    }

    interface FilterListener {
        fun onFilter(filters: Filters)
    }

    private var mFilterListener: FilterListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_filters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_search.setOnClickListener(this)
        button_cancel.setOnClickListener(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FilterListener) {
            mFilterListener = context
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener!!.onFilter(getFilters())
        }
        dismiss()
    }

    private fun onCancelClicked() {
        dismiss()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.button_search -> onSearchClicked()
            R.id.button_cancel -> onCancelClicked()
        }
    }

    private fun getSelectedCategory(): String? {
        val selected = spinner_category.selectedItem as String
        return if (getString(R.string.value_any_category) == selected) {
            null
        } else {
            selected
        }
    }

    private fun getSelectedCity(): String? {
        val selected = spinner_city.selectedItem as String
        return if (getString(R.string.value_any_city) == selected) {
            null
        } else {
            selected
        }
    }

    private fun getSelectedPrice(): Int {
        return when (spinner_price.selectedItem as String) {
            getString(R.string.price_1) -> {
                1
            }
            getString(R.string.price_2) -> {
                2
            }
            getString(R.string.price_3) -> {
                3
            }
            else -> {
                -1
            }
        }
    }

    private fun getSelectedSortBy(): String? {
        val selected = spinner_sort.selectedItem as String
        if (getString(R.string.sort_by_rating) == selected) {
            return Restaurant.FIELD_AVG_RATING
        }
        if (getString(R.string.sort_by_price) == selected) {
            return Restaurant.FIELD_PRICE
        }
        return if (getString(R.string.sort_by_popularity) == selected) {
            Restaurant.FIELD_POPULARITY
        } else null
    }

    private fun getSortDirection(): Query.Direction? {
        val selected = spinner_sort.selectedItem as String
        if (getString(R.string.sort_by_rating) == selected) {
            return Query.Direction.DESCENDING
        }
        if (getString(R.string.sort_by_price) == selected) {
            return Query.Direction.ASCENDING
        }
        return if (getString(R.string.sort_by_popularity) == selected) {
            Query.Direction.DESCENDING
        } else null
    }

    fun resetFilters() {
        spinner_category.setSelection(0)
        spinner_city.setSelection(0)
        spinner_price.setSelection(0)
        spinner_sort.setSelection(0)
    }

    private fun getFilters(): Filters {
        val filters = Filters()
        filters.category = getSelectedCategory()
        filters.city = getSelectedCity()
        filters.price = getSelectedPrice()
        filters.sortBy = getSelectedSortBy()
        filters.sortDirection = getSortDirection()
        return filters
    }

}