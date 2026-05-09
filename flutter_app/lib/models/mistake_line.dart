class MistakeLine {
  int? mistakeTypeId;
  int count;

  MistakeLine({this.mistakeTypeId, this.count = 1});

  Map<String, dynamic> toJson() => {
        'mistakeTypeId': mistakeTypeId,
        'count': count,
      };
}
