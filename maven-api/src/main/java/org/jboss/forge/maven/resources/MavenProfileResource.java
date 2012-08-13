/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.resources;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Profile;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.VirtualResource;

/**
 * MavenProfileResource
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class MavenProfileResource extends VirtualResource<Profile>
{
   private final Profile profile;

   public MavenProfileResource(Resource<?> parent, Profile profile)
   {
      super(parent);
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
      List<Resource<?>> children = new ArrayList<Resource<?>>();
      for (Dependency dep : profile.getDependencies())
      {
         children.add(new MavenDependencyResource(this, dep));
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
