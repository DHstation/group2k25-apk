package com.quantiumcode.group2k25.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.Locale

class CurrencyFormatter(private val editText: EditText) : TextWatcher {

    private var isFormatting = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isFormatting) return
        isFormatting = true

        val digits = s.toString().replace(Regex("[^\\d]"), "")
        if (digits.isEmpty()) {
            editText.setText("")
            isFormatting = false
            return
        }

        val value = digits.toLongOrNull() ?: 0
        val formatted = formatCurrency(value / 100.0)

        editText.removeTextChangedListener(this)
        editText.setText(formatted)
        editText.setSelection(formatted.length)
        editText.addTextChangedListener(this)

        isFormatting = false
    }

    companion object {
        private val brLocale = Locale("pt", "BR")

        fun formatCurrency(value: Double): String {
            val format = NumberFormat.getCurrencyInstance(brLocale)
            return format.format(value)
        }

        fun parseCurrency(formatted: String): Double {
            val digits = formatted.replace(Regex("[^\\d]"), "")
            return if (digits.isEmpty()) 0.0 else digits.toLong() / 100.0
        }
    }
}
