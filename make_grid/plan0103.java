package make_grid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;

import igeo.*;
import plan1212.GHouse;
import plan1212.Mapper;
import processing.core.*;

public class plan0103 extends PApplet {
	ICurve river;
	ICurve[] greenland, road;
	ICurve[] boundary;
	IFieldVisualizer IF;
	Geometry[] boundaryGeo;
	HVorGrid[] xx;
	Modify[] modify;
	Growing[] growing;
	ICompoundField[] field;
	ArrayList<double[]> faceValue;
	GeometryFactory gf = new GeometryFactory();

	int[] divNum;
	double height = 3;
	int[] growNum;
	double g = 1.1;
	double deltaDegree = Math.PI/8;
	int durationNum = 600000;

	public void setup() {
		size(800, 600, IG.GL);
		IG.bg(255);
		
		IG.open("bigballs_0105.3dm");
		boundary = IG.layer("bubble").curves();
		for(int i=0;i<boundary.length;i++){
			boundary[i].hide();
		}
		ICurve[] crvs1 = IG.layer("river").curves();
		river = crvs1[0].hide();
		road = IG.layer("road").curves();
		for(int i=0;i<road.length;i++){
			road[i].hide();
		}
		greenland = IG.layer("greenland").curves();
		for(int i=0;i<greenland.length;i++){
			greenland[i].hide();
		}
		ICurve[] crvs = IG.layer("business").curves();
		for(int i=0;i<crvs.length;i++){
			crvs[i].hide();
		}
		boundaryGeo = new Geometry[boundary.length];
		for(int k=0;k<boundary.length;k++){
			IVecI[] vec = boundary[k].cps();
			Coordinate[] coors = new Coordinate[vec.length+1];
			for(int i=0;i<vec.length;i++){
				coors[i] = new Coordinate(vec[i].x(), vec[i].y());
			}
			coors[vec.length] = coors[0];
			LinearRing lr = gf.createLinearRing(coors);
			boundaryGeo[k] = gf.createPolygon(lr,null);
		}
		
		xx = new HVorGrid[boundary.length];
		divNum = new int[boundary.length];
		for(int i=0;i<boundary.length;i++){
			divNum[i] = (int)boundaryGeo[i].getArea()/180;
			println(divNum[i]);
			xx[i] = new HVorGrid(boundary[i], divNum[i], 194078);
		}
		
		field = new ICompoundField[boundary.length];
		modify = new Modify[boundary.length];
		for(int i=0;i<boundary.length;i++){
			field[i] = new ICompoundField();
			field[i].add(new ICurveTangentField(boundary[i]).gauss(25).intensity(10));
			//是否显示力场
			IF = new IFieldVisualizer(511,319,0, 1120,667,0, 40,40,1).alpha(5).arrowSize(0.01f);
		
			for (int j = 0; j < divNum[i]; j++) {
				HFace smallrect = xx[i].get(j);
				smallrect.loadContour();
				for (HEdge e : smallrect.contour) {
					e.line.clr(0.4,0.2);
				}
			}
			
			//set modify
			ArrayList<HVec> pts = new ArrayList<HVec>();
			for (HVec vx : xx[i].vxs) {
				pts.add(vx);
			}
			
			modify[i] = new Modify(pts, field[i]);
			modify[i].duration(durationNum);
			IG.top();

		}	
	}
	
	public void keyPressed() {
		if(key == 's'){
			//开始优化网格
			for(int i=0;i<boundary.length;i++){
				modify[i].start();
			}
		}
		
		if (key == 'g') {
			//开始长房子
			growNum = calGrowNum(divNum, river, boundary, boundaryGeo);	
			
			growing = new Growing[boundary.length];
			for(int i=0;i<boundary.length;i++){
				ArrayList<GHouse> houseList = new ArrayList<GHouse>();
				
				double bestValue = 100;
				int bestChoice = -1;
				for(int j=0;j<divNum[i];j++){
					HFace f = xx[i].get(j);
					ICompoundField field = new ICompoundField();
					field.add(new ICurveTangentField(boundary[i]).gauss(25).intensity(10));
					if(value(f, 1, river, road, greenland, boundary[i], field)<bestValue && cal90degree(f) == 4){
						bestValue = value(f, 1, river, road, greenland, boundary[i], field);
						bestChoice = j;
						println(bestChoice, bestValue);
					}
				}
				
				IVec[] v = xx[i].get(bestChoice).vertex();
				for(int j=0;j<v.length;j++){
					//new IPoint(v[j].x(),v[j].y()).clr(0,255,0);
				}
				GHouse g0 = new GHouse(xx[i].get(bestChoice), 1);
				houseList.add(g0);
				
				growing[i] = new Growing(houseList, divNum[i], growNum[i], height, boundaryGeo[i], xx[i], divNum[i]/18, river, road, greenland, boundary[i], xx, divNum);
				growing[i].start();
			}
		}
	}
	
