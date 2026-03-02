package es.upsa.mimo.gamercollection.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.upsa.mimo.gamercollection.data.local.daos.GameDao
import es.upsa.mimo.gamercollection.data.local.daos.SagaDao
import es.upsa.mimo.gamercollection.data.local.daos.SongDao
import es.upsa.mimo.gamercollection.data.local.model.GameEntity
import es.upsa.mimo.gamercollection.data.local.model.SagaEntity
import es.upsa.mimo.gamercollection.data.local.model.SongEntity

@Database(
    entities = [
        GameEntity::class,
        SagaEntity::class,
        SongEntity::class,
    ],
    version = 2,
)
@TypeConverters(ListConverter::class, DateConverter::class)
abstract class GamerCollectionDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao
    abstract fun sagaDao(): SagaDao
    abstract fun songDao(): SongDao
}