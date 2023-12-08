package com.example.quadcare.NetworkCalls

import com.example.quadcare.Models.Specialization
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {

    @GET("api/patient/GetAllPatients")
    fun getPatients(): Call<List<PatientItem>>

    @POST("api/patient/AddPatient")
    fun savePatient(@Body patientItem: PatientItem): Call<Boolean>

    @GET("api/appointment/GetAllAppointments")
    fun getAppointments(): Call<List<AppointmentItem>>

    @POST("api/appointment/BookAppointments")
    fun bookAppointment(@Body appointmentItem: AppointmentItem): Call<Boolean>
    
    @GET("api/doctor/GetSpecializations")
    fun getSpecializations(): Call<List<Specialization>>

    @GET("api/doctor/GetDoctors")
    fun getDoctors(): Call<List<DoctorItem>>

    @POST("api/doctor/AddDoctor")
    fun addDoctor(@Body doctorItem: DoctorItem): Call<Boolean>

    @POST("api/appointment/AssignAppointment")
    fun assignAppointment(@Body assignDoctor: AssignDoctor): Call<Boolean>
}