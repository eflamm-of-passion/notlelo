package io.eflamm.notlelo.views

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import io.eflamm.notlelo.R

class HeaderViewOld: ConstraintLayout {

    private lateinit var title: String

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context,
                attrs: AttributeSet,
                defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.header_view, this)

        context.theme.obtainStyledAttributes(
            attrs, R.styleable.HeaderView, 0, 0).apply {
            try {
                title = getString(R.styleable.HeaderView_titleText) ?: "Title"
                val titleTextView = findViewById<TextView>(R.id.headerViewTitle)
                titleTextView.text = title
            } finally {
                recycle()
            }
        }

        val button = findViewById<Button>(R.id.headerViewBackButton)
        button.setOnClickListener { onClickBackButton() }
    }

    private fun onClickBackButton () {
        val activity = context as Activity
        activity.finish()
    }

}