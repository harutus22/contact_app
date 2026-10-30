package com.example.contactapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.contactapp.data_models.Contact
import com.example.contactapp.screens.DetailedScreen

@Composable
fun DetailedScreenRoute(
    contact: Contact,
    backStack: NavBackStack<NavKey>
) {
    DetailedScreen(
        contact
    )
}