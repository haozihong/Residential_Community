package make_grid;

import igeo.*;
import java.util.ArrayList;

public class Room {
	public int id;
	public String name;
	public double area;
	public ArrayList<Room> links = new ArrayList<Room>();
	public ArrayList<Integer> linksId = new ArrayList<Integer>();
	public HFace face;
	
	Room(int id, String name, double area) {
		this.id = id;
		this.name = name;
		this.area = area;
	}
	
	public void addLinkId(int id2) {
		linksId.add(new Integer(id2));
	}
	
	public void addLink(Room room2) {
		links.add(room2);
	}
	
	public double cost() {
		return 0;
	}

}
