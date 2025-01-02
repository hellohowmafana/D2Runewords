package com.example.d2runewords

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.setPadding

class ItemsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_items)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        generateViews(intent.getStringExtra(getString(R.string.trans_class)),
            intent.getBooleanExtra(getString(R.string.trans_showDetails), true))
    }


    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun generateViews(title : String?, showDetails: Boolean)
    {
        findViewById<TextView>(R.id.class_name).text = title

        val data = items.filter { it.cnClass == title }

        val itemPaddingHor = DimensionConverter.dp2px(14)
        val itemPaddingVer = DimensionConverter.dp2px(8)
        val itemImageWidth = DimensionConverter.dp2px(80)
        val itemImageHeight = DimensionConverter.dp2px(140)
        val itemImageMarginRight = DimensionConverter.dp2px(10)
        val itemTextSize = 12f
        val itemTextPaddingHor = DimensionConverter.dp2px(2)
        val itemTextPaddingVer = DimensionConverter.dp2px(1)

        val itemGrpTextMarginLeft = DimensionConverter.dp2px(6)

        if(showDetails) {
            data.forEach {
                val parentLayout = LinearLayout(this).apply {
                    layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    ).apply { setPadding(itemPaddingHor,itemPaddingVer,itemPaddingHor,itemPaddingVer) }
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                }

                val imageView = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        itemImageWidth, itemImageHeight
                    ).apply{ rightMargin = itemImageMarginRight}
                    setImageResource(resources.getIdentifier(it.getImgResStr(), "drawable", packageName))
                }

                val innerLayout = LinearLayout(this).apply {
                    layoutParams = LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                    )
                    orientation = LinearLayout.VERTICAL
                }

                val nameView = TextView(this).apply {
                    textSize = itemTextSize
                    text = it.cnName
                    setPadding(itemTextPaddingVer, itemTextPaddingVer, itemTextPaddingHor, itemTextPaddingVer)
                    setTypeface(null, Typeface.BOLD)
                }

                innerLayout.addView(nameView)

                val props = mutableListOf<Pair<String, Any>>().apply {
                    if (it.OneHandDmg != null) add(getString(R.string.pp_onehanddamage) to it.OneHandDmg)
                    if (it.TwoHandDmg != null) add(getString(R.string.pp_twohanddamage) to it.TwoHandDmg)
                    if (it.ThrowingDmg != null) add(getString(R.string.pp_throwingdmg) to it.ThrowingDmg)
                    if (it.MaxStack != null) add(getString(R.string.pp_maxstack) to it.MaxStack)
                    if (it.Defence != null) add(getString(R.string.pp_defence) to it.Defence)
                    if (it.Block != null) add(getString(R.string.pp_block) to it.Block)
                    if (it.Slot != null) add(getString(R.string.pp_slot) to it.Slot)
                    if (it.Range != null) add(getString(R.string.pp_range) to it.Range)
                    if (it.Speed != null) add(getString(R.string.pp_speed) to it.Speed)
                    if (it.StrReq != null) add(getString(R.string.pp_strrequired) to it.StrReq)
                    if (it.DexReq != null) add(getString(R.string.pp_dexrequired) to it.DexReq)
                    if (it.Durability != null) add(getString(R.string.pp_durability) to it.Durability)
                    if (it.CLevel != null) add(getString(R.string.pp_reqlevel) to it.CLevel)
                    add(getString(R.string.pp_qlevel) to it.QLevel)
                    if (it.MaxSocket != null) add(getString(R.string.pp_maxsockets) to it.MaxSocket)
                    if (it.AssassinKickingDamage != null) add(getString(R.string.pp_assassinkickingdamage) to it.AssassinKickingDamage)
                    if (it.SmiteDmg != null) add(getString(R.string.pp_smitedamage) to it.SmiteDmg)
                }

                props.forEach{
                    val secondText : String
                    if(it.second is Array<*>)
                    {
                        val minmax = it.second as Array<Int>
                        val ave = minmax.average()
                        secondText = "${minmax[0]}-${minmax[1]}(${if(ave % 1 == 0.0) ave.toInt() else ave})"
                    }
                    else secondText = it.second.toString()
                    val ppText = TextView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        ).apply { setPadding(itemTextPaddingHor,itemTextPaddingVer, itemTextPaddingHor,itemTextPaddingVer) }
                        textSize = itemTextSize
                    }
                    ppText.text = "${it.first}: ${secondText}"
                    innerLayout.addView(ppText)
                }

                parentLayout.addView(imageView)
                parentLayout.addView(innerLayout)
                findViewById<LinearLayout>(R.id.items).addView(parentLayout)
            }
        }
        else
        {
            val grouped : MutableList<Array<Item>> =  mutableListOf()
            if(title == getString(R.string.class_circlets)){
                data.forEach{
                    grouped.add(arrayOf(it))
                }
            }
            else
            {
                val n = data.size / 3
                for (i in 0 until n) {
                    grouped.add(arrayOf(data[i], data[i + n], data[i + 2 * n]))
                }
            }

            grouped.forEach { group ->
                run {
                    try {
                        val img = ImageView(this)
                        img.layoutParams = ViewGroup.LayoutParams(itemImageWidth, itemImageHeight)
                        img.setImageResource(resources.getIdentifier(group[0].getImgResStr(), "drawable", packageName))

                        val layout = TableLayout(this)

                        group.forEach {
                            run {
                                val row = TableRow(this)

                                val lp = TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                                    .apply { leftMargin = itemGrpTextMarginLeft }

                                var textView = TextView(this).apply {
                                    text = it.cnName
                                    setTypeface(null, Typeface.BOLD)
                                    layoutParams = lp
                                }
                                row.addView(textView)

                                textView = TextView(this).apply {
                                    if (it.MaxSocket != null)
                                        text = "${it.MaxSocket}孔"
                                    layoutParams = lp
                                }
                                row.addView(textView)

                                textView = TextView(this).apply {
                                    if (it.CLevel != null)
                                        text = "${getString(R.string.pp_reqlevel)}:${it.CLevel}"
                                    layoutParams = lp
                                }
                                row.addView(textView)

                                layout.addView(row)
                            }
                        }

                        val itemLayout = LinearLayout(this).apply {
                            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                .apply { setPadding(itemPaddingHor, itemPaddingVer, itemPaddingHor, itemPaddingVer) }
                            orientation = LinearLayout.HORIZONTAL
                            gravity = Gravity.CENTER_VERTICAL
                            addView(img)
                            addView(layout)
                        }

                        findViewById<LinearLayout>(R.id.items).addView(itemLayout)
                    } catch (_: Exception) {
                    }
                }
            }
        }
    }
}