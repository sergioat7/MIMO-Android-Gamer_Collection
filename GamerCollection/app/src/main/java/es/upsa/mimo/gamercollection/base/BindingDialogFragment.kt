/*
 * Copyright (c) 2022 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 9/3/2022
 */

package es.upsa.mimo.gamercollection.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import es.upsa.mimo.gamercollection.R
import java.lang.reflect.ParameterizedType

abstract class BindingDialogFragment<Binding : ViewDataBinding> : DialogFragment() {

    //region Protected properties
    protected lateinit var binding: Binding
        private set
    protected abstract val transparentStyle: Boolean
    //endregion

    //region Lifecycle methods
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bindingType =
            (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
                .firstOrNull {
                    (it as? Class<*>)?.let { clazz ->
                        ViewDataBinding::class.java.isAssignableFrom(
                            clazz
                        )
                    } == true
                }
                ?: error("Class is not parametrized with ViewDataBinding subclass")
        val inflateMethod = (bindingType as Class<*>).getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.javaPrimitiveType
        )
        @Suppress("UNCHECKED_CAST")
        binding = inflateMethod.invoke(null, inflater, container, false) as Binding
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (transparentStyle) {
            setStyle(STYLE_NO_TITLE, R.style.Theme_GamerCollection_DialogTransparent)
        }
    }
    //endregion
}