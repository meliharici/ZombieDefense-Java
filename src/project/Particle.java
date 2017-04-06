package project;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.Line;

public abstract class Particle extends Virtual_Object{
	int particle_damage = 0;
	static Random rgen = new Random();
	double x_velocity;
	double y_velocity;
	double velocity_factor = 0.7;
	int angle;
	double angle_radian;
	double gravityfactor = 0.0010;
	protected static final int general_SIZE = 2;
	
	public Particle(int x_center,int y_center){
		super(x_center, y_center,general_SIZE,general_SIZE);
		angle = 0;
	}
	public Particle(int x_center,int y_center,int angle){
		super(x_center, y_center,general_SIZE,general_SIZE);
		
		setX_velocity(1);
		setY_velocity(0);
		set_Angle();
		
		turn(angle);
	}
	
	public void turn(int i){
		angle += i;
		angle = angle%360;
		angle_radian = angle/57.2957795;
		
		double speedlength = (Math.sqrt(Math.pow(x_velocity, 2)+Math.pow(y_velocity, 2)));
		x_velocity = Math.cos(angle_radian)*speedlength;
		y_velocity = -Math.sin(angle_radian)*speedlength;
	}

	
	public void setX_velocity(double x_velocity) {
		
		this.x_velocity = x_velocity;
	}

	public void setY_velocity(double y_velocity) {
		this.y_velocity = y_velocity;
	}
	public void set_Angle(){
		double x_angle = 0;
		double y_angle = 0;
		double unit_x_velocity;
		unit_x_velocity = x_velocity/(Math.sqrt(Math.pow(x_velocity, 2)+Math.pow(y_velocity, 2)));
		
		x_angle = Math.acos(unit_x_velocity)*57.2957795;
		
		if(y_velocity>0){
			angle = (int) (360-x_angle);
		}
		else
			angle = (int) x_angle;
		
		angle = angle%360;
		angle_radian = angle/57.2957795;
	}
	public void move(int i){
		x_center += (x_velocity*i)*velocity_factor;
		y_velocity += gravityfactor;
		y_center += (y_velocity*i)*velocity_factor;
		
		set_Angle();
	}

		
	public boolean collidesWith(wall p){
		boolean collosion = false;
		double x_corner = x_center + (Math.cos(angle_radian));
		double y_corner = y_center - (Math.sin(angle_radian));
		if(p.get_Polygon().contains(x_corner, y_corner)){
			collosion = true;
			int polygon_angle = p.get_angle();
			int angle_between = 0;
			
			if(polygon_angle == 0){
				polygon_angle = 180;		
			}
			
			if(p.get_cornerpolygon1().contains(x_corner, y_corner)||p.get_cornerpolygon2().contains(x_corner, y_corner)){
				polygon_angle += 90;
				polygon_angle = polygon_angle%180;
				
				if(polygon_angle == 90){
					x_velocity = -x_velocity;			//  dikse yan kenara çarpan ters tarafa gidiyor
				}
				else if(polygon_angle == 180){
					y_velocity = -y_velocity;			// üstten veya alttan çarpma durumu alta veya yukarý gider
				}
				else{
					if(Math.abs(angle%180-polygon_angle)<=90){
						angle_between = angle%180-polygon_angle;		// calculates ccw or cw
					}
					else
						if(angle%180-polygon_angle>=0){
							angle_between = -(180 - (angle%180-polygon_angle));
						}
						else
							angle_between = 180 + (angle%180-polygon_angle);
						
					int absolute_angle_between = 90 - Math.abs(angle_between);
					
					if(angle_between > 0){						// ang X 2 for reflection angle
						turn(180+absolute_angle_between*2);		
					}
					else											// it's for flame and wall angle calculations 
						turn(180-absolute_angle_between*2);
				}
			}
			else{
				if(polygon_angle == 180)
					y_velocity = -y_velocity;
				else if(polygon_angle == 90)
					x_velocity = -x_velocity;
				else{
					if(Math.abs(angle%180-polygon_angle)<=90){
						angle_between = angle%180-polygon_angle;
					}
					else
						if(angle%180-polygon_angle>=0){
							angle_between = -(180 - (angle%180-polygon_angle));
						}
						else
							angle_between = 180 + (angle%180-polygon_angle);
						
					int absolute_angle_between = 90 - Math.abs(angle_between);
					
					if(angle_between > 0){
						turn(180+absolute_angle_between*2);
					}
					else
						turn(180-absolute_angle_between*2);
				}
				
			}
			
		}
		return collosion;
	}
}
class Flame extends Particle{
	private static int move_Before_Death = 250;
	private int move_Before_Death_actual;
	int move_Count;
	workSplitter splitter;
	int index;
	int SIZE;
	

