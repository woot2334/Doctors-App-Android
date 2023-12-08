package com.example.quadcare.Activities

import android.app.DatePickerDialog
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
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.example.quadcare.Fragments.ButtonFragment
import com.example.quadcare.Models.AddAppointmentViewModel
import com.example.quadcare.Models.DBStorage
import com.example.quadcare.NetworkCalls.*
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

class AddAppointmentActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var patientSpinner: Spinner;
    private lateinit var reasonOfAppointment: EditText;
    private lateinit var appointmentTime: EditText;
    private lateinit var appointmentDate: EditText;
    private var patientList: ArrayList<PatientItem> = ArrayList();
    private val patientNames: ArrayList<String> = ArrayList();
    private lateinit var toolBar: Toolbar;
    private lateinit var sharedPreferences: SharedPreferences;
    private var switchStatus: Boolean = false;
    private lateinit var switch: Switch;
    private var db = DBStorage(this);
    private final val ADDAPPOINTMENTACTIVITY = "AddAppointment Activity";
    private lateinit var progressBar: ProgressBar;

    val appointmentViewModel: AddAppointmentViewModel by lazy {
        ViewModelProviders.of(this)[AddAppointmentViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_appointment)
        setTheme();
        setToolBar();
        getPatientsList();
    }

    private fun getPatientsList() {
        progressBar = findViewById(R.id.assign_progress_bar)
        var retrofit = ApiUtilities.getInstance().create(ApiInterface::class.java)
        var getPatients = retrofit.getPatients();
        getPatients.enqueue(object : Callback<List<PatientItem>> {

            override fun onResponse(
                call: Call<List<PatientItem>>,
                response: Response<List<PatientItem>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    for (patient in response.body()!!) {
                        patientList.add(
                            PatientItem(
                                id = patient.id,
                                name = patient.name,
                                age = patient.age,
                                address = patient.address,
                                phone = patient.phone,
                                email = patient.email
                            )
                        )
                        patientNames.add(patient.name)
                    }
                }
                progressBar.visibility = View.GONE;
                setControls()
            }

            override fun onFailure(call: Call<List<PatientItem>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setControls() {
        setSpinner();
        setReasonOfAppointment();
        setAppointmentTime();
        setAppointmentDate();
        setAddAppointmentBtn();
    }

    // Spinner Start
    private fun setSpinner() {
        this.patientSpinner = this.findViewById(R.id.appointment_Patient)
        patientSpinner.onItemSelectedListener = this

        val ad: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            android.R.layout.simple_spinner_item, patientNames as List<Any?>
        )
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        patientSpinner.adapter = ad
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        appointmentViewModel.patientId = patientList[p2].id
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
    // Spinner end

    private fun setAppointmentDate() {
        appointmentDate = findViewById(R.id.appointment_date)
        appointmentDate.setOnClickListener {
            openAppointmentDate()
        }
    }

    private fun setAppointmentTime() {
        appointmentTime = findViewById(R.id.appointment_time)
        appointmentTime.setOnClickListener {
            openAppointmentTime()
        }
    }

    private fun openAppointmentDate() {
        var c = Calendar.getInstance();
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)
        var dpc =
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, day ->
                appointmentDate.setText("${month + 1}/${day}/${year}")
                appointmentViewModel.appointmentDate = "${month + 1}/${day}/${year}"
            }, year, month, day)
        dpc.show()
    }

    private fun openAppointmentTime() {
        var c = Calendar.getInstance()
        var hour = c.get(Calendar.HOUR_OF_DAY)
        var minitue = c.get(Calendar.MINUTE)
        TimePickerDialog(this, { view, hourOfDay, minite ->
            var time = "${hourOfDay}:${minite}"
            appointmentTime.setText(time)
            appointmentViewModel.appointmentTime = time
        }, hour, minitue, false).show()
    }

    private fun setReasonOfAppointment() {
        reasonOfAppointment = findViewById(R.id.appointment_reasonForAppointment)
        reasonOfAppointment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                appointmentViewModel.reasonofAppointment = p0.toString();
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun setAddAppointmentBtn() {
        var buttonFrag = ButtonFragment();
        var bundle = Bundle();
        bundle.putString("buttonTextValue", getString(R.string.book_appointment))
        buttonFrag.arguments = bundle;
        buttonFrag.mButtonClickListner = object: ButtonFragment.buttonClickListner{
            override fun onClick() {
                var hashMap_3 = HashMap<String, Any>();
                hashMap_3.put(COL_ANAME,ADDAPPOINTMENTACTIVITY)
                hashMap_3.put(COL_ADESCRIPTION,"Add Patients Clicked !!")
                db.insertData(APP_TABLE,hashMap_3)
                addAppointment()
            }
        }
        supportFragmentManager.beginTransaction().add(R.id.add_appointment_btn,buttonFrag).commit()
    }

    private fun addAppointment() {
        progressBar.visibility = View.VISIBLE
        var retrofitObj = ApiUtilities.getInstance().create(ApiInterface::class.java)
        retrofitObj.bookAppointment(
            AppointmentItem(appointmentViewModel.appointmentDate,-1,0,false,appointmentViewModel.patientId,appointmentViewModel.appointmentTime,"","")
        ).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful && response.body()!!) {
                    toastMsg( "Appointment Data Saved Successfully")
                } else {
                    toastMsg("Appointment Data Saving Failed")
                }
                Log.d("AddAppointmenActivity","AppointmentBooked")
                progressBar.visibility = View.GONE;
                finish();
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun toastMsg(msg:String) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT)
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
