package balls;

import igeo.*;

public final class ClosestPt {
	public IVec cp = null, cpv1, cpv2, outerDir;
	public double minDist = 0;
	
	public ClosestPt(IVecI[] pts, IVec pt) {
		for (int i=0; i<pts.length; i++) {
			IVec pt0 = pts[i].get();
			IVec pt1 = pts[(i+1)%pts.length].get();
			double r = pt.ratioOnSegment(pt0, pt1);
			if(r<0){ r=0; } else if(r>1.0){ r=1.0; } // inside segment
			IVec cpt = pt0.sum(pt1,r);
			double dist = cpt.dist(pt);
			if (cp == null || dist<minDist) {
				cp = cpt;
				cpv1 = pt0;
				cpv2 = pt1;
				minDist = dist;
			}
		}
		outerDir = cpv2.dif(cpv1).rot(-Math.PI/2);
	}
}
