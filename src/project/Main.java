package project;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.crypto.Data;

public class Main{
	static int panel_xsize = 1400;
	static int panel_ysize = 650;
	static the_Game game;
	static JFrame frame;
	
	public static void main(String[] args) {
		frame = new JFrame("Zombie Defense");
		frame.setSize(panel_xsize, panel_ysize+150);
		frame.setLayout(null);
		
		Zombie.setAnimations();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		MapEditor editor = new MapEditor();
		editor.setBounds(0,0,panel_xsize,panel_ysize);
		editor.setFocusable(true);			
		
		game = new the_Game();
		game.setBounds(0, 0, panel_xsize, panel_ysize);
		
		
		Menu main_menu = new Menu();
		main_menu.setBounds(0, 0, panel_xsize, panel_ysize);
		frame.add(main_menu);
		
		initializeButtons(frame,main_menu,game,editor);
				
		frame.setVisible(true);
	
		
		while(true) {
			game.move();
			
			Main_ButtonListener.current_panel.repaint();
			
			try {
				Thread.currentThread().sleep(2);	// 2 milisaniyede bir move komutu 
			} catch (InterruptedException e) {		
				// TODO Auto-generated catch block		
				e.printStackTrace();
			}
        }
		
	}
	
	
	public static void initializeButtons(JFrame frame,Menu main_menu,the_Game game,MapEditor editor){
		JButton Menu_Button = new JButton("Menu");
		Menu_Button.setBounds(800, 675, 75, 50);
		Menu_Button.addMouseListener(new Main_ButtonListener(frame,main_menu,game,editor));
		frame.add(Menu_Button);
		
		JButton Map_Button = new JButton("Map Editor");
		Map_Button.setBounds(540, 675, 125, 50);
		Map_Button.addMouseListener(new Main_ButtonListener(frame,main_menu,game,editor));
		frame.add(Map_Button);
		
		JButton Game_Button = new JButton("Game");
		Game_Button.setBounds(700,675,75,50);
		Game_Button.addMouseListener(new Main_ButtonListener(frame,main_menu,game,editor));
		frame.add(Game_Button);
		
		JButton Exit_Button = new JButton("Exit");
		Exit_Button.setBounds(900,675,75,50);
		Exit_Button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
				}
			});
		frame.add(Exit_Button);
		
		
	}
}

class Main_ButtonListener extends MouseAdapter {
	JFrame frame;
	Menu menu;
	the_Game game;
	static JPanel current_panel;
	MapEditor mapeditor;
	
	public Main_ButtonListener(JFrame frame,Menu menu,the_Game game,MapEditor editor){
		this.menu = menu;
		this.game = game;
		this.frame = frame;
		mapeditor = editor;
		current_panel = menu;
	}
	public static JPanel getCurrentPanel(){
		return current_panel;
	}
	
	public void mouseClicked(MouseEvent e){
		if(e.getSource() instanceof JButton){
			JButton button = (JButton) e.getSource();
			if(button.getText().equals("Game")){
				Game_data.player.isInvulnearible = false;
				try {
					Game_data.walls = (ArrayList<wall>) SaveNLoad.load("res/data/wall_info.txt");
				} catch (Exception e1) {
					Game_data.walls = new ArrayList<wall>();
				}
				try {
					Game_data.barricades = (ArrayList<Barricade>) SaveNLoad.load("res/data/barricade_info.txt");
				} catch (Exception e1) {
					Game_data.barricades = new ArrayList<Barricade>();
				}
				try {
					Game_data.spawners = (ArrayList<ZombieSpawner>) SaveNLoad.load("res/data/spawner_info.txt");
				} catch (Exception e1) {
					Game_data.spawners = new ArrayList<ZombieSpawner>();
				}
				game.updateWalls();
				Game_data.renderer.initializestaticgraphics();
				frame.remove(current_panel);
				frame.add(game);
				game.requestFocusInWindow();
				current_panel = game;
				
				Game_data.player.x_center = 100;
				Game_data.player.y_center = 40;
				Game_data.player.y_velocity = 0;
			}
			if(button.getText().equals("Menu")){
				Game_data.player.isInvulnearible = true;
				frame.remove(current_panel);
				frame.add(menu);
				frame.repaint();
				menu.requestFocusInWindow();
				current_panel = menu;
			}
			if(button.getText().equals("Map Editor")){
				Game_data.player.isInvulnearible = true;
				frame.remove(current_panel);
				frame.add(mapeditor);
				current_panel = mapeditor;
				frame.repaint();
				SwingUtilities.getWindowAncestor(mapeditor).requestFocus();
				mapeditor.requestFocusInWindow();
				mapeditor.requestFocus();
				mapeditor.grabFocus();
			}
		
		}
	}
}
class Menu extends JPanel{
	public Menu(){
		JLabel label = new JLabel(new ImageIcon("res/graphics/Game_Menu_Image.png"));
		add(label);
	}
}


