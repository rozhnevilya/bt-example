package com.jitsib

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream

class ReadThread(private val socket: BluetoothSocket, private val callback: (String) -> Unit) : Thread() {
    private val inputStream: InputStream = socket.inputStream

    override fun run() {
        val buffer = ByteArray(1024)
        val stringBuilder = StringBuilder()

        try {
            while (true) {
                val bytesRead = inputStream.read(buffer)
                if (bytesRead == -1) {
                    // Достигнут конец потока
                    break
                }

                val receivedData = String(buffer, 0, bytesRead)
                Log.e("bt receivedData", receivedData)
                stringBuilder.append(receivedData)

                if (receivedData.contains('\n')) {
                    // Если встречен символ новой строки, считаем сообщение полным
                    val fullMessage = stringBuilder.toString().substringBefore('\n')
                    stringBuilder.setLength(0) // Очищаем StringBuilder
                    callback.invoke(fullMessage)
                    Log.e("bt full message", fullMessage)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Обработка ошибок при чтении данных
        }
    }
}