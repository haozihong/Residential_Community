package balls;

import igeo.*;

public class HPoint extends HAgent implements IVecI {
	public IVec pos;
	boolean show = true;
	float r = 2.5f;
	
	public HPoint() {
		pos = new IVec();
	}
	
	public HPoint(HServer server, HPoint pt) {
		this(server, pt.pos.dup());
	}
	
	public HPoint(HServer server, IVecI pt) {
		this.server = server;
		server.addAgent(this);
		pos = pt.get();
	}
	
	public HPoint show() {
		show = true;
		return this;
	}
	
	public HPoint hide() {
		show = false;
		return this;
	}
	
	public void del() {
		server.agents.remove(this);
	}
	
	public HPoint ptSize(float r) {
		this.r = r;
		return this;
	}
	
	public void drawGraphic() {
		if (show) {
			server.main.fill(0);
			server.main.ellipse((float) pos.x, (float) pos.y, r, r);
		}
	}

	@Override
	public double x() {
		return pos.x;
	}

	@Override
	public double y() {
		return pos.y;
	}

	@Override
	public double z() {
		return pos.z;
	}

	@Override
	public IVecI x(double vx) {
		pos.x(vx);
		return this;
	}

	@Override
	public IVecI y(double vy) {
		pos.y(vy);
		return this;
	}

	@Override
	public IVecI z(double vz) {
		pos.z(vz);
		return this;
	}

	@Override
	public IVecI x(IDoubleI vx) {
		pos.x(vx);
		return this;
	}

	@Override
	public IVecI y(IDoubleI vy) {
		pos.y(vy);
		return this;
	}

	@Override
	public IVecI z(IDoubleI vz) {
		pos.z(vz);
		return this;
	}

	@Override
	public IVecI x(IVecI v) {
		pos.x(v);
		return this;
	}

	@Override
	public IVecI y(IVecI v) {
		pos.y(v);
		return this;
	}

	@Override
	public IVecI z(IVecI v) {
		pos.z(v);
		return this;
	}

	@Override
	public IVecI x(IVec2I v) {
		pos.x(v);
		return this;
	}

	@Override
	public IVecI y(IVec2I v) {
		pos.y(v);
		return this;
	}

	@Override
	public double x(ISwitchE e) {
		return pos.x(e);
	}

	@Override
	public double y(ISwitchE e) {
		return pos.y(e);
	}

	@Override
	public double z(ISwitchE e) {
		return pos.z(e);
	}

	@Override
	public IDoubleI x(ISwitchR r) {
		return pos.x(r);
	}

	@Override
	public IDoubleI y(ISwitchR r) {
		return pos.y(r);
	}

	@Override
	public IDoubleI z(ISwitchR r) {
		return pos.z(r);
	}

	@Override
	public IVec get() {
		return pos;
	}

	@Override
	public IVecI dup() {
		return new HParticle(server,this);
	}

	@Override
	public IVec2I to2d() {
		return new IVec2(pos);
	}

