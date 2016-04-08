


/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 * 
 * Copyright (C) 2016 Vivid Solutions
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Vivid Solutions BSD
 * License v1.0 (found at the root of the repository).
 * 
 */
package org.locationtech.jts.operation.relate;

import org.locationtech.jts.algorithm.BoundaryNodeRule;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.IntersectionMatrix;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.GeometryGraphOperation;
import org.locationtech.jts.operation.predicate.RectangleContains;
import org.locationtech.jts.operation.predicate.RectangleIntersects;

/**
 * Implements the SFS <tt>relate()</tt> generalized spatial predicate on two {@link Geometry}s.
 * <b>
 * The class supports specifying a custom {@link BoundaryNodeRule}
 * to be used during the relate computation.
 * <p>
 * If named spatial predicates are used on the result {@link IntersectionMatrix}
 * of the RelateOp, the result may or not be affected by the 
 * choice of <tt>BoundaryNodeRule</tt>, depending on the exact nature of the pattern.
 * For instance, {@link IntersectionMatrix#isIntersects()} is insensitive 
 * to the choice of <tt>BoundaryNodeRule</tt>, 
 * whereas {@link IntersectionMatrix#isTouches(int, int)} is affected by the rule chosen.
 * <p>
 * <b>Note:</b> custom Boundary Node Rules do not (currently)
 * affect the results of other {@link Geometry} methods (such
 * as {@link Geometry#getBoundary}.  The results of
 * these methods may not be consistent with the relationship computed by
 * a custom Boundary Node Rule.
 *
 * @version 1.7
 */
public class RelateOp
  extends GeometryGraphOperation
{
  /**
   * Computes the {@link IntersectionMatrix} for the spatial relationship
   * between two {@link Geometry}s, using the default (OGC SFS) Boundary Node Rule
   *
   * @param a a Geometry to test
   * @param b a Geometry to test
   * @return the IntersectonMatrix for the spatial relationship between the geometries
   */
  public static IntersectionMatrix relate(Geometry a, Geometry b)
  {
    RelateOp relOp = new RelateOp(a, b);
    IntersectionMatrix im = relOp.getIntersectionMatrix();
    return im;
  }

  /**
   * Computes the {@link IntersectionMatrix} for the spatial relationship
   * between two {@link Geometry}s using a specified Boundary Node Rule.
   *
   * @param a a Geometry to test
   * @param b a Geometry to test
   * @param boundaryNodeRule the Boundary Node Rule to use
   * @return the IntersectonMatrix for the spatial relationship between the input geometries
   */
  public static IntersectionMatrix relate(Geometry a, Geometry b, BoundaryNodeRule boundaryNodeRule)
  {
    RelateOp relOp = new RelateOp(a, b, boundaryNodeRule);
    IntersectionMatrix im = relOp.getIntersectionMatrix();
    return im;
  }
  
  public static IntersectionMatrix relateWithCheck(Geometry g1, Geometry g2) {
    g1.checkNotGeometryCollection(g1);
    g1.checkNotGeometryCollection(g2);
    return RelateOp.relate(g1, g2);
  }
  
  public static boolean relate(Geometry g1, Geometry g2, String intersectionPattern) {
    return relateWithCheck(g1, g2).matches(intersectionPattern);
  }
  
  public static boolean disjoint(Geometry g1, Geometry g2) {
    return !g1.intersects(g2);
  }
  
  public static boolean touches(Geometry g1, Geometry g2) {
    // short-circuit test
    if (! g1.getEnvelopeInternal().intersects(g2.getEnvelopeInternal()))
      return false;
    return relate(g1, g2).isTouches(g1.getDimension(), g2.getDimension());
  }
    
  public static boolean intersects(Geometry g1, Geometry g2) {
    // short-circuit envelope test
    if (! g1.getEnvelopeInternal().intersects(g2.getEnvelopeInternal()))
      return false;

    /**
     * TODO: (MD) Add optimizations:
     *
     * - for P-A case:
     * If P is in env(A), test for point-in-poly
     *
     * - for A-A case:
     * If env(A1).overlaps(env(A2))
     * test for overlaps via point-in-poly first (both ways)
     * Possibly optimize selection of point to test by finding point of A1
     * closest to centre of env(A2).
     * (Is there a test where we shouldn't bother - e.g. if env A
     * is much smaller than env B, maybe there's no point in testing
     * pt(B) in env(A)?
     */

    // optimization for rectangle arguments
    if (g1.isRectangle()) {
      return RectangleIntersects.intersects((Polygon) g1, g2);
    }
    if (g2.isRectangle()) {
      return RectangleIntersects.intersects((Polygon) g2, g1);
    }
    // general case
    return RelateOp.relate(g1, g2).isIntersects();
  }
  
  public static boolean crosses(Geometry g1, Geometry g2) {
    // short-circuit test
    if (! g1.getEnvelopeInternal().intersects(g2.getEnvelopeInternal()))
      return false;
    return relate(g1, g2).isCrosses(g1.getDimension(), g2.getDimension());
  }
  
  public static boolean within(Geometry g1, Geometry g2) {
    return g2.contains(g1);
  }
  
  public static boolean contains(Geometry g1, Geometry g2) {
    // short-circuit test
    if (! g1.getEnvelopeInternal().contains(g2.getEnvelopeInternal()))
      return false;
    // optimization for rectangle arguments
    if (g1.isRectangle()) {
      return RectangleContains.contains((Polygon) g1, g2);
    }
    // general case
    return relate(g1, g2).isContains();
  }
  
  public static boolean overlaps(Geometry g1, Geometry g2) {
    // short-circuit test
    if (! g1.getEnvelopeInternal().intersects(g2.getEnvelopeInternal()))
      return false;
    return relate(g1, g2).isOverlaps(g1.getDimension(), g2.getDimension());
  }
  
  public static boolean covers(Geometry g1, Geometry g2) {
    // short-circuit test
    if (! g1.getEnvelopeInternal().covers(g2.getEnvelopeInternal()))
      return false;
    // optimization for rectangle arguments
    if (g1.isRectangle()) {
      // since we have already tested that the test envelope is covered
      return true;
    }
    return relate(g1, g2).isCovers();
  }
  
  public static boolean coveredBy(Geometry g1, Geometry g2) {
    return covers(g2, g1);
  }

  private RelateComputer _relate;

  /**
   * Creates a new Relate operation, using the default (OGC SFS) Boundary Node Rule.
   *
   * @param g0 a Geometry to relate
   * @param g1 another Geometry to relate
   */
  public RelateOp(Geometry g0, Geometry g1)
  {
    super(g0, g1);
    _relate = new RelateComputer(arg);
  }

  /**
   * Creates a new Relate operation with a specified Boundary Node Rule.
   *
   * @param g0 a Geometry to relate
   * @param g1 another Geometry to relate
   * @param boundaryNodeRule the Boundary Node Rule to use
   */
  public RelateOp(Geometry g0, Geometry g1, BoundaryNodeRule boundaryNodeRule)
  {
    super(g0, g1, boundaryNodeRule);
    _relate = new RelateComputer(arg);
  }

  /**
   * Gets the IntersectionMatrix for the spatial relationship
   * between the input geometries.
   *
   * @return the IntersectonMatrix for the spatial relationship between the input geometries
   */
  public IntersectionMatrix getIntersectionMatrix()
  {
    return _relate.computeIM();
  }

}
