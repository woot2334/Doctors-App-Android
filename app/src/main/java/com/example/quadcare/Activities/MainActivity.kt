package com.example.quadcare.Activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.quadcare.Broadcasts.InternetChangeReceiver
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.example.quadcare.Fragments.HomeCardFragment
import com.example.quadcare.Models.DBStorage
import com.example.quadcare.R
import com.example.quadcare.constants.*

val MAINACTIVITY = "MainActivity"

class MainActivity : AppCompatActivity() {

    var db = DBStorage(this)
    private lateinit var cld : InternetChangeReceiver
    private lateinit var Button1: Button
    var simpleVideoView: VideoView? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.setvideoplayback()
        this.setCallbacks()
        this.checkNetworkConnection()
    }

    private fun setvideoplayback() {
        simpleVideoView = findViewById<View>(R.id.simpleVideoView) as VideoView

        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            simpleVideoView!!.setVideoURI(Uri.parse("android.resource://"
                    + packageName + "/" + R.raw.quadcareclipfinal))
        } else {
            simpleVideoView!!.setVideoURI(Uri.parse("android.resource://"
                    + packageName + "/" + R.raw.quadcareclipland))
        }
        simpleVideoView!!.requestFocus()
        simpleVideoView!!.start()

        simpleVideoView!!.setOnCompletionListener{
            simpleVideoView!!.start()
        }

        simpleVideoView!!.setOnErrorListener { mp, what, extra ->
            Toast.makeText(applicationContext, "An Error Occurred " +
                    "While Playing Video !!!", Toast.LENGTH_LONG).show()
            false
        }
    }

    private fun setCallbacks(){
        var viewPatients = HomeCardFragment()
        var bundle = Bundle()
        bundle.putInt("cardText",R.string.viewPatients)
        bundle.putInt("icon",R.drawable.patient_icon)
        viewPatients.arguments = bundle
        viewPatients.homecardListener = object: HomeCardFragment.HomeFragmentListerner {
            override fun setOnClickListerner(context: Context) {
                Log.d(MAINACTIVITY,"View Patients is Clicked !!")
                var intent = Intent(context, ViewPatientsActivity::class.java)

                var hashMap_1 = HashMap<String, Any>();

                hashMap_1.put(COL_ANAME,MAINACTIVITY)
                hashMap_1.put(COL_ADESCRIPTION,"View Patients is Clicked !!")
                db.insertData(APP_TABLE,hashMap_1)
                startActivity(intent)
            }
        }
        supportFragmentManager.beginTransaction().add(R.id.viewPatients,viewPatients).commit()

        var viewDoctos = HomeCardFragment()
        bundle = Bundle()
        bundle.putInt("cardText",R.string.viewDoctor)
        bundle.putInt("icon",R.drawable.doctor_icon)
        viewDoctos.arguments = bundle
        viewDoctos.homecardListener = object: HomeCardFragment.HomeFragmentListerner {
            override fun setOnClickListerner(context: Context) {

                Log.d(MAINACTIVITY,"View Doctors is Clicked !!")
                var intent = Intent(context, ViewDoctorsActivity::class.java)

                var hashMap = HashMap<String, Any>();

                hashMap.put(COL_ANAME,MAINACTIVITY)
                hashMap.put(COL_ADESCRIPTION,"View Doctors is Clicked !!")
                db.insertData(APP_TABLE,hashMap)
                startActivity(intent)
            }
        }
        supportFragmentManager.beginTransaction().add(R.id.viewDoctors,viewDoctos).commit()

        var viewAppointments = HomeCardFragment()
        bundle = Bundle()
        bundle.putInt("cardText",R.string.viewAppointments)
        bundle.putInt("icon",R.drawable.appointment_icon)
        viewAppointments.arguments = bundle
        viewAppointments.homecardListener = object: HomeCardFragment.HomeFragmentListerner {
            override fun setOnClickListerner(context: Context) {
                Log.d(MAINACTIVITY,"View Appointments is Clicked !!")

                var hashMap_2 = HashMap<String, Any>();

                hashMap_2.put(COL_ANAME,MAINACTIVITY)
                hashMap_2.put(COL_ADESCRIPTION,"View Appointments is Clicked !!")
                println("PowerTesting")
                db.insertData(APP_TABLE,hashMap_2)
                var intent = Intent(context, ViewAppointmentsActivity::class.java)
                startActivity(intent)
            }
        }
        supportFragmentManager.beginTransaction().add(R.id.viewAppointments,viewAppointments).commit()

        var viewActivity = HomeCardFragment()
        bundle = Bundle()
        bundle.putInt("cardText",R.string.ViewActivity)
        bundle.putInt("icon",R.drawable.activities_icon)
        viewActivity.arguments = bundle
        viewActivity.homecardListener = object: HomeCardFragment.HomeFragmentListerner {
            override fun setOnClickListerner(context: Context) {
                Log.d(MAINACTIVITY,"View Activities is Clicked !!")
                var intent = Intent(context, DashBoardActivity::class.java)

                var hashMap_3 = HashMap<String, Any>();
                hashMap_3.put(COL_ANAME,MAINACTIVITY)
                hashMap_3.put(COL_ADESCRIPTION,"View Activities Clicked !!")
                db.insertData(APP_TABLE,hashMap_3)
                startActivity(intent)
            }
        }
        supportFragmentManager.beginTransaction().add(R.id.viewActiviy,viewActivity).commit()
    }

    override fun onResume() {
        super.onResume();
        setvideoplayback();
    }

    private fun checkNetworkConnection(){
        cld = InternetChangeReceiver(application)
        cld.observe(this, { isConnected ->
            if(isConnected){
                //Toast.makeText(this, "Internet Connected", Toast.LENGTH_LONG).show()

            }else{
                Toast.makeText(this, "Internet NOT Connected", Toast.LENGTH_LONG).show()
            }

        })

    }

}
