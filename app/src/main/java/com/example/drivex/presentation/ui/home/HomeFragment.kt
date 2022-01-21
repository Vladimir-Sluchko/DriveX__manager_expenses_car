package com.example.drivex.presentation.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drivex.R
import com.example.drivex.presentation.adapters.MainAdapter
import com.example.drivex.presentation.ui.activity.FuelActivity
import com.example.drivex.presentation.ui.activity.viewModels.AbstractViewModel
import com.example.drivex.presentation.ui.dialogs.SettingsDialog.Companion.TYPE_CAR
import com.example.drivex.presentation.ui.dialogs.SettingsDialog.Companion.TYPE_CONSUMPTION
import com.example.drivex.presentation.ui.dialogs.SettingsDialog.Companion.TYPE_CURENCY
import com.example.drivex.presentation.ui.dialogs.SettingsDialog.Companion.TYPE_DISTANCE
import com.example.drivex.presentation.ui.dialogs.SettingsDialog.Companion.TYPE_VOLUME
import com.example.drivex.presentation.ui.setting.SettingFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: AbstractViewModel
    lateinit var adapter: MainAdapter
    lateinit var allExpenses: TextView
    lateinit var allMileage: TextView
    lateinit var allVolume: TextView
    lateinit var allCostFuel: TextView
    lateinit var allCostService: TextView
    lateinit var carModel: TextView
    lateinit var liveDataCost: LiveData<Double>
    lateinit var liveDataMileage: LiveData<String>
    lateinit var liveDataCostFUel: LiveData<Int>
    lateinit var liveDataVolumeFUel: LiveData<Int>
    lateinit var liveDataCostService: LiveData<Int>
    lateinit var liveDatarefuelSum: LiveData<Int>
    private var currencySP: String = ""
    private var consumptionSP: String = ""
    private var volumeSP: String = ""
    private var distanceSP: String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater
            .inflate(R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(this).get(AbstractViewModel::class.java)
        allExpenses = view.findViewById(R.id.all_expencess_car)
        allMileage = view.findViewById(R.id.text_mileage_info)
        allVolume = view.findViewById(R.id.volume_summ)
        allCostFuel = view.findViewById(R.id.fuel_expenses)
        allCostService = view.findViewById(R.id.service_summ)
        recyclerView = view.findViewById(R.id.recycler_view_home)
        carModel = view.findViewById(R.id.text_model_info)
        liveDataCost = viewModel.allExpensesSum
        liveDataMileage = viewModel.lastMileageStr
        liveDataCostFUel = viewModel.allFuelCostSum
        liveDataVolumeFUel = viewModel.allFuelVolumeSum
        liveDataCostService = viewModel.allServiceCostSum
        liveDatarefuelSum = viewModel.refuelSum

        viewModel.expenses.observe(viewLifecycleOwner, { expenses ->
            adapter.submitList(expenses)
        })
        setinfoView()
        getSharedPref()
        return view
    }

    private fun getSharedPref() {
        val prefs: SharedPreferences? = activity?.getSharedPreferences(
            SettingFragment.APP_PREFERENCES, Context.MODE_PRIVATE
        )
        if (prefs != null) {
            currencySP = prefs.getString(TYPE_CURENCY, "$") ?: "$"
            consumptionSP = prefs.getString(TYPE_CONSUMPTION, "L/100km") ?: "L/100km"
            volumeSP = prefs.getString(TYPE_VOLUME, "L") ?: "L"
            distanceSP = prefs.getString(TYPE_DISTANCE, "Km") ?: "Km"
            carModel.text = (prefs.getString(TYPE_CAR, "")) ?: "Your Car"
        }
        setRecyclerview(currencySP)
    }

    @SuppressLint("SetTextI18n")
    private fun setinfoView() {
        liveDataCost.observe(viewLifecycleOwner, {
            chekVisibilyty(it, allExpenses)
            allExpenses.text = getString(R.string.total_expenses) + ": $it $currencySP"
        })
        liveDataMileage.observe(viewLifecycleOwner, {
            chekVisibilyty(it, allMileage)
            allMileage.text = getString(R.string.mileage) + ": $it $distanceSP"
        })
        liveDatarefuelSum.observe(viewLifecycleOwner, {
            chekVisibilyty(it, allCostFuel)
            allCostFuel.text = getString(R.string.refuel_expenses) + ": $it $currencySP"
        })
        liveDataVolumeFUel.observe(viewLifecycleOwner, {
            chekVisibilyty(it, allVolume)
            allVolume.text = getString(R.string.total_fuel_volume) + ": $it $volumeSP"
        })
        liveDataCostService.observe(viewLifecycleOwner, {
            chekVisibilyty(it, allCostService)
            allCostService.text =
                getString(R.string.total_expenses_for_service) + ": $it $currencySP"
        })
    }

    private fun chekVisibilyty(value: Any?, expenses: TextView) {
        if (value == null)
            expenses.isGone = true
    }

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val expenses = adapter.listExp.currentList[position]
            viewModel.delete(expenses)
            Snackbar.make(requireView(), getText(R.string.entry_deleted), Snackbar.LENGTH_LONG)
                .apply {
                    setAction(getText(R.string.cancel_general)) {
                        viewModel.insert(expenses)
                    }
                    show()
                }
        }
    }

    private fun setRecyclerview(currencySP: String) {
        adapter = MainAdapter(context, currency = currencySP) { id ->
            startActivity(Intent(context, FuelActivity::class.java).putExtra("id", id))
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }
}