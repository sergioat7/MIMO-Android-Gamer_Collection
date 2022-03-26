package es.upsa.mimo.gamercollection.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import es.upsa.mimo.gamercollection.base.BaseModel
import es.upsa.mimo.gamercollection.daos.GameDao
import es.upsa.mimo.gamercollection.daos.SagaDao
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.SagaResponse
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import es.upsa.mimo.gamercollection.utils.Constants

@Database(
    entities = [
        GameResponse::class,
        SagaResponse::class,
        SongResponse::class], version = 2
)
@TypeConverters(ListConverter::class, DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao
    abstract fun sagaDao(): SagaDao

    companion object {

        //region Private properties
        private var instance: AppDatabase? = null
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE Format")
                database.execSQL("DROP TABLE Genre")
                database.execSQL("DROP TABLE Platform")
                database.execSQL("DROP TABLE State")
            }
        }
        //endregion

        //region Public methods
        fun getAppDatabase(context: Context): AppDatabase {

            if (instance == null) {
                synchronized(AppDatabase::class) {

                    instance = Room
                        .databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            Constants.DATABASE_NAME
                        )
                        .addMigrations(MIGRATION_1_2)
                        .build()
                }
            }
            return instance!!
        }

        fun <T> getDisabledContent(
            currentValues: List<BaseModel<T>>,
            newValues: List<BaseModel<T>>
        ): List<BaseModel<T>> {

            val disabledContent = arrayListOf<BaseModel<T>>()
            for (currentValue in currentValues) {

                if (newValues.firstOrNull { it.id == currentValue.id } == null) {
                    disabledContent.add(currentValue)
                }
            }
            return disabledContent
        }
        //endregion
    }
}