class the_Game extends JPanel{
	Game_data data;
	
	public the_Game(){
		data = new Game_data();
		
		Game_data.player = new Player();
		
		Game_data.arrows = new ArrayList<Arrow>();
		Game_data.bullets = new ArrayList<Ball>();
		Game_data.isFiring = false;
		
		
		addMouseMotionListener(new MouseDetecter(Game_data.player));
		addMouseListener(new MouseDetecter(Game_data.player));
		addKeyListener(new KeyDetecter(Game_data.player));
		
		try {
			Game_data.walls = (ArrayList<wall>) SaveNLoad.load("res/data/wall_info.txt");
		} catch (Exception e1) {
			Game_data.walls = new ArrayList<wall>();
			System.out.println("no wall file found");
		}
		try {
			Game_data.barricades = (ArrayList<Barricade>) SaveNLoad.load("res/data/barricade_info.txt");
		} catch (Exception e1) {
			Game_data.barricades = new ArrayList<Barricade>();
			System.out.println("no barricade file found");
		}
		try {
			Game_data.spawners = (ArrayList<ZombieSpawner>) SaveNLoad.load("res/data/spawner_info.txt");
		} catch (Exception e1) {
			Game_data.spawners = new ArrayList<ZombieSpawner>();
			System.out.println("no spawner file found");
		}
		
		
		try {
			Game_data.background = ImageIO.read(new File("res/graphics/Game_Background.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Cannot read Background image");
		}
		Game_data.threads = new ArrayList<workSplitter>();
		for(int i = 0; i<Game_data.thread_count;i++){
			Game_data.threads.add(new workSplitter(Game_data.walls));
			Game_data.threads.get(i).start();
		}
		Game_data.renderer = new RenderThread();
		Game_data.renderer.initializestaticgraphics();
		Game_data.renderer.start();
	}	
	
	public void updateWalls(){
		for(int i = 0; i<Game_data.threads.size();i++){
			Game_data.threads.get(i).updateWalls(Game_data.walls);
		}
	}
	long spawntime = System.currentTimeMillis();
	public void move(){
		
		Game_data.player.collidesWith(Game_data.walls,Game_data.arrows);
		Game_data.player.move();
		
		int waittime = 5000-Game_data.ZombieCount*100;			// waittime: zombilerim spawnlar� aras� s�re
		if(waittime < 200){
			waittime = 200;			// bu �ekilde belli bir yerden sonra zombi saysn�n sabit kalmas�n� sa�lad�k
		}							// ama bir yerden sonra bir sap�tma s�z konusu...
		if(System.currentTimeMillis()-spawntime>waittime){
			int whichspawner = Game_data.rgen.nextInt(Game_data.spawners.size()-1);
			Game_data.spawners.get(whichspawner).spawn();		
			
			spawntime = System.currentTimeMillis();
		}
		if(Game_data.ZombieCount>500){
			JOptionPane.showMessageDialog(null, "You win");
			System.exit(0);
		}
		
		for(int i = 0;i<Game_data.zombies.size();i++){
			try{
				Game_data.zombies.get(i).move();
				Game_data.zombies.get(i).collidesWith(Game_data.walls, Game_data.arrows);
				
				for(int a = 0;a<Game_data.arrows.size();a++){
					Game_data.arrows.get(a).collidesWith(Game_data.zombies.get(i));
				}
				for(int b = 0;b<Game_data.bullets.size();b++){
					Game_data.bullets.get(b).collidesWith(Game_data.zombies.get(i));
				}
			}
			catch(Exception ex){
				
			}
			for(int k = 0;k<Game_data.barricades.size();k++){
				try{
					Game_data.zombies.get(i).collidesWith(Game_data.barricades.get(k));
				}
				catch(Exception ex){
					
				}
			}
		}
			
		for(int g = 0;g<Game_data.walls.size();g++){
			for(int j=0;j<Game_data.arrows.size();j++){
				try {
					Game_data.arrows.get(j).collidesWith(Game_data.walls.get(g));
				}
				catch (Exception e){
				}
			}
			for(int k=0;k<Game_data.bullets.size();k++){
				try {
					Game_data.bullets.get(k).collidesWith(Game_data.walls.get(g));
				}
				catch (Exception ex){
					
				}
			}
		}
		if(Game_data.isFiring&&Game_data.Shoot_Type==Game_data.SHOOT_FLAME){
			int spread_degree = 4;

			Game_data.player.isAttacking = true;
			Game_data.player.attacktime = System.currentTimeMillis();
			
			Flame flame = new Flame((int)(Game_data.player.x_center),(int)(Game_data.player.y_center-11),Game_data.threads.get(Game_data.which_Thread));
			flame.setX_velocity(Game_data.Particle_xvelocity);
			flame.setY_velocity(Game_data.Particle_yvelocity);
			flame.set_Angle();
			flame.turn(spread_degree);
			
			for(int i = 0;i<3;i++){		
				Flame tempflame = new Flame((int)(Game_data.player.x_center),(int)(Game_data.player.y_center-11),Game_data.threads.get(Game_data.which_Thread));
				tempflame.setX_velocity(1);
				tempflame.setY_velocity(0);
				tempflame.set_Angle();
				tempflame.turn(flame.angle);
				
				tempflame.turn(-(data.rgen.nextInt(spread_degree*2)));
				
				if(Game_data.player.flameThrowerammo>0){
					Game_data.threads.get(Game_data.which_Thread).add_particle(tempflame);
					Game_data.player.flameThrowerammo --;
				}
			}
			
			flame.turn(-(data.rgen.nextInt(spread_degree*2)));
			
			if(Game_data.player.flameThrowerammo>0){
				Game_data.threads.get(Game_data.which_Thread).add_particle(flame);
				Game_data.player.flameThrowerammo --;
			}
			Game_data.which_Thread ++;
			Game_data.which_Thread = Game_data.which_Thread%Game_data.thread_count;
		}
		
		long lasttime = System.currentTimeMillis();
		int delaytime = 1;
		try{
			if(lasttime-firsttime-2>1)
				delaytime = (int) (lasttime-firsttime-2);
			else
				delaytime = 1;
		}
		catch(Exception ex){
			
		}
		long firsttime = System.currentTimeMillis();
		
		for(int i=0;i<Game_data.arrows.size();i++){
			Game_data.arrows.get(i).move(delaytime);
		}
		for(int i = 0;i<Game_data.bullets.size();i++){
			try{
				Game_data.bullets.get(i).move(delaytime);
			}
			catch(Exception ex){
				
			}
		}
		for(int p = 0;p<Game_data.bloods.size();p++){
			try{
				Game_data.bloods.get(p).move(delaytime);
				for(int i = 0;i<Game_data.walls.size();i++){
					Game_data.bloods.get(p).collidesWith(Game_data.walls.get(i));
				}
			}
			catch (Exception ex){
				
			}
		}
		
		
		for(int i = 0; i<Game_data.threads.size();i++){
			if(!Game_data.threads.get(i).isAlive()){
				System.out.println("thread is dead");
				System.out.println(i);
				
				Game_data.threads.get(i).interrupt();
				
				Game_data.threads.remove(i);
				workSplitter new_thread = new workSplitter(Game_data.walls);
				Game_data.threads.add(new_thread);
				new_thread.start();
			}
		}
		
	}
	public void update(Graphics g){
		paint(g);
	}
	
	long firsttime;
	long lasttime;
	
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		
		firsttime = System.currentTimeMillis();
		
		if(Game_data.Shoot_Type == Game_data.SHOOT_PARTICLE){
			g.drawString("Ammo = Infinite", 500, 300);
		}
		else if(Game_data.Shoot_Type == Game_data.SHOOT_ARROW){
			g.drawString("Ammo = "+Game_data.player.arrowCount, 500, 300);
			
		}
		else if(Game_data.Shoot_Type == Game_data.SHOOT_FLAME){
			g.drawString("Ammo = "+Game_data.player.flameThrowerammo, 500, 300);
		}

		
		g2.drawImage(RenderThread.completed_frame, null, 0, 0);
			

			lasttime = System.currentTimeMillis();
			if(lasttime-firsttime>10)
//				System.out.println(lasttime-firsttime);
			
		
		g.dispose();
		g2.dispose();
	}
	
	
class MouseDetecter extends MouseAdapter{
	private Player player;
	private double xdistance;
	private double ydistance;
	private double length; 
	private Arrow arrow;
	private Ball bullet;
	
