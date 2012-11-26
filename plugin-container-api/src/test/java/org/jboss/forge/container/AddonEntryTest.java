package org.jboss.forge.container;

import org.junit.Assert;
import org.junit.Test;

public class AddonEntryTest
{
   @Test
   public void testFromCoordinates()
   {
      AddonEntry entry = AddonEntry.fromCoordinates("org.example:example-addon,1.0.0-SNAPSHOT,2.0.0");
      Assert.assertEquals("org.example:example-addon,1.0.0-SNAPSHOT,2.0.0", entry.toCoordinates());
      Assert.assertEquals("org.example.example-addon:1.0.0-SNAPSHOT", entry.toModuleId());
   }

   @Test
   public void testFromIndividual()
   {
      AddonEntry entry = AddonEntry.from("org.example:example-addon", "1.0.0-SNAPSHOT", "2.0.0");
      Assert.assertEquals("org.example:example-addon,1.0.0-SNAPSHOT,2.0.0", entry.toCoordinates());
      Assert.assertEquals("org.example.example-addon:1.0.0-SNAPSHOT", entry.toModuleId());
   }
}
