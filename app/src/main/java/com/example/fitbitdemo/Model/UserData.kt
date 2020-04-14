package com.example.fitbitdemo.Model

import com.google.gson.annotations.SerializedName

/**
 *
 *
 * @author Aditi Shirsat
 */

data class UserData(
    @SerializedName("activities") val activities: ArrayList<String>? = arrayListOf(),
    @SerializedName("summary") val summary: Summary,
    @SerializedName("fairlyActiveMinutes") val fairlyActiveMinutes: String? = "",
    @SerializedName("lightlyActiveMinutes") val lightlyActiveMinutes: String? = "",
    @SerializedName("marginalCalories") val marginalCalories: String? = "",
    @SerializedName("sedentaryMinutes") val sedentaryMinutes: String? = "",
    @SerializedName("steps") val steps: String? = "",
    @SerializedName("veryActiveMinutes") val veryActiveMinutes: String? = ""
)

data class Summary(
    @SerializedName("activeScore") val activeScore: String? = "",
    @SerializedName("activityCalories") val activityCalories: String? = "",
    @SerializedName("caloriesBMR") val caloriesBMR: String? = "",
    @SerializedName("caloriesOut") val caloriesOut: String? = "",
    @SerializedName("distances") val distances: ArrayList<Distance>? = arrayListOf()
)

data class Distance(
    @SerializedName("activity") val activity: String? = "",
    @SerializedName("distance") val distance: String? = ""
)