/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.CherryPickResult;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.FetchResult;
import org.jboss.forge.addon.git.exceptions.CantMergeCommitException;
import org.jboss.forge.addon.resource.DirectoryResource;

/**
 * Forge's wrapper on top of the JGit API.
 * <p>
 * Most of the JGit operations require too much ceremony: calling a lot of setter methods before doing the real
 * execution. This API encapsulates all the possible parameters as part of the method parameters and does all the setter
 * calls itself.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 */
public interface GitUtils
{

   /**
    * Clones a GIT repository.
    * 
    * @param dir The target location where the GIT repository will be cloned. It should not exist, or if exists, it
    *           should be an empty directory.
    * @param repoUri The URI pointing to the source of the repository which will be cloned.
    * @return A JGit API handle to the GIT object pointing to the cloned repository.
    * @throws GitAPIException Thrown when there are problems cloning the repository.
    */
   Git clone(final DirectoryResource dir, final String repoUri) throws GitAPIException;

   /**
    * Returns a handle to an existing Git repository.
    * 
    * @param dir The directory containing the GIT repository
    * @return A JGit API handle to the specified directory.
    * @throws IOException Thrown when there are problems accessing the specified directory.
    */
   Git git(final DirectoryResource dir) throws IOException;

   /**
    * Checks out or creates a given GIT branch.
    * 
    * @param git A JGit API handle to the directory, where branches will be checked out.
    * @param remote The name of the branch that will be created/checked out.
    * @param createBranch If true, the specified branch will be created
    * @param mode When creating a branch, specifies whether it should track its remote branch as upstream.
    * @param force If <code>true</code>, forces resetting the starting point of the newly switched branch.
    * @return A <code>Ref</code> object (part of the JGit API), pointing to the checked out branch.
    * @throws GitAPIException Thrown when there are problems checking out the new branch.
    */
   Ref checkout(final Git git, final String remote, final boolean createBranch,
            final SetupUpstreamMode mode, final boolean force) throws GitAPIException;

   /**
    * Checks out an already existing branch.
    * 
    * @param git A JGit API handle to the directory, where branches will be checked out.
    * @param localRef A GIT object pointing to the branch to be checked out.
    * @param mode When creating a branch, specifies whether it should track its remote branch as upstream.
    * @param force If <code>true</code>, forces resetting the starting point of the newly switched branch.
    * @return A <code>Ref</code> object (part of the JGit API), pointing to the checked out branch.
    * @throws GitAPIException Thrown when there are problems checking out the new branch.
    */
   Ref checkout(final Git git, final Ref localRef, final SetupUpstreamMode mode,
            final boolean force) throws GitAPIException;

   /**
    * Fetches the Git objects from a remote repository.
    * 
    * @param git A JGit API handle to the repository, where the refs will be fetched to.
    * @param remote A URI or name of a remote branch, where the refs will be fetched from.
    * @param refSpec A space-separated list of <code>ref specs</code>. May be <code>null</code>.
    * @param timeout If greater than 0, a timeout for the fetch operation.
    * @param fsck If set to <code>true</code>, objects that will be fetched will be checked for validity.
    * @param dryRun If set to <code>true</code>, this fetch is marked as a <i>dry run</i>.
    * @param thin Sets the thin-pack reference for this fetch operation.
    * @param prune If set to <code>true</code> the objects that do not exist anymore, will be removed in the source.
    * @return A <code>FetchResult</code> object (part of the JGit API), specifying the status of this fetch operation.
    * @throws GitAPIException Thrown when there are problems fetching the refs
    */
   FetchResult fetch(final Git git, final String remote, final String refSpec, final int timeout,
            final boolean fsck, final boolean dryRun,
            final boolean thin, final boolean prune) throws GitAPIException;

   /**
    * Initializes a new GIT repository.
    * 
    * @param dir The directory in which to create a new .git/ folder and repository.
    * @return A JGit API handle to the specified directory.
    * @throws IOException Thrown when there are problems accessing the specified directory.
    */
   Git init(final DirectoryResource dir) throws IOException;

   /**
    * Pulls the latest changes from the remote repository and incorporates them into the given one.
    * 
    * @param git A JGit API handle to the repository, where the changes will be pulled to.
    * @param timeout If greater than 0, the timeout, used for the fetching step
    * @return A <code>PullResult</code> object (part of the JGit API), that specifies the result of the pull action.
    * @throws GitAPIException Thrown when there are problems pulling the latest changing
    */
   PullResult pull(final Git git, final int timeout) throws GitAPIException;

   /**
    * Returns all the remote branches of a given GIT repository.
    * 
    * @param repo A JGit API handle to the repository, which remote branches will be listed.
    * @return A list of <code>Ref</code> objects (part of the JGit API), that identify the remote branches for the given
    *         repository.
    * @throws GitAPIException Thrown when there are problems obtaining the list of remote branches.
    */
   List<Ref> getRemoteBranches(final Git repo) throws GitAPIException;

   /**
    * Returns all the local branches of a given GIT repository.
    * 
    * @param repo A JGit API handle to the repository, which local branches will be listed.
    * @return A list of <code>Ref</code> objects (part of the JGit API), that identify the local branches for the given
    *         repository.
    * @throws GitAPIException Thrown when there are problems obtaining the list of local branches.
    */
   List<Ref> getLocalBranches(final Git repo) throws GitAPIException;

   /**
    * Returns the name of the branch that is currently checked out in the given repository.
    * 
    * @param repo A JGit API handle to the repository.
    * @return The name of the currently checked out branch in the given repository.
    * @throws IOException If there are problems accessing the directory of the GIT repository.
    */
   String getCurrentBranchName(final Git repo) throws IOException;

