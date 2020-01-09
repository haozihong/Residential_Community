package gzf.gui;

import processing.core.PApplet;
import processing.core.PGraphics;
import gzf.Vec;

/**
 * A implementation of right-handed coordinate system camera, both perspective
 * and orthogonal views are supported, 3d picking are supported.
 * <p>
 * A camera is represented as a location and three directions in 3d space, the
 * location is where the camera is, and the z direction is the sight direction
 * of the camera, the y direction is the <i>up</i> on the screen and the x is
 * the <i> left</i>.
 * <p>
 * 2014-11-03:
 * <p>
 * No longer support processing 2.0 and below because the meaning of the
 * parameters of <code>PApplet.ortho()</code> has changed.
 * <p>
 * 
 * @author guozifeng
 * 
 */
public class Camera {
	public static final double DEFAULT_FOVY = Math.PI / 3;
	public static final float PROCESSING_CAMERA_POSITION = 0.000001f;
	public static final double DEFAULT_NEAR = 1000;
	public static final double DEFAULT_FAR = 1000;

	public static final int PARENT_TYPE_NULL = -1;
	public static final int PARENT_TYPE_PAPPLET = 0;
	public static final int PARENT_TYPE_PGRAPHICS = 1;
	public static final int DEFAULT_PARENT_TYPE = PARENT_TYPE_NULL;

	/**
	 * parent for view
	 */
	private PApplet parentApplet = null;
	/**
	 * parent for export image
	 */
	private PGraphics parentGraphics = null;

	/**
	 * view port size(from JME3, keep them in case of changes)
	 */
	private double viewPortLeft = 0;
	private double viewPortRight = 1;
	private double viewPortTop = 1;
	private double viewPortBottom = 0;

	/**
	 * for PApplet.frustum() and calculation of projection matrix;
	 */
	private double frustumNear = 1.0;
	private double frustumFar = 2.0;
	private double frustumLeft = -0.5;
	private double frustumRight = 0.5;
	private double frustumTop = 0.5;
	private double frustumBottom = -0.5;

	/**
	 * view port size
	 */
	private float height = 0;
	private float width = 0;

	private float heightHalf = 0;
	private float widthHalf = 0;

	/**
	 * perspective
	 */
	private double fovy = DEFAULT_FOVY;
	private double near = DEFAULT_NEAR;
	private double far = DEFAULT_FAR;
	private boolean perspective = true;

	/**
	 * matrix
	 */
	private Matrix4 viewMatrix = null;
	private Matrix4 projectionMatrix = null;
	private Matrix4 viewProjectionMatrix = null;
	private Matrix4 inverseMatrix = null;
	private float[][] viewMatrixFloat = null;

	/**
	 * update view matrix(for example, after move the camera)
	 */
	private boolean updateViewMatrix = true;

	/**
	 * update projection matrix(for example, after change the fovy)
	 */
	private boolean updateProjectionMatrix = true;

	/**
	 * update view projection matrix
	 */
	private boolean updateViewProjectionMatrix = true;

	/**
	 * camera 3d position
	 */
	private Vec pos = null;
	/**
	 * orientation of camera, z is the direction of sight, x is left and y is
	 * up.
	 */
	private Vec x = null, y = null, z = null;
	private Vec lookAt = null;

	/**
	 * switch parent
	 */
	private int parentType = DEFAULT_PARENT_TYPE;

	/**
	 * if this camera has been applied
	 */
	private boolean applied = false;

	/**
	 * do not use
	 */
	public Camera() {
	}

	/**
	 * default
	 */
	public Camera(Object parent) {
		this(parent, new Vec(0, 0, 1));
		setParent(parent);
		updateMatrix();
	}

	/**
	 * from position
	 */
	public Camera(Object parent, Vec pos) {
		this(parent, pos, new Vec(0, 0, 1), new Vec(1, 0, 0));
		setParent(parent);
		updateMatrix();
	}

