/*
 * Copyright (c) 2016 Vivid Solutions.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.locationtech.jts.geom;

import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import junit.framework.TestCase;
import junit.textui.TestRunner;


/**
 * Tests for {@link GeometryFactory}.
 *
 * @version 1.13
 */
public class GeometryFactoryTest extends TestCase {

  PrecisionModel precisionModel = new PrecisionModel();
  GeometryFactory geometryFactory = new GeometryFactory(precisionModel, 0);
  WKTReader reader = new WKTReader(geometryFactory);

  public static void main(String args[]) {
    TestRunner.run(GeometryFactoryTest.class);
  }

  public GeometryFactoryTest(String name) { super(name); }

  public void testCreateEmpty() {
    checkEmpty( geometryFactory.createEmpty(0), Point.class);
    checkEmpty( geometryFactory.createEmpty(1), LineString.class);
    checkEmpty( geometryFactory.createEmpty(2), Polygon.class);
    
    checkEmpty( geometryFactory.createPoint(), Point.class);
    checkEmpty( geometryFactory.createLineString(), LineString.class);
    checkEmpty( geometryFactory.createPolygon(), Polygon.class);
    
    checkEmpty( geometryFactory.createMultiPoint(), MultiPoint.class);
    checkEmpty( geometryFactory.createMultiLineString(), MultiLineString.class);
    checkEmpty( geometryFactory.createMultiPolygon(), MultiPolygon.class);
    checkEmpty( geometryFactory.createGeometryCollection(), GeometryCollection.class);
  }
  
  private void checkEmpty(Geometry geom, Class clz) {
    assertTrue(geom.isEmpty());
    assertTrue( geom.getClass() == clz );
  }
  
  public void testMultiPointCS()
  {
    GeometryFactory gf = new GeometryFactory(new PackedCoordinateSequenceFactory());
    CoordinateSequence mpSeq = gf.getCoordinateSequenceFactory().create(1, 4);
    mpSeq.setOrdinate(0, 0, 50);
    mpSeq.setOrdinate(0, 1, -2);
    mpSeq.setOrdinate(0, 2, 10);
    mpSeq.setOrdinate(0, 3, 20);
    
    MultiPoint mp = gf.createMultiPoint(mpSeq);
    CoordinateSequence pSeq = ((Point)mp.getGeometryN(0)).getCoordinateSequence();
    assertEquals(4, pSeq.getDimension());
    for (int i = 0; i < 4; i++)
      assertEquals(mpSeq.getOrdinate(0, i), pSeq.getOrdinate(0, i));
  }
  
  private Geometry read(String wkt) throws ParseException
  {
    return reader.read(wkt);
  }
}
