package com.cherenkevich.drivex.data.model.api_models

import androidx.room.PrimaryKey
import java.io.Serializable

data class ParkingExpense(
    @PrimaryKey(autoGenerate = true)
    val baseExpenseModel: BaseExpenseModel,
    var uid: Int = 0,
    var longtitudeParkingPlace: Long = 0L,
    var latitudeParkingPlace: Long = 0L,
    var timeParkingPeriod: String = "",// период времени почему в стринге? може Long?
    var mapScreenPlaceURL: String = ""
) : Serializable
