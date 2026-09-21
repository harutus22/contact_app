package com.example.contactapp.data_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Organization(
    val company: String?,
    val title: String?,
    val department: String?
): Parcelable {

    override fun toString(): String {
        return (if (!company.isNullOrEmpty()) company else "") +
                (if (!title.isNullOrEmpty()) " , $title" else "") +
                if (!department.isNullOrEmpty()) " , $department" else ""
    }
}