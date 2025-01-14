package org.modelmapper.internal.util.plugins;

import java.util.regex.Pattern;

public class ByteBuddyProxyDiscoveryPlugin extends AbstractByteCodeEnhancedProxyDiscoveryPlugin {
    private final Pattern pattern = Pattern.compile(".*\\$ByteBuddy\\$.*");
    @Override
    protected Pattern getProxyTypePattern() {
        return pattern;
    }
}
