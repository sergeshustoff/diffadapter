package dev.shustoff.diffadapter.utils

import androidx.recyclerview.widget.GridLayoutManager

internal class CustomSpanSizeLookup(
    private val layoutManager: GridLayoutManager,
    private val getItemsCountInRow: (Int) -> Int
) : GridLayoutManager.SpanSizeLookup() {

    init {
        isSpanIndexCacheEnabled = true
    }

    override fun getSpanSize(position: Int): Int {
        return layoutManager.spanCount / getItemsCountInRow.invoke(position)
    }
}