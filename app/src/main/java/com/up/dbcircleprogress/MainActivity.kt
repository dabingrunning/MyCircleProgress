package com.up.dbcircleprogress

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.up.dbcircleprogress.view.MyCircleProgress
import com.up.dbcircleprogress.view.MyLineProgressBar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button_up).setOnClickListener {
            findViewById<MyCircleProgress>(R.id.my_progress).setPercentValue(1f)
            findViewById<MyLineProgressBar>(R.id.line_progress).setProgress(50f)
        }
    }


}