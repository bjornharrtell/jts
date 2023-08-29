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

package org.locationtech.jts.algorithm.match;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.operation.overlay.OverlayOp;
import org.locationtech.jts.operation.union.UnionOp;

/**
 * Measures the degree of similarity between two {@link Geometry}s
 * using the area of intersection between the geometries.
 * The measure is normalized to lie in the range [0, 1].
 * Higher measures indicate a great degree of similarity.
 * <p>
 * NOTE: Currently experimental and incomplete.
 * 
 * @author mbdavis
 *
 */
public class AreaSimilarityMeasure 
	implements SimilarityMeasure
{
	/*
	public static double measure(Geometry a, Geometry b)
	{
		AreaSimilarityMeasure gv = new AreaSimilarityMeasure(a, b);
		return gv.measure();
	}
	*/
	
  /**
   * Creates a new instance.
   */
	public AreaSimilarityMeasure()
	{
	}
	
	public double measure(Geometry g1, Geometry g2)
	{		
		double areaInt = OverlayOp.intersection(g1, g2).getArea();
		double areaUnion = UnionOp.union(g1, g2).getArea();
		return areaInt / areaUnion;
	}
	
	
}