   /**
    * Changes the currently checked out branch of a given repository.
    * 
    * @param repo A JGit API handle to the repository, where the new branch will be checked out.
    * @param branchName The name of the branch which will be checked out.
    * @return A <code>Ref</code> object (part of the JGit API), pointing to the checked out branch.
    */
   Ref switchBranch(final Git repo, final String branchName);

   /**
    * Returns a list of the messages of all the commits in the current branch.
    * 
    * @param repo A JGit API handle to the repository, which commit messages will be listed.
    * @return A list of all the commit messages for the current branch
    * @throws GitAPIException Thrown when there are problems obtaining the list of the commit messages.
    */
   List<String> getLogForCurrentBranch(final Git repo) throws GitAPIException;

   /**
    * Returns a list of the messages of all the commits for a given branch.
    * 
    * @param repo A JGit API handle to the repository, which commit messages will be listed.
    * @param branchName The branch which log messages will be returned.
    * @return A list of all the commit messages for the given branch.
    * @throws GitAPIException Thrown when there are problems obtaining the list of the commit messages.
    */
   List<String> getLogForBranch(final Git repo, String branchName) throws GitAPIException,
            IOException;

   /**
    * Stages the files matching the given pattern from the working tree to the GIT index.
    * 
    * @param repo The JGit API handle to the repository, where files will be added.
    * @param filePattern The file to be added to the index. If a directory is passed, its content will be added
    *           recursively.
    * @throws GitAPIException Thrown when there are problems adding files to the GIT index.
    */
   void add(final Git repo, String filePattern) throws GitAPIException;

   /**
    * Stages all the new, modified and deleted files from the working tree to the GIT index.
    * 
    * @param repo The JGit API handle to the repository, where files will be added.
    * @throws GitAPIException Thrown when there are problems adding files to the GIT index.
    */
   void addAll(final Git repo) throws GitAPIException;

   /**
    * Creates a new commit with the current contents of the index along with a log message.
    * 
    * @param repo The JGit API handle to the repository, where the commit will be created.
    * @param message The commit message.
    * @throws GitAPIException Thrown when there are problems creating the commit.
    */
   void commit(final Git repo, String message) throws GitAPIException;

   /**
    * Stages all the new, modified and deleted files to the index and creates a commit from its current contents.
    * 
    * @param repo The JGit API handle to the repository, where the commit will be created.
    * @param message The commit message.
    * @throws GitAPIException Thrown when there are problems creating the commit.
    */
   void commitAll(final Git repo, String message) throws GitAPIException;

   /**
    * Stashes the contents on the working directory and index in separate commits and resets to the current HEAD commit.
    * 
    * @param repo The JGit API handle to the repository, where the stash action will be performed.
    * @throws GitAPIException Thrown when there are problems performing the stash action.
    */
   void stashCreate(final Git repo) throws GitAPIException;

   /**
    * Applies the changes in a stashed commit to the working directory and index.
    * 
    * @param repo The JGit API handle to the repository, where the stash action will be performed.
    * @param stashRef The stash reference to apply.
    * @throws GitAPIException Thrown when there are problems performing the stash action.
    */
   void stashApply(final Git repo, String... stashRef) throws GitAPIException;

   /**
    * Drops the last stashed commit.
    * 
    * @param repo The JGit API handle to the repository, where the stash action will be performed.
    * @throws GitAPIException Thrown when there are problems performing the stash action.
    */
   void stashDrop(final Git repo) throws GitAPIException;

   /**
    * Cherry-picks a given commit to the current HEAD.
    * 
    * @param repo The JGit API handle to the repository, where the cherry-pick action will be performed.
    * @param commit A reference to the commit, which will be cherry-picked to the current HEAD.
    * @throws GitAPIException Thrown when there are problems performing the cherry-pick action.
    */
   void cherryPick(final Git repo, Ref commit) throws GitAPIException;

   /**
    * Does the same as the original cherry-pick except committing after running merger.
    * 
    * @param repo The JGit API handle to the repository, where the cherry-pick action will be performed.
    * @param commit A reference to the commit, which will be cherry-picked to the current HEAD.
    * @return A <code>CherryPickResult</code> object (part of the JGit API), encapsulating the result of the cherry-pick
    *         command.
    * @throws GitAPIException Thrown when there are problems performing the cherry-pick action.
    * @throws CantMergeCommitException Thrown when the commit to cherry-pick has no parents.
    */
   CherryPickResult cherryPickNoMerge(final Git git, Ref commit) throws GitAPIException,
            CantMergeCommitException;

   /**
    * Resets the HEAD, the index and the working tree to point to the given commit.
    * 
    * @param repo The JGit API handle to the repository, which will be reset.
    * @param newBase The commit to which the HEAD will be reset.
    * @throws GitAPIException Thrown when there are problems performing the reset action.
    */
   void resetHard(final Git repo, String newBase) throws GitAPIException;

   /**
    * Creates a new branch in the given GIT repository and checks it out.
    * 
    * @param repo The JGit API handle to the repository, where a new branch will be created.
    * @param branchName The name of the branch that will be created.
    * @return A <code>Ref</code> object (part of the JGit API), pointing to the created branch.
    * @throws GitAPIException Thrown when there are problems creating the new branch.
    */
   Ref createBranch(Git git, String branchName) throws GitAPIException;

   /**
    * Closes the resources and decrements the use count of the given repository.
    * 
    * @param repo The repository where the close action will be performed.
    */
   void close(final Git repo);

}
