package com.al7afez.al7afez.model.details;

import com.al7afez.al7afez.model.entities.MistakeType;
import com.al7afez.al7afez.model.entities.RecitationDocument;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class RecitationMistakeLine extends BaseLine {
    @ManyToOne
    @JoinColumn(name = "recitation_document_id")
    private RecitationDocument recitationDocument;

    @ManyToOne
    @JoinColumn(name = "mistake_type_id")
    private MistakeType mistakeType;

    private Integer surahNumber;
    private Integer ayaNumber;
    private Integer wordIndex;

    public RecitationDocument getRecitationDocument() {
        return recitationDocument;
    }

    public void setRecitationDocument(RecitationDocument recitationDocument) {
        this.recitationDocument = recitationDocument;
    }

    public MistakeType getMistakeType() {
        return mistakeType;
    }

    public void setMistakeType(MistakeType mistakeType) {
        this.mistakeType = mistakeType;
    }

    public Integer getSurahNumber() {
        return surahNumber;
    }

    public void setSurahNumber(Integer surahNumber) {
        this.surahNumber = surahNumber;
    }

    public Integer getAyaNumber() {
        return ayaNumber;
    }

    public void setAyaNumber(Integer ayaNumber) {
        this.ayaNumber = ayaNumber;
    }

    public Integer getWordIndex() {
        return wordIndex;
    }

    public void setWordIndex(Integer wordIndex) {
        this.wordIndex = wordIndex;
    }
}
