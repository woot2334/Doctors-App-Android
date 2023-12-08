package com.example.quadcare.Models

import androidx.lifecycle.ViewModel

class AddDoctorViewModel : ViewModel(){
    var name: String = "";
    var specialzation: String = " ";
    var phoneNumber: String = " ";
    var shiftStartTime: String = " ";
    var shiftEndTime: String = " ";
    var specializationId : Int = 0;
}
