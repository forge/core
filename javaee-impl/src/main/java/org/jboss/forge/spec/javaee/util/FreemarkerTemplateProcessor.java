package org.jboss.forge.spec.javaee.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerTemplateProcessor
{

   private freemarker.template.Configuration freemarkerConfig;

   public FreemarkerTemplateProcessor()
   {
      freemarkerConfig = new freemarker.template.Configuration();
      freemarkerConfig.setClassForTemplateLoading(getClass(), "/");
      freemarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
   }

   public String processTemplate(Map<Object, Object> map, String templateLocation)
   {
      Writer output = new StringWriter();
      try
      {
         Template templateFile = freemarkerConfig.getTemplate(templateLocation);
         templateFile.process(map, output);
         output.flush();
      }
      catch (IOException ioEx)
      {
         throw new RuntimeException(ioEx);
      }
      catch (TemplateException templateEx)
      {
         throw new RuntimeException(templateEx);
      }
      return output.toString();
   }

}
