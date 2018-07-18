package com.example.hovhannesstepanyan.qrscanner

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import android.widget.TextView

class Main2Activity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val bundle = intent.extras

        val value = bundle.get("privet") as HashMap<*, *>

        val layout: LinearLayout = findViewById(R.id.layout)


        value.forEach {
            val textView = TextView(this)
            textView.text = "${it.key} = ${it.value}"
            layout.addView(textView)
        }




    }
}