	/**
	 * from position, z direction, x direction
	 */
	public Camera(Object parent, Vec pos, Vec z, Vec planxz) {
		iniCoordinateSystem(pos, z, planxz);
		setParent(parent);
		updateMatrix();
	}

	/**
	 * from eye and target
	 */
	public Camera(Object parent, Vec pos, Vec lookAt) {
		this.lookAt = lookAt;
		this.pos = pos;
		iniCoordinateSystem(pos, lookAt);
		setParent(parent);
		updateMatrix();
	}

	/**
	 * copy
	 */
	public Camera(Camera origin) {
		this(origin.getParent(), origin.pos().dup(), origin.z().dup(), origin.x().dup());
		this.lookAt = origin.lookAt.dup();
		this.far = origin.far;
		this.near = origin.near;
		this.fovy = origin.fovy;
		updateProjectionMatrix();
		updateViewMatrix();
		updateMatrix();
	}

	/**
	 * 2 points initialization
	 */
	private void iniCoordinateSystem(Vec pos, Vec lookAt) {
		Vec zz = lookAt.dup().sub(pos);
		Vec planxz = null;
		if (zz.x != 0 || zz.y != 0) {
			// not z axis
			planxz = Vec.zaxis.cross(zz);
		} else {
			// z axis
			// make world x at the left side of screen
			planxz = Vec.yaxis.cross(zz);
		}
		iniCoordinateSystem(pos, zz, planxz);
	}

	/**
	 * 1 point 2 direction initialization
	 */
	private void iniCoordinateSystem(Vec pos, Vec z, Vec planxz) {
		this.pos = pos;
		this.z = z.dup().unit();
		this.x = planxz;
		this.y = this.z.cross(this.x).unit();
		this.x = this.y.cross(this.z).unit();
		if (lookAt == null) {
			lookAt = pos.dup().add(z);
		}
		updateViewMatrix();
	}

	private void getProjectionMatrix() {
		getFrustumPerspective(fovy, width / height, near, far);
		projectionMatrix = new Matrix4();
		projectionMatrix.fromFrustum(frustumNear, frustumFar, frustumLeft, frustumRight, frustumTop, frustumBottom, !perspective);
	}

	private void getFrustumPerspective(double fovy, double aspect, double near, double far) {
		double tan = Math.tan(fovy / 2);
		double cameraZ = height / 2 / tan;
		double zNear = cameraZ / near;
		double zFar = cameraZ * far;

		double h = height / 2 / near; // tan * zNear;
		double w = h * aspect; // = width / 2 / near
		frustumLeft = -w;
		frustumRight = w;
		frustumBottom = -h;
		frustumTop = h;
		frustumNear = zNear;
		frustumFar = zFar;

		if (!perspective) {
			double dist = pos.dist(lookAt);
			frustumLeft *= dist;
			frustumRight *= dist;
			frustumBottom *= dist;
			frustumTop *= dist;
		}
	}

	/**
	 * get view matrix for current camera
	 */
	private void getViewMatrix() {
		// repair float error
		Vec xBak = this.x.dup();
		Vec yBak = this.y.dup();
		Vec zBak = this.z.dup();
		Vec posBak = this.pos.dup();
		Vec lookAtBak = this.lookAt.dup();

		// left hand to right hand
		this.z.rev();
		rotateCameraZ(Math.PI);

		// get matrix
		double[][] matrix = new double[4][4];
		matrix[0][0] = x.x;
		matrix[0][1] = x.y;
		matrix[0][2] = x.z;
		matrix[0][3] = -pos.dot(x);

		matrix[1][0] = y.x;
		matrix[1][1] = y.y;
		matrix[1][2] = y.z;
		matrix[1][3] = -pos.dot(y);

		matrix[2][0] = z.x;
		matrix[2][1] = z.y;
		matrix[2][2] = z.z;
		matrix[2][3] = -pos.dot(z);

		matrix[3][0] = 0;
		matrix[3][1] = 0;
		matrix[3][2] = 0;
		matrix[3][3] = 1;

		this.x.set(xBak);
		this.y.set(yBak);
		this.z.set(zBak);
		this.pos.set(posBak);
		this.lookAt.set(lookAtBak);

		viewMatrix = new Matrix4(matrix);
		this.viewMatrixFloat = getMatrixFloat(viewMatrix);
	}

