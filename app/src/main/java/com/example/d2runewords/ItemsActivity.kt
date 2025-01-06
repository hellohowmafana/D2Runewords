package com.example.d2runewords

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
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
import kotlin.math.min

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

        if(intent.hasExtra(getString(R.string.trans_class)))
            generateViews(intent.getStringExtra(getString(R.string.trans_class)),
                intent.getBooleanExtra(getString(R.string.trans_show_details), true))
        else if(intent.hasExtra(getString(R.string.trans_itemgroup_class)))
            generateViews(intent.getStringExtra(getString(R.string.trans_itemgroup_class)),
                intent.getStringExtra(getString(R.string.trans_itemgroup_name)))
        else if(intent.hasExtra(getString(R.string.trans_runeword)))
            generateViews(intent.getStringExtra(getString(R.string.trans_runeword)))
    }

    private val itemPaddingHor = DimensionConverter.dp2px(14)
    private val itemPaddingVer = DimensionConverter.dp2px(8)
    private val itemImageWidth = DimensionConverter.dp2px(80)
    private val itemImageHeight = DimensionConverter.dp2px(140)
    private val itemImageMarginRight = DimensionConverter.dp2px(10)
    private val itemTextSize = 12f
    private val itemTextPaddingHor = DimensionConverter.dp2px(2)
    private val itemTextPaddingVer = DimensionConverter.dp2px(1)

    private val itemGrpTextMarginLeft = DimensionConverter.dp2px(6)

    @SuppressLint("SetTextI18n")
    private fun generateItemDetails(datum: Item) : ViewGroup {
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
            setImageResource(resources.getIdentifier(datum.getImgResStr(), "drawable", packageName))
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
            text = datum.cnName
            setPadding(itemTextPaddingVer, itemTextPaddingVer, itemTextPaddingHor, itemTextPaddingVer)
            setTypeface(null, Typeface.BOLD)
        }

        innerLayout.addView(nameView)

        val className = datum.Class?:datum.cnClass
        if (!arrayOf("标枪", "法师天球", "腰带", "手套", "鞋子").contains(className)) {
            innerLayout.setOnClickListener {
                val intent = Intent(this, RuneWordsActivity::class.java).apply {
                    putExtra(
                        getString(R.string.trans_runeword_class),
                        className
                    )
                    putExtra(getString(R.string.trans_runeword_socket), datum.MaxSocket)
                }
                startActivity(intent)
            }
        }

        val props = mutableListOf<Pair<String, Any>>().apply {
            if (datum.OneHandDmg != null) add(getString(R.string.pp_onehanddamage) to datum.OneHandDmg)
            if (datum.TwoHandDmg != null) add(getString(R.string.pp_twohanddamage) to datum.TwoHandDmg)
            if (datum.ThrowingDmg != null) add(getString(R.string.pp_throwingdmg) to datum.ThrowingDmg)
            if (datum.MaxStack != null) add(getString(R.string.pp_maxstack) to datum.MaxStack)
            if (datum.Defence != null) add(getString(R.string.pp_defence) to datum.Defence)
            if (datum.Block != null) add(getString(R.string.pp_block) to datum.Block)
            if (datum.Slot != null) add(getString(R.string.pp_slot) to datum.Slot)
            if (datum.Range != null) add(getString(R.string.pp_range) to datum.Range)
            if (datum.Speed != null) add(getString(R.string.pp_speed) to datum.Speed)
            if (datum.StrReq != null) add(getString(R.string.pp_strrequired) to datum.StrReq)
            if (datum.DexReq != null) add(getString(R.string.pp_dexrequired) to datum.DexReq)
            if (datum.Durability != null) add(getString(R.string.pp_durability) to datum.Durability)
            if (datum.CLevel != null) add(getString(R.string.pp_reqlevel) to datum.CLevel)
            add(getString(R.string.pp_qlevel) to datum.QLevel)
            if (datum.MaxSocket != null) add(getString(R.string.pp_maxsockets) to datum.MaxSocket)
            if (datum.AssassinKickingDamage != null) add(getString(R.string.pp_assassinkickingdamage) to datum.AssassinKickingDamage)
            if (datum.SmiteDmg != null) add(getString(R.string.pp_smitedamage) to datum.SmiteDmg)
        }

        props.forEach{ prop ->
            val secondText : String
            if(prop.second is Array<*>)
            {
                val minmax = prop.second as Array<Int>
                if(minmax[0] == minmax[1])
                    secondText = minmax[0].toString()
                else {
                    val ave = minmax.average()
                    secondText =
                        "${minmax[0]}-${minmax[1]}(${if (ave % 1 == 0.0) ave.toInt() else ave})"
                }
            }
            else secondText = prop.second.toString()
            val ppText = TextView(this).apply {
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                ).apply { setPadding(itemTextPaddingHor,itemTextPaddingVer, itemTextPaddingHor,itemTextPaddingVer) }
                textSize = itemTextSize
            }
            ppText.text = "${prop.first}: ${secondText}"
            innerLayout.addView(ppText)
        }

        parentLayout.addView(imageView)
        parentLayout.addView(innerLayout)
        return parentLayout
    }

    @SuppressLint("SetTextI18n")
    private fun generateGroupedItem(data: Array<Item>) : ViewGroup {
        val img = ImageView(this)
        img.layoutParams = ViewGroup.LayoutParams(itemImageWidth, itemImageHeight)
        img.setImageResource(resources.getIdentifier(data[0].getImgResStr(), "drawable", packageName))

        val layout = TableLayout(this)

        data.forEach {
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

        itemLayout.setOnClickListener{
            val intent = Intent(this, ItemsActivity::class.java).apply {
                putExtra(getString(R.string.trans_itemgroup_class), data[0].cnClass)
                putExtra(getString(R.string.trans_itemgroup_name), data[0].cnName)
            }
            startActivity(intent)
        }

        return itemLayout
    }

    @SuppressLint("SetTextI18n")
    private fun generateViews(className: String?, itemName: String?) {
        findViewById<TextView>(R.id.class_name).text = "$className($itemName)"

        val data = items.filter { it.cnClass == className }
        val ix = data.indexOfFirst { it.cnName == itemName }
        val c = data.size / 3
        val datum = if(className == getString(R.string.class_circlets))
            arrayOf(data[ix]) else
            arrayOf(data[ix], data[ix + c], data[ix + c * 2])

        datum.forEach {
            val itemLayout = generateItemDetails(it)
            findViewById<LinearLayout>(R.id.items).addView(itemLayout)
        }
    }

    private fun generateViews(className : String?, showDetails: Boolean)
    {
        findViewById<TextView>(R.id.class_name).text = className

        val data = items.filter { it.cnClass == className }

        if(showDetails) {
            data.forEach {
                val itemLayout = generateItemDetails(it)
                findViewById<LinearLayout>(R.id.items).addView(itemLayout)
            }
        }
        else
        {
            val grouped : MutableList<Array<Item>> =  mutableListOf()
            if(className == getString(R.string.class_circlets)){
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

            grouped.forEach {
                val itemLayout = generateGroupedItem(it)
                findViewById<LinearLayout>(R.id.items).addView(itemLayout)
            }
        }
    }

    private fun generateViews(runeword : String?){
        val rw = rgeneral.firstOrNull{it.chnName == runeword}?:
        r110.firstOrNull{it.chnName == runeword}?:
        r111.firstOrNull{it.chnName == runeword}?:
        rladder110.firstOrNull{it.chnName == runeword}?:
        r24.firstOrNull{it.chnName == runeword}?:
        r26.firstOrNull{it.chnName == runeword}

        if (rw != null) {
            findViewById<TextView>(R.id.class_name).text = rw.chnName

            val data = items.filter { rw.matchEquipment(it.Class?:it.cnClass) && if(it.MaxSocket != null) rw.slotNum <= it.MaxSocket else false }
            data.forEach { val itemLayout = generateItemDetails(it)
                    findViewById<LinearLayout>(R.id.items).addView(itemLayout)
            }
        }
    }
}