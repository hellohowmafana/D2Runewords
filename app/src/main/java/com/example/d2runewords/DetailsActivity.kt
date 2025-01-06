package com.example.d2runewords

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
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
import androidx.core.view.forEach
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import kotlin.math.ceil


class DetailsActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "DefaultLocale", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rwName = intent.getStringExtra(getString(R.string.rw_name))
        findViewById<TextView>(R.id.name).text = rwName
        var value = intent.getStringExtra(getString(R.string.rw_ver))?:""
        value = when(value){
            "general"->""
            "ladder1.10"->"天梯1.10"
            else -> value
        }
        findViewById<TextView>(R.id.ver).text = value
        findViewById<TextView>(R.id.level).text = "角色等级需求:" +
                intent.getIntExtra(getString(R.string.rw_level), 0).toString()
        val effect = findViewById<TextView>(R.id.effect)
        value = intent.getStringExtra(getString(R.string.rw_effect))?:""
        val f : String = getColor(R.color.white).toString()
        if(value.isNotEmpty())
            value = value.replace("<",
                "<font color=\"#${getColor(R.color.white).toUInt().toString(16).padStart(8, '0').substring(2)}\">").
            replace(">","</font>").replace("|","<br>")
        effect.text = Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT)
        effect.setOnClickListener {
            val intent = Intent(this, ItemsActivity::class.java)
            intent.putExtra(getString(R.string.trans_runeword), rwName)
            startActivity(intent)
        }

        val runesLayout = findViewById<LinearLayout>(R.id.runes)
        runesLayout.removeAllViews()
        val runesData = intent.getIntArrayExtra(getString(R.string.rw_runes))
        runesData?.forEach { runeData ->
            run {
                val unit = LinearLayout(this)
                var pm = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                pm.leftMargin = DimensionConverter.dp2px(2)
                pm.rightMargin = DimensionConverter.dp2px(2)
                unit.layoutParams = pm
                unit.orientation = LinearLayout.VERTICAL
                unit.gravity = Gravity.CENTER
                val img = ImageView(this)
                val i =
                    resources.getIdentifier(String.format("rune%02d", runeData), "drawable", packageName)
                img.setImageDrawable(getDrawable(i))
                pm = LayoutParams(DimensionConverter.dp2px(52), DimensionConverter.dp2px(52))
                pm.bottomMargin = DimensionConverter.dp2px(1)
                img.layoutParams = pm
                unit.addView(img)

                val text = TextView(this)
                text.gravity = Gravity.CENTER
                val rr = runes.find { rr -> rr.no == runeData }
                if(rr != null){
                    text.text = "(${rr.no})${rr.name}"
                }
                text.setTextColor(getColor(R.color.lightgray))
                text.textSize = 10f
                unit.addView(text)

                runesLayout.addView(unit)
            }
        }

        val equip = findViewById<TextView>(R.id.equipment);
        value = intent.getStringArrayExtra(getString(R.string.rw_equip))?.joinToString ("/") ?: ""
        equip.text = runesData?.size.toString() + "凹槽 " + value
    }
}