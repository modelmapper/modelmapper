package org.modelmapper.kotlin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.modelmapper.ConstructorInjector
import org.modelmapper.ConstructorParam
import org.modelmapper.ModelMapper
import kotlin.reflect.KClass

data class PersonD(val name: String, val age: Int)
data class NewPersonD(val name: String, val age: Int)


class Person(){
    var name: String? = null
    var age: Int = 0
}

class NewPerson(){
    var name: String? = null
    var age: Int = 0
}


class NewPersonMix(val name:String){
    var age: Int = 0
}





class COverrid: ConstructorInjector {

    override fun getParameters(destinationType: Class<*>): List<ConstructorParam> {
        val kotlinClass: KClass<*> = destinationType.kotlin
        var constructor = kotlinClass.constructors.first()
        var params = mutableListOf<ConstructorParam>()
        var index =0
        for(param in constructor.parameters){
            //println(" - ${param.name} : ${param.type}")
            params.add(ConstructorParam(index,param.name, param.type.javaClass))
            index++
        }
        return params
    }

    override fun isApplicable(destinationType: Class<*>): Boolean {
        val kotlinClass: KClass<*> = destinationType.kotlin
        for(constructor in kotlinClass.constructors){
            if(constructor.parameters.size==0)return false
        }

        return true
    }

}
class Mapper {
    @org.junit.jupiter.api.Test
    fun testMappingFromNormalToMixedClass() {
        val person = Person();

        person.name = "Alice"
        person.age = 30
        var mapper = ModelMapper( )
        mapper.configuration.setConstructorInjector(KotlinConstructorInjector())

        mapper.createTypeMap(Person::class.java, NewPersonMix::class.java)
        var result =mapper.map(person, NewPersonMix::class.java)
        assertNotNull(result)
        assertEquals(result.name,person.name)
        assertEquals(result.age,person.age)
    }

    @Test
    fun testMappingFromNormalToDataClass() {
        val person = Person();

        person.name = "Alice"
        person.age = 30
        var mapper = ModelMapper( )
        mapper.configuration.setConstructorInjector(KotlinConstructorInjector())

        mapper.createTypeMap(Person::class.java, NewPersonD::class.java)
        var result =mapper.map(person, NewPersonD::class.java)
        assertNotNull(result)
        assertEquals(result.name,person.name)
        assertEquals(result.age,person.age)
    }

    @Test
    fun testMappingDataClass() {
        val person = PersonD("Alice", 30)
        var mapper = ModelMapper( )
        mapper.configuration.setConstructorInjector(KotlinConstructorInjector())

        mapper.createTypeMap(PersonD::class.java, NewPersonD::class.java)
        var result =mapper.map(person, NewPersonD::class.java)
        assertNotNull(result)
        assertEquals(result.name,person.name)
        assertEquals(result.age,person.age)
    }

    @Test
    fun testMapping() {
        val person = Person()
        person.name = "Alice"
        person.age = 30
        var mapper = ModelMapper( )
        mapper.configuration.setConstructorInjector(KotlinConstructorInjector())

        mapper.createTypeMap(Person::class.java, NewPerson::class.java)
        var result =mapper.map(person, NewPerson::class.java)
        assertNotNull(result)
        assertEquals(result.name,person.name)
        assertEquals(result.age,person.age)
    }
}