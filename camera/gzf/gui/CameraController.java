package gzf.gui;

import gzf.Vec;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.MouseEvent;

/**
 * Controller of camera, include 7 views and integrated mouse control of the
 * camera.
 * 
 * <p>
 * To support processing 3.0+, codes that automatically adjust the camera when
 * the window(or screen) size changes has been removed, this application can be
 * achieved throw the calling of <code>getCamera().parentResize()</code> or
 * <code>componentResized(null)</code>.
 * 
 * @author guozifeng
 * 
 */
public class CameraController implements ComponentListener {
	/**
	 * default position
	 */
	public static final double DEFAULT_DISTANCE = 1000; // not unit
	/**
	 * some color
	 */
	public static final int COLOR_SKY = 0xffc0c0ff;
	public static final int COLOR_SKY2 = 0xffd0e0ff;
	/**
	 * default view index
	 */
	public static final int INDEX_PERSPECTIVE = 0;
	public static final int INDEX_TOP = 1;
	public static final int INDEX_FRONT = 2;
	public static final int INDEX_BACK = 3;
	public static final int INDEX_LEFT = 4;
	public static final int INDEX_RIGHT = 5;
	public static final int INDEX_ISO = 6;
	public static final int INDEX_DEFINEDLIMIT = INDEX_ISO;

	/**
	 * mouse button
	 */
	public static final int MOUSE_LEFTBUTTON = PApplet.LEFT;
	public static final int MOUSE_RIGHTBUTTON = PApplet.RIGHT;
	public static final int MOUSE_WHEEL = 3; // processing don't have wheel

	/**
	 * default movement
	 */
	public static final double DEFAULT_PAN_SPEED = 1.0 / 300.0;
	public static final double DEFAULT_ROTATE_SPEED = Math.PI / 500.0;
	public static final double DEFAULT_ZOOM_SPEED = 1.0 / 10.0;
	public static final double ZOOM_DISTANCE = 500.0;

	/**
	 * euler angle
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static double[] getABC(Vec x, Vec y, Vec z) {
		double a = Math.atan2(z.x, z.y);
		double b = Math.acos(z.z);
		double c = Math.atan2(x.z, y.z);

		return new double[] { a, b, c };
	}

	/**
	 * print text on xy plane in 3d space
	 * 
	 * @param parent
	 * @param t
	 * @param x
	 * @param y
	 * @param z
	 * @param scale
	 */
	public static void text(PGraphics parent, String t, float x, float y, float z, float scale) {
		text(parent, t, x, y, z, scale, PConstants.CENTER);
	}

	/**
	 * print text on xy plane in 3d space
	 * 
	 * @param parent
	 * @param t
	 * @param x
	 * @param y
	 * @param z
	 * @param scale
	 */
	public static void text(PGraphics parent, String t, float x, float y, float z, float scale, int align) {
		parent.pushMatrix();
		parent.translate(x, y, z);
		parent.scale(scale);
		parent.rotateX(PApplet.PI);
		parent.textAlign(align);
		parent.text(t, 0, 0);
		parent.popMatrix();
	}

	/**
	 * print text on xy plane in 3d space
	 * 
	 * @param parent
	 * @param t
	 * @param x
	 * @param y
	 * @param z
	 * @param scale
	 */
	public static void text(PApplet parent, String t, float x, float y, float z, float scale) {
		text(parent.g, t, x, y, z, scale);
	}

	/**
	 * print text on xy plane in 3d space
	 * 
	 * @param parent
	 * @param t
	 * @param x
	 * @param y
	 * @param z
	 * @param scale
	 */
	public static void text(PApplet parent, String t, float x, float y, float z, float scale, int align) {
		text(parent.g, t, x, y, z, scale, align);
	}

	/**
	 * parent
	 */
	private PApplet parent = null;
	/**
	 * list of views
	 */
	private ArrayList<Camera> cameras = new ArrayList<Camera>();
	/**
	 * default views
	 */
	private Camera camPerspective = null;
	private Camera camTop = null;
	private Camera camFront = null;
	private Camera camBack = null;
	private Camera camLeft = null;
	private Camera camRight = null;
	private Camera camISO = null;
	/**
	 * current view
	 */
	private Camera currentView = null;
	/**
	 * record previous position of mouse
	 */
	private int lastX = 0, lastY = 0;
	/**
	 * record zoom data
	 */
	private int lastZoomData = 0;

