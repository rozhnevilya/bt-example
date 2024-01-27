package com.jitsib

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class recyclerViewMessage (
    private val dataList: List<DC_message_item>
) : RecyclerView.Adapter<recyclerViewMessage.CustomViewHolder>() {

    // Инициализация кастомного ViewHolder
    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Пользовательный интерфейс
        val time = itemView.findViewById<TextView>(R.id.textView_message_time)
        val word = itemView.findViewById<TextView>(R.id.textView_message_word)
    }
    // Создание экземпляра на основе макета (.xml) - view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recyclerViewMessage.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_message_item, parent, false)
        return recyclerViewMessage.CustomViewHolder(view)
    }
    // Связывание данных из списка с item
    // Заполнение экземпляра
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentItem = dataList[position]

        holder.time.text = currentItem.time
        holder.word.text = currentItem.message

    }
    // Возвращание количества элементов в списке данных
    override fun getItemCount() = dataList.size
}