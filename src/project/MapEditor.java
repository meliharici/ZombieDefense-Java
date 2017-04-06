package project;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

/* Main was getting too crowded so we decided to move this one into separate file */
public class MapEditor extends JPanel {

	static int whatToDraw;
	static final int drawWall = 1;
	static final int drawBarricade = 2;
	static final int drawSpawner = 3;

	ArrayList<wall> walls;
	ArrayList<Barricade> barricades;
	ArrayList<ZombieSpawner> spawners;

	String floating_buttontext;
	
	int x, y;
	int x_size, y_size;
	int max_x, max_y, min_x, min_y;
	static boolean drawing_new_rect;
	private static final int min_Size = 7;
	public boolean isEntered = false;
	MapEditor_MouseListener handler;
	int mouseX, mouseY;

	public MapEditor() {
		setLayout(null);

		handler = new MapEditor_MouseListener();

		addMouseListener(handler);
		addMouseMotionListener(handler);
		addMouseWheelListener(handler);
		walls = new ArrayList<wall>();
		barricades = new ArrayList<Barricade>();
		spawners = new ArrayList<ZombieSpawner>();
		repaint();

		initializeButtons();

		addKeyListener(new MapEditor_KeyListener());
	}

	public void paint(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		super.paint(g2);

		if (isEntered) {
			g2.drawString(floating_buttontext, mouseX, mouseY);
		}

		if (drawing_new_rect) {
			g2.setColor(Color.red);
			if (x_size / 2 > min_Size && y_size / 2 > min_Size)
				g2.drawRect(min_x, min_y, x_size, y_size);
			else
				g2.fillRect(min_x, min_y, x_size, y_size);
		}

		for (int i = 0; i < walls.size(); i++) {
			walls.get(i).paint(g2);
		}

		for (int b = 0; b < barricades.size(); b++) {
			barricades.get(b).paint(g2);
		}
		
		for(int s = 0; s<spawners.size(); s++){
			spawners.get(s).paint(g2);
		}

	}

	public void initializeButtons() {
		JButton save_button = new JButton("Save");
		save_button.addMouseListener(new Button_MouseListener());
		save_button.setBounds(1100, 50, 75, 50);
		add(save_button);

		JButton load_button = new JButton("Load");
		load_button.addMouseListener(new Button_MouseListener());
		load_button.setBounds(1100, 125, 75, 50);
		add(load_button);

		JButton reset_button = new JButton("Reset");
		reset_button.addMouseListener(new Button_MouseListener());
		reset_button.setBounds(1100, 200, 75, 50);
		add(reset_button);

		JButton wall_button = new JButton("Wall");
		wall_button.addMouseListener(new Button_MouseListener());
		wall_button.setBounds(1100, 275, 35, 50);
		add(wall_button);

		JButton barricade_button = new JButton("Barricade");
		barricade_button.addMouseListener(new Button_MouseListener());
		barricade_button.setBounds(1145, 275, 35, 50);
		add(barricade_button);
		
		JButton spawner_button = new JButton("Spawner");
		spawner_button.addMouseListener(new Button_MouseListener());
		spawner_button.setBounds(1100, 350, 75, 50);
		add(spawner_button);
	}

	class Button_MouseListener extends MouseAdapter {
		JButton button;

		public Button_MouseListener() {

		}

		public void mousePressed(MouseEvent e) {

			button = (JButton) e.getSource();
			if (button.getText().equals("Save")) {
				try {
					SaveNLoad.save("res/data/wall_info.txt", walls);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					SaveNLoad.save("res/data/barricade_info.txt", barricades);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					SaveNLoad.save("res/data/spawner_info.txt", spawners);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				repaint();
			}
			if (button.getText().equals("Load")) {
				try {
					walls = (ArrayList<wall>) SaveNLoad.load("res/data/wall_info.txt");
				} catch (Exception e1) {
					walls = new ArrayList<wall>();
					System.out.println("no wall file found");
				}
				try {
					barricades = (ArrayList<Barricade>) SaveNLoad.load("res/data/barricade_info.txt");
				} catch (Exception e1) {
					barricades = new ArrayList<Barricade>();
					System.out.println("no barricade file found");
				}
				try {
					spawners = (ArrayList<ZombieSpawner>)SaveNLoad.load("res/data/spawner_info.txt");
				} catch (Exception e1) {
					spawners = new ArrayList<ZombieSpawner>();
					System.out.println("no spawner file found");
				}
				repaint();
			}
			if (button.getText().equals("Reset")) {
				walls.clear();
				barricades.clear();
				spawners.clear();
				repaint();
			}
			if (button.getText().equals("Wall")) {
				whatToDraw = drawWall;
			}

			if (button.getText().equals("Barricade")) {
				whatToDraw = drawBarricade;

			}
			if(button.getText().equals("Spawner")){
				whatToDraw = drawSpawner;
			}

		}

		public void mouseEntered(MouseEvent e) {

			button = (JButton) e.getSource();

			if (button.getText().equals("Wall")) {
				floating_buttontext = "Draw wall";
				isEntered = true;
				mouseX = e.getX() + button.getX();
				mouseY = e.getY() + button.getY();
			}

			if (button.getText().equals("Barricade")) {
				floating_buttontext = "Draw barricade";
				isEntered = true;
				mouseX = e.getX() + button.getX();
				mouseY = e.getY() + button.getY();
			}
		}
		

		public void mouseExited(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			button = (JButton) e.getSource();

			if (button.getText().equals("Wall")) {
				isEntered = false;

			}

			if (button.getText().equals("Barricade")) {
				isEntered = false;

			}
		}

	}

	class MapEditor_KeyListener extends KeyAdapter {
		public MapEditor_KeyListener() {

		}

		public void KeyPressed(KeyEvent e) {
			if (e.getKeyChar() == 'a') {
				handler.currentwall.turn_wall(15);
				System.out.println("Turn");
			}
			if (e.getKeyChar() == 'd') {
				handler.currentwall.turn_wall(-15);
				System.out.println("Turn");
			}
		}

	}

	class MapEditor_MouseListener extends MouseAdapter {
		wall currentwall;
		boolean wasrightclick = false;

		public MapEditor_MouseListener() {
		}
		public void mouseWheelMoved(MouseWheelEvent e){
			if(currentwall != null){
				currentwall.turn_wall(e.getWheelRotation());
			}
		}
		

		public void mouseClicked(MouseEvent e){
			if(e.getButton() == MouseEvent.BUTTON1){
				if(whatToDraw == drawSpawner){
					spawners.add(new ZombieSpawner(e.getX(),e.getY()));
				}
			}
			else if(e.getButton() == MouseEvent.BUTTON3){
				for(int i = 0;i<spawners.size();i++){
					if(spawners.get(i).polygon.contains(e.getX(), e.getY())){
						spawners.remove(spawners.get(i));
					}
				}
			}
		}
		
		public void mousePressed(MouseEvent e) {
			drawing_new_rect = true;
			for (int i = 0; i < walls.size(); i++) {
				if (walls.get(i).get_Polygon().contains(e.getX(), e.getY())) {
					currentwall = walls.get(i);
					drawing_new_rect = false;
				}
			}
			for (int i = 0; i<barricades.size();i++){
				if( barricades.get(i).get_Polygon().contains(e.getX(), e.getY())){
					currentwall = barricades.get(i);
					drawing_new_rect = false;
				}
			}
			if (drawing_new_rect) {
				x = e.getX();
				y = e.getY();
			}

			if (e.getButton() == MouseEvent.BUTTON3) {
				wasrightclick = true;
				if (!drawing_new_rect) {
					if(currentwall instanceof Barricade){
						barricades.remove(currentwall);
					}
					else
						walls.remove(currentwall);
				}
			}

		}

		public void mouseDragged(MouseEvent e) {
			if (!wasrightclick) {
				if (drawing_new_rect) {

					max_x = Math.max(x, e.getX());
					max_y = Math.max(y, e.getY());
					min_x = Math.min(x, e.getX());	
					min_y = Math.min(y, e.getY());

					x_size = max_x - min_x;
					y_size = max_y - min_y;

					repaint();
				} else {
					currentwall.x_center = e.getX();
					currentwall.y_center = e.getY();
					repaint();
				}
			}

		}

		public void mouseReleased(MouseEvent e) {		
			if (!wasrightclick) {
				if (drawing_new_rect) {
					if (x_size / 2 > min_Size && y_size / 2 > min_Size) {
						if (whatToDraw == drawWall) {
							walls.add(new wall((max_x + min_x) / 2,
									(max_y + min_y) / 2, ((int) x_size / 2),
									((int) y_size / 2)));
						} 
						if(whatToDraw == drawBarricade) {
							barricades.add(new Barricade((max_x + min_x) / 2,
									(max_y + min_y) / 2, ((int) x_size / 2),
									((int) y_size / 2)));
						}
					}

					drawing_new_rect = false;
				} else {
					currentwall = null;
				}
			} else
				wasrightclick = false;

			x_size = 0;
			y_size = 0;
			max_x = 0;
			min_x = 0;
			max_y = 0;
			min_y = 0;
		}
	}
}
