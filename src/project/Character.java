package project;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public abstract class Character extends Virtual_Object{
	double x_velocity = 0;
	double y_velocity = 0;
	double jump_velocity = 0;
	boolean isOnAir = true;
	boolean canJump;
	double velocity_factor = 0.7;
	double gravity_factor = 0.007;
	public static final int SIZE = 20;
	Polygon char_polygon;
	
	int doWhat;
	static int moveRight = 1;
	static int moveLeft = 2;
	static int dontMove = 0;

	boolean wallisdown = false;
	wall wallbelow;
	
	public Character(int xlocation,int ylocation){
		super(xlocation,ylocation,SIZE-8,SIZE);
		
	}

	
	public void initializedirection(){
		if(wallisdown){
			if(doWhat != dontMove){
				double angle = wallbelow.get_angle();
				double radian_angle = angle / 57.2957795;
				if(doWhat == moveRight){
					x_velocity = Math.abs(Math.cos(radian_angle));
					if(y_velocity > -1){
						if(angle<90){
							y_velocity = -((Math.abs(Math.sin(radian_angle)))+0.01);
						}
						else{
							y_velocity = Math.abs(Math.sin(radian_angle))+0.01;
						}
					}
				}
				if(doWhat == moveLeft){
					x_velocity = -Math.abs(Math.cos(radian_angle));
					if(y_velocity > -1){
						if(angle<90){
							y_velocity = ((Math.abs(Math.sin(radian_angle)))+0.01);
						}
						else{
							y_velocity = -(Math.abs(Math.sin(radian_angle))+0.01);
						}
					}

				}
			}
			else
				x_velocity = 0;
		}
		else{
			if(doWhat == moveRight)
				x_velocity = 1;
			if(doWhat == moveLeft)
				x_velocity = -1;
		}
		if(doWhat == Character.dontMove){
			x_velocity = 0;
		}


	}
	
	public void move(){
		if(isOnAir){
			y_velocity += gravity_factor;
		}
		initializedirection();

		x_center += x_velocity*velocity_factor;
		y_center += y_velocity*velocity_factor ;
		
	}
	
	public void jump(){
		
		if(canJump){
			y_velocity -= 1.2;
			canJump = false;
		}
			
	}
	
	public void collidesWith(Arrow arrow){
		if(arrow.isStuck){
			int detectionfactor = SIZE + 1;
			
			if(arrow.get_Polygon().contains((int)x_center, (int)y_center+detectionfactor)){
				if(arrow.angle%180<30||180-arrow.angle%180<30){
					isOnAir = false;
					jump_velocity = 0;
					if(y_velocity>0)
						canJump = true;
					if(y_velocity > 0){
						y_velocity = 0;
					}
				}
			}
		}
	}
	
	public boolean collidesWith(ArrayList<wall> walls,ArrayList<Arrow> arrows){
		boolean collosion = false;
		boolean inside = false;
		boolean wallisup = false;
		boolean wallisright = false;
		boolean wallisleft = false;
		isOnAir = true;
		wallisdown = false;
		int detectionfactor = SIZE;
		
		for(int j = 0;j<arrows.size();j++){
			try{
				collidesWith(arrows.get(j));
			}
			catch(Exception ex){
				
			}
		}

		for(int i = 0;i<walls.size();i++){
			wall p = walls.get(i);
			
			if(p.get_Polygon().contains((int)x_center, (int)y_center)){
				inside = true;
			}
			if(p.get_Polygon().contains((int)x_center+x_size, (int)y_center)){
				wallisright = true;
				collosion = true;
			}
			if(p.get_Polygon().contains((int)x_center-x_size, (int)y_center)){
				wallisleft = true;
				collosion = true;
			}
			if(p.get_Polygon().contains((int)x_center, (int)y_center+detectionfactor)){
				wallisdown = true;
				isOnAir = false;
				if(y_velocity>0){
					canJump = true;
				}
				collosion = true;
				wallbelow = p;
			}
			if(p.get_Polygon().contains((int)x_center, (int)y_center-detectionfactor)){
				wallisup = true;
				collosion = true;
			}
			if(inside){
				y_center -= 1;
			}
			if(wallisright){
				if(doWhat == Character.moveRight){
					doWhat = Character.dontMove;
				}
			}
			if(wallisleft){
				if(doWhat == Character.moveLeft){
					doWhat = Character.dontMove;
				}
			}
			if(wallisup){
				if(y_velocity < 0){
					y_velocity = 0;
				}
			}
			if(wallisdown){
				if(y_velocity > 0){
					y_velocity = 0;
				}
			}	
		}
		return collosion;
	}
	
}
class Player extends Character{
	int health = 250;
	