	/**
	 * convert matrix to float array(for processing.applyMatrix())
	 */
	private float[][] getMatrixFloat(Matrix4 m) {
		double[][] matrix = m.getMatrix();
		float[][] matrixF = new float[matrix.length][matrix[0].length];
		for (int i = 0; i < matrixF.length; i++) {
			for (int j = 0; j < matrixF[i].length; j++) {
				matrixF[i][j] = (float) matrix[i][j];
			}
		}
		return matrixF;
	}

	/**
	 * update matrix(called by applyCamera)
	 */
	private void updateMatrix() {
		if (updateViewMatrix) {
			// System.out.println("update view matrix");
			getViewMatrix();
			updateViewMatrix = false;
		}
		if (updateProjectionMatrix) {
			// System.out.println("update projection matrix");
			getProjectionMatrix();
			updateProjectionMatrix = false;
		}
	}

	/**
	 * update matrix(called by 3d picking)
	 * <p>
	 * when camera stays still, avoid the extra calculation of matrix
	 */
	private void updateViewProjectionMatrix() {
		if (updateViewProjectionMatrix) {
			viewProjectionMatrix = projectionMatrix.mult(viewMatrix);
			inverseMatrix = viewProjectionMatrix.invert();
			updateViewProjectionMatrix = false;
		}
	}

	/**
	 * set parent(PApplet or PGraphics)
	 */
	public void setParent(Object parent) {
		if (parent instanceof PApplet) {
			this.parentType = PARENT_TYPE_PAPPLET;
			parentApplet = (PApplet) parent;

			width = parentApplet.width;
			height = parentApplet.height;
			widthHalf = width / 2;
			heightHalf = height / 2;

		} else if (parent instanceof PGraphics) {
			this.parentType = PARENT_TYPE_PGRAPHICS;
			parentGraphics = (PGraphics) parent;
			parentApplet = null;

			width = parentGraphics.width;
			height = parentGraphics.height;
			widthHalf = width / 2;
			heightHalf = height / 2;
		} else {
			System.err.println("Parent can only be PApplet or PGraphics");
		}
	}

	/**
	 * get parent
	 */
	public Object getParent() {
		if (parentType == PARENT_TYPE_PAPPLET) {
			return parentApplet;
		} else if (parentType == PARENT_TYPE_PGRAPHICS) {
			return parentGraphics;
		} else {
			return null;
		}
	}

	/**
	 * reserve function for view port resizing
	 */
	public void updateFrameSize() {
		if (parentType == PARENT_TYPE_PAPPLET) {
			width = parentApplet.width;
			height = parentApplet.height;
		} else if (parentType == PARENT_TYPE_PGRAPHICS) {
			width = parentGraphics.width;
			height = parentGraphics.height;
		}
		updateProjectionMatrix();
	}

	/**
	 * called when camera rotates or moves
	 */
	public void updateViewMatrix() {
		updateViewMatrix = true;
		updateViewProjectionMatrix = true;
	}

	/**
	 * 
	 */
	public void updateProjectionMatrix() {
		updateProjectionMatrix = true;
		updateViewProjectionMatrix = true;
	}

	/**
	 * get view size
	 */
	public float width() {
		return width;
	}

	/**
	 * get view size
	 */
	public float height() {
		return height;
	}

	/**
	 * if perspective
	 */
	public void perspective(boolean v) {
		perspective = v;
		updateProjectionMatrix();
	}

	/**
	 * get perspective
	 */
	public boolean perspective() {
		return perspective;
	}

	/**
	 * set perspective angle
	 */
	public void fovy(double fovy) {
		this.fovy = fovy;
		updateProjectionMatrix();
	}

	/**
	 * get fovy
	 */
	public double fovy() {
		return fovy;
	}

	/**
	 * larger, closer
	 */
	public void near(double near) {
		this.near = near;
		updateProjectionMatrix();
	}

