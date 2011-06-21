/**
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * ModelMapper is an intelligent object mapping library.
 * 
 * <p>
 * The principal public APIs in this package are:
 * 
 * <dl>
 * <dt>{@link org.modelmapper.ModelMapper}
 * <dd>The class you instantiate to perform object mapping, load mapping definitions and register mappers.
 * 
 * <dt>{@link org.modelmapper.PropertyMap}
 * <dd>The class you extend to define mappings between source and destination properties.
 * 
 * <dt>{@link org.modelmapper.TypeMap}
 * <dd>The interface you use to perform configuration, introspection and mapping between two types.
 * 
 * <dt>{@link org.modelmapper.Converter}
 * <dd>The interface you implement to perform custom mapping between two types or property hierarchies.
 * 
 * <dt>{@link org.modelmapper.Provider}
 * <dd>The interface you implement to provide instances of destination types.
 * 
 * <dt>{@link org.modelmapper.Condition}
 * <dd>The interface you implement to conditionally create a mapping.
 * </dl>
 */
package org.modelmapper;

