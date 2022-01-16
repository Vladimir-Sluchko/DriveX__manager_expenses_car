package com.example.drivex.presentation.ui.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.drivex.R
import com.example.drivex.presentation.ui.dialogs.SettingsDialog.Companion.TYPE_SOUND
import com.example.drivex.presentation.ui.setting.SettingFragment
import com.example.drivex.utils.Constans.IS_PAYMENT
import com.example.drivex.utils.Constans.IS_REFUEL
import com.example.drivex.utils.Constans.IS_SHOPPING
import com.example.drivex.utils.Constans.PAYMENT_TYPE
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton
import com.nightonke.boommenu.BoomMenuButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var boomMenu: BoomMenuButton
    private lateinit var playerStart: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        boomMenu = findViewById(R.id.boom_menu)
        setBoomMenu()
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_car,
                R.id.nav_notify,
                R.id.nav_map,
                R.id.nav_stat,
                R.id.nav_settings,
                R.id.nav_info
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val prefs: SharedPreferences? = this.getSharedPreferences(
            SettingFragment.APP_PREFERENCES, Context.MODE_PRIVATE)
        if(prefs!=null&&prefs.contains(TYPE_SOUND)){
            startMusic(prefs.getBoolean(TYPE_SOUND,false))

        }
    }

    private fun startMusic(isNeedSound: Boolean) {
        if (isNeedSound) {
            playerStart = MediaPlayer.create(this, R.raw.engine_start)
            playerStart.start()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setBoomMenu() {
        val intentFuell = Intent(this, FuelActivity::class.java)
        val intentService = Intent(this, ServiceActivity::class.java)
        val intentMap = Intent(this, MapsActivity::class.java)
        val buttonFuel = TextOutsideCircleButton.Builder()
            .normalImageRes(R.drawable.ic_car)
            .normalText("Заправка")
            .listener { startActivity(intentFuell.putExtra(PAYMENT_TYPE, IS_REFUEL)) }
        val buttonService = TextOutsideCircleButton.Builder()
            .normalImageRes(R.drawable.ic_service)
            .normalText("Добавить ТО")
            .listener { startActivity(intentService) }
        val buttonPayments = TextOutsideCircleButton.Builder()
            .normalImageRes(R.drawable.ic_money)
            .normalText("Платеж")
            .listener { startActivity(intentFuell.putExtra(PAYMENT_TYPE, IS_PAYMENT)) }
        val buttonExpenses = TextOutsideCircleButton.Builder()
            .normalImageRes(R.drawable.ic_shopping)
            .normalText("Покупка")
            .listener { startActivity(intentFuell.putExtra(PAYMENT_TYPE, IS_SHOPPING)) }
        val buttonDriving = TextOutsideCircleButton.Builder()
            .normalImageRes(R.drawable.ic_map)
            .normalText("Поездка")
            .listener { startActivity(intentMap) }
        boomMenu.addBuilder(buttonFuel)
        boomMenu.addBuilder(buttonService)
        boomMenu.addBuilder(buttonPayments)
        boomMenu.addBuilder(buttonExpenses)
        boomMenu.addBuilder(buttonDriving)
    }













}