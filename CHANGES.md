# 3.2.0

* Bump asm and byte-buddy for JDK 21 support

# 3.1.0

* Migrates optional converters from modelmapper-module-java8

# 3.0.0

* Minimum JDK 8
* Makes the option collectionsMergeEnabled disable by default

# 2.4.5

### New Features

* Bump asm for JDK 18 support
* Bump byte-buddy

# 2.4.4

### Bug Fixes

* Fix anonymous enum conversion


# 2.4.3

### Bug Fixes

* Fixed #606 - Fix random ClassCastException on java 11
* Fixed #613 - Fix java internal api usage issue on java 16

# 2.4.2

### Bug Fixes

* Fixed #608 - addMappings failed while adding multiple mappings

# 2.4.1

### Bug Fixes

* Bump dependency for better support on Java 12 and Java 16
* Fix anonymous enum conversion
* Fixed #512 - Create intermediate object depends on if match source found
* Fixed #573 - Keep parent source chain to have correct source in context
* Fixed #547 - Fixed generic type info for Map's value type
* Fixed #563 - Resolve more specific instead of generic one in value reader

# 2.4.0

### New Features

* New API `when(condition).skip(src, dest)`
* New configuration `preferNestedProperties` for better support circular reference mapping

### Bug Fixes

* Fixed #414 - Eliminates warnings while defining explicit mapping on collections
* Fixed #462 - Improves performance when creating explicit mapping on a giant model
* Fixed #392 - Fixes handling protobuf iterable
* Fixed #581 - Fix wrong circular reference
* Fixed #550 - Resolve key value types correctly

# 2.3.9

### New Features

* Upgrade ASM for Java 15

### Bug Fixes

* Fixed #553 - the result of post converter was dropped

# 2.3.8

### New Features

* Update typetools to 0.6.2

### Bug Fixes

* #482: NullPointerException on openJDK 11

# 2.3.7

### Bug Fixes

* Fixed issues #520 and #528, which caused excessive locking in PropertyInfoRegistry.

# 2.3.6

### New Features

* Supports Java 13

# 2.3.5

### New Features

* Supports expression chain (when -> with -> using -> map)

### Bug Fixes

* Fixes deproxy when implicit mapping is disabled
* Improves on configuration converter store

# 2.3.4

### New Features

* Supports protocol buffer with model in snake case

# 2.3.3

### New Features

* Provides setCollectionsMergeEnabled configuration

### Bug Fixes

* Date converter supports java.util.Date
* Supports HibernateProxy

# 2.3.2

* #416: asm7.0 support

# 2.3.1

### Bug Fixes

* #411: Fix NoSuchElementException when iterating through Collection interface using iterator
* #407: OSGi issue

# 2.3.0

### New Features

* Support Java 11

### Bug Fixes

* Fixed #389: Provides helper function for collection mapping
* Fixed #397: SortedMap support

# 2.2.0

### New Features

* Provides ModelMapper.addConverter(sourceType, destinationType, converter)
* Provides Converters.Collection.map() to create converter for collection
* Provides builder pattern support

### Bug Fixes

* Fixed #379: Mapping generic types not working
* Fixed #151: Converter not working properly
* Fixed #386: Mapping Source object to Destination object for iterable correctly

# 2.1.1

### New Features

* Protocol buffer module

### Bug Fixes

* Fixed #363: Fixes OSGi loading issue
* Fixed #364: Resolves type correctly
* Fixed #370: Fixes MapValueReader get wrong field type
* Fixed #372: Explicit property mapping will be failed when using source(propertyPath) with List type

# 2.1.0

### New Features

* Provided Jackson module
* Provided TypeMap::addImplicitMappings and ModelMapper::emptyTypeMap
* Provided deepCopy configuration
* Lambda api improvement: mapping from source
* Inherit TypeMap from source's property

### Bug Fixes

* Fixed #360: only assign the destination value if it was created by provider

# 2.0.0

### New Features

* ModelMapper with modules
* Minimum JDK support to 1.6

### Bug Fixes

* Fixed #322 - Java 10
* Fixed #331 - Convert nested objects

# 1.1.3

### Bug Fixes

*  Fixed #296 - Updates dependency for Java 9 issue

# 1.1.2

### Bug Fixes

* Fixed #284 - OSGi Import-Package list is wrong
* Fixed #287 - NullPointerException when mapping final class by using lambda mapping

# 1.1.1

### Bug Fixes

* Fixed #260 - handling well on explicit mapping, like EnumSet
* Fixed #267 - propertyCondition won't be used by skip

