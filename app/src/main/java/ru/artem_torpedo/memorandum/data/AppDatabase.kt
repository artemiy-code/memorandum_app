package ru.artem_torpedo.memorandum.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [NoteDbModel::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    companion object {

        private var instance: AppDatabase? = null
        private val lock = Any()

        fun getInstance(context: Context): AppDatabase {
            instance?.also { return it }
            synchronized(lock) {
                instance?.also { return it }
                return Room.databaseBuilder(
                    context = context,
                    klass = AppDatabase::class.java,
                    name = "note.db"
                ).build().also {
                    instance = it
                }
            }
        }
    }
}