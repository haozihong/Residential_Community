package make_grid;

import igeo.*;
import plan1212.GHouse;
import plan1212.Mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;

public class Growing implements Runnable {
	Thread thread;
	GeometryFactory gf = new GeometryFactory();
	HVorGrid xx;
	HVorGrid[] xxz;
	int[] divNumz;
	ArrayList<GHouse> houseList;
	ArrayList<GHouse> onehouseList = new ArrayList<GHouse>();
	int growNum;
	int divNum;
	double height;
	Geometry boundaryGeo;
	ICurve river, boundary;
	ICurve[] greenland, road;
	ICompoundField field = new ICompoundField();
	double maxRiverDis, maxGreenDis, maxRoadDis;
	ISurface s;

	int r = 50;
	int serviceNum;
	int minLength = 4;
	int minL = 6;

	int minArea = 90;
	double deltaDegree = Math.PI/8;
	double minangle = Math.PI /4;
	int seed = 1600;
	double totalArea = 166029;

	Growing(ArrayList<GHouse> houseList, int divNum, int growNum, double height, Geometry boundaryGeo, HVorGrid xx,
			int serviceNum, ICurve river, ICurve[] road, ICurve[] greenland, ICurve boundary, HVorGrid[] xxz,
			int[] divNumz) {
		this.houseList = houseList;
		this.height = height;
		this.boundaryGeo = boundaryGeo;
		this.divNum = divNum;
		this.growNum = growNum;
		this.xx = xx;
		this.serviceNum = serviceNum;
		this.river = river;
		this.road = road;
		this.boundary = boundary;
		this.greenland = greenland;
		this.xxz = xxz;
		this.divNumz = divNumz;
		field.add(new ICurveTangentField(boundary).gauss(25).intensity(10));

		IVecI[] vec = boundary.cps();
		maxRiverDis = -1;
		maxGreenDis = -1;
		maxRoadDis = -1;
		for (int i = 0; i < vec.length; i++) {
			double distRiver = river.dist(vec[i]);
			if (distRiver > maxRiverDis) {
				maxRiverDis = distRiver;
			}
			for (int j = 0; j < road.length; j++) {
				double distRoad = road[j].dist(vec[i]);
				if (distRoad > maxRoadDis) {
					maxRoadDis = distRoad;
				}
			}
			for (int j = 0; j < greenland.length; j++) {
				double distGreen = greenland[j].dist(vec[i]);
				if (distGreen > maxGreenDis) {
					maxGreenDis = distGreen;
				}
			}
		}
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		thread = null;
	}

	public void run() {
		Thread thisThread = Thread.currentThread();
		int oldNum = 0;
		int co =0;
		while (thread == thisThread) {
			houseList = grow(houseList);
			houseList = reduce(houseList);
			int residentNum = calResidentNum(houseList);
			System.out.println(residentNum);

			boolean m = false;
			if (residentNum > growNum) {
				m = booleangrow(houseList);
				if(oldNum == residentNum){
					co++;
				}else{
					oldNum = residentNum;
				}
				if(co==50){
					m = true;
				}
			}

			if (residentNum > growNum && m == true) {
				houseList = reduceOne(houseList);
				houseList = getSevice(houseList);
				
				//画生成泡泡的平面
				ArrayList<HFace> faces = new ArrayList<HFace>();
				for (int i = 0; i < divNum; i++) {
					HFace f = xx.get(i);
					if (f.getbuilt(1)) {
						faces.add(f);
					}
				}
				ArrayList<ICurve> crv = contour(faces);
				for (ICurve c : crv)
					c.clr(1., 0, 0).layer("contour");

				houseList = addbusiness(houseList);

				for (int i = 0; i < houseList.size(); i++) {
					houseList.get(i).drawGHouse();
				}

				double FAR = calFAR(houseList, totalArea);
				System.out.println(FAR);
	
				//final
				ICurve[] crvs = IG.curves(); 
				for(int i=0;i<crvs.length;i++){crvs[i].hide(); }
				//draw grid 
				for(int i=0;i<xxz.length;i++){ 
					for(int j=0;j<divNumz[i];j++){
						HFace f = xxz[i].get(j); 
						IVecI[] grid = f.vertex(); 
						new ICurve(grid, 1, true); 
					} 
				}
				
				for(int i=0;i<divNum;i++){ 
					HFace f = xx.get(i); 
					IVecI[] grid = f.vertex(); 
					new ICurve(grid, 1, true);

					int layer = 0; 
					for(int k=1;k<8;k++){
						if(f.getbuilt(k)==false){ 
							layer = k-1;
							break; 
						} 
					}

				 if(layer > 1){ 
					 IVec[] vec = poly90degree(f);
					 drawFinalGHouse(vec, layer); 
					 } 
				 if(layer == 1){ 
					 IVec[] vec = f.vertex(); 
					 drawFinalGHouse(vec, layer); 
					 } 
				 }	
				this.stop();
			}
		}
	}

