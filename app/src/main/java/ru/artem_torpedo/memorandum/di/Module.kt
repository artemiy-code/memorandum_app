package ru.artem_torpedo.memorandum.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.artem_torpedo.memorandum.data.AppDatabase
import ru.artem_torpedo.memorandum.data.NotesDao
import ru.artem_torpedo.memorandum.data.NotesRepositoryImpl
import ru.artem_torpedo.memorandum.domain.NotesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface Module {

    @Singleton
    @Binds
    fun repoInsteadOfInterface(
        repo: NotesRepositoryImpl,
    ): NotesRepository

    companion object {
        @Singleton
        @Provides
        fun getDatabaseMainClass(@ApplicationContext context: Context): AppDatabase {
            return AppDatabase.getInstance(context)
        }

        @Singleton
        @Provides
        fun getDao (db : AppDatabase): NotesDao {
            return db.notesDao()
        }
    }
}