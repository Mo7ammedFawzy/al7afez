package com.al7afez.al7afez.entities;

import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
public class Sheikh extends MasterFile {
    private LocalDate birthDate;
    private String phoneNumber;
    private Gender gender;

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