	public MouseDetecter(Player player){
		this.player = player;
	}
	
		public void mousePressed(MouseEvent e){
			Game_data.player.isAttacking = true;
			Game_data.player.attacktime = System.currentTimeMillis();
			xdistance = e.getX() - player.x_center;
			ydistance = e.getY() - player.y_center;
			
			length = Math.sqrt((xdistance*xdistance+ydistance*ydistance));
			
			
			if(Game_data.Shoot_Type == Game_data.SHOOT_ARROW){
				if(Game_data.player.arrowCount>0){
					Game_data.player.arrowCount --;
					
					arrow = new Arrow((int)(player.x_center),(int)(player.y_center-11));
					arrow.setX_velocity(xdistance/length);
					arrow.setY_velocity(ydistance/length);
					Game_data.arrows.add(arrow);
				}
				
			}
			else if(Game_data.Shoot_Type == Game_data.SHOOT_FLAME){
				Game_data.Particle_xvelocity = xdistance/length;
				Game_data.Particle_yvelocity = ydistance/length;
						
				Game_data.isFiring = true;
			}
			else if(Game_data.Shoot_Type == Game_data.SHOOT_PARTICLE){
				bullet = new Ball((int)(player.x_center),(int)(player.y_center-11),Game_data.bullets);
				bullet.setX_velocity(xdistance/length);
				bullet.setY_velocity(ydistance/length);
				Game_data.bullets.add(bullet);
			}
				
			
		}
		public void mouseReleased(MouseEvent e){
			if(Game_data.Shoot_Type == Game_data.SHOOT_FLAME){
				Game_data.isFiring = false;
			}
			
		}
		 public void mouseDragged(MouseEvent e){
			if(Game_data.Shoot_Type == Game_data.SHOOT_FLAME){
				xdistance = e.getX() - player.x_center;
				ydistance = e.getY() - player.y_center;
				length = Math.sqrt((xdistance*xdistance+ydistance*ydistance));
				
				Game_data.Particle_xvelocity = xdistance/length;
				Game_data.Particle_yvelocity = ydistance/length;
				
				if(Game_data.player.x_center>e.getX())
					Game_data.player.directionfaced = Player.facingLeft;
				if(Game_data.player.x_center<e.getX())
					Game_data.player.directionfaced = Player.facingRight;
			}	
		}
		 
