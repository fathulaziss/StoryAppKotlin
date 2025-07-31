package com.example.storyappkotlin.ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyappkotlin.R

class PasswordEditText : AppCompatEditText {
    private var defaultBorderColor: Int = Color.LTGRAY
    private var focusedBorderColor: Int = Color.BLUE
    private var errorBorderColor: Int = Color.RED
    private var cornerRadius: Float = 20f
    private var prefixIcon: Drawable? = null
    private var eyeIconOn: Drawable? = null
    private var eyeIconOff: Drawable? = null
    private var isPasswordVisible: Boolean = false
    private var maxLines: Int = 1
    private var scrollHorizontally: Boolean = true
    private var isValid: Boolean = false

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
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

        inputType = 129
        eyeIconOff = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off)
        eyeIconOn = ContextCompat.getDrawable(context, R.drawable.ic_visibility_on)

        setMaxLines(maxLines)
        setHorizontallyScrolling(scrollHorizontally)
        updateBorderDrawable(defaultBorderColor)

        prefixIcon?.let { prefix ->
            eyeIconOff?.let { eyeOff ->
                setIcon(prefix, eyeOff)
            }
        }

        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                validateInput()
            } else {
                if (isValid) {
                    updateBorderDrawable(defaultBorderColor)
                } else {
                    updateBorderDrawable(errorBorderColor)
                }
            }
            updateEyeIconVisibility()
        }

        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                validateInput()
            }

            override fun afterTextChanged(editable: Editable?) {
                // do nothing
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { e ->
            if (e.action == MotionEvent.ACTION_UP) {
                val touchPosition = e.x.toInt()
                eyeIconOff?.let { eyeIcon ->
                    val drawableRight = width - paddingRight - eyeIcon.intrinsicWidth

                    // Check if the user touched near the eye icon (on the right side of the EditText)
                    if (touchPosition >= drawableRight) {
                        togglePasswordVisibility()
                        return true
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            isPasswordVisible = false
            eyeIconOff?.let { eyeOff ->
                setCompoundDrawablesWithIntrinsicBounds(prefixIcon, null, eyeOff, null)
            }
        } else {
            inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            isPasswordVisible = true
            eyeIconOn?.let { eyeOn ->
                setCompoundDrawablesWithIntrinsicBounds(prefixIcon, null, eyeOn, null)
            }
        }

        setSelection(text?.length ?: 0)
        invalidate()
    }

    private fun setIcon(prefixIcon: Drawable, suffixIcon: Drawable) {
        setCompoundDrawablesWithIntrinsicBounds(prefixIcon, null, suffixIcon, null)
        compoundDrawablePadding = 16
    }

    private fun validateInput() {
        val text = text.toString().trim()
        when {
            text.isEmpty() -> {
                isValid = false
                updateBorderDrawable(errorBorderColor)
                error = "Password can't be Empty"
            }
            isValidInput(text) -> {
                isValid = false
                updateBorderDrawable(errorBorderColor)
                error = "Password can't be less than 8 character"
            }
            else -> {
                isValid = true
                updateBorderDrawable(focusedBorderColor)
                error = null
            }
        }
    }

    private fun isValidInput(password: String): Boolean {
        return password.length < 8
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

    private fun updateEyeIconVisibility() {
        if (isPasswordVisible) {
            // Set the eye icon to "on" when the password is visible
            eyeIconOn?.let { eyeOn ->
                setCompoundDrawablesWithIntrinsicBounds(prefixIcon, null, eyeOn, null)
            }
        } else {
            // Set the eye icon to "off" when the password is hidden
            eyeIconOff?.let { eyeOff ->
                setCompoundDrawablesWithIntrinsicBounds(prefixIcon, null, eyeOff, null)
            }
        }
    }
}