/*
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
package org.modelmapper.internal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.modelmapper.ModelMapper;

/**
 * Using Bridge Class Loader approach as described in the following article:
 * https://www.infoq.com/articles/code-generation-with-osgi
 * <p>
 * This eliminates the need for forcing ModelMapper users to add custom OSGi imports to cglib's internals.
 * <p>
 * In addition to that, this Class Loader attempts to solve the issue described in the following StackOverflow topic:
 * https://stackoverflow.com/questions/47854086/cglib-creating-class-proxy-in-osgi-results-in-noclassdeffounderror
 * <p>
 * This can also easily be solved by introducing a Dynamic-ImportPackage: * in ModelMapper's MANIFEST.MF
 * But this feels like an overkill.
 * Instead, we take a bit different approach.
 * Since with Bridging we already defined a separate class space for enchanted classes, we might as well try to
 * include in the resolving procedure the ClassLoaders of class types that the User's type inherits from.
 * That way, we make sure to delegate class loading of types that are otherwise unknown to the User's bundle
 * (such as javax.xml.datatype.XMLGregorianCalendar in the StackOverflow topic) to the respective Bundle's ClassLoader
 * that actually uses the type.
 *
 * @author m.dzhigarov
 */
public class BridgeClassLoaderFactory {
  private static final class BridgeClassLoader extends ClassLoader {
    private final ClassLoader internalClassSpace; // Bridging the internal lib class space as described in https://www.infoq.com/articles/code-generation-with-osgi
    private final Set<ClassLoader> additionalClassLoaders; // Additional Class Loaders in attempt to solve https://stackoverflow.com/questions/47854086/cglib-creating-class-proxy-in-osgi-results-in-noclassdeffounderror

    BridgeClassLoader(ClassLoader primary) {
      super(primary);
      internalClassSpace = ModelMapper.class.getClassLoader(); // ModelMapper's own ClassLoader must know how to load its internals
      additionalClassLoaders = Collections.newSetFromMap(new ConcurrentHashMap<ClassLoader, Boolean>());
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
      if (name.startsWith("org.modelmapper.internal.cglib"))
        return internalClassSpace.loadClass(name);

      for (ClassLoader additionalClassLoader : additionalClassLoaders) {
        try {
          return additionalClassLoader.loadClass(name);
        }
        catch (ClassNotFoundException e) {
          // Don't mind... Attempt next class loader
        }
      }

      throw new ClassNotFoundException(name);
    }

    private void addAdditionalClassLoaders(Set<ClassLoader> additionalClassLoaders) {
      additionalClassLoaders.remove(this.getParent()); // Make sure that the parent ClassLoader is not part of the collection. Otherwise, a recursion might occur in findClass().
      this.additionalClassLoaders.addAll(additionalClassLoaders);
    }
  }

  private static final Map<ClassLoader, WeakReference<BridgeClassLoader>> CACHE = new WeakHashMap<ClassLoader, WeakReference<BridgeClassLoader>>();
  static ClassLoader getClassLoader(Class<?> appType) {
    Set<Class<?>> allExtendedOrImplementedTypesRecursively = getAllExtendedOrImplementedTypesRecursively(appType);
    Set<ClassLoader> allClassLoadersInTheTypeHierarchy = getAllClassLoadersInTheTypeHierarchy(allExtendedOrImplementedTypesRecursively);

    synchronized (BridgeClassLoaderFactory.class) {
      BridgeClassLoader bridgeClassLoader = null;
      WeakReference<BridgeClassLoader> bridgeClassLoaderRef = CACHE.get(appType.getClassLoader());
      if (bridgeClassLoaderRef != null) {
        bridgeClassLoader = bridgeClassLoaderRef.get();
      }

      if (bridgeClassLoader == null) {
        bridgeClassLoader = new BridgeClassLoader(appType.getClassLoader());
        CACHE.put(appType.getClassLoader(), new WeakReference<BridgeClassLoader>(bridgeClassLoader));
      }

      bridgeClassLoader.addAdditionalClassLoaders(allClassLoadersInTheTypeHierarchy);

      return bridgeClassLoader;
    }
  }

  private static Set<ClassLoader> getAllClassLoadersInTheTypeHierarchy(Set<Class<?>> allExtendedOrImplementedTypesRecursively) {
    Set<ClassLoader> result = new HashSet<ClassLoader>();
    for (Class<?> clazz : allExtendedOrImplementedTypesRecursively) {
      if (clazz.getClassLoader() != null) {
        result.add(clazz.getClassLoader());
      }
    }

    return result;
  }

  // Extracted from https://stackoverflow.com/questions/22031207/find-all-classes-and-interfaces-a-class-extends-or-implements-recursively
  private static Set<Class<?>> getAllExtendedOrImplementedTypesRecursively(Class<?> clazzArg) {
    Class<?> clazz = clazzArg;
    List<Class<?>> res = new ArrayList<Class<?>>();

    do {
      res.add(clazz);

      // First, add all the interfaces implemented by this class
      Class<?>[] interfaces = clazz.getInterfaces();
      if (interfaces.length > 0) {
        res.addAll(Arrays.asList(interfaces));

        for (Class<?> interfaze : interfaces) {
          res.addAll(getAllExtendedOrImplementedTypesRecursively(interfaze));
        }
      }

      // Add the super class
      Class<?> superClass = clazz.getSuperclass();

      // Interfaces does not have java,lang.Object as superclass, they have null, so break the cycle and return
      if (superClass == null) {
        break;
      }

      // Now inspect the superclass
      clazz = superClass;
    } while (!"java.lang.Object".equals(clazz.getCanonicalName()));

    return new HashSet<Class<?>>(res);
  }
}