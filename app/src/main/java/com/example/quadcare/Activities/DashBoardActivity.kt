package com.example.quadcare.Activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.setPadding
import com.example.quadcare.Models.DBStorage
import com.example.quadcare.Models.activity
import com.example.quadcare.R
import com.example.quadcare.constants.APP_TABLE

class DashBoardActivity : AppCompatActivity() {

    private lateinit var tableLayout : TableLayout;
    lateinit var dashBoardModel : ArrayList<activity>;
    //private lateinit var dashBoardModel: DashBoardModel;
    lateinit var notesButton: Button;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        setConnections()
        getData()
        loadData()
    }

    private fun setConnections() {
        tableLayout = findViewById(R.id.tableLayout)
        notesButton = findViewById(R.id.notesButton);
        notesButton.setOnClickListener{
            openNotesActivity();
        }
    }

    private fun openNotesActivity() {
        var intent = Intent(this,NotesActivity::class.java);
        startActivity(intent);
    }

    private fun getData(){
        var dbStore = DBStorage(this)
        dashBoardModel = dbStore.getData(APP_TABLE)
    }

    private fun loadData() {
        for (i in dashBoardModel){
            tableLayout.addView(addTableRow(i))
        }
    }

    private fun addTableRow(dashBoardEmpModel: activity): View {
        var tableRow = TableRow(this)
        var params  = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT)
        tableRow.layoutParams = params
        var textViewName = getTextView(dashBoardEmpModel.actName)
        var textViewAmount = getTextView(dashBoardEmpModel.actDecription)
        tableRow.addView(textViewName)
        tableRow.setBackgroundColor(getColor(R.color.secondaryColor))
        tableRow.addView(textViewAmount)
        tableRow.setPadding(40)
        return tableRow
    }

    @SuppressLint("ResourceAsColor")
    private fun getTextView(text: String) : TextView {
        var textView = TextView(this)
        var params  = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT,1f)
        textView.text = text
        println(text)
        textView.setTextColor(R.color.white);
        textView.gravity = Gravity.CENTER_HORIZONTAL
        textView.layoutParams = params
        return textView
    }
}