	public ArrayList<GHouse> grow(ArrayList<GHouse> houseList) {
		int houseChoice = -1, edgeChoice = -1;
		double bestValue = 10;
		for (int i = 0; i < houseList.size(); i++) {
			if (houseList.get(i).getLayer() == 1) {
				HEdge[] edges = houseList.get(i).getHFace().contour();
				for (int j = 0; j < edges.length; j++) {
					HEdge nextEdge = edges[j].twin;
					if (nextEdge != null) {
						HFace f = nextEdge.face;
						double value = value(f, 1, river, road, greenland, boundary, field);
						if (value < bestValue && (!f.getbuilt(1)) && nextEdge.len() > minLength
								&& changeGeo(f).getArea() > minArea-50 && cal90degree(houseList.get(i).getHFace()) == 4
								) {
							bestValue = value;
							houseChoice = i;
							edgeChoice = j;
						}
					}
				}
				double value = value(houseList.get(i).getHFace(), 2, river, road, greenland, boundary, field);
				if (value < bestValue && (!houseList.get(i).getHFace().getbuilt(2))
						&& cal90degree(houseList.get(i).getHFace()) == 4
						&& changeGeo(houseList.get(i).getHFace()).getArea() > minArea
						&& calMinEdge(houseList.get(i).getHFace())> minL
						&& (!testField(houseList.get(i).getHFace())) && testLength(houseList.get(i).getHFace()) < 5) {
					bestValue = value;
					houseChoice = i;
					edgeChoice = edges.length;
				}
			} else {
				HEdge[] edges = houseList.get(i).getHFace().contour();
				double value = value(houseList.get(i).getHFace(), houseList.get(i).getLayer() + 1, river, road,
						greenland, boundary, field);
				if (value < bestValue && houseList.get(i).getLayer() + 1 < 7
						&& !houseList.get(i).getHFace().getbuilt(houseList.get(i).getLayer() + 1)
						&& !testField(houseList.get(i).getHFace())) {
					bestValue = value;
					houseChoice = i;
					edgeChoice = edges.length;
				}
			}
		}

		Random random = new Random();
		if (houseChoice == -1) {
			int rhouseChoice = random.nextInt(divNum);
			HFace f = xx.get(rhouseChoice);
			if ((!f.getbuilt(1)) && changeGeo(f).getArea() > minArea) {
				GHouse newHouse = new GHouse(f, 1);
				houseList.add(newHouse);
				System.out.println("m");
			}
		} else {
			HEdge[] edges = houseList.get(houseChoice).getHFace().contour();
			if (edgeChoice == edges.length) {
				GHouse newHouse = new GHouse(houseList.get(houseChoice).getHFace(),
						houseList.get(houseChoice).getLayer() + 1);
				houseList.add(newHouse);
			} else {
				HFace nextFace = edges[edgeChoice].twin.face;
				GHouse newHouse = new GHouse(nextFace, houseList.get(houseChoice).getLayer());
				houseList.add(newHouse);
			}
		}
		return houseList;
	}

