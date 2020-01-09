package gzf.gui;

import gzf.Vec;

public class Matrix4 {
	private double[][] matrix = new double[4][4];

	public Matrix4() {

	}

	public Matrix4(double[][] data) {
		matrix = data;
	}

	public Matrix4(Matrix4 mx) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = mx.matrix[i][j];
			}
		}
	}

	public double[][] getMatrix() {
		return matrix;
	}

	public Matrix4 mult(double s) {
		Matrix4 out = new Matrix4(this);
		for (int i = 0; i < out.matrix.length; i++) {
			for (int j = 0; j < out.matrix[i].length; j++) {
				out.matrix[i][j] *= s;
			}
		}
		return out;
	}

	public Matrix4 multLocal(double s) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] *= s;
			}
		}
		return this;
	}

	public Matrix4 mult(Matrix4 mx) {
		Matrix4 out = new Matrix4();

		double temp00, temp01, temp02, temp03;
		double temp10, temp11, temp12, temp13;
		double temp20, temp21, temp22, temp23;
		double temp30, temp31, temp32, temp33;

		temp00 = matrix[0][0] * mx.matrix[0][0] + matrix[0][1] * mx.matrix[1][0] + matrix[0][2] * mx.matrix[2][0] + matrix[0][3] * mx.matrix[3][0];
		temp01 = matrix[0][0] * mx.matrix[0][1] + matrix[0][1] * mx.matrix[1][1] + matrix[0][2] * mx.matrix[2][1] + matrix[0][3] * mx.matrix[3][1];
		temp02 = matrix[0][0] * mx.matrix[0][2] + matrix[0][1] * mx.matrix[1][2] + matrix[0][2] * mx.matrix[2][2] + matrix[0][3] * mx.matrix[3][2];
		temp03 = matrix[0][0] * mx.matrix[0][3] + matrix[0][1] * mx.matrix[1][3] + matrix[0][2] * mx.matrix[2][3] + matrix[0][3] * mx.matrix[3][3];

		temp10 = matrix[1][0] * mx.matrix[0][0] + matrix[1][1] * mx.matrix[1][0] + matrix[1][2] * mx.matrix[2][0] + matrix[1][3] * mx.matrix[3][0];
		temp11 = matrix[1][0] * mx.matrix[0][1] + matrix[1][1] * mx.matrix[1][1] + matrix[1][2] * mx.matrix[2][1] + matrix[1][3] * mx.matrix[3][1];
		temp12 = matrix[1][0] * mx.matrix[0][2] + matrix[1][1] * mx.matrix[1][2] + matrix[1][2] * mx.matrix[2][2] + matrix[1][3] * mx.matrix[3][2];
		temp13 = matrix[1][0] * mx.matrix[0][3] + matrix[1][1] * mx.matrix[1][3] + matrix[1][2] * mx.matrix[2][3] + matrix[1][3] * mx.matrix[3][3];

		temp20 = matrix[2][0] * mx.matrix[0][0] + matrix[2][1] * mx.matrix[1][0] + matrix[2][2] * mx.matrix[2][0] + matrix[2][3] * mx.matrix[3][0];
		temp21 = matrix[2][0] * mx.matrix[0][1] + matrix[2][1] * mx.matrix[1][1] + matrix[2][2] * mx.matrix[2][1] + matrix[2][3] * mx.matrix[3][1];
		temp22 = matrix[2][0] * mx.matrix[0][2] + matrix[2][1] * mx.matrix[1][2] + matrix[2][2] * mx.matrix[2][2] + matrix[2][3] * mx.matrix[3][2];
		temp23 = matrix[2][0] * mx.matrix[0][3] + matrix[2][1] * mx.matrix[1][3] + matrix[2][2] * mx.matrix[2][3] + matrix[2][3] * mx.matrix[3][3];

		temp30 = matrix[3][0] * mx.matrix[0][0] + matrix[3][1] * mx.matrix[1][0] + matrix[3][2] * mx.matrix[2][0] + matrix[3][3] * mx.matrix[3][0];
		temp31 = matrix[3][0] * mx.matrix[0][1] + matrix[3][1] * mx.matrix[1][1] + matrix[3][2] * mx.matrix[2][1] + matrix[3][3] * mx.matrix[3][1];
		temp32 = matrix[3][0] * mx.matrix[0][2] + matrix[3][1] * mx.matrix[1][2] + matrix[3][2] * mx.matrix[2][2] + matrix[3][3] * mx.matrix[3][2];
		temp33 = matrix[3][0] * mx.matrix[0][3] + matrix[3][1] * mx.matrix[1][3] + matrix[3][2] * mx.matrix[2][3] + matrix[3][3] * mx.matrix[3][3];

		out.matrix[0][0] = temp00;
		out.matrix[0][1] = temp01;
		out.matrix[0][2] = temp02;
		out.matrix[0][3] = temp03;
		out.matrix[1][0] = temp10;
		out.matrix[1][1] = temp11;
		out.matrix[1][2] = temp12;
		out.matrix[1][3] = temp13;
		out.matrix[2][0] = temp20;
		out.matrix[2][1] = temp21;
		out.matrix[2][2] = temp22;
		out.matrix[2][3] = temp23;
		out.matrix[3][0] = temp30;
		out.matrix[3][1] = temp31;
		out.matrix[3][2] = temp32;
		out.matrix[3][3] = temp33;

		return out;
	}

	public Matrix4 multLocal(Matrix4 mx) {
		double temp00, temp01, temp02, temp03;
		double temp10, temp11, temp12, temp13;
		double temp20, temp21, temp22, temp23;
		double temp30, temp31, temp32, temp33;

		temp00 = matrix[0][0] * mx.matrix[0][0] + matrix[0][1] * mx.matrix[1][0] + matrix[0][2] * mx.matrix[2][0] + matrix[0][3] * mx.matrix[3][0];
		temp01 = matrix[0][0] * mx.matrix[0][1] + matrix[0][1] * mx.matrix[1][1] + matrix[0][2] * mx.matrix[2][1] + matrix[0][3] * mx.matrix[3][1];
		temp02 = matrix[0][0] * mx.matrix[0][2] + matrix[0][1] * mx.matrix[1][2] + matrix[0][2] * mx.matrix[2][2] + matrix[0][3] * mx.matrix[3][2];
		temp03 = matrix[0][0] * mx.matrix[0][3] + matrix[0][1] * mx.matrix[1][3] + matrix[0][2] * mx.matrix[2][3] + matrix[0][3] * mx.matrix[3][3];

		temp10 = matrix[1][0] * mx.matrix[0][0] + matrix[1][1] * mx.matrix[1][0] + matrix[1][2] * mx.matrix[2][0] + matrix[1][3] * mx.matrix[3][0];
		temp11 = matrix[1][0] * mx.matrix[0][1] + matrix[1][1] * mx.matrix[1][1] + matrix[1][2] * mx.matrix[2][1] + matrix[1][3] * mx.matrix[3][1];
		temp12 = matrix[1][0] * mx.matrix[0][2] + matrix[1][1] * mx.matrix[1][2] + matrix[1][2] * mx.matrix[2][2] + matrix[1][3] * mx.matrix[3][2];
		temp13 = matrix[1][0] * mx.matrix[0][3] + matrix[1][1] * mx.matrix[1][3] + matrix[1][2] * mx.matrix[2][3] + matrix[1][3] * mx.matrix[3][3];

		temp20 = matrix[2][0] * mx.matrix[0][0] + matrix[2][1] * mx.matrix[1][0] + matrix[2][2] * mx.matrix[2][0] + matrix[2][3] * mx.matrix[3][0];
		temp21 = matrix[2][0] * mx.matrix[0][1] + matrix[2][1] * mx.matrix[1][1] + matrix[2][2] * mx.matrix[2][1] + matrix[2][3] * mx.matrix[3][1];
		temp22 = matrix[2][0] * mx.matrix[0][2] + matrix[2][1] * mx.matrix[1][2] + matrix[2][2] * mx.matrix[2][2] + matrix[2][3] * mx.matrix[3][2];
		temp23 = matrix[2][0] * mx.matrix[0][3] + matrix[2][1] * mx.matrix[1][3] + matrix[2][2] * mx.matrix[2][3] + matrix[2][3] * mx.matrix[3][3];

		temp30 = matrix[3][0] * mx.matrix[0][0] + matrix[3][1] * mx.matrix[1][0] + matrix[3][2] * mx.matrix[2][0] + matrix[3][3] * mx.matrix[3][0];
		temp31 = matrix[3][0] * mx.matrix[0][1] + matrix[3][1] * mx.matrix[1][1] + matrix[3][2] * mx.matrix[2][1] + matrix[3][3] * mx.matrix[3][1];
		temp32 = matrix[3][0] * mx.matrix[0][2] + matrix[3][1] * mx.matrix[1][2] + matrix[3][2] * mx.matrix[2][2] + matrix[3][3] * mx.matrix[3][2];
		temp33 = matrix[3][0] * mx.matrix[0][3] + matrix[3][1] * mx.matrix[1][3] + matrix[3][2] * mx.matrix[2][3] + matrix[3][3] * mx.matrix[3][3];

		matrix[0][0] = temp00;
		matrix[0][1] = temp01;
		matrix[0][2] = temp02;
		matrix[0][3] = temp03;
		matrix[1][0] = temp10;
		matrix[1][1] = temp11;
		matrix[1][2] = temp12;
		matrix[1][3] = temp13;
		matrix[2][0] = temp20;
		matrix[2][1] = temp21;
		matrix[2][2] = temp22;
		matrix[2][3] = temp23;
		matrix[3][0] = temp30;
		matrix[3][1] = temp31;
		matrix[3][2] = temp32;
		matrix[3][3] = temp33;

		return this;
	}

	public Matrix4 invert() {
		Matrix4 store = new Matrix4();

		double fA0 = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
		double fA1 = matrix[0][0] * matrix[1][2] - matrix[0][2] * matrix[1][0];
		double fA2 = matrix[0][0] * matrix[1][3] - matrix[0][3] * matrix[1][0];
		double fA3 = matrix[0][1] * matrix[1][2] - matrix[0][2] * matrix[1][1];
		double fA4 = matrix[0][1] * matrix[1][3] - matrix[0][3] * matrix[1][1];
		double fA5 = matrix[0][2] * matrix[1][3] - matrix[0][3] * matrix[1][2];
		double fB0 = matrix[2][0] * matrix[3][1] - matrix[2][1] * matrix[3][0];
		double fB1 = matrix[2][0] * matrix[3][2] - matrix[2][2] * matrix[3][0];
		double fB2 = matrix[2][0] * matrix[3][3] - matrix[2][3] * matrix[3][0];
		double fB3 = matrix[2][1] * matrix[3][2] - matrix[2][2] * matrix[3][1];
		double fB4 = matrix[2][1] * matrix[3][3] - matrix[2][3] * matrix[3][1];
		double fB5 = matrix[2][2] * matrix[3][3] - matrix[2][3] * matrix[3][2];
		double fDet = fA0 * fB5 - fA1 * fB4 + fA2 * fB3 + fA3 * fB2 - fA4 * fB1 + fA5 * fB0;

		if (Math.abs(fDet) <= 0f) {
			throw new ArithmeticException("This matrix cannot be inverted");
		}

		store.matrix[0][0] = +matrix[1][1] * fB5 - matrix[1][2] * fB4 + matrix[1][3] * fB3;
		store.matrix[1][0] = -matrix[1][0] * fB5 + matrix[1][2] * fB2 - matrix[1][3] * fB1;
		store.matrix[2][0] = +matrix[1][0] * fB4 - matrix[1][1] * fB2 + matrix[1][3] * fB0;
		store.matrix[3][0] = -matrix[1][0] * fB3 + matrix[1][1] * fB1 - matrix[1][2] * fB0;
		store.matrix[0][1] = -matrix[0][1] * fB5 + matrix[0][2] * fB4 - matrix[0][3] * fB3;
		store.matrix[1][1] = +matrix[0][0] * fB5 - matrix[0][2] * fB2 + matrix[0][3] * fB1;
		store.matrix[2][1] = -matrix[0][0] * fB4 + matrix[0][1] * fB2 - matrix[0][3] * fB0;
		store.matrix[3][1] = +matrix[0][0] * fB3 - matrix[0][1] * fB1 + matrix[0][2] * fB0;
		store.matrix[0][2] = +matrix[3][1] * fA5 - matrix[3][2] * fA4 + matrix[3][3] * fA3;
		store.matrix[1][2] = -matrix[3][0] * fA5 + matrix[3][2] * fA2 - matrix[3][3] * fA1;
		store.matrix[2][2] = +matrix[3][0] * fA4 - matrix[3][1] * fA2 + matrix[3][3] * fA0;
		store.matrix[3][2] = -matrix[3][0] * fA3 + matrix[3][1] * fA1 - matrix[3][2] * fA0;
		store.matrix[0][3] = -matrix[2][1] * fA5 + matrix[2][2] * fA4 - matrix[2][3] * fA3;
		store.matrix[1][3] = +matrix[2][0] * fA5 - matrix[2][2] * fA2 + matrix[2][3] * fA1;
		store.matrix[2][3] = -matrix[2][0] * fA4 + matrix[2][1] * fA2 - matrix[2][3] * fA0;
		store.matrix[3][3] = +matrix[2][0] * fA3 - matrix[2][1] * fA1 + matrix[2][2] * fA0;

		double fInvDet = 1.0 / fDet;
		store.multLocal(fInvDet);
		return store;
	}

	public Matrix4 invertLocal() {
		double max[][] = new double[4][4];

		double fA0 = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
		double fA1 = matrix[0][0] * matrix[1][2] - matrix[0][2] * matrix[1][0];
		double fA2 = matrix[0][0] * matrix[1][3] - matrix[0][3] * matrix[1][0];
		double fA3 = matrix[0][1] * matrix[1][2] - matrix[0][2] * matrix[1][1];
		double fA4 = matrix[0][1] * matrix[1][3] - matrix[0][3] * matrix[1][1];
		double fA5 = matrix[0][2] * matrix[1][3] - matrix[0][3] * matrix[1][2];
		double fB0 = matrix[2][0] * matrix[3][1] - matrix[2][1] * matrix[3][0];
		double fB1 = matrix[2][0] * matrix[3][2] - matrix[2][2] * matrix[3][0];
		double fB2 = matrix[2][0] * matrix[3][3] - matrix[2][3] * matrix[3][0];
		double fB3 = matrix[2][1] * matrix[3][2] - matrix[2][2] * matrix[3][1];
		double fB4 = matrix[2][1] * matrix[3][3] - matrix[2][3] * matrix[3][1];
		double fB5 = matrix[2][2] * matrix[3][3] - matrix[2][3] * matrix[3][2];
		double fDet = fA0 * fB5 - fA1 * fB4 + fA2 * fB3 + fA3 * fB2 - fA4 * fB1 + fA5 * fB0;

		if (Math.abs(fDet) <= 0f) {
			throw new ArithmeticException("This matrix cannot be inverted");
		}

		max[0][0] = +matrix[1][1] * fB5 - matrix[1][2] * fB4 + matrix[1][3] * fB3;
		max[1][0] = -matrix[1][0] * fB5 + matrix[1][2] * fB2 - matrix[1][3] * fB1;
		max[2][0] = +matrix[1][0] * fB4 - matrix[1][1] * fB2 + matrix[1][3] * fB0;
		max[3][0] = -matrix[1][0] * fB3 + matrix[1][1] * fB1 - matrix[1][2] * fB0;
		max[0][1] = -matrix[0][1] * fB5 + matrix[0][2] * fB4 - matrix[0][3] * fB3;
		max[1][1] = +matrix[0][0] * fB5 - matrix[0][2] * fB2 + matrix[0][3] * fB1;
		max[2][1] = -matrix[0][0] * fB4 + matrix[0][1] * fB2 - matrix[0][3] * fB0;
		max[3][1] = +matrix[0][0] * fB3 - matrix[0][1] * fB1 + matrix[0][2] * fB0;
		max[0][2] = +matrix[3][1] * fA5 - matrix[3][2] * fA4 + matrix[3][3] * fA3;
		max[1][2] = -matrix[3][0] * fA5 + matrix[3][2] * fA2 - matrix[3][3] * fA1;
		max[2][2] = +matrix[3][0] * fA4 - matrix[3][1] * fA2 + matrix[3][3] * fA0;
		max[3][2] = -matrix[3][0] * fA3 + matrix[3][1] * fA1 - matrix[3][2] * fA0;
		max[0][3] = -matrix[2][1] * fA5 + matrix[2][2] * fA4 - matrix[2][3] * fA3;
		max[1][3] = +matrix[2][0] * fA5 - matrix[2][2] * fA2 + matrix[2][3] * fA1;
		max[2][3] = -matrix[2][0] * fA4 + matrix[2][1] * fA2 - matrix[2][3] * fA0;
		max[3][3] = +matrix[2][0] * fA3 - matrix[2][1] * fA1 + matrix[2][2] * fA0;

		this.matrix = max;

		double fInvDet = 1.0 / fDet;
		multLocal(fInvDet);
		return this;
	}

	public double multProj(Vec vec, Vec store) {
		double vx = vec.x, vy = vec.y, vz = vec.z;
		store.x = matrix[0][0] * vx + matrix[0][1] * vy + matrix[0][2] * vz + matrix[0][3];
		store.y = matrix[1][0] * vx + matrix[1][1] * vy + matrix[1][2] * vz + matrix[1][3];
		store.z = matrix[2][0] * vx + matrix[2][1] * vy + matrix[2][2] * vz + matrix[2][3];
		return matrix[3][0] * vx + matrix[3][1] * vy + matrix[3][2] * vz + matrix[3][3];
	}

	public void fromFrustum(double near, double far, double left, double right, double top, double bottom, boolean parallel) {
		if (parallel) {
			// scale
			matrix[0][0] = 2.0 / (right - left);
			// matrix[1][1] = 2.0f / (bottom - top);
			matrix[1][1] = 2.0f / (top - bottom);
			matrix[2][2] = -2.0f / (far - near);
			matrix[3][3] = 1f;

			// translation
			matrix[0][3] = -(right + left) / (right - left);
			// m31 = -(bottom + top) / (bottom - top);
			matrix[1][3] = -(top + bottom) / (top - bottom);
			matrix[2][3] = -(far + near) / (far - near);
		} else {
			matrix[0][0] = (2.0f * near) / (right - left);
			matrix[1][1] = (2.0f * near) / (top - bottom);
			matrix[3][2] = -1.0f;
			matrix[3][3] = -0.0f;

			// A
			matrix[0][2] = (right + left) / (right - left);

			// B
			matrix[1][2] = (top + bottom) / (top - bottom);

			// C
			matrix[2][2] = -(far + near) / (far - near);

			// D
			matrix[2][3] = -(2.0f * far * near) / (far - near);
		}
	}
}
