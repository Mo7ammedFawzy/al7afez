Entities
1-student [MasterFile,birthDate,phoneNumber,parentPhoneNumber,gender]
2-sheikh [MasterFile,birthDate,phoneNumber,gender]
3-level [MasterFile,fromSurah,toSurah,fromAya,toAya,numberOfAyatPerSession]
4-group [MasterFile,level,sheikh,student[]]
5-recitation - document [BaseEntity,student,fromSurah,toSurah,fromAya,toAya,numberOfAyat,grade,mistakeType[]]
6-mistakeType [MasterFile,mistakeType]
- BaseEntity(id,code,creationDate)
- MasterFile(name) extends BaseEntity
=======Scenario======
