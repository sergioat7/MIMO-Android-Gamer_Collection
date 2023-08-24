package es.upsa.mimo.gamercollection.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.upsa.mimo.gamercollection.database.GamerCollectionDatabase
import es.upsa.mimo.gamercollection.database.daos.GameDao
import es.upsa.mimo.gamercollection.database.daos.SagaDao
import es.upsa.mimo.gamercollection.database.daos.SongDao
import es.upsa.mimo.gamercollection.utils.Constants
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    //region Private properties
    private var instance: GamerCollectionDatabase? = null
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE Format")
            database.execSQL("DROP TABLE Genre")
            database.execSQL("DROP TABLE Platform")
            database.execSQL("DROP TABLE State")
        }
    }
    //endregion

    @Singleton
    @Provides
    fun provideGamerCollectionDatabase(@ApplicationContext context: Context): GamerCollectionDatabase {

        return Room
            .databaseBuilder(
                context.applicationContext,
                GamerCollectionDatabase::class.java,
                Constants.DATABASE_NAME
            )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Singleton
    @Provides
    fun providesGameDao(database: GamerCollectionDatabase): GameDao = database.gameDao()

    @Singleton
    @Provides
    fun providesSagaDao(database: GamerCollectionDatabase): SagaDao = database.sagaDao()

    @Singleton
    @Provides
    fun providesSongDao(database: GamerCollectionDatabase): SongDao = database.songDao()
}