	public double value(HFace face, double layer, ICurve river, ICurve[] road, ICurve[] greenland, ICurve boundary, ICompoundField field){
		IVecI[] vec = boundary.cps();
		double maxRiverDis = -1;
		double maxGreenDis = -1;
		double maxRoadDis = -1;
		for(int i=0;i<vec.length;i++){
			double distRiver = river.dist(vec[i]);
			if(distRiver > maxRiverDis){
				maxRiverDis = distRiver;
			}
			for(int j=0;j<road.length;j++){
				double distRoad = road[j].dist(vec[i]);
				if(distRoad > maxRoadDis){
					maxRoadDis = distRoad;
				}
			}
			for(int j=0;j<greenland.length;j++){
				double distGreen = greenland[j].dist(vec[i]);
				if(distGreen > maxGreenDis){
					maxGreenDis = distGreen;
				}
			}
		}
		
		Geometry h = changeGeo(face);
		Point p = h.getCentroid();
		IVec center = new IVec(p.getX(), p.getY());
		
		double distRiver = river.dist(center)/maxRiverDis;
		double distGreenland = 1;
		double distRoad = 1;
		for(int i=0;i<greenland.length;i++){
			double dist = greenland[i].dist(center)/maxGreenDis;
			if(dist<distGreenland){
				distGreenland = dist;
			}	
		}
		for(int i=0;i<road.length;i++){
			double dist = (maxRoadDis - road[i].dist(center))/maxRoadDis;
			if(dist<distRoad){
				distRoad = dist;
			}	
		}
		return Math.pow(distRiver,1.5)*distGreenland*distRoad*(layer/6);
	}
	
	public int[] calGrowNum(int[] divNum, ICurve river, ICurve[] boundary, Geometry[] boundaryGeo){
		Point[] center = new Point[boundaryGeo.length];
		double[] dist = new double[boundaryGeo.length];
		double distmax = -1;
		double distmin = 10000;
		for(int i=0;i<boundaryGeo.length;i++){
			center[i] = boundaryGeo[i].getCentroid();
			IVec v = new IVec(center[i].getX(), center[i].getY());
			dist[i] = river.dist(v);
			if(dist[i] > distmax){
				distmax = dist[i];
			}
			if(dist[i] < distmax){
				distmin = dist[i];
			}
		}
		double[] scale = new double[boundaryGeo.length];
		for(int i=0;i<boundaryGeo.length;i++){
			scale[i] = map((float)dist[i], (float)distmin, (float)distmax, 0.95f, 1f);
		}

		int[] growNum = new int[boundaryGeo.length];
		for(int i=0;i<boundaryGeo.length;i++){
			growNum[i] = (int)(divNum[i]*g*scale[i]-6);
			println(growNum[i]);
		}
		return growNum;
	}
	
	public int cal90degree(HFace face){
		IVec[] vec = face.vertex();
		int count = 0;
		for(int i=0;i<vec.length;i++){
			double angle =  IVec.angle(vec[i], vec[(i+1)%vec.length], vec[(i+2)%vec.length]);
			if(angle < (Math.PI/2+deltaDegree) && angle > (Math.PI/2-deltaDegree)){
				count++;
			}
		}
		return count;
	}
	
	public double calminangle(HFace face){
		double minAngle = 100;
		IVec[] vec = face.vertex();
		for(int i=0;i<vec.length;i++){
			double angle =  IVec.angle(vec[i], vec[(i+1)%vec.length], vec[(i+2)%vec.length]);
			if(angle < minAngle){
				minAngle = angle;
			}
		}
		return minAngle;
	}
	
	public Geometry changeGeo(GHouse house){
		IVec[] vec = house.getHFace().vertex();
		Coordinate[] coords = new Coordinate[vec.length+1];
		for(int i=0;i<coords.length;i++){
			if(i==coords.length-1){
				coords[i] = new Coordinate(vec[0].x, vec[0].y);
			}else{
				coords[i] = new Coordinate(vec[i].x, vec[i].y);
			}
		}
		LinearRing lr = gf.createLinearRing(coords);
		Geometry geoHouse = gf.createPolygon(lr,null);
		return geoHouse;
	}
	
	public Geometry changeGeo(HFace face){
		IVec[] vec = face.vertex();
		Coordinate[] coords = new Coordinate[vec.length+1];
		for(int i=0;i<coords.length;i++){
			if(i==coords.length-1){
				coords[i] = new Coordinate(vec[0].x, vec[0].y);
			}else{
				coords[i] = new Coordinate(vec[i].x, vec[i].y);
			}
		}
		LinearRing lr = gf.createLinearRing(coords);
		Geometry geoHouse = gf.createPolygon(lr,null);
		return geoHouse;
	}
}
