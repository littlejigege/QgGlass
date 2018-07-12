package com.qgstudio.qgglass.data

enum class StateCode(val value:Int) {
    OK(200),
    PARAMETER_NULL(501),
    ACCOUNT_NOT_FOUNT(51),
    PASSWORD_ERROR(52),
    PHONE_TOO_LONG(54),
    CONTACT_NOT_FOUND(55)
}