package com.example.contactlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ContactViewModel(
    // Pass our Dao in the constructor
    private val dao: ContactDao
): ViewModel() {

    private val _sortType = MutableStateFlow(SortType.FIRST_NAME)
    // always holds the current contact to hold our sort type
    private val _contacts = _sortType
            // Alt+Enter add OptIn to viewModel
        .flatMapLatest { sortType->
            when(sortType){
                SortType.FIRST_NAME->dao.getContactsOrderedByFirstName()
                SortType.LAST_NAME->dao.getContactsOrderedByLastName()
                SortType.PHONE_NUMBER->dao.getContactsOrderedByPhoneNumber()
            }
        }
            // stateIn to catch in flow
        // SharingStarted.WhileSubscribed it's only executed while there is an active subscriber to the state flow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(ContactState())
    // combine the three flows into one flow, so if there's a change in one, it'll update it in the others
    val state = combine(_state, _sortType, _contacts){ state, sortType, contacts ->
        state.copy(
            contacts = contacts,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState()) // Add the 5 seconds to avoid a bug for the UI

    fun onEvent(event: ContactEvent){
        when(event){
            // Alt+Enter to add remaining branches
            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.deleteContact(event.contact)
                }
            }
            ContactEvent.HideDialog -> {
                _state.update{it.copy(
                    isAddingContact = false
                )}
            }
            ContactEvent.SaveContact -> {
                val firstName = state.value.firstName
                val lastName = state.value.lastName
                val phoneNumber = state.value.phoneNumber

                if(firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()){
                    return
                }

                val contact = Contact(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                )
                viewModelScope.launch {
                    dao.upsertContact(contact)
                }
                // reset fields after upsert to be empty
                _state.update{it.copy(
                    isAddingContact = false,
                    firstName = "",
                    lastName = "",
                    phoneNumber = "",
                )}
            }
            is ContactEvent.SetFirstName -> {
                _state.update{it.copy(
                    firstName = event.firstName
                )}
            }
            is ContactEvent.SetLastName -> {
                _state.update{it.copy(
                    lastName = event.lastName
                )}
            }
            is ContactEvent.SetPhoneNumber -> {
                _state.update{it.copy(
                    phoneNumber = event.phoneNumber
                )}
            }
            ContactEvent.ShowDialog -> {
                _state.update{it.copy(
                    isAddingContact = true
                )}
            }
            is ContactEvent.SortContacts -> {
                _sortType.value = event.sortType
            }
        }
    }
}