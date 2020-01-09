package balls;

import igeo.*;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.*;

public class HBall extends HAgent {
	public int ptNum;
	public double radius, maxRadius = 0;
	public static double strength = 100;
	public HParticle[] pts;
	public double div, space, tension = 8, fric = .2;
	public double grow0 = 7000, grow1 = 8000;
	public ArrayList<HTL> lines = new ArrayList<HTL>();
	
	public HBoundary bound = null;

	public HBall(HServer server, IVecI o, double r) {
		this(server, o, r, 8);
	}
	
	public HBall(HServer server, IVecI o, double r, double div) {
		this(server, o, r, 8, 8);
	}
	
	public HBall(HServer server, IVecI o, double r, double div, double tension) {
		this.server = server;
		this.div = div;
		this.tension = tension;
		space = div/2;
		server.addAgent(this);;
		ptNum = (int) (2 * r * Math.PI / div);
		radius = r;
		pts = new HParticle[ptNum];
		for (int i = 0; i < ptNum; i++) {
			double ang = 2 * Math.PI * i / ptNum;
			pts[i] = new HParticle(server, new IVec(r * Math.cos(ang), r * Math.sin(ang)));
			pts[i].hide();
			pts[i].pos.add(o);
			pts[i].fric(fric);
		}
		for (int i = 0; i < ptNum; i++) {
			lines.add(new HTL(server,pts[i], pts[(i + 1) % ptNum], 0, strength*tension));
//			new HStraightener(server, pts[(i +pts.length- 1) % ptNum],pts[i],pts[(i + 1) % ptNum]).tension(1000);
		}
	}
	
	public HBall(HServer server, Polygon poly, double div, double tension) {
		LineString c = poly.getExteriorRing();
		this.server = server;
		this.div = div;
		this.tension = tension;
		space = div/2;
		server.addAgent(this);
		
		Coordinate[] coors = c.getCoordinates();
		IVec[] vxs = new IVec[coors.length];
		for (int i=0; i<coors.length; i++)
			vxs[i] = new IVec(coors[i].x,coors[i].y);
		
		if (area(vxs)<0) {
			IVec[] temp = new IVec[vxs.length];
			for (int i=0; i<vxs.length; i++)
				temp[i] = vxs[vxs.length-1-i];
			vxs = temp;
		}
		ICurve ic = new ICurve(vxs,1,true);
		
		radius = Math.sqrt(area(vxs)/Math.PI);
		System.out.println(radius);
		double length = c.getLength();
		ptNum = (int) (length/div);
		pts = new HParticle[ptNum];
		for (int i=0; i<ptNum; i++) {
			pts[i] = new HParticle(server,ic.pt((double) i/ptNum));
			pts[i].hide();
			pts[i].fric(fric);
		}
		for (int i = 0; i < ptNum; i++) {
			lines.add(new HTL(server,pts[i], pts[(i + 1) % ptNum], 0, strength*tension));
//			new HStraightener(server, pts[(i +pts.length- 1) % ptNum],pts[i],pts[(i + 1) % ptNum]).tension(1000);
		}
	}
	
	public static boolean isClockwise(IVecI pt1, IVecI pt2, IVecI pt3) {
		return pt2.dif(pt1).cross(pt3.dif(pt2)).z()<0;
	}
	
	public static IVecI[] DPReduce(IVecI[] pts, double threshold) {
		int idx = 0;
		double max = 0;
		IVec pt1 = pts[0].get();
		IVec pt2 = pts[pts.length-1].get();
		for (int i=1; i<pts.length-1; i++) {
			double dist = pts[i].get().distToSegment(pt1, pt2);
			if (dist>max) {
				max = dist;
				idx = i;
			}
		}
		if (max>threshold) {
			IVecI[] pts1 = new IVecI[idx+1];
			for (int i=0; i<=idx; i++)
				pts1[i] = pts[i];
			IVecI[] pts2 = new IVecI[pts.length-idx];
			for (int i=0,j=idx; j<pts.length; i++,j++)
				pts2[i] = pts[j];
			IVecI[] res1 = DPReduce(pts1,threshold);
			IVecI[] res2 = DPReduce(pts2,threshold);
			IVecI[] res = new IVecI[res1.length+res2.length-1];
			for (int i=0; i<res1.length; i++)
				res[i] = res1[i];
			for (int i=res1.length, j=1; j<res2.length; i++,j++)
				res[i] = res2[j];
			return res;
		} else {
			return new IVecI[]{pts[0],pts[pts.length-1]};
		}
	}
	
	public IVecI[] reduce(double threshold) {
		if (threshold == 0) return pts;
		int idx = 0;
		double max = 0;
		for (int i=1; i<pts.length; i++)  {
			double dist = pts[0].dist(pts[i]);
			if (dist > max) {
				max = dist;
				idx = i;
			}
		}
		IVecI[] pts2 = new IVecI[pts.length+1];
		int j = idx;
		for (int i=0; i<pts2.length; i++) {
			pts2[i] = pts[j];
			j = (j+1)%pts.length;
		}
		IVecI[] res = DPReduce(pts2, threshold);
		return res;
	}
	
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
	
	public double area() { return area(pts); }
	