	public boolean booleangrow(ArrayList<GHouse> houseList) {
		int houseChoice = -1, edgeChoice = -1;
		double bestValue = 10;
		for (int i = 0; i < houseList.size(); i++) {
			if (houseList.get(i).getLayer() == 1) {
				HEdge[] edges = houseList.get(i).getHFace().contour();
				for (int j = 0; j < edges.length; j++) {
					HEdge nextEdge = edges[j].twin;
					if (nextEdge != null) {
						HFace f = nextEdge.face;
						double value = value(f, 1, river, road, greenland, boundary, field);
						if (value < bestValue && (!f.getbuilt(1)) && nextEdge.len() > minLength
								&& changeGeo(f).getArea() > minArea) {
							bestValue = value;
							houseChoice = i;
							edgeChoice = j;
						}
					}
				}
				double value = value(houseList.get(i).getHFace(), 2, river, road, greenland, boundary, field);
				if (value < bestValue && (!houseList.get(i).getHFace().getbuilt(2))
						&& cal90degree(houseList.get(i).getHFace()) == 4 && (!testField(houseList.get(i).getHFace()))
						&& testLength(houseList.get(i).getHFace()) < 5) {
					bestValue = value;
					houseChoice = i;
					edgeChoice = edges.length;
				}
			} else {
				HEdge[] edges = houseList.get(i).getHFace().contour();
				double value = value(houseList.get(i).getHFace(), houseList.get(i).getLayer() + 1, river, road,
						greenland, boundary, field);
				if (value < bestValue && houseList.get(i).getLayer() + 1 < 7
						&& !houseList.get(i).getHFace().getbuilt(houseList.get(i).getLayer() + 1)
						&& (!testField(houseList.get(i).getHFace()))) {
					bestValue = value;
					houseChoice = i;
					edgeChoice = edges.length;
				}
			}
		}
		boolean m = false;
		if (houseChoice == -1 || edgeChoice == -1) {
			System.out.println("max");
			m = true;
		}
		return m;
	}

	public double value(HFace face, int layer, ICurve river, ICurve[] road, ICurve[] greenland, ICurve boundary,ICompoundField field) {
		Geometry h = changeGeo(face);
		Point p = h.getCentroid();
		IVec center = new IVec(p.getX(), p.getY());

		double distRiver = river.dist(center) / maxRiverDis;
		double distGreenland = 1;
		double distRoad = 1;
		for (int i = 0; i < greenland.length; i++) {
			double dist = greenland[i].dist(center) / maxGreenDis;
			if (dist < distGreenland) {
				distGreenland = dist;
			}
		}
		for (int i = 0; i < road.length; i++) {
			double dist = (maxRoadDis - road[i].dist(center)) / maxRoadDis;
			if (dist < distRoad) {
				distRoad = dist;
			}
		}

		HEdge[] edge = face.contour();
		double f = 1;
		for (int j = 0; j < edge.length; j++) {
			if (edge[j].twin != null && edge[j].twin.face.getbuilt(layer) == true) {
				f = 0.001;
			}
		}
		return Math.pow(distRiver, 1.5) * distGreenland * distRoad * f;
	}

	//减去在阴影里的块
	public ArrayList<GHouse> reduce(ArrayList<GHouse> houseList) {
		boolean[] testShadow = new boolean[houseList.size()];
		ArrayList<Integer> remove = new ArrayList<Integer>();
		for (int i = 0; i < houseList.size(); i++) {
			if (!(houseList.get(i).getLayer() == 1)) {
				testShadow[i] = testField(houseList.get(i).getHFace());
			}
			if (testShadow[i] && (!(houseList.get(i).getLayer() == 1))) {
				remove.add(i);
				System.out.println(i);
			}
		}
		for (int i = 0; i < remove.size(); i++) {
			int s = remove.get(remove.size() - 1 - i);
			houseList.get(s).changeBuilt();
			houseList.get(s).delGHouse();
			houseList.remove(s);
		}
		return houseList;
	}

	public int calResidentNum(ArrayList<GHouse> houseList) {
		int residentNum = 0;
		for (int i = 0; i < houseList.size(); i++) {
			if (houseList.get(i).getLayer() > 1) {
				residentNum++;
			}
		}
		return residentNum;
	}

