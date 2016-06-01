/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.resources;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * A {@link Resource} that represents a Java {@link Class}.
 *
 * @author Mike Brock
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface JavaResource extends FileResource<JavaResource>
{
   /**
    * The possible keys be set in the user configuration file for formatting options
    */
   static final String FORMATTER_PROFILE_NAME_KEY = "org.jboss.forge.addon.parser.java.formatter_profile_name";
   static final String FORMATTER_PROFILE_PATH_KEY = "org.jboss.forge.addon.parser.java.formatter_profile_path";

   /**
    * Set the content of this {@link Resource} to the value of the given
    * {@link org.jboss.forge.roaster.model.source.JavaSource}.
    */
   JavaResource setContents(final JavaSource<?> source);

   /**
    * Attempt to determine and return the {@link JavaType} type of the underlying class.
    */
   <T extends JavaType<?>> T getJavaType() throws FileNotFoundException;

   /**
    * Sets the contents using the given formatter properties
    */
   JavaResource setContents(InputStream data, Properties formatterProperties);

   /**
    * 
    * Returns the fully qualified type name. Returns <code>null</code> if any error occurs
    */
   default String getFullyQualifiedTypeName()
   {
      try
      {
         return getJavaType().getQualifiedName();
      }
      catch (FileNotFoundException e)
      {
         return null;
      }
   }

}