	public static IVec closestPt(IVecI[] c, IVec pt) {
		int minIdx = -1;
		double dist = 0, minDist = 0;
		for (int i=0; i<c.length; i++) {
			dist = pt.dist(c[i]);
			if (i==0 || dist<minDist) {
				minDist = dist;
				minIdx = i;
			}
		}
		IVec pt0 = c[(minIdx+c.length-1)%c.length].get();
		IVec pt1 = c[minIdx].get();
		IVec pt2 = c[(minIdx+1)%c.length].get();
		double r1 = pt.ratioOnSegment(pt0, pt1);
		double r2 = pt.ratioOnSegment(pt1, pt2);
		if(r1<0){ r1=0; } else if(r1>1.0){ r1=1.0; } // inside segment
		if(r2<0){ r2=0; } else if(r2>1.0){ r2=1.0; } // inside segment
		IVec cp1 = pt0.sum(pt1,r1);
		IVec cp2 = pt1.sum(pt2,r2);
		if (pt.dist(cp1)<pt.dist(cp2))
			return cp1;
		else
			return cp2;
	}
	
	public IVec nml(int i) {
		IVec pt1 = pts[i].pos;
		IVec pt0 = pts[(i+ptNum-1)%ptNum].pos;
		IVec pt2 = pts[(i+1)%ptNum].pos;
		IVec nml1 = pt2.dif(pt1).rot(IG.z, -Math.PI/2).unit();
		IVec nml2 = pt1.dif(pt0).rot(IG.z, -Math.PI/2).unit();
		return nml1.sum(nml2).unit();
	}
	
	public IVec nml(HParticle pt) {
		int index = 0;
		for (; index<ptNum && pts[index]!=pt; index++);
		if (index == ptNum)
			return null;
		else
			return nml(index);
	}
	
	public double angle(int i) {
		IVec pt1 = pts[i].pos;
		IVec pt0 = pts[(i+ptNum-1)%ptNum].pos;
		IVec pt2 = pts[(i+1)%ptNum].pos;
		IVec l1 = pt1.dif(pt0);
		IVec l2 = pt2.dif(pt1);
		return l1.angle(l2,IG.z);
	}
	
	public void push(IVecI v) {
		for (int i=0; i<ptNum; i++)
			pts[i].push(v);
	}
	
	public void grow(double t) { radius *= t; }
	
	public double length() {
		double l = 0;
		for (int i=0; i<pts.length; i++)
			l += pts[i].dist(pts[(i+1)%pts.length]);
		return l;
	}
	
	public void rebuild() {
		double len = length();
		int num2 = (int) (len/div);
		HParticle[] pts2 = new HParticle[num2];
		int pti = 1;
		double ptt0 = 0;
		double ptt1 = pts[0].dist(pts[1])/len;
		for (int i=0; i<num2; i++) {
			double t = (double) i/num2;
			while (ptt1 <= t) {
				pti++;
				ptt0 = ptt1;
				ptt1 += pts[pti-1].dist(pts[pti%pts.length])/len;
			}
			double r = (t-ptt0)/(ptt1-ptt0);
			pts2[i] = new HParticle(server,pts[pti-1].sum(pts[pti%pts.length],r));
			pts2[i].hide();
			pts2[i].fric(fric);
		}
		for (int i=0; i<pts.length; i++)
			pts[i].del();
		pts = pts2;
		ptNum = num2;
		for (int i=0; i<lines.size(); i++)
			lines.get(i).del();
		lines.clear();
		for (int i=0; i<pts.length; i++)
			lines.add(new HTL(server,pts[i], pts[(i + 1) % ptNum],0, strength*tension));
		System.out.println("rebuild");
	}
	
	public void interact(ArrayList<HAgent> agents) {
		for (int k=0; k<agents.size(); k++) {
			HAgent agent = agents.get(k);
			if (agent != this && agent instanceof HBall) {
				HBall other = (HBall) agent;
				if (bound == null && other.bound == null || bound != null && other.bound != null && bound == other.bound) 
				for (int i=0; i<ptNum; i++) {
					for (int j=0; j<other.ptNum; j++) {
						double dist = pts[i].dist(other.pts[j]);
						if (dist < (space + other.space)) {
							double force = Math.pow((space + other.space - dist)/(space + other.space), 3);
							IVec dif = nml(i).len(force*strength*25);
							other.pts[j].push(dif);
						}
						
					}
				}
			}
		}
	}
	
	public void update() {
		double area = area();
		
		double d = (radius - Math.sqrt(area/Math.PI))*strength*10;
//		System.out.println(d);
		for (int i=0; i<ptNum; i++) {
			IVec dif = nml(i).len(d);
			pts[i].push(dif);
		}

//		double delta = Math.pow(radius, 2)*Math.PI-area;
//		System.out.println(delta);
//		if (delta < grow0)
//			if (maxRadius==0 || radius<maxRadius) grow(1.001);
//		if (delta > grow1) grow(.99);
		
		double maxa = 0;
		for (int i=0; i<pts.length; i++)
			if (pts[i].da>maxa) maxa = pts[i].da;
		System.out.println(maxa);
		if (maxa < grow0) if (maxRadius==0 || radius<maxRadius) grow(1.001);
		if (maxa > grow1) grow(.999);
		
		if (length()/ptNum > div*1.2 || length()/ptNum < div*.92) rebuild();
	}

	public void drawGraphic() {}
}
