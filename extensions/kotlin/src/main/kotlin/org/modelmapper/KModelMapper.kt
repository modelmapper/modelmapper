package org.modelmapper

import org.modelmapper.kotlin.KotlinConstructorInjector

class KModelMapper : ModelMapper {
    constructor() : super() {
        configuration.setConstructorInjector(KotlinConstructorInjector())
    }
}