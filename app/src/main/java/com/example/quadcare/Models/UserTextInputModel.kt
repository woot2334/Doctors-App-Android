package com.example.quadcare.Models

import android.content.Context
import java.io.File

class UserTextInputModel(var _context: Context, var fileName: String) {

    private fun makeFile(): File {
        return File(_context.filesDir,fileName);
    }

    fun saveText(s: String) {
        val file = this.makeFile();
        file.delete();
        file.writeText(s);
    }

    fun loadText(): String {
        val file = this.makeFile();
        var s = "";
        if(file.exists()) {
            s = file.readText();
        }
        return s
    }


}
