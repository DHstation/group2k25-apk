package com.quantiumcode.group2k25.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class PhoneFormatter(private val editText: EditText) : TextWatcher {

    private var isFormatting = false
    private var deletingHyphen = false
    private var hyphenStart = 0
    private var cursorCompanion = 0

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (isFormatting) return
        if (count == 1 && after == 0) {
            val c = s?.get(start)
            if (c == '-' || c == ')' || c == ' ' || c == '(') {
                deletingHyphen = true
                hyphenStart = start
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isFormatting) return
        isFormatting = true

        if (deletingHyphen) {
            if (hyphenStart > 0) {
                s?.delete(hyphenStart - 1, hyphenStart)
            }
            deletingHyphen = false
        }

        val digits = s.toString().replace(Regex("[^\\d]"), "")
        val formatted = formatPhone(digits)

        editText.removeTextChangedListener(this)
        editText.setText(formatted)
        editText.setSelection(formatted.length.coerceAtMost(editText.text.length))
        editText.addTextChangedListener(this)

        isFormatting = false
    }

    companion object {
        fun formatPhone(digits: String): String {
            val d = digits.take(11)
            return when {
                d.length <= 2 -> "($d"
                d.length <= 7 -> "(${d.substring(0, 2)}) ${d.substring(2)}"
                d.length <= 11 -> "(${d.substring(0, 2)}) ${d.substring(2, 7)}-${d.substring(7)}"
                else -> d
            }
        }

        fun stripPhone(formatted: String): String {
            return formatted.replace(Regex("[^\\d]"), "")
        }
    }
}