	public Flame(int x_center, int y_center,workSplitter splitter) {
		super(x_center, y_center);
		this.splitter = splitter;
		
		velocity_factor = super.velocity_factor*1.1;
		gravityfactor = 0;
		
		move_Before_Death_actual = move_Before_Death + rgen.nextInt(100);
		
		SIZE = general_SIZE+1;
		
		particle_damage = 2;
		
	}
	
	public void collidesWith(Zombie z){
		if(!z.isDying){
			if(z.bodyarea.contains(x_center,y_center)){
				z.attackbody(particle_damage);			
				
				Smoke smoke = new Smoke((int)x_center,(int)y_center,splitter);
				smoke.x_velocity = x_velocity/4;
				smoke.y_velocity = y_velocity/2.5;
				splitter.add_particle(smoke);					// flame and smoke are workSlplitter's works
				splitter.particles[index] = null;
			}
			else if(z.headarea.contains(x_center,y_center)){
				z.attackbody(particle_damage);
				
				Smoke smoke = new Smoke((int)x_center,(int)y_center,splitter);
				smoke.x_velocity = x_velocity/4;
				smoke.y_velocity = y_velocity/2.5;
				splitter.add_particle(smoke);
				splitter.particles[index] = null;
			}
		}
	}
	
	public void paint(Graphics g){
		g.setColor(new Color(0,(int)(250-move_Count/2),(int)(move_Count/2),100));
		g.fillOval((int)(x_center-SIZE), (int)(y_center-SIZE),2*SIZE,2*SIZE);
	}
	public boolean collidesWith(wall p){
		
		boolean collosion = super.collidesWith(p);
		
//		if(collosion){
//			particles.add(new Smoke((int)(x_center+x_velocity*rgen.nextInt(30)),(int)y_center,particles));
//			
//		}
		return collosion;
	}
	
	public void move(int i){
		
		super.move(i);
		
		move_Count ++;
		
		if(move_Count%75==0){
			SIZE ++;
		}
		
		if(move_Count>move_Before_Death_actual){
			Smoke smoke = new Smoke((int)x_center,(int)y_center,splitter);
			smoke.x_velocity = x_velocity/4;
			smoke.y_velocity = y_velocity/2.5;
			splitter.add_particle(smoke);
			splitter.particles[index] = null;
		}
	}
	
}
class Smoke extends Particle{
	private static int move_Before_Death = 600;
	workSplitter splitter;
	int index;
	private int SIZE;
	private int move_Count;
	
	public Smoke(int x_center,int y_center,workSplitter splitter){
		super(x_center, y_center);
		
		velocity_factor = 1;
		gravityfactor = -0.0002;
		
		y_velocity = 0;
		
		this.splitter = splitter;
	}
	
	public void paint(Graphics g){
		g.setColor(Color.black);
		g.fillOval((int)(x_center-SIZE), (int)(y_center-SIZE),2*SIZE,2*SIZE);
	}
	
	public void move(int i){
		
		SIZE = general_SIZE+move_Count/100;

		super.move(i);

		move_Count ++;

		if(move_Count>move_Before_Death){
			splitter.particles[index] = null;
		}
	}
	public boolean collidesWith(wall p){
		return false;
	}
	
}
class Ball extends Particle{
	private static int move_Before_Death = 750;
	private int move_Count;
	ArrayList<Ball> bullets;
	int SIZE = general_SIZE;

	public Ball(int x_center, int y_center,ArrayList<Ball> bullets) {
		super(x_center, y_center);
		this.bullets = bullets;
		
		velocity_factor = 2;
		gravityfactor = 0.0002;
		
		particle_damage = 10;
		
	}
	
	public void paint(Graphics g){
		g.setColor(new Color(0,250,0));
		g.fillOval((int)(x_center-SIZE), (int)(y_center-SIZE),2*SIZE,2*SIZE);

	}
	public void collidesWith(Zombie z){
		if(!z.isDying){
			if(z.headarea.contains(x_center,y_center)){
				z.attackhead(particle_damage);
				bullets.remove(this);
				for(int i=0;i<3;i++){
					Blood blood = new Blood((int)x_center,(int)y_center);
					blood.setX_velocity(x_velocity);
					blood.setY_velocity(y_velocity);
					blood.set_Angle();
					
					int turn_angle = rgen.nextInt(60) - 30;
					blood.turn(turn_angle);
					
					Game_data.bloods.add(blood);
				}
			}
			else if(z.bodyarea.contains(x_center,y_center)){
				z.attackbody(particle_damage);
				bullets.remove(this);
				
				for(int i=0;i<3;i++){
					Blood blood = new Blood((int)x_center,(int)y_center);
					blood.setX_velocity(x_velocity);
					blood.setY_velocity(y_velocity);
					blood.set_Angle();
					
					int turn_angle = rgen.nextInt(60) - 30;
					blood.turn(turn_angle);
					
					Game_data.bloods.add(blood);
				}
			}
		}
	}
	
