/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.facets;

import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.AuthenticationSelector;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.repository.DefaultAuthenticationSelector;
import org.sonatype.aether.util.repository.DefaultMirrorSelector;

/**
 * An {@link AuthenticationSelector} that resolves the Authentication info lazily at runtime. This selector determines
 * whether a remote repository is mirrored and then returns the authentication info for the mirror. If no mirror exists,
 * the authentication info for the remote repository is returned.
 */
final class LazyAuthenticationSelector extends DefaultAuthenticationSelector implements AuthenticationSelector
{
   private final DefaultMirrorSelector mirrorSelector;

   LazyAuthenticationSelector(DefaultMirrorSelector mirrorSelector)
   {
      this.mirrorSelector = mirrorSelector;
   }

   @Override
   public Authentication getAuthentication(RemoteRepository repository)
   {
      RemoteRepository mirror = mirrorSelector.getMirror(repository);
      if (mirror != null)
      {
         return super.getAuthentication(mirror);
      }
      return super.getAuthentication(repository);
   }
}