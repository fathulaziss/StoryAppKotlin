package com.example.storyappkotlin.ui.component

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.example.storyappkotlin.R

class EmailEditText : AppCompatEditText {
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
                0,0
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
                validateInput()
            } else {
                if (isValidInput(text.toString())) {
                    updateBorderDrawable(defaultBorderColor)
                } else {
                    updateBorderDrawable(errorBorderColor)
                }
            }
        }

        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInput()
            }

            override fun afterTextChanged(s: Editable?) {
                // do nothing
            }
        })
    }

    private fun setPrefixIcon(drawable: Drawable) {
        setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null)
        compoundDrawablePadding = 16
    }

    private fun validateInput() {
        val text = text.toString().trim()
        when {
            text.isEmpty() -> {
                updateBorderDrawable(errorBorderColor)
                error = "Email can't be Empty"
            }
            isValidInput(text) -> {
                updateBorderDrawable(focusedBorderColor)
                error = null
            }
            else -> {
                updateBorderDrawable(errorBorderColor)
                error = "Email not valid"
            }
        }
    }

    private fun isValidInput(email: String): Boolean {
        val isValid = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return isValid
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