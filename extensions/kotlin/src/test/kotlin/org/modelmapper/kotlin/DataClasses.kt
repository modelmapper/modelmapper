package org.modelmapper.kotlin


data class PersonD(val name: String, val age: Int)
data class NewPersonD(val name: String, val age: Int)


class Person() {
    var name: String? = null
    var age: Int = 0
}

class NewPerson() {
    var name: String? = null
    var age: Int = 0
}


class NewPersonMix(val name: String) {
    var age: Int = 0
}
