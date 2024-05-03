package com.notsatria.videoscroll.recyclerview

import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.notsatria.videoscroll.R
import com.notsatria.videoscroll.databinding.ItemPersonBinding
import com.notsatria.videoscroll.model.User
import com.notsatria.videoscroll.utils.StorageUtil
import com.xwray.groupie.viewbinding.BindableItem

class PersonItem(val user: User, private val context: Context) : BindableItem<ItemPersonBinding>() {
    override fun bind(viewBinding: ItemPersonBinding, position: Int) {
        with(viewBinding) {
            tvPersonName.text = user.name
            tvPersonEmail.text = user.email
            if (user.photoUrl != null)
                Glide.with(context)
                    .load(StorageUtil.pathToReference(user.photoUrl))
                    .placeholder(R.drawable.ic_account)
                    .into(ivProfile)
        }
    }

    override fun getLayout(): Int = R.layout.item_person
    override fun initializeViewBinding(view: View): ItemPersonBinding {
        return ItemPersonBinding.bind(view)
    }
}