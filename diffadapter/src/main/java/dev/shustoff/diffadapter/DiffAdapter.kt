package dev.shustoff.diffadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dev.shustoff.diffadapter.utils.DiffCallback
import dev.shustoff.diffadapter.utils.CustomSpanSizeLookup

class DiffAdapter internal constructor(
    private val holderCreatorsRegistry: HashMap<Class<out Any>, HolderInfo>,
    private val typesRegistry: HashMap<Int, HolderInfo>,
    private val defineGridSpans: Boolean
) : RecyclerView.Adapter<TypedViewHolder<*>>() {

    var items: List<Any> = emptyList()
        set(value) {
            val oldItems = field
            if (oldItems !== value) {
                field = value

                DiffUtil.calculateDiff(DiffCallback(oldItems, items, ::getHolderInfoByItem), true)
                    .dispatchUpdatesTo(this)
            }
        }

    fun show(newItems: List<Any>) {
        items = newItems
    }

    override fun getItemViewType(position: Int): Int =
        getItemHolderInfoByPosition(position).viewType

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        val layoutManager = recyclerView.layoutManager
            ?: throw IllegalStateException("set layout manager before adapter")

        if (defineGridSpans) {
            (layoutManager as? GridLayoutManager)?.let {
                it.spanSizeLookup = CustomSpanSizeLookup(it, ::getItemsCountInRow)

                var span = 1
                typesRegistry.values.map { it.itemsCountInRow }.distinct()
                    .forEach {
                        if (it > 1 && span.rem(it) != 0) {
                            span *= it
                        }
                    }
                it.spanCount = span
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypedViewHolder<*> {
        val holderInfo = typesRegistry[viewType]!!
        return holderInfo.createFromParent.invoke(parent).also {
            (it.itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)
                ?.isFullSpan = holderInfo.itemsCountInRow == 1
        }
    }

    override fun onBindViewHolder(holder: TypedViewHolder<*>, position: Int) {
        holder.castAndBind(items[position])
    }

    override fun getItemCount() = items.size

    private fun getItemsCountInRow(position: Int): Int = getItemHolderInfoByPosition(position).itemsCountInRow

    private fun getItemHolderInfoByPosition(position: Int): HolderInfo {
        val item = items[position]
        return getHolderInfoByItem(item)
    }

    private fun getHolderInfoByItem(item: Any) = (holderCreatorsRegistry[item.javaClass]
        ?: throw IllegalArgumentException("Class not supported: ${item.javaClass.name}"))
}