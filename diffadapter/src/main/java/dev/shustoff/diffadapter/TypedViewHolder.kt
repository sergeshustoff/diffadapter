package dev.shustoff.diffadapter

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class TypedViewHolder<T>(
    itemView: View,
    onItemClick: ((T) -> Unit)? = null
) : RecyclerView.ViewHolder(itemView) {

    protected val resources: Resources
        get() = itemView.resources

    protected val context: Context
        get() = itemView.context

    var item: T? = null
        private set

    init {
        if (onItemClick != null) {
            itemView.setOnClickListener {
                item?.let { onItemClick.invoke(it) }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun castAndBind(item: Any) {
        this.item = item as T
        bind(item)
    }

    protected abstract fun bind(item: T)
}
