package make_grid;

import processing.core.*;
import igeo.*;
import java.util.ArrayList;

public class MakePlan1 extends PApplet{
	public double totArea;
	public ArrayList<Room> rooms = new ArrayList<Room>();
	public HGeo plan = new HGeo();
	
	public void setup() {
		size(1000,750,IG.GL);
//		fileInput("roomlist.txt");
		initRooms();
		IG.x.show().clr(1.,0,0);
		IG.y.show();
	}
	
	public void fileInput(String fileName) {
		String[] lines = loadStrings(fileName);
		totArea = 0;
		for (int i=0; i<lines.length; i++) {
			String[] line = splitTokens(lines[i]);
			int id = Integer.parseInt(line[0]);
			String name = line[1];
			double area = Double.parseDouble(line[2]);
			totArea += area;
			Room room = new Room(id,name,area);
			rooms.add(room);
			for (int j=3; j<line.length; j++) {
				room.addLinkId(Integer.parseInt(line[j]));
			}
		}
		for (int i=0; i<rooms.size(); i++) {
			Room room = rooms.get(i);
			for (int j=0; j<room.linksId.size(); j++) {
				room.addLink(rooms.get(room.linksId.get(j)));
			}
		}
	}
	
	public void initRooms() {
//		rooms.get(0).face = plan.init(totArea);
//		for (int i=1; i<rooms.size(); i++) {
//			rooms.get(i).face = plan.generateOne();
//		}
		plan.init(10);
		for (int i=0; i<16; i++) {
			plan.generateOne();
		}
	}
}
