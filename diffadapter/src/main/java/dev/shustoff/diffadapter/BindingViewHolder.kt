package dev.shustoff.diffadapter

import androidx.viewbinding.ViewBinding

class BindingViewHolder<T, Binding: ViewBinding>(
    private val binding: Binding,
    onItemClick: (Binding.(T) -> Unit)?,
    private val bind: (Binding, T) -> Unit
) : TypedViewHolder<T>(binding.root) {

    init {
        if (onItemClick != null) {
            binding.root.setOnClickListener {
                item?.let { onItemClick.invoke(binding, it) }
            }
        }
    }

    override fun bind(item: T) {
        bind.invoke(binding, item)
    }
}
