package org.modelmapper.internal.util.plugins;

import java.util.regex.Pattern;

public class PermazenProxyDiscoveryPlugin extends AbstractByteCodeEnhancedProxyDiscoveryPlugin {
    private final Pattern pattern = Pattern.compile(".*\\$\\$Permazen.*");
    @Override
    protected Pattern getProxyTypePattern() {
        return pattern;
    }
}
