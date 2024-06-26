/*
 * Copyright (c) 2016 Vivid Solutions.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.locationtech.jts.operation.relate;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.WKTReader;

import junit.textui.TestRunner;
import test.jts.GeometryTestCase;


/**
 * Tests {@link Geometry#relate}.
 *
 * @author Martin Davis
 * @version 1.7
 */
public class ContainsTest
    extends GeometryTestCase
{
  public static void main(String[] args) {
    TestRunner.run(ContainsTest.class);
  }

  private GeometryFactory fact = new GeometryFactory();
  private WKTReader rdr = new WKTReader(fact);

  public ContainsTest(String name)
  {
    super(name);
  }

  /**
   * From GEOS #572.
   * A case where B is contained in A, but 
   * the JTS relate algorithm fails to compute this correctly.
   * when using an FP intersection algorithm.
   * This case works when using CGAlgorithmsDD#intersection(Coordinate, Coordinate, Coordinate, Coordinate).
   * 
   * The cause is that the long segment in A nodes the single-segment line in B.
   * The node location cannot be computed precisely.
   * The node then tests as not lying precisely on the original long segment in A.
   * 
   * The solution is to change the relate algorithm so that it never computes
   * new intersection points, only ones which occur at existing vertices.
   * (The topology of the implicit intersections can still be computed
   * to contribute to the intersection matrix result).
   * This will require a complete reworking of the relate algorithm. 
   * 
   * @throws Exception
   */
  public void testContainsIncorrect()
      throws Exception
  {
    String a = "LINESTRING (1 0, 0 2, 0 0, 2 2)";
    String b = "LINESTRING (0 0, 2 2)";
    checkContains(a, b);
  }
  
  /**
   * From GEOS #933.
   * A case where B is contained in A, but 
   * the JTS relate algorithm fails to compute this correctly.
   * when using an FP intersection algorithm.
   * This case works when using CGAlgorithmsDD#intersection(Coordinate, Coordinate, Coordinate, Coordinate).
  */
  public void testContainsGEOS933()
      throws Exception
  {
    String a = "MULTILINESTRING ((0 0, 1 1), (0.5 0.5, 1 0.1, -1 0.1))";
    String b = "LINESTRING (0 0, 1 1)";
    checkContains(a, b);
  }
  
  private void checkContains(String wktA, String wktB) {
    Geometry geomA = read(wktA);
    Geometry geomB = read(wktB);
    boolean actual = geomA.contains(geomB);
    assertTrue(actual);
  }
  
  private void checkContainsError(String wktA, String wktB) {
    Geometry geomA = read(wktA);
    Geometry geomB = read(wktB);
    boolean actual = geomA.contains(geomB);
    assertFalse(actual);
  }
}
