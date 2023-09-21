package com.example.contactlist

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    // Specify our different tables
    entities = [Contact::class],
    // Tell room when there is a certain update to our database how it should migrate old data to the new data
    version = 1
)
abstract class ContactDatabase:RoomDatabase() {
    abstract val dao: ContactDao
}