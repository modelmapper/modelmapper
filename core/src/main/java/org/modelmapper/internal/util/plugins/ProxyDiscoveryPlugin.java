package org.modelmapper.internal.util.plugins;

/**
 * Plugin for detecting proxies objects.
 */
public interface ProxyDiscoveryPlugin {
    /**
     * Gets the target for the proxy or null if no proxy could be discovered for the specified object.
     * The returned target can be a class or an object (instance of a specific concrete class).
     *
     * @param object The object or class to get the proxy target for
     * @return The target of the proxy or null if not detectable by this plugin.
     */
    Object getProxyTarget(Object object);
}
