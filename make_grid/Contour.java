package make_grid;

import java.util.ArrayList;
import java.util.Arrays;

import igeo.*;

public class Contour {
	public ArrayList<ICurve> contour(ArrayList<HFace> faces) {
		ArrayList<ICurve> curves = new ArrayList<ICurve>();
		ArrayList<HEdge> edges = new ArrayList<HEdge>();
		for (HFace face : faces) {
			HEdge[] contour = face.contour();
			for (HEdge edge : contour)
				if (edges.indexOf(edge) != -1) edges.add(edge);
		}
		for (int i = 0; i < edges.size(); i++) {
			if (edges.get(i).twin != null) System.out.println("r");
			if (edges.get(i).twin != null && edges.indexOf(edges.get(i).twin) != -1) {
				edges.remove(edges.get(i).twin);
				edges.remove(i);
				i--;
			}
		}
		while (edges.size() > 0) {
			ArrayList<IVec> pts = new ArrayList<IVec>();
			HEdge edge = edges.get(0);
			pts.add(edge.vx1);
			while (edge != null) {
				HVec pt = edge.vx2;
				pts.add(pt);
				edges.remove(edge);
				edge = null;
				for (HEdge e : pt.edges)
					if (edges.indexOf(e) != -1) {
						edge = e;
						break;
					}
			}
			curves.add(new ICurve(pts.toArray(new IVec[pts.size()]),1,true));
		}
		return curves;
	}
}
