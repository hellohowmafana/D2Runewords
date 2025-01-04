package com.example.d2runewords

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class ClassesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_classes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageButtons = findAllImageButtons(findViewById<ViewGroup>(R.id.classes))
        imageButtons.forEach{
            it.setOnClickListener{
                val intent = Intent(this, ItemsActivity::class.java).apply {
                    putExtra(getString(R.string.trans_class), it.contentDescription)
                    putExtra(getString(R.string.trans_show_details), false)
                }
                startActivity(intent)
            }
            it.setOnLongClickListener {
                val intent = Intent(this, ItemsActivity::class.java).apply {
                    putExtra(getString(R.string.trans_class), it.contentDescription)
                    putExtra(getString(R.string.trans_show_details), true)
                }
                startActivity(intent)
                true
            }
        }
    }

    private fun findAllImageButtons(viewGroup: ViewGroup): List<ImageButton> {
        val imageButtons = mutableListOf<ImageButton>()
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is ImageButton) {
                imageButtons.add(child)
            } else if (child is ViewGroup) {
                imageButtons.addAll(findAllImageButtons(child))
            }
        }
        return imageButtons
    }
}