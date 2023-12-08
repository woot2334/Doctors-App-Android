package com.example.quadcare.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quadcare.NetworkCalls.AppointmentItem
import com.example.quadcare.R

class AppointmentsRecyclerAdapter(var listData: ArrayList<AppointmentItem>,var mListner : AppointmentsRecyclerAdapter.appointmentAdapterListner): RecyclerView.Adapter<AppointmentsRecyclerAdapter.ViewHolder>() {

    public interface appointmentAdapterListner {
        fun onClickListner(appointment: AppointmentItem);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.appointment_card_view, parent, false)
        return ViewHolder(v,mListner)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.appointmentNum.text = "Appointment #${listData[position].id}";
        holder.patientid.text = listData[position].patientName;
        holder.patientTiming.text = "${listData[position].date} ${listData[position].time}"
        holder.assignButton.visibility = if (listData[position].isAssigned) View.GONE else View.VISIBLE;
        holder.doctorName.visibility = if (!listData[position].isAssigned) View.GONE else View.VISIBLE;
        holder.doctorName.text = listData[position].doctorName;
        holder.assignButton.bottom = R.id.assign
    }

    inner class ViewHolder(itemView: View,mListner: AppointmentsRecyclerAdapter.appointmentAdapterListner): RecyclerView.ViewHolder(itemView){
        var appointmentNum: TextView
        var patientid: TextView
        var patientTiming: TextView
        var assignButton: Button
        var doctorName: TextView

        init {
            appointmentNum = itemView.findViewById(R.id.appointment_number)
            patientid = itemView.findViewById(R.id.appointment_patient_id)
            patientTiming = itemView.findViewById(R.id.appointment_timing)
            assignButton = itemView.findViewById((R.id.assign))
            doctorName = itemView.findViewById(R.id.appointment_Doctor)

            assignButton.setOnClickListener{
                println("Assign Button Clicked $absoluteAdapterPosition")
                mListner.onClickListner(listData[absoluteAdapterPosition])
            }
        }
    }
}