	/**
	 * speed control
	 */
	private double panSpeed = DEFAULT_PAN_SPEED;
	private double zoomSpeed = DEFAULT_ZOOM_SPEED;
	private double rotateSpeed = DEFAULT_ROTATE_SPEED;

	/**
	 * lights
	 */
	private boolean light = false;

	/**
	 * debug trigger
	 */
	private boolean openDebug = false;

	/**
	 * user operation
	 */
	private int panButton = MOUSE_LEFTBUTTON;
	private int rotateButton = MOUSE_WHEEL;
	private int zoomButton = MOUSE_WHEEL;// 缩放对应滚轮时, 映射到滚动上, 对应按键时, 映射到mouseY上

	/**
	 * do not use
	 */
	public CameraController() {
	}

	/**
	 * 
	 * @param parent
	 */
	public CameraController(PApplet parent) {
		this.parent = parent;
		ini(DEFAULT_DISTANCE);
	}

	/**
	 * 
	 * @param parent
	 */
	public CameraController(PApplet parent, double dist) {
		this.parent = parent;
		ini(dist);
	}

	/**
	 * initialize
	 */
	private void ini(double dist) {
		/*
		 * This line is removed to support processing 3.0
		 */
		parent.addComponentListener(this);
		parent.registerMethod("mouseEvent", this);
		parent.registerMethod("pre", this);
		parent.registerMethod("draw", this);

		// parent.registerMethod("resize", this);
		resetCamera(dist);
	}

	/**
	 * update mouse position
	 */
	private void updateMouse(int currentX, int currentY) {
		lastX = currentX;
		lastY = currentY;
	}

	/**
	 * reset the view
	 */
	public void resetCamera() {
		resetCamera(DEFAULT_DISTANCE);
	}

	/**
	 * reset the view
	 */
	public void resetCamera(double dist) {
		camPerspective = new Camera(parent, new Vec(-dist, -dist, dist), new Vec());
		camTop = new Camera(parent, new Vec(0, 0, dist), new Vec());
		camTop.perspective(false);
		camFront = new Camera(parent, new Vec(0, -dist, 0), new Vec());
		camFront.perspective(false);
		camBack = new Camera(parent, new Vec(0, dist, 0), new Vec());
		camBack.perspective(false);
		camLeft = new Camera(parent, new Vec(-dist, 0, 0), new Vec());
		camLeft.perspective(false);
		camRight = new Camera(parent, new Vec(dist, 0, 0), new Vec());
		camRight.perspective(false);
		camISO = new Camera(parent, new Vec(-dist, -dist, dist), new Vec());
		camISO.perspective(false);

		cameras.clear();
		cameras.add(camPerspective);
		cameras.add(camTop);
		cameras.add(camFront);
		cameras.add(camBack);
		cameras.add(camLeft);
		cameras.add(camRight);
		cameras.add(camISO);
		perspective();
	}

	/**
	 * set button which moves the camera
	 */
	public void setPanButton(int button) {
		panButton = button;
	}

	/**
	 * set button which rotate the camera
	 */
	public void setRotateButton(int button) {
		rotateButton = button;
	}

	/**
	 * set button which zoom the camera
	 */
	public void setZoomButton(int button) {
		rotateButton = button;
	}

	/**
	 * set current view to perspective(default)
	 */
	public void perspective() {
		currentView = camPerspective;
	}

	/**
	 * set current view to perspective(default)
	 */
	public void defaultView() {
		perspective();
	}

	/**
	 * set current view to top
	 */
	public void top() {
		currentView = camTop;
	}

	/**
	 * set current view to front
	 */
	public void front() {
		currentView = camFront;
	}

	/**
	 * set current view to back
	 */
	public void back() {
		currentView = camBack;
	}

	/**
	 * set current view to left
	 */
	public void left() {
		currentView = camLeft;
	}

