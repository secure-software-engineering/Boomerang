/**
 * ***************************************************************************** 
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 *   Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.sootup.jimple;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Statement;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import sootup.core.graph.StmtGraph;
import sootup.core.jimple.common.stmt.JIdentityStmt;
import sootup.core.jimple.common.stmt.Stmt;

public class JimpleUpControlFlowGraph implements ControlFlowGraph {

  private final JimpleUpMethod method;
  private final StmtGraph<?> graph;

  private boolean cacheBuilt = false;
  private final List<Statement> statements = Lists.newArrayList();
  private final Collection<Statement> startPointCache = new LinkedHashSet<>();
  private final Collection<Statement> endPointCache = new LinkedHashSet<>();
  private final Multimap<Statement, Statement> predecessorsOfCache = HashMultimap.create();
  private final Multimap<Statement, Statement> successorsOfCache = HashMultimap.create();

  public JimpleUpControlFlowGraph(JimpleUpMethod method) {
    this.method = method;
    this.graph = method.getDelegate().getBody().getStmtGraph();
  }

  @Override
  public Collection<Statement> getStartPoints() {
    buildCache();
    return startPointCache;
  }

  @Override
  public Collection<Statement> getEndPoints() {
    buildCache();
    return endPointCache;
  }

  @Override
  public Collection<Statement> getSuccsOf(Statement curr) {
    buildCache();
    return successorsOfCache.get(curr);
  }

  @Override
  public Collection<Statement> getPredsOf(Statement curr) {
    buildCache();
    return predecessorsOfCache.get(curr);
  }

  @Override
  public List<Statement> getStatements() {
    buildCache();
    return statements;
  }

  public void buildCache() {
    if (cacheBuilt) {
      return;
    }

    Collection<Stmt> heads = graph.getEntrypoints();
    for (Stmt head : heads) {
      if (head instanceof JIdentityStmt) {
        continue;
      }

      Statement statement = JimpleUpStatement.create(head, method);
      startPointCache.add(statement);
    }

    Collection<Stmt> tails = graph.getTails();
    for (Stmt tail : tails) {
      Statement statement = JimpleUpStatement.create(tail, method);
      endPointCache.add(statement);
    }

    List<Stmt> units = method.getDelegate().getBody().getStmts();
    for (Stmt unit : units) {
      Statement first = JimpleUpStatement.create(unit, method);
      statements.add(first);

      for (Stmt predecessor : graph.predecessors(unit)) {
        Statement predStatement = JimpleUpStatement.create(predecessor, method);
        predecessorsOfCache.put(first, predStatement);
      }

      for (Stmt successor : graph.successors(unit)) {
        Statement succStatement = JimpleUpStatement.create(successor, method);
        successorsOfCache.put(first, succStatement);
      }
    }

    cacheBuilt = true;
  }
}
