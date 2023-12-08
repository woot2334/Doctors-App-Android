package com.example.quadcare.Adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.quadcare.NetworkCalls.PatientItem
import com.example.quadcare.R
import org.w3c.dom.Text

class PatientsRecyclerAdapter(var listData: ArrayList<PatientItem>,var mListner: patientAdapterListner): RecyclerView.Adapter<PatientsRecyclerAdapter.ViewHolder>() {

    public interface patientAdapterListner {
        fun onClickListner(patientId:Long, test: String);
        fun onClickListner(test: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.patient_card_view, parent, false)
        return ViewHolder(v,mListner)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.patientName.text = listData[position].name
        holder.patientId.text = "Patient #${listData[position].id}"
        holder.patientAddress.text = listData[position].address
        holder.patientAge.text = "Age: ${listData[position].age}"
        holder.patientImg.setImageResource(R.drawable.patient_icon)
    }

    inner class ViewHolder(itemView: View, mListner: patientAdapterListner): RecyclerView.ViewHolder(itemView){
        var patientImg: ImageView
        var patientName: TextView
        var patientId: TextView
        var patientAge: TextView
        var patientAddress: TextView
        var phoneIcon: ImageView
        var mailIcon: ImageView

        init {
            patientAge = itemView.findViewById(R.id.patient_age)
            patientImg = itemView.findViewById(R.id.patient_image)
            patientName = itemView.findViewById(R.id.patient_name)
            patientId = itemView.findViewById(R.id.patient_id)
            patientAddress = itemView.findViewById((R.id.patient_address))
            phoneIcon = itemView.findViewById(R.id.phone_icon)
            mailIcon = itemView.findViewById(R.id.mail_icon)

            var context = itemView.getContext()

            patientImg.setOnClickListener {
                var test = "image"
                mListner.onClickListner(listData[absoluteAdapterPosition].id.toLong(), test)
            }

            phoneIcon.setOnClickListener{
                var test = "phone"
//                val it = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + listData[absoluteAdapterPosition].id))
//                startActivity(it)
                mListner.onClickListner(listData[absoluteAdapterPosition].phone.toLong(), test)
            }

            mailIcon.setOnClickListener{
                var test = "mail"
                mListner.onClickListner(listData[absoluteAdapterPosition].email)
            }
        }
    }
}
