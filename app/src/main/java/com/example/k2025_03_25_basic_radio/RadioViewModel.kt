package com.example.k2025_03_25_basic_radio


import android.app.Application
import androidx.lifecycle.AndroidViewModel

class RadioViewModel(application: Application) : AndroidViewModel(application) {

    var radioService: RadioService? = null

    fun playRadio(url: String) {
        radioService?.playRadio(url)
    }

    fun stopRadio() {
        radioService?.stopRadio()
    }
}
