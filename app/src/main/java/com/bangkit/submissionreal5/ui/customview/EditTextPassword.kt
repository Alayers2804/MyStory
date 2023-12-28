package com.bangkit.submissionreal5.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.bangkit.submissionreal5.R

class EditTextPassword: AppCompatEditText, View.OnTouchListener {

    private var textLength = 0

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init() {

        addTextChangedListener { s->
            if(s != null){
                textLength = s.length
            }
            println("message getted: ${s?.length ?: 0}")
            if(textLength < 8){
                showErrorMessage("Password Kurang dari 8 Karakter")
            } else {
                hideError()
            }
        }

    }


    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        hint = "Masukkan password anda"

        background = ContextCompat.getDrawable(context, R.drawable.custom_form)

        setTextColor(ContextCompat.getColor(context, com.google.android.material.R.color.m3_ref_palette_black))

        setHintTextColor(ContextCompat.getColor(context, R.color.md_theme_light_onBackground))
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }

    private fun showErrorMessage(errorMessage: String) {
        error = errorMessage
    }

    private fun hideError() {
        error = null
    }


    // ...
}