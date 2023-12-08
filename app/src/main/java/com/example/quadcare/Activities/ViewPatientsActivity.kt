package com.example.quadcare.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quadcare.Adapters.PatientsRecyclerAdapter
import com.example.quadcare.Models.DBStorage
import com.example.quadcare.NetworkCalls.*
import com.example.quadcare.R
import com.example.quadcare.constants.APP_TABLE
import com.example.quadcare.constants.COL_ADESCRIPTION
import com.example.quadcare.constants.COL_ANAME
import retrofit2.*
import java.util.*

class ViewPatientsActivity : AppCompatActivity() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<PatientsRecyclerAdapter.ViewHolder>? = null
    private lateinit var recyclerview_activity: RecyclerView;
    private lateinit var progressBar: ProgressBar;
    private var patientListdata = ArrayList<PatientItem>();
    private var displayPatientData = ArrayList<PatientItem>();
    private lateinit var toolBar: Toolbar;
    private lateinit var sharedPreferences: SharedPreferences;
    private var switchStatus: Boolean = false;
    private var db =  DBStorage(this);
    private var VIEWPATIENTACTIVITY = "View Patient Activity";
    private lateinit var switch: Switch;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_patients)
        setTheme()
        setToolBar()
        getPatients();
    }

    private fun getPatients(){
        var retrofit = ApiUtilities.getInstance().create(ApiInterface::class.java)
        progressBar = findViewById(R.id.progress_bar)
        var apiCall = retrofit.getPatients();

        apiCall.enqueue(object : Callback<List<PatientItem>> {
            override fun onResponse(
                call: Call<List<PatientItem>>,
                response: Response<List<PatientItem>>
            ) {
                var responseBody = response.body();
                progressBar.visibility = View.GONE;
                if (responseBody != null) {
                    for ( data in responseBody){
                        var patientItem = PatientItem(data.address,data.age,data.id,data.name,data.email,data.phone);
                        patientListdata.add(patientItem);
                        displayPatientData.add(patientItem);
                    }
                    setRecycleView();
                }
            }

            override fun onFailure(call: Call<List<PatientItem>>, t: Throwable) {

            }
        })
    }

    private fun setRecycleView() {
        layoutManager = LinearLayoutManager(this)
        recyclerview_activity = this.findViewById(R.id.recyclerview_activity)
        recyclerview_activity.layoutManager = layoutManager;
        adapter = PatientsRecyclerAdapter(displayPatientData,object: PatientsRecyclerAdapter.patientAdapterListner{
            override fun onClickListner(patientId: Long, test: String) {
                if (test == "phone"){
                    print("***************************************" + patientId)
                    openPhoneApp(patientId)

                }else if(test == "image"){
                    openPatientProfileActivity(patientId)
                }
            }

            override fun onClickListner(test: String) {
                openMailApp(test)
            }
        });
        recyclerview_activity.adapter = adapter;
    }

    private fun openMailApp(test: String) {
        val it = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",test,null))
        startActivity(Intent.createChooser(it, "send email to patients..........."))
//        if (it.resolveActivity(packageManager) != null){
//            startActivity(it)
//        }else{
//            Toast.makeText(this, "Required App is not installed", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun openPhoneApp(id: Long) {
        val it = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + id))
        startActivity(it)
    }

    private fun openPatientProfileActivity(id: Long) {
        var id = id.toInt()
        var bundle = Bundle();
        bundle.putInt("PatientId",id)
        var intent = Intent(this,PatientProfileActivity::class.java).also {
            it.putExtra("profile_args",bundle)
        }
        startActivity(intent)
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
        menuInflater.inflate(R.menu.viewpage_menu,menu)
        var menuItem = menu!!.findItem(R.id.memu_search_icon)
        setToggle(menu)
        if(menuItem != null) {

            val searchView = menuItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()){
                        displayPatientData.clear()
                        val search = newText.lowercase(Locale.getDefault())
                        patientListdata.forEach{
                            if(it.name.lowercase(Locale.getDefault()).contains(search)
                                || it.address.lowercase(Locale.getDefault()).contains(search)){
                                displayPatientData.add(it)
                                println("Added")
                            }
                        }
                        recyclerview_activity.adapter!!.notifyDataSetChanged()
                    }else {
                        displayPatientData.clear()
                        displayPatientData.addAll(patientListdata)
                        recyclerview_activity.adapter!!.notifyDataSetChanged()
                    }
                    return true
                }
            })
        }
        return true
    }

    private fun setToggle(menu:Menu) {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.addIcon -> {
                var intent = Intent(this,AddPatientActivity::class.java)
                var hashMap_3 = HashMap<String, Any>();
                hashMap_3.put(COL_ANAME,VIEWPATIENTACTIVITY)
                hashMap_3.put(COL_ADESCRIPTION,"Add Patients Clicked !!")
                db.insertData(APP_TABLE,hashMap_3)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRestart() {
        super.onRestart()
        patientListdata.clear()
        displayPatientData.clear()
        recyclerview_activity.adapter!!.notifyDataSetChanged()
        progressBar.visibility = View.VISIBLE
        getPatients();
    }
}
