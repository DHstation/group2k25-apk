package com.quantiumcode.group2k25.data.local

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    fun hasToken(): Boolean = getToken() != null

    fun getUserPhone(): String? = prefs.getString(KEY_PHONE, null)

    fun saveUserPhone(phone: String) {
        prefs.edit().putString(KEY_PHONE, phone).apply()
    }

    fun getUserType(): String? = prefs.getString(KEY_USER_TYPE, null)

    fun saveUserType(type: String) {
        prefs.edit().putString(KEY_USER_TYPE, type).apply()
    }

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    fun saveUserName(name: String?) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getCustomerId(): String? = prefs.getString(KEY_CUSTOMER_ID, null)

    fun saveCustomerId(id: String?) {
        prefs.edit().putString(KEY_CUSTOMER_ID, id).apply()
    }

    fun getLeadId(): String? = prefs.getString(KEY_LEAD_ID, null)

    fun saveLeadId(id: String?) {
        prefs.edit().putString(KEY_LEAD_ID, id).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "group2k25_auth"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_PHONE = "user_phone"
        private const val KEY_USER_TYPE = "user_type"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_CUSTOMER_ID = "customer_id"
        private const val KEY_LEAD_ID = "lead_id"
    }
}
