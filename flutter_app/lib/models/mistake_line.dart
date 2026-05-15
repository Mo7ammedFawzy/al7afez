class MistakeLine {
  int? mistakeTypeId;
  int? surahNumber;
  int? ayaNumber;
  int? wordIndex;

  MistakeLine({
    this.mistakeTypeId,
    this.surahNumber,
    this.ayaNumber,
    this.wordIndex,
  });

  Map<String, dynamic> toJson() => {
        if (mistakeTypeId != null) 'mistakeTypeId': mistakeTypeId,
        if (surahNumber != null) 'surahNumber': surahNumber,
        if (ayaNumber != null) 'ayaNumber': ayaNumber,
        if (wordIndex != null) 'wordIndex': wordIndex,
      };
}
