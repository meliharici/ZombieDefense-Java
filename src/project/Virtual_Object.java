package project;

import java.awt.Graphics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;

import javax.swing.JPanel;

/* Base of all the Virtual_Virtual_Objects that have physical interactions. */
public abstract class Virtual_Object implements Serializable{
	double x_center;
	double y_center;
	int x_size;
	int y_size;
	
	public Virtual_Object(double x_center, double y_center, int x_size, int y_size) {
		this.x_center = x_center;
		this.y_center = y_center;
		this.x_size = x_size;
		this.y_size = y_size;
	}

	public abstract void paint(Graphics g);
}
