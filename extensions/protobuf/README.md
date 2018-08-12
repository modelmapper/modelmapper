## Dependency

```xml
<dependency>
    <groupId>org.modelmapper.extensions</groupId>
    <artifactId>modelmapper-protobuf</artifactId>
    <version>${version}</version>
</dependency>
```

## Registering module

```java
modelMapper.registerModule(new ProtobufModule());
```
