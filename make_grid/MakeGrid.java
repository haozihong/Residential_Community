package make_grid;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

import igeo.*;
import plan1212.GHouse;
import plan1212.Mapper;
import processing.core.*;

public class MakeGrid extends PApplet {
	private HRectGrid grid;
	Modify modify;
	ICompoundField field;
	ArrayList<GHouse> houseList = new ArrayList<GHouse>();

	int m = 10;
	int n = 10;
	double size = 5d;
	float height = 1.2f;
	int growNum = 155;
	GeometryFactory gf = new GeometryFactory();
	
	public void setup() {
		size(1000, 750, IG.GL);
		IG.open("curve_field.3dm");
		ICurve[] crvs = IG.curves();
		for(int i=0;i<crvs.length;i++){
			crvs[i].hide();
		}
		//IG.darkBG();
		field = new ICompoundField();
		for (int i = 0; i < IG.curveNum(); i++) {
			field.add(new ICurveTangentField(IG.curve(i)).gauss(10).intensity(10));
		}
		//new IFieldVisualizer(0,0,0, 100,100,0, 40,40,1);

		grid = new HRectGrid(m, n, 5d);
		//draw grid
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				HFace rect = grid.get(i, j);
				rect.loadContour();
				for (HEdge e : rect.contour) {
					e.line.hsb((double) i / 10d, (double) j / 10d, 1d);
				}
			}
		}
		
		//set modify
		ArrayList<HVec> pts = new ArrayList<HVec>();
		for (HVec[] vxs2 : grid.vxs) {
			for (HVec vx :vxs2) {
				pts.add(vx);
			}
		}
		modify = new Modify(pts, field);
		modify.start();
		
		IG.top();
	}

	public void keyPressed() {
		if (key == '`') {
			modify.stop();
		}
		if (key == 'g') {
			GHouse g0 = new GHouse(grid.get(m/2, n/2) ,1);
			houseList.add(g0);

			randomSeed(2000);
			for(int count=0;count<1;){
				//每次生长
				houseList = grow(houseList);
				//删除在阴影里的块				
				boolean[] testShadow = new boolean[houseList.size()];
				ArrayList<Integer> remove = new ArrayList<Integer>();
				for(int i=0;i<houseList.size();i++){
					testShadow[i] = testField(houseList.get(i));
					if(testShadow[i] && (!(houseList.get(i).getLayer()==1))){
						remove.add(i);
					}
				}
				for(int i=0;i<remove.size();i++){
					int s = remove.get(remove.size()-1-i);
					houseList.get(s).changeBuilt();
					houseList.remove(s);
				}	
				//户数达到数量后停止
				int residentNum = 0;
				for(int i=0;i<houseList.size();i++){
					if(houseList.get(i).getLayer()>1){
						residentNum++;
					}
				}
				println(residentNum);
				if(residentNum>=growNum){
					count++;
				}
			}
			//对上方没有长住宅的一层挖院子
			ArrayList<Integer> removeF = new ArrayList<Integer>();
			for(int i=0;i<houseList.size();i++){
				if(houseList.get(i).getLayer()==1 && houseList.get(i).getHFace().getbuilt(2)==false){
						removeF.add(i);
				}
			}
			
			for(int i=0;i<removeF.size();i++){
				int s = removeF.get(removeF.size()-1-i);
		    	if(random(0,1) > 0.2){
					houseList.get(s).changeBuilt();
			    	houseList.remove(s);
		    	}
			}
			//删除一层孤立点
			ArrayList<Integer> removeG = new ArrayList<Integer>();
			for(int i=0;i<houseList.size();i++){
				HEdge[] edge = houseList.get(i).getHFace().contour();
				boolean f = false;	
				for(int j=0;j<edge.length;j++){
					if(edge[j].twin!=null && edge[j].twin.face.getbuilt(2)==true){
						f = true;
					}
				}
				if(f ==false && houseList.get(i).getLayer()==1 && houseList.get(i).getHFace().getbuilt(2)==false){
					removeG.add(i);
				}  
			}
		
			for(int i=0;i<removeG.size();i++){
				int s = removeG.get(removeG.size()-1-i);
				houseList.get(s).changeBuilt();
			    houseList.remove(s);
			}
			//删除一层遮挡南侧的点
			ArrayList<Integer> removeS = new ArrayList<Integer>();
			for(int i=0;i<houseList.size();i++){
				if(houseList.get(i).getLayer()==1 && houseList.get(i).getHFace().getbuilt(2)==false){
					HEdge edge = houseList.get(i).getHFace().contour[2];
					if(edge.twin !=null){
						HFace twin = edge.twin.face;
						if(twin.getbuilt(2) == true){
							removeS.add(i);
							println(i);
						}
					}
				}  
			}
		
			for(int i=0;i<removeS.size();i++){
				int s = removeS.get(removeS.size()-1-i);
				houseList.get(s).changeBuilt();
			    houseList.remove(s);
			}
			drawFinalGHouse();
		}
	}
	
	public ArrayList<GHouse> grow(ArrayList<GHouse> houseList){
		for(int co=0;co<1;){
			int houseChoice = (int)random(0,houseList.size());
			HEdge[] edges = houseList.get(houseChoice).getHFace().contour();
			int edgeChoice;
			if(houseList.get(houseChoice).getLayer() == 1){
				if(random(0,1)>0.2){
					edgeChoice = edges.length;
				}else{
				edgeChoice = (int)random(0, edges.length);
				}
			}else{
				edgeChoice = edges.length;
			}
			GHouse newHouse = null;
			if(edgeChoice == edges.length){
				if(houseList.get(houseChoice).getLayer()+1<7){
					if(!houseList.get(houseChoice).getHFace().getbuilt(houseList.get(houseChoice).getLayer()+1)){	
							newHouse = new GHouse (houseList.get(houseChoice).getHFace(), houseList.get(houseChoice).getLayer()+1);	
					}
				}
				if(newHouse != null){
					if(!testField(newHouse) && testLength(newHouse)<3){
						houseList.add(newHouse);
						co++;
					}else{
						newHouse.changeBuilt();
					}
			    }
			}else{
				HEdge twinEdge = edges[edgeChoice].twin;
				if(twinEdge != null){
					HFace nextFace = twinEdge.face;
					if(!nextFace.getbuilt(houseList.get(houseChoice).getLayer())){
						newHouse = new GHouse(nextFace, houseList.get(houseChoice).getLayer());
					}
				}
				if(newHouse != null){
					houseList.add(newHouse);
					co++;
			    }
			}
		
		}
		return houseList;
	}

	public int testLength(GHouse house){
		int count = 0;
		ArrayList<Geometry> geoHouseList = new ArrayList<Geometry>();
		for(int i=0;i<houseList.size();i++){
			Geometry g = changeGro(houseList.get(i));
			geoHouseList.add(g);
		}
		Geometry geoHouse = changeGro(house);

		for(int k=0;k<10;k++){
			for(int i=0;i<geoHouseList.size();i++){
				if(geoHouse.buffer(0.1).overlaps(geoHouseList.get(i)) && (!(houseList.get(i).layer == 1))){
					geoHouse = geoHouse.union(geoHouseList.get(i));
					count++;
					break;
				}
			}
		}
		return count;
	}
	
	//日照
	public boolean testField(GHouse house){
		ArrayList<Geometry> fieldList = new ArrayList<Geometry>();
		boolean e = false;
		for(int i=0;i<houseList.size();i++){
			if(houseList.get(i).layer > 1){
				IVec[] vec = houseList.get(i).face.vertex();
				Coordinate p0 = new Coordinate(vec[3].x - 6, vec[3].y + 2.5);
				Coordinate p1 = new Coordinate(vec[2].x + 6, vec[2].y + 2.5);
				Coordinate p2 = new Coordinate(vec[2].x + 6, vec[2].y + houseList.get(i).layer*height*1.2);
				Coordinate p3 = new Coordinate(vec[3].x - 6, vec[3].y + houseList.get(i).layer*height*1.2);
				LinearRing lr = gf.createLinearRing(new Coordinate[]{p0,p1,p2,p3,p0});
				Geometry g = gf.createPolygon(lr,null);
				fieldList.add(g);
			}
		}
				
		Geometry geoHouse = changeGro(house);
		for(int i=0;i<fieldList.size();i++){
			if(geoHouse.overlaps(fieldList.get(i)) || geoHouse.within(fieldList.get(i))){
				e = true;
			}
		}
		return e;
	}
		
	public void drawFinalGHouse(){
		IG.open("test.3dm");
		ISurface s = IG.layer("s").surfaces()[0].clr(.4,.4).hide();
		ICurve[] crvs = IG.curves();
		for(int i=0;i<crvs.length;i++){
			crvs[i].hide();
		}
		
		for(int i=0;i<m;i++){
			for(int j=0;j<n;j++){
				HFace f = grid.get(i, j);
				int layer = 0;
				for(int k=1;k<8;k++){
					if(f.getbuilt(k)==false){
						layer = k-1;
						break;
					}
				}
		    	
				IVec[] vec = f.vertex();
				ISurface bottom = new ISurface(vec[3],vec[0],vec[1],vec[2]);
			    if(layer!=0 && layer!=1){
					Mapper mapper = new Mapper(s,bottom);
					ArrayList<ICurve> cs = new ArrayList<ICurve>();
					for (ICurve c : IG.layer("g").curves()) {
						c.clr(1.,0,0);
						ICurve mc = mapper.map(c).clr(1.,0,0);
						cs.add(mc);
					}
					//住宅
					for(int k=0;k<cs.size();k++){
						if(k==0){
							IVec[] vectop = new IVec[cs.get(k).cps().length];
							for(int m=0;m<cs.get(k).cps().length;m++){
								vectop[m] = (IVec) cs.get(k).cps()[m];
							}
							for(int m=0;m<cs.get(k).cps().length;m++){
								vectop[m].z = layer*height;
							}
							ISurface top = new ISurface(vectop).clr(255,0,0);
							IG.extrude(vectop, -layer*height).clr(255,0,0);
						}
						if(k==1){
							IVec[] vectop = new IVec[cs.get(k).cps().length];
							for(int m=0;m<cs.get(k).cps().length;m++){
								vectop[m] = (IVec) cs.get(k).cps()[m];
							}
							for(int m=0;m<cs.get(k).cps().length;m++){
								vectop[m].z = layer*height+0.5;
							}
							ISurface top = new ISurface(vectop).clr(255,0,0);
							IG.extrude(vectop, -layer*height-0.5).clr(255,0,0);
						}
						if(k==2||k==3){
							IVec[] vectop = new IVec[cs.get(k).cps().length];
							for(int m=0;m<cs.get(k).cps().length;m++){
								vectop[m] = (IVec) cs.get(k).cps()[m];
							}
							for(int m=0;m<cs.get(k).cps().length;m++){
								vectop[m].z = layer*height;
							}
							ISurface top = new ISurface(vectop).clr(255,0,0);
							IG.extrude(vectop, -layer*height).clr(255,0,0);
						}
					}
			    }
			    //一层
			    if(layer == 1){
					IVec[] vectop = new IVec[vec.length];
					for(int k=0;k<vectop.length;k++){
						vectop[k] = new IVec(0,0,0);
					}
					for(int k=0;k<vectop.length;k++){
						vectop[k].x = vec[k].x();
						vectop[k].y = vec[k].y();
						vectop[k].z = height;
					}
					ISurface top = new ISurface(vectop).clr(255,255,0);
					IG.extrude(vectop, 1, true, -height).clr(255,255,0);
			    }
			}	
		}
	}
	
	public Geometry changeGro(GHouse house){
		IVec[] vec = house.getHFace().vertex();
		Coordinate p0 = new Coordinate(vec[0].x, vec[0].y);
		Coordinate p1 = new Coordinate(vec[1].x, vec[1].y);
		Coordinate p2 = new Coordinate(vec[2].x, vec[2].y);
		Coordinate p3 = new Coordinate(vec[3].x, vec[3].y);
		LinearRing lr = gf.createLinearRing(new Coordinate[]{p0,p1,p2,p3,p0});
		Geometry geoHouse = gf.createPolygon(lr,null);
		return geoHouse;
	}
}
