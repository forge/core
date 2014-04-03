/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.git;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CherryPickResult;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.MultipleParentsNotAllowedException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.merge.MergeMessageFormatter;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.merge.ResolveMerger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.jboss.forge.addon.git.exceptions.CantMergeCommitException;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.util.Strings;

/**
 * Convenience tools for interacting with the Git version control system.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 *
 */
public class GitUtilsImpl implements GitUtils
{
   
   @Override
   public Git clone(final DirectoryResource dir, final String repoUri) throws GitAPIException
   {
      CloneCommand clone = Git.cloneRepository().setURI(repoUri)
               .setDirectory(dir.getUnderlyingResourceObject());
      Git git = clone.call();
      return git;
   }

   @Override
   public Git git(final DirectoryResource dir) throws IOException
   {
      RepositoryBuilder db = new RepositoryBuilder().findGitDir(dir.getUnderlyingResourceObject());
      return new Git(db.build());
   }

   @Override
   public Ref checkout(final Git git, final String remote, final boolean createBranch,
            final SetupUpstreamMode mode, final boolean force)
            throws GitAPIException
   {
      CheckoutCommand checkout = git.checkout();
      checkout.setCreateBranch(createBranch);
      checkout.setName(remote);
      checkout.setForce(force);
      checkout.setUpstreamMode(mode);
      return checkout.call();
   }

   @Override
   public Ref checkout(final Git git, final Ref localRef, final SetupUpstreamMode mode, 
            final boolean force) throws GitAPIException
   {
      CheckoutCommand checkout = git.checkout();
      checkout.setName(Repository.shortenRefName(localRef.getName()));
      checkout.setForce(force);
      checkout.setUpstreamMode(mode);
      return checkout.call();
   }

   @Override
   public FetchResult fetch(final Git git, final String remote, final String refSpec, final int timeout,
            final boolean fsck, final boolean dryRun,
            final boolean thin,
            final boolean prune) throws GitAPIException
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

   @Override
   public Git init(final DirectoryResource dir) throws IOException
   {
      FileResource<?> gitDir = dir.getChildDirectory(".git").reify(FileResource.class);
      gitDir.mkdirs();

      RepositoryBuilder db = new RepositoryBuilder().setGitDir(gitDir.getUnderlyingResourceObject()).setup();
      Git git = new Git(db.build());
      git.getRepository().create();
      return git;
   }

   @Override
   public PullResult pull(final Git git, final int timeout) throws GitAPIException
   {
      PullCommand pull = git.pull();
      if (timeout >= 0)
         pull.setTimeout(timeout);
      pull.setProgressMonitor(new TextProgressMonitor());

      PullResult result = pull.call();
      return result;
   }

   @Override
   public List<Ref> getRemoteBranches(final Git repo) throws GitAPIException
   {
      List<Ref> results = new ArrayList<>();
      try
      {
         FetchResult fetch = repo.fetch().setRemote("origin").call();
         Collection<Ref> refs = fetch.getAdvertisedRefs();
         for (Ref ref : refs)
         {
            if (ref.getName().startsWith("refs/heads"))
            {
               results.add(ref);
            }
         }
      }
      catch (InvalidRemoteException e)
      {
         e.printStackTrace();
      }

      return results;
   }

   @Override
   public List<Ref> getLocalBranches(final Git repo) throws GitAPIException
   {
      // returns only local branches by default
      return repo.branchList().call();
   }

   @Override
   public String getCurrentBranchName(final Git repo) throws IOException
   {
      return repo.getRepository().getBranch();
   }

   @Override
   public Ref switchBranch(final Git repo, final String branchName)
   {
      Ref switchedBranch = null;
      try
      {
         switchedBranch = repo.checkout().setName(branchName).call();
         if (switchedBranch == null)
            throw new RuntimeException("Couldn't switch to branch " + branchName);
      }
      catch (GitAPIException e)
      {
         e.printStackTrace();
      }

      return switchedBranch;
   }

   @Override
   public List<String> getLogForCurrentBranch(final Git repo) throws GitAPIException
   {
      List<String> results = new ArrayList<>();
      Iterable<RevCommit> commits = repo.log().call();

      for (RevCommit commit : commits)
         results.add(commit.getFullMessage());

      return results;
   }

   @Override
   public List<String> getLogForBranch(final Git repo, String branchName) throws GitAPIException,
            IOException
   {
      String oldBranch = repo.getRepository().getBranch();
      repo.checkout().setName(branchName).call();

      List<String> results = getLogForCurrentBranch(repo);

      repo.checkout().setName(oldBranch).call();

      return results;
   }

   @Override
   public void add(final Git repo, String filepattern) throws GitAPIException
   {
      repo.add().addFilepattern(filepattern).call();
   }

