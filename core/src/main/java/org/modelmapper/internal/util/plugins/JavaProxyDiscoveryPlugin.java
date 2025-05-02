package org.modelmapper.internal.util.plugins;

import java.lang.reflect.Proxy;

public class JavaProxyDiscoveryPlugin implements ProxyDiscoveryPlugin {
    @Override
    public Object getProxyTarget(Object object) {
        if (Proxy.isProxyClass(object.getClass())) {
            final Class<?>[] interfaces = object.getClass().getInterfaces();
            if (interfaces.length == 0) {
                return null;
            }
            return interfaces[0];
        }
        return null;
    }
}
