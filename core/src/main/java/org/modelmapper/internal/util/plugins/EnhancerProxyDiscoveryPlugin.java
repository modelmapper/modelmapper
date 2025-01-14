package org.modelmapper.internal.util.plugins;

import java.util.regex.Pattern;

public class EnhancerProxyDiscoveryPlugin extends AbstractByteCodeEnhancedProxyDiscoveryPlugin {
    private final Pattern pattern = Pattern.compile(".*\\$\\$EnhancerBy.*");
    @Override
    protected Pattern getProxyTypePattern() {
        return pattern;
    }
}
