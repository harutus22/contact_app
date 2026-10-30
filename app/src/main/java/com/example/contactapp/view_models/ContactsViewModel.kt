package com.example.contactapp.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactapp.repository.ContactsRepository
import com.example.contactapp.data_models.Contact
import com.example.contactapp.data_models.ContactsUiState
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val repository: ContactsRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData(ContactsUiState())
    val uiState: LiveData<ContactsUiState> =
        _uiState

    private var contactsLiveData: LiveData<List<Contact>>? = null

    private val contactsObserver = Observer<List<Contact>> { contacts ->
        _uiState.value = _uiState.value?.copy(
            isLoading = false,
            contacts = contacts,
            error = null,
        )
    }

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(
                isLoading = true,
                error = null,
            )

            try {
                contactsLiveData?.removeObserver(contactsObserver)
                val liveData = repository.getContacts()
                contactsLiveData = liveData
                liveData.observeForever(contactsObserver)
            } catch (e: Exception) {
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    error = e.message,
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        contactsLiveData?.removeObserver(contactsObserver)
    }
}