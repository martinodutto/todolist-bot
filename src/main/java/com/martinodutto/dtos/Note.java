package com.martinodutto.dtos;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Note {

    private Long chatId;

    private Long noteId;

    private String idea;

    public Note(Long chatId, Long noteId, String idea) {
        this.chatId = chatId;
        this.noteId = noteId;
        this.idea = idea;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public String getIdea() {
        return idea;
    }

    public void setIdea(String idea) {
        this.idea = idea;
    }

    @NotNull
    @Override
    public String toString() {
        return "Note{" +
                "chatId=" + chatId +
                ", noteId=" + noteId +
                ", idea='" + idea + '\'' +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note = (Note) o;

        if (chatId != null ? !chatId.equals(note.chatId) : note.chatId != null) return false;
        if (noteId != null ? !noteId.equals(note.noteId) : note.noteId != null) return false;
        return idea != null ? idea.equals(note.idea) : note.idea == null;
    }

    @Override
    public int hashCode() {
        int result = chatId != null ? chatId.hashCode() : 0;
        result = 31 * result + (noteId != null ? noteId.hashCode() : 0);
        result = 31 * result + (idea != null ? idea.hashCode() : 0);
        return result;
    }
}
