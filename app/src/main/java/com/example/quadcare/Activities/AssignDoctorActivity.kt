package com.example.quadcare.Activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.RadioGroup.OnCheckedChangeListener
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.example.quadcare.Fragments.ButtonFragment
import com.example.quadcare.Models.AssignDoctorViewModel
import com.example.quadcare.Models.DBStorage
import com.example.quadcare.NetworkCalls.ApiInterface
import com.example.quadcare.NetworkCalls.ApiUtilities
import com.example.quadcare.NetworkCalls.AssignDoctor
import com.example.quadcare.NetworkCalls.DoctorItem
import com.example.quadcare.R
import com.example.quadcare.constants.APP_TABLE
import com.example.quadcare.constants.COL_ADESCRIPTION
import com.example.quadcare.constants.COL_ANAME
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class AssignDoctorActivity : AppCompatActivity() {

    private var appointmentId: Int = 0;
    private lateinit var assignAppointmentTime: TextView;
    private lateinit var assignAppointmentDate: TextView;
    private lateinit var assignAppointmentTitle: TextView;
    private lateinit var radioGroup: RadioGroup;
    private lateinit var assignButton: Button;
    private lateinit var assignProgress: ProgressBar;
    private lateinit var doctors: List<DoctorItem>;
    private lateinit var toolBar: Toolbar;
    private lateinit var sharedPreferences: SharedPreferences;
    private var switchStatus: Boolean = false;
    private lateinit var switch:Switch;
    private var db = DBStorage(this);
    private var ASSIGNDOCTORACTIVITY = "AssignDoctor Activity";

    val assignDoctorViewModel: AssignDoctorViewModel by lazy {
        ViewModelProviders.of(this)[AssignDoctorViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_doctor)
        var bundle = intent.getBundleExtra("AssignAppointment") as Bundle;
        setConnections(bundle);
        setTheme();
        setToolBar();
        getDoctorList();
    }

    private fun setConnections(bundle: Bundle) {
        assignAppointmentTime = findViewById(R.id.assign_appointmentTime);
        assignAppointmentTime.text = bundle.getString("appointmentTime");
        assignAppointmentDate = findViewById(R.id.assign_appointment_date);
        assignAppointmentDate.text = bundle.getString("AppointmentDate");
        assignAppointmentTitle = findViewById(R.id.assign_appointment_title);
        appointmentId = bundle.getInt("AppointmentId")
        assignAppointmentTitle.text = "Appointment $appointmentId";
        radioGroup = findViewById(R.id.assign_radio_grp);
        assignProgress = findViewById(R.id.assign_progress_bar);
//        assignButton = findViewById(R.id.assign_appointment_btn);

        var buttonFrag = ButtonFragment();
        var bundle = Bundle();
        bundle.putString("buttonTextValue", getString(R.string.assign))
        buttonFrag.arguments = bundle;
        buttonFrag.mButtonClickListner = object: ButtonFragment.buttonClickListner{
            override fun onClick() {
                var hashMap_3 = HashMap<String, Any>();
                hashMap_3.put(COL_ANAME,ASSIGNDOCTORACTIVITY)
                hashMap_3.put(COL_ADESCRIPTION,"Add Patients Clicked !!")
                db.insertData(APP_TABLE,hashMap_3)
                assignAppointment();
            }
        }
        supportFragmentManager.beginTransaction().add(R.id.assign_appointment_btn,buttonFrag).commit()
    }

    private fun getDoctorList() {
        var retrofit = ApiUtilities.getInstance().create(ApiInterface::class.java);
        retrofit.getDoctors().enqueue(object : Callback<List<DoctorItem>>{
            override fun onResponse(
                call: retrofit2.Call<List<DoctorItem>>,
                response: Response<List<DoctorItem>>
            ) {
                assignProgress.visibility = View.GONE;
                if(response.isSuccessful){
                    bindRadioGrp(response.body()!!)
                }
            }

            override fun onFailure(call: retrofit2.Call<List<DoctorItem>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun bindRadioGrp(doctors: List<DoctorItem>) {
        this.doctors = doctors;
        var index: Int = 0;
        radioGroup.setOnCheckedChangeListener(object: OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                println("$p1 checked")
                assignDoctorViewModel.doctorId = doctors[p1].id
            }
        })
        for (doctor in doctors){
            var radioBtn = RadioButton(this);
            radioBtn.text = doctor.name;
            radioBtn.id = index;
            radioGroup.addView(radioBtn);
            index += 1;
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
        setToggle(menu!!)
        return true
    }

     private fun setToggle(menu: Menu) {
        switch = findViewById(R.id.action_switch)
         removeToolItems(menu)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.addIcon -> {
                var intent = Intent(this,AddPatientActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun removeToolItems(menu: Menu){
        menu.removeItem(R.id.memu_search_icon);
        menu.removeItem(R.id.addIcon);
    }

    private fun assignAppointment() {
        var retrofit = ApiUtilities.getInstance().create(ApiInterface::class.java);
        if(assignDoctorViewModel.doctorId != 0){
            retrofit.assignAppointment(AssignDoctor(appointmentId,assignDoctorViewModel.doctorId))
                .enqueue(object : Callback<Boolean>{
                    override fun onResponse(
                        call: retrofit2.Call<Boolean>,
                        response: Response<Boolean>
                    ) {
                        if(response.isSuccessful){
                            if(response.body()!!){
                                showToast(R.string.Successfully);
                            } else {
                                showToast(R.string.failed);
                            }
                        }
                        finish()
                    }

                    override fun onFailure(call: retrofit2.Call<Boolean>, t: Throwable) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }

    private fun showToast(toastMsg: Int) {
        Toast.makeText(this,"Assigned $toastMsg",Toast.LENGTH_SHORT).show();
    }
}
