package com.example.contactlist

data class ContactState (
    // List of our contacts (default empty)
    val contacts: List<Contact> = emptyList(),
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    // if a user is adding a new contact to show the dialogue, which will be default to false
    val isAddingContact: Boolean = false,
    // default sort type
    val sortType: SortType = SortType.FIRST_NAME
)