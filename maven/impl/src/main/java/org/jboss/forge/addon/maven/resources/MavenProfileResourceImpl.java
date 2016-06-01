/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.resources;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Profile;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.VirtualResource;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenProfileResourceImpl extends VirtualResource<Profile> implements MavenProfileResource
{
   private final Profile profile;

   public MavenProfileResourceImpl(ResourceFactory factory, Resource<?> parent, Profile profile)
   {
      super(factory, parent);
      this.profile = profile;
   }

   @Override
   public String getName()
   {
      return profile.getId();
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      List<Resource<?>> children = new ArrayList<>();
      for (Dependency dep : profile.getDependencies())
      {
         children.add(new MavenDependencyResourceImpl(getResourceFactory(), this, dep));
      }
      return children;
   }

   @Override
   public Profile getUnderlyingResourceObject()
   {
      return profile;
   }

   @Override
   public boolean delete() throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("not supported");
   }

   @Override
   public boolean delete(boolean recursive) throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException("not supported");
   }
}