	public double near() {
		return near;
	}

	/**
	 * larger, further
	 */
	public void far(double far) {
		this.far = far;
		updateProjectionMatrix();
	}

	public double far() {
		return far;
	}

	/**
	 * position
	 */
	public Vec pos() {
		return pos;
	}

	/**
	 * position
	 */
	synchronized public void pos(Vec v) {
		pos = v;
		iniCoordinateSystem(pos, lookAt);
	}

	/**
	 * lookAt
	 */
	public Vec lookAt() {
		return lookAt;
	}

	/**
	 * lookAt
	 */
	synchronized public void lookAt(Vec v) {
		lookAt = v;
		iniCoordinateSystem(pos, lookAt);
	}

	/**
	 * x
	 */
	public Vec x() {
		return x;
	}

	/**
	 * y
	 */
	public Vec y() {
		return y;
	}

	/**
	 * z
	 */
	public Vec z() {
		return z;
	}

	/**
	 * pan(positive = left, up; negative = right, down)
	 */
	public void pan(double u, double v) {
		Vec d = x.dup().mul(u);
		d.add(y.dup().mul(v));
		move(d);
	}

	/**
	 * move along object x direction
	 */
	public void moveX(double d) {
		move(x.dup().mul(d));
	}

	/**
	 * move along object y direction
	 */
	public void moveY(double d) {
		move(y.dup().mul(d));
	}

	/**
	 * move along object z direction
	 */
	public void moveZ(double d) {
		move(z.dup().mul(d));
	}

	/**
	 * move
	 */
	synchronized public void move(Vec d) {
		pos.add(d);
		if (lookAt != null)
			lookAt.add(d);
		updateViewMatrix();
	}

	/**
	 * zoom in
	 */
	public void zoomIn(double d) {
		moveIn(d);
	}

	/**
	 * zoom out
	 */
	public void zoomOut(double d) {
		moveIn(-d);
	}

	/**
	 * move in or out(lookAt point stays still)
	 */
	synchronized public void moveIn(double dist) {
		pos.add(z.dup().mul(dist));
		updateViewMatrix();
		if (!perspective)
			updateProjectionMatrix();
	}

	/**
	 * rotate along x axis
	 */
	public void rotateCameraX(double angle) {
		rotate(pos, x, angle);
	}

	/**
	 * rotate along x axis
	 */
	public void rotateCameraY(double angle) {
		rotate(pos, y, angle);
	}

	/**
	 * rotate along x axis
	 */
	public void rotateCameraZ(double angle) {
		rotate(pos, z, angle);
	}

	/**
	 * rotate around the world x axis
	 */
	public void rotateXWorld(double angle) {
		rotate(pos, new Vec(1, 0, 0), angle);
	}

	/**
	 * rotate around the world x axis
	 */
	public void rotateYWorld(double angle) {
		rotate(pos, new Vec(0, 1, 0), angle);

	}

	/**
	 * rotate around the world x axis
	 */
	public void rotateZWorld(double angle) {
		rotate(pos, new Vec(0, 0, 1), angle);
	}

	/**
	 * rotate around the look at point horizontal
	 */
	public void rotateLookAtHorizontal(double angle) {
		if (lookAt != null) {
			rotate(lookAt, new Vec(0, 0, 1), angle);
		}
	}

	/**
	 * rotate around the look at point vertical
	 */
	public void rotateLookAtVertical(double angle) {
		if (lookAt != null) {
			rotate(lookAt, x.dup(), angle);
		}
	}

	/**
	 * rotate
	 */
	synchronized public void rotate(Vec center, Vec axis, double angle) {
		pos.rot(center, axis, angle);
		x.rot(axis, angle);
		y.rot(axis, angle);
		z.rot(axis, angle);
		if (lookAt != null)
			lookAt.rot(center, axis, angle);
		updateViewMatrix();
	}

	/**
	 * scale
	 */
	synchronized public void scale(Vec center, double s) {
		pos.scale(center, s);
		if (lookAt != null)
			lookAt.scale(center, s);
		updateViewMatrix();
	}

