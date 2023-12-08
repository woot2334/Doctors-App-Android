package com.example.quadcare.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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
import com.example.quadcare.Adapters.DoctorsRecyclerAdapter
import com.example.quadcare.Adapters.PatientsRecyclerAdapter
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

class ViewDoctorsActivity : AppCompatActivity() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<DoctorsRecyclerAdapter.ViewHolder>? = null
    private lateinit var recyclerview_activity: RecyclerView;
    private lateinit var progressBar: ProgressBar;
    private var doctorListdata: ArrayList<DoctorItem> = ArrayList();
    private var displayDoctorDate = ArrayList<DoctorItem>();
    private lateinit var toolBar: Toolbar;
    private lateinit var sharedPreferences: SharedPreferences;
    private var switchStatus: Boolean = false;
    private lateinit var switch:Switch;
    private var db = DBStorage(this);
    private var VIEWDOCTORACTIVITY = "View Doctor Activity";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_doctors)
        setToolBar();
        setTheme();
        getDoctors();
    }

    private fun getDoctors() {
        var retrofit = ApiUtilities.getInstance().create(ApiInterface::class.java)
        progressBar = findViewById(R.id.progress_bar)
        var apiCall = retrofit.getDoctors();

        apiCall.enqueue(object : Callback<List<DoctorItem>> {
            override fun onResponse(
                call: Call<List<DoctorItem>>,
                response: Response<List<DoctorItem>>
            ) {
                var responseBody = response.body();
                progressBar.visibility = View.GONE;
                if (responseBody != null) {
                    for (data in responseBody) {
                        var doctorItem = DoctorItem(data.id, data.name, data.specializationId, data.specialization,data.phone,data.email,data.shiftStartTime,data.shiftEndTime);
                        doctorListdata.add(doctorItem);
                        displayDoctorDate.add(doctorItem);
                    }
                    setRecycleView();
                }
            }

            override fun onFailure(call: Call<List<DoctorItem>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun setRecycleView(){
        layoutManager = LinearLayoutManager(this)
        recyclerview_activity = this.findViewById(R.id.doctor_recyclerview_activity)
        recyclerview_activity.layoutManager = layoutManager
        adapter = DoctorsRecyclerAdapter(displayDoctorDate,object: DoctorsRecyclerAdapter.doctorAdapterListner{
            override fun onClickListner(number: Long) {
                    openDoctorPhone(number)
            }
        });
        recyclerview_activity.adapter = adapter
    }

    fun openDoctorPhone(number: Long){
        val it = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number))
        startActivity(it)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.addIcon -> {
                var intent = Intent(this,AddDoctorActivity::class.java)
                var hashMap_3 = HashMap<String, Any>();
                hashMap_3.put(COL_ANAME,VIEWDOCTORACTIVITY)
                hashMap_3.put(COL_ADESCRIPTION,"Add Doctors Clicked !!")
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
        println("Triggered")
        menuInflater.inflate(R.menu.viewpage_menu,menu);
        var menuItem = menu!!.findItem(R.id.memu_search_icon)
        setToggle(menu!!)
        println(menuItem)
        if(menuItem != null) {

            val searchView = menuItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()){
                        displayDoctorDate.clear()
                        val search = newText.lowercase(Locale.getDefault())
                        doctorListdata.forEach{
                            if(it.name.lowercase(Locale.getDefault()).contains(search)){
                                displayDoctorDate.add(it)
                                println("Added")
                            }
                        }
                        recyclerview_activity.adapter!!.notifyDataSetChanged()
                    }else {
                        displayDoctorDate.clear()
                        displayDoctorDate.addAll(doctorListdata)
                        recyclerview_activity.adapter!!.notifyDataSetChanged()
                    }
                    return true
                }
            })
        }
        return true;
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
        doctorListdata.clear()
        displayDoctorDate.clear()
        recyclerview_activity.adapter!!.notifyDataSetChanged()
        progressBar.visibility = View.VISIBLE
        getDoctors();
    }
}
