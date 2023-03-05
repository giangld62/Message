package vn.tapbi.message.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import vn.tapbi.message.common.Constant
import vn.tapbi.message.data.model.Contact
import vn.tapbi.message.data.model.Conversation
import vn.tapbi.message.data.model.Message

@Database(entities = [Conversation::class, Message::class, Contact::class], version = Constant.DB_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val conversationDao: ConversationDao
    abstract val messageDao: MessageDao
    abstract val contactDao: ContactDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {}
        }
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {}
        }
    }
}