	/**
	 * copy the camera
	 */
	public Camera copy() {
		return new Camera(this);
	}

	/**
	 * get coordinate in 3d space from mouse position
	 * 
	 * @return Vec[]{position on screen, direction};
	 */
	public Vec[] pick3d(double mouseX, double mouseY) {
		Vec pt = pick3d(mouseX, mouseY, 0);
		Vec dir = pick3d(mouseX, mouseY, 1);
		dir.sub(pt).unit();
		return new Vec[] { pt, dir };
	}

	/**
	 * Pick a point on x-y plane from the coordinate on screen
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public Vec pick3dXYPlane(double mouseX, double mouseY) {
		Vec[] pt = pick3d(mouseX, mouseY);
		Vec ptp = pt[0].dup().sub(Vec.origin);
		double dot = ptp.dot(Vec.zaxis);
		double distToPlan = Math.abs(dot);
		double zVal = -1 * Math.signum(dot);
		Vec toPlan = new Vec(0, 0, zVal);
		double cos = pt[1].dot(toPlan);
		double len = distToPlan / cos;
		return pt[0].dup().add(pt[1].dup().mul(len));
	}

	/**
	 * get coordinate in 3d space from mouse position and depth on screen.
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @param zPos
	 *            depth on screen, 0 to 1.
	 * @return
	 */

	synchronized public Vec pick3d(double mouseX, double mouseY, double zPos) {
		updateViewProjectionMatrix();
		Vec store = new Vec((mouseX / width - viewPortLeft) / (viewPortRight - viewPortLeft) * 2 - 1, (mouseY / height - viewPortBottom)
				/ (viewPortTop - viewPortBottom) * 2 - 1, zPos * 2 - 1);
		double w = inverseMatrix.multProj(store, store);
		store.mul(1.0 / w);
		return store;
	}

	/**
	 * resize
	 */
	synchronized public void parentResize() {
		if (parentType == PARENT_TYPE_PAPPLET) {
			this.width = parentApplet.width;
			this.height = parentApplet.height;
			this.updateViewMatrix();
			this.updateProjectionMatrix();
		} else if (parentType == PARENT_TYPE_PGRAPHICS) {
			this.width = parentGraphics.width;
			this.height = parentGraphics.height;
			this.updateViewMatrix();
			this.updateProjectionMatrix();
		}
	}

	/**
	 * get the camera works
	 * <p>
	 * put this method in draw loop and call it before drawing anything 3d
	 */
	synchronized public void applyCamera() {
		updateMatrix();
		float[][] m = viewMatrixFloat;

		if (parentType == PARENT_TYPE_PAPPLET) {
			applied = true;
			parentApplet.pushMatrix();

			parentApplet.camera(widthHalf, heightHalf, PROCESSING_CAMERA_POSITION, widthHalf, heightHalf, 0, 0, 1, 0);
			parentApplet.translate(widthHalf, heightHalf);
			if (perspective) {
				parentApplet.frustum((float) frustumLeft, (float) frustumRight, (float) frustumBottom, (float) frustumTop, (float) frustumNear,
						(float) frustumFar);
			} else {
				/*
				 * 2014-11-03: When used these code in processing2.0 and below,
				 * widthHalf is unnecessary and near should change to -far.
				 */
				parentApplet.ortho((float) frustumLeft + widthHalf, (float) frustumRight + widthHalf, (float) frustumBottom + heightHalf, (float) frustumTop
						+ heightHalf, (float) -frustumFar, (float) frustumFar);
			}
			parentApplet.applyMatrix(m[0][0], m[0][1], m[0][2], m[0][3], m[1][0], m[1][1], m[1][2], m[1][3], m[2][0], m[2][1], m[2][2], m[2][3], m[3][0],
					m[3][1], m[3][2], m[3][3]);
		} else if (parentType == PARENT_TYPE_PGRAPHICS) {
			applied = true;
			parentGraphics.pushMatrix();

			parentGraphics.camera(widthHalf, heightHalf, PROCESSING_CAMERA_POSITION, widthHalf, heightHalf, 0, 0, 1, 0);
			parentGraphics.translate(widthHalf, heightHalf);
			if (perspective) {
				parentGraphics.frustum((float) frustumLeft, (float) frustumRight, (float) frustumBottom, (float) frustumTop, (float) frustumNear,
						(float) frustumFar);
			} else {
				/*
				 * 2014-11-03: When used these code in processing2.0 and below,
				 * widthHalf is unnecessary and near should change to -far.
				 */
				parentGraphics.ortho((float) frustumLeft + widthHalf, (float) frustumRight + widthHalf, (float) frustumBottom + heightHalf, (float) frustumTop
						+ heightHalf, (float) -frustumFar, (float) frustumFar);
			}
			parentGraphics.applyMatrix(m[0][0], m[0][1], m[0][2], m[0][3], m[1][0], m[1][1], m[1][2], m[1][3], m[2][0], m[2][1], m[2][2], m[2][3], m[3][0],
					m[3][1], m[3][2], m[3][3]);
		} else {
			System.err.println("Cannot apply camera with out canvas!" + '\n' + "(need instance of PApplet or PGraphics as parent)");
		}
	}

