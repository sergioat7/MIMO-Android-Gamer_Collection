package es.upsa.mimo.gamercollection.adapters

import es.upsa.mimo.gamercollection.models.FilterModel

interface OnFiltersSelected {
    fun filter(filters: FilterModel?)
}