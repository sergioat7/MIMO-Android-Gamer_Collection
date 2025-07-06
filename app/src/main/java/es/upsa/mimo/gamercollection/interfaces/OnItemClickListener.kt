package es.upsa.mimo.gamercollection.interfaces

interface OnItemClickListener {
    fun onItemClick(id: Int)
    fun onSubItemClick(id: Int)
    fun onLoadMoreItemsClick()
}