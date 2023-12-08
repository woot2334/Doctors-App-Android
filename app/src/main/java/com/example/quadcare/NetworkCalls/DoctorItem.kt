package com.example.quadcare.NetworkCalls

data class DoctorItem (
    val id: Int,
    val name: String,
    val specializationId: Int,
    val specialization: String,
    val phone: String,
    val email: String,
    val shiftStartTime: String,
    val shiftEndTime: String
)
