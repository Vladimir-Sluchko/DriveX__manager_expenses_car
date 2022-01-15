package com.example.drivex.presentation.ui.setting

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.drivex.R
import com.example.drivex.presentation.ui.dialogs.SettingsDialog
import com.example.drivex.presentation.ui.dialogs.SettingsDialog.Companion.TYPE_CONSUMPTION
import com.example.drivex.presentation.ui.dialogs.SettingsDialog.Companion.TYPE_CURENCY
import com.example.drivex.presentation.ui.dialogs.SettingsDialog.Companion.TYPE_SOUND
import com.example.drivex.presentation.ui.dialogs.SettingsDialog.Companion.TYPE_VOLUME
import kotlinx.android.synthetic.main.fragment_setting.*
import java.util.*


class SettingFragment : Fragment() {
    companion object {
        const val APP_PREFERENCES = "com.drivex.app"
    }

    private lateinit var settingViewModel: SettingViewModel
    private lateinit var carVendor: Spinner
    private lateinit var carModel: EditText
    private lateinit var soundStartApp: SwitchCompat
    private lateinit var utilsVolume: LinearLayout
    private lateinit var utilsFuel: LinearLayout
    private lateinit var utilsCurrency: LinearLayout
    private lateinit var currency: TextView
    private lateinit var volume: TextView
    private lateinit var consumption: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        settingViewModel = ViewModelProvider(this).get(SettingViewModel::class.java)
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs: SharedPreferences? = context?.getSharedPreferences(
            APP_PREFERENCES, Context.MODE_PRIVATE
        )
        carVendor = view.findViewById(R.id.car_vendor)
        carModel = view.findViewById(R.id.Car_model)
        soundStartApp = view.findViewById(R.id.switch_sound)
        utilsVolume = view.findViewById(R.id.volume_picker_setting)
        utilsFuel = view.findViewById(R.id.fuel_picker_setting)
        utilsCurrency = view.findViewById(R.id.currency_picker_setting)
        currency = utilsCurrency.findViewById(R.id.curency)
        volume = utilsVolume.findViewById(R.id.volume)
        consumption = utilsFuel.findViewById(R.id.fuel_settings)
        if (prefs != null)
            applySettingsToSP(prefs)

        soundStartApp.setOnCheckedChangeListener { _, isChecked ->
               saveToSP(type = TYPE_SOUND, boolean = isChecked)
        }
        utilsVolume.setOnClickListener {
            createDialog(
                arrayOf(
                    getString(R.string.liter),
                    getString(R.string.Gallon)
                ), TYPE_VOLUME
            )
        }
        utilsFuel.setOnClickListener {
            createDialog(
                arrayOf(
                    getString(R.string.l_km),
                    getString(R.string.l_mile),
                    getString(R.string.g_km),
                    getString(R.string.g_mile)
                ), TYPE_CONSUMPTION
            )
        }
        utilsCurrency.setOnClickListener {
            createDialog(
                arrayOf(
                    getCurrency(Locale.getDefault()),
                    getCurrency(Locale.US),
                    getCurrency(Locale.GERMANY),
                    getCurrency(Locale.PRC),
                    getCurrency(Locale.UK)
                ), TYPE_CURENCY
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun applySettingsToSP(prefs: SharedPreferences) {
        when {
            prefs.contains(TYPE_VOLUME) -> volume.text =
                prefs.getString(TYPE_VOLUME, getString(R.string.volume))
            prefs.contains(TYPE_CONSUMPTION) -> consumption.text =
                prefs.getString(TYPE_CONSUMPTION, getString(R.string.l_km))
            prefs.contains(TYPE_CURENCY) -> currency.text =
                prefs.getString(TYPE_CURENCY, getCurrency(Locale.getDefault()))
            prefs.contains(TYPE_SOUND) -> switch_sound.isChecked = prefs.getBoolean(TYPE_SOUND,false)
        }
    }


    override fun onStart() {
        super.onStart()
        childFragmentManager.setFragmentResultListener("requestKey", this) { key, bundle ->
            bundle.getString(TYPE_VOLUME).let {
                it?.setText(volume, TYPE_VOLUME)
            }
            bundle.getString(TYPE_CURENCY).let {
                it?.setText(currency, TYPE_CURENCY)
            }
            bundle.getString(TYPE_CONSUMPTION).let {
                it?.setText(consumption, TYPE_CONSUMPTION)
            }
        }
    }

    private fun String.setText(textview: TextView, type: String) {
        if (this.isEmpty().not()) {
            textview.text = this
            saveToSP(this, type)
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun saveToSP(string: String? = null, type: String, boolean: Boolean = false) {
        val prefs: SharedPreferences? = context?.getSharedPreferences(
            APP_PREFERENCES, Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor? = prefs?.edit()
        if (string!=null)
            editor?.putString(type, string)
        else
            editor?.putBoolean(type, boolean)
        editor?.apply()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getCurrency(locale: Locale): String {
        val currency: Currency = Currency.getInstance(locale)
        return currency.displayName + " " + currency.symbol
    }

    private fun createDialog(array: Array<String>, type: String) {
        val myDialogFragment = SettingsDialog(array, type)
        val manager: FragmentManager = childFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        myDialogFragment.show(transaction, "dialog")
    }

}


