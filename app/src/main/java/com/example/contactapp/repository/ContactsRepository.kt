package com.example.contactapp.repository

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.contactapp.data_models.Contact
import com.example.contactapp.data_models.Organization
import java.util.concurrent.Executors

class ContactsRepository(
    context: Context,
) {

    private val contentResolver: ContentResolver =
        context.applicationContext.contentResolver

    private val executor = Executors.newSingleThreadExecutor()

    private val _contacts = object : MutableLiveData<List<Contact>>() {
        private var contentObserver: ContentObserver? = null

        override fun onActive() {
            super.onActive()
            if (contentObserver == null) {
                contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
                    override fun onChange(selfChange: Boolean) {
                        fetchAndEmitContacts()
                    }
                }
                contentResolver.registerContentObserver(
                    ContactsContract.Contacts.CONTENT_URI,
                    true,
                    contentObserver!!,
                )
            }
            fetchAndEmitContacts()
        }

        override fun onInactive() {
            super.onInactive()
            contentObserver?.let { observer ->
                contentResolver.unregisterContentObserver(observer)
                contentObserver = null
            }
        }
    }

    val contactsLiveData: LiveData<List<Contact>> = _contacts

    fun getContacts(): LiveData<List<Contact>> {
        fetchAndEmitContacts()
        return contactsLiveData
    }

    private fun fetchAndEmitContacts() {
        executor.execute {
            val contactsMap = mutableMapOf<Long, ContactBuilder>()

            loadNames(contactsMap)
            loadPhotos(contactsMap)
            loadPhones(contactsMap)
            loadEmails(contactsMap)
            loadAddresses(contactsMap)
            loadOrganizations(contactsMap)
            loadBirthdays(contactsMap)

            val result = contactsMap.values
                .map { it.build() }
                .sortedBy { it.name?.lowercase() }

            _contacts.postValue(result)
        }
    }

    private fun loadNames(
        contacts: MutableMap<Long, ContactBuilder>,
    ) {
        val projection = arrayOf(
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME
        )

        val selection =
            "${ContactsContract.Data.MIMETYPE} = ?"

        val selectionArgs = arrayOf(
            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
        )

        contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->

            val idIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.Data.CONTACT_ID
            )

            val nameIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex)

                contacts.getOrPut(id) {
                    ContactBuilder(id)
                }.name = name
            }
        }
    }

    private fun loadPhotos(
        contacts: MutableMap<Long, ContactBuilder>
    ) {
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        )

        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->

            val idIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.Contacts._ID
            )

            val photoIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
            )

            while (cursor.moveToNext()) {
                val contactId = cursor.getLong(idIndex)

                val photoUri = cursor.getString(photoIndex)

                contacts[contactId]?.photoUri = photoUri
            }
        }
    }

    private fun loadPhones(
        contacts: MutableMap<Long, ContactBuilder>
    ) {
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE
        )

        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->

            val idIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            )

            val numberIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            val typeIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.TYPE
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val number = cursor.getString(numberIndex)
                val type = cursor.getInt(typeIndex)

                val builder = contacts.getOrPut(id) {
                    ContactBuilder(id)
                }

                when (type) {
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> {
                        builder.mobilePhones += number
                    }

                    ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> {
                        builder.homePhones += number
                    }

                    ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> {
                        builder.workPhones += number
                    }
                }
            }
        }
    }

    private fun loadEmails(
        contacts: MutableMap<Long, ContactBuilder>
    ) {
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.TYPE
        )

        contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->

            val idIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Email.CONTACT_ID
            )

            val addressIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Email.ADDRESS
            )

            val typeIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Email.TYPE
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val email = cursor.getString(addressIndex)
                val type = cursor.getInt(typeIndex)

                val builder = contacts.getOrPut(id) {
                    ContactBuilder(id)
                }

                when (type) {
                    ContactsContract.CommonDataKinds.Email.TYPE_HOME -> {
                        builder.homeEmails += email
                    }

                    ContactsContract.CommonDataKinds.Email.TYPE_WORK -> {
                        builder.workEmails += email
                    }
                }
            }
        }
    }

    private fun loadAddresses(
        contacts: MutableMap<Long, ContactBuilder>
    ) {
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID,
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
            ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
        )

        contentResolver.query(
            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->

            val idIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID
            )

            val typeIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE
            )

            val formattedIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val type = cursor.getInt(typeIndex)

                val address = cursor.getString(formattedIndex)

                val builder = contacts.getOrPut(id) {
                    ContactBuilder(id)
                }

                when (type) {
                    ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME -> {
                        builder.homeAddresses += address
                    }

                    ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK -> {
                        builder.workAddresses += address
                    }
                }
            }
        }
    }

    private fun loadOrganizations(
        contacts: MutableMap<Long, ContactBuilder>
    ) {
        val projection = arrayOf(
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.CommonDataKinds.Organization.COMPANY,
            ContactsContract.CommonDataKinds.Organization.TITLE,
            ContactsContract.CommonDataKinds.Organization.DEPARTMENT
        )

        val selection =
            "${ContactsContract.Data.MIMETYPE} = ?"

        val selectionArgs = arrayOf(
            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
        )

        contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->

            val idIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.Data.CONTACT_ID
            )

            val companyIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Organization.COMPANY
            )

            val titleIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Organization.TITLE
            )

            val departmentIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Organization.DEPARTMENT
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)

                val organization = Organization(
                    company = cursor.getString(companyIndex),
                    title = cursor.getString(titleIndex),
                    department = cursor.getString(departmentIndex)
                )

                contacts.getOrPut(id) {
                    ContactBuilder(id)
                }.organization = organization
            }
        }
    }

    private fun loadBirthdays(
        contacts: MutableMap<Long, ContactBuilder>
    ) {
        val projection = arrayOf(
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.CommonDataKinds.Event.START_DATE,
            ContactsContract.CommonDataKinds.Event.TYPE
        )

        val selection = """
            ${ContactsContract.Data.MIMETYPE} = ?
            AND ${ContactsContract.CommonDataKinds.Event.TYPE} = ?
        """.trimIndent()

        val selectionArgs = arrayOf(
            ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
            ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY.toString()
        )

        contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->

            val idIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.Data.CONTACT_ID
            )

            val birthdayIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Event.START_DATE
            )

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val birthday = cursor.getString(birthdayIndex)

                contacts.getOrPut(id) {
                    ContactBuilder(id)
                }.birthday = birthday
            }
        }
    }

    private class ContactBuilder(
        val id: Long
    ) {
        var name: String? = null
        var photoUri: String? = null

        val mobilePhones = mutableListOf<String>()
        val homePhones = mutableListOf<String>()
        val workPhones = mutableListOf<String>()

        val homeEmails = mutableListOf<String>()
        val workEmails = mutableListOf<String>()

        val homeAddresses = mutableListOf<String>()
        val workAddresses = mutableListOf<String>()

        var organization: Organization? = null
        var birthday: String? = null


        fun build(): Contact {
            return Contact(
                id = id,
                name = name,
                photoUri = photoUri,
                mobilePhones = mobilePhones.toList(),
                homePhones = homePhones.toList(),
                workPhones = workPhones.toList(),
                homeEmails = homeEmails.toList(),
                workEmails = workEmails.toList(),
                homeAddresses = homeAddresses.toList(),
                workAddresses = workAddresses.toList(),
                organization = organization,
                birthday = birthday
            )
        }
    }
}