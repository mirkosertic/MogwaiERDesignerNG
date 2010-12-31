SELECT
	SUBSTR ("UnitName", INSTR ("UnitName", ' ') + 1)  "FirstName",
	SUBSTR ("UnitName", 0, INSTR ("UnitName", ' ') - 1) "LastName",
	"UnitKey","KODAVETID","UnitName","Title",
	"StreetName","StreetNmbr","PostalZone","Town","Community",
	"Canton","RVOKey","X","Y","Country","PostalFirstLine",
	"PostalStreet","PostalStreetNumber","PostalPostZone",
	"PostalTown","PostalCountry","HomePhones","WorkPhones",
	"MobilePhones","Faxs","Email","AnimalMovementDBIDs",
	"OfficeOfAgricultureDBIDs","LanguageKey","Comments","Active",
	"TS","Locked","RVONumber"
   FROM "Units"