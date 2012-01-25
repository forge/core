/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.InvalidTagNameException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;

/**
 * Convenience tools for interacting with the Git version control system.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class GitUtils
{
   public static Git clone(final DirectoryResource dir, final String repoUri) throws IOException
   {
      CloneCommand clone = Git.cloneRepository().setURI(repoUri)
               .setDirectory(dir.getUnderlyingResourceObject());
      Git git = clone.call();
      return git;
   }

   public static Git git(final DirectoryResource dir) throws IOException
   {
      RepositoryBuilder db = new RepositoryBuilder().findGitDir(dir.getUnderlyingResourceObject());
      return new Git(db.build());
   }

   public static Ref checkout(final Git git, final String remote, final boolean createBranch,
            final SetupUpstreamMode mode, final boolean force)
            throws JGitInternalException,
            RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException
   {
      CheckoutCommand checkout = git.checkout();
      checkout.setCreateBranch(createBranch);
      checkout.setName(remote);
      checkout.setForce(force);
      checkout.setUpstreamMode(mode);
      return checkout.call();
   }

   public static Ref checkout(final Git git, final Ref localRef, final boolean createBranch,
            final SetupUpstreamMode mode, final boolean force)
            throws JGitInternalException,
            RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException
   {
      CheckoutCommand checkout = git.checkout();
      checkout.setName(Repository.shortenRefName(localRef.getName()));
      checkout.setForce(force);
      checkout.setUpstreamMode(mode);
      return checkout.call();
   }

   public static FetchResult fetch(final Git git, final String remote, final String refSpec, final int timeout,
            final boolean fsck, final boolean dryRun,
            final boolean thin,
            final boolean prune) throws JGitInternalException, InvalidRemoteException
   {
      FetchCommand fetch = git.fetch();
      fetch.setCheckFetchedObjects(fsck);
      fetch.setRemoveDeletedRefs(prune);
      if (refSpec != null)
         fetch.setRefSpecs(new RefSpec(refSpec));
      if (timeout >= 0)
         fetch.setTimeout(timeout);
      fetch.setDryRun(dryRun);
      fetch.setRemote(remote);
      fetch.setThin(thin);
      fetch.setProgressMonitor(new TextProgressMonitor());

      FetchResult result = fetch.call();
      return result;
   }

   /**
    * Initialize a new git repository.
    * 
    * @param dir The directory in which to create a new .git/ folder and repository.
    */
   public static Git init(final DirectoryResource dir) throws IllegalArgumentException, IOException
   {
      FileResource<?> gitDir = dir.getChildDirectory(".git").reify(FileResource.class);
      gitDir.mkdirs();

      RepositoryBuilder db = new RepositoryBuilder().setGitDir(gitDir.getUnderlyingResourceObject()).setup();
      Git git = new Git(db.build());
      git.getRepository().create();
      return git;
   }

   public static PullResult pull(final Git git, final int timeout) throws WrongRepositoryStateException,
            InvalidConfigurationException, DetachedHeadException, InvalidRemoteException, CanceledException,
            RefNotFoundException, NoHeadException
   {
      PullCommand pull = git.pull();
      if (timeout >= 0)
         pull.setTimeout(timeout);
      pull.setProgressMonitor(new TextProgressMonitor());

      PullResult result = pull.call();
      return result;
   }

   public static List<Ref> getRemoteBranches(final Git repo) throws JGitInternalException,
            ConcurrentRefUpdateException,
            InvalidTagNameException, NoHeadException
   {
      List<Ref> results = new ArrayList<Ref>();
      try {
         FetchResult fetch = repo.fetch().setRemote("origin").call();
         Collection<Ref> refs = fetch.getAdvertisedRefs();
         for (Ref ref : refs) {
            if (ref.getName().startsWith("refs/heads"))
            {
               results.add(ref);
            }
         }
      }
      catch (InvalidRemoteException e) {
         e.printStackTrace();
      }

      return results;
   }
}
