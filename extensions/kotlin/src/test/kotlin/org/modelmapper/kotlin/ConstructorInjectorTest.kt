package org.modelmapper.kotlin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.modelmapper.KModelMapper
import org.modelmapper.ModelMapper

class Mapper {
    @Test
    fun testMappingFromNormalToMixedClass() {
        val person = Person()

        person.name = "Alice"
        person.age = 30
        val mapper = ModelMapper()
        mapper.configuration.setConstructorInjector(KotlinConstructorInjector())

        mapper.createTypeMap(Person::class.java, NewPersonMix::class.java)
        val result = mapper.map(person, NewPersonMix::class.java)
        assertNotNull(result)
        assertEquals(result.name, person.name)
        assertEquals(result.age, person.age)
    }

    @Test
    fun testMappingFromNormalToDataClass() {
        val person = Person()

        person.name = "Alice"
        person.age = 30
        val mapper = ModelMapper()
        mapper.configuration.setConstructorInjector(KotlinConstructorInjector())

        mapper.createTypeMap(Person::class.java, NewPersonD::class.java)
        val result = mapper.map(person, NewPersonD::class.java)
        assertNotNull(result)
        assertEquals(result.name, person.name)
        assertEquals(result.age, person.age)
    }

    @Test
    fun testMappingFromNormalToDataClassWithKotlin() {
        val person = Person()

        person.name = "Alice"
        person.age = 30
        val mapper = KModelMapper()

        mapper.createTypeMap(Person::class.java, NewPersonD::class.java)
        val result = mapper.map(person, NewPersonD::class.java)
        assertNotNull(result)
        assertEquals(result.name, person.name)
        assertEquals(result.age, person.age)
    }

    @Test
    fun testMappingDataClass() {
        val person = PersonD("Alice", 30)
        val mapper = ModelMapper()
        mapper.configuration.setConstructorInjector(KotlinConstructorInjector())

        mapper.createTypeMap(PersonD::class.java, NewPersonD::class.java)
        val result = mapper.map(person, NewPersonD::class.java)
        assertNotNull(result)
        assertEquals(result.name, person.name)
        assertEquals(result.age, person.age)
    }

    @Test
    fun testMapping() {
        val person = Person()
        person.name = "Alice"
        person.age = 30
        val mapper = ModelMapper()
        mapper.configuration.setConstructorInjector(KotlinConstructorInjector())

        mapper.createTypeMap(Person::class.java, NewPerson::class.java)
        val result = mapper.map(person, NewPerson::class.java)
        assertNotNull(result)
        assertEquals(result.name, person.name)
        assertEquals(result.age, person.age)
    }
}