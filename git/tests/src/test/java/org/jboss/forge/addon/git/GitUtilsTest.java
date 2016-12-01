/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.CherryPickResult;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Ref;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.git.exceptions.CantMergeCommitException;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 *
 */
@RunWith(Arquillian.class)
public class GitUtilsTest
{

   private ProjectFactory projectFactory;
   private GitUtils gitUtils;

   @Before
   public void setUp()
   {
      AddonRegistry addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
      this.projectFactory = addonRegistry.getServices(ProjectFactory.class).get();
      this.gitUtils = addonRegistry.getServices(GitUtils.class).get();
   }

   @Test
   public void testCreateRepo() throws Exception
   {
      Project project = projectFactory.createTempProject();

      Git repo = gitUtils.init(project.getRoot().reify(DirectoryResource.class));
      Assert.assertTrue(repo.getRepository().getDirectory().exists());
   }

   @Test
   public void testGetTags() throws Exception
   {
      Project project = projectFactory.createTempProject();

      Git repo = gitUtils.init(project.getRoot().reify(DirectoryResource.class));
      Map<String, Ref> tags = repo.getRepository().getTags();
      Assert.assertTrue(tags.isEmpty());
   }

   @Test
   public void shouldShowLocalBranch() throws Exception
   {
      List<Ref> branches = null;
      String[] commitMsgs = { "initial commit", "First commit" };
      String[] branchNames = { "master", "branch_two" };

      Project project = projectFactory.createTempProject();
      Git repo = gitUtils.init(project.getRoot().reify(DirectoryResource.class));

      gitUtils.addAll(repo);
      gitUtils.commitAll(repo, commitMsgs[0]);

      branches = gitUtils.getLocalBranches(repo);
      Assert.assertEquals("should contain only one branch", 1, branches.size());
      Assert.assertTrue("should be called master", branches.get(0).getName().endsWith(branchNames[0]));
   }

   @Test
   public void shouldReturnOneLogEntryForSingleCommit() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Git repo = gitUtils.init(project.getRoot().reify(DirectoryResource.class));
      String commitMsg = "First commit";

      gitUtils.addAll(repo);
      gitUtils.commitAll(repo, "initial commit");

      FileResource<?> file = (FileResource<?>) project.getRoot().getChild("test.txt");
      file.setContents("Foo bar baz contents").createNewFile();
      gitUtils.addAll(repo);
      gitUtils.commitAll(repo, commitMsg);

