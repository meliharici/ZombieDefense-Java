package project;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Game_data {
	static final int SHOOT_ARROW = 0;
	static final int SHOOT_PARTICLE = 1;
	static final int SHOOT_FLAME = 2;
	static int Shoot_Type = SHOOT_PARTICLE;
	
	static final int Flame_Thrower_Price = 100;
	static final int Flame_Thrower_Ammo_Price = 50;
	
	static final int Bow_Price = 100;
	static final int Arrow_Price = 50;
	
	static final int Barricade_health = 50;

	static RenderThread renderer;
	
	static final int thread_count = 3; /* This is the count of the threads that handle flame-thrower particles only */
	static int which_Thread = 0;
	
	static ArrayList<wall> walls;
	static ArrayList<Barricade> barricades;
	static ArrayList<ZombieSpawner> spawners;
	static final int barricade_cost = 50;
	
	static ArrayList<Arrow> arrows;
	static ArrayList<Ball> bullets;
	
	static ArrayList<Blood> bloods = new ArrayList<Blood>();
	
	static ArrayList<Zombie> zombies = new ArrayList<Zombie>();
	
	static Player player;
	static Image background;
	static ArrayList<workSplitter> threads;
	
	static boolean isFiring;
	
	static double Particle_xvelocity;
	static double Particle_yvelocity;
	
	static Random rgen = new Random();
	
	static String framerate;
	
	static int ZombieCount = 0;
	
	public Game_data(){
		
	}
}
