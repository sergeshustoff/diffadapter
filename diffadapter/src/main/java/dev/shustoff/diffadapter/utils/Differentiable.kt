package dev.shustoff.diffadapter.utils

interface Differentiable {

    fun isSame(other: Any): Boolean

    fun isContentSame(other: Any): Boolean
}