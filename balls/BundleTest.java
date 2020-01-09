package balls;

import java.util.ArrayList;

import igeo.*;
import processing.core.*;

public class BundleTest extends PApplet {
	public void setup(){ 
		size(1000,750,IG.GL);
		for(int i=0; i < 4; i++){
			new LineAgent(new IParticle(IRand.pt(-40,-40,0,40,40,0)).hide().fric(0.2), IRand.dir(IG.zaxis).len(2), null);
		}
//		for(int i=0; i < 20; i++){
//			new IAttractor(IRand.pt(-200,-200,0,200,200,0)).linear(50).intensity(-30); //repulsion force
//		}
		IG.bg(1.0,1.0,1.0);
		IG.top();
	}
	
	class LineAgent extends IParticle{ 
		LineAgent parent, root;
		boolean isColliding;
		IVec dir;
		
		LineAgent(IParticle parent, IVec dir, LineAgent root){
			super(parent.pos().cp(dir));
			if(parent instanceof LineAgent){
				this.parent = (LineAgent)parent;
			}
			isColliding=false;
			hide(); // hide point
			this.dir = dir;
			fric(0.2);
			if(root!=null){ this.root = root; }
			else{ this.root = this; }
		}
		
		public void interact(ArrayList< IDynamics > agents){ 
			if(time()==0){ //only in the first time
				for(int i=0; i < agents.size(); i++){
					if(agents.get(i) instanceof LineAgent){
						LineAgent lineAgent = (LineAgent)agents.get(i); 
						if(lineAgent!=this){ //agents include "this"
							if(lineAgent.parent!=null && lineAgent.pos().dist(pos()) < pos().dist(parent.pos())*2){
								IVec intxn = IVec.intersectSegment(lineAgent.parent.pos(),lineAgent.pos(),parent.pos(),pos());
								if( intxn != null && !intxn.eq(parent.pos()) && !lineAgent.isColliding ){
									isColliding = true;
									return;
								}
							}
						}
					}
				}
			}
			else if(time()==1){
				for(int i=0; i < agents.size(); i++){
					if(agents.get(i) instanceof LineAgent){
						LineAgent lineAgent = (LineAgent)agents.get(i); 
						if(lineAgent!=this && lineAgent.alive()){ //agents include "this"
							if(lineAgent.root != root){ // different branch
								IVec dif = lineAgent.pos().dif(pos());
								if(dif.len() < dir.len()*2.5 && dif.len()>dir.len()){ // within threshold
									ISpringLine sp = 
											new ISpringLine(lineAgent,this,5,dir.len()*1.5).tension(5);
									double t = -Math.cos(IG.time()*0.015)*0.5+0.5;   
									sp.hsb(0.7-t*0.2, 1, 0.8-t*0.2, 0.2);
								}
							}
						}
					}
				}
			}
		}
		
		public void update(){
			if( isColliding ){ 
				del();
			}
			else{
				if(time()==0 && alive()){
					if(parent!=null && parent.alive()){
						ISpringLine ln = new ISpringLine(this, parent,100);
						double t = -Math.cos(IG.time()*0.015)*0.5+0.5; 
						ln.hsb( 0.7-t*0.2, 1, 0.8-t*0.2, 0.2); // color by tim
						if(parent.parent!=null && parent.parent.alive()){
							IStraightenerCurve st
							= new IStraightenerCurve(this,parent,parent.parent).tension(100);
							st.hsb( 0.7-t*0.2, 1, 0.8-t*0.2, 0.2);
						}
					}
				}
				if(time()==4){
					IVec dir2 = dir.cp();
					double angle = PI*0.12 ;
					if( IRand.pct(50) ){ 
						dir.rot(angle);
						dir2.rot(-angle);
					}
					else{
						dir.rot(-angle);
						dir2.rot(angle);
					}
					new LineAgent(this, dir, root);
					if( IRand.pct(20) ){ // 20% branching probability
						new LineAgent(this,dir2, null);
					}
				}
			}
		}
	}
}
