package com.al7afez.al7afez.service;

import com.al7afez.al7afez.dto.RecitationSuggestionResponse;
import com.al7afez.al7afez.model.entities.Level;
import com.al7afez.al7afez.model.entities.RecitationDocument;

public final class QuranUtils {

    public record SurahAya(int surah, int aya) {
    }

    private static final int[] AYAT = {
            0,                                                    // index 0 unused
            7, 286, 200, 176, 120, 165, 206, 75, 129, 109,   // 1-10
            123, 111, 43, 52, 99, 128, 111, 110, 98, 135,   // 11-20
            112, 78, 118, 64, 77, 227, 93, 88, 69, 60,    // 21-30
            34, 30, 73, 54, 45, 83, 182, 88, 75, 85,    // 31-40
            54, 53, 89, 59, 37, 35, 38, 29, 18, 45,    // 41-50
            60, 49, 62, 55, 78, 96, 29, 22, 24, 13,    // 51-60
            14, 11, 11, 18, 12, 12, 30, 52, 52, 44,    // 61-70
            28, 28, 20, 56, 40, 31, 50, 40, 46, 42,    // 71-80
            29, 19, 36, 25, 22, 17, 19, 26, 30, 20,    // 81-90
            15, 21, 11, 8, 8, 19, 5, 8, 8, 11,    // 91-100
            11, 8, 3, 9, 5, 4, 7, 3, 6, 3,     // 101-110
            5, 4, 5, 6                                      // 111-114
    };

    private QuranUtils() {
    }

    /**
     * Normalizes a (startSurah, aya) position so that if aya exceeds the startSurah's
     * ayat count the position rolls over into subsequent surahs.
     */
    public static SurahAya normalize(int startSurah, int aya) {
        while (startSurah <= 114 && aya > AYAT[startSurah]) {
            aya -= AYAT[startSurah];
            startSurah++;
        }
        return new SurahAya(startSurah, aya);
    }

    /**
     * Returns the position that is {@code noOfAyatToRecite} ayat ahead of (startSurah, startAya),
     * spanning surahs as needed.
     */
    public static SurahAya advance(int startSurah, int startAya, int noOfAyatToRecite) {
        return normalize(startSurah, startAya + noOfAyatToRecite);
    }

    public static RecitationSuggestionResponse suggestNextRecitationData(RecitationDocument lastRecitation, int sessionAyatCount) {
        SurahAya from = normalize(lastRecitation.getToSurah(), lastRecitation.getToAya() + 1);
        SurahAya to   = sessionAyatCount > 0 ? advance(from.surah(), from.aya(), sessionAyatCount - 1) : from;
        return new RecitationSuggestionResponse(from.surah(), from.aya(), to.surah(), to.aya(), sessionAyatCount);
    }

    public static RecitationSuggestionResponse suggestNextRecitationData(Level level) {
        int sessionAyatCount = level.getNumberOfAyatPerSession();
        SurahAya from = new SurahAya(level.getFromSurah(), level.getFromAya());
        SurahAya to   = sessionAyatCount > 0 ? advance(from.surah(), from.aya(), sessionAyatCount - 1) : from;
        return new RecitationSuggestionResponse(from.surah(), from.aya(), to.surah(), to.aya(), sessionAyatCount);
    }
}
