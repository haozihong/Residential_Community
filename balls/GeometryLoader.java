package balls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Load geometries from files
 * 
 * @author guoguo
 *
 */
public class GeometryLoader {
	/**
	 * Load geometries from obj file.
	 * <p>
	 * 
	 * @param filename
	 *            File to be opened
	 * @param mergeContiguous
	 *            Whether contiguous faces will be merged, this option will help
	 *            to merge the triangle faces produced by the triangulation
	 *            process during the export of obj file.
	 * @returnm Array presented geometry collection
	 * @throws IOException
	 */
	public static Geometry[] loadFromObj(String filename, boolean mergeContiguous) throws IOException {
		GeometryFactory gf = new GeometryFactory();
		ArrayList<Coordinate> vertices = new ArrayList<Coordinate>();
		ArrayList<Polygon> polygons = new ArrayList<Polygon>();

		/*
		 * load from file
		 */
		File file = new File(filename);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();

		while (line != null) {
			if (!line.isEmpty()) {
				String[] data = line.split(" ");
				// vertices
				if (data[0].equals("v")) {
					vertices.add(getCoord(data));
				}

				/*
				 * additional information such as group, material, texture uv
				 * coordinates and vertices normals are ignored.
				 */

				/*
				 * this reader requires all vertices which are contained in a
				 * polygon(face) present before this polygon in the obj file.
				 */
				if (data[0].equals("f")) {
					int len = data.length;
					Coordinate[] coords = new Coordinate[len];
					for (int i = 0; i < len; i++) {
						if (i < len - 1) {
							String[] subData = data[i + 1].split("/");
							int index = Integer.valueOf(subData[0]) - 1;
							coords[i] = vertices.get(index);
						} else {
							coords[i] = coords[0];
						}
					}

					LinearRing lr = gf.createLinearRing(coords);
					polygons.add(gf.createPolygon(lr, null));
				}
			}
			line = reader.readLine();
		}
		reader.close();

		/*
		 * merge faces if required
		 */
		if (mergeContiguous && polygons.size() > 1) {
			Geometry mergedGeo = polygons.get(0);
			for (int i = 0; i < polygons.size(); i++) {
				mergedGeo = mergedGeo.union(polygons.get(i));
			}

			if (mergedGeo instanceof MultiPolygon) {
				MultiPolygon mp = (MultiPolygon) mergedGeo;
				polygons.clear();
				for (int i = 0; i < mp.getNumGeometries(); i++) {
					polygons.add((Polygon) mp.getGeometryN(i));
				}
			}

			if (mergedGeo instanceof Polygon) {
				polygons.clear();
				polygons.add((Polygon) mergedGeo);
			}
		}

		Polygon[] outGeo = new Polygon[polygons.size()];
		outGeo = polygons.toArray(outGeo);

		return outGeo;
	}

	private static Coordinate getCoord(String[] s) {
		return new Coordinate(Double.valueOf(s[1]), Double.valueOf(s[2]), Double.valueOf(s[3]));
	}
}
