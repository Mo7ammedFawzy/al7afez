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

    private Integer count;
    private String note;

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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