	/**
	 * set current view to right
	 */
	public void right() {
		currentView = camRight;
	}

	/**
	 * set current view to iso
	 */
	public void iso() {
		currentView = camISO;
	}

	/**
	 * cannot set default ortho views(top, left etc)
	 */
	public void setCurrentViewToOrtho() {
		int id = cameras.indexOf(currentView);
		if (id < 1 || id > INDEX_DEFINEDLIMIT) {
			currentView.perspective(false);
		}
	}

	/**
	 * cannot set default ortho views(top, left etc)
	 */
	public void setCurrentViewToPerspective() {
		int id = cameras.indexOf(currentView);
		if (id < 1 || id > INDEX_DEFINEDLIMIT) {
			currentView.perspective(true);
		}
	}

	/**
	 * switch between perspective and ortho
	 * <p>
	 * cannot set default ortho views(top, left etc)
	 */
	public void setCurrentViewSwitch() {
		int id = cameras.indexOf(currentView);
		if (id < 1 || id > INDEX_DEFINEDLIMIT) {
			currentView.perspective(!currentView.perspective());
		}
	}

	/**
	 * pan the camera
	 */
	public void pan(int currentX, int currentY, boolean onXY) {
		double stepX = currentX - lastX;
		double stepY = currentY - lastY;
		double step = currentView.pos().dist(currentView.lookAt()) * panSpeed;
		stepX *= step;
		stepY *= step;
		if (!onXY) {
			currentView.pan(stepX, stepY);
		} else {
			Vec dx = currentView.x().dup().mul(stepX).z(0);
			Vec dy = currentView.y().dup().mul(stepY).z(0);
			currentView.move(dx.add(dy));
		}
	}

	/**
	 * rotate the camera
	 * <p>
	 * cannot rotate the default ortho views(top, left etc)
	 */
	public void rotateAroundLookAt(int currentX, int currentY) {
		boolean noRotation = true;
		if (currentView == this.camPerspective) {
			noRotation = false;
		} else {
			int id = cameras.indexOf(currentView);
			if (id > INDEX_DEFINEDLIMIT) {
				noRotation = false;
			}
		}
		if (!noRotation && currentView.lookAt() != null) {
			double ang1 = -(currentX - lastX) * rotateSpeed;
			double ang2 = (currentY - lastY) * rotateSpeed;
			currentView.rotateLookAtHorizontal(ang1);
			currentView.rotateLookAtVertical(ang2);
		}
	}

	/**
	 * rotate the camera as turning the head around
	 * <p>
	 * cannot rotate the default ortho views(top, left etc)
	 */
	public void rotateHead(int currentX, int currentY) {
		boolean noRotation = true;
		if (currentView == this.camPerspective) {
			noRotation = false;
		} else {
			int id = cameras.indexOf(currentView);
			if (id > INDEX_DEFINEDLIMIT) {
				noRotation = false;
			}
		}
		if (!noRotation && currentView.lookAt() != null) {
			double ang1 = (currentX - lastX) * rotateSpeed;
			double ang2 = -(currentY - lastY) * rotateSpeed;
			currentView.rotateCameraY(ang1);
			currentView.rotateCameraX(ang2);
		}
	}

	/**
	 * zoom camera
	 * <p>
	 * adapt both wheel and button
	 */
	public void zoom(int data) {
		double signum = 0;
		if (Math.abs(data) == 1) {
			signum = -data;
		} else {
			signum = Math.signum(lastZoomData - data);
		}
		if (signum != 0) {
			double dist = ZOOM_DISTANCE;
			if (currentView.lookAt() != null) {
				dist = currentView.lookAt().dist(currentView.pos());
			}
			currentView.moveIn(signum * dist * zoomSpeed);
		}
		lastZoomData = data;
	}

