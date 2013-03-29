package com.janpix.rup.empi

//import com.janpix.rup.Sex
import java.util.Set;

class Person {
	
	//Basado en HL7 User definition table
	static final String TYPE_SEX_FEMALE 			= 'F'
	static final String TYPE_SEX_MALE 				= 'M'
	static final String TYPE_SEX_OTHER 			= 'O'
	static final String TYPE_SEX_UNKNOWN 			= 'U'
	static final String TYPE_SEX_AMBIGUOS 			= 'A'
	static final String TYPE_SEX_NOT_APPLICABLE 	= 'N'
	
	static final String TYPE_MARITALSTATUS_MARRIED	= 'M'
	static final String TYPE_MARITALSTATUS_SINGLE	= 'S'
	static final String TYPE_MARITALSTATUS_WIDOWED	= 'W'
	
	/*enum Sex{
		FEMALE('F'),MALE('M'),OTHER('O'),
		UNKNOWN('U'),AMBIGUOS('A'),NOT_APPLICABLE('N')
		private final String value;
		public Sex(String p){this.value = p}
		public String value(){return value;}
	}
	enum MaritalStatus{
		MARRIED('M'),SINGLE('S'),WIDOWED('W'),
		private final String value;
		public String value(){return value;}
		public MaritalStatus(String p){this.value = p}
	}*/
	
	PersonName givenName
	ExtendedDate birthdate
	String administrativeSex
	String maritalStatus
	City birthplace
	Boolean multipleBirthIndicator 
	ExtendedDate deathDate
	String tribalCitizenship //Tribu
	
	Date lastUpdated
	Date dateCreated
		
	List<Address> addresses	= []
	List<PhoneNumber> phoneNumbers = []
	Set<Identifier> identifiers = []
	
	static hasMany = [
			identifiers:Identifier,
			addresses:Address,
			phoneNumbers:PhoneNumber
		]
	
    static constraints = {
		givenName(nullable:false)
		administrativeSex(nullable:false)
		birthdate(nullable:false)
		
		maritalStatus(nullable:true)
		deathDate(nullable:true)
		birthplace(nullable:true)
		multipleBirthIndicator(nullable:true)
		tribalCitizenship(nullable:true)
		
		identifiers(nullable:true)
		phoneNumbers(nullable:true)
		addresses(nullable:true)
		
    }
	
	String toString(){
		return "${givenName}-${document}-${birthdate}"
	}
	
	/**
	 * Agrega un identificador a la persona
	 * @param id
	 * @return
	 */
	Boolean addIdentifier(Identifier id){
		
	}
}
