package com.janpix.rup.empi

class City {
	String name
	Province province
	
	static belongsTo = [province:Province]
	
    static constraints = {
    }
}
