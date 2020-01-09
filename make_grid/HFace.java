package make_grid;

import igeo.*;
import java.util.ArrayList;

public class HFace {
	public HEdge edge;
	public HEdge[] contour;
	public boolean[] built = new boolean[200];
	public double oriArea, aveArea = 0;
	
	public HFace(HEdge edge) {
		this.edge = edge;
		oriArea = area();
	}
	
	public boolean getbuilt(int layer){
		return built[layer];
	}
	
	public void loadContour() {
		ArrayList<HEdge> edges = new ArrayList<HEdge>();
		edges.add(edge);
		HEdge et = edge.next;
		while(et!=edge) {
			edges.add(et);
			et = et.next;
		}
		contour = edges.toArray(new HEdge[edges.size()]);
	}
	
	public HEdge[] contour() {
		loadContour();
		return contour;
	}
	
	public HVec[] vertex() {
		loadContour();
		HVec[] vxs = new HVec[contour.length];
		for (int i=0; i<contour.length; i++) {
			vxs[i] = contour[i].vx1;
		}
		return vxs;
	}
	
	public double area() {
		HEdge[] contour = contour();
		IVec[] pts = new IVec[contour.length];
		for (int i=0; i<pts.length; i++) {
			pts[i] = contour[i].vx1;
		}
		return HGeo.area(pts);
	}
	
	public double rectArea(IVec[] rect) {
		double a = rect[1].dist(rect[0]);
		double b = rect[2].dist(rect[1]);
		return a*b;
	}
	
	public IVec[] bounding() {
		IVec[] pts = vertex();
		IVec mo = null,mu = null,mv = null;
		double minArea = 1e300;
		double maxu = 0, minu = 0, maxv = 0, minv = 0;
		for (int i=0; i<pts.length; i++) {
			IVec o = pts[i];
			IVec u = pts[(i+1)%pts.length].dif(pts[i]).unit();
			IVec v = u.cp().rot(Math.PI/2);
			double u0 = 1e300;
			double u1 = -1e300;
			double v0 = 1e300;
			double v1 = -1e300;
			for (IVec pt : pts) {
				double[] uv = pt.projectTo2Vec(u, v);
				u0 = Math.min(u0, uv[0]);
				u1 = Math.max(u1, uv[0]);
				v0 = Math.min(v0, uv[1]);
				v1 = Math.max(v1, uv[1]);
			}
			double area = (u1-u0)*(v1-v0);
			if (area<minArea) {
				minArea = area;
				maxu = u1;
				minu = u0;
				maxv = v1;
				minv = v0;
				mo = o;
				mu = u;
				mv = v;
			}
		}
		IVec[] rect = new IVec[4];
		rect[0] = mu.cp().mul(minu).add(mv.cp().mul(minv)).add(mo);
		rect[1] = mu.cp().mul(maxu).add(mv.cp().mul(minv)).add(mo);
		rect[2] = mu.cp().mul(maxu).add(mv.cp().mul(maxv)).add(mo);
		rect[3] = mu.cp().mul(minu).add(mv.cp().mul(maxv)).add(mo);
		return rect;
	}
	
//	public IVec center() {
//		double x = 0;
//		double y = 0;
//		
//	}
	
	public double value(ICompoundField field) {
		HVec[] vxs = vertex();
		IVec[] bound = bounding();
		IVec dir1 = bound[0].dif(bound[1]);
		IVec dir2 = bound[2].dif(bound[1]);
		double a1 = IG.x.angle(dir1);
		double a2 = IG.x.angle(dir2);
		if (a1>Math.PI/2) a1 = Math.PI - a1;
		if (a2>Math.PI/2) a2 = Math.PI - a2;
		double a = bound[1].dist(bound[0]);
		double b = bound[2].dist(bound[1]);
		if (a1>a2) {
			double temp = a;
			a = b;
			b = temp;
		}
		double abV = a/b;
		abV /= 1.6;
		if (abV > 1) abV = 1/abV;
		abV = Math.min(1, abV/.8);

		
		double angV = 0;
		for (int i=0; i<vxs.length; i++) {
			IVec vx0 = vxs[(i+vxs.length-1)%vxs.length];
			IVec vx1 = vxs[i];
			IVec vx2 = vxs[(i+1)%vxs.length];
			double ang = IVec.angle(vx0, vx1, vx2);
			angV += 1-Math.abs(Math.min(Math.sin(ang), -Math.cos(ang)));
		}
		angV /= vxs.length;
		
//		double dirV = 0;
//		for (HEdge edge : contour) {
//			IVecI vf = field.get(edge.mid());
//			double ang = vf.angle(edge.vx2.dif(edge.vx1));
//			dirV += 1-Math.abs(Math.min(Math.sin(ang), -Math.cos(ang)));
//		}
//		dirV /= vxs.length;
		double x = 0;
		double y = 0;
		for (IVec pt : vxs) {
			x += pt.x;
			y += pt.y;
		}
		x /= vxs.length;
		y /= vxs.length;
		IVecI vf = field.get(new IVec(x,y));
		double ang = vf.angle(bound[0].dif(bound[1]));
		double dirV = 1-Math.abs(Math.min(Math.sin(ang), -Math.cos(ang)));
		
		double area = area();
		double areaV = area/rectArea(bound);
		areaV = Math.min(1, areaV/.9);
		
		double aveV = 1;
		if (aveArea>0) {
			aveV = area / aveArea;
			if (aveV > 1) aveV = 1/aveV;
		}
		
		return Math.pow(angV,2)*Math.pow(dirV,vf.len()/10)*Math.pow(areaV,2)*Math.pow(abV,1);
	}
	
	public HVec addVx(HVec vn) {
		HEdge et = edge;
		boolean once = false;
		while(!once || et!=edge) {
			once = true;
			if (vn.eq(et.vx1)) return et.vx1;
			if (vn.eq(et.vx2)) return et.vx2;
			if (IVec.angle(et.vx2, et.vx1, vn) < .0001) {
				et.split(vn);
				return vn;
			}
			et = et.next;
		}
		return null;
	}
	
	public HFace addEdge(HVec vn1, HVec vn2) {
		HEdge e1 = null;
		HEdge e2 = null;
		HEdge et = edge;
		boolean once = false;
		while(!once || et!=edge) {
			once = true;
			if (et.vx1.eq(vn1))
				e1 = et;
			if (et.vx1.eq(vn2))
				e2 = et;
			et = et.next;
		}

		HEdge en1 = new HEdge(vn2,vn1,e2.prev,e1);
		HEdge en2 = en1.makeTwin();
		en2.prev = e1.prev;
		en2.next = e2;
		e1.prev.next = en2;
		e2.prev.next = en1;
		e1.prev = en1;
		e2.prev = en2;
		
		this.edge = e1;
		return new HFace(e2);
	}
	
	public void hide() {
		loadContour();
		for (int i=0; i<contour.length; i++)
			contour[i].line.hide();
	}
}
