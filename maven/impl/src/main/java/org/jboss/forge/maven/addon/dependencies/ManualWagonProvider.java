/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.addon.dependencies;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.file.FileWagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagonAuthenticator;
import org.apache.maven.wagon.providers.http.LightweightHttpsWagon;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.sonatype.aether.connector.wagon.WagonProvider;

class ManualWagonProvider implements WagonProvider
{
   private static final String HTTP = "http";
   private static final String HTTPS = "https";
   private static final String FILE = "file";

   /**
    * {@inheritDoc}
    *
    * @see org.sonatype.aether.connector.wagon.WagonProvider#lookup(java.lang.String)
    */
   @Override
   public Wagon lookup(final String roleHint) throws Exception
   {
      if (roleHint.equals(HTTP))
      {
         return setAuthenticator(new LightweightHttpWagon());
      }
      else if (roleHint.equals(HTTPS))
      {
         return setAuthenticator(new LightweightHttpsWagon());
      }
      else if (roleHint.equals(FILE))
      {
         return new FileWagon();
      }

      throw new RuntimeException("Role hint not supported: " + roleHint);
   }

   /**
    * {@inheritDoc}
    *
    * @see org.sonatype.aether.connector.wagon.WagonProvider#release(org.apache.maven.wagon.Wagon)
    */
   @Override
   public void release(final Wagon wagon)
   {
      // NO-OP
   }

   // SHRINKRES-68
   // Wagon noes not correctly fill Authenticator field if Plexus is not used
   // we need to use reflexion in order to get fix this behavior
   // http://dev.eclipse.org/mhonarc/lists/aether-users/msg00113.html
   private LightweightHttpWagon setAuthenticator(final LightweightHttpWagon wagon)
   {
      final Field authenticator;
      try
      {
         authenticator = AccessController.doPrivileged(new PrivilegedExceptionAction<Field>()
         {
            @Override
            public Field run() throws Exception
            {
               final Field field = LightweightHttpWagon.class.getDeclaredField("authenticator");
               field.setAccessible(true);
               return field;
            }
         });
      }
      catch (final PrivilegedActionException pae)
      {
         throw new ResolutionException("Could not manually set authenticator to accessible on "
                  + LightweightHttpWagon.class.getName(), pae);
      }
      try
      {
         authenticator.set(wagon, new LightweightHttpWagonAuthenticator());
      }
      catch (final Exception e)
      {
         throw new ResolutionException("Could not manually set authenticator on "
                  + LightweightHttpWagon.class.getName(), e);
      }

      // SHRINKRES-69
      // Needed to ensure that we do not cache BASIC Auth values
      wagon.setPreemptiveAuthentication(true);

      return wagon;
   }

}
