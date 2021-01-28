package dev.shustoff.diffadapter

import android.view.ViewGroup

internal class HolderInfo(
    val createFromParent: (parent: ViewGroup) -> TypedViewHolder<*>,
    val viewType: Int,
    val itemsCountInRow: Int,
    val mutable: Boolean,
    val getId: (Any) -> Any
)