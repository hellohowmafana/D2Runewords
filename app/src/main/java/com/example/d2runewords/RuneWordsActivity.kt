package com.example.d2runewords

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.buildSpannedString
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.aigestudio.wheelpicker.WheelPicker

class RuneWordsActivity : AppCompatActivity() {
    var version = ""
    var category = ""
    var slot = 0
    var rune = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rune_words)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<EditText>(R.id.search).addTextChangedListener { showResults() }
        showResults()
    }

    fun generate(
        version: String,
        category: String,
        slot: Int,
        rune: Int,
        search: String
    ): List<RuneWord> {
        var data: MutableList<RuneWord> = mutableListOf()
        if (version == "1.*") {
            data.addAll(rgeneral)
            data.addAll(r110)
            data.addAll(r111)
        } else if (version == "ladder") {
            data.addAll(rgeneral)
            data.addAll(r110)
            data.addAll(r111)
            data.addAll(rladder110)
        } else {
            data.addAll(rgeneral)
            data.addAll(r110)
            data.addAll(r111)
            data.addAll(rladder110)
            data.addAll(r24)
            data.addAll(r26)
        }

        fun matchEquipment(eqip: String): Boolean {
            var bodyArmor = arrayOf(
                "近战武器",
                "棍棒",
                "铁锤",
                "钉头锤",
                "权杖",
                "刀剑",
                "法杖",
                "斧头",
                "手杖",
                "锤类",
                "爪类",
                "匕首",
                "长柄武器",
                "长矛",
            )
            var armor = bodyArmor + arrayOf("武器", "弓", "十字弓")
            var shield = arrayOf("盾牌", "圣骑士盾牌")

            if (category == "武器")
                return armor.contains(eqip);
            else if (category == "近战武器")
                return bodyArmor.contains(eqip);
            else if (category == "盾牌")
                return shield.contains(eqip);
            else if (arrayOf("头盔", "盔甲").contains(category))
                return category == eqip;
            else if (bodyArmor.drop(1).contains(category))
                return arrayOf("武器", "近战武器", category).contains(eqip);
            else //"弓","十字弓"
                return arrayOf("弓", "十字弓", "武器").contains(eqip);
        }

        val resData =
            data.filter { en ->
                (category.isBlank() || en.equip.find { eq -> matchEquipment(eq) } != null) &&
                        (slot == 0 || en.slotNum == slot) &&
                        (rune == 0 || en.runes.contains(rune)) &&
                        (search.isBlank() || en.chnName.contains(search) || en.effect.contains(
                            search
                        ))
            }

        return resData
    }

    fun shiftVersion(version: String, toInner: Boolean) : String
    {
        val array = resources.getStringArray(R.array.version)
        val dic = listOf(array[0] to "", array[1] to "1.*", array[2] to "ladder", array[3] to "2.+")
        if(toInner)
            return dic.find { p -> p.first == version }?.second ?: ""
        else
            return dic.find { p -> p.second == version }?.first ?: ""
    }

    fun shiftCategory(category:String, toInner: Boolean) : String
    {
        if(toInner) {
            return if(category == "弓/十字弓") "弓"
            else category
        }
        else {
            return if(category == "弓") "弓/十字弓"
            else category
        }
    }

    fun showResults()
    {
        var search = findViewById<EditText>(R.id.search).text.toString()

        val data = generate(version,category,slot,rune,search)
        val res = findViewById<LinearLayout>(R.id.res)
        res.removeAllViews()
        if(data.isNotEmpty()) {
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
    }

    fun FilterClick(view: View) {
        val popup = PopupWindow(this)
        popup.contentView = layoutInflater.inflate(R.layout.popup_filter, null)
        popup.isOutsideTouchable = true
        popup.width = LayoutParams.MATCH_PARENT
        popup.height = LayoutParams.WRAP_CONTENT
        popup.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        popup.isFocusable = true

        var sel = popup.contentView.findViewById<WheelPicker>(R.id.version)
        var inx = sel.data.indexOf(shiftVersion(version, false))
        if(inx == -1) inx = 0
        sel.setSelectedItemPosition(inx, false)

        sel = popup.contentView.findViewById<WheelPicker>(R.id.category)
        inx = sel.data.indexOf(shiftCategory(category, false))
        if(inx == -1) inx = 0
        sel.setSelectedItemPosition(inx, false)

        sel = popup.contentView.findViewById<WheelPicker>(R.id.slot)
        inx = sel.data.indexOf(slot.toString())
        if(inx == -1) inx = 0
        sel.setSelectedItemPosition(inx, false)

        sel = popup.contentView.findViewById<WheelPicker>(R.id.rune)
        inx = sel.data.indexOfFirst {it.toString().substring(0,2).toIntOrNull() == rune}
        if(inx == -1) inx = 0
        sel.setSelectedItemPosition(inx, false)

        popup.contentView.findViewById<Button>(R.id.confirm_button).setOnClickListener(View.OnClickListener {
            _ -> run {
            var txt_version = ""
            var txt_category = ""
            var txt_slot = ""
            var txt_rune = ""

            sel = popup.contentView.findViewById<WheelPicker>(R.id.version)
            if(sel.currentItemPosition == 0) {
                txt_version = ""
                version = ""
            }
            else {
                txt_version = sel.data[sel.currentItemPosition].toString()
                val array = resources.getStringArray(R.array.version)
                val dic = mapOf(array[0] to "", array[1] to "1.*", array[2] to "ladder", array[3] to "2.+")
                version = dic[txt_version].toString()
            }

            sel = popup.contentView.findViewById<WheelPicker>(R.id.category)
            if(sel.currentItemPosition == 0) {
                txt_category = ""
                category = ""
            }
            else {
                txt_category = sel.data[sel.currentItemPosition].toString()
                category = shiftCategory(txt_category, true)
            }

            sel = popup.contentView.findViewById<WheelPicker>(R.id.slot)
            if(sel.currentItemPosition == 0) {
                txt_slot = ""
                slot = 0
            }
            else {
                txt_slot = sel.data[sel.currentItemPosition].toString()
                slot = txt_slot.toInt()
            }

            sel = popup.contentView.findViewById<WheelPicker>(R.id.rune)
            if(sel.currentItemPosition == 0) {
                txt_rune = ""
                rune = 0
            }
            else {
                txt_rune = sel.data[sel.currentItemPosition].toString()
                rune = txt_rune.substring(0, 2).toInt()
            }

            var text = ""
            if (txt_version.isNotBlank()) text += txt_version
            if (txt_slot.isNotBlank()) text += " " + txt_slot + "孔"
            if (txt_category.isNotBlank()) text += txt_category
            if (txt_rune.isNotBlank()) text += " " + txt_rune.replace(" ", "#")
            findViewById<TextView>(R.id.filters).text = text
            popup.dismiss()
            showResults()
            }
        })
        popup.contentView.findViewById<Button>(R.id.cancel_button).setOnClickListener(View.OnClickListener { _ -> popup.dismiss() })
        popup.showAtLocation(findViewById(R.id.main),Gravity.TOP,0,0)
    }
}