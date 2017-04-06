package project;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.io.Serializable;
import java.util.ArrayList;

public class wall extends Virtual_Object implements Serializable {
	private double angle;
	private int[] x_array;
	private int[] y_array;
	protected Polygon polygon;
	private Polygon cornerpolygon1;
	private Polygon cornerpolygon2;
	
	public Polygon get_Polygon(){
		return polygon;
	}
	public Polygon get_cornerpolygon1(){
		return cornerpolygon1;
	}
	public Polygon get_cornerpolygon2(){
		return cornerpolygon2;
	}

	public wall(double x_center, double y_center, int x_size, int y_size) {
		super(x_center, y_center, x_size, y_size);
		this.angle = 0;
	}
	
	public void turn_wall(int angle){
		this.angle += ((0.0174532925)*(angle));
		
	}
	public int get_angle(){
		int normal_angle = (int) (angle/(0.0174532925));
		if(normal_angle<0)
			normal_angle += 180;
		normal_angle = normal_angle %180;
		return normal_angle;
	}
	
	public void setPolygons(){
		int[] x_array = find_x_array();
		int[] y_array = find_y_array();
		polygon = new Polygon(x_array,y_array,4);
		
		int[] cornerpolygon1_xarray = new int[3];
		int[] cornerpolygon1_yarray = new int[3];			// wall �n noktalar�n� �iziyor
		int[] cornerpolygon2_xarray = new int[3];			// ��genin �� tane kenar noktas� vard�r. o y�zden polygonun 3 noktas� var
		int[] cornerpolygon2_yarray = new int[3];
		
		cornerpolygon1_xarray[0] = x_array[0];
		cornerpolygon1_xarray[1] = (int) x_center;
		cornerpolygon1_xarray[2] = x_array[3];
		
		cornerpolygon1_yarray[0] = y_array[0];
		cornerpolygon1_yarray[1] = (int) y_center;			// duvar�n i�ine �izilen cornerpolygonun kenar noktalr� bunlar..
		cornerpolygon1_yarray[2] = y_array[3];				// corner polygon ��gen..
		
		cornerpolygon2_xarray[0] = (int) x_center;
		cornerpolygon2_xarray[1] = x_array[1];
		cornerpolygon2_xarray[2] = x_array[2];
		
		cornerpolygon2_yarray[0] = (int) y_center;
		cornerpolygon2_yarray[1] = y_array[1];
		cornerpolygon2_yarray[2] = y_array[2];
		
		cornerpolygon1 = new Polygon(cornerpolygon1_xarray,cornerpolygon1_yarray,3);
		cornerpolygon2 = new Polygon(cornerpolygon2_xarray,cornerpolygon2_yarray,3);
	}
	
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.BLUE);
		setPolygons();
		g2.fillPolygon(polygon);
		
		
	}
	public int[] find_x_array(){
		x_array = new int[4];
									//  / d�nd�rme olaylar�nda ana dikt�rtgenin kenarlar�n� hesapl�yor...
		x_array[0] = (int) ((-x_size*Math.cos(angle))-(y_size*Math.sin(angle))+x_center);
		x_array[1] = (int) ((x_size*Math.cos(angle))-(y_size*Math.sin(angle))+x_center);
		x_array[2] = (int) ((x_size*Math.cos(angle))+(y_size*Math.sin(angle))+x_center);
		x_array[3] = (int) ((-x_size*Math.cos(angle))+(y_size*Math.sin(angle))+x_center);
				
		return x_array;
	}
	public int[] find_y_array(){
		y_array = new int[4];		// a��s�na ba�l� olarak ana wall'�n polygonunun kenar noktas�n� hesapl�yor.
		
		y_array[0] = (int) (-y_size*Math.cos(angle)+x_size*Math.sin(angle)+y_center);
		y_array[1] = (int) (-y_size*Math.cos(angle)-x_size*Math.sin(angle)+y_center);
		y_array[2] = (int) (y_size*Math.cos(angle)-x_size*Math.sin(angle)+y_center);
		y_array[3] = (int) (y_size*Math.cos(angle)+x_size*Math.sin(angle)+y_center);
		
		return y_array;
	}

}
class Barricade extends wall {
	int life = Game_data.Barricade_health;
	boolean isActive = true;

	public Barricade(double x_center, double y_center, int x_size, int y_size) {
		super(x_center, y_center, x_size, y_size);
			
	}
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		if(isActive){
			g2.setColor(new Color(50,150,30,230));
		}
		else
			g2.setColor(new Color(50,150,30,50));
		setPolygons();
		g2.fillPolygon(polygon);
		
	}
	public void turn_wall(int i){
		
	}
	

	public void attack(int x) {
		if(life-x<0){
			isActive = false;
			for(int i = 0;i<360;i++){		
				Barricade_particle part = new Barricade_particle((int)x_center,(int)y_center,Game_data.bullets);
				part.setX_velocity(1);
				part.setY_velocity(0);
				part.turn(Game_data.rgen.nextInt(360));
				
				Game_data.bullets.add(part);
			}
		}
		else
			life = life - x;
	}

}
