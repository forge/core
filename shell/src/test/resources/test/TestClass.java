package test;

import java.io.File;
import java.io.FileWriter;

public class TestClass
{
   public static void main(String[] args) throws Exception
   {
      File f = new File("test.txt");
      f.createNewFile();

      if (args.length > 0)
      {
         FileWriter fileWriter = new FileWriter(f);

         for (String arg : args)
         {
            fileWriter.write(arg);
         }

         fileWriter.close();
      }

   }
}