	// 删除孤立的六层
	public ArrayList<GHouse> reduceSix(ArrayList<GHouse> houseList) {
		ArrayList<Integer> removeG = new ArrayList<Integer>();
		for (int i = 0; i < houseList.size(); i++) {
			HEdge[] edge = houseList.get(i).getHFace().contour();
			boolean f = false;
			for (int j = 0; j < edge.length; j++) {
				if (edge[j].twin != null && edge[j].twin.face.getbuilt(2) == true) {
					f = true;
				}
			}
			if (f == false && houseList.get(i).getLayer() > 1) {
				removeG.add(i);
			}
		}
		for (int i = 0; i < removeG.size(); i++) {
			int s = removeG.get(removeG.size() - 1 - i);
			houseList.get(s).changeBuilt();
			houseList.get(s).delGHouse();
			houseList.remove(s);
		}
		return houseList;
	}

	// 删除一层孤立点
	public ArrayList<GHouse> reduceOne(ArrayList<GHouse> houseList) {
		ArrayList<Integer> removeG = new ArrayList<Integer>();
		for (int i = 0; i < houseList.size(); i++) {
			HEdge[] edge = houseList.get(i).getHFace().contour();
			boolean f = false;
			for (int j = 0; j < edge.length; j++) {
				if (edge[j].twin != null && edge[j].twin.face.getbuilt(2) == true) {
					f = true;
				}
			}
			if (f == false && houseList.get(i).getLayer() == 1 && houseList.get(i).getHFace().getbuilt(2) == false) {
				removeG.add(i);
			}
		}
		for (int i = 0; i < removeG.size(); i++) {
			int s = removeG.get(removeG.size() - 1 - i);
			houseList.get(s).changeBuilt();
			houseList.get(s).delGHouse();
			houseList.remove(s);
		}
		return houseList;
	}

	// 增加一层商业
	public ArrayList<GHouse> addbusiness(ArrayList<GHouse> houseList) {
		ICurve[] crvs = IG.layer("business").curves();
		for(int i=0;i<crvs.length;i++){
			crvs[i].hide();
		}
		for (int k = 0; k < crvs.length; k++) {
			IVecI[] vec = crvs[k].cps();
			Geometry line = gf.createLineString(new Coordinate[] { new Coordinate(vec[0].x(), vec[0].y()),
					new Coordinate(vec[1].x(), vec[1].y()) });

			for (int i = 0; i < divNum; i++) {
				if (changeGeo(xx.get(i)).intersects(line) && xx.get(i).getbuilt(1) == false) {
					houseList.add(new GHouse(xx.get(i), 1));
				}
			}
		}
		return houseList;
	}

	// divide list
	public ArrayList<GHouse> getSevice(ArrayList<GHouse> houseList) {
		ArrayList<Integer> removeF = new ArrayList<Integer>();
		for (int i = 0; i < houseList.size(); i++) {
			if (houseList.get(i).getLayer() == 1 && houseList.get(i).getHFace().getbuilt(2) == false) {
				onehouseList.add(houseList.get(i));
				removeF.add(i);
			}
		}

		for (int i = 0; i < removeF.size(); i++) {
			int s = removeF.get(removeF.size() - 1 - i);
			houseList.get(s).changeBuilt();
			houseList.get(s).delGHouse();
			houseList.remove(s);
		}

		System.out.println(onehouseList.size());
		for (int i = 0; i < houseList.size(); i++) {
			System.out.println(houseList.get(i).layer);
		}

		// 求公共
		HFace[] face = xx.vors;
		int serviceSum = 0;
		for (int i = 0; i < face.length; i++) {
			if (face[i].getbuilt(2) == true) {
				serviceSum++;
			}
		}

		ArrayList<Integer> choiceList = new ArrayList<Integer>();
		for (int i = 0; i < serviceNum; i++) {
			choiceList.add(i);
		}
		int count = calService(choiceList);
		Random random = new Random(seed);
		for (int i = 0; i < 1;) {
			int choice = random.nextInt(serviceNum);
			int old = choiceList.get(choice);
			choiceList.set(choice, random.nextInt(onehouseList.size()));
			int newcount = calService(choiceList);

			if (newcount > count) {
				count = newcount;
			} else {
				choiceList.set(choice, old);
			}
			if (count == serviceSum) {
				i++;
			}
		}
		drawFinalService(choiceList);
		for (int i = 0; i < choiceList.size(); i++) {
			GHouse ser = new GHouse(onehouseList.get(choiceList.get(i)).face, 1);
			houseList.add(ser);
		}

		return houseList;
	}

