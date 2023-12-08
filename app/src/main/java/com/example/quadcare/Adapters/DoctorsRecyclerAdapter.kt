package com.example.quadcare.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quadcare.NetworkCalls.DoctorItem
import com.example.quadcare.R

class DoctorsRecyclerAdapter(var listData: ArrayList<DoctorItem>, var mListner:doctorAdapterListner): RecyclerView.Adapter<DoctorsRecyclerAdapter.ViewHolder>()  {

    public interface doctorAdapterListner {
        fun onClickListner(phoneNumber:Long)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.doctor_card_view, parent, false)
        return ViewHolder(v,mListner)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.doctorName.text = listData[position].name
        holder.doctorSp.text = listData[position].specialization
        holder.doctorShift.text = "${listData[position].shiftStartTime} to ${listData[position].shiftEndTime}";
        holder.doctorImg.setImageResource(R.drawable.doctor_icon)
    }

    inner class ViewHolder(itemView: View, mListner: doctorAdapterListner): RecyclerView.ViewHolder(itemView){
        var doctorImg: ImageView
        var doctorName: TextView
        var doctorSp: TextView
        var doctorShift: TextView
        var phoneIcon: ImageView

        init {
            doctorImg = itemView.findViewById(R.id.doctor_image)
            doctorName = itemView.findViewById(R.id.doctor_name)
            doctorSp = itemView.findViewById(R.id.doctor_specialization)
            doctorShift = itemView.findViewById((R.id.doctor_shift_timing))
            phoneIcon = itemView.findViewById(R.id.d_phone_icon)

            phoneIcon.setOnClickListener{
                var test = "mail"
                mListner.onClickListner(listData[absoluteAdapterPosition].phone.toLong())
            }
        }
    }
}
