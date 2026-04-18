package com.al7afez.al7afez.entities;

import jakarta.persistence.Entity;

@Entity
public class Level extends MasterFile {
    private int fromSurah;
    private int toSurah;
    private int fromAya;
    private int toAya;
    private int numberOfAyatPerSession;

    public int getFromSurah() {
        return fromSurah;
    }

    public void setFromSurah(int fromSurah) {
        this.fromSurah = fromSurah;
    }

    public int getToSurah() {
        return toSurah;
    }

    public void setToSurah(int toSurah) {
        this.toSurah = toSurah;
    }

    public int getFromAya() {
        return fromAya;
    }

    public void setFromAya(int fromAya) {
        this.fromAya = fromAya;
    }

    public int getToAya() {
        return toAya;
    }

    public void setToAya(int toAya) {
        this.toAya = toAya;
    }

    public int getNumberOfAyatPerSession() {
        return numberOfAyatPerSession;
    }

    public void setNumberOfAyatPerSession(int numberOfAyatPerSession) {
        this.numberOfAyatPerSession = numberOfAyatPerSession;
    }
}
