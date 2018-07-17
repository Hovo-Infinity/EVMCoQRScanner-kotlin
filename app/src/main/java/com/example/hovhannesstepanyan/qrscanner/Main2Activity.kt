package com.example.hovhannesstepanyan.qrscanner

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val bundle = intent.extras

        val value = bundle.getString("privet")

        val layout: LinearLayout = findViewById(R.id.layout)



        val textView = TextView(this)
        textView.setText(value)

        layout.addView(textView)




    }
}
