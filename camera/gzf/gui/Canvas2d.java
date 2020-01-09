package gzf.gui;

import gzf.Vec;
import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * draw 2d object in 3d screen
 * <p>
 * how to use:
 * <p>
 * initiate: (recommended in setup) Canvas2d canvas = new Canvas2d(PApplet or
 * PGraphics, Camera or CameraControlers);
 * <p>
 * draw: (in main loop) canvas.beginshape(); canvas.vertex(); canvas.endShape();
 * etc
 * 
 * @author guoguo
 *
 */
public class Canvas2d {
	/**
	 * 0 to 1
	 * <p>
	 * close to 0: drawing will in front of the 3d objects which are near the
	 * camera
	 * <p>
	 * close to 1: drawing will behind the 3d objects which are far from the
	 * camera
	 * <p>
	 */
	public static double zInScreen = 0.00000001;
	public static double zNeg = 1 / zInScreen;
	public static double ellipseResolution = 0.5;

	private Camera c = null;
	private CameraController cam = null;
	private PGraphics parent = null;

	/**
	 * left top to left bottom, clockwise
	 */
	private Vec[] corner = new Vec[4];

	private double width = 0, height = 0;
	private double scaleTo3d = 0;

	private Vec x = null, y = null, z = null, o = null;

	/**
	 * do not use
	 */
	public Canvas2d() {
	}

	/**
	 * should update corner manually
	 * 
	 * @param parent
	 * @param cam
	 */
	public Canvas2d(PGraphics parent, CameraController cam) {
		this.cam = cam;
		this.parent = parent;
	}

	/**
	 * 
	 * @param parent
	 * @param cam
	 */
	public Canvas2d(PApplet parent, CameraController cam) {
		this.cam = cam;
		this.parent = parent.g;
		parent.registerMethod("pre", this);
	}

	/**
	 * should update corner manually
	 * 
	 * @param parent
	 * @param cam
	 */
	public Canvas2d(PGraphics parent, Camera cam) {
		this.c = cam;
		this.parent = parent;
	}

	/**
	 * 
	 * @param parent
	 * @param cam
	 */
	public Canvas2d(PApplet parent, Camera cam) {
		this.c = cam;
		this.parent = parent.g;
		parent.registerMethod("pre", this);
	}

	/**
	 * 
	 * @return
	 */
	public PGraphics getGraphics() {
		return parent;
	}

	/**
	 * override processing method
	 */
	public void pre() {
		updateCorner();
	}

	/**
	 * get camera currently being used
	 */
	public Camera getCamera() {
		Camera camera = c;
		if (camera == null) {
			camera = cam.getCamera();
		}
		return camera;
	}

	/**
	 * update drawing area
	 */
	public void updateCorner() {
		Camera camera = getCamera();

		/*
		 * When the value becomes large, distances between shapes and screen
		 * should be larger to prevent distortions caused by the floating point
		 * error
		 */
		double dist = Math.max(1, camera.pos().len());
		double zAdd = Math.min(zNeg, dist); // limitation of final z to 1

		width = camera.width();
		height = camera.height();

		Vec[] p1 = new Vec[4];
		Vec[] p2 = new Vec[4];
		Vec[] d = new Vec[4];

		p1[0] = camera.pick3d(0, 0, 0);
		p1[1] = camera.pick3d(width, 0, 0);
		p1[2] = camera.pick3d(width, height, 0);
		p1[3] = camera.pick3d(0, height, 0);

		p2[0] = camera.pick3d(0, 0, 1);
		p2[1] = camera.pick3d(width, 0, 1);
		p2[2] = camera.pick3d(width, height, 1);
		p2[3] = camera.pick3d(0, height, 1);

		for (int i = 0; i < 4; i++) {
			d[i] = p2[i].dup().sub(p1[i]).mul(zInScreen * zAdd);
			corner[i] = p1[i].dup().add(d[i]);
		}
		x = corner[1].dup().sub(corner[0]).unit();
		y = corner[3].dup().sub(corner[0]).unit();
		z = x.cross(y).unit().rev();
		o = corner[0];

		scaleTo3d = corner[1].dist(corner[0]) / width;
	}

