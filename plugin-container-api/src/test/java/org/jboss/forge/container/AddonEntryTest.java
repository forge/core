package org.jboss.forge.container;

import org.junit.Assert;
import org.junit.Test;

public class AddonEntryTest
{
   @Test
   public void testCoordinates()
   {
      AddonEntry entry = AddonEntry.fromCoordinates("org.example.example-addon:2.0:1.0-SNAPSHOT");
      Assert.assertEquals("org.example.example-addon:2.0:1.0-SNAPSHOT", entry.toCoordinates());
      Assert.assertEquals("org.example.example-addon:1.0-SNAPSHOT", entry.toModuleId());
   }

   @Test
   public void testCoordinatesFromIndividual()
   {
      AddonEntry entry = AddonEntry.from("org.example.example-addon", "2.0");
      Assert.assertEquals("org.example.example-addon:2.0:main", entry.toCoordinates());
      Assert.assertEquals("org.example.example-addon:main", entry.toModuleId());
   }
}
