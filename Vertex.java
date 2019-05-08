package cg_assignment2;

public class Vertex {

	public double x;
	public double y;
	public double z;
	public double w;
	
	public Vertex(double X, double Y, double Z, double W){
		this.x = X;
		this.y = Y;
		this.z = Z;
		this.w = W;
	}
		
	public Vertex(Vertex vrtx){
		x = vrtx.x;
		y = vrtx.y;
		z = vrtx.z;
		w = vrtx.w;
	}
	
	public void changePoint(double X, double Y, double Z, double W){
		x = X;
		y = Y;
		z = Z;
		w = W;
	}
}
