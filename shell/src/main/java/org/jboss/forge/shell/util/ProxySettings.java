package org.jboss.forge.shell.util;

import org.jboss.forge.env.Configuration;

public class ProxySettings {

    private static final String PROXY_CONFIG_HOST_KEY = "host";
    private static final String PROXY_CONFIG_PORT_KEY = "port";
    
    private final String proxyHost;
    private final int proxyPort;
    
    private ProxySettings(String proxyHost, int proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    public static ProxySettings fromHostAndPort(String proxyHost, int proxyPort) {
        return new ProxySettings(proxyHost, proxyPort);
    }
    
    public static ProxySettings fromForgeConfiguration(Configuration proxyConfig) {
        if (proxyConfig == null || !proxyConfig.containsKey(PROXY_CONFIG_HOST_KEY) || 
                !proxyConfig.containsKey(PROXY_CONFIG_PORT_KEY))
            throw new IllegalArgumentException("The proxy configuraiton should be set. See https://docs.jboss.org/author/display/FORGE/Configure+HTTP+Proxy"); 
        return new ProxySettings(proxyConfig.getString(PROXY_CONFIG_HOST_KEY), 
                proxyConfig.getInt(PROXY_CONFIG_PORT_KEY));
    }
    
    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }
}
