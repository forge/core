package org.jboss.forge.addon.configuration.proxy;

import static org.junit.Assert.assertEquals;

import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import org.apache.commons.configuration.BaseConfiguration;
import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.ConfigurationAdapter;
import org.junit.Test;

public class ForgeProxySelectorTest
{
   private static final String PROXY_HOST = "test.proxy.org";
   private static final int PROXY_PORT = 8080;

   @Test
   public void testNoProxy() throws URISyntaxException
   {
      final Configuration configuration = new ConfigurationAdapter(new BaseConfiguration());
      final ProxySettings proxySettings = ProxySettings.fromForgeConfiguration(configuration);
      final ForgeProxySelector forgeProxySelector = new ForgeProxySelector(null, proxySettings);

      final URI testURI = new URI("https://host.org/resource.html");
      final List<Proxy> expectedProxies = forgeProxySelector.select(testURI);


      assertEquals(1, expectedProxies.size());
      assertEquals(Proxy.NO_PROXY, expectedProxies.get(0));
   }

   @Test
   public void testProxy() throws URISyntaxException
   {
      final ProxySettings proxySettings = ProxySettings.fromHostAndPort(PROXY_HOST, PROXY_PORT);
      final ForgeProxySelector forgeProxySelector = new ForgeProxySelector(null, proxySettings);

      final URI testURI = new URI("https://host.org/resource.html");
      final List<Proxy> expectedProxies = forgeProxySelector.select(testURI);


      assertEquals(1, expectedProxies.size());
      assertEquals(PROXY_HOST + ":" + PROXY_PORT, expectedProxies.get(0).address().toString());
   }

   @Test
   public void testProxyWithExclusion() throws URISyntaxException
   {
      final List<String> nonProxyHosts = Collections.singletonList("*.proxied.org");
      final ProxySettings proxySettings =
               ProxySettings.fromHostPortAndNonProxyHosts(PROXY_HOST, PROXY_PORT, nonProxyHosts);
      final ForgeProxySelector forgeProxySelector = new ForgeProxySelector(null, proxySettings);

      final URI testProxiedURI = new URI("https://host.org/resource.html");
      final List<Proxy> expectedProxies = forgeProxySelector.select(testProxiedURI);

      assertEquals(1, expectedProxies.size());
      assertEquals(PROXY_HOST + ":" + PROXY_PORT, expectedProxies.get(0).address().toString());

      final URI testNonProxiedURI = new URI("https://non.proxied.org/other-resource.html");
      final List<Proxy> expectedNoProxies = forgeProxySelector.select(testNonProxiedURI);

      assertEquals(1, expectedNoProxies.size());
      assertEquals(Proxy.NO_PROXY, expectedNoProxies.get(0));
   }
}
