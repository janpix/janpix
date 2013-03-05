/**
 * Test de integracion que la fabrica de medicion con AVI-b
 */

package com.janpix.rup.empi.avib
import groovy.util.GroovyTestCase;

import com.janpix.rup.empi.Identity;
import com.janpix.rup.exceptions.*
import com.janpix.rup.empi.Person
 
class FactoryAVIbTests extends GroovyTestCase {
	
	def factory
	def weightName 				= 0.25
	def weightBirthdate 		= 0.15
	def weightSex 				= 0.1
	def weightSecondLastName 	= 0.05
	def weightLivingplace 		= 0.15
	def weightAddress 			= 0.1
	def weightDocument 			= 0.2
	
	def i1
	def i2
	def i3
	def i4
	def i5
	
	def upperUmbral = 0.8
	def lowerUmbral = 0.6
	
	
	
	void setUp(){
		super.setUp()

		//Creo la fabrica con los pesos
		def weights = [
				"name":weightName,"birthdate":weightBirthdate,"sex":weightSex,
				"secondLastName":weightSecondLastName,"livingplace":weightLivingplace,
				"address":weightAddress,"document":weightDocument
			]
		factory = new FactoryAVIb(weights:weights)
		
		//Creo algunas identidades
		i1 = new Identity(
				name:"Barnech, Martín Gonzalo",
				birthdate:Date.parse( "yyyy-M-d", "1987-01-16" ),
				sex:Person.TYPE_SEX_MALE,
				secondLastName:"Mannino",
				livingplace:"Argentina, Buenos Aires, Luján",
				address:"Constitucion 2213",
				document:"DNI:32850137"
				)
		i2 = new Identity(
				name:"Barnech, Martín",
				birthdate:Date.parse( "yyyy-M-d", "1987-01-17" ),
				sex:Person.TYPE_SEX_MALE,
				secondLastName:"Manino",
				livingplace:"Argentina, Bs As, Lujan",
				address:"Constitución 2213",
				document:"DNI:32850137"
			)
		i3 = new Identity(
				name:"Barneche, Martin",
				birthdate:Date.parse( "yyyy-M-d", "1988-01-16" ),
				sex:Person.TYPE_SEX_MALE,
				secondLastName:"Mannino",
				livingplace:"Argentina, Buenos Airess, Lujan",
				address:"Constitución 2213",
				document:"DNI:32850187"
			)
		i4 = new Identity(
				name:"Magneres, Joaquin Ignacio",
				birthdate:Date.parse( "yyyy-M-d", "1987-05-01" ),
				sex:Person.TYPE_SEX_MALE,
				secondLastName:"Fontela",
				livingplace:"Argentina, C.A.B.A, C.A.B.A",
				address:"Zapata 346",
				document:"DNI:32900800"
			)
		i5 = new Identity(name:"Rodriguez, Maria",birthdate:Date.parse( "yyyy-M-d", "1987-01-16" ),sex:"Femenino")
	
	}
	
	
	/**
	 * Testea el correcto calculo de distancias entre nombres
	 */
	void testDistanceName(){
		//La distancia es de 8 ( Gonzalo) esta de mas. Y es dividida por el String mas largo
		def distanceName1	= factory.buildMeasurementDistanceName().calculateDistance(i1,i2)
		assertEquals(new Double(this.weightName*(8/23)),distanceName1)
		
		//La distancia es de 2 (la e final de Barneche y el acento en Martín). El string mas largo es de 16
		def distanceName2	= factory.buildMeasurementDistanceName().calculateDistance(i2,i3)
		assertEquals(new Double(this.weightName*(2/16)),distanceName2)
		
		//Estoy comparando Martin y Joaquin. La distancia tiene que ser mayor a 0.5 ponderada por su peso
		def distanceName3	= factory.buildMeasurementDistanceName().calculateDistance(i1,i4)
		assertTrue((distanceName3 > (0.5*this.weightName)))
	}
	
