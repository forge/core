package org.jboss.forge.furnace;

import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.versions.SingleVersion;
import org.junit.Assert;
import org.junit.Test;

public class AddonIdTest
{
   @Test
   public void testFromCoordinatesMissingAPIVersion() throws Exception
   {
      AddonId addon = AddonId.fromCoordinates("org.jboss.forge.addon:resources,2.0.0-SNAPSHOT");
      Assert.assertNull(addon.getApiVersion());
      Assert.assertEquals("org.jboss.forge.addon:resources", addon.getName());
      Assert.assertEquals(new SingleVersion("2.0.0-SNAPSHOT"), addon.getVersion());
   }

   @Test
   public void testFromCoordinates()
   {
      AddonId entry = AddonId.fromCoordinates("org.example:example-addon,1.0.0-SNAPSHOT,2.0.0");
      Assert.assertEquals("org.example:example-addon,1.0.0-SNAPSHOT", entry.toCoordinates());
   }

   @Test
   public void testFromIndividual()
   {
      AddonId entry = AddonId.from("org.example:example-addon", "1.0.0-SNAPSHOT", "2.0.0");
      Assert.assertEquals("org.example:example-addon,1.0.0-SNAPSHOT", entry.toCoordinates());
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNoName()
   {
      AddonId.from(null, "1.0.0-SNAPSHOT", "2.0.0");
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNoVersion()
   {
      AddonId.from("name", "", "2.0.0");
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNoNameOrVersion()
   {
      AddonId.from(null, null, "2.0.0");
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullVersion()
   {
      AddonId.from("name", null, "2.0.0");
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullNameCompact()
   {
      AddonId.from(null, "1.0.0-SNAPSHOT");
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNoNameCompact()
   {
      AddonId.from("", "1.0.0-SNAPSHOT");
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullVersionCompact()
   {
      AddonId.from("name", null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNoNameOrVersionCompact()
   {
      AddonId.from(null, null);
   }

   @Test
   public void testNoApi()
   {
      AddonId.from("name", "1.0.0-SNAPSHOT", null);
      AddonId.from("name", "1.0.0-SNAPSHOT");
   }
}