	public int calService(ArrayList<Integer> choiceList) {
		int count = 0;
		ArrayList<Geometry> serviceList = new ArrayList<Geometry>();
		for (int i = 0; i < serviceNum; i++) {
			Geometry geo = changeGeo(onehouseList.get(choiceList.get(i)));
			Point p = geo.getCentroid();
			Geometry circle = createCircle(p.getX(), p.getY(), r);
			for (int j = 0; j < houseList.size(); j++) {
				Geometry g = changeGeo(houseList.get(j).face);
				if (circle.intersects(g)) {
					if (serviceList.size() == 0) {
						serviceList.add(g);
						count++;
					} else {
						boolean b = false;
						for (int k = 0; k < serviceList.size(); k++) {
							if (serviceList.get(k).equals(g)) {
								b = true;
							}
						}
						if (b == false) {
							serviceList.add(g);
							count++;
						}
					}
				}
			}
		}
		return count;
	}

	public void drawFinalService(ArrayList<Integer> choiceList) {
		ArrayList<Geometry> serviceList = new ArrayList<Geometry>();
		for (int i = 0; i < serviceNum; i++) {
			Geometry geo = changeGeo(onehouseList.get(choiceList.get(i)));
			Point p = geo.getCentroid();
			Geometry circle = createCircle(p.getX(), p.getY(), r);
			new ICircle(p.getX(), p.getY(), 2, r);
			for (int j = 0; j < houseList.size(); j++) {
				Geometry g = changeGeo(houseList.get(j).face);
				if (circle.intersects(g)) {
					if (serviceList.size() == 0) {
						serviceList.add(g);
					} else {
						boolean b = false;
						for (int k = 0; k < serviceList.size(); k++) {
							if (serviceList.get(k).equals(g)) {
								b = true;
							}
						}
						if (b == false) {
							serviceList.add(g);
						}
					}
				}
			}
		}
	}

	public int testLength(HFace face) {
		int count = 0;
		ArrayList<Geometry> geoHouseList = new ArrayList<Geometry>();
		for (int i = 0; i < houseList.size(); i++) {
			Geometry g = changeGeo(houseList.get(i));
			geoHouseList.add(g);
		}
		Geometry geoHouse = changeGeo(face);

		for (int k = 0; k < 10; k++) {
			for (int i = 0; i < geoHouseList.size(); i++) {
				if (geoHouse.buffer(0.1).overlaps(geoHouseList.get(i)) && (!(houseList.get(i).layer == 1))) {
					geoHouse = geoHouse.union(geoHouseList.get(i));
					count++;
					break;
				}
			}
		}
		return count;
	}

