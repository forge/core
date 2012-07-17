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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Ref;
import org.jboss.forge.project.Project;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 * 
 */
public class GitUtilsTest extends AbstractShellTest
{
   @Test
   public void testCreateRepo() throws Exception
   {
      Project project = initializeJavaProject();

      Git repo = GitUtils.init(project.getProjectRoot());
      Assert.assertTrue(repo.getRepository().getDirectory().exists());
   }

   @Test
   public void testGetTags() throws Exception
   {
      Project project = initializeJavaProject();

      Git repo = GitUtils.init(project.getProjectRoot());
      Map<String, Ref> tags = repo.getRepository().getTags();
      Assert.assertTrue(tags.isEmpty());
   }

   @Test
   public void shouldShowLocalBranch() throws Exception
   {
      List<Ref> branches = null;
      String[] commitMsgs = { "initial commit", "First commit" };
      String[] branchNames = { "master", "branch_two" };

      Project project = initializeJavaProject();
      Git repo = GitUtils.init(project.getProjectRoot());

      GitUtils.addAll(repo);
      GitUtils.commitAll(repo, commitMsgs[0]);

      branches = GitUtils.getLocalBranches(repo);
      Assert.assertEquals("should contain only one branch", 1, branches.size());
      Assert.assertTrue("should be called master", branches.get(0).getName().endsWith(branchNames[0]));
   }

   @Test
   public void shouldReturnOneLogEntryForSingleCommit() throws Exception
   {
      Project project = initializeJavaProject();
      Git repo = GitUtils.init(project.getProjectRoot());
      String commitMsg = "First commit";

      GitUtils.addAll(repo);
      GitUtils.commitAll(repo, "initial commit");

      FileResource<?> file = (FileResource<?>) project.getProjectRoot().getChild("test.txt");
      file.setContents("Foo bar baz contents").createNewFile();
      GitUtils.addAll(repo);
      GitUtils.commitAll(repo, commitMsg);

      List<String> logs = GitUtils.getLogForCurrentBranch(repo);
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

      Project project = initializeJavaProject();
      Git repo = GitUtils.init(project.getProjectRoot());

      GitUtils.addAll(repo);
      GitUtils.commitAll(repo, commitMsgs[0]);

      FileResource<?> file1 = project.getProjectRoot().getChild("test.txt").reify(FileResource.class);
      isCreated = file1.createNewFile();
      Assert.assertTrue("file 1 was not created", isCreated);
      file1.setContents("Foo bar baz contents");
      GitUtils.addAll(repo);
      GitUtils.commitAll(repo, commitMsgs[1]);

      FileResource<?> file2 = project.getProjectRoot().getChild("testTwo.txt").reify(FileResource.class);
      isCreated = file2.createNewFile();
      Assert.assertTrue("file 2 was not created", isCreated);
      file2.setContents("Foo bar baz contents");
      GitUtils.addAll(repo);
      GitUtils.commitAll(repo, commitMsgs[2]);

      List<String> logs = GitUtils.getLogForCurrentBranch(repo);
      Collections.reverse(logs); // git log returns commits sorted by time added DESC

      Assert.assertNotNull("log should not be null", logs);
      Assert.assertEquals("log should contain three items", 3, logs.size());
      Assert.assertEquals("commit messages should be the same", commitMsgs[0], logs.get(0));
      Assert.assertEquals("commit messages should be the same", commitMsgs[1], logs.get(1));
      Assert.assertEquals("commit messages should be the same", commitMsgs[2], logs.get(2));
   }

   @Test(expected = NoHeadException.class)
   public void shouldThrowNoHeadExceptionWhenRepoHasNoCommits() throws Exception
   {
      Project project = initializeJavaProject();
      Git repo = GitUtils.init(project.getProjectRoot());

      GitUtils.getLogForCurrentBranch(repo);
   }