	public void move(int i){
		
		super.move(i);
		
		move_Count ++;
		
		if(move_Count>move_Before_Death){
			bullets.remove(this);
		}
	}
	
}
class Barricade_particle extends Ball{
	public Barricade_particle(int x_center,int y_center,ArrayList<Ball> bullets){
		super(x_center,y_center,bullets);
		gravityfactor *= 5;
		velocity_factor /= 3;
		SIZE --;
	}
	public void collidesWith(Zombie z){
		
	}
}
class Arrow extends Particle{
	Polygon polygon;
	Zombie last_zombie_hit;
	private static final int SIZE = 15;
	int body_x_center,body_y_center;
	boolean isStuck;
	
	public Arrow(int x_center,int y_center){
		super(x_center, y_center);
		gravityfactor = 0.0005;
		velocity_factor *= 2;
		
		particle_damage = 65;
	}
	public Polygon get_Polygon(){
		return polygon;
	}
	
	public void setX_velocity(double x_velocity){
		super.setX_velocity(x_velocity);
		body_x_center = (int) (x_center-x_velocity*SIZE);
	}
	public void setY_velocity(double y_velocity){
		super.setY_velocity(y_velocity);
		body_y_center = (int) (y_center-y_velocity*SIZE);
	}
	
	public void collidesWith(Zombie z){
		if(!isStuck){											// last_zombie_hit --> öldürülen son zombi
			if(z!=null&&z!=last_zombie_hit){						
				if(z.headarea.contains(x_center,y_center)){			
					z.attackhead(particle_damage);
					last_zombie_hit = z;
					for(int i=0;i<3;i++){
						Blood blood = new Blood((int)x_center,(int)y_center);
						blood.setX_velocity(x_velocity);
						blood.setY_velocity(y_velocity);
						blood.set_Angle();
						
						int turn_angle = rgen.nextInt(60) - 30;
						blood.turn(turn_angle);
						
						Game_data.bloods.add(blood);
					}
				}
				else if(z.bodyarea.contains(x_center,y_center)){
					z.attackbody(particle_damage);
					last_zombie_hit = z;
					for(int i=0;i<3;i++){
						Blood blood = new Blood((int)x_center,(int)y_center);
						blood.setX_velocity(x_velocity);
						blood.setY_velocity(y_velocity);
						blood.set_Angle();
						
						int turn_angle = rgen.nextInt(60) - 30;
						blood.turn(turn_angle);
						
						Game_data.bloods.add(blood);
					}
				}
			}
		}
	}
	public void move(int i){
		if(!isStuck){
			super.move(i);
			
			body_x_center = (int) (x_center-x_velocity*SIZE);			
			body_y_center = (int) (y_center-y_velocity*SIZE);
		}
	}
	
	public boolean collidesWith(wall p){
		boolean collosion = false;
		if(!isStuck){
			if(p.get_Polygon().contains(x_center, y_center)){
				collosion = true;
				body_x_center = (int) (x_center-x_velocity*SIZE);			
				body_y_center = (int) (y_center-y_velocity*SIZE);
				
				x_velocity = 0;
				y_velocity = 0;
				isStuck = true;
				
			    int[] x_array = new int[4];
				int[] y_array = new int[4];
				
				x_array[0] = body_x_center;			// oklarýn altýnda görünmez dörtgen var, sýrf oklar saplanýnca oluþuyorlar..
				x_array[1] = (int) x_center;		// body_x_center ==> okun göbek noktasý
				x_array[2] = (int) x_center;		// okun tam altýna çiziyor bu þekilde sen bu polygonlarýn üstünde kalabiliyosun
				x_array[3] = body_x_center;
				
				y_array[0] = body_y_center;
				y_array[1] = (int) y_center;
				y_array[2] = (int) (y_center+4);
				y_array[3] = body_y_center+4;
				
				polygon = new Polygon(x_array,y_array,4);
			}
		}
		return collosion;
	}
	
	public void paint(Graphics g){
		g.setColor(Color.green);
		g.drawLine((int)(x_center+x_velocity*3), (int)(y_center+y_velocity*3), body_x_center, body_y_center);
		
	}
}

class Blood extends Particle{
	private int move_until_stuck = 5;
	int move_Count;
	boolean isStuck;
	boolean isHit;

	public Blood(int x_center, int y_center) {
		super(x_center, y_center);
		gravityfactor = 0.001;
		
		
		move_until_stuck += rgen.nextInt(13);
	}

	public void paint(Graphics g) {
		g.setColor(Color.RED);
		g.fillOval((int)x_center-x_size, (int)y_center-y_size, 2*x_size, 2*y_size);
		
	}
	
	public void move(int i){
		if(isHit){
			if(move_Count<move_until_stuck){
				super.move(i);
				move_Count++;
			}
			else
				isStuck = true;
		}
		else
			super.move(i);
	}
	public boolean collidesWith(wall p){
		boolean collosion = false;
		if(p.get_Polygon().contains(x_center, y_center)){
			collosion = true;
			isHit = true;
		}
		
		return collosion;
	}
	
}

