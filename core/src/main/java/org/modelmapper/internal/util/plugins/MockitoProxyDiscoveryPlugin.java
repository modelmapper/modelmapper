package org.modelmapper.internal.util.plugins;

import java.util.regex.Pattern;

public class MockitoProxyDiscoveryPlugin extends AbstractByteCodeEnhancedProxyDiscoveryPlugin {
    private final Pattern pattern = Pattern.compile(".*\\$MockitoMock\\$.*");
    @Override
    protected Pattern getProxyTypePattern() {
        return pattern;
    }
}
