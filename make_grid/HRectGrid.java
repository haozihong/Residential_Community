package make_grid;

import igeo.*;

public class HRectGrid {
	public int m,n;
	public double a;
	public HFace[][] grid;
	public HVec[][] vxs;
	
	public HRectGrid(int m, int n, double a) {
		this.m = m;
		this.n = n;
		this.a = a;
		init();
	}
	
	public HRectGrid(double la, double lb, double a) {
		this((int) (la/a), (int) (lb/a),a);
	}
	
	private void init() {
		grid = new HFace[m][n];
		vxs = new HVec[m+1][n+1];
		for (int i=0; i<m+1; i++) {
			for (int j=0; j<n+1; j++) {
				vxs[i][j] = new HVec(j*a, i*a);
			}
		}
		HEdge[][][] rects = new HEdge[m][n][];
		for (int i=0; i<m; i++) {
			for (int j=0; j<n; j++) {
				HEdge[] rect = new HEdge[4];
				rect[0] = new HEdge(vxs[i][j],vxs[i][j+1],null,null);
				rect[1] = new HEdge(vxs[i][j+1],vxs[i+1][j+1],null,null);
				rect[2] = new HEdge(vxs[i+1][j+1],vxs[i+1][j],null,null);
				rect[3] = new HEdge(vxs[i+1][j],vxs[i][j],null,null);
				rect[0].prev = rect[3];
				rect[0].next = rect[1];
				rect[1].prev = rect[0];
				rect[1].next = rect[2];
				rect[2].prev = rect[1];
				rect[2].next = rect[3];
				rect[3].prev = rect[2];
				rect[3].next = rect[0];
				rects[i][j] = rect;
				grid[i][j] = new HFace(rect[0]);
				for (HEdge edge : rect) {
					edge.face = grid[i][j];
				}
			}
		}
		for (int i=0; i<m; i++) {
			for (int j=0; j<n; j++) {
				if (i>0) rects[i][j][0].setTwin(rects[i-1][j][2]);
				if (j>0) rects[i][j][3].setTwin(rects[i][j-1][1]);
			}
		}
	}
	
	public HFace get(int x, int y) {
		return grid[x][y];
	}
}