	int directionfaced;
	static final int facingRight = 1;
	static final int facingLeft = 2;
	
	ArrayList<Image> Right_animations = new ArrayList<Image>();
	Image Right_attack;
	Image Right_jump;
	
	ArrayList<Image> Left_animations = new ArrayList<Image>();
	Image Left_attack;
	Image Left_jump;
	
	Image CurrentImage;
	
	boolean isAttacking;
	long attacktime;
	
	int money = 0;
	boolean hasFlameThrower = false;
	int flameThrowerammo = 0;
	
	boolean hasBow = false;
	int arrowCount = 0;
	
	boolean isInvulnearible = false;
	
	public Player(){
		super(150,150);
		
		setAnimations();
	}
	
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		
		Image i = getCurrentImage();
		if(i != null)
			g2.drawImage(i, (int)x_center-i.getWidth(null)/2, (int)y_center-i.getHeight(null)/2, null);
	}
	
	public void setAnimations(){
		for(int i = 0;i<8;i++){
			try {
				Right_animations.add(ImageIO.read(new File("res/graphics/Character/RCharacter-Move"+(i+1)+".png")));
				Left_animations.add(ImageIO.read(new File("res/graphics/Character/LCharacter-Move"+(i+1)+".png")));
			} catch (IOException e) {
				System.out.println(i);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			Right_attack = ImageIO.read(new File("res/graphics/Character/RCharacter-Attack.png"));
			Left_attack = ImageIO.read(new File("res/graphics/Character/LCharacter-Attack.png"));
			
			Right_jump = ImageIO.read(new File("res/graphics/Character/LCharacter-Jump.png"));
			Left_jump = ImageIO.read(new File("res/graphics/Character/RCharacter-Jump.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void attack(int i){
		if(isInvulnearible == false){
			health -= i;
			
			if(health<0){
				JOptionPane.showMessageDialog(null, "YOU DIED \nZOMBIES KILLED = "+Game_data.ZombieCount);
				System.exit(0);
			}
		}
	}
	
	int animationloop = 0;
	int animationdelay = 75;
	long currenttime = System.currentTimeMillis();
	public Image getCurrentImage(){
		Image i = null;
		
		if(isOnAir){
			if(directionfaced == Player.facingLeft)
				i = Left_jump;
			else if(directionfaced == Player.facingRight)
				i = Right_jump;
			animationloop = 0;
		}
		else if(doWhat == Player.dontMove){
			if(directionfaced == Player.facingLeft)
				i = Left_animations.get(6);
			if(directionfaced == Player.facingRight)
				i = Right_animations.get(6);
			animationloop = 0;
		}
		else{
			if(System.currentTimeMillis()-currenttime<Left_animations.size()*animationdelay){
				animationloop = (int) ((System.currentTimeMillis()-currenttime)/animationdelay);
			}
			else
				currenttime = System.currentTimeMillis();
			if(directionfaced == Player.facingLeft){
				i = Left_animations.get((int)(animationloop));
			}
			if(directionfaced == Player.facingRight){
				i = Right_animations.get((int)(animationloop));
			}
		}
		if(isAttacking){
			if(System.currentTimeMillis()-attacktime<250){		// 250 - animatoin time
				if(directionfaced == Player.facingLeft){
					i = Left_attack;
				}
				if(directionfaced == Player.facingRight){
					i = Right_attack;
				}
				
			}
			else{
				isAttacking = false;
			}
		}
		
		
		return i;
	}
	
}
class Zombie extends Character{
	boolean isSpawning = true;
	boolean isDying;
	boolean isHSdeath;
	
	int wasDoing;
	
	Polygon bodyarea;
	int body_health = 250;
	Polygon headarea;
	int head_health = 50;
	
	Image i;
	
	
	static ArrayList<Image> Right_HS_Death = new ArrayList<Image>();
	static ArrayList<Image> Left_HS_Death = new ArrayList<Image>();
	
	static ArrayList<Image> Right_Normal_Death = new ArrayList<Image>();
	static ArrayList<Image> Left_Normal_Death = new ArrayList<Image>();
	
	static ArrayList<Image> Right_Spawn = new ArrayList<Image>();
	static ArrayList<Image> Left_Spawn = new ArrayList<Image>();
	
	static ArrayList<Image> Right_Walk = new ArrayList<Image>();
	static ArrayList<Image> Left_Walk = new ArrayList<Image>();
	
	
	
	public Zombie(int x,int y){
		super(x,y);
		
		setPolygons();
		
		x_size = x_size +6;
		y_size = y_size - 6;
		Random rgen = new Random();
		boolean i = rgen.nextBoolean();
		velocity_factor = (rgen.nextInt(2)+1)/10.0;
		
		if(i){
			doWhat = Character.moveLeft;
		}
		else
			doWhat = Character.moveRight;
		wasDoing = doWhat;
	}
	
	long time = System.currentTimeMillis();
	public void collidesWith(Barricade b){
		if(b.get_Polygon().contains(x_center-x_size, y_center)){
			if(b.isActive){
				if(doWhat == Character.moveLeft){	
					doWhat = Character.dontMove;
					wasDoing = Character.moveLeft;
					if(System.currentTimeMillis()-time>400){
						b.attack(1);
						time = System.currentTimeMillis();
						
						double x_distance = x_center - b.x_center;
						double y_distance = y_center - b.y_center;
						double length = Math.sqrt(Math.pow(x_distance, 2)+Math.pow(y_distance, 2));
						
						Random rgen = new Random();
						int turn_angle = -30;
						
						for(int i = 0; i<10;i++){
							Barricade_particle part = new Barricade_particle((int)b.x_center,(int)b.y_center,Game_data.bullets);
							part.setX_velocity(x_distance/length);
							part.setY_velocity(y_distance/length);
							part.set_Angle();
							part.turn(turn_angle+rgen.nextInt(60));
							
							Game_data.bullets.add(part);
						}
					}
				}
			}
		}
		if(b.get_Polygon().contains(x_center+x_size, y_center)){
			if(b.isActive){
				if(doWhat == Character.moveRight){	// duvar saðýnda saða gidemez
					doWhat = Character.dontMove;
					wasDoing = Character.moveRight;		//   wasDoing = hangi animasyon oynatýlacak , doWhat = zombi napýcak
					if(System.currentTimeMillis()-time>400){			// zombinin saldýrma sýklýðý: 400 milisaniyede bir
						b.attack(1);		// yanýnda duvar varsa duvara saldýr diyor
						time = System.currentTimeMillis();
						
						double x_distance = x_center - b.x_center;
						double y_distance = y_center - b.y_center;
						double length = Math.sqrt(Math.pow(x_distance, 2)+Math.pow(y_distance, 2));
						
						Random rgen = new Random();
						int turn_angle = -30;
						
						for(int i = 0; i<10;i++){
							Barricade_particle part = new Barricade_particle((int)b.x_center,(int)b.y_center,Game_data.bullets);
							part.setX_velocity(x_distance/length);
							part.setY_velocity(y_distance/length);
							part.set_Angle();
							part.turn(turn_angle+rgen.nextInt(60)); 
							
							Game_data.bullets.add(part);	
						}
					}
				}
			}
		}
	}
	
	public void attackbody(int i){
		if(!isDying){
			body_health -= i;
			if(body_health<0){
				Game_data.ZombieCount++;
				isDying = true;
				isHSdeath = false;
				currenttime = System.currentTimeMillis();
			}
		}
	}
	public void attackhead(int i){
		if(!isDying){
			head_health -= i;
			if(head_health<0){
				Game_data.ZombieCount++;
				isDying = true;
				isHSdeath = true;
				currenttime = System.currentTimeMillis();
				
				Random rgen = new Random();
				for(int j = 0;i<100;i++){
					Blood blood = new Blood((int)x_center,(int)y_center);
					blood.setX_velocity(1);
					blood.setY_velocity(0);
					blood.set_Angle();
					
					blood.turn(rgen.nextInt(180));
					
					Game_data.bloods.add(blood);
				}
			}
		}
		
	}
	
	
	int animationloop = 0;
	int animationdelay = 75;
	long currenttime = System.currentTimeMillis();
	public Image getCurrentImage(){
		Image i = null;
		if(isSpawning){																		
			if(System.currentTimeMillis()-currenttime<Left_Spawn.size()*animationdelay ){
				animationloop = (int) ((System.currentTimeMillis()-currenttime)/animationdelay);
			}
			else{
				isSpawning = false;
				currenttime = System.currentTimeMillis();
			}
			if(wasDoing == Character.moveLeft){
				i = Left_Spawn.get(animationloop);
			}
			else if(wasDoing == Character.moveRight){
				i = Right_Spawn.get(animationloop);
				
			}
		}
		else if(isDying){
			animationdelay = 100;
			if(isHSdeath){
				if(System.currentTimeMillis()-currenttime<Left_HS_Death.size()*animationdelay ){
					animationloop = (int) ((System.currentTimeMillis()-currenttime)/animationdelay);
				}
				else{
					Game_data.player.money += 25;
					Game_data.zombies.remove(this);
				}
				if(wasDoing == Character.moveLeft){
					i = Left_HS_Death.get(animationloop);
					
				}
				else if(wasDoing == Character.moveRight){
					i = Right_HS_Death.get(animationloop);
				}	
			}
			else{
				if(System.currentTimeMillis()-currenttime<Left_Normal_Death.size()*animationdelay ){
					animationloop = (int) ((System.currentTimeMillis()-currenttime)/animationdelay);
				}
				else{
					Game_data.player.money += 10;
					Game_data.zombies.remove(this);
				}
				if(wasDoing == Character.moveLeft){
					i = Left_Normal_Death.get(animationloop);
				}
				else if(wasDoing == Character.moveRight){
					i = Right_Normal_Death.get(animationloop);
				}
				
			}
		}
		else{
			if(System.currentTimeMillis()-currenttime<Left_Walk.size()*animationdelay ){
				animationloop = (int) ((System.currentTimeMillis()-currenttime)/animationdelay);
			}
			else{
				currenttime = System.currentTimeMillis();
			}
			if(wasDoing == Character.moveLeft){
				i = Left_Walk.get(animationloop);
			}
			else if(wasDoing == Character.moveRight){
				i = Right_Walk.get(animationloop);
			}
			
		}
		
		return i;
	}
	
	
	public void setPolygons(){
		int[] bodypolygon_x_array = new int[4];
		int[] bodypolygon_y_array = new int[4];
		bodypolygon_x_array[0] = (int) (x_center-x_size);
		bodypolygon_x_array[1] = (int) (x_center+x_size);
		bodypolygon_x_array[2] = (int) (x_center+x_size);
		bodypolygon_x_array[3] = (int) (x_center-x_size);
		
		bodypolygon_y_array[0] = (int) (y_center-(y_size-13));		// hit_boxes
		bodypolygon_y_array[1] = (int) (y_center-(y_size-13));
		bodypolygon_y_array[2] = (int) (y_center+(y_size+6));
		bodypolygon_y_array[3] = (int) (y_center+(y_size+6));
		
		bodyarea = new Polygon(bodypolygon_x_array,bodypolygon_y_array,4);
		
		int[] headpolygon_x_array = new int[4];
		int[] headpolygon_y_array = new int[4];
		headpolygon_x_array[0] = (int) (x_center-(x_size-5));
		headpolygon_x_array[1] = (int) (x_center+(x_size-5));
		headpolygon_x_array[2] = (int) (x_center+(x_size-5));
		headpolygon_x_array[3] = (int) (x_center-(x_size-5));
		
		headpolygon_y_array[0] = (int) (y_center-(y_size+3));
		headpolygon_y_array[1] = (int) (y_center-(y_size+3));
		headpolygon_y_array[2] = (int) (y_center+(y_size-19));
		headpolygon_y_array[3] = (int) (y_center+(y_size-19));
		
		headarea = new Polygon(headpolygon_x_array,headpolygon_y_array,4);
	}
	
	public void paint(Graphics g){
//		g.setColor(Color.GREEN);
//		g.drawPolygon(bodyarea);
//		                                  This parts are used to visualize the hit-boxes
//		g.setColor(Color.GREEN);
//		g.drawPolygon(headarea);
		
		if(getCurrentImage() != null){
			i = getCurrentImage();
		}
		if(i != null){
			g.drawImage(i, (int)(x_center-(i.getWidth(null)/2.0)), (int)(y_center-(i.getHeight(null)/2.0)), null);
		}
	}
	
	long attacktime = System.currentTimeMillis();
	public void move(){
		super.move();
		
		setPolygons();
		
//		System.out.println(Game_data.player.x_center-x_center);
		if(Game_data.player.x_center-x_center<-14){
			doWhat = Character.moveLeft;							
		}														
		else if(Game_data.player.x_center-x_center>14){
			doWhat = Character.moveRight;
		}
		else
			doWhat = Character.dontMove;
		wasDoing = doWhat;
		
		if(!isDying){
			if(bodyarea.contains(Game_data.player.x_center,Game_data.player.y_center)){
				if(System.currentTimeMillis()-attacktime>500){
					Game_data.player.attack(25);
					attacktime = System.currentTimeMillis();
					
					double x_distance = x_center - Game_data.player.x_center;
					double y_distance = y_center - Game_data.player.y_center;
					double length = Math.sqrt(Math.pow(x_distance, 2)+Math.pow(y_distance, 2));
					
					Random rgen = new Random();
					int turn_angle = -30;
					
					for(int i = 0; i<150;i++){
						Blood blood = new Blood((int)Game_data.player.x_center,(int)Game_data.player.y_center);
						blood.setX_velocity(x_distance/length);
						blood.setY_velocity(y_distance/length);
						blood.set_Angle();
						blood.turn(turn_angle+rgen.nextInt(60));
						
						Game_data.bloods.add(blood);
					}
				}
			}
		}
	}
	
	public static void setAnimations(){
		for(int i = 0;i<12;i++){
			try {
				Right_HS_Death.add(ImageIO.read(new File("res/graphics/Zombie/ZombieHSDeath/RZombie-Death"+(i+1)+".png")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(int i = 0;i<12;i++){
			try {
				Left_HS_Death.add(ImageIO.read(new File("res/graphics/Zombie/ZombieHSDeath/LZombie-Death"+(i+1)+".png")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		for(int i = 0;i<9;i++){
			try {
				Right_Normal_Death.add(ImageIO.read(new File("res/graphics/Zombie/ZombieNormalDeath/RZombie-Death"+(i+1)+".png")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(int i = 0;i<9;i++){
			try {
				Left_Normal_Death.add(ImageIO.read(new File("res/graphics/Zombie/ZombieNormalDeath/LZombie-Death"+(i+1)+".png")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(int i = 0;i<11;i++){
			try {
				Right_Spawn.add(ImageIO.read(new File("res/graphics/Zombie/ZombieSpawn/RZombie-Spawn"+(i+1)+".png")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(int i = 0;i<11;i++){
			try {
				Left_Spawn.add(ImageIO.read(new File("res/graphics/Zombie/ZombieSpawn/LZombie-Spawn"+(i+1)+".png")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(int i = 0;i<8;i++){
			try {
				Right_Walk.add(ImageIO.read(new File("res/graphics/Zombie/ZombieWalk/RZombie-Walk"+(i+1)+".png")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(int i = 0;i<8;i++){
			try {
				Left_Walk.add(ImageIO.read(new File("res/graphics/Zombie/ZombieWalk/LZombie-Walk"+(i+1)+".png")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
class ZombieSpawner implements Serializable{
	int x_location,y_location;
	Polygon polygon;
	
	public ZombieSpawner(int x,int y){
		x_location = x;
		y_location = y;
		
		setPolygon();
	}
	public void setPolygon(){
		int[] x_array = new int[4];
		int[] y_array = new int[4];
		x_array[0] = x_location-5;
		x_array[1] = x_location+5;
		x_array[2] = x_location+5;
		x_array[3] = x_location-5;
		
		y_array[0] = y_location-5;
		y_array[1] = y_location-5;
		y_array[2] = y_location+5;
		y_array[3] = y_location+5;
		
		polygon = new Polygon(x_array,y_array,4);
	}
	
	public void paint(Graphics g){
		g.setColor(Color.RED);
		g.drawOval(x_location-5, y_location-5, 10, 10);
	}
	public void spawn(){
		Zombie zombie = new Zombie(x_location,y_location);
		
		Game_data.zombies.add(zombie);
		zombie.currenttime = System.currentTimeMillis();
	}
}



