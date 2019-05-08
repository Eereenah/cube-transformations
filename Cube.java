package cg_assignment2;

public class Cube {

	Polygon pl[];
	double[][] world = new double[4][4];

	public Cube(Polygon pl[], double[][] world) {
		this.pl = pl;
		this.world = world;
	}

	public Polygon[] getPolygons() {
		return pl;
	}

	public void reset() {
		pl = null;
		Vertex a = new Vertex(-1, -1, 1, 1);
		Vertex b = new Vertex(-1, -1, -1, 1);
		Vertex c = new Vertex(1, -1, -1, 1);
		Vertex d = new Vertex(1, -1, 1, 1);
		Vertex aa = new Vertex(-1, 1, 1, 1);
		Vertex bb = new Vertex(-1, 1, -1, 1);
		Vertex cc = new Vertex(1, 1, -1, 1);
		Vertex dd = new Vertex(1, 1, 1, 1);

		Vertex[] front = {a, aa, dd, d};
		Vertex[] top = {aa, bb, cc, dd};
		Vertex[] back = {c, cc, bb, b};
		Vertex[] bottom = {b, a, d, c};
		Vertex[] left = {b, bb, aa, a};
		Vertex[] right = {d, dd, cc, c};

		Polygon ff = new Polygon(front);
		Polygon tf = new Polygon(top);
		Polygon baf = new Polygon(back);
		Polygon bof = new Polygon(bottom);
		Polygon lef = new Polygon(left);
		Polygon rf = new Polygon(right);

		Polygon[] faces = {ff, baf, tf, bof, lef, rf};
		this.pl = faces;
	}

	public Cube() {
		reset();
		double[][] world = {{ 1, 0, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 1, -5 }, { 0, 0, 0, 1 }};
		this.world = world;

	}
}