   @Override
   public void addAll(final Git repo) throws GitAPIException
   {
      repo.add().addFilepattern(".").call();
   }

   @Override
   public void commit(final Git repo, String message) throws GitAPIException
   {
      repo.commit().setMessage(message).call();
   }

   @Override
   public void commitAll(final Git repo, String message) throws GitAPIException
   {
      repo.commit().setMessage(message).setAll(true).call();
   }

   @Override
   public void stashCreate(final Git repo) throws GitAPIException
   {
      repo.stashCreate().call();
   }

   @Override
   public void stashApply(final Git repo, String... stashRef) throws GitAPIException
   {
      if (stashRef.length >= 1 && !Strings.isNullOrEmpty(stashRef[0]))
      {
         repo.stashApply().setStashRef(stashRef[0]).call();
      }
      else
      {
         repo.stashApply().call();
      }
   }

   @Override
   public void stashDrop(final Git repo) throws GitAPIException
   {
      repo.stashDrop().call();
   }

   @Override
   public void cherryPick(final Git repo, Ref commit) throws GitAPIException
   {
      repo.cherryPick().include(commit).call();
   }

   @Override
   public CherryPickResult cherryPickNoMerge(final Git git, Ref src) throws GitAPIException,
            CantMergeCommitException
   {
      // Does the same as the original git-cherryPick
      // except commiting after running merger
      Repository repo = git.getRepository();

      RevCommit newHead = null;
      List<Ref> cherryPickedRefs = new LinkedList<Ref>();

      RevWalk revWalk = new RevWalk(repo);
      try
      {
         // get the head commit
         Ref headRef = repo.getRef(Constants.HEAD);
         if (headRef == null)
            throw new NoHeadException(
                     JGitText.get().commitOnRepoWithoutHEADCurrentlyNotSupported);
         RevCommit headCommit = revWalk.parseCommit(headRef.getObjectId());

         newHead = headCommit;

         // get the commit to be cherry-picked
         // handle annotated tags
         ObjectId srcObjectId = src.getPeeledObjectId();
         if (srcObjectId == null)
            srcObjectId = src.getObjectId();
         RevCommit srcCommit = revWalk.parseCommit(srcObjectId);

         // get the parent of the commit to cherry-pick
         if (srcCommit.getParentCount() == 0)
            throw new CantMergeCommitException("Commit with zero parents cannot be merged");

         if (srcCommit.getParentCount() > 1)
            throw new MultipleParentsNotAllowedException(
                     MessageFormat.format(
                              JGitText.get().canOnlyCherryPickCommitsWithOneParent,
                              srcCommit.name(),
                              Integer.valueOf(srcCommit.getParentCount())));

         RevCommit srcParent = srcCommit.getParent(0);
         revWalk.parseHeaders(srcParent);

         ResolveMerger merger = (ResolveMerger) MergeStrategy.RESOLVE
                  .newMerger(repo);
         merger.setWorkingTreeIterator(new FileTreeIterator(repo));
         merger.setBase(srcParent.getTree());
         if (merger.merge(headCommit, srcCommit))
         {
            DirCacheCheckout dco = new DirCacheCheckout(repo,
                     headCommit.getTree(), repo.lockDirCache(),
                     merger.getResultTreeId());
            dco.setFailOnConflict(true);
            dco.checkout();

            cherryPickedRefs.add(src);
         }
         else
         {
            if (merger.failed())
               return new CherryPickResult(merger.getFailingPaths());

            // there are merge conflicts
            String message = new MergeMessageFormatter()
                     .formatWithConflicts(srcCommit.getFullMessage(),
                              merger.getUnmergedPaths());

            repo.writeCherryPickHead(srcCommit.getId());
            repo.writeMergeCommitMsg(message);

            return CherryPickResult.CONFLICT;
         }
      }
      catch (IOException e)
      {
         throw new JGitInternalException(
                  MessageFormat.format(
                           JGitText.get().exceptionCaughtDuringExecutionOfCherryPickCommand,
                           e), e);
      }
      finally
      {
         revWalk.release();
      }

      return new CherryPickResult(newHead, cherryPickedRefs);
   }

   @Override
   public void resetHard(final Git repo, String newBase) throws GitAPIException
   {
      repo.reset().setMode(ResetCommand.ResetType.HARD).setRef(newBase).call();
   }

   @Override
   public Ref createBranch(Git git, String branchName) throws GitAPIException
   {
      Ref newBranch = git.branchCreate().setName(branchName).call();

      if (newBranch == null)
         throw new RuntimeException("Couldn't create new branch " + branchName);

      return newBranch;
   }

   @Override
   public void close(final Git repo)
   {
      repo.getRepository().close();
   }
}