	/**
	 * print text in 3d space, facing toward the camera
	 * 
	 * @param t
	 *            Text
	 * @param x
	 *            x coordinate(world)
	 * @param y
	 *            y coordinate(world)
	 * @param z
	 *            z coordinate(world)
	 * @param scale
	 *            scale of the text
	 * @param align
	 */
	public void text3d(String t, float x, float y, float z, float scale, int align) {
		Camera cam = getCamera();

		double[] abc = getABC(cam.x().dup().rev(), cam.y(), cam.z().dup().rev());

		parent.pushMatrix();
		parent.translate(x, y, z);
		parent.scale(scale);
		/*
		 * the drawing space of processing employs left-handed system in which
		 * the rotate directions are opposite to those in right-handed system.
		 * So the first two angles should multiply -1 because they decide the z
		 * direction of the rotated result.
		 */
		parent.rotateZ(-(float) abc[0]);
		parent.rotateX(-(float) abc[1]);
		parent.rotateZ((float) abc[2]);

		/*
		 * flip the text
		 */
		parent.rotateY(PConstants.PI);
		parent.textAlign(align);
		parent.text(t, 0, 0);
		parent.popMatrix();
	}

	/**
	 * print text in 3d space, facing toward the camera
	 * 
	 * @param t
	 *            Text
	 * @param x
	 *            x coordinate(world)
	 * @param y
	 *            y coordinate(world)
	 * @param z
	 *            z coordinate(world)
	 * @param scale
	 *            scale of the text
	 */
	public void text3d(String t, float x, float y, float z, float scale) {
		this.text3d(t, x, y, z, scale, PConstants.LEFT);
	}

	/**
	 * Pick a point in 3d space from the coordinate on screen
	 * 
	 * @param mouseX
	 *            x coordinate on screen, usually use the mouse position
	 * @param mouseY
	 *            y coordinate on screen, usually use the mouse position
	 * @param zPos
	 *            depth into the screen, 0 to 1.
	 * @return A coordinate in 3d space
	 */
	public Vec pick3d(int mouseX, int mouseY, double zPos) {
		return currentView.pick3d(mouseX, mouseY, zPos);
	}

	/**
	 * Pick a point in 3d space from the coordinate on screen
	 * 
	 * @param mouseX
	 *            x coordinate on screen, usually use the mouse position
	 * @param mouseY
	 *            y coordinate on screen, usually use the mouse position
	 * @param zPos
	 *            depth into the screen, 0 to 1.
	 * @return A double array represented coordinate in 3d space
	 */
	public double[] pick3dDouble(int mouseX, int mouseY, double zPos) {
		return currentView.pick3d(mouseX, mouseY, zPos).toDoubleArray();
	}

	/**
	 * Pick a point on x-y plane from the coordinate on screen
	 * 
	 * @param mouseX
	 *            x coordinate on screen, usually use the mouse position
	 * @param mouseY
	 *            y coordinate on screen, usually use the mouse position
	 * 
	 * @return A coordinate on x-y plane
	 */
	public Vec pick3dXYPlane(double mouseX, double mouseY) {
		return currentView.pick3dXYPlane(mouseX, mouseY);
	}

	/**
	 * Pick a point on x-y plane from the coordinate on screen
	 * 
	 * @param mouseX
	 *            x coordinate on screen, usually use the mouse position
	 * @param mouseY
	 *            y coordinate on screen, usually use the mouse position
	 * 
	 * @return A double array represented coordinate on x-y plane
	 */
	public double[] pick3dXYPlaneDouble(double mouseX, double mouseY) {
		return currentView.pick3dXYPlane(mouseX, mouseY).toDoubleArray();
	}

	/**
	 * Get a point and a direction(a ray) in 3d space from the coordinate on
	 * screen
	 * 
	 * @param mouseX
	 *            x coordinate on screen, usually use the mouse position
	 * @param mouseY
	 *            y coordinate on screen, usually use the mouse position
	 * 
	 * @return Coordinates. Vec[]{point, direction}
	 */
	public Vec[] pick3d(int mouseX, int mouseY) {
		return currentView.pick3d(mouseX, mouseY);
	}

