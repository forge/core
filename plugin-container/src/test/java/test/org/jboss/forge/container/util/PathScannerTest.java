package test.org.jboss.forge.container.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jboss.forge.container.util.PathScanner;
import org.junit.Assert;
import org.junit.Test;

public class PathScannerTest
{

   @Test
   public void testScanClassloader()
   {
      Set<String> paths = PathScanner.scan(ClassLoader.getSystemClassLoader(),
               new HashSet<String>(Arrays.asList("")));
      Assert.assertTrue(!paths.isEmpty());
   }

}
