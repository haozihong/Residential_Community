package balls;

import processing.core.*;

import java.io.IOException;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.*;
import igeo.*;
import make_grid.HVorGrid;
import gzf.gui.*;

public class T1_BigBalls extends PApplet {
	JTSRender render;
	CameraController cam;
	public HServer server;
	public int updateRate = 10; //in milliseconds

	double fir = 1.;
	double step = .9995;
	ArrayList<HBoundary> bounds = new ArrayList<HBoundary>();
	Geometry[] geos = null;
	ArrayList<IVec> vorCenter = new ArrayList<IVec>();
	String workDir = "";//"D:/Hao Zihong/Study/´óËÄ/2/site/";
	String inputFile = "block0104.3dm";
	String saveFile = "bigballs01_0110_02.3dm";
	
	int slct = 0;
	
	public void setup() {
		size(1000,750,P3D);
		cam = new CameraController(this,1000);
		cam.top();
		render = new JTSRender(this);
		server = new HServer(this);
		IRand.init(0);
		IG.init();
		IG.open(workDir + inputFile);
		
		System.out.println(IG.layer("block").curveNum());
		for (int i=0; i<IG.layer("block").curveNum(); i++) {
			ICurve block = IG.layer("block").curve(i);
			double area = Math.abs(HBall.area(block.cps()));
			int num = (int) (area / 20000);
			System.out.println(block.isClosed());
			System.out.println(num);
			
			bounds.add(initBlock(block, num));
		}

		server.updateRate = 10d/1000;
		server.start();
		server.pause();
	}
	
	public void draw() {
		background(255);

		server.drawAgents();
		
		if (server.running)
		for (HBoundary bound : bounds) {
			double sc = step;
			if (Math.abs(bound.sc-1)>.0001) {
				while(bound.sc*sc<1) {
					sc += (1-sc)*.1;
				}
				bound.scale(IVec.center(bound.cps),sc);
			}
		}
		
//		String size = "";
//		for (int i=0; i<bounds.size(); i++)
//			for (int j=0; j<bounds.get(i).balls.size(); j++)
//				size += (int) bounds.get(i).balls.get(j).radius+", ";
//		System.out.println(size);
	}
	
	public void keyPressed() {
		if (key == 'p' || key == 'P') server.running = !server.running;
		if (key == '1') {
			for (HBoundary bound : bounds)
				for (HBall b : bound.balls)
					b.radius /= step;
		}
		if (key == '9') server.stop();
		if (key == '0') save(workDir + saveFile);

	}
	
	public HBoundary initBlock(ICurve block, int ptNum) {
		HBoundary bound = new HBoundary(server, block);

		Polygon site = IJTool.toJTSPolygon(block);
		System.out.println(site);
		ArrayList<Coordinate> ptsj = new ArrayList<Coordinate>();
		GeometryCollection vor = HVorGrid.makeVorRandom(site,ptNum,0,0);
		for (int t=0; t<20; t++) {
			ptsj.clear();
			int num = vor.getNumGeometries();
			for (int i=0; i<num; i++) {
				Geometry geo = vor.getGeometryN(i);
				Coordinate[] vxs = geo.getCoordinates();
				double totW = 0, x = 0, y = 0;
				for (int j=0; j<vxs.length-1; j++) {
					Coordinate vx1 = vxs[j];
					Coordinate vx2 = vxs[j+1];
					double w = vx1.distance(vx2);
					x += (vx1.x+vx2.x)/2*w;
					y += (vx1.y+vx2.y)/2*w;
					totW += w;
				}
				x /= totW;
				y /= totW;
				ptsj.add(new Coordinate(x,y));
			}
			vor = HVorGrid.makeVor(site, ptsj, 0);
		}
		
		IVec[] pts = new IVec[ptsj.size()];
		for (int i=0; i<pts.length; i++) {
			pts[i] = new IVec(ptsj.get(i).x, ptsj.get(i).y);
			vorCenter.add(pts[i]);
		}
		
		double[] dm = new double[ptNum];
		for (int i=0; i<ptNum; i++)
			dm[i] = new ClosestPt(block.cps(),pts[i]).minDist;
		for (int i=0; i<ptNum-1; i++) {
			for (int j=i+1; j<ptNum; j++) {
				double d = pts[i].dist(pts[j])/2;
				if (d<dm[i])
					dm[i] = d;
				if (d<dm[j])
					dm[j] = d;
			}
		}
		for (int i=0; i<ptNum; i++) {
			HBall ball = new HBall(server, pts[i], dm[i], 5, 10);
			ball.space = 15;
			ball.grow0 = 7000;
			ball.grow1 = 8000;
//			ball.maxRadius = 80;
			bound.addBall(ball);
			ball.bound = bound;
		}
		bound.scale(IVec.center(bound.cps),fir);
		return bound;
	}
	
	public void save(String fileName) {
		server.pause();
		IG.init();
		for (int i=0; i<server.agents.size(); i++) {
			HAgent agent = server.agents.get(i);
			if (agent instanceof HBall) {
				new ICurve(((HBall) agent).reduce(0),1,true).layer("bubble_ori");
				IVecI[] rdc = ((HBall) agent).reduce(2);
				new ICurve(rdc,1,true).layer("bubble");
			}
			if (agent instanceof HBoundary) {
				new ICurve(((HBoundary) agent).cps,1,true).layer("block").clr(1.,0,0);
			}
			IG.layer("block").clr(1.,0,0);
		}
		for (int i=0; i<vorCenter.size(); i++)
			new IPoint(vorCenter.get(i)).layer("vorPt");
		IG.save(fileName);
	}
}
