package com.example.contactlist

// Contain different user actions
sealed interface ContactEvent{
    // save the contact
    object SaveContact: ContactEvent
    // set the first name, etc.....
    data class SetFirstName(val firstName:String): ContactEvent
    data class SetLastName(val lastName:String): ContactEvent
    data class SetPhoneNumber(val phoneNumber:String): ContactEvent
    // show/hide dialog
    object ShowDialog: ContactEvent
    object HideDialog: ContactEvent
    // Based on our Enum class SortType
    data class SortContacts(val sortType: SortType): ContactEvent
    // For when we click out trash can
    data class DeleteContact(val contact: Contact): ContactEvent
}