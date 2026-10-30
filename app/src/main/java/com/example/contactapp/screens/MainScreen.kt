package com.example.contactapp.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.contactapp.data_models.Contact
import com.example.contactapp.data_models.ContactsUiState
import com.example.contactapp.screens.components.ContactItem

@Composable
fun MainScreen(
    contactsUiState: ContactsUiState,
    onItemChosen: (Contact) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(contactsUiState.contacts) { contact ->
            ContactItem(contact) {
                onItemChosen(contact)
            }
        }
    }
}