	public boolean testField(HFace face) {
		ArrayList<Geometry> fieldList = new ArrayList<Geometry>();
		IVec[] vec0 = poly90degree(face);
		boolean e = false;
		for (int i = 0; i < houseList.size(); i++) {
			if (houseList.get(i).getLayer() > 1) {
				IVec[] vec = poly90degree(houseList.get(i).getHFace());
				Coordinate p0 = new Coordinate(vec[0].x ,vec[0].y + houseList.get(i).layer*height);
				Coordinate p1 = new Coordinate(vec[1].x ,vec[1].y + houseList.get(i).layer*height);
				Coordinate p2 = new Coordinate(vec[2].x ,vec[2].y + houseList.get(i).layer*height);
				Coordinate p3 = new Coordinate(vec[3].x ,vec[3].y + houseList.get(i).layer*height);

				LinearRing lr = gf.createLinearRing(new Coordinate[] { p0, p1, p2, p3, p0 });
				Geometry g = gf.createPolygon(lr, null);
				fieldList.add(g);
			}
		}		
		double ymin = 10000, ymin2 = 10000;
		int y1 = -1, y2 = -1;
		for (int j = 0; j < vec0.length; j++) {
			if (vec0[j].y() < ymin) {
				ymin = vec0[j].y();
				y1 = j;
			}
		}
		for (int j = 0; j < vec0.length; j++) {
			if (vec0[j].y() < ymin2 && j != y1) {
				ymin2 = vec0[j].y();
				y2 = j;
			}
		}
		
		Geometry line = gf.createLineString(new Coordinate[]{new Coordinate(vec0[y1].x(),vec0[y1].y()),new Coordinate(vec0[y2].x(),vec0[y2].y())});
		Geometry line0 = gf.createLineString(new Coordinate[]{new Coordinate(vec0[y1].x(),vec0[y1].y()),new Coordinate(vec0[(y1-1+vec0.length)%vec0.length].x(),vec0[(y1-1+vec0.length)%vec0.length].y())});
		Geometry line1 = gf.createLineString(new Coordinate[]{new Coordinate(vec0[y2].x(),vec0[y2].y()),new Coordinate(vec0[(y2-1+vec0.length)%vec0.length].x(),vec0[(y2-1+vec0.length)%vec0.length].y())});
		double dist1 = vec0[y1].dist(vec0[(y1-1+vec0.length)%vec0.length]);
		double dist2 = vec0[y2].dist(vec0[(y2-1+vec0.length)%vec0.length]);

		for (int i = 0; i < fieldList.size(); i++) {
			if(Math.abs(dist2 - dist1)>15){
				if (line0.intersects(fieldList.get(i)) || line1.intersects(fieldList.get(i))) {
					e = true;
				}
			}else{
				if (line.intersects(fieldList.get(i))) {
					e = true;
				}
			}
		}
		return e;
	}

	public Geometry changeGeo(GHouse house) {
		IVec[] vec = house.getHFace().vertex();
		Coordinate[] coords = new Coordinate[vec.length + 1];
		for (int i = 0; i < coords.length; i++) {
			if (i == coords.length - 1) {
				coords[i] = new Coordinate(vec[0].x, vec[0].y);
			} else {
				coords[i] = new Coordinate(vec[i].x, vec[i].y);
			}
		}
		LinearRing lr = gf.createLinearRing(coords);
		Geometry geoHouse = gf.createPolygon(lr, null);
		return geoHouse;
	}

	public Geometry changeGeo(HFace face) {
		IVec[] vec = face.vertex();
		Coordinate[] coords = new Coordinate[vec.length + 1];
		for (int i = 0; i < coords.length; i++) {
			if (i == coords.length - 1) {
				coords[i] = new Coordinate(vec[0].x, vec[0].y);
			} else {
				coords[i] = new Coordinate(vec[i].x, vec[i].y);
			}
		}
		LinearRing lr = gf.createLinearRing(coords);
		Geometry geoHouse = gf.createPolygon(lr, null);
		return geoHouse;
	}

	public int cal90degree(HFace face) {
		IVec[] vec = face.vertex();
		int count = 0;
		for (int i = 0; i < vec.length; i++) {
			double angle = IVec.angle(vec[i], vec[(i + 1) % vec.length], vec[(i + 2) % vec.length]);
			if (angle < (Math.PI / 2 + deltaDegree) && angle > (Math.PI / 2 - deltaDegree)) {
				count++;
			}
		}
		return count;
	}

	public double calminangle(HFace face) {
		double minAngle = 100;
		IVec[] vec = face.vertex();
		for (int i = 0; i < vec.length; i++) {
			double angle = IVec.angle(vec[i], vec[(i + 1) % vec.length], vec[(i + 2) % vec.length]);
			if (angle < minAngle) {
				minAngle = angle;
			}
		}
		return minAngle;
	}

