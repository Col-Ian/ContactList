package com.example.contactlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    // Create the database
   val firstName: String,
   val lastName: String,
   val phoneNumber: String,
   // Primary Key for Room
   @PrimaryKey(autoGenerate = true)
   val id: Int = 0
)