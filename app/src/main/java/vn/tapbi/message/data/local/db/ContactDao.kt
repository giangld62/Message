package vn.tapbi.message.data.local.db

import androidx.lifecycle.LiveData
import androidx.room.*
import vn.tapbi.message.data.model.Contact

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllContacts(list: List<Contact>)

    @Query("select * from Contact order by name")
    fun getAllContactsLiveData(): LiveData<List<Contact>>

    @Query("select * from Contact order by name")
    fun getAllContacts(): List<Contact>

    @Query("select * from Contact where contactId =:contactId")
    fun getContactById(contactId: Int): Contact

    @Query("select * from Contact where phoneNumber =:address")
    fun getContactByAddress(address: String): Contact?

    @Query("select * from Contact where name like :queryString or phoneNumber like :queryString order by name")
    fun searchForContactsLiveData(queryString: String): List<Contact>

    @Query("update Contact set name =:contactName where contactId =:contactId")
    fun updateContactName(contactId: Int, contactName: String)

    @Query("update Contact set phoneNumber =:phoneNumber where contactId =:contactId")
    fun updateContactPhoneNumber(contactId: Int, phoneNumber: String)

    @Query("update Contact set photoUri =:photoString where contactId =:contactId")
    fun updateContactPhoto(contactId: Int, photoString: String)

    @Query("select * from Contact where contactId =:id")
    fun getContactLiveData(id: Int): LiveData<Contact>

    @Query("select count(contactId) from Contact")
    fun getContactsCount(): Int

    @Query("delete from Contact where contactId =:contactId")
    fun deleteContacts(contactId: Int)

    @Query("select count(phoneNumber) from Contact where phoneNumber =:phoneNumber and contactId !=:contactId")
    fun isPhoneNumberExist(contactId: Int, phoneNumber: String): Int

    @Query("select * from Contact order by contactLastUpdateTime desc limit 1")
    fun getContactLatestUpdate(): Contact

}