	public IVec[] poly90degree(HFace face) {
		IVec[] vec = face.vertex();
		ArrayList<IVec> polyList = new ArrayList<IVec>();
		for (int i = 0; i < vec.length; i++) {
			double angle = IVec.angle(vec[i], vec[(i + 1) % vec.length], vec[(i + 2) % vec.length]);
			if (angle < (Math.PI / 2 + deltaDegree) && angle > (Math.PI / 2 - deltaDegree)) {
				polyList.add(vec[(i + 1) % vec.length]);
			}
		}
		IVec[] poly = new IVec[polyList.size()];
		for (int i = 0; i < polyList.size(); i++) {
			poly[i] = polyList.get(i);
		}
		return poly;
	}

	public ArrayList<ICurve> contour(ArrayList<HFace> faces) {
		ArrayList<ICurve> curves = new ArrayList<ICurve>();
		ArrayList<HEdge> edges = new ArrayList<HEdge>();
		for (HFace face : faces)
			edges.addAll(Arrays.asList(face.contour()));
		for (int i = 0; i < edges.size(); i++)
			if (edges.get(i).twin != null && edges.indexOf(edges.get(i).twin) != -1) {
				edges.remove(edges.get(i).twin);
				edges.remove(i);
				i--;
			}
		while (edges.size() > 0) {
			ArrayList<IVec> pts = new ArrayList<IVec>();
			HEdge edge = edges.get(0);
			pts.add(edge.vx1);
			while (edge != null) {
				HVec pt = edge.vx2;
				pts.add(pt);
				edges.remove(edge);
				edge = null;
				for (HEdge e : pt.edges)
					if (edges.indexOf(e) != -1) {
						edge = e;
						break;
					}
			}
			curves.add(new ICurve(pts.toArray(new IVec[pts.size()]), 1, true));
		}
		return curves;
	}

