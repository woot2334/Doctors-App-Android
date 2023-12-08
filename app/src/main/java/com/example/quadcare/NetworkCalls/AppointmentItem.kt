package com.example.quadcare.NetworkCalls

data class AppointmentItem(
    val date: String,
    val doctorId: Int,
    val id: Int,
    val isAssigned: Boolean,
    val patientId: Int,
    val time: String,
    val patientName: String,
    val doctorName: String
)