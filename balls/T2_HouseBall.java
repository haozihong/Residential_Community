package balls;

import igeo.*;

import processing.core.*;

import java.util.ArrayList;

import com.sun.corba.se.spi.activation.Server;
import com.vividsolutions.jts.geom.*;

import gzf.gui.*;

public class T2_HouseBall extends PApplet {
	CameraController cam;
	JTSRender render;
	ArrayList<HServer> servers = new ArrayList<HServer>();
	ArrayList<HBoundary> bounds = new ArrayList<HBoundary>();
	ArrayList<Geometry> geos = new ArrayList<Geometry>();
	String workDir = "D:/Hao Zihong/Study/´óËÄ/2/site/";
	String inputFile = "03/03-contour.3dm";
	String saveFile = "smallballs03_0111.3dm";
	int slct = 0;
	
	public void setup() {
		size(1280,720,P3D);
		smooth(8);
		
		cam = new CameraController(this,200);
		cam.top();
		cam.getCamera().parentResize();
		render = new JTSRender(this);
		IG.init();
		IG.open(workDir + inputFile);

		ICurve[] ihouses = IG.layer("house").curves();
		Polygon[] houses = new Polygon[ihouses.length];
		for (int i=0; i<ihouses.length; i++) {
			houses[i] = IJTool.toJTSPolygon(ihouses[i]);
			geos.add(houses[i]);
		}
		
		for (int i=0; i<IG.layer("bubble").curveNum(); i++) {
			ICurve ib = IG.layer("bubble").curve(i);
			Polygon oriBound = IJTool.toJTSPolygon(ib);
			Polygon boundary = (Polygon) oriBound.buffer(2);
			HServer server = new HServer(this);
			servers.add(server);
			HBoundary bound = new HBoundary(server, boundary);
			bound.oriBound = oriBound;
			server.bound = bound;
			bounds.add(bound);
			
			ArrayList<Polygon> thisH = new ArrayList<Polygon>();
			for (int j=0; j<ihouses.length; j++)
				if (ihouses[j].cp(0).isInside2d(ib.cps())) {
					thisH.add((Polygon) houses[j].buffer(3.5,2));
					new HInnerBoundary(server, (Polygon) houses[j].buffer(2,1));
				}
			Geometry houseCollection = new GeometryFactory().createGeometryCollection(thisH.toArray(new Polygon[thisH.size()]));
			Geometry union = houseCollection.buffer(0);
			int num = union.getNumGeometries();
			for (int j=0; j<num; j++) {
				Polygon house = (Polygon) union.getGeometryN(j);
				if (house.getNumPoints()<3) continue;
				HBall ball = new HBall(server, house, 1.5, 4);
				ball.space = 3;
				ball.grow0 = 900;
				ball.grow1 = 12000000;
				
				bound.addBall(ball);
				ball.bound = bound;
				}
			}
	
		servers.get(slct).bound.clr = 0xCCFF0000;
		servers.get(slct).bound.strokeW = 3;
		for (HServer server : servers) {
			server.start();
			server.pause();
		}
	}
	
	public void draw() {
		background(255);
		
		for (Geometry g : geos) {
			render.draw(g);
		}
		
		for (HServer server : servers)
			server.drawAgents();
	}
	
	public void keyPressed() {
		if (key == 'p')
			for (HServer server : servers)
				server.pause();
		if (key == 'o')
			for (HServer server : servers)
				server.resume();
		if (key == '0') save(workDir + saveFile);
		if (key == 'w') {
			servers.get(slct).bound.clr = 0xFF000000;
			servers.get(slct).bound.strokeW = 1.8f;
			slct = (slct+1)%servers.size();
			servers.get(slct).bound.clr = 0xCCFF0000;
			servers.get(slct).bound.strokeW = 3;
		}
		if (key == 'q') {
			servers.get(slct).bound.clr = 0xFF000000;
			servers.get(slct).bound.strokeW = 1.8f;
			slct = (slct-1+servers.size())%servers.size();
			servers.get(slct).bound.clr = 0xCCFF0000;
			servers.get(slct).bound.strokeW = 3;
		}
		if (key == 's')	servers.get(slct).switchStatus();
	}

	public void save(String fileName) {
		for (HServer server : servers)
			server.stop();
		IG.init();
		ArrayList<HAgent> agents = new ArrayList<HAgent>();
		for (HServer server : servers)
			agents.addAll(server.agents);
		for (int i=0; i<servers.size(); i++) {
			HBoundary bound = servers.get(i).bound;
			new ICurve(bound.cps,1,true).layer("block").clr(1.,0,0);
			for (int j=0; j<servers.get(i).agents.size(); j++) {
				HAgent agent = servers.get(i).agents.get(j);
				if (agent instanceof HBall) {
					IVecI[] rdc = ((HBall) agent).reduce(0);
					Geometry jball = IJTool.toJTSPolygon(rdc);
					jball = jball.intersection(bound.oriBound);
					rdc = IJTool.toICurve(jball.getCoordinates()).cps();
					for (IVecI pt : rdc)
						new HPoint(servers.get(0), pt).ptSize(1f);
					new ICurve(rdc,1,true).layer("bubble");
				}
				if (agent instanceof HBoundary) {
				}
				IG.layer("block").clr(1.,0,0);
			}
			IG.save(fileName);
			
		}
	}
}