# 1.1.0

### New Features

* Added a new configuration option: skipNull
* Added example for Type Map inheritance
* Includes example as a module of ModelMapper
* Includes typetools as dependency for lambda expression type resolver

### Bug Fixes

* Fixed #194 - Returns null when read inexist value using ValueReader
* Fixed #249 - Fixed NullPointerException when getting destination property

# 1.0.0

### New Features

* Add support ExpressionMap for lambda friendly API
* Add support TypeMap inheritance
* Added `ModelMapper.typeMap` for shortcut to create or get TypeMap

### Bug Fixes

* Fixed #168 - TypeMap won't cache wrong mapping result when using ValueReader
* Fixed #171 - Destination properties won't be erased when not mapping.
* Fixed #219 - Reduced the jar size
* Fixed #220 - Resolved ASM type correctly

# 0.7.8

### Bug Fixes

* Fixed #197 - Fix conditional skip bug
* Fixed #200 - Improve converter selection from ConverterStore
* Fixed #204 - Fix handling mutator/accessor had same signature problem

# 0.7.7

### Bug Fixes

* Fixed #153 - Reusable Generic PropertyMap
* Fixed #185 - Deproxy dynamic proxies

# 0.7.6

### Bug Fixes

* Fixed #176 - Reuse of Converters

# 0.7.4

### Bug Fixes

* Fixed #141 - Add cglib reflect package to shadowed jar
* Fixed #143 - Prevent StringIndexOutOfBoundsException when matching underscore tokens
* Fixed #138 - Add support for mapping to SortedSets destinations
* Fixed #129 - Add support for converting empty strings to primitives and primitive wrappers

# 0.7.3

### Bug Fixes

* Fixed #115 - Fixed mapping of arrays
* Fixed #119 - Fix the use of Providers with circular/hierarchical references
* Fixed #120 - Added support for fragile proxies
* Fixed #126 - Ignore synthetic and bridge methods when resolving methods for explicit mappings

# 0.7.2

### Bug Fixes

* Fixed #113 - Proxies should ignore overriden equals methods
* Fixed #114 - Use specific classloader for loading PropertyMap classes

# 0.7.1

### New Features

* Add support for mapping destination fields using a Converter

### Bug Fixes

* Fixed #111 - Ignore synthetic members and bridge methods

# 0.7.0

### New Features

* Added #92 - Map properties using field references
* Added #102 - Skip properties using field references

### Bug Fixes

* Fixed #47 - Handle explicitly provided convertable properties
* Fixed #79 - Use Objenesis to construct proxies
* Fixed #101 - Handle circular references for boxed primitives
* Fixed #104 - Mapping to existing instances of the same type
* Fixed #106 - Missing null check
* Fixed #109 - Support for mapping primitive wrappers
* Fixed #110 - Problem mapping proxied interfaces to POJOs

### API Changes

For field references:

* Added `PropertyMap.map(Object, Object)`
* Added `PropertyMap.skip(Object)`
* Added `PropertyMap.skip(Object, Object)`

# 0.6.5
4/15/2014

* Fixed #100 - Problem mapping nested collections

# 0.6.4
3/31/2014

* Fixed #95 - Empower ConditionalConverters to handle null properties in source objects
* Fixed #96 - Inherited shaded paths preventing customer converters from running

# 0.6.3 
1/24/2014

* Fixed #80 - ModelMapper extensions use maven backward dependencies
* Fixed #82 - Ignore enums when traversing Enum types
* Fixed #85 - Allow non-void setter return types
* Fixed #91 - Add shaded cglib to Export-Package for better OSGi-support

### API Changes

* For #83 - `MappingContext.create(CS source, CD destination)`

# 0.6.2 
11/5/2013

### New Features

* Added support for mapping Groovy properties
* Improved jOOQ support

### Bug Fixes

* Fixed #74 - Null intermediate values possible when instance requested via global provider

# 0.6.0 
8/2/2013

### New Features

* Added support for named TypeMaps
* Added support for ValueReaders
* Added 3rd party integration for Jackson, Gson and jOOQ
* Added Simpler mapper EDSL for dealing with providers
* Added #61 - Added UNDERSCORE NameTokenizer
* Added support for explicit mapping of source path strings

### Bug Fixes

* Fixed #8 - Introduced global property conditions
* Fixed #39 - Issue when mapping to a String
* Fixed #49 - Allow implicit mapping to be skipped globally
* Fixed #54 - Fixed OSGI support
* Fixed #56 - Allow full type matching to be required
* Fixed issue with Javabeans name transformers for get/set methods
* Fixed issue with merged TypeMap Providers not being copied

