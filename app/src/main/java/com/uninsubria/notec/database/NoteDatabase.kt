package com.uninsubria.notec.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.uninsubria.notec.database.Dao.FolderDao
import com.uninsubria.notec.database.Dao.NoteDao
import com.uninsubria.notec.database.model.Folder
import com.uninsubria.notec.database.model.Note
import com.uninsubria.notec.util.Util

// TODO: ExportSchema = False??

@Database (entities = [Note::class, Folder::class], version = 6)

@TypeConverters(Converters::class)
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
                    //.addCallback(roomCallback)
                    .build()

                INSTANCE = instance
                return instance
            }
        }

        /*private val roomCallback =

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
*/
    }

    class DataGenerator {

        companion object {

            fun getNotes(): List<Note>{

                val util = Util()

                return listOf(
                    Note(
                        1,
                        0,
                        "Prima nota!",
                        "Scrivi qui",
                        util.getDataShort(),
                        "Libri",
                        false
                    ),
                    Note(
                        2,
                        0,
                        "CheckList!",
                        "Scrivi qui",
                        util.getDataShort(),
                        "Spesa",
                        false
                    ),
                    Note(
                        3,
                        0,
                        "Immagini!",
                        "Scrivi qui",
                        util.getDataShort(),
                        "Fiori",
                        false
                    )

                )
            }

            fun getFolders(): List<Folder> {
                return listOf(
                    Folder("Libri"),
                    Folder("Spesa"),
                    Folder("Fiori")
                )
            }
        }

    }
}

