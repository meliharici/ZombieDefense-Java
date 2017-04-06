package project;

import java.util.ArrayList;

public class workSplitter extends Thread {
	private ArrayList<wall> walls;
//	ArrayList<Particle> particles;								workSplitter flame thrower parçacýklarýný hallediyor
	Particle[] particles;										// parçacýklar mevcud olan workSplittelrler arasýnda paylaþýlýyor
	
	public workSplitter(ArrayList<wall> walls){					
		this.walls = walls;
//		this.particles = new ArrayList<Particle>();
		particles = new Particle[5000];
	}
	public int getEmptyIndex(){
		for(int i = 0;i<particles.length;i++){
			if(particles[i] == null){
				return i;
			}
		}
		return -1;
	}
	
	public void add_particle(Particle p){
		int index = getEmptyIndex();
		
		if(index != -1){
			particles[index] = p;
			if(p instanceof Flame){
				((Flame)p).index = index;
			}
			if(p instanceof Smoke){
				((Smoke)p).index = index;
			}
		}
	}
	public void updateWalls(ArrayList<wall> walls){
		this.walls = walls;
	}

	long lasttime;
	long firsttime;
	int delaytime;
	public void run(){
		boolean isStopped = false;
		
		while(!isStopped){
			
			lasttime = System.currentTimeMillis();
			
			try{
				if(lasttime-firsttime-2 > 1)
					delaytime = (int) (lasttime-firsttime-2);
				else
					delaytime = 1;
//				if(delaytime>5)
//					System.out.println(delaytime);
			}
			catch(Exception ex){
				
			}
			firsttime = System.currentTimeMillis();
			
			for(int i = 0; i<particles.length;i++){
				for(int j=0; j<walls.size();j++){
					try{
						Particle part = particles[i];
						if(part != null){
							part.collidesWith(walls.get(j));
						}
						
					}
					catch(Exception ex){
						
					
					}
				}
			}
			for(int i = 0;i<particles.length;i++){
				for(int k = 0;k<Game_data.zombies.size();k++){
					try{
						Particle part = particles[i];
						if(part instanceof Flame){
							((Flame)part).collidesWith(Game_data.zombies.get(k));
						}

					}
					catch(Exception ex){
						
					}
				}
			}
			
			for(int i = 0; i<particles.length;i++){
				try{
					Particle part = particles[i];
					if(part != null){
						part.move(delaytime);
					}
				}
				catch(Exception ex){
					System.out.println(ex.getMessage());
				}
			}
			
			try {
				this.sleep(2);
			} catch (InterruptedException e) {
				isStopped = true;
				System.out.println("interrupted");
			}
			
		}
	}
}


