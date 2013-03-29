package com.janpix.rup.empi

class PersonName {
	String firstName
	String lastName
	String motherLastName
	String alias
	
	static belongsTo = [person:Person]
	
    static constraints = {
		motherLastName(nullable:true)
		firstName(
			nullable:true,
			validator:{val,obj->
				if(obj.alias == null)
					return val != null
			}
		)
		lastName(
			nullable:true,
			validator:{val,obj->
				if(obj.alias == null)
					return val != null
			}
		)
		alias(
			nullable:true,
			validator:{val,obj->
				if(obj.firstName == null && obj.lastName == null)
					return val != null
			}
		)
    }
	
	String toString(){
		return lastName+", "+firstName
	}
}
