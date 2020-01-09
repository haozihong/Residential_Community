package make_grid;

import igeo.*;
import java.util.ArrayList;

public class HGeo {
	public ArrayList<IVec> vs = new ArrayList<IVec>();
	public ArrayList<HEdge> es = new ArrayList<HEdge>();
	public ArrayList<HFace> fs = new ArrayList<HFace>();

	public static double triangleArea(IVecI pa, IVecI pb, IVecI pc) {
		// area of triangle
		IVecI v1 = pb.dif(pa);
		IVecI v2 = pc.dif(pa);
		double cp = v1.cross(v2).z();
		return cp / 2;
	}

	public static double area(IVecI[] polygon) {
		// area of polygon
		double a = 0.0;
		IVecI p0 = polygon[0];
		for (int i = 1; i < polygon.length - 1; i++) {
			IVecI p1 = polygon[i];
			IVecI p2 = polygon[i + 1];
			a += triangleArea(p0, p1, p2);
		}
		return a;
	}

	public HFace init(double x, double y) {
		HVec[] pts = new HVec[4];
		pts[0] = new HVec(0, 0);
		pts[1] = new HVec(x, 0);
		pts[2] = new HVec(x, y);
		pts[3] = new HVec(0, y);
		for (int i = 0; i < 4; i++)
			es.add(new HEdge(pts[i], pts[(i + 1) % 4], null, null));
		for (int i = 0; i < 4; i++) {
			es.get(i).prev = es.get((i + 4 - 1) % 4);
			es.get(i).next = es.get((i + 1) % 4);
		}
		fs.add(new HFace(es.get(0)));
		return fs.get(0);
	}

	public HFace init(double area) {
		return init(Math.sqrt(area), Math.sqrt(area));
	}

	public HFace generateOne() {
		HFace big = null;
		double max = 0;
		for (HFace face : fs) {
			double area = face.area();
			if (area > max) {
				max = area;
				big = face;
			}
		}

		HEdge[] corner = new HEdge[10];
		int ci = 0;
		HEdge et = big.edge;
		boolean once = false;
		while (!once || et != big.edge) {
			once = true;
			if (IVec.angle(et.prev.vx1, et.vx1, et.vx2) == Math.PI / 2) {
				System.out.println(IVec.angle(et.prev.vx1, et.vx1, et.vx2));
				corner[ci] = et;
				ci++;
			}
			et = et.next;
		}
		System.out.println(ci);

		int li = 0;
		max = 0;
		for (int i = 0; i < 4; i++) {
			double len = corner[i].vx1.dist(corner[(i + 1) % 4].vx1);
			if (len > max) {
				max = len;
				li = i;
			}
		}
		HVec vn1 = new HVec(corner[li].vx1.sum(corner[(li + 1) % 4].vx1.dif(corner[li].vx1).mul(.5)));
		HVec vn2 = new HVec(corner[(li + 2) % 4].vx1.sum(corner[(li + 3) % 4].vx1.dif(corner[(li + 2) % 4].vx1).mul(.5)));

		vn1 = big.addVx(vn1);
		vn2 = big.addVx(vn2);
		

		HFace fn = big.addEdge(vn1, vn2);
		fs.add(fn);
		return fn;
	}
}
