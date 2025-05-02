package org.modelmapper.internal.util.plugins;

import java.util.regex.Pattern;

public class HibernateProxyDiscoveryPlugin extends AbstractByteCodeEnhancedProxyDiscoveryPlugin {
    private final Pattern pattern = Pattern.compile(".*\\$HibernateProxy\\$.*");
    @Override
    protected Pattern getProxyTypePattern() {
        return pattern;
    }
}
