package balls;

import igeo.*;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.*;

public class HInnerBoundary extends HAgent {
	public ArrayList<HBall> balls = new ArrayList<HBall>();
//	public Polygon c;
	public IVec[] cps;
	public double space = 3;

	public HInnerBoundary(HServer server, Polygon c) {
		this.server = server;
		server.addAgent(this);
		
		Coordinate[] coors = c.getCoordinates();
		cps = new IVec[coors.length-1];
		for (int i = 0; i < cps.length; i++) {
			cps[i] = new IVec(coors[i].x,coors[i].y);
		}
		
		if (HBall.area(cps)<0) {
			IVec[] temp = new IVec[cps.length];
			for (int i=0; i<cps.length; i++)
				temp[i] = cps[cps.length-1-i];
			cps = temp;
		}
	}
	
	public HInnerBoundary(HServer server, ICurve ic) {
		this.server = server;
		server.addAgent(this);
		
		cps = new IVec[ic.cpNum()-1];
		for (int i = 0; i < cps.length; i++) {
			cps[i] = ic.cp(i);
		}

		if (HBall.area(cps)<0) {
			IVec[] temp = new IVec[cps.length];
			for (int i=0; i<cps.length; i++)
				temp[i] = cps[cps.length-1-i];
			cps = temp;
		}
	}

	public void addBall(HBall b) {
		balls.add(b);
	}
	
	public void interact(ArrayList<HAgent> agents) {
		for (int i=0; i<agents.size(); i++) {
			if (agents.get(i) != this && agents.get(i) instanceof HBall) {
				HBall b = (HBall) agents.get(i);
				for (HParticle pt : b.pts) {
					if (pt.pos.isInside2d(cps)) {
						ClosestPt clp = new ClosestPt(cps,pt.get());
//						IVec dir = clp.outerDir;
//						double force = Math.pow(space - clp.minDist, 1) * HBall.strength;
//						dir.len(force);
//						pt.push(dir);
						pt.v.set(0,0,0);
						pt.pos.set(clp.cp);
					}
				}
			}
		}
	}
}
