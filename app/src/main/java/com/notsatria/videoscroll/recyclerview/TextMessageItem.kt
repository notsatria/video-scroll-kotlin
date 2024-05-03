package com.notsatria.videoscroll.recyclerview

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.notsatria.videoscroll.R
import com.notsatria.videoscroll.databinding.ItemTextMessageBinding
import com.notsatria.videoscroll.model.TextMessage
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.viewbinding.BindableItem
import java.text.SimpleDateFormat
import java.util.Locale

class TextMessageItem(val message: TextMessage, val context: Context) :
    Item() {
    override fun getLayout(): Int = R.layout.item_text_message

    override fun isSameAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is TextMessageItem) return false
        if (other.message != message) return false
        return true
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.apply {
            val viewBinding = ItemTextMessageBinding.bind(this)
            viewBinding.tvMessage.text = message.text
            setTimeText(viewBinding)
            setMessageRootGravity(viewBinding)
        }
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as TextMessageItem)
    }

    private fun setTimeText(viewBinding: ItemTextMessageBinding) {
        val dateFormat = SimpleDateFormat("HH:mm dd MMM yy", Locale("id", "ID"))
        viewBinding.tvTime.text = dateFormat.format(message.time)
    }

    private fun setMessageRootGravity(viewBinding: ItemTextMessageBinding) {
        if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            viewBinding.messageRoot.apply {
                setBackgroundResource(R.drawable.rounded_rect_teal)
                viewBinding.tvMessage.setTextColor(context.getColor(R.color.white))
                viewBinding.tvTime.setTextColor(context.getColor(R.color.white))
                (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.END
            }
        } else {
            viewBinding.messageRoot.apply {
                setBackgroundResource(R.drawable.rounded_rect_white)
                (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.START
            }
        }
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }


}