package com.example.d2runewords

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.setMargins

class RuneWordsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rune_words)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun RunesClick(view: View) {
        val intent =  Intent(this, RunesActivity::class.java)
        startActivity(intent)
    }

    fun generate(version:String, category: String, slot:Int, rune:Int, search:String): List<RuneWord>{
        var data :MutableList<RuneWord> = mutableListOf()
        if(version== "1.*") {
            data.addAll(rgeneral)
            data.addAll(r110)
            data.addAll(r111)
        }
        else if(version == "ladder")
        {
            data.addAll(rgeneral)
            data.addAll(r110)
            data.addAll(r111)
            data.addAll(rladder110)
        }
        else
        {
            data.addAll(rgeneral)
            data.addAll(r110)
            data.addAll(r111)
            data.addAll(rladder110)
            data.addAll(r24)
            data.addAll(r26)
        }

        fun matchEquipment(eqip: String) : Boolean
        {
            var bodyArmor = arrayOf("近战武器","棍棒","铁锤","钉头锤","权杖","刀剑","法杖","斧头","手杖","锤类","爪类","匕首","长柄武器","长矛",)
            var armor = bodyArmor + arrayOf("武器","弓","十字弓")
            var shield = arrayOf("盾牌","圣骑士盾牌")

            if(category == "武器")
                return armor.contains(eqip);
            else if(category == "近战武器")
                return bodyArmor.contains(eqip);
            else if(category == "盾牌")
                return shield.contains(eqip);
            else if(arrayOf("头盔","盔甲").contains(category))
                return category == eqip;
            else if(bodyArmor.drop(1).contains(category))
                return arrayOf("武器","近战武器",category).contains(eqip);
            else //"弓","十字弓"
                return arrayOf("弓","十字弓","武器").contains(eqip);
        }

        val resData =
            data.filter { en ->
                (category.isBlank() || en.equip.find { eq -> matchEquipment(eq) } != null) &&
                (slot == 0 || en.slotNum == slot) &&
                (rune == 0 || en.runes.contains(rune)) &&
                (search.isBlank() || en.chnName.contains(search) || en.effect.contains(search))
            }

        return resData
    }

    fun OKClick(view: View) {
        var version = findViewById<Spinner>(R.id.version).selectedItem.toString()
        var category = findViewById<Spinner>(R.id.category).selectedItem.toString()
        var slot = findViewById<Spinner>(R.id.slot).selectedItem.toString().toIntOrNull()?:0
        var rune = findViewById<Spinner>(R.id.rune).selectedItem.toString().substring(0,2).toIntOrNull()?:0
        var search = findViewById<EditText>(R.id.search).text.toString()

        var strArray = resources.getStringArray(R.array.version)
        val dic = mapOf(strArray[0] to "", strArray[1] to "1.*", strArray[2] to "ladder", strArray[3] to "2.+")
        version = dic[version].toString()

        strArray = resources.getStringArray(R.array.category)
        if(category == strArray[0]) category = ""
        else if(category.contains("弓")) category = "弓"

        val data = generate(version,category,slot,rune,search)
        if(data.isNotEmpty()) {
            val res = findViewById<LinearLayout>(R.id.res)
            res.removeAllViews()
            for (d in data) {
                val text = TextView(this)
                val pm = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    DimensionConverter.dp2px(33)
                )
                pm.setMargins(DimensionConverter.dp2px(50),DimensionConverter.dp2px(2),DimensionConverter.dp2px(50),DimensionConverter.dp2px(2))
                text.layoutParams = pm
                text.setBackgroundColor(getColor(R.color.darkgray))
                text.setTextColor(getColor(R.color.white))
                text.text = d.chnName + " " +  d.runes.joinToString("+")
                text.gravity = Gravity.CENTER
                text.setOnClickListener({v ->
                    val intent =  Intent(this, DetailsActivity::class.java)
                    intent.putExtra(getString(R.string.rw_name), d.chnName)
                    intent.putExtra(getString(R.string.rw_ver), d.ver)
                    intent.putExtra(getString(R.string.rw_equip), d.equip)
                    intent.putExtra(getString(R.string.rw_runes), d.runes)
                    intent.putExtra(getString(R.string.rw_level), d.level)
                    intent.putExtra(getString(R.string.rw_effect), d.effect)
                    startActivity(intent)
                })

                res.addView(text)
            }
        }
        else {
            AlertDialog.Builder(this)
                .setMessage("查无结果")
                .show();
        }
    }
}