      List<String> logs = gitUtils.getLogForCurrentBranch(repo);
      Assert.assertNotNull("log should not be null", logs);
      // git log returns commits sorted by time added DESC
      Assert.assertEquals("log should contain two items", 2, logs.size());
      Assert.assertEquals("commit messages should be the same", commitMsg, logs.get(0));
   }

   @Test
   public void shouldReturnTwoLogEntriesForTwoCommits() throws Exception
   {
      boolean isCreated = false;
      String[] commitMsgs = { "initial commit", "First commit", "Second commit" };

      Project project = projectFactory.createTempProject();
      Git repo = gitUtils.init(project.getRoot().reify(DirectoryResource.class));

      gitUtils.addAll(repo);
      gitUtils.commitAll(repo, commitMsgs[0]);

      FileResource<?> file1 = project.getRoot().getChild("test.txt").reify(FileResource.class);
      isCreated = file1.createNewFile();
      Assert.assertTrue("file 1 was not created", isCreated);
      file1.setContents("Foo bar baz contents");
      gitUtils.addAll(repo);
      gitUtils.commitAll(repo, commitMsgs[1]);

      FileResource<?> file2 = project.getRoot().getChild("testTwo.txt").reify(FileResource.class);
      isCreated = file2.createNewFile();
      Assert.assertTrue("file 2 was not created", isCreated);
      file2.setContents("Foo bar baz contents");
      gitUtils.addAll(repo);
      gitUtils.commitAll(repo, commitMsgs[2]);

      List<String> logs = gitUtils.getLogForCurrentBranch(repo);
      Collections.reverse(logs); // git log returns commits sorted by time added DESC

      Assert.assertNotNull("log should not be null", logs);
      Assert.assertEquals("log should contain three items", 3, logs.size());
      Assert.assertEquals("commit messages should be the same", commitMsgs[0], logs.get(0));
      Assert.assertEquals("commit messages should be the same", commitMsgs[1], logs.get(1));
      Assert.assertEquals("commit messages should be the same", commitMsgs[2], logs.get(2));
   }

   @Test
   public void shouldThrowNoHeadExceptionWhenRepoHasNoCommits() throws Exception
   {
      Project project = projectFactory.createTempProject();
      Git repo = gitUtils.init(project.getRoot().reify(DirectoryResource.class));

      try
      {
         gitUtils.getLogForCurrentBranch(repo);
         Assert.fail("Expected " + NoHeadException.class);
      }
      catch (NoHeadException nhe)
      {
         // expected
      }
   }

   @Test
   public void shouldStashAndApplyCommit() throws Exception
   {
      boolean isCreated = false;
      String[] commitMsgs = { "initial commit", "First commit" };

      Project project = projectFactory.createTempProject();
      Git repo = gitUtils.init(project.getRoot().reify(DirectoryResource.class));

      gitUtils.addAll(repo);
      gitUtils.commitAll(repo, commitMsgs[0]);

      FileResource<?> file1 = project.getRoot().getChild("test.txt").reify(FileResource.class);
      isCreated = file1.createNewFile();
      Assert.assertTrue("file 1 was not created", isCreated);
      file1.setContents("Foo bar baz contents");

      gitUtils.addAll(repo);
      gitUtils.stashCreate(repo);

      Assert.assertTrue("should contain one stash", repo.stashList().call().iterator().hasNext());

      gitUtils.stashApply(repo);

      gitUtils.addAll(repo);
      gitUtils.commit(repo, commitMsgs[1]);

      gitUtils.stashDrop(repo);

      List<String> logs = gitUtils.getLogForCurrentBranch(repo);
      Collections.reverse(logs); // git-log shows logs in DESC order

      Assert.assertNotNull("log should not be null", logs);
      Assert.assertEquals("log should contain two items", 2, logs.size());
      Assert.assertEquals("commit messages should be the same", commitMsgs[0], logs.get(0));
      Assert.assertEquals("commit messages should be the same", commitMsgs[1], logs.get(1));

      Assert.assertFalse("should contain no stashes", repo.stashList().call().iterator().hasNext());
   }

   @Test
   public void shouldCherryPickChanges() throws Exception
   {
      // git init
      // create new branch (b2) but stay on master
      // commit new file #1
      // switch to other branch
      // commit new file #2
      // switch to master
      // cherry pick the latest commit from b2
      // verify file #2 exists
      // verify number of commits (3 on master branch)

      String[] branchNames = { "master", "branch_two" };
      String[] files = { "test1.txt", "test2.txt" };

      Project project = projectFactory.createTempProject();
      Git repo = gitUtils.init(project.getRoot().reify(DirectoryResource.class));

      gitUtils.addAll(repo);
      gitUtils.commitAll(repo, "initial commit");

      repo.branchCreate().setName(branchNames[1]).call();

      FileResource<?> file0 = project.getRoot().getChild(files[0]).reify(FileResource.class);
      file0.createNewFile();
      gitUtils.add(repo, files[0]);
      gitUtils.commit(repo, "file added on " + branchNames[0]);

      gitUtils.switchBranch(repo, branchNames[1]);

      FileResource<?> file1 = project.getRoot().getChild(files[1]).reify(FileResource.class);
      file1.createNewFile();
      gitUtils.add(repo, files[1]);
      gitUtils.commit(repo, "file added on " + branchNames[1]);

      gitUtils.getLogForCurrentBranch(repo);

      gitUtils.switchBranch(repo, branchNames[0]);
      Ref branch2Ref = repo.getRepository().findRef(branchNames[1]);
      gitUtils.cherryPick(repo, branch2Ref);

      // assert file2 exists
      Assert.assertTrue("file from cherry picked commit should exist", project.getRoot().getChild(files[1])
               .exists());

      // assert number of commits (on master). Should be 3, latest created by the merge from cherry pick
      List<String> log = gitUtils.getLogForCurrentBranch(repo);
      Assert.assertEquals("wrong number of commits", 3, log.size());
   }

   @Test
   public void shouldCherryPickChangesWithoutNewCommit() throws Exception
   {
      // git init
      // create new branch (b2) but stay on master
      // commit new file #1
      // switch to other branch
      // commit new file #2
      // switch to master
      // cherry pick (without committing) the latest commit from b2
      // verify file #2 exists
      // verify number of commits (2 on master branch)

      String[] branchNames = { "master", "branch_two" };
      String[] files = { "test1.txt", "test2.txt" };

      Project project = projectFactory.createTempProject();
      Git repo = gitUtils.init(project.getRoot().reify(DirectoryResource.class));

      gitUtils.addAll(repo);
      gitUtils.commitAll(repo, "initial commit");

      repo.branchCreate().setName(branchNames[1]).call();

      FileResource<?> file0 = project.getRoot().getChild(files[0]).reify(FileResource.class);
      file0.createNewFile();
      gitUtils.add(repo, files[0]);
      gitUtils.commit(repo, "file added on " + branchNames[0]);

      gitUtils.switchBranch(repo, branchNames[1]);

      FileResource<?> file1 = project.getRoot().getChild(files[1]).reify(FileResource.class);
      file1.createNewFile();
      gitUtils.add(repo, files[1]);
      gitUtils.commit(repo, "file added on " + branchNames[1]);

      gitUtils.getLogForCurrentBranch(repo);

      gitUtils.switchBranch(repo, branchNames[0]);
      Ref branch2Ref = repo.getRepository().findRef(branchNames[1]);
      gitUtils.cherryPickNoMerge(repo, branch2Ref);

      // assert file2 exists
      Assert.assertTrue("file from cherry picked commit should exist", project.getRoot().getChild(files[1])
               .exists());

      // assert number of commits (on master). Should be 2 (cherry pick produced no merge)
      List<String> log = gitUtils.getLogForCurrentBranch(repo);
      Assert.assertEquals("wrong number of commits", 2, log.size());
   }

   @Test
   public void shouldDiscardLastCommit() throws Exception
   {
      // git init
      // commit new file1
      // verify number of commits
      // commit new file2
      // verify number of commits
      // reset hard to the prev commit
      // verify number of commits
      // verify file2 doesn't exist

      List<String> log = null;
      String[] branchNames = { "master" };
      String[] files = { "test1.txt", "test2.txt" };

      Project project = projectFactory.createTempProject();
      Git repo = gitUtils.init(project.getRoot().reify(DirectoryResource.class));

      gitUtils.addAll(repo);
      gitUtils.commitAll(repo, "initial commit");

      FileResource<?> file0 = project.getRoot().getChild(files[0]).reify(FileResource.class);
      file0.createNewFile();
      gitUtils.add(repo, files[0]);
      gitUtils.commit(repo, "file added on " + branchNames[0]);

      log = gitUtils.getLogForCurrentBranch(repo);
      Assert.assertEquals("wrong number of commits", 2, log.size());
      Assert.assertTrue("file should exist", project.getRoot().getChild(files[0]).exists());

      FileResource<?> file1 = project.getRoot().getChild(files[1]).reify(FileResource.class);
      file1.createNewFile();
      gitUtils.add(repo, files[1]);
      gitUtils.commit(repo, "file added on " + branchNames[0]);

      log = gitUtils.getLogForCurrentBranch(repo);
      Assert.assertEquals("wrong number of commits", 3, log.size());
      Assert.assertTrue("file should exist", project.getRoot().getChild(files[1]).exists());

      gitUtils.resetHard(repo, "HEAD^1");
      log = gitUtils.getLogForCurrentBranch(repo);
      Assert.assertEquals("wrong number of commits", 2, log.size());
      Assert.assertTrue("file should exist", project.getRoot().getChild(files[0]).exists());
      Assert.assertFalse("file should not exist", project.getRoot().getChild(files[1]).exists());
   }

   @Test
   public void shouldCreateNewBranch() throws Exception
   {
      // git init
      // create new branch
      // verify branch exists

      String testBranchName = "testBranch";
      Project project = projectFactory.createTempProject();
      Git repo = gitUtils.init(project.getRoot().reify(DirectoryResource.class));

      gitUtils.addAll(repo);
      gitUtils.commitAll(repo, "initial commit");

      Ref testBranch = gitUtils.createBranch(repo, testBranchName);
      Assert.assertNotNull(testBranch);

      List<Ref> branches = gitUtils.getLocalBranches(repo);
      Assert.assertTrue("Branch is not created", branches.contains(testBranch));
   }

   @Test
   public void shouldNotCrashWhenCherryPickNoMergeIsCalledOnLastCommit() throws Exception
   {
      String[] branchNames = { "master" };
      String[] files = { "test1.txt" };
      List<String> commits = null;
      CherryPickResult cherryPickResult = null;

      Project project = projectFactory.createTempProject();
      Git repo = gitUtils.init(project.getRoot().reify(DirectoryResource.class));

      gitUtils.addAll(repo);
      gitUtils.commitAll(repo, "initial commit");

      FileResource<?> file0 = project.getRoot().getChild(files[0]).reify(FileResource.class);
      file0.createNewFile();
      gitUtils.add(repo, files[0]);
      gitUtils.commit(repo, "file added on " + branchNames[0]);

      commits = gitUtils.getLogForCurrentBranch(repo);
      Assert.assertEquals("Wrong number of commits in log", 2, commits.size());
      cherryPickResult = gitUtils.cherryPickNoMerge(repo, repo.getRepository().findRef(branchNames[0]));
      Assert.assertEquals("Wrong cherrypick status", CherryPickResult.CherryPickStatus.OK,
               cherryPickResult.getStatus());
      gitUtils.resetHard(repo, "HEAD^1");

      commits = gitUtils.getLogForCurrentBranch(repo);
      Assert.assertEquals("Wrong number of commits in log", 1, commits.size());
      try
      {
         gitUtils.cherryPickNoMerge(repo, repo.getRepository().findRef(branchNames[0]));
         Assert.fail("Expected exception: " + CantMergeCommitException.class);
      }
      catch (CantMergeCommitException cmce)
      {
         // Expected
      }
   }
}
