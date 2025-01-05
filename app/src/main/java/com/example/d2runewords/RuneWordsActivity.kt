package com.example.d2runewords

import android.annotation.SuppressLint
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.aigestudio.wheelpicker.WheelPicker

class RuneWordsActivity : AppCompatActivity() {
    var version = ""
    var category = ""
    var slot = 0
    var rune = 0
    var search = ""

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

    override fun onStart() {
        super.onStart()

        if(intent.hasExtra(getString(R.string.trans_runeword_class))) {
            apply(intent.getStringExtra(getString(R.string.trans_runeword_class)),
                intent.getIntExtra(getString(R.string.trans_runeword_socket),0))
        }
    }

    fun generate(
        version: String,
        category: String,
        slot: Int,
        rune: Int,
        search: String
    ): List<RuneWord> {
        var data = mutableListOf<RuneWord>()
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
            val mace = arrayOf("锤类","钉头锤","铁锤")
            val bodyArmor = arrayOf(
                "近战武器") + mace + arrayOf(
                "棍棒",
                "权杖",
                "刀剑",
                "法杖",
                "斧头",
                "手杖",
                "爪类",
                "匕首",
                "长柄武器",
                "长矛",
            )
            val armor = bodyArmor + arrayOf("武器", "弓", "十字弓")
            val shield = arrayOf("盾牌", "圣骑士盾牌")

            if (category == "武器")
                return armor.contains(eqip);
            else if (category == "近战武器")
                return bodyArmor.contains(eqip);
            else if (category == "盾牌")
                return shield.contains(eqip);
            else if (arrayOf("头盔", "盔甲", "圣骑士盾牌").contains(category))
                return category == eqip;
            else if (category == "锤类")
                return mace.contains(eqip)
            else if (mace.drop(1).contains(category))
                return arrayOf("锤类", category).contains(eqip)
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

    private fun shiftVersion(version: String, toInner: Boolean) : String
    {
        val array = resources.getStringArray(R.array.version)
        val dic = listOf(array[0] to "", array[1] to "1.*", array[2] to "ladder", array[3] to "2.+")
        if(toInner)
            return dic.find { p -> p.first == version }?.second ?: ""
        else
            return dic.find { p -> p.second == version }?.first ?: ""
    }

    private fun shiftCategory(category:String, toInner: Boolean) : String
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

    private fun shiftRune(rune:Int) : String
    {
        val r = runes.find { it.no == rune }
        return if (r != null) "${r.no}#${r.name}" else ""
    }

    private fun shiftRune(rune:String) : Int
    {
        return rune.substring(0, 2).toInt()
    }

    @SuppressLint("SetTextI18n")
    private fun generateView(data:List<RuneWord>)
    {
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

    private fun showFilterText()
    {
        var text = ""
        if (version.isNotBlank()) text += shiftVersion(version, false)
        if (slot != 0) text += " " + slot.toString() + "孔"
        if (category.isNotBlank()) text += shiftCategory(category, false)
        if (rune != 0) text += " " + shiftRune(rune)
        findViewById<TextView>(R.id.filters).text = text
    }

    private fun showResults()
    {
        showFilterText()
        val data = generate(version,category,slot,rune,search)
        generateView(data)
    }

    private fun apply(className: String?, socket: Int) {
        category =
            if (arrayOf("投掷类武器", "亚马逊武器").contains(className))
                items.find { it.cnClass == className }?.Class ?: ""
            else
                when (className) {
                    "剑" -> "刀剑"
                    "弓" -> "弓/十字弓"
                    "十字弓" -> "弓/十字弓"
                    "刺客爪" -> "爪类"
                    "头饰" -> "头盔"
                    "野蛮人头盔" -> "盾牌"
                    "德鲁伊头盔" -> "盾牌"
                    "圣骑士盾牌" -> "圣骑士盾牌"
                    "防腐之首" -> "盾牌"
                    else -> className
                } ?: ""

        slot = socket

        showResults()
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
                sel = popup.contentView.findViewById<WheelPicker>(R.id.version)
                version = if(sel.currentItemPosition == 0) {
                    ""
                } else {
                    shiftVersion(sel.data[sel.currentItemPosition].toString(), true)
                }

                sel = popup.contentView.findViewById<WheelPicker>(R.id.category)
                category = if(sel.currentItemPosition == 0) {
                    ""
                } else {
                    shiftCategory(sel.data[sel.currentItemPosition].toString(), true)
                }

                sel = popup.contentView.findViewById<WheelPicker>(R.id.slot)
                slot = if(sel.currentItemPosition == 0) {
                    0
                } else {
                    sel.data[sel.currentItemPosition].toString().toInt()
                }

                sel = popup.contentView.findViewById<WheelPicker>(R.id.rune)
                rune = if(sel.currentItemPosition == 0) {
                    0
                } else {
                    shiftRune(sel.data[sel.currentItemPosition].toString())
                }
                
                search = findViewById<EditText>(R.id.search).text.toString()

                popup.dismiss()
                showResults()
            }
        })
        popup.contentView.findViewById<Button>(R.id.cancel_button).setOnClickListener(View.OnClickListener { _ -> popup.dismiss() })
        popup.showAtLocation(findViewById(R.id.main),Gravity.TOP,0,0)
    }
}