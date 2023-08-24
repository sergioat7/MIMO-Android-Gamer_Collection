package es.upsa.mimo.gamercollection.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.upsa.mimo.gamercollection.database.daos.GameDao
import es.upsa.mimo.gamercollection.database.daos.SagaDao
import es.upsa.mimo.gamercollection.database.daos.SongDao
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.models.SongResponse
import es.upsa.mimo.gamercollection.models.base.BaseModel

@Database(
    entities = [
        GameResponse::class,
        SagaResponse::class,
        SongResponse::class], version = 2
)
@TypeConverters(ListConverter::class, DateConverter::class)
abstract class GamerCollectionDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao
    abstract fun sagaDao(): SagaDao
    abstract fun songDao(): SongDao

    companion object {

        //region Public methods
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