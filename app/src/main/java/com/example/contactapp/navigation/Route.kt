package com.example.contactapp.navigation

import androidx.navigation3.runtime.NavKey
import com.example.contactapp.data_models.Contact
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {

    @Serializable
    data object MainScreenRoute : Route, NavKey

    @Serializable
    data class DetailedScreenRoute(val contact: Contact) : Route, NavKey

}