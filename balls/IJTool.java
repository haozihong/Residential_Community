package balls;

import com.vividsolutions.jts.geom.*;

import igeo.*;

public class IJTool {

	public static ICurve toICurve(Coordinate[] coors) {
		IVec[] pts = new IVec[coors.length];
		for (int i = 0; i < coors.length; i++)
			pts[i] = new IVec(coors[i].x, coors[i].y, Double.isNaN(coors[i].z) ? 0 : coors[i].z);
		return new ICurve(pts);
	}

	public static LineString toJTSLine(IVec[] c) {
		Coordinate[] coords = new Coordinate[c.length];
		for (int i = 0; i < c.length; i++) {
			IVec pt = c[i];
			coords[i] = new Coordinate(pt.x, pt.y);
		}
		LineString g = new GeometryFactory().createLineString(coords);
		return g;
	}

	public static Polygon toJTSPolygon(ICurve c) {
		if (!c.isClosed())
			return null;
		Coordinate[] coors = new Coordinate[c.cpNum()];
		for (int i = 0; i < c.cpNum() - 1; i++) {
			IVec pt = c.cp(i);
			coors[i] = new Coordinate(pt.x, pt.y);
		}
		coors[c.cpNum() - 1] = coors[0];
		Polygon g = new GeometryFactory().createPolygon(coors);
		return g;
	}

	public static Polygon toJTSPolygon(IVecI[] c) {
		int num;
		if (c[c.length-1].eq(c[0])) num = c.length-1; else num = c.length;
		Coordinate[] coords = new Coordinate[num + 1];
		for (int i = 0; i < num; i++) {
			IVec pt = c[i].get();
			coords[i] = new Coordinate(pt.x, pt.y);
		}
		coords[num] = coords[0];
		Polygon g = new GeometryFactory().createPolygon(coords);
		return g;
	}

}
