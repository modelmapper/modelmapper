package org.modelmapper.internal.util.plugins;

import java.util.regex.Pattern;

public class SpringCglibProxyDiscoveryPlugin extends AbstractByteCodeEnhancedProxyDiscoveryPlugin {
    private final Pattern pattern = Pattern.compile(".*\\$\\$SpringCGLIB.*");
    @Override
    protected Pattern getProxyTypePattern() {
        return pattern;
    }
}
