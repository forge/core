/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.scanner;

import org.jboss.forge.addon.text.highlight.Syntax.Builder;
import org.junit.Test;

public class SQLScannerTestCase extends AbstractScannerTestCase {

   @Test
   public void shouldMatchSQLCreateTablesExample() throws Exception {
      assertMatchExample(Builder.create(), "sql", "create_tables.in.sql");
   }

   @Test
   public void shouldMatchSQLMaintenanceExample() throws Exception {
      assertMatchExample(Builder.create(), "sql", "maintenance.in.sql");
   }

   @Test
   public void shouldMatchSQLMySQLCommentsExample() throws Exception {
      assertMatchExample(Builder.create(), "sql", "mysql-comments.in.sql");
   }

   @Test
   public void shouldMatchSQLMySQLLongQueryExample() throws Exception {
      assertMatchExample(Builder.create(), "sql", "mysql-long-queries.in.sql");
   }

   @Test
   public void shouldMatchSQLNorwegianExample() throws Exception {
      assertMatchExample(Builder.create(), "sql", "norwegian.in.sql");
   }

   @Test
   public void shouldMatchSQLPittsburghExample() throws Exception {
      assertMatchExample(Builder.create(), "sql", "pittsburgh.in.sql");
   }

   @Test
   public void shouldMatchSQLReferenceExample() throws Exception {
      assertMatchExample(Builder.create(), "sql", "reference.in.sql");
   }

   @Test
   public void shouldMatchSQLSelectsInExample() throws Exception {
      assertMatchExample(Builder.create(), "sql", "selects.in.sql");
   }

   @Test
   public void shouldMatchSQLTheGoatHerderIssue163Example() throws Exception {
      assertMatchExample(Builder.create(), "sql", "thegoatherder-issue-163.in.sql");
   }
}