### API Changes

For named TypeMaps:

* Added `ModelMapper.createTypeMap(Class, Class, String)`
* Added `ModelMapper.createTypeMap(Class, Class, String, Configuration)`
* Added `ModelMapper.createTypeMap(Object, Class, String)`
* Added `ModelMapper.createTypeMap(Object, Class, String, Configuration)`
* Added `ModelMapper.map(Object, Class, String)`
* Added `ModelMapper.map(Object, Object, String)`
* Added `ModelMapper.map(Object, Type, String)`

For Value Readers:

* Added `ValueReader` interface to the SPI
* Added `Configuration.getValueReaders()`
* Added `Configuration.addValueReader(ValueReader)`

For mapping source path Strings:

* Added `PropertyMap.source(String)`

Other changes:

* Added `NameTokenizers.UNDERSCORE`
* Changed `Configuration.enableFieldMatching(boolean)` to `setFieldMatchingEnabled(boolean)`
* Changed `Configuration.ignoreAmbiguity(boolean)` to `setAmbiguityIgnored(boolean)`

# 0.5.6 
3/21/2013

### New Features

* Fixed #46 - Property map difficult to construct types
* Added #45 - Android compatibility
* Added #42 - Support for pre and post mapping Converters
* Added #40 - Enhance TypeTokens support

### Bug Fixes

* Fixed #31 - Multiple source properties hierarchy matching problem
* Fixed #38 - Map mocked objects

# 0.5.5 
2/14/2013

### New Features

* Added #34 - Added support for TypeTokens
* Added #23 - Support for combined token matching

### Bug Fixes

* Fixed #32 - Mapping creates instances for null objects
* Fixed #37 - Mappings being incorrectly created

### API Changes

For TypeTokens:

* Added `TypeToken` class
* Added `ModelMapper.map(Object, Type)`


# 0.5.4 
12/22/2012

* Fixed #18 - Updated manifest.mf to contain OSGI bundle information for shaded packages
* Fixed #26 - MM attempts to instantiate primitive wrapper when used in destination Converter
* Fixed #27 - Destinations values are not set in MappingContext
* Fixed #30 - Cannot proxy types without a default constructor

# 0.5.3 
10/22/2012

* Fixed #22 - Destination properties being cached by mutator
* Fixed #21 - Added support for XMLGregorianCalendar conversion
* Fixed issue when merging from a TypeMap with a Converter

# 0.5.2 
10/18/2012

* Fixed #19 - Merged mappings should respect MatchingStrategy
* Fixed #20 - Enum conversion should support String->Enum

# 0.5.1 
9/26/2012

* Fixed scenario where a circular mapping can overrides an existing mapping
* Fixed #11 - Improper shading
* Fixed #10 - Added support for mapped enum conversion

# 0.5.0 
9/12/2012

* Fixed #2 - Add support for circular references
* Fixed #9 - Overriding intermediate objects in provided destinations
* Fixed conversion of char[] to String
* Fixed GC #20 - Improved hashCode in TypeInfoRegistry
* Completed GC #21 - Implement strict matching strategy
* Fixed GC #22 - PropertyMap doesn't work when token matches exist

# 0.4.0 
7/22/2012

* Added support for TypeMap-wide property conditions, converters and providers
* Improved generic type resolution
* Added support for auto-TypeMap merging
* Fixed #3: Disambiguation enhancements
* Fixed #4: Added source to ProvisionRequest
* Fixed #5: Incorrect shading of null parameters
* Fixed #7: Conversion skipped when source is null
* Fixed GC #8: Skipped circular properties
* Fixed GC #10: Incorrect mappings created for multiple source mappings

# 0.3.5 
8/8/2011

* Fixed GC #9: Compatibility with Java 5

# 0.3.4 
7/30/2011

* Simplified the ConditionalConverter SPI
* Fixed GC #4: Exposed conditional converters for mutation via Configuration.getConverters()
* Fixed GC #5: Copy null values for primitives

# 0.3.3 
7/26/2011

* Fixed GC #3: Missing repackaged cglib dependencies
* Fixed GC #2: UnsupportedOperationException when adding ConditionalConverters to configuration

# 0.3.2 
7/19/2011

* Rolled back class file target version to 1.5

# 0.3.1 
6/27/2011

* Added better handling of inherited generic component types
* Added support for shaded properties when using a converter, a skipped mapping or a null source constant

# 0.3.0 
6/20/2011

* Initial public release