	/**
	 * Get a point and a direction(a ray) in 3d space from the coordinate on
	 * screen
	 * 
	 * @param mouseX
	 *            x coordinate on screen, usually use the mouse position
	 * @param mouseY
	 *            y coordinate on screen, usually use the mouse position
	 * 
	 * @return Double array represented coordinates. double[0:point,
	 *         1:direction][x,y,z].
	 */
	public double[][] pick3dDouble(int mouseX, int mouseY) {
		Vec[] tmp = currentView.pick3d(mouseX, mouseY);
		return new double[][] { tmp[0].toDoubleArray(), tmp[1].toDoubleArray() };
	}

	/**
	 * store current view as a new camera
	 * 
	 * @return index of the new camera
	 */
	public int storeCurrentView() {
		Camera tmpCam = currentView.copy();
		cameras.add(tmpCam);
		return cameras.indexOf(tmpCam);
	}

	/**
	 * create a new angle of view and save it as a new camera
	 * 
	 * @param pos
	 * @param lookAt
	 * @return index of the new camera
	 */
	public int createView(Vec pos, Vec lookAt) {
		Camera tmpCam = new Camera(parent, pos, lookAt);
		cameras.add(tmpCam);
		return cameras.indexOf(tmpCam);
	}

	/**
	 * change current view
	 */
	public void changeCurrentView(int index) {
		if (index < cameras.size())
			currentView = cameras.get(index);
	}

	/**
	 * get current view
	 */
	public Camera getCamera() {
		return currentView;
	}

	/**
	 * get current view
	 */
	public Camera getCurrentView() {
		return currentView;
	}

	/**
	 * set fovy
	 */
	public void setFovy(double v) {
		int index = cameras.indexOf(currentView);
		if (index == INDEX_PERSPECTIVE || index > INDEX_DEFINEDLIMIT)
			currentView.fovy(v); // 平行投影不设置
	}

	/**
	 * set fovy
	 */
	public void setFovyAll(double v) {
		for (Camera c : cameras) {
			if (c.perspective()) {
				c.fovy(v);
			}
		}
	}

	/**
	 * set far
	 */
	public void setFar(double v) {
		currentView.far(v);
	}

	/**
	 * set far
	 */
	public void setFarAll(double v) {
		for (Camera c : cameras) {
			c.far(v);
		}
	}

	/**
	 * set near
	 */
	public void setNear(double v) {
		currentView.near(v);
	}

	/**
	 * set near
	 */
	public void setNearAll(double v) {
		for (Camera c : cameras) {
			c.near(v);
		}
	}

	/**
	 * open default lights in the 3d environment
	 */
	public void openLight() {
		light = true;
	}

	/**
	 * close default lights in the 3d environment
	 */
	public void closeLight() {
		light = false;
	}

	/**
	 * open debug
	 * <p>
	 * show a little box at the look-at point a and a red point where mouse is
	 * pointing
	 * <p>
	 * to show the box and the point, you need to delete the background method
	 * in draw loop in processing(the screen will still keep refreshing)
	 */
	public void openDebug() {
		openDebug = true;
	}

	/**
	 * close debug
	 */
	public void closeDebug() {
		openDebug = false;
	}

	/**
	 * return the PGraphics object used to export high resolution image
	 * 
	 * @param scale
	 * @return
	 */
	public PGraphics highResolutionImage(int scale) {
		return parent.createGraphics(parent.width * scale, parent.height * scale, PApplet.P3D);
	}

	/**
	 * return the camera used by PGraphics object.
	 * <p>
	 * the returning camera will be in perspective mode, if need ortho, use
	 * cameraForHighResolutionImage(PGraphics, scale) and pass scale into it.
	 * 
	 * @param p
	 * @return
	 */
	public Camera cameraForHighResolutionImage(PGraphics p) {
		Camera cam = currentView.copy();
		cam.setParent(p);
		cam.perspective(true);
		return cam;
	}

	/**
	 * return the camera used by PGraphics object.
	 * <p>
	 * fit both perspective and ortho(scale doesn't do any thing if the camera
	 * is in perspective mode)
	 * 
	 * @param p
	 * @return
	 */
	public Camera cameraForHighResolutionImage(PGraphics p, int scale) {
		Camera cam = currentView.copy();
		cam.setParent(p);
		cam.perspective(currentView.perspective());
		if (!cam.perspective()) {
			double d = cam.pos().dist(cam.lookAt());
			double m = d - d / scale;
			cam.moveIn(m);
		}
		return cam;
	}

