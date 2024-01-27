package com.jitsib

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    private val BTList = mutableListOf<BluetoothDevice>()
    private val messageList = mutableListOf<DC_message_item>()
    private lateinit var adapter: recyclerViewBt
    private lateinit var socket: BluetoothSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            btOn()
        }

        val editText = findViewById<EditText>(R.id.message)
        val buttonSend = findViewById<Button>(R.id.buttonSend)
        buttonSend.setOnClickListener{
            val message = editText.text
            Log.e("message test", message.toString())
            // Проверяем, инициализирован ли socket
            if (::socket.isInitialized && socket.isConnected) {

                for(i in 0 until 3){
                    writeBtDevice(socket, message.toString())
                }
                editText.text.clear()
                editText.clearFocus()
            } else {
                // Обработка случая, когда socket не инициализирован или не подключен
                Log.e("Socket Error", "Socket is not initialized or not connected")
                Toast.makeText(applicationContext, "Проверьте подключение по bluetooth", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun btOn() {

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            Toast.makeText(applicationContext, "Устройство не поддерживает Bluetooth", Toast.LENGTH_SHORT).show()
        } else {
            // Проверка и запрос разрешения BLUETOOTH_CONNECT во время выполнения
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
                // Разрешение не предоставлено, запросите его
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 123)
            } else {
                // Разрешение предоставлено, выполните нужные действия с BluetoothAdapter
                if (!bluetoothAdapter.isEnabled) {
                    // Включите Bluetooth
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBtIntent, 123)
                } else {
                    // Bluetooth уже включен
                    Toast.makeText(applicationContext, "Bluetooth уже включен", Toast.LENGTH_SHORT).show()

                    // Получение списка сопряженных устройств
                    BTList.clear()
                    BTList.addAll(bluetoothAdapter.bondedDevices)
                    for (device in BTList) {
                        val deviceName = device.name // Имя устройства
                        val deviceAddress = device.address // MAC-адрес устройства

                        Log.e("bt test", "Name: $deviceName, Address: $deviceAddress")
                    }
                    alertDialog()
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    fun alertDialog(){

        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view: View = inflater.inflate(R.layout.alertdialog_btdevice, null)

        builder.setView(view)
        val alertDialog: AlertDialog = builder.create()

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = recyclerViewBt(BTList, applicationContext)

        adapter.setOnItemClickListener { bluetoothDevice ->
            //Toast.makeText(applicationContext, "Clicked on device: ${bluetoothDevice.alias}", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
            btConnect(bluetoothDevice)
        }
        recyclerView.adapter = adapter

        alertDialog.show()
    }

    fun btConnect(btDevice: BluetoothDevice) {
        val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // UUID для сервиса Serial Port Profile (SPP)

        try {
            // Попытка подключения
            socket = btDevice.createInsecureRfcommSocketToServiceRecord(uuid)
            socket.connect()

            // Ваш код для работы с подключением Bluetooth, например, создание потоков для чтения и записи данных

            // Проверка успешного подключения
            if (socket.isConnected) {
                Toast.makeText(applicationContext, "Успешное подключение к устройству", Toast.LENGTH_SHORT).show()
                Log.e("bt connect", "Успешное подключение к устройству")

                val readThread = ReadThread(socket) { receivedData ->
                    // Обработка полученных данных в основном потоке
                    Log.e("MainActivity", "Received data in main thread: $receivedData")
                }
                readThread.start()

            } else {
                Toast.makeText(applicationContext, "Не удалось подключиться к устройству", Toast.LENGTH_SHORT).show()
                Log.e("bt connect", "Не удалось подключиться к устройству")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Ошибка подключения к устройству", Toast.LENGTH_SHORT).show()
            Log.e("bt connect", "Ошибка подключения к устройству")
        }
    }

    fun writeBtDevice(socket: BluetoothSocket, message: String) {
        try {
            val outputStream: OutputStream = socket.outputStream

            val messageBytes = (message + "\n").toByteArray()
            outputStream.write(messageBytes)

        } catch (e: IOException) {
            e.printStackTrace()
            // Обработка ошибок при записи данных
        }
    }

}