package com.example.quadcare.Activities

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.example.quadcare.Models.DBStorage
import com.example.quadcare.Models.UserTextInputModel
import com.example.quadcare.R

class NotesActivity : AppCompatActivity() {

    private lateinit var notesInput : EditText;
    private lateinit var toolBar: Toolbar;
    private lateinit var sharedPreferences: SharedPreferences;
    private var switchStatus: Boolean = false;
    private lateinit var switch: Switch;
    private lateinit var fileManager: UserTextInputModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        setConnections();
        setTheme()
        setToolBar()
    }

    private fun setConnections() {
        notesInput = findViewById(R.id.notesInput);
        fileManager = UserTextInputModel(this,"notes.txt")
        notesInput.setText(fileManager.loadText());
        notesInput.addTextChangedListener(object:TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println(p0.toString());
                fileManager.saveText(p0.toString());
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

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

    private fun removeToolItems(menu: Menu){
        menu.removeItem(R.id.memu_search_icon);
        menu.removeItem(R.id.addIcon);
    }

    private fun setToolBar() {
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        supportActionBar?.setDisplayShowTitleEnabled(false);
    }

}