	public void drawFinalGHouse(IVec[] vec, int layer){
		if (layer != 0 && layer != 1) {
			int start = -1;
			double x = -10000;
			double y = -10000;

			double ymax = -10000, ymax2 = -10000;
			int y1 = -1, y2 = -1;
			for (int j = 0; j < vec.length; j++) {
				if (vec[j].y() > ymax) {
					ymax = vec[j].y();
					y1 = j;
				}
			}
			for (int j = 0; j < vec.length; j++) {
				if (vec[j].y() > ymax2 && j != y1) {
					ymax2 = vec[j].y();
					y2 = j;
				}
			}
	
			for (int j = 0; j < vec.length; j++) {
					if (start == -1 && j != y1 && j != y2) {
						start = (j+1) % vec.length;
						y = Math.abs(vec[j].dif(vec[(j + 1) % vec.length]).angle(IG.x));
						x = vec[j].dist(vec[(j+1)%vec.length]);
					} else {
						if ((vec[j].dist(vec[(j+1)%vec.length]) - x > 15 || Math.abs(vec[j].dif(vec[(j + 1) % vec.length]).angle(IG.x)) < y)&& j != y1 && j != y2 ) {
							start = (j+1) % vec.length;
							x = vec[j].dist(vec[(j+1)%vec.length]);
							y = Math.abs(vec[j].dif(vec[(j + 1) % vec.length]).angle(IG.x));
						}
					}
			}
			ICurve line1 = new ICurve(vec[(start+1)%vec.length].x(), vec[(start+1)%vec.length].y(), vec[(start+1)%vec.length].z(),vec[(start+2)%vec.length].x(), vec[(start+2)%vec.length].y(), vec[(start+2)%vec.length].z());
			ICurve line2 = new ICurve(vec[(start+3)%vec.length].x(), vec[(start+3)%vec.length].y(), vec[(start+3)%vec.length].z(),vec[(start)%vec.length].x(), vec[(start)%vec.length].y(), vec[(start)%vec.length].z());

			IVec v1 = line1.mid();
			IVec v2 = line2.mid();

			new ISurface(v1.x(),v1.y(),v1.z(),v2.x(),v2.y(),v2.z(),v2.x(),v2.y(),v2.z()+height*layer,v1.x(),v1.y(),v1.z()+height*layer).clr(255,0,0);
			
			IVec vsz = vec[(start+1)%vec.length].dif(v1).mul(1.5/vec[(start+1)%vec.length].dif(v1).len());
			IVec vx1 = v1.cp().add(vsz);
			IVec vsy = vec[(start+2)%vec.length].dif(v1).mul(1.5/vec[(start+2)%vec.length].dif(v1).len());
			IVec vx2 = v1.cp().add(vsy);
			IVec vsx = v2.cp().dif(v1).mul(6/v2.dif(v1).len());
			IVec vxx = v1.cp().add(vsx);
			IVec vx3 = vxx.cp().add(vsy);
			IVec vx4 = vxx.cp().add(vsz);

			new ISurface(vx1.x(),vx1.y(),vx1.z(),vx2.x(),vx2.y(),vx2.z(),vx3.x(),vx3.y(),vx3.z(),vx4.x(),vx4.y(),vx4.z()).clr(255,0,0);
			IVec[] vc  = new IVec[4];
			vc[0] = vx1;
			vc[1] = vx2;
			vc[2] = vx3;
			vc[3] = vx4;
			ICurve c = new ICurve(vc, 1, true).layer("4");
			IG.extrude(c, -height*layer).clr(255,0,0).layer("4");
			
			ICurve a = new ICurve(vec, 1, true).clr(255,0,0).layer("4");
			ICurve b = new ICurve(vec, 1, true).layer("4");
			IG.extrude(b, -height*layer).clr(255,0,0).layer("4");
		}
		if (layer == 1) {
			IVec[] vectop = new IVec[vec.length];
			for (int k = 0; k < vectop.length; k++) {
				vectop[k] = new IVec(0, 0, 0);
			}
			for (int k = 0; k < vectop.length; k++) {
				vectop[k].x = vec[k].x();
				vectop[k].y = vec[k].y();
				vectop[k].z = height;
			}
			ISurface top = new ISurface(vectop).clr(255, 255, 0).layer("1").clr(255,255,0);
			ISurface[] side = new ISurface[vectop.length];
			for (int i = 0; i < side.length - 1; i++) {
				side[i] = new ISurface(vectop[i].x(), vectop[i].y(), vectop[i].z(), vectop[(i + 1)].x(),
						vectop[(i + 1)].y(), vectop[(i + 1)].z(), vectop[(i + 1)].x(), vectop[(i + 1)].y(),
						vectop[(i + 1)].z() - height, vectop[i].x(), vectop[i].y(), vectop[i].z() - height).layer("1").clr(255,255,0);
			}
			side[side.length - 1] = new ISurface(vectop[0].x(), vectop[0].y(), vectop[0].z(),
					vectop[side.length - 1].x(), vectop[side.length - 1].y(), vectop[side.length - 1].z(),
					vectop[side.length - 1].x(), vectop[side.length - 1].y(), vectop[side.length - 1].z() - height,
					vectop[0].x(), vectop[0].y(), vectop[0].z() - height).layer("1").clr(255,255,0);
		}
	}

	public Geometry createCircle(double x, double y, final double r) {
		final int num = 12;// 圆上面的点个数
		Coordinate coords[] = new Coordinate[num + 1];
		for (int i = 0; i < num; i++) {
			double angle = ((double) i / (double) num) * Math.PI * 2.0;
			double dx = Math.cos(angle) * r;
			double dy = Math.sin(angle) * r;
			coords[i] = new Coordinate((double) x + dx, (double) y + dy);
		}
		coords[num] = coords[0];
		LinearRing ring = gf.createLinearRing(coords);
		Geometry circle = gf.createPolygon(ring);
		return circle;
	}
	
	public double calFAR(ArrayList<GHouse> houseList, double totalArea){
		double areaSum = 0;
		for(int i=0;i<houseList.size();i++){
			areaSum += changeGeo(houseList.get(i).getHFace()).getArea();
		}
		double FAR = areaSum/totalArea;
		return FAR;
	}
	
	public double calMinEdge(HFace face){
		IVec[] vec = poly90degree(face);
		double minL = 1000;
		for(int i=0;i<vec.length;i++){
			if(vec[i].dist(vec[(i+1)%vec.length])<minL){
				minL = vec[i].dist(vec[(i+1)%vec.length]);
			}
		}
		return minL;
	}
}