	@Override
	public IVec2I to2d(IVecI projectionDir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVec2I to2d(IVecI xaxis, IVecI yaxis) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVec2I to2d(IVecI xaxis, IVecI yaxis, IVecI origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVec4I to4d() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVec4I to4d(double w) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVec4I to4d(IDoubleI w) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDoubleI getX() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDoubleI getY() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDoubleI getZ() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI set(IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI set(double x, double y, double z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI set(IDoubleI x, IDoubleI y, IDoubleI z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI add(double x, double y, double z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI add(IDoubleI x, IDoubleI y, IDoubleI z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI add(IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI sub(double x, double y, double z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI sub(IDoubleI x, IDoubleI y, IDoubleI z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI sub(IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI mul(IDoubleI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI mul(double v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI div(IDoubleI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI div(double v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI neg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rev() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI flip() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI zero() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI add(IVecI v, double f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI add(IVecI v, IDoubleI f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI add(double f, IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI add(IDoubleI f, IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double dot(IVecI v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double dot(double vx, double vy, double vz) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double dot(ISwitchE e, IVecI v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IDoubleI dot(ISwitchR r, IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI cross(IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI cross(double vx, double vy, double vz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double len() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double len(ISwitchE e) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IDoubleI len(ISwitchR r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double len2() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double len2(ISwitchE e) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IDoubleI len2(ISwitchR r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI len(IDoubleI l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI len(double l) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI unit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double dist(IVecI v) {
		return pos.dist(v);
	}

	@Override
	public double dist(double vx, double vy, double vz) {
		return pos.dist(vx, vy, vz);
	}

	@Override
	public double dist(ISwitchE e, IVecI v) {
		return pos.dist(e, v);
	}

	@Override
	public IDoubleI dist(ISwitchR r, IVecI v) {
		return pos.dist(r, v);
	}

	@Override
	public double dist2(IVecI v) {
		return pos.dist2(v);
	}

	@Override
	public double dist2(double vx, double vy, double vz) {
		return pos.dist2(vx, vy, vz);
	}

	@Override
	public double dist2(ISwitchE e, IVecI v) {
		return pos.dist2(e, v);
	}

	@Override
	public IDoubleI dist2(ISwitchR r, IVecI v) {
		return pos.dist2(r, v);
	}

	@Override
	public boolean eq(IVecI v) {
		return pos.eq(v);
	}

	@Override
	public boolean eq(double vx, double vy, double vz) {
		return pos.eq(vx, vy, vz);
	}

	@Override
	public boolean eq(ISwitchE e, IVecI v) {
		return pos.eq(e, v);
	}

	@Override
	public IBoolI eq(ISwitchR r, IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean eq(IVecI v, double tolerance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eq(double vx, double vy, double vz, double tolerance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eq(ISwitchE e, IVecI v, double tolerance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IBoolI eq(ISwitchR r, IVecI v, IDoubleI tolerance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean eqX(IVecI v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqY(IVecI v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqZ(IVecI v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqX(double vx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqY(double vy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqZ(double vz) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqX(ISwitchE e, IVecI v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqY(ISwitchE e, IVecI v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqZ(ISwitchE e, IVecI v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IBoolI eqX(ISwitchR r, IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBoolI eqY(ISwitchR r, IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBoolI eqZ(ISwitchR r, IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean eqX(IVecI v, double tolerance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqY(IVecI v, double tolerance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqZ(IVecI v, double tolerance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqX(double vx, double tolerance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqY(double vy, double tolerance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqZ(double vz, double tolerance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqX(ISwitchE e, IVecI v, double tolerance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqY(ISwitchE e, IVecI v, double tolerance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean eqZ(ISwitchE e, IVecI v, double tolerance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IBoolI eqX(ISwitchR r, IVecI v, IDoubleI tolerance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBoolI eqY(ISwitchR r, IVecI v, IDoubleI tolerance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBoolI eqZ(ISwitchR r, IVecI v, IDoubleI tolerance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double angle(IVecI v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double angle(double vx, double vy, double vz) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double angle(ISwitchE e, IVecI v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IDoubleI angle(ISwitchR r, IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double angle(IVecI v, IVecI axis) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double angle(double vx, double vy, double vz, double axisX, double axisY, double axisZ) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double angle(ISwitchE e, IVecI v, IVecI axis) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IDoubleI angle(ISwitchR r, IVecI v, IVecI axis) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot(IVecI axis, IDoubleI angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot(IVecI axis, double angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot(double axisX, double axisY, double axisZ, double angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot(double angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot(IDoubleI angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot(IVecI center, IVecI axis, IDoubleI angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot(IVecI center, IVecI axis, double angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot(double centerX, double centerY, double centerZ, double axisX, double axisY, double axisZ,
			double angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot(IVecI axis, IVecI destDir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot(IVecI center, IVecI axis, IVecI destPt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot2(double angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot2(IDoubleI angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot2(IVecI center, double angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot2(IVecI center, IDoubleI angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot2(double centerX, double centerY, double angle) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot2(IVecI destDir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI rot2(IVecI center, IVecI destPt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI scale(IDoubleI f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI scale(double f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI scale(IVecI center, IDoubleI f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI scale(IVecI center, double f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI scale(double centerX, double centerY, double centerZ, double f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI scale1d(IVecI axis, double f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI scale1d(IVecI axis, IDoubleI f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI scale1d(double axisX, double axisY, double axisZ, double f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI scale1d(IVecI center, IVecI axis, double f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI scale1d(IVecI center, IVecI axis, IDoubleI f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI scale1d(double centerX, double centerY, double centerZ, double axisX, double axisY, double axisZ,
			double f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI ref(IVecI planeDir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI ref(double planeX, double planeY, double planeZ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI ref(IVecI center, IVecI planeDir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI ref(double centerX, double centerY, double centerZ, double planeX, double planeY, double planeZ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI mirror(IVecI planeDir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI mirror(double planeX, double planeY, double planeZ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI mirror(IVecI center, IVecI planeDir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI mirror(double centerX, double centerY, double centerZ, double planeX, double planeY, double planeZ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shear(double sxy, double syx, double syz, double szy, double szx, double sxz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shear(IDoubleI sxy, IDoubleI syx, IDoubleI syz, IDoubleI szy, IDoubleI szx, IDoubleI sxz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shear(IVecI center, double sxy, double syx, double syz, double szy, double szx, double sxz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shear(IVecI center, IDoubleI sxy, IDoubleI syx, IDoubleI syz, IDoubleI szy, IDoubleI szx,
			IDoubleI sxz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shearXY(double sxy, double syx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shearXY(IDoubleI sxy, IDoubleI syx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shearXY(IVecI center, double sxy, double syx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shearXY(IVecI center, IDoubleI sxy, IDoubleI syx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shearYZ(double syz, double szy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shearYZ(IDoubleI syz, IDoubleI szy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shearYZ(IVecI center, double syz, double szy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shearYZ(IVecI center, IDoubleI syz, IDoubleI szy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shearZX(double szx, double sxz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shearZX(IDoubleI szx, IDoubleI sxz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shearZX(IVecI center, double szx, double sxz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI shearZX(IVecI center, IDoubleI szx, IDoubleI sxz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI translate(double x, double y, double z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI translate(IDoubleI x, IDoubleI y, IDoubleI z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI translate(IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI transform(IMatrix3I mat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI transform(IMatrix4I mat) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI transform(IVecI xvec, IVecI yvec, IVecI zvec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI transform(IVecI xvec, IVecI yvec, IVecI zvec, IVecI translate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI mv(double x, double y, double z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI mv(IDoubleI x, IDoubleI y, IDoubleI z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI mv(IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI cp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI cp(double x, double y, double z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI cp(IDoubleI x, IDoubleI y, IDoubleI z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI cp(IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVec dif(IVecI v) {
		return pos.dif(v);
	}

	@Override
	public IVecI dif(double vx, double vy, double vz) {
		return pos.dif(vx, vy, vz);
	}

	@Override
	public IVecI diff(IVecI v) {
		return pos.diff(v);
	}

	@Override
	public IVecI diff(double vx, double vy, double vz) {
		return pos.diff(vx, vy, vz);
	}

	@Override
	public IVecI mid(IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI mid(double vx, double vy, double vz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI sum(IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI sum(double vx, double vy, double vz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI sum(IVecI... v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI bisect(IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI bisect(double vx, double vy, double vz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI sum(IVecI v2, double w1, double w2) {
		return pos.sum(v2, w1, w2);
	}

	@Override
	public IVecI sum(IVecI v2, double w2) {
		return pos.sum(v2, w2);
	}

	@Override
	public IVecI sum(IVecI v2, IDoubleI w1, IDoubleI w2) {
		return pos.sum(v2, w1, w2);
	}

	@Override
	public IVecI sum(IVecI v2, IDoubleI w2) {
		return pos.sum(v2, w2);
	}

	@Override
	public IVecI nml(IVecI v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI nml(double vx, double vy, double vz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI nml(IVecI pt1, IVecI pt2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVecI nml(double vx1, double vy1, double vz1, double vx2, double vy2, double vz2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid() {
		if(pos==null){ return false; } return pos.isValid();
	}

}
