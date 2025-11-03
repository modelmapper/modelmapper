package org.modelmapper.kotlin

import org.modelmapper.spi.ConstructorInjector
import org.modelmapper.ConstructorParam
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class KotlinConstructorInjector : ConstructorInjector {
    companion object {
        val cache = ConcurrentHashMap<Class<*>, Boolean>()
    }

    override fun getParameters(destinationType: Class<*>): List<ConstructorParam> {
        val kotlinClass: KClass<*> = destinationType.kotlin
        val constructor = kotlinClass.constructors.first()
        val params = mutableListOf<ConstructorParam>()
        var index = 0
        for (param in constructor.parameters) {
            params.add(ConstructorParam(index, param.name, param.type.javaClass))
            index++
        }
        return params
    }

    override fun isApplicable(destinationType: Class<*>): Boolean {
        if (cache.containsKey(destinationType)) return cache[destinationType]!!
        val kotlinClass: KClass<*> = destinationType.kotlin
        for (constructor in kotlinClass.constructors) {
            if (constructor.parameters.isEmpty()) {
                cache[destinationType] = false
                return false
            }
        }
        cache[destinationType] = true
        return true
    }
}