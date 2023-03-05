package vn.tapbi.message.data.repository

import android.content.ContentProviderOperation
import android.content.ContentUris
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.ContactsContract.RawContacts
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import timber.log.Timber
import vn.tapbi.message.common.Constant
import vn.tapbi.message.data.local.SharedPreferenceHelper
import vn.tapbi.message.data.local.db.ContactDao
import vn.tapbi.message.data.local.db.ConversationDao
import vn.tapbi.message.data.local.db.MessageDao
import vn.tapbi.message.data.model.Contact
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


class ContactRepository @Inject constructor(
    private val sharedPreferenceHelper: SharedPreferenceHelper,
    private val contactDao: ContactDao,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {

    fun insertAllContactsToRoom(context: Context) {
        if (sharedPreferenceHelper.getBoolean(Constant.LOAD_ALL_CONTACT_FIRST_TIME, true)) {
            contactDao.insertAllContacts(getAllContactsFromContentProvider(context, null, null))
            sharedPreferenceHelper.storeBoolean(Constant.LOAD_ALL_CONTACT_FIRST_TIME, false)
        }
    }

    fun getAllContactsLiveData(): LiveData<List<Contact>> {
        return contactDao.getAllContactsLiveData()
    }

    fun searchForContacts(queryString: String): List<Contact> {
        return if(queryString!="") {
            contactDao.searchForContactsLiveData(queryString)
        }else{
            contactDao.getAllContacts()
        }
    }

    fun updateContactName(contactId: Int, contactName: String) {
        contactDao.updateContactName(contactId, contactName)
    }

    fun updateContactPhoneNumber(contactId: Int, phoneNumber: String) {
        contactDao.updateContactPhoneNumber(contactId, phoneNumber)
    }

    fun updateContactPhoto(contactId: Int, photoString: String) {
        contactDao.updateContactPhoto(contactId, photoString)
    }

    fun updateContactChangeFromContentProviderToRoom(context: Context) {
        val contactLastUpdateTime = try {
            contactDao.getContactLatestUpdate().contactLastUpdateTime
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val contacts = getAllContactsFromContentProvider(
            context,
            "contact_last_updated_timestamp > ?",
            arrayOf(contactLastUpdateTime.toString())
        )
        contactDao.insertAllContacts(contacts)
        for (contact in contacts) {
            conversationDao.updateConversation(
                contactId = contact.contactId,
                photoLink = contact.photoUri,
                contactName = contact.name,
                address = contact.phoneNumber
            )
            messageDao.updateMessage(
                contactId = contact.contactId,
                photoLink = contact.photoUri,
                contactName = contact.name,
                address = contact.phoneNumber
            )
            conversationDao.updateConversation2(
                contactId = contact.contactId,
                address = contact.phoneNumber
            )
            messageDao.updateMessage2(
                contactId = contact.contactId,
                address = contact.phoneNumber
            )
        }
    }

    fun getContactLiveData(id: Int): LiveData<Contact> {
        return contactDao.getContactLiveData(id)
    }

    fun getContactsCount(): Int {
        return contactDao.getContactsCount()
    }

    fun deleteContacts(context: Context, numberOfContactsToDelete: Int) {
        for (contactId in getDeletedContactIdFromContentProvider(
            context,
            numberOfContactsToDelete
        )) {
            val contact = contactDao.getContactById(contactId)
            conversationDao.updateConversation(
                contactId = -1,
                photoLink = null,
                contactName = contact.phoneNumber,
                address = contact.phoneNumber
            )
            messageDao.updateMessage(
                contactId = -1,
                photoLink = null,
                contactName = contact.phoneNumber,
                address = contact.phoneNumber
            )
            contactDao.deleteContacts(contactId)
        }
    }

    fun getContactByAddress(address: String): Contact?{
        return contactDao.getContactByAddress(address)
    }

    fun isPhoneNumberExist(contactId: Int, phoneNumber: String): Boolean {
        return contactDao.isPhoneNumberExist(contactId, phoneNumber) != 0
    }

    fun updateName(contactId: Int, rawContactId: Int, name: String, context: Context) {
        val ops = ArrayList<ContentProviderOperation>()
        ops.add(
            ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(
                    "(${ContactsContract.Data.CONTACT_ID} = ? OR ${ContactsContract.Data.RAW_CONTACT_ID} = ?) AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(
                        contactId.toString(),
                        rawContactId.toString(),
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                    )
                )
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, "")
                .withValue(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, "")
                .build()
        )

        // Update
        try {
            context.contentResolver
                .applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun updatePhoneNumber(contactId: Int, rawContactId: Int, phone: String, context: Context) {
        val ops = ArrayList<ContentProviderOperation>()
        ops.add(
            ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(
                    "(${ContactsContract.Data.CONTACT_ID} = ? OR ${ContactsContract.Data.RAW_CONTACT_ID} = ?) AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(
                        contactId.toString(),
                        rawContactId.toString(),
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                )
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .build()
        )

        // Update
        try {
            context.contentResolver
                .applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Timber.e("EDIT CONTACT ERROR")
        }
    }

    fun getContactsCountFromContentProvider(context: Context): Int {
        var contactsCount = 0
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (cursor != null) {
            contactsCount = cursor.count
        }
        cursor?.close()
        return contactsCount
    }

    private fun getDeletedContactIdFromContentProvider(
        context: Context,
        numberOfContactsToDelete: Int
    ): List<Int> {
        val contactIds = arrayListOf<Int>()
        val cursor = context.contentResolver.query(
            ContactsContract.DeletedContacts.CONTENT_URI,
            null,
            null,
            null,
            "contact_deleted_timestamp desc limit $numberOfContactsToDelete"
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    contactIds.add(cursor.getInt(cursor.getColumnIndexOrThrow("contact_id")))
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return contactIds
    }


    private fun getAllContactsFromContentProvider(
        context: Context,
        selection: String?,
        selectionArgs: Array<String>?,
    ): List<Contact> {
        val contacts = arrayListOf<Contact>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP
        )
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id =
                        cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    val rawContactId =
                        cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID))
                    val name =
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val phoneNumber =
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            .replace("+84", "0").replace("-", "").replace("\\s+".toRegex(), "")
                            .replace("(", "").replace(")", "")
                    val photoUri =
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                    val contactLastUpdateTime =
                        cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP))
                    contacts.add(
                        Contact(
                            contactId = id,
                            rawContactId = rawContactId,
                            name = name,
                            phoneNumber = phoneNumber,
                            photoUri = photoUri,
                            contactLastUpdateTime = contactLastUpdateTime
                        )
                    )
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return contacts
    }

    fun writeDisplayPhoto(context: Context, rawContactId: Long, uri: Uri) {
        try {
            val stream = ByteArrayOutputStream()
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    uri
                )
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val photo = stream.toByteArray()
            val rawContactPhotoUri = Uri.withAppendedPath(
                ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId),
                RawContacts.DisplayPhoto.CONTENT_DIRECTORY
            )
            val fd: AssetFileDescriptor? =
                context.contentResolver.openAssetFileDescriptor(rawContactPhotoUri, "rw")
            val os: FileOutputStream? = fd?.createOutputStream()
            os?.write(photo)
            os?.close()
            fd?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}