package dev.shustoff.diffadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

class DiffAdapterBuilder internal constructor(
    private val defineGridSpans: Boolean
) {
    private val holderCreatorsRegistry = HashMap<Class<out Any>, HolderInfo>()
    private val typesRegistry = HashMap<Int, HolderInfo>()

    inline fun <reified T : Any> forType() = DiffAdapterTypeBinder(clazz = T::class)

    fun <T : Any> bindHolderInternal(
        classes: List<KClass<out T>>,
        getId: (T) -> Any,
        mutable: Boolean = false,
        itemsCountInRow: Int = 1,
        createFromParent: (parent: ViewGroup) -> TypedViewHolder<T>
    ): DiffAdapterBuilder {
        val holderInfo =
            HolderInfo(
                createFromParent,
                typesRegistry.size,
                itemsCountInRow,
                mutable,
                { getId(it as T) }
            )

        classes.forEach {
            holderCreatorsRegistry[it.java] = holderInfo
        }
        typesRegistry[holderInfo.viewType] = holderInfo
        return this
    }

    fun build(): DiffAdapter = DiffAdapter(holderCreatorsRegistry, typesRegistry, defineGridSpans)

    inner class DiffAdapterTypeBinder<T : Any>(
        private val clazz: KClass<T>
    ) {
        private val types = mutableListOf<KClass<out T>>(clazz)
        private var mutable = false
        private var getId: (T) -> Any = { it }

        fun includingSubtypes(vararg subtypes: KClass<out T>): DiffAdapterTypeBinder<T> {
            types.addAll(subtypes)
            return this
        }

        fun alwaysChanged(): DiffAdapterTypeBinder<T> {
            mutable = true
            return this
        }

        fun withId(getId: (T) -> Any): DiffAdapterTypeBinder<T> {
            this.getId = getId
            return this
        }

        fun allInstancesAreSame(): DiffAdapterTypeBinder<T> {
            this.getId = { clazz }
            return this
        }

        fun <Binding : ViewBinding> holder(
            inflateBinding: (LayoutInflater, ViewGroup, Boolean) -> Binding,
            itemsCountInRow: Int = 1,
            createHolder: (Binding) -> TypedViewHolder<T>
        ): DiffAdapterBuilder {
            return bindHolderInternal(types, getId, mutable, itemsCountInRow) { parent ->
                val binding = inflateBinding.invoke(LayoutInflater.from(parent.context), parent, false)
                createHolder.invoke(binding)
            }
        }

        fun <Binding : ViewBinding> binding(
            inflateBinding: (LayoutInflater, ViewGroup, Boolean) -> Binding,
            onClick: (Binding.(T) -> Unit)? = null,
            init: Binding.() -> Unit = { },
            bind: Binding.(T) -> Unit = { },
            itemsCountInRow: Int = 1
        ): DiffAdapterBuilder {
            return bindHolderInternal(types, getId, mutable, itemsCountInRow) { parent ->
                val binding = inflateBinding.invoke(LayoutInflater.from(parent.context), parent, false)
                init.invoke(binding)
                BindingViewHolder(binding, onClick, bind)
            }
        }
    }
}

fun diffAdapterBuilder(defineGridSpans: Boolean = true) = DiffAdapterBuilder(defineGridSpans)