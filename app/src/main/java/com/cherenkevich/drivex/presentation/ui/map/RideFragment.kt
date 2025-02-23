package com.cherenkevich.drivex.presentation.ui.map

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cherenkevich.drivex.R
import com.cherenkevich.drivex.presentation.adapters.RideAdapter
import com.cherenkevich.drivex.utils.SortType
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.android.synthetic.main.fragment_ride.*
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import com.cherenkevich.drivex.presentation.ui.activity.MapsActivity
import com.cherenkevich.drivex.presentation.ui.fragments.AbstractFragment
import com.cherenkevich.drivex.presentation.ui.map.MapViewModel
import javax.inject.Inject


@AndroidEntryPoint
class RideFragment : AbstractFragment() {

    @Inject
    lateinit var prefs: SharedPreferences

    private lateinit var rideAdapter: RideAdapter
    private lateinit var viewModel: MapViewModel
    private lateinit var toolbarRide: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setFloatingMenuVisibility(false)
        return inflater.inflate(R.layout.fragment_ride, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rideAdapter = RideAdapter(prefs,context)
        viewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        toolbarRide = view.findViewById(R.id.rides_toolbar)
        toolbarRide.setBackgroundColor(resources.getColor(R.color.toolbar_background3))
        setupRecyclerView()
        setToolbar(toolbarRide,R.string.menu_ride)


        when (viewModel.sortType) {
            SortType.DATE -> spFilter.setSelection(0)
            SortType.RUNNING_TIME -> spFilter.setSelection(1)
            SortType.DISTANCE -> spFilter.setSelection(2)
        }
        viewModel.runs.observe(viewLifecycleOwner, Observer { runs ->
            if (runs.isEmpty()) {
                val intentMap = Intent(activity, MapsActivity::class.java)
                spFilter.isGone = true
                val builder = AlertDialog.Builder(context)
                builder
                    .setTitle(R.string.warning)
                    .setMessage(R.string.create_trip_nothing)
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(R.string.create_trip) { dialog, id ->
                        startActivity(intentMap)
                    }
                    .setNegativeButton(R.string.cancel_general) { dialog, _ ->
                        dialog.cancel()
                    }
                builder.create()
                builder.show()
            } else
                rideAdapter.submitList(runs)
        })

        spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}

            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                when (pos) {
                    0 -> viewModel.sortRuns(SortType.DATE)
                    1 -> viewModel.sortRuns(SortType.RUNNING_TIME)
                    2 -> viewModel.sortRuns(SortType.DISTANCE)
                }
            }
        }
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
            val run = rideAdapter.differ.currentList[position]
            viewModel.deleteRun(run)
            Snackbar.make(requireView(), "Поездка удалена", Snackbar.LENGTH_LONG).apply {
                setAction("Undo") {
                    viewModel.insertRun(run)
                }
                show()
            }
        }
    }

    private fun setupRecyclerView() = rvRuns.apply {
        adapter = rideAdapter
        layoutManager = LinearLayoutManager(activity)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
    }
}