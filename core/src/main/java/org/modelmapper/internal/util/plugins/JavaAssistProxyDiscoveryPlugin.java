package org.modelmapper.internal.util.plugins;

import org.modelmapper.internal.util.Types;

import java.lang.reflect.Method;

public class JavaAssistProxyDiscoveryPlugin extends AbstractProxyDiscoveryPlugin {

    private Method isProxyMethod;

    public JavaAssistProxyDiscoveryPlugin() {
        try {
            Class<?> factoryClass = Types.class.getClassLoader().loadClass(
                    "javassist.util.proxy.ProxyFactory");
            isProxyMethod = factoryClass.getMethod("isProxyClass",
                    new Class<?>[]{Class.class});
        } catch (Exception ignore) {
        }
    }

    @Override
    protected boolean isProxiedClass(Class<?> objectClass) {
        try {
            return isProxyMethod != null
                    && (Boolean) isProxyMethod.invoke(null, objectClass);
        } catch (Exception ignore) {
        }
        return false;
    }
}
