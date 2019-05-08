package cg_assignment2;

public class Camera {
	
	double fov;
	int f, n;
	double[][] view = {{1, 0, 0, 0},{0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
	double aspect;
	
	public Camera(double fov, int f, int n, double aspect){
		this.fov = fov;
		this.f = f;
		this.n = n;
		this.aspect = aspect;
	}
}
