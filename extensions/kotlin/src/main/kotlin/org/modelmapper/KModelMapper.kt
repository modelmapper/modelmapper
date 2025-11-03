package org.modelmapper

import org.modelmapper.kotlin.KotlinConstructorInjector

/**
 * ModelMapper - Performs object mapping, maintains {@link Configuration} and stores {@link TypeMap
 * TypeMaps}. Add support for Kotlin's data classes
 *
 * <ul>
 * <li>To perform object mapping use {@link #map(Object, Class) map}.</li>
 * <li>To configure the mapping of one type to another use {@link #createTypeMap(Class, Class)
 * createTypeMap}.</li>
 * <li>To add mappings for specific properties use {@link #addMappings(PropertyMap) addMappings}
 * supplying a {@link PropertyMap}.</li>
 * <li>To configure ModelMapper use {@link #getConfiguration}.
 * <li>To validate mappings use {@link #validate}.
 * </ul>
 *
 * @author Enrico Da Ros
 */
class KModelMapper : ModelMapper {
    constructor() : super() {
        configuration.setConstructorInjector(KotlinConstructorInjector())
    }
}