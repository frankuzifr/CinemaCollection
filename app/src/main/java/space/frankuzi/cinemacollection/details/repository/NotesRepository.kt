package space.frankuzi.cinemacollection.details.repository

import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.structs.FilmNote
import space.frankuzi.cinemacollection.data.room.entity.NotesDbEntity

class NotesRepository(
    private val database: AppDatabase
) {
    suspend fun getFilmNotes(kinopoiskId: Int): List<FilmNote> {
        return database.getNotesDao().getNotesByFilm(kinopoiskId).map {
            FilmNote(
                date = it.noteDate,
                note = it.noteText
            )
        }
    }

    suspend fun addFilmNotes(kinopoiskId: Int, note: FilmNote) {
        val notesDbEntity = NotesDbEntity(
            kinopoiskId = kinopoiskId,
            noteDate = note.date,
            noteText = note.note
        )
        database.getNotesDao().addNoteToFilm(notesDbEntity)
    }
}