	/**
	 * reset the view matrix of the processing applet
	 */
	synchronized public void resetAppMatrix() {
		if (applied) {
			applied = false;
			if (parentType == PARENT_TYPE_PAPPLET) {
				parentApplet.popMatrix();
			} else if (parentType == PARENT_TYPE_PGRAPHICS) {
				parentGraphics.popMatrix();
			}
		}
	}

	private void drawGrid(float len, float num) {
		float weight = 0.3f;
		int gray = 100;
		len *= 2;
		float step = len / num;
		float start = -len / 2;
		if (parentType == PARENT_TYPE_PAPPLET) {
			parentApplet.stroke(gray);
			parentApplet.strokeWeight(weight);
			for (int i = 0; i <= num; i++) {
				parentApplet.line(start, i * step + start, 0, len + start, i * step + start, 0);
				parentApplet.line(i * step + start, start, 0, i * step + start, len + start, 0);
			}
		} else if (parentType == PARENT_TYPE_PGRAPHICS) {
			parentGraphics.stroke(gray);
			parentGraphics.strokeWeight(weight);
			for (int i = 0; i <= num; i++) {
				parentGraphics.line(start, i * step + start, 0, len + start, i * step + start, 0);
				parentGraphics.line(i * step + start, start, 0, i * step + start, len + start, 0);
			}
		}
	}

	/**
	 * don't get lost in an empty 3d space
	 */
	public void drawSystem(float len) {
		if (parentType == PARENT_TYPE_PAPPLET) {
			parentApplet.pushStyle();
			drawGrid(len, 20);
			parentApplet.strokeWeight(1);
			parentApplet.stroke(0xffff0000);
			parentApplet.line(0, 0, 0, len, 0, 0);
			parentApplet.stroke(0xff00ff00);
			parentApplet.line(0, 0, 0, 0, len, 0);
			parentApplet.stroke(0xff0000ff);
			parentApplet.line(0, 0, 0, 0, 0, len);
			parentApplet.popStyle();
		} else if (parentType == PARENT_TYPE_PGRAPHICS) {
			parentGraphics.pushStyle();
			drawGrid(len, 20);
			parentGraphics.strokeWeight(1);
			parentGraphics.stroke(0xffff0000);
			parentGraphics.line(0, 0, 0, len, 0, 0);
			parentGraphics.stroke(0xff00ff00);
			parentGraphics.line(0, 0, 0, 0, len, 0);
			parentGraphics.stroke(0xff0000ff);
			parentGraphics.line(0, 0, 0, 0, 0, len);
			parentGraphics.popStyle();
		} else {
			System.err.println("Cannot draw with out canvas!" + '\n' + "(need instance of PApplet or PGraphics as parent)");
		}
	}
}
