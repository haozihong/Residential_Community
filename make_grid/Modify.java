package make_grid;

import igeo.*;
import java.util.ArrayList;
import java.util.Random;

public class Modify implements Runnable {
	Thread thread;
	double radius = 7d;
	int time = 0, duration = -1;
	Random ran;
	ArrayList<HVec> pts;
	ICompoundField field;
	
	public Modify(ArrayList<HVec> pts, ICompoundField field) {
		this.pts = pts;
		this.field = field;
		ran = new Random(14800);
	}
	
	public void start() {
		thread = new Thread(this);
		time = 0;
		thread.start();
	}
	
	public void stop() {
		thread = null;
		System.out.println("hh");
	}
	
	public int time() {return time;}
	
	public void duration(int dur) {duration = dur;}
	
	private void randPt() {
		HVec pt = pts.get((int) (ran.nextDouble()*pts.size()));
		while (pt.fixed)
			pt = pts.get((int) (ran.nextDouble()*pts.size()));
		ArrayList<HFace> relaFaces = new ArrayList<HFace>();
		for (HEdge edge : pt.edges) {
			relaFaces.add(edge.face);
		}
		double value0 = 1;
		for (HFace face : relaFaces) {
			value0 *= face.value(field);
		}
		IVec dif = IRand.dir2(ran.nextDouble()*radius);
		double value1 = 1;
		pt.add(dif);
		for (HFace face : relaFaces) {
			value1 *= face.value(field);
		}
		if (value1<value0) pt.sub(dif);
	}
	
	public void run() {
		Thread thisThread = Thread.currentThread();
		while (thread == thisThread) {
			if (duration>=0 && time>=duration) {
				stop();
				break;
			}
			randPt();
			time++;
		}
	}
}
