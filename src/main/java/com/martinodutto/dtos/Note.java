package com.martinodutto.dtos;

import org.jetbrains.annotations.NotNull;

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
}
