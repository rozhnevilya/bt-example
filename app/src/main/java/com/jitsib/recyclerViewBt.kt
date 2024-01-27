package com.jitsib

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView

class recyclerViewBt (
    private val dataList: List<BluetoothDevice>,
    private val contex1: Context,
    private var itemClickListener: ((BluetoothDevice) -> Unit)? = null
) : RecyclerView.Adapter<recyclerViewBt.CustomViewHolder>() {

    // Инициализация кастомного ViewHolder
    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Пользовательный интерфейс
        val name = itemView.findViewById<TextView>(R.id.btDevice_name)
        val mac = itemView.findViewById<TextView>(R.id.btDevice_mac)
    }
    // Создание экземпляра на основе макета (.xml) - view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recyclerViewBt.CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return recyclerViewBt.CustomViewHolder(view)
    }
    // Связывание данных из списка с item
    // Заполнение экземпляра
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentItem = dataList[position]

        holder.name.text = currentItem.alias.toString()
        holder.mac.text = currentItem.address.toString()
        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(currentItem)
        }

    }
    // Возвращание количества элементов в списке данных
    override fun getItemCount() = dataList.size

    fun setOnItemClickListener(listener: (BluetoothDevice) -> Unit) {
        this.itemClickListener = listener
    }
}