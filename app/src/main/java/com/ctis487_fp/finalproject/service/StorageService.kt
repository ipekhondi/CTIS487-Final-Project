package com.ctis487_fp.finalproject.service

import com.ctis487_fp.finalproject.Note
import kotlinx.coroutines.flow.Flow

interface StorageService {
    val notes: Flow<List<Note>>
    suspend fun createNote(note: Note)
    suspend fun readNote(noteId: String): Note?
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(noteId: String)
}
