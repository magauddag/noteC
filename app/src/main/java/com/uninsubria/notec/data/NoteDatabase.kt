package com.uninsubria.notec.data

import android.content.Context
import android.os.AsyncTask
import android.provider.ContactsContract
import android.provider.Settings.Global.getString
import android.view.View
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.uninsubria.notec.R
import com.uninsubria.notec.R.string.*
import kotlinx.coroutines.withContext
import java.security.AccessControlContext
import java.util.concurrent.Executors
import kotlin.coroutines.coroutineContext

// TODO: ExportSchema = False??

@Database (entities = [Note::class, Folder::class], version = 5)
abstract class NoteDatabase: RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun folderDao(): FolderDao

    //Make the database a singleton
    companion object {

        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase (context: Context): NoteDatabase {

            val tempInstance = INSTANCE

            if (tempInstance != null)
                return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()

                INSTANCE = instance
                return instance
            }
        }

        private val roomCallback =

            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Executors.newSingleThreadExecutor().execute {
                        INSTANCE?.let {
                            for (i in DataGenerator.getNotes()) {
                                it.noteDao().insert(i)
                            }
                            for (i in DataGenerator.getFolders()) {
                                it.folderDao().insert(i)
                            }
                        }

                    }
                }
            }

    }

    class DataGenerator {

        companion object {

            fun getNotes(): List<Note>{
                return listOf(
                    Note(1, 0, "Prima nota!", "Scrivi qui", "oggi", "Libri", false),
                    Note(2, 0, "CheckList!", "Scrivi qui", "domani", "Spesa", false),
                    Note(3, R.drawable.example_image, "Immagini!", "Scrivi qui", "ieri", "Fiori", false)

                )
            }

            fun getFolders(): List<Folder> {
                return listOf(
                    Folder("Preferiti"),
                    Folder("Private"),
                    Folder("Foto")
                )
            }
        }

    }
}

