package cg_assignment2;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class Transformations {

	public static BufferedImage gradientSetRaster(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();

		WritableRaster raster = img.getRaster();
		int[] pixel = { 0, 0, 0 };

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				raster.setPixel(x, y, pixel);
			}
		}
		return img;
	}

	public static void main(String... args) {
		Frame w = new Frame("Raster"); // window
		final int imageWidth = 600;
		final int imageHeight = 600;

		w.setSize(imageWidth, imageHeight);
		w.setLocation(100, 100);
		w.setVisible(true);

		Graphics g = w.getGraphics();

		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		gradientSetRaster(img);
		g.drawImage(img, 0, 0, (img1, infoflags, x, y, width, height) -> true);
		Cube cube = new Cube();

		double fov = Math.toRadians(90);
		int n = 1;
		int f = 9;
		double aspect = imageWidth / imageHeight;
		Camera cam = new Camera(fov, n, f, aspect);

		double[][] proj = { { (1 / Math.tan(fov / 2)) / aspect, 0, 0, 0 }, { 0, 1 / Math.tan(fov / 2), 0, 0 },
				{ 0, 0, -(f + n) / (f - n), -2 * f * n / (f - n) }, { 0, 0, -1, 0 } };
		// multiply cube vertices with the world matrix, 0 and 1 stand for front
		// and back faces
		// multiply by the view matrix from camera
		// multiply by the projection matrix
		// normalize the coordinates

		for (Vertex v : cube.pl[0].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(proj, v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Vertex v : cube.pl[1].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(proj, v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}

		for (Polygon poly : cube.pl) {
			double[] x = new double[poly.vrt.length];
			double[] y = new double[poly.vrt.length];
			int i = 0;
			for (Vertex ver : poly.vrt) {
				x[i] = ver.x * imageWidth / 2 + imageWidth / 2;
				y[i] = -ver.y * imageHeight / 2 + imageHeight / 2;
				i++;
			}
			draw_polygon(img, g, x, y);
		}

		w.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_T) {
					gradientSetRaster(img);
					rotateY(img, g, 1.0, cube, cam, proj, imageWidth, imageHeight);
				} else if (e.getKeyCode() == KeyEvent.VK_R) {
					gradientSetRaster(img);
					rotateY(img, g, -1.0, cube, cam, proj, imageWidth, imageHeight);
				} else if (e.getKeyCode() == KeyEvent.VK_E) {
					gradientSetRaster(img);
					rotateX(img, g, 1.0, cube, cam, proj, imageWidth, imageHeight);
				} else if (e.getKeyCode() == KeyEvent.VK_W) {
					gradientSetRaster(img);
					rotateX(img, g, -1.0, cube, cam, proj, imageWidth, imageHeight);
				} else if (e.getKeyCode() == KeyEvent.VK_Y) {
					gradientSetRaster(img);
					rotateZ(img, g, 1.0, cube, cam, proj, imageWidth, imageHeight);
				} else if (e.getKeyCode() == KeyEvent.VK_U) {
					gradientSetRaster(img);
					rotateZ(img, g, -1.0, cube, cam, proj, imageWidth, imageHeight);
				} else if (e.getKeyCode() == KeyEvent.VK_S) {
					gradientSetRaster(img);
					translateX(img, g, 0.05, cube, cam, proj, imageWidth, imageHeight);
				} else if (e.getKeyCode() == KeyEvent.VK_A) {
					gradientSetRaster(img);
					translateX(img, g, -0.05, cube, cam, proj, imageWidth, imageHeight);
				} else if (e.getKeyCode() == KeyEvent.VK_F) {
					gradientSetRaster(img);
					translateY(img, g, 0.05, cube, cam, proj, imageWidth, imageHeight);
				} else if (e.getKeyCode() == KeyEvent.VK_D) {
					gradientSetRaster(img);
					translateY(img, g, -0.05, cube, cam, proj, imageWidth, imageHeight);
				} else if (e.getKeyCode() == KeyEvent.VK_G) {
					gradientSetRaster(img);
					translateZ(img, g, 0.05, cube, cam, proj, imageWidth, imageHeight);
				} else if (e.getKeyCode() == KeyEvent.VK_H) {
					gradientSetRaster(img);
					translateZ(img, g, -0.05, cube, cam, proj, imageWidth, imageHeight);
				} else if (e.getKeyCode() == KeyEvent.VK_X) {
					gradientSetRaster(img);
					changeFOV(img, g, 0.5, cube, cam, proj, imageWidth, imageHeight);
				} else if (e.getKeyCode() == KeyEvent.VK_Z) {
					gradientSetRaster(img);
					changeFOV(img, g, -0.5, cube, cam, proj, imageWidth, imageHeight);
				}
				g.drawImage(img, 0, 0, (img1, infoflags, x, y, width, height) -> true);
			}
		});

		w.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				w.dispose();
				g.dispose();
				System.exit(0);
			}
		});
	}

	public static double[][] calc_proj(Camera cam) {
		double[][] proj = { { (1 / Math.tan(cam.fov / 2)) / cam.aspect, 0, 0, 0 },
				{ 0, 1 / Math.tan(cam.fov / 2), 0, 0 },
				{ 0, 0, -(cam.f + cam.n) / (cam.f - cam.n), -2 * cam.f * cam.n / (cam.f - cam.n) }, { 0, 0, -1, 0 } };
		return proj;
	}

	public static void draw_line(BufferedImage img, Graphics g, int x0, int y0, int x1, int y1) {
		int d = 0;
		int[] pixel = { 255, 255, 255 };
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		// use for determining the sign of the slope
		int ix = x0 < x1 ? 1 : -1;
		int iy = y0 < y1 ? 1 : -1;
		int x = x0;
		int y = y0;

		// when slope < 1
		if (dx >= dy) {
			while (true) {
				img.getRaster().setPixel(x, y, pixel);
				if (x == x1)
					break;
				x += ix;
				d += dy * 2;
				if (d > dx) {
					y += iy;
					d -= dx * 2;
				}
			}
		}
		// when slope > 1
		else {
			while (true) {
				img.getRaster().setPixel(x, y, pixel);
				if (y == y1)
					break;
				y += iy;
				d += dx * 2;
				if (d > dy) {
					x += ix;
					d -= dy * 2;
				}
			}
		}
		g.drawImage(img, 0, 0, (img1, infoflags, a, b, width, height) -> true);
	}

	public static void draw_polygon(BufferedImage img, Graphics g, double[] pointsX, double[] pointsY) {
		int size = pointsX.length;
		for (int j = 0; j < size - 1; j++) {
			draw_line(img, g, (int) pointsX[j], (int) pointsY[j], (int) pointsX[j + 1], (int) pointsY[j + 1]);
		}
		draw_line(img, g, (int) pointsX[0], (int) pointsY[0], (int) pointsX[size - 1], (int) pointsY[size - 1]);
	}

	public static double[][] mmult(double[][] m1, double[][] m2) {
		double[][] temp = new double[4][4];
		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m2.length; j++) {
				for (int k = 0; k < m1.length; k++) {
					temp[i][j] += m1[i][k] * m2[k][j];
				}
			}
		}
		return temp;
	}

	public static void vmult(double[][] m1, Vertex v) {
		double[] v1 = { v.x, v.y, v.z, v.w };
		double[] temp = { 0, 0, 0, 0 };
		for (int i = 0; i < m1.length; i++) {
			for (int j = 0; j < m1.length; j++) {
				temp[i] += m1[i][j] * v1[j];
			}
		}
		v.changePoint(temp[0], temp[1], temp[2], temp[3]);
	}

	public static void rotateY(BufferedImage img, Graphics g, double theta, Cube cube, Camera cam, double[][] proj,
			int imageWidth, int imageHeight) {
		double rad = Math.PI * theta / 180;
		cube.reset();
		double[][] rotmat = { { Math.cos(rad), 0, Math.sin(rad), 0 }, { 0, 1, 0, 0 },
				{ -Math.sin(rad), 0, Math.cos(rad), 0 }, { 0, 0, 0, 1 } };
		cube.world = mmult(cube.world, rotmat);
		for (Vertex v : cube.pl[0].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(calc_proj(cam), v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Vertex v : cube.pl[1].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(calc_proj(cam), v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Polygon poly : cube.pl) {
			double[] x = new double[poly.vrt.length];
			double[] y = new double[poly.vrt.length];
			int i = 0;
			for (Vertex ver : poly.vrt) {
				x[i] = ver.x * imageWidth / 2 + imageWidth / 2;
				y[i] = -ver.y * imageHeight / 2 + imageHeight / 2;
				i++;
			}
			draw_polygon(img, g, x, y);
		}
	}

	public static void rotateX(BufferedImage img, Graphics g, double theta, Cube cube, Camera cam, double[][] proj,
			int imageWidth, int imageHeight) {
		double rad = Math.PI * theta / 180;
		cube.reset();
		double[][] rotmat = { { 1, 0, 0, 0 }, { 0, Math.cos(rad), -Math.sin(rad), 0 },
				{ 0, Math.sin(rad), Math.cos(rad), 0 }, { 0, 0, 0, 1 } };
		cube.world = mmult(cube.world, rotmat);
		for (Vertex v : cube.pl[0].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(calc_proj(cam), v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Vertex v : cube.pl[1].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(calc_proj(cam), v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Polygon poly : cube.pl) {
			double[] x = new double[poly.vrt.length];
			double[] y = new double[poly.vrt.length];
			int i = 0;
			for (Vertex ver : poly.vrt) {
				x[i] = ver.x * imageWidth / 2 + imageWidth / 2;
				y[i] = -ver.y * imageHeight / 2 + imageHeight / 2;
				i++;
			}
			draw_polygon(img, g, x, y);
		}
	}

	public static void rotateZ(BufferedImage img, Graphics g, double theta, Cube cube, Camera cam, double[][] proj,
			int imageWidth, int imageHeight) {
		double rad = Math.PI * theta / 180;
		cube.reset();
		double[][] rotmat = { { Math.cos(rad), -Math.sin(rad), 0, 0 }, { Math.sin(rad), Math.cos(rad), 0, 0 },
				{ 0, 0, 1, 0 }, { 0, 0, 0, 1 } };
		cube.world = mmult(cube.world, rotmat);
		for (Vertex v : cube.pl[0].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(calc_proj(cam), v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Vertex v : cube.pl[1].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(calc_proj(cam), v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Polygon poly : cube.pl) {
			double[] x = new double[poly.vrt.length];
			double[] y = new double[poly.vrt.length];
			int i = 0;
			for (Vertex ver : poly.vrt) {
				x[i] = ver.x * imageWidth / 2 + imageWidth / 2;
				y[i] = -ver.y * imageHeight / 2 + imageHeight / 2;
				i++;
			}
			draw_polygon(img, g, x, y);
		}
	}

	public static void translateX(BufferedImage img, Graphics g, double tx, Cube cube, Camera cam, double[][] proj,
			int imageWidth, int imageHeight) {
		cube.reset();
		double[][] transmat = { { 1, 0, 0, tx }, { 0, 1, 0, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 1 } };
		cam.view = mmult(cam.view, transmat);
		for (Vertex v : cube.pl[0].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(calc_proj(cam), v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Vertex v : cube.pl[1].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(calc_proj(cam), v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Polygon poly : cube.pl) {
			double[] x = new double[poly.vrt.length];
			double[] y = new double[poly.vrt.length];
			int i = 0;
			for (Vertex ver : poly.vrt) {
				x[i] = ver.x * imageWidth / 2 + imageWidth / 2;
				y[i] = -ver.y * imageHeight / 2 + imageHeight / 2;
				i++;
			}
			draw_polygon(img, g, x, y);
		}
	}

	public static void translateY(BufferedImage img, Graphics g, double ty, Cube cube, Camera cam, double[][] proj,
			int imageWidth, int imageHeight) {
		cube.reset();
		double[][] transmat = { { 1, 0, 0, 0 }, { 0, 1, 0, ty }, { 0, 0, 1, 0 }, { 0, 0, 0, 1 } };
		cam.view = mmult(cam.view, transmat);
		for (Vertex v : cube.pl[0].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(calc_proj(cam), v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Vertex v : cube.pl[1].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(calc_proj(cam), v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Polygon poly : cube.pl) {
			double[] x = new double[poly.vrt.length];
			double[] y = new double[poly.vrt.length];
			int i = 0;
			for (Vertex ver : poly.vrt) {
				x[i] = ver.x * imageWidth / 2 + imageWidth / 2;
				y[i] = -ver.y * imageHeight / 2 + imageHeight / 2;
				i++;
			}
			draw_polygon(img, g, x, y);
		}
	}

	public static void translateZ(BufferedImage img, Graphics g, double tz, Cube cube, Camera cam, double[][] proj,
			int imageWidth, int imageHeight) {
		cube.reset();
		double[][] transmat = { { 1, 0, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 1, tz }, { 0, 0, 0, 1 } };
		cam.view = mmult(cam.view, transmat);
		for (Vertex v : cube.pl[0].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(calc_proj(cam), v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Vertex v : cube.pl[1].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(calc_proj(cam), v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Polygon poly : cube.pl) {
			double[] x = new double[poly.vrt.length];
			double[] y = new double[poly.vrt.length];
			int i = 0;
			for (Vertex ver : poly.vrt) {
				x[i] = ver.x * imageWidth / 2 + imageWidth / 2;
				y[i] = -ver.y * imageHeight / 2 + imageHeight / 2;
				i++;
			}
			draw_polygon(img, g, x, y);
		}
	}

	public static void changeFOV(BufferedImage img, Graphics g, double angle, Cube cube, Camera cam, double[][] proj,
			int imageWidth, int imageHeight) {
		cube.reset();
		double dfov = Math.toDegrees(cam.fov) + angle;
		cam.fov = Math.toRadians(dfov);
		double[][] new_proj = { { (1 / Math.tan(cam.fov / 2)) / (cam.aspect), 0, 0, 0 },
				{ 0, 1 / Math.tan(cam.fov / 2), 0, 0 },
				{ 0, 0, -(cam.f + cam.n) / (cam.f - cam.n), -2 * cam.f * cam.n / (cam.f - cam.n) }, { 0, 0, -1, 0 } };
		for (Vertex v : cube.pl[0].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(new_proj, v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Vertex v : cube.pl[1].vrt) {
			vmult(cube.world, v);
			vmult(cam.view, v);
			vmult(new_proj, v);
			double[] old = { v.x, v.y, v.z, v.w };
			v.changePoint(old[0] / v.w, old[1] / v.w, old[2] / v.w, 1.0);
		}
		for (Polygon poly : cube.pl) {
			double[] x = new double[poly.vrt.length];
			double[] y = new double[poly.vrt.length];
			int i = 0;
			for (Vertex ver : poly.vrt) {
				x[i] = ver.x * imageWidth / 2 + imageWidth / 2;
				y[i] = -ver.y * imageHeight / 2 + imageHeight / 2;
				i++;
			}
			draw_polygon(img, g, x, y);
		}
	}
}