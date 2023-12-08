package com.example.quadcare.Activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.ProgressBar
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quadcare.Adapters.AppointmentsRecyclerAdapter
import com.example.quadcare.Adapters.DoctorsRecyclerAdapter
import com.example.quadcare.Models.DBStorage
import com.example.quadcare.NetworkCalls.*
import com.example.quadcare.R
import com.example.quadcare.constants.APP_TABLE
import com.example.quadcare.constants.COL_ADESCRIPTION
import com.example.quadcare.constants.COL_ANAME
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

class ViewAppointmentsActivity : AppCompatActivity() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<AppointmentsRecyclerAdapter.ViewHolder>? = null
    private lateinit var recyclerview_activity: RecyclerView
    private lateinit var progressBar: ProgressBar;
    private var appointmentListdata: ArrayList<AppointmentItem> = ArrayList<AppointmentItem>();
    private var displayAppointmentList = ArrayList<AppointmentItem>();
    private lateinit var toolBar: Toolbar;
    private lateinit var sharedPreferences: SharedPreferences;
    private var switchStatus: Boolean = false;
    private lateinit var switch:Switch;
    var db = DBStorage(this)
    private var VIEWAPPOINTMENTACTIVITY = "View Appointment Activity";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_appointments)
        setToolBar()
        setTheme()
        getAppointments()
    }

    private fun getAppointments() {
        var retrofit = ApiUtilities.getInstance().create(ApiInterface::class.java)
        progressBar = findViewById(R.id.progress_bar)
        var apiCall = retrofit.getAppointments();

        apiCall.enqueue(object : Callback<List<AppointmentItem>> {
            override fun onResponse(
                call: Call<List<AppointmentItem>>,
                response: Response<List<AppointmentItem>>
            ) {
                var responseBody = response.body();
                progressBar.visibility = View.GONE;
                if (responseBody != null) {
                    for ( data in responseBody) {
                        var appointmentItem = AppointmentItem(data.date,data.doctorId,data.id,data.isAssigned
                            ,data.patientId,data.time,data.patientName,data.doctorName);
                        appointmentListdata.add(appointmentItem);
                        displayAppointmentList.add(appointmentItem);
                    }
                    setRecycleView();
                }
            }

            override fun onFailure(call: Call<List<AppointmentItem>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setRecycleView(){
        layoutManager = LinearLayoutManager(this)
        recyclerview_activity = this.findViewById(R.id.appointment_recyclerview_activity)
        recyclerview_activity.layoutManager = layoutManager
        adapter = AppointmentsRecyclerAdapter(appointmentListdata,object: AppointmentsRecyclerAdapter.appointmentAdapterListner{
            override fun onClickListner(appointment: AppointmentItem) {
                openAssignDoctorActivity(appointment)
            }

        });
        recyclerview_activity.adapter = adapter
    }

    private fun openAssignDoctorActivity(appointment:AppointmentItem) {
        var intent = Intent(this,AssignDoctorActivity::class.java)
        var bundle = Bundle()
        bundle.putString("appointmentTime",appointment.time)
        bundle.putInt("AppointmentId",appointment.id)
        bundle.putString("AppointmentDate",appointment.date);
        intent.putExtra("AssignAppointment",bundle);
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.addIcon -> {
                var intent = Intent(this,AddAppointmentActivity::class.java)
                var hashMap_3 = HashMap<String, Any>();
                hashMap_3.put(COL_ANAME,VIEWAPPOINTMENTACTIVITY)
                hashMap_3.put(COL_ADESCRIPTION,"Add Appointment Clicked !!")
                db.insertData(APP_TABLE,hashMap_3)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setTheme() {
        sharedPreferences = getSharedPreferences("night",0)
        var bool: Boolean = sharedPreferences.getBoolean("night_mode",true);
        println("checkValue $bool")
        if(bool){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            switchStatus = true;
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.viewpage_menu,menu);
        var menuItem = menu!!.findItem(R.id.memu_search_icon)
        setToggle(menu!!)
        if(menuItem != null) {
            val searchView = menuItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()){
                        displayAppointmentList.clear()
                        val search = newText.lowercase(Locale.getDefault())
                        appointmentListdata.forEach{
                            if(it.id.toString().lowercase(Locale.getDefault()).contains(search)){
                                displayAppointmentList.add(it)
                                println("Added")
                            }
                        }
                        recyclerview_activity.adapter!!.notifyDataSetChanged()
                    }else {
                        displayAppointmentList.clear()
                        displayAppointmentList.addAll(appointmentListdata)
                        recyclerview_activity.adapter!!.notifyDataSetChanged()
                    }
                    return true
                }
            })
        }
        return true
    }

    private fun setToggle(menu: Menu) {
        switch = findViewById(R.id.action_switch)
        switch.isChecked = switchStatus;
        switch.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if(p1){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    p0?.isChecked = true;
                    p0?.refreshDrawableState()
                    var editor: SharedPreferences.Editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode",true);
                    editor.commit()
                }else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    p0?.isChecked = false;
                    var editor: SharedPreferences.Editor = sharedPreferences.edit();
                    editor.putBoolean("night_mode",false);
                    p0?.refreshDrawableState()
                    editor.commit()
                }
            }
        });
    }


    private fun setToolBar() {
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        supportActionBar?.setDisplayShowTitleEnabled(false);
    }

    override fun onRestart() {
        super.onRestart()
        appointmentListdata.clear()
        displayAppointmentList.clear()
        recyclerview_activity.adapter!!.notifyDataSetChanged()
        progressBar.visibility = View.VISIBLE
        getAppointments();
    }

}
