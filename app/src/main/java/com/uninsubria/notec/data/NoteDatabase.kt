package com.uninsubria.notec.data

import android.content.Context
import android.os.AsyncTask
import android.view.View
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.security.AccessControlContext

// TODO: ExportSchema = False??

@Database (entities = [Note::class], version = 1)
abstract class NoteDatabase: RoomDatabase() {

    abstract fun noteDao(): NoteDao

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

        /*val roomCallback =

            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    PopulateDbAsyncTask(INSTANCE!!).execute()
                }

            }*/
    }


    /*private class PopulateDbAsyncTask(db : NoteDatabase): AsyncTask<Void, Void, Void>() {
        val noteDao = db.noteDao()


        override fun doInBackground(vararg params: Void?): Void? {
            noteDao.insert(Note(0, "Titolo 1", "Corpo 1", "Oggi", "Link" ))
            noteDao.insert(Note(1, "Titolo 2", "Corpo 2", "Ieri", "Foto" ))
            noteDao.insert(Note(2, "Titolo 3", "Corpo 3", "Domani", "YT" ))
            return null
        }

    }*/
}