package com.example.quadcare.Models

import androidx.lifecycle.ViewModel

class AddAppointmentViewModel: ViewModel() {
    var patientId:Int = -1;
    var appointmentDate: String = " ";
    var appointmentTime:String = " ";
    var reasonofAppointment: String = " ";
}
