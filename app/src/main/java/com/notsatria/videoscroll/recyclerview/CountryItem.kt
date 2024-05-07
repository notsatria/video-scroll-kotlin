package com.notsatria.videoscroll.recyclerview

import android.content.Context
import android.widget.ArrayAdapter
import com.notsatria.videoscroll.R
import com.notsatria.videoscroll.databinding.CountryCardItemBinding
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class CountryItem(
    val countryName: String,
    val context: Context,
    val onItemDelete: OnItemDeleteClickListener
) : Item() {
    interface OnItemDeleteClickListener {
        fun onItemDelete(countryName: String)
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            val viewBinding = CountryCardItemBinding.bind(this)
            viewBinding.tvCountryName.text = countryName
            viewBinding.ivDeleteCountry.setImageResource(R.drawable.ic_close)
            viewBinding.ivDeleteCountry.setOnClickListener {
                onItemDelete.onItemDelete(countryName)
            }
        }
    }

    override fun getLayout(): Int = R.layout.country_card_item

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is CountryItem) return false
        if (other.countryName != countryName) return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as CountryItem)
    }

    override fun hashCode(): Int {
        var result = countryName.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }

}