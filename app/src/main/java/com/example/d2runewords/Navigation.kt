package com.example.d2runewords

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

class Navigation : LinearLayout {

    constructor(context: Context?, attrset: AttributeSet?) : super(context, attrset) {

        LayoutInflater.from(context).inflate(R.layout.layout_navigation, this)
        AddOnClickListeners()
        val attrs = context?.obtainStyledAttributes(attrset, R.styleable.Navigation)
        if(attrs != null) {
                val id = when (attrs.getString(R.styleable.Navigation_currentActivity)) {
                resources.getString(R.string.rune) -> R.id.runes
                resources.getString(R.string.runeword) -> R.id.runewords
                resources.getString(R.string.item) -> R.id.items
                else -> null
            }
            if (id != null) {
                val currentView = findViewById<TextView>(id)
                currentView.setTextColor(ContextCompat.getColor(context,R.color.palegold))
                currentView.isClickable = false
            }
        }

        attrs?.recycle()
    }

    fun AddOnClickListeners()
    {

        findViewById<TextView>(R.id.runes).setOnClickListener(View.OnClickListener { _ ->
            run {
                val activity = context as? Activity
                if (activity != null) {
                    activity.intent = Intent(context, RunesActivity::class.java)
                    activity.startActivity(activity.intent)
                }
            }
        })

        findViewById<TextView>(R.id.runewords).setOnClickListener(View.OnClickListener { _ ->
            run {
                val activity = context as? Activity
                if (activity != null) {
                    activity.intent = Intent(context, RuneWordsActivity::class.java)
                    activity.startActivity(activity.intent)
                }
            }
        })

        findViewById<TextView>(R.id.items).setOnClickListener(View.OnClickListener { _ ->
            run {
                val activity = context as? Activity
                if (activity != null) {
                    activity.intent = Intent(context, ClassesActivity::class.java)
                    activity.startActivity(activity.intent)
                }
            }
        })

    }
}