	/**
	 * Testea el correcto calculo de distancia entre fechas de nacimiento
	 */
	void testDistanceBirthdate(){
		//La distancia es de 0.3 (asi esta configurado en la clase) por el peso ya que difieren en el dia
		def distanceBirth1	= factory.buildMeasurementDistanceBirthdate().calculateDistance(i1,i2)
		assertEquals(0.3*this.weightBirthdate,distanceBirth1)
		
		//La distancia es de 1.0*peso ya que son fechas de diferentes años
		def distanceBirth2	= factory.buildMeasurementDistanceBirthdate().calculateDistance(i1,i3)
		assertEquals(1.0*this.weightBirthdate,distanceBirth2)
		
		//La distancia es de 0 ya que es la misma fecha
		def distanceBirth3	= factory.buildMeasurementDistanceBirthdate().calculateDistance(i1,i5)
		assertEquals(0.0*this.weightBirthdate,distanceBirth3)
	}
	
	/**
	 * Testea el correcto calculo de distancia entre sexo
	 */
	void testDistanceSex(){
		def distanceSex1	= factory.buildMeasurementDistanceSex().calculateDistance(i1,i4)
		assertEquals(0.0,distanceSex1)
		
		def distanceSex2	= factory.buildMeasurementDistanceSex().calculateDistance(i1,i5)
		assertEquals(this.weightSex*1.0,distanceSex2)
	}
	
	/**
	 * Calcula que la distancia entre 2 identidades de la misma personas sean menores a un umbral alto
	 */
	void testMatchIdentity1Identity2(){
		//id1 - id2 deben matchear
		def distanceName1			= factory.buildMeasurementDistanceName().calculateDistance(i1,i2)
		def distanceBirth1			= factory.buildMeasurementDistanceBirthdate().calculateDistance(i1,i2)
		def distanceSex1			= factory.buildMeasurementDistanceSex().calculateDistance(i1,i2)
		def distanceSecondLastName1	= factory.buildMeasurementDistanceSecondLastName().calculateDistance(i1,i2)
		def distanceLivingplace1	= factory.buildMeasurementDistanceLivingplace().calculateDistance(i1,i2)
		def distanceAddress1		= factory.buildMeasurementDistanceAddress().calculateDistance(i1,i2)
		def distanceDocument1		= factory.buildMeasurementDistanceDocument().calculateDistance(i1,i2)
		
		def totalDistance = distanceName1 + distanceBirth1 + distanceSex1 +distanceSecondLastName1 + distanceLivingplace1 +distanceAddress1 + distanceDocument1
		println totalDistance
		assertTrue(((1-totalDistance)> upperUmbral))
		

	}
	
	/**
	 * Identidad1 e Identidad3 deberian de ser posibles matcheos ya que difiere nombre, fecha nacimiento, dni
	 */
	void testMatchIdentity1Identity3(){
		//id1 - id3 deben matchear
		def distanceName2			= factory.buildMeasurementDistanceName().calculateDistance(i1,i3)
		def distanceBirth2			= factory.buildMeasurementDistanceBirthdate().calculateDistance(i1,i3)
		def distanceSex2			= factory.buildMeasurementDistanceSex().calculateDistance(i1,i3)
		def distanceSecondLastName2	= factory.buildMeasurementDistanceSecondLastName().calculateDistance(i1,i3)
		def distanceLivingplace2	= factory.buildMeasurementDistanceLivingplace().calculateDistance(i1,i3)
		def distanceAddress2		= factory.buildMeasurementDistanceAddress().calculateDistance(i1,i3)
		def distanceDocument2		= factory.buildMeasurementDistanceDocument().calculateDistance(i1,i3)
		
		def totalDistance2 = distanceName2 + distanceBirth2 + distanceSex2 +distanceSecondLastName2 + distanceLivingplace2 +distanceAddress2 + distanceDocument2
		println totalDistance2
		assertTrue(((1-totalDistance2)> lowerUmbral))
	}
	
	
	
}