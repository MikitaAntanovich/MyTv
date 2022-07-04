package com.horizont.mytv

import kotlinx.serialization.Serializable

@Serializable
data class User(val NUMBER: String, val MAC_FROM_LIBRARY: String, val MAC: String, val ABOUT_FROM_LIBRARY: String, val MODEL: String, val ID: String, val BRAND: String, val TYPE: String, val MANUFACTURER: String, val BOARD: String, val DISPLAY: String, val FINGERPRINT: String)