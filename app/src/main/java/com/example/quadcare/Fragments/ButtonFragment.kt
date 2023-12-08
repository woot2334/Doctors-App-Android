package com.example.quadcare.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.quadcare.R

class ButtonFragment : Fragment() {

    private lateinit var frag_button: Button;
    public lateinit var mButtonClickListner : buttonClickListner;

    public interface buttonClickListner {
        public fun onClick();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_button, container, false);
        setConnections(view);
        return view;
    }

    private fun setConnections(view:View) {
        frag_button = view.findViewById(R.id.button_frag);
        frag_button.text = arguments?.getString("buttonTextValue");
        frag_button.setOnClickListener{
            mButtonClickListner.onClick();
        }
    }
}
