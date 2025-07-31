package com.example.storyappkotlin.ui.component

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.storyappkotlin.R

class CustomEditText : AppCompatEditText {
    private var defaultBorderColor: Int = Color.LTGRAY
    private var focusedBorderColor: Int = Color.BLUE
    private var errorBorderColor: Int = Color.RED
    private var cornerRadius: Float = 20f
    private var prefixIcon: Drawable? = null
    private var maxLines: Int = 1
    private var scrollHorizontally: Boolean = true

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        attrs?.let {
            val a: TypedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CustomEditText,
                0, 0
            )

            if (a.hasValue(R.styleable.CustomEditText_prefixIcon)) {
                prefixIcon = a.getDrawable(R.styleable.CustomEditText_prefixIcon)
            }

            defaultBorderColor = a.getColor(R.styleable.CustomEditText_borderColor, Color.LTGRAY)
            focusedBorderColor = a.getColor(R.styleable.CustomEditText_focusBorderColor, Color.BLUE)
            errorBorderColor = a.getColor(R.styleable.CustomEditText_errorBorderColor, Color.RED)
            cornerRadius = a.getDimension(R.styleable.CustomEditText_cornerRadius, 20f)
            maxLines = a.getInt(R.styleable.CustomEditText_maxLines, 1)
            scrollHorizontally = a.getBoolean(R.styleable.CustomEditText_scrollHorizontally, true)

            a.recycle()
        }

        setMaxLines(maxLines)
        setHorizontallyScrolling(scrollHorizontally)
        updateBorderDrawable(defaultBorderColor)

        prefixIcon?.let {
            setPrefixIcon(it)
        }

        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                updateBorderDrawable(focusedBorderColor)
            } else {
                updateBorderDrawable(defaultBorderColor)
            }
        }
    }

    private fun setPrefixIcon(drawable: Drawable) {
        setCompoundDrawables(drawable, null, null, null)
        compoundDrawablePadding = 16
    }

    private fun updateBorderDrawable(borderColor: Int) {
        val borderDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setStroke(4, borderColor)
            setCornerRadius(cornerRadius)
            setColor(Color.TRANSPARENT)
        }

        background = borderDrawable
    }
}