   @Test
   public void shouldStashAndApplyCommit() throws Exception
   {
      boolean isCreated = false;
      String[] commitMsgs = { "initial commit", "First commit" };

      Project project = initializeJavaProject();
      Git repo = GitUtils.init(project.getProjectRoot());

      GitUtils.addAll(repo);
      GitUtils.commitAll(repo, commitMsgs[0]);

      FileResource<?> file1 = project.getProjectRoot().getChild("test.txt").reify(FileResource.class);
      isCreated = file1.createNewFile();
      Assert.assertTrue("file 1 was not created", isCreated);
      file1.setContents("Foo bar baz contents");

      GitUtils.addAll(repo);
      GitUtils.stashCreate(repo);

      Assert.assertTrue("should contain one stash", repo.stashList().call().iterator().hasNext());

      GitUtils.stashApply(repo);

      GitUtils.addAll(repo);
      GitUtils.commit(repo, commitMsgs[1]);

      GitUtils.stashDrop(repo);

      List<String> logs = GitUtils.getLogForCurrentBranch(repo);
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

      Project project = initializeJavaProject();
      Git repo = GitUtils.init(project.getProjectRoot());

      GitUtils.addAll(repo);
      GitUtils.commitAll(repo, "initial commit");

      repo.branchCreate().setName(branchNames[1]).call();

      FileResource<?> file0 = project.getProjectRoot().getChild(files[0]).reify(FileResource.class);
      file0.createNewFile();
      GitUtils.add(repo, files[0]);
      GitUtils.commit(repo, "file added on " + branchNames[0]);

      GitUtils.switchBranch(repo, branchNames[1]);

      FileResource<?> file1 = project.getProjectRoot().getChild(files[1]).reify(FileResource.class);
      file1.createNewFile();
      GitUtils.add(repo, files[1]);
      GitUtils.commit(repo, "file added on " + branchNames[1]);

      GitUtils.getLogForCurrentBranch(repo);

      GitUtils.switchBranch(repo, branchNames[0]);
      Ref branch2Ref = repo.getRepository().getRef(branchNames[1]);
      GitUtils.cherryPick(repo, branch2Ref);

      // assert file2 exists
      Assert.assertTrue("file from cherry picked commit should exist", project.getProjectRoot().getChild(files[1])
               .exists());

      // assert number of commits (on master). Should be 3, latest created by the merge from cherry pick
      List<String> log = GitUtils.getLogForCurrentBranch(repo);
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

      Project project = initializeJavaProject();
      Git repo = GitUtils.init(project.getProjectRoot());

      GitUtils.addAll(repo);
      GitUtils.commitAll(repo, "initial commit");

      repo.branchCreate().setName(branchNames[1]).call();

      FileResource<?> file0 = project.getProjectRoot().getChild(files[0]).reify(FileResource.class);
      file0.createNewFile();
      GitUtils.add(repo, files[0]);
      GitUtils.commit(repo, "file added on " + branchNames[0]);

      GitUtils.switchBranch(repo, branchNames[1]);

      FileResource<?> file1 = project.getProjectRoot().getChild(files[1]).reify(FileResource.class);
      file1.createNewFile();
      GitUtils.add(repo, files[1]);
      GitUtils.commit(repo, "file added on " + branchNames[1]);

      GitUtils.getLogForCurrentBranch(repo);

      GitUtils.switchBranch(repo, branchNames[0]);
      Ref branch2Ref = repo.getRepository().getRef(branchNames[1]);
      GitUtils.cherryPickNoMerge(repo, branch2Ref);

      // assert file2 exists
      Assert.assertTrue("file from cherry picked commit should exist", project.getProjectRoot().getChild(files[1])
               .exists());

      // assert number of commits (on master). Should be 2 (cherry pick produced no merge)
      List<String> log = GitUtils.getLogForCurrentBranch(repo);
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

      Project project = initializeJavaProject();
      Git repo = GitUtils.init(project.getProjectRoot());

      GitUtils.addAll(repo);
      GitUtils.commitAll(repo, "initial commit");

      FileResource<?> file0 = project.getProjectRoot().getChild(files[0]).reify(FileResource.class);
      file0.createNewFile();
      GitUtils.add(repo, files[0]);
      GitUtils.commit(repo, "file added on " + branchNames[0]);

      log = GitUtils.getLogForCurrentBranch(repo);
      Assert.assertEquals("wrong number of commits", 2, log.size());
      Assert.assertTrue("file should exist", project.getProjectRoot().getChild(files[0]).exists());

      FileResource<?> file1 = project.getProjectRoot().getChild(files[1]).reify(FileResource.class);
      file1.createNewFile();
      GitUtils.add(repo, files[1]);
      GitUtils.commit(repo, "file added on " + branchNames[0]);

      log = GitUtils.getLogForCurrentBranch(repo);
      Assert.assertEquals("wrong number of commits", 3, log.size());
      Assert.assertTrue("file should exist", project.getProjectRoot().getChild(files[1]).exists());

      GitUtils.resetHard(repo, "HEAD^1");
      log = GitUtils.getLogForCurrentBranch(repo);
      Assert.assertEquals("wrong number of commits", 2, log.size());
      Assert.assertTrue("file should exist", project.getProjectRoot().getChild(files[0]).exists());
      Assert.assertFalse("file should not exist", project.getProjectRoot().getChild(files[1]).exists());
   }

   @Test
   public void shouldCreateNewBranch() throws Exception
   {
      // git init
      // create new branch
      // verify branch exists

      String testBranchName = "testBranch";
      Project project = initializeJavaProject();
      Git repo = GitUtils.init(project.getProjectRoot());

      GitUtils.addAll(repo);
      GitUtils.commitAll(repo, "initial commit");

      Ref testBranch = GitUtils.createBranch(repo, testBranchName);
      Assert.assertNotNull(testBranch);

      List<Ref> branches = GitUtils.getLocalBranches(repo);
      Assert.assertTrue("Branch is not created", branches.contains(testBranch));
   }

}
