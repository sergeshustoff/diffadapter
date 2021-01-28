package dev.shustoff.diffadapter.utils

import androidx.recyclerview.widget.DiffUtil
import dev.shustoff.diffadapter.HolderInfo

internal class DiffCallback(
    private val oldItems: List<Any?>,
    private val newItems: List<Any?>,
    private val getHolderInfo: (Any) -> HolderInfo
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition] ?: return false
        val newItem = newItems[newItemPosition] ?: return false
        val oldHolder = getHolderInfo(oldItem)
        val newHolder = getHolderInfo(newItem)

        return if (oldHolder === newHolder) {
            oldHolder.getId(oldItem) == oldHolder.getId(newItem)
        } else {
            false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition] ?: return false
        val newItem = newItems[newItemPosition] ?: return false
        val oldHolder = getHolderInfo(oldItem)
        val newHolder = getHolderInfo(newItem)

        return if (oldHolder === newHolder) {
            !oldHolder.mutable && oldItem == newItem
        } else {
            false
        }
    }

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size
}