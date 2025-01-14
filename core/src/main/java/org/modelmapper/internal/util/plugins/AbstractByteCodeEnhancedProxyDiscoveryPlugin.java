package org.modelmapper.internal.util.plugins;

import java.util.regex.Pattern;

public abstract class AbstractByteCodeEnhancedProxyDiscoveryPlugin extends AbstractProxyDiscoveryPlugin {
    @Override
    protected boolean isProxiedClass(Class<?> objectClass) {
        return getProxyTypePattern().matcher(objectClass.getSimpleName()).matches();
    }

    protected abstract Pattern getProxyTypePattern();
}
