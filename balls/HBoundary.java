package balls;

import java.util.ArrayList;
import com.vividsolutions.jts.geom.*;
import igeo.*;
import processing.core.PApplet;

public class HBoundary extends HAgent {
	public ArrayList<HBall> balls = new ArrayList<HBall>();
	public Polygon oriBound = null;
	public IVec[] cps;
	public double sc = 1;
	public int clr = 0xFF000000;
	float strokeW = 1.8f;
	
	public HBoundary(HServer server, Polygon c) {
		this.server = server;
		server.addAgent(this);

		Coordinate[] coors = c.getCoordinates();
		cps = new IVec[coors.length-1];
		for (int i=0; i<coors.length-1; i++)
			cps[i] = new IVec(coors[i].x,coors[i].y);
	}
	
	public HBoundary(HServer server, ICurve ic) {
		this.server = server;
		server.addAgent(this);
		
		cps = new IVec[ic.cpNum()-1];
		for (int i=0; i<cps.length; i++)
			cps[i] = ic.cp(i);
	}
	
	public void addBall(HBall b) { balls.add(b); }
	
	public static Geometry mul(Geometry geo, double t) {
		Coordinate[] coors = geo.getCoordinates();
		for (Coordinate coor : coors) {
			coor.x *= t;
			coor.y *= t;
			if (!Double.isNaN(coor.z)) coor.z *= t;
		}
		return geo;
	}
	
	public void mul(double t) {
		for (IVec v : cps) {
			v.mul(t);
		}
		sc *= t;
	}
	
	public void scale(IVecI o, double t) {
		for (IVec v : cps) {
			v.scale(o, t);
		}
		sc *= t;
	}
	
	public void interact(ArrayList<HAgent> agents) {
		for (HBall b : balls) {
			for (HParticle pt : b.pts) {
				if (!pt.get().isInside2d(cps)) {
					IVec pt2 = new ClosestPt(cps, pt.get()).cp;
//					new HPoint(server, pt2);
					IVec dif = pt2.dif(pt);
					dif.len(Math.pow(dif.len(), 2)*HBall.strength);
					pt.push(dif);
				}
			}
		}
	}
	
	public void drawGraphic() {
		server.main.noFill();
		server.main.stroke(clr);
		server.main.strokeWeight(strokeW);
		server.main.beginShape();
		if (oriBound != null) {
			Coordinate[] coors = oriBound.getCoordinates();
			for (int i=0; i<coors.length; i++)
				server.main.vertex((float) coors[i].x, (float) coors[i].y);
		} else {
			for (int i=0; i<cps.length; i++)
				server.main.vertex((float) cps[i].x, (float) cps[i].y);
		}
		server.main.endShape(PApplet.CLOSE);
		server.main.stroke(0xFF000000);
		server.main.strokeWeight(1);
	}
}