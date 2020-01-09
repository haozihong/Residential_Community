package balls;

import java.util.ArrayList;

import igeo.*;

public class HTL extends HAgent {
	HParticle pt1, pt2;
	double len, tension; // proportial coefficient

	HTL(HServer server, HParticle p1, HParticle p2, double l, double t) {
		this.server = server;
		server.addAgent(this);
		pt1 = p1;
		pt2 = p2;
		len = l;
		tension = t;
	}

	HTL(HServer server, HParticle p1, HParticle p2, double t) {
		this(server, p1, p2, p1.pos.dist(p2.pos), t);
	}

	HTL(HServer server, IVec p1, IVec p2, double l, double t) {
		this(server, new HParticle(server,p1), new HParticle(server,p2), l, t);
	}

	HTL(HServer server, IVec p1, IVec p2, double t) {
		this(server, new HParticle(server, p1), new HParticle(server, p2), p1.dist(p2) * .7, t);
	}

	void setTension(double t) {	tension = t; }

	public void interact(ArrayList<HAgent> agents) {
		IVec dif = pt2.pos.dif(pt1.pos);
		double dl = dif.len();
//		if (dl > len) {
			dif.len(Math.abs(dl - len)).mul(tension);
			if (dl-len<0) dif.flip();
			pt1.push(dif);
			pt2.pull(dif); // opposite force
//		}
	}

	public void drawGraphic() {
		server.main.line((float) pt1.pos.x, (float) pt1.pos.y, (float) pt2.pos.x, (float) pt2.pos.y);
	}
	
	public void del() {
		server.agents.remove(this);
	}
}
