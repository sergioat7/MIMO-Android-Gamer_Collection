package es.upsa.mimo.gamercollection.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import es.upsa.mimo.gamercollection.models.FormatResponse
import es.upsa.mimo.gamercollection.models.GenreResponse
import es.upsa.mimo.gamercollection.persistence.daos.FormatDao
import es.upsa.mimo.gamercollection.persistence.daos.GenreDao

@Database(entities = [FormatResponse::class, GenreResponse::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun formatDao(): FormatDao
    abstract fun genreDao(): GenreDao

    companion object {

        var instance: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase {

            if (instance == null) {
                synchronized(AppDatabase::class) {
                    //TODO ejecutar las consultas en background
//                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "GamerCollection").build()
                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "GamerCollection").allowMainThreadQueries().build()
                }
            }
            return instance!!
        }

        fun destroyDatabase() {
            instance = null
        }
    }
}