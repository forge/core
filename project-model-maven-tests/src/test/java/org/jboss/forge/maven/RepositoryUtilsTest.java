/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.maven.settings.Proxy;
import org.junit.Test;

/**
 * Test case for {@link RepositoryUtils} class
 * 
 * @author George Gastaldi <gegastaldi@gmail.com>
 * 
 */
public class RepositoryUtilsTest {

    @Test
    public void testConvertFromMavenProxyExpectNull() {
        assertNull(RepositoryUtils.convertFromMavenProxy(null));
    }

    @Test
    public void testConvertFromMavenProxyWithoutAuth() {
        Proxy proxySettings = new Proxy();
        proxySettings.setHost("foo.com");
        proxySettings.setPort(3128);
        org.sonatype.aether.repository.Proxy proxyObj = RepositoryUtils.convertFromMavenProxy(proxySettings);
        assertNotNull(proxyObj);
        assertEquals(proxySettings.getHost(), proxyObj.getHost());
        assertEquals(proxySettings.getPort(), proxyObj.getPort());
        assertNull(proxyObj.getAuthentication().getUsername());
        assertNull(proxyObj.getAuthentication().getPassword());
    }

    @Test
    public void testConvertFromMavenProxyWithAuth() {
        Proxy proxySettings = new Proxy();
        proxySettings.setHost("foo.com");
        proxySettings.setPort(3128);
        proxySettings.setUsername("john");
        proxySettings.setPassword("doe");
        org.sonatype.aether.repository.Proxy proxyObj = RepositoryUtils.convertFromMavenProxy(proxySettings);
        assertNotNull(proxyObj);
        assertEquals(proxySettings.getHost(), proxyObj.getHost());
        assertEquals(proxySettings.getPort(), proxyObj.getPort());
        assertEquals(proxySettings.getUsername(),proxyObj.getAuthentication().getUsername());
        assertEquals(proxySettings.getPassword(),proxyObj.getAuthentication().getPassword());
    }
}
