package org.locationtech.jts.geom.util;

import org.locationtech.jts.geom.Geometry;

/**
 * An interface for geometry functions used for mapping.
 * 
 * @author Martin Davis
 *
 */
public interface MapOp {
	/**
	 * Computes a new geometry value.
	 * 
	 * @param g the input geometry
	 * @return a result geometry
	 */
	Geometry map(Geometry g);
}