	public void translate(double x, double y, double z) {
		Vec to = getDirection(x, y, z);// this.x.dup().mul(x).add(this.y.dup().mul(y)).add(this.z.dup().mul(z));
		parent.translate((float) to.x, (float) to.y, (float) to.z);
	}

	/**
	 * processing begin shape
	 */
	public void beginShape() {
		parent.beginShape();
	}

	/**
	 * processing begin shape
	 */
	public void beginShape(int mode) {
		parent.beginShape(mode);
	}

	/**
	 * processing end shape
	 */
	public void endShape() {
		parent.endShape();
	}

	/**
	 * processing end shape
	 */
	public void endShape(int mode) {
		parent.endShape(mode);
	}

	/**
	 * override processing vertex
	 */
	public void vertex(double coodX, double coodY) {
		if (x == null) {
			updateCorner();
		}
		Vec finalPt = getPoint(coodX, coodY, 0); // o.dup().add(x.dup().mul(coodX
													// *
													// scaleTo3d)).add(y.dup().mul(coodY
													// * scaleTo3d));
		parent.vertex((float) finalPt.x, (float) finalPt.y, (float) finalPt.z);
	}

	/**
	 * override processing vertex
	 */
	public void vertex(double coodX, double coodY, double coodZ) {
		if (x == null) {
			updateCorner();
		}
		Vec finalPt = getPoint(coodX, coodY, coodZ);// o.dup().add(x.dup().mul(coodX
													// *
													// scaleTo3d)).add(y.dup().mul(coodY
													// *
													// scaleTo3d)).add(z.dup().mul(coodZ
													// * scaleTo3d));
		parent.vertex((float) finalPt.x, (float) finalPt.y, (float) finalPt.z);
	}

	/**
	 * override processing line
	 */
	public void line(double x1, double y1, double x2, double y2) {
		if (x == null) {
			updateCorner();
		}
		Vec pt1 = getPoint(x1, y1, 0);// o.dup().add(x.dup().mul(x1 *
										// scaleTo3d)).add(y.dup().mul(y1 *
										// scaleTo3d));
		Vec pt2 = getPoint(x2, y2, 0);// o.dup().add(x.dup().mul(x2 *
										// scaleTo3d)).add(y.dup().mul(y2 *
										// scaleTo3d));
		parent.line((float) pt1.x, (float) pt1.y, (float) pt1.z, (float) pt2.x, (float) pt2.y, (float) pt2.z);
	}

	public void pushMatrix() {
		parent.pushMatrix();
	}

	public void popMatrix() {
		parent.popMatrix();
	}

	/**
	 * override processing ellipse
	 */
	public void ellipse(double centerX, double centerY, double a, double b) {
		if (x == null) {
			updateCorner();
		}
		// at least 1 point
		double resolutoin = Math.max(1, (ellipseResolution * Math.max(a, b)));
		beginShape();
		for (int i = 0; i < resolutoin; i++) {
			double ang = Math.PI * 2 * i / resolutoin;
			double sin = Math.sin(ang);
			double cos = Math.cos(ang);
			double x = a * cos;
			double y = b * sin;
			vertex(x + centerX, y + centerY);
		}
		endShape(PApplet.CLOSE);
	}

	/**
	 * override processing rectangle
	 */
	public void rectangle(double left, double top, double w, double h) {
		if (x == null) {
			updateCorner();
		}
		beginShape();

		vertex(left, top);
		vertex(left + w, top);
		vertex(left + w, top + h);
		vertex(left, top + h);

		endShape(PApplet.CLOSE);
	}

	/**
	 * override processing text
	 */
	@Deprecated
	public void text(String str, double x, double y) {
		Vec pt = getPoint(x, y, 0);
		if (cam != null) {
			cam.text3d(str, (float) pt.x, (float) pt.y, (float) pt.z, 1.0f / parent.textSize);
		}
	}

	private Vec getDirection(double x, double y, double z) {
		return this.x.dup().mul(x * scaleTo3d).add(this.y.dup().mul(y * scaleTo3d)).add(this.z.mul(z * scaleTo3d));
	}

	private Vec getPoint(double x, double y, double z) {
		return o.dup().add(this.x.dup().mul(x * scaleTo3d)).add(this.y.dup().mul(y * scaleTo3d)).add(this.z.mul(z * scaleTo3d));
	}
}
