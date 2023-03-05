package vn.tapbi.message.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import vn.tapbi.message.common.Constant
import vn.tapbi.message.data.local.db.AppDatabase
import vn.tapbi.message.data.local.db.ContactDao
import vn.tapbi.message.data.local.db.ConversationDao
import vn.tapbi.message.data.local.db.MessageDao
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideSharedPreference(app: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app.applicationContext)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app.applicationContext,
            AppDatabase::class.java,
            Constant.DB_NAME
        ).fallbackToDestructiveMigration().addMigrations(AppDatabase.MIGRATION_1_2).build()
    }

    @Provides
    @Singleton
    fun provideConversationDao(db: AppDatabase): ConversationDao{
        return db.conversationDao
    }

    @Provides
    @Singleton
    fun provideMessageDao(db: AppDatabase): MessageDao{
        return db.messageDao
    }

    @Provides
    @Singleton
    fun provideContactDao(db: AppDatabase): ContactDao {
        return db.contactDao
    }


}
