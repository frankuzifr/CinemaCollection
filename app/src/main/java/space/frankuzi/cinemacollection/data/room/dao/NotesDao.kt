package space.frankuzi.cinemacollection.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import space.frankuzi.cinemacollection.data.room.entity.NotesDbEntity

@Dao
interface NotesDao {

    @Insert
    suspend fun addNoteToFilm(notesDbEntity: NotesDbEntity)

    @Query("SELECT * FROM notes WHERE kinopoisk_id = :kinopoisk_id")
    suspend fun getNotesByFilm(kinopoisk_id: Int): List<NotesDbEntity>
}