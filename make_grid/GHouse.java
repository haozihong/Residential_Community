package make_grid;

import igeo.IG;
import igeo.ISurface;
import igeo.IVec;
import make_grid.HFace;

public class GHouse {
	HFace face;
	int layer;
	float height = 1.2f;
	
	GHouse(HFace face, int layer){
		this.face = face;
		this.layer = layer;
		face.built[layer] = true;
	}

	public HFace getHFace(){
		return face;
	}
	
	public int getLayer(){
		return layer;
	}
	
	public void changeBuilt(){
		face.built[layer] = false;
	}
	
	public void drawGHouse(){
		IVec[] vec = face.vertex();
		IVec[] vecb = new IVec[vec.length];
		for(int i=0;i<vecb.length;i++){
			vecb[i] = vec[i].sum(new IVec(0,0,height*(layer-1)));
		}
		IG.extrude(vecb, 1, true, height);
		
		IVec[] vect = new IVec[vec.length];
		for(int i=0;i<vect.length;i++){
			vect[i] = vec[i].sum(new IVec(0,0,height*layer));
		}
		new ISurface(vect);
	}
	
	public void drawFinalGHouse(){
		IVec[] vec = face.vertex();
		IVec[] vecb = new IVec[vec.length];
		for(int i=0;i<vecb.length;i++){
			vecb[i] = vec[i].sum(new IVec(0,0,height*(layer-1)));
		}
		IG.extrude(vecb, 1, true, height);
		
		IVec[] vect = new IVec[vec.length];
		for(int i=0;i<vect.length;i++){
			vect[i] = vec[i].sum(new IVec(0,0,height*layer));
		}
		new ISurface(vect);
	}
}
