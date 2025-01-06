package com.example.d2runewords

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setMargins
import com.google.android.flexbox.FlexboxLayout

class RunesActivity : AppCompatActivity() {
    @SuppressLint("DefaultLocale", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_runes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val con = findViewById<FlexboxLayout>(R.id.container)
        runes.forEach { rune ->
            val im = LinearLayout(this)
            im.orientation = LinearLayout.VERTICAL
            var pm = LayoutParams(
                DimensionConverter.dp2px(63),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            pm.setMargins(DimensionConverter.dp2px(2))
            im.layoutParams = pm
            im.gravity = Gravity.CENTER_HORIZONTAL
            con.addView(im)

            val img = ImageView(this)
            val i =
                resources.getIdentifier(String.format("rune%02d", rune.no), "drawable", packageName)
            img.setImageDrawable(getDrawable(i))
            pm =  LayoutParams(DimensionConverter.dp2px(50), DimensionConverter.dp2px(50))
            pm.bottomMargin = 3
            img.layoutParams = pm
            im.addView(img)

            val text = TextView(this)
            pm = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            pm.bottomMargin = DimensionConverter.dp2px(10)
            text.layoutParams = pm
            text.text = String.format("#${rune.no} ${rune.name}")
            text.setTextColor(getColor(R.color.lightgray))
            text.textSize = 12f
            im.addView(text)
            im.setOnClickListener {
                val intent = Intent(this, RuneWordsActivity::class.java).apply {
                    putExtra(getString(R.string.trans_rune), rune.no)
                }
                startActivity(intent)
            }
        }
    }
}