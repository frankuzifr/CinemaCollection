package space.frankuzi.cinemacollection.data.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "notes")
data class NotesDbEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "kinopoisk_id") val kinopoiskId: Int,
    @ColumnInfo(name = "note_date") val noteDate: String,
    @ColumnInfo(name = "note_text") val noteText: String
)

