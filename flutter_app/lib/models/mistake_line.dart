class MistakeLine {
  int? mistakeTypeId;
  int count;
  String note;

  MistakeLine({this.mistakeTypeId, this.count = 1, this.note = ''});

  Map<String, dynamic> toJson() => {
        'mistakeTypeId': mistakeTypeId,
        'count': count,
        'note': note.isEmpty ? null : note,
      };
}
