package make_grid;

import igeo.*;
import java.util.ArrayList;

public class HVec extends IVec {
	ArrayList<HEdge> edges = new ArrayList<HEdge>();
	public boolean fixed = false;
	
	HVec(IVec v) {
		super(v);
	}
	
	HVec(double x, double y) {
		super(x,y);
	}
	
	public void addEdge(HEdge edge) {
		edges.add(edge);
	}
	
	public boolean removeEdge(HEdge edge) {
		return edges.remove(edge);
	}
}
