/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
