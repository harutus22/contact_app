package com.example.contactapp.data_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: Long,
    val name: String?,
    val photoUri: String?,
    val mobilePhones: List<String>,
    val homePhones: List<String>,
    val workPhones: List<String>,
    val homeEmails: List<String>,
    val workEmails: List<String>,
    val homeAddresses: List<String>,
    val workAddresses: List<String>,
    val organization: Organization?,
    val birthday: String?
): Parcelable