		public void mouseMoved(MouseEvent e){
			((JPanel)e.getSource()).setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			if(Game_data.player.x_center>e.getX())
				Game_data.player.directionfaced = Player.facingLeft;
			if(Game_data.player.x_center<e.getX())
				Game_data.player.directionfaced = Player.facingRight;
		}
	}

class KeyDetecter extends KeyAdapter{
	private Player player;
	JPanel panel;
	
	public KeyDetecter(Player player){
		this.player = player;
	}
	double velocityfactor = 0.8;
	
	public void keyPressed(KeyEvent e){
		if(e.getKeyChar() == 'a'){
			player.doWhat = Player.moveLeft;
//			System.out.print("left");
		}
		if(e.getKeyChar() == 'd'){
			player.doWhat = Player.moveRight;
//			System.out.print("right");
		}
		if(e.getKeyChar() == 's'){
//			character.y_velocity = velocityfactor;
		}
		if(e.getKeyChar() == 'w'){
//			System.out.print("jump");
//			System.out.print(character.isOnAir);
			player.jump();
			
		}
		if(e.getKeyChar() == 'b'){
			boolean isInside = false;
			Barricade barricade = null;
			for(int i = 0;i<Game_data.barricades.size();i++){
				if(Game_data.barricades.get(i).polygon.contains(Game_data.player.x_center, Game_data.player.y_center)){
					isInside = true;
					barricade = Game_data.barricades.get(i);
				}
			}
			try{
				if(barricade!=null||!barricade.isActive){
					if(Game_data.player.money>Game_data.barricade_cost){
						barricade.life = 100;
						barricade.isActive = true;
						Game_data.player.money -= Game_data.barricade_cost;
					}
				}
			}
			catch(Exception ex){
				
			}
		}
		if(e.getKeyChar() == 'g'){
			if(Main_ButtonListener.current_panel != BuyMenu.buymenu){
				panel = Main_ButtonListener.current_panel;
				Main_ButtonListener.current_panel = BuyMenu.buymenu;
				Main.game.add(BuyMenu.buymenu);
			}
			else {
				Main_ButtonListener.current_panel = panel;
				Main.game.remove(BuyMenu.buymenu);
			}
		}
		if(e.getKeyCode() == '1'){
			Game_data.Shoot_Type = Game_data.SHOOT_PARTICLE;
		}
		if(e.getKeyCode() == '2'){
			if(Game_data.player.hasBow){
				Game_data.Shoot_Type = Game_data.SHOOT_ARROW;
			}
		}
		if(e.getKeyCode() == '3'){
			if(Game_data.player.hasFlameThrower){
				Game_data.Shoot_Type = Game_data.SHOOT_FLAME;
			}
		}
	}
	
	public void keyReleased(KeyEvent e){
		if(e.getKeyChar() == 'a'){
			if(player.doWhat == Player.moveLeft){
				player.doWhat = Player.dontMove;
			}
		}
		if(e.getKeyChar() == 'd'){
			if(player.doWhat == Player.moveRight){
				player.doWhat = Player.dontMove;
			}
		}
		if(e.getKeyChar() == 's'){
//			character.y_velocity = 0;
		}
		if(e.getKeyChar() == 'w'){
//			character.y_velocity = 0;
		}
	}
}
}

