package com.example.quadcare.Activities

import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.example.quadcare.Fragments.ButtonFragment
import com.example.quadcare.Models.AddDoctorViewModel
import com.example.quadcare.Models.DBStorage
import com.example.quadcare.Models.Specialization
import com.example.quadcare.NetworkCalls.ApiInterface
import com.example.quadcare.NetworkCalls.ApiUtilities
import com.example.quadcare.NetworkCalls.DoctorItem
import com.example.quadcare.R
import com.example.quadcare.constants.APP_TABLE
import com.example.quadcare.constants.COL_ADESCRIPTION
import com.example.quadcare.constants.COL_ANAME
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class AddDoctorActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var name: TextView;
    private lateinit var specialization: Spinner;
    private lateinit var phoneNumber: TextView;
    private lateinit var shiftStartTime: TextView;
    private lateinit var shiftEndTime: TextView;
    private var specializations: ArrayList<Specialization> = ArrayList();
    private var specializationNames: ArrayList<String> = ArrayList();
    private lateinit var toolBar: Toolbar;
    private lateinit var sharedPreferences: SharedPreferences;
    private var switchStatus: Boolean = false;
    private lateinit var switch: Switch;
    private var db = DBStorage(this);
    private final var ADDDOCTORACTIVITY = "Add Doctor Activity";
    private lateinit var progressBar: ProgressBar;

    val addDoctorViewModel: AddDoctorViewModel by lazy {
        ViewModelProviders.of(this)[AddDoctorViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_doctor)
        setTheme();
        setToolBar();
        getSpecializations()
    }

    private fun getSpecializations() {
        progressBar = findViewById(R.id.assign_progress_bar);
        var retrofit = ApiUtilities.getInstance().create(ApiInterface::class.java);
        var getSpecialization = retrofit.getSpecializations();
        getSpecialization.enqueue(object : Callback<List<Specialization>>{
            override fun onResponse(
                call: Call<List<Specialization>>,
                response: Response<List<Specialization>>
            ) {
                if(response.isSuccessful && response.body() != null){
                    specializations.add(
                        Specialization(
                        id = -1, name = "Select Specialization"
                    ))
                    specializationNames.add("Select Specialization")
                    for(specialization in response.body()!!){
                        specializations.add(Specialization(
                            specialization.id,specialization.name
                        ));
                        specializationNames.add(specialization.name)
                    }
                }
                println(specializationNames)
                progressBar.visibility = View.GONE
                setControls()
            }

            override fun onFailure(call: Call<List<Specialization>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setControls() {
        setName();
        setSpecilization();
        setPhoneNumber();
        connectShiftStartTime();
        connectShiftEndTime();
        setAddDoctorBtn();
    }

    private fun setName() {
        name = findViewById(R.id.add_doctor_name)
        name.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                addDoctorViewModel.name = p0.toString();
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun setPhoneNumber() {
        phoneNumber = findViewById(R.id.add_doctor_phone);
        phoneNumber.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                addDoctorViewModel.phoneNumber = p0.toString();
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun setSpecilization() {
        this.specialization = this.findViewById(R.id.add_doctor_specialization)
        specialization.onItemSelectedListener = this

        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item, specializationNames as List<Any?>
        )
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        specialization.adapter = ad
    }

    private fun connectShiftStartTime() {
        shiftStartTime = findViewById(R.id.doctor_start_time)
        shiftStartTime.setOnClickListener{
            setShiftStartTime()
        }
    }

    private fun connectShiftEndTime() {
        shiftEndTime = findViewById(R.id.doctor_end_time)
        shiftEndTime.setOnClickListener {
            setShiftEndTime()
        }
    }

    private fun setShiftEndTime(){
        var c = Calendar.getInstance()

        var hour = c.get(Calendar.HOUR_OF_DAY)
        var minitue = c.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, hourOfDay, minute ->
            var time = "${hourOfDay}:${minute}"
            shiftEndTime.setText(time)
            addDoctorViewModel.shiftEndTime = time
        },hour,minitue,false).show()
    }

    private fun setShiftStartTime(){
        var c = Calendar.getInstance()

        var hour = c.get(Calendar.HOUR_OF_DAY)
        var minitue = c.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, hourOfDay, minite ->
            var time = "${hourOfDay}:${minite}"
            shiftStartTime.text = time
            addDoctorViewModel.shiftStartTime = time
        },hour,minitue,false).show()
    }

    private fun setAddDoctorBtn() {
        var buttonFrag = ButtonFragment();
        var bundle = Bundle();
        bundle.putString("buttonTextValue", getString(R.string.add_doctor))
        buttonFrag.arguments = bundle;
        buttonFrag.mButtonClickListner = object: ButtonFragment.buttonClickListner{
            override fun onClick() {
                var hashMap_3 = HashMap<String, Any>();
                hashMap_3.put(COL_ANAME,ADDDOCTORACTIVITY)
                hashMap_3.put(COL_ADESCRIPTION,"Add Patients Clicked !!")
                db.insertData(APP_TABLE,hashMap_3)
                addDoctor();
            }
        }
        supportFragmentManager.beginTransaction().add(R.id.add_doctor_btn,buttonFrag).commit()
    }

    private fun addDoctor() {
        progressBar.visibility = View.VISIBLE
        Log.d("AddDoctorActivity","Add Doctor Clicked")
        var retrofitObj = ApiUtilities.getInstance().create(ApiInterface::class.java)
        retrofitObj.addDoctor(DoctorItem(0,addDoctorViewModel.name,addDoctorViewModel.specializationId,addDoctorViewModel.specialzation,addDoctorViewModel.phoneNumber,"",addDoctorViewModel.shiftStartTime,addDoctorViewModel.shiftEndTime))
            .enqueue(object : Callback<Boolean>{
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if(response.isSuccessful && response.body()!!) {
                        Log.d("AddDoctor","Doctor Data Saved Successfully")
                    }else {
                        Log.d("AddDoctor","Doctor Data Saving Failed")
                    }

                    progressBar.visibility = View.GONE;
                }
                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        addDoctorViewModel.specializationId = specializations[p2].id;
        Log.d("Spinner Doctor","Spinner DOctor")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
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

    private fun removeToolItems(menu: Menu){
        menu.removeItem(R.id.memu_search_icon);
        menu.removeItem(R.id.addIcon);
    }
}
