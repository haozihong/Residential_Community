package balls;

import java.util.ArrayList;

import igeo.IVec;

public class HStraightener extends HAgent {
	
	public HParticle pt1,pt2,pt3;
	public double tension = 1d;
	public boolean constantTension = false;
	public double maxTension = -1;
	
	HStraightener(HServer server, HParticle pt1, HParticle pt2, HParticle pt3) {
		this.server = server;
		server.addAgent(this);
		this.pt1 = pt1;
		this.pt2 = pt2;
		this.pt3 = pt3;
	}
	
	public HStraightener tension(double tension) {
		this.tension = tension;
		return this;
	}
	
	public HStraightener constant(boolean cnst) {
		constantTension = cnst;
		return this;
	}
	
	public HStraightener maxTension(double maxTension) {
		this.maxTension = maxTension;
		return this;
	}
	
	
	public void interact(ArrayList<HAgent> agents) {
		IVec p1 = pt1.get(), p2 = pt2.get(), p3 = pt3.get();
		IVec dif = p3.diff(p1);
		
		
		dif.mul(p2.diff(p1).dot(dif)/dif.len2()).add(p1).sub(p2);
		if(!constantTension){
		    dif.mul(tension);
		    if(maxTension>=0 && dif.len()>maxTension){
			dif.len(maxTension);
		    }
		}
		else if(dif.len2()>0){ dif.len(tension); }
		
		// adding force to the middle point
		pt2.push(dif);
		// adding force to the end points, half in the opposite direction.
		dif.mul(-0.5);
		pt1.push(dif);
		pt3.push(dif);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawGraphic() {
		// TODO Auto-generated method stub
		
	}
}
