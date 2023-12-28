package com.bangkit.submissionreal5.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.bangkit.submissionreal5.R

class CustomButton: AppCompatButton {

    private lateinit var enabledBackgroundColor : Drawable
    private lateinit var disabledBackgroundColor: Drawable
    private var txtColor: Int = 0

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context,attrs,defStyleAttr){
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if(isEnabled) enabledBackgroundColor else disabledBackgroundColor

        setTextColor(txtColor)
        textSize = 12f
        gravity = Gravity.CENTER
    }

    private fun init(){
        txtColor = ContextCompat.getColor(context, R.color.md_theme_light_background)
        enabledBackgroundColor = ContextCompat.getDrawable(context, R.drawable.custom_button) as Drawable
        disabledBackgroundColor = ContextCompat.getDrawable(context, R.drawable.custom_button_disabled) as Drawable
    }

}