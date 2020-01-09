package make_grid;

import igeo.*;

public class HEdge {
	public HVec vx1, vx2;
	public HEdge prev, next;
	public HEdge twin = null;
	public HFace face;
	public ICurve line;

	public HEdge(HVec vx1, HVec vx2, HEdge prev, HEdge next) {
		this.vx1 = vx1;
		this.vx2 = vx2;
		vx1.addEdge(this);
		vx2.addEdge(this);
		line = new ICurve(vx1, vx2);
		this.prev = prev;
		this.next = next;
	}

	public double len() {
		return vx1.dist(vx2);
	}
	
	public IVec mid() {
		return vx1.mid(vx2);
	}

	public HEdge makeTwin() {
		twin = new HEdge(vx2, vx1, null, null);
		twin.twin = this;
		return twin;
	}

	public void setTwin(HEdge t) {
		if (t != null) {
			twin = t;
			t.twin = this;
		}
	}

	public HEdge split(HVec vx3) {
		if (vx3.eq(vx1) || vx3.eq(vx2))
			return null;
		HEdge e2 = new HEdge(vx3, vx2, this, next);
		vx2 = vx3;
		next = e2;
		if (twin != null) {
			e2.setTwin(twin);
			setTwin(twin.split(vx3));
		}
		line.del();
		line = new ICurve(vx1, vx2);
		return e2;
	}

	public HEdge split(double t) {
		HVec vx3 = new HVec(vx1.sum(vx2.dif(vx1).mul(t)));
		return split(vx3);
	}
	
	public void move(IVec v) {
		
	}
}
