package org.locationtech.jts.operation.union;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.operation.overlay.OverlayOp;
import org.locationtech.jts.operation.overlay.snap.SnapIfNeededOverlayOp;

public class UnionOp {
  public static Geometry union(Geometry g, Geometry other) {
    // handle empty geometry cases
    if (g.isEmpty() || other.isEmpty()) {
      if (g.isEmpty() && other.isEmpty())
        return OverlayOp.createEmptyResult(OverlayOp.UNION, g, other, g.getFactory());
        
    // special case: if either input is empty ==> other input
      if (g.isEmpty()) return (Geometry) other.copy();
      if (other.isEmpty()) return (Geometry) g.copy();
    }
    
    // TODO: optimize if envelopes of geometries do not intersect
    
    Geometry.checkNotGeometryCollection(g);
    Geometry.checkNotGeometryCollection(other);
    return SnapIfNeededOverlayOp.overlayOp(g, other, OverlayOp.UNION);
  }
}
