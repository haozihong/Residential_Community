package make_grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import igeo.*;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;

public class HVorGrid {
	public Polygon site;
	public int ptNum;
	private HashMap<Coordinate, HVec> ptMap = new HashMap<Coordinate, HVec>();
	public ArrayList<HVec> vxs = new ArrayList<HVec>();
	public ArrayList<HEdge> edges = new ArrayList<HEdge>();
	public HFace[] vors;
	
	public HVorGrid(Polygon site, int ptNum, long seed) {
		this.site = site;
		this.ptNum = ptNum;
		init(seed);
	}
	
	public HVorGrid(ICurve c, int ptNum, long seed) {
		Coordinate[] coors = new Coordinate[c.cpNum()];
		for (int i=0; i<c.cpNum()-1; i++)
			coors[i] = new Coordinate(c.cp(i).x,c.cp(i).y);
		coors[coors.length-1] = coors[0];
		site = new GeometryFactory().createPolygon(coors);
		this.ptNum = ptNum;
		init(seed);
	}
	
	private void init(long seed) {
		GeometryCollection vor = makeVorRandom(site,ptNum,seed,0);
		for (int t=0; t<20; t++) {
			ArrayList<Coordinate> pts = new ArrayList<Coordinate>();
			int num = vor.getNumGeometries();
			for (int i=0; i<num; i++) {
				Geometry geo = vor.getGeometryN(i);
				Coordinate[] vxs = geo.getCoordinates();
				double totW = 0, x = 0, y = 0;
				for (int j=0; j<vxs.length-1; j++) {
					Coordinate vx1 = vxs[j];
					Coordinate vx2 = vxs[j+1];
					double w = vx1.distance(vx2);
					x += (vx1.x+vx2.x)/2*w;
					y += (vx1.y+vx2.y)/2*w;
					totW += w;
				}
				x /= totW;
				y /= totW;
				pts.add(new Coordinate(x,y));
			}
			vor = makeVor(site, pts, 0);
		}
		
		Coordinate[] coors = vor.getCoordinates();
		for (Coordinate coor : coors) {
			HVec vx = new HVec(coor.x,coor.y);
			vxs.add(vx);
			if (closestDist(site,coor)<.0001) vx.fixed = true;
			ptMap.put(coor, vx);
		}
		
		int num = vor.getNumGeometries();
		vors = new HFace[num];
		for (int i=0; i<num; i++) {
			Geometry geo = vor.getGeometryN(i);
			Coordinate[] vxs = geo.getCoordinates();
			HEdge[] poly = new HEdge[vxs.length-1];
			for (int j=vxs.length-1; j>0; j--) {
				HVec vx1 = ptMap.get(vxs[j]);
				HVec vx2 = ptMap.get(vxs[j-1]);
				poly[j-1] = new HEdge(vx1,vx2,null,null);
				edges.add(poly[j-1]);
			}
			for (int j=0; j<vxs.length-1; j++) {
				poly[j].prev = poly[(j+poly.length-1)%poly.length];
				poly[j].next = poly[(j+1)%poly.length];
			}
			vors[i] = new HFace(poly[0]);
			for (HEdge edge : poly) {
				edge.face = vors[i];
			}
		}
		for (int i=0; i<edges.size()-1; i++) {
			HEdge e1 = edges.get(i);
			for (int j=i+1; j<edges.size(); j++) {
				HEdge e2 = edges.get(j);
				if (e1.vx1 == e2.vx2 && e1.vx2 == e2.vx1)
					e1.setTwin(e2);
			}
		}
	}
	
	public static double closestDist(Polygon line, Coordinate pt) {
		Coordinate[] pts = line.getCoordinates();
		IVec pti = new IVec(pt.x,pt.y);
		int num = pts.length-1;
		double minDist = 0;
		for (int i=0; i<pts.length; i++) {
			IVec pt0 = new IVec(pts[i].x,pts[i].y);
			IVec pt1 = new IVec(pts[(i+1)%num].x,pts[(i+1)%num].y);
			double r = pti.ratioOnSegment(pt0, pt1);
			if(r<0){ r=0; } else if(r>1.0){ r=1.0; } // inside segment
			IVec cpi = pt0.sum(pt1,r);
			Coordinate cp = new Coordinate(cpi.x,cpi.y);
			double dist = cp.distance(pt);
			if (i == 0 || dist<minDist) {
				minDist = dist;
			}
		}
		return minDist;
	}
	
	public static GeometryCollection makeVor(Polygon site, ArrayList<Coordinate> pts, double bufferDis) {
		VoronoiDiagramBuilder vor = new VoronoiDiagramBuilder();
		double minX = 1e302;
		double maxX = -1e302;
		double minY = 1e302;
		double maxY = -1e302;
		Coordinate[] coors = site.getCoordinates();
		for (int i = 0; i < coors.length-1; ++i) {
			minX = Math.min(minX, coors[i].x);
			maxX = Math.max(maxX, coors[i].x);
			minY = Math.min(minY, coors[i].y);
			maxY = Math.max(maxY, coors[i].y);
		}
		Envelope en = new Envelope(minX, maxX, minY, maxY);
		vor.setClipEnvelope(en);
		int num = pts.size();
		vor.setSites(pts);
		Geometry geo = vor.getDiagram(new GeometryFactory());
		num = geo.getNumGeometries();
		Geometry[] geos = new Geometry[num];
		for (int i = 0; i < num; i++) {
			geos[i] = geo.getGeometryN(i);
			geos[i] = geos[i].intersection(site);
			geos[i] = geos[i].buffer(-bufferDis);
		}
		return new GeometryFactory().createGeometryCollection(geos);
	}

	public static GeometryCollection makeVorRandom(Polygon site, int num, long seed, double bufferDis) {
		double minX = 1e302;
		double maxX = -1e302;
		double minY = 1e302;
		double maxY = -1e302;
		Coordinate[] coors = site.getCoordinates();
		for (int i = 0; i < coors.length-1; ++i) {
			minX = Math.min(minX, coors[i].x);
			maxX = Math.max(maxX, coors[i].x);
			minY = Math.min(minY, coors[i].y);
			maxY = Math.max(maxY, coors[i].y);
		}
		ArrayList<Coordinate> pts = new ArrayList<Coordinate>();
		IVec[] ipts = new IVec[coors.length-1];
		for (int i=0; i<coors.length-1; i++) {
			ipts[i] = new IVec(coors[i].x,coors[i].y);
		}
		Random ran = new Random(seed);
		for (int i = 0; i < num; i++) {
			double x = minX;
			double y = minY;
			while (!new IVec(x,y).isInside2d(ipts)) {
				x = ran.nextDouble() * (maxX - minX) + minX;
				y = ran.nextDouble() * (maxY - minY) + minY;
			}
			pts.add(new Coordinate(x, y));
		}
		return makeVor(site, pts, bufferDis);
	}
	
	public static GeometryCollection makeVorRandom(Polygon site, int num, double bufferDis) {
		return makeVorRandom(site, num, 1, bufferDis);
	}

	public HFace get(int i) {
		return vors[i];
	}
}
