/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.yaml.resource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jboss.forge.addon.resource.AbstractFileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.yaml.snakeyaml.Yaml;

/**
 * Skeleton class for {@link YamlResource} implementations
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractYamlResource extends AbstractFileResource<YamlResource> implements YamlResource
{
   public AbstractYamlResource(final ResourceFactory factory, final File file)
   {
      super(factory, file);
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }

   @SuppressWarnings("unchecked")
   @Override
   public Optional<Map<String, Object>> getModel()
   {
      // https://bitbucket.org/asomov/snakeyaml/wiki/Documentation
      Map<String, Object> map = null;
      Yaml yaml = new Yaml();
      try (FileReader reader = new FileReader(getUnderlyingResourceObject()))
      {
         map = (Map<String, Object>) yaml.load(reader);
      }
      catch (IOException e)
      {
         throw new ResourceException("Error while reading YAML file", e);
      }
      return Optional.ofNullable(map);
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<Map<String, Object>> getAllModel()
   {
      List<Map<String, Object>> result = new ArrayList<>();
      Yaml yaml = new Yaml();
      try (FileReader reader = new FileReader(getUnderlyingResourceObject()))
      {
         for (Object obj : yaml.loadAll(reader))
         {
            if (obj instanceof Map)
               result.add((Map<String, Object>) obj);
         }
      }
      catch (IOException e)
      {
         throw new ResourceException("Error while reading YAML file", e);
      }
      return result;
   }

   @Override
   public YamlResource setContents(Map<String, Object> data)
   {
      Yaml yaml = new Yaml();
      String dump = yaml.dumpAsMap(data);
      setContents(dump);
      return this;
   }

   @Override
   public YamlResource setContents(List<Map<String, Object>> data)
   {
      Yaml yaml = new Yaml();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintStream ps = new PrintStream(baos, true);
      Iterator<Map<String, Object>> it = data.iterator();
      while (it.hasNext())
      {
         Map<String, Object> model = it.next();
         ps.print(yaml.dumpAsMap(model));
         if (it.hasNext())
            ps.println("---");
      }
      setContents(baos.toString());
      return this;
   }
}