	/**
	 * apply camera to processing, put this method in draw loop and call it
	 * before drawing anything 3d
	 * <p>
	 * 
	 * @Deprecated This method will be automatically called in the draw loop
	 */
	@Deprecated
	public void applyCamera() {
		currentView.applyCamera();
		if (light) {
			parent.ambientLight(150, 150, 150);
			parent.directionalLight(210, 210, 210, -1, 1, -2);
		}
		if (openDebug) {
			Vec pt = currentView.pick3d(lastX, lastY, 0.999);
			parent.pushStyle();

			parent.stroke(0xffff0000);
			parent.strokeWeight(3);
			parent.fill(255);

			parent.point((float) pt.x, (float) pt.y, (float) pt.z);

			parent.pushMatrix();
			parent.translate((float) currentView.lookAt().x, (float) currentView.lookAt().y, (float) currentView.lookAt().z);
			parent.box(10);
			parent.popMatrix();

			parent.popStyle();
		}
	}

	/**
	 * draw x, y, z axis
	 */
	public void drawSystem(float len) {
		currentView.drawSystem(len);
	}

	/**
	 * mouse event
	 */
	public void mouseEvent(MouseEvent event) {
		int button = event.getButton();
		int mouseX = event.getX();
		int mouseY = event.getY();

		switch (event.getAction()) {
		case MouseEvent.DRAG:
			if (button == panButton) {
				pan(mouseX, mouseY, false);
			}
			if (button == rotateButton) {
				this.rotateAroundLookAt(mouseX, mouseY);
			}
			if (button == zoomButton && zoomButton != MOUSE_WHEEL) {
				zoom(mouseY);
			}
			break;
		case MouseEvent.CLICK:

			break;
		case MouseEvent.WHEEL:
			if (zoomButton == MOUSE_WHEEL) {
				zoom(event.getCount());
			}
			break;
		}
		updateMouse(mouseX, mouseY);
	}

	/**
	 * apply camera
	 * <p>
	 * called by processing automatically
	 */
	public void pre() {
		currentView.applyCamera();
		// System.out.println("Matrix set!");
		if (light) {
			parent.ambientLight(150, 150, 150);
			parent.directionalLight(210, 210, 210, -1, 1, -2);
		}
		if (openDebug) {
			parent.background(parent.getBackground().getRGB());

			Vec pt = currentView.pick3d(lastX, lastY, 0.999);
			parent.pushStyle();

			parent.stroke(0xffff0000);
			parent.strokeWeight(3);
			parent.fill(255);

			parent.point((float) pt.x, (float) pt.y, (float) pt.z);

			parent.stroke(0xff0000ff);
			pt = currentView.pick3dXYPlane(lastX, lastY);
			parent.point((float) pt.x, (float) pt.y, (float) pt.z);

			parent.pushMatrix();
			parent.translate((float) currentView.lookAt().x, (float) currentView.lookAt().y, (float) currentView.lookAt().z);
			parent.box(10);
			parent.popMatrix();

			parent.popStyle();
		}
	}

	/**
	 * reset camera
	 */
	public void draw() {
		begin2d();
	}

	/**
	 * If you need to draw 2d object on a 3d screen, call this method in the
	 * draw loop, and the later drawing will be 2d.
	 * <p>
	 * Note: some 2d drawing may be blocked by objects in 3d space drawn before,
	 * and this operation cannot be undone!
	 */
	public void begin2d() {
		currentView.resetAppMatrix();
		// System.out.println("Matrix reset!");
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {

	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		for (Camera c : cameras) {
			c.parentResize();
		}
	}

	@Override
	public void componentShown(ComponentEvent arg0) {

	}

	/**
	 * for large image export
	 * 
	 * @param p
	 */
	public static void openLightOnPGraphics(PGraphics p) {
		p.ambientLight(150, 150, 150);
		p.directionalLight(210, 210, 210, -1, 1, -2);
	}
}
