package org.modelmapper.kotlin

import org.modelmapper.ConstructorInjector
import org.modelmapper.ConstructorParam
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class KotlinConstructorInjector : ConstructorInjector {
    companion object{
        val cache = ConcurrentHashMap<Class<*>, Boolean>()
    }
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
        if(cache.containsKey(destinationType))return cache[destinationType]!!
        val kotlinClass: KClass<*> = destinationType.kotlin
        for(constructor in kotlinClass.constructors){
            if(constructor.parameters.size==0){
                cache.put(destinationType,false)
                return false
            }
        }
        cache.put(destinationType,true)
        return true
    }
}