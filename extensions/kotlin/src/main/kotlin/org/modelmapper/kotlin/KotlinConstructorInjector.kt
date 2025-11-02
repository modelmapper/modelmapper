package org.modelmapper.kotlin

import org.modelmapper.ConstructorInjector
import org.modelmapper.ConstructorParam
import kotlin.reflect.KClass

class KotlinConstructorInjector : ConstructorInjector {
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