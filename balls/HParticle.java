package balls;

import java.util.ArrayList;

import igeo.*;

public class HParticle extends HPoint implements IVecI {
	double fric = 0d;
	double m = 1d;
	double maxa = 0, da = 0;
	public IVec v = new IVec();
	public IVec a = new IVec();
	
	public HParticle(HServer server, IVecI pos) {
		super(server,pos);
	}
	
	public HParticle fric(double f) {
		fric = f;
		return this;
	}
	
	public HParticle push(IVecI f) {
		IVecI force = f.cp();
		force.mul(1/m);
		double d = force.len();
		if (d > maxa) maxa = d;
		a.add(force);
		return this;
	}
	
	public HParticle pull(IVecI f) {
		return push(f.cp().flip());
	}
	
	public void update() {
		da = maxa;
		maxa = 0;
		v.add(a.mul(server.updateRate));
		a.set(0,0,0);
		pos.add(v.cp().mul(server.updateRate));
		v.mul(1-fric);
	}

	public void del() {
		server.agents.remove(this);
	}
	
	public HParticle dup() {
		return new HParticle(server, this);
	}
}
