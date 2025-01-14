package org.modelmapper.internal.util.plugins;

public abstract class AbstractProxyDiscoveryPlugin implements ProxyDiscoveryPlugin {

    @Override
    public Object getProxyTarget(Object object) {
        final Class<?> objectClass = object instanceof Class ? (Class<?>) object : object.getClass();
        if (isProxiedClass(objectClass)) {
            final Class<?> superClass = objectClass.getSuperclass();
            if (superClass == Object.class || superClass == null) {
                final Class<?>[] interfaces = objectClass.getInterfaces();
                if (interfaces.length > 0) {
                    return interfaces[0];
                }
            } else {
                return superClass;
            }
        }
        return null;
    }

    protected abstract boolean isProxiedClass(Class<?> objectClass);
}
