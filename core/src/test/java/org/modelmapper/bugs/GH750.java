package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Types;
import org.testng.annotations.Test;

/**
 * https://github.com/modelmapper/modelmapper/issues/750
 */
@Test
public class GH750 {
  public void shouldNotTreatUnnamedPackageAsInternalType() throws Exception {
    Class<?> unnamed = compileAndLoad("UnnamedType", "public class UnnamedType {}");

    assertEquals(unnamed.getName(), "UnnamedType");
    assertFalse(Types.isInternalType(unnamed));
    assertTrue(Types.isInternalType(String.class));
  }

  public void shouldMapBeansInTheUnnamedPackage() throws Exception {
    ClassLoader loader = compile("UnnamedSource", beanSource("UnnamedSource"), "UnnamedDest",
        beanSource("UnnamedDest"));
    Class<?> sourceType = Class.forName("UnnamedSource", true, loader);
    Class<?> destinationType = Class.forName("UnnamedDest", true, loader);
    Object source = sourceType.getDeclaredConstructor().newInstance();
    sourceType.getMethod("setName", String.class).invoke(source, "ada");

    Object destination = new ModelMapper().map(source, destinationType);

    assertEquals(destinationType.getMethod("getName").invoke(destination), "ada");
  }

  private static String beanSource(String className) {
    return "public class " + className + " {\n"
        + "  private String name;\n"
        + "  public String getName() { return name; }\n"
        + "  public void setName(String name) { this.name = name; }\n"
        + "}\n";
  }

  private static Class<?> compileAndLoad(String className, String source) throws Exception {
    return Class.forName(className, true, compile(className, source));
  }

  private static ClassLoader compile(String... nameAndSource) throws Exception {
    Path directory = Files.createTempDirectory("gh750-unnamed");
    List<String> args = new ArrayList<String>();
    args.add("-d");
    args.add(directory.toString());
    for (int i = 0; i < nameAndSource.length; i += 2) {
      Path sourceFile = directory.resolve(nameAndSource[i] + ".java");
      Files.write(sourceFile, nameAndSource[i + 1].getBytes(StandardCharsets.UTF_8));
      args.add(sourceFile.toString());
    }

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    if (compiler == null) {
      throw new IllegalStateException("JDK compiler is required");
    }
    ByteArrayOutputStream errors = new ByteArrayOutputStream();
    if (compiler.run(null, null, errors, args.toArray(new String[args.size()])) != 0) {
      throw new IllegalStateException(errors.toString("UTF-8"));
    }
    return new URLClassLoader(new URL[] { directory.toUri().toURL() }, GH750.class.getClassLoader());
  }
}
