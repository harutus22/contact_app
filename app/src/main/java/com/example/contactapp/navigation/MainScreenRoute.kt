package com.example.contactapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.example.contactapp.screens.MainScreen
import com.example.contactapp.view_models.ContactsViewModel

@Composable
fun MainScreenRoute(
    viewModel: ContactsViewModel,
    backstack: NavBackStack<NavKey>
) {
    val state by viewModel.uiState.observeAsState()
    MainScreen(
        contactsUiState = state!!,
        onItemChosen = { contact ->
            backstack.add(Route.DetailedScreenRoute(contact))
        }
    )
}