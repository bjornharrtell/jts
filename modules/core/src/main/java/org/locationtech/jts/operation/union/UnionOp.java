package org.locationtech.jts.operation.union;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.operation.overlay.OverlayOp;
import org.locationtech.jts.operation.overlay.snap.SnapIfNeededOverlayOp;

public class UnionOp
{
	public static Geometry union(Geometry g, Geometry other) {
        if (g.isEmpty() || other.isEmpty()) {
            if (g.isEmpty() && other.isEmpty()) return OverlayOp.createEmptyResult(OverlayOp.UNION, g, other, g.getFactory());
            if (g.isEmpty()) return other.copy();
            if (other.isEmpty()) return g.copy();
        }
        Geometry.checkNotGeometryCollection(g);
        Geometry.checkNotGeometryCollection(other);
        return SnapIfNeededOverlayOp.overlayOp(g, other, OverlayOp.UNION);
    }
}
