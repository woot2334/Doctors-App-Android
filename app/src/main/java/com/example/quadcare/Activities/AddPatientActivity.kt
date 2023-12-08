package com.example.quadcare.Activities
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
import com.example.quadcare.Models.AddPatientViewModel
import com.example.quadcare.Models.DBStorage
import com.example.quadcare.NetworkCalls.ApiInterface
import com.example.quadcare.NetworkCalls.ApiUtilities
import com.example.quadcare.NetworkCalls.PatientItem
import com.example.quadcare.R
import com.example.quadcare.constants.APP_TABLE
import com.example.quadcare.constants.COL_ADESCRIPTION
import com.example.quadcare.constants.COL_ANAME
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.HashMap

class AddPatientActivity : AppCompatActivity() {
    private lateinit var add_patient_name : EditText;
    private lateinit var add_patient_age : EditText;
    private lateinit var add_patient_phone : EditText;
    private lateinit var add_patient_email : EditText;
    private lateinit var add_patient_address : EditText;
    private lateinit var toolBar: Toolbar;
    private lateinit var sharedPreferences: SharedPreferences;
    private var switchStatus: Boolean = false;
    private var db = DBStorage(this);
    private lateinit var switch: Switch;
    private var ADDPATIENTACTIVITY = "AddPatient Activity";
    private lateinit var progressBar: ProgressBar;

    val addPatientViewModel: AddPatientViewModel by lazy {
        ViewModelProviders.of(this)[AddPatientViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)
        setTheme()
        setToolBar()
        this.setControls();
    }
    private fun setControls(){
        this.setName()
        this.setAge()
        this.setPhone()
        this.setEmail()
        this.setAddress()
        this.setAddPatient();
        progressBar = findViewById(R.id.assign_progress_bar)
        progressBar.visibility = View.GONE;
    }

    private fun setAddress() {
        this.add_patient_address = this.findViewById(R.id.add_patient_address)
        this.add_patient_address.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                addPatientViewModel.address = p0.toString();
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun setEmail() {
        this.add_patient_email = this.findViewById(R.id.add_patient_email)
        this.add_patient_email.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                addPatientViewModel.email = p0.toString();
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun setPhone() {
        this.add_patient_phone = this.findViewById(R.id.add_patient_phone)
        this.add_patient_phone.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                addPatientViewModel.phoneNumber = p0.toString()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun setAge() {
        this.add_patient_age = this.findViewById(R.id.add_patient_age)
        this.add_patient_age.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                addPatientViewModel.age = Integer.parseInt(p0.toString());
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun setName(){
        this.add_patient_name = this.findViewById(R.id.add_patient_name)
        this.add_patient_name.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                addPatientViewModel.name = p0.toString();
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun setAddPatient(){
        var buttonFrag = ButtonFragment();
        var bundle = Bundle();
        bundle.putString("buttonTextValue", getString(R.string.add_patient))
        buttonFrag.arguments = bundle;
        buttonFrag.mButtonClickListner = object: ButtonFragment.buttonClickListner{
            override fun onClick() {
                var hashMap_3 = HashMap<String, Any>();
                hashMap_3.put(COL_ANAME,ADDPATIENTACTIVITY)
                hashMap_3.put(COL_ADESCRIPTION,"Add Patients Clicked !!")
                db.insertData(APP_TABLE,hashMap_3)
                savePatient();
            }
        }
        supportFragmentManager.beginTransaction().add(R.id.add_patient_Save,buttonFrag).commit()
    }

    private fun savePatient(){
        progressBar = findViewById(R.id.assign_progress_bar);
        Log.d("AddPatientActivity","Save Patient Clicked");
        var retrofit = ApiUtilities.getInstance().create(ApiInterface::class.java)
        retrofit.savePatient(PatientItem(addPatientViewModel.address, addPatientViewModel.age, 0, addPatientViewModel.name,addPatientViewModel.email,addPatientViewModel.phoneNumber))
            .enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if(response.isSuccessful && response.body() != null){
                        savePatientToast()
                    }
                    progressBar.visibility = View.GONE;
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    throw t;
                    //Toast.makeText(this, "network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                }
            })
    }

    private fun savePatientToast(){
        Toast.makeText(this,"Patient Added Successfully",Toast.LENGTH_SHORT).show();
        finish()
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
