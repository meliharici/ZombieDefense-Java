package project;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

public class RenderThread extends Thread{
	static BufferedImage default_frame;
	static BufferedImage completed_frame;
	static BufferedImage draw_image;
	static boolean isFrameReady;
	long bloodtime;
											// paint iþlerini bu hallediyor çünkü main de yapsak çok iþ yükü biniyor ve yavaþlýyordu
	public RenderThread(){
		bloodtime = System.currentTimeMillis();
	}
	
	public void initializestaticgraphics(){		
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		
		default_frame = gc.createCompatibleImage(Main.panel_xsize, Main.panel_ysize);
		completed_frame = gc.createCompatibleImage(Main.panel_xsize, Main.panel_ysize);
		draw_image = gc.createCompatibleImage(Main.panel_xsize, Main.panel_ysize);
		Graphics2D g2 = default_frame.createGraphics();
		g2.drawImage(Game_data.background, 0, 0, null);
		
		for(int j=0;j<Game_data.walls.size();j++){
			Game_data.walls.get(j).paint(g2);
		}
		
		
	}
	public void addStaticBlood(){
		Graphics2D g2 = default_frame.createGraphics();
		for(int i = 0;i<Game_data.bloods.size(); i++){
			if(Game_data.bloods.get(i).isStuck){
				Game_data.bloods.get(i).paint(g2);
				Game_data.bloods.remove(Game_data.bloods.get(i));
			}
		}
	}
	
	public void run(){
		while(true){
			
			Graphics2D g2 = draw_image.createGraphics();
			g2.drawImage(RenderThread.default_frame, 0, 0, null);
			
			long firsttime = System.currentTimeMillis();
			for(int i = 0;i<Game_data.threads.size();i++){
				for(int p=0;p<Game_data.threads.get(i).particles.length;p++){
					try{
						Particle part = Game_data.threads.get(i).particles[p];
						
						if(part != null){
//							if(part instanceof Flame)
//								draw_image.setRGB((int)part.x_center, (int)part.y_center, new Color(5,255,5).getRGB());
//							else if(part instanceof Smoke)
//								draw_image.setRGB((int)part.x_center, (int)part.y_center, new Color(65,65,65).getRGB());
							part.paint(g2);
						}
					}
					catch(Exception ex){
					}
				}
			}
			long lasttime = System.currentTimeMillis();
//			System.out.println(lasttime-firsttime);
			
			for(int j=0;j<Game_data.barricades.size();j++){
				Game_data.barricades.get(j).paint(g2);
			}
			for(int i = 0;i<Game_data.bloods.size();i++){
				try{
					Game_data.bloods.get(i).paint(g2);
				}
				catch(Exception ex){
					
				}
			}
			
			if(System.currentTimeMillis()-bloodtime>10000){
				addStaticBlood();
				bloodtime = System.currentTimeMillis();
			}
			
			for(int p=0;p<Game_data.arrows.size();p++){
				Game_data.arrows.get(p).paint(g2);
			}
			for(int i = 0;i<Game_data.bullets.size();i++){
				try{
					Game_data.bullets.get(i).paint(g2);
				}
				catch(Exception ex){
					
				}
			}
			Game_data.player.paint(g2);
			
			for(int i = 0;i<Game_data.zombies.size();i++){
				try{
					Game_data.zombies.get(i).paint(g2);
				}
				catch(Exception ex){
					
				}
			}
			
			g2.setColor(Color.GREEN);
			int ammocounter_xlocation = 1215;
			int ammocounter_ylocation = 20;
			if(Game_data.Shoot_Type == Game_data.SHOOT_PARTICLE){
				g2.drawString("Ammo = Infinite", ammocounter_xlocation, ammocounter_ylocation);
			}
			else if(Game_data.Shoot_Type == Game_data.SHOOT_ARROW){
				g2.drawString("Ammo = "+Game_data.player.arrowCount, ammocounter_xlocation, ammocounter_ylocation);
				
			}
			else if(Game_data.Shoot_Type == Game_data.SHOOT_FLAME){
				g2.drawString("Ammo = "+Game_data.player.flameThrowerammo, ammocounter_xlocation, ammocounter_ylocation);
			}
			g2.drawString("Money = "+Game_data.player.money+"$", ammocounter_xlocation, ammocounter_ylocation+15);
			g2.drawString("Health = "+Game_data.player.health, 15, 15);
			
//			for(int j=0;j<Game_data.walls.size();j++){
//				Game_data.walls.get(j).paint(g2);
//			}
			
			Graphics2D frameGraphics = completed_frame.createGraphics();
			frameGraphics.drawImage(draw_image, 0, 0, null);
			
			try {
				sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
