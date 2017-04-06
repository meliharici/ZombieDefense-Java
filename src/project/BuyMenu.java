package project;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

public class BuyMenu extends JPanel {
	JButton flame_thrower_button;
	JButton flame_thrower_ammo_button;
	JButton bow_button;
	JButton arrow_button;
	JButton return_button;
	static BuyMenu buymenu = new BuyMenu();
	public BuyMenu(){
		setLayout(null);
		setSize(400,400);
		setBounds(0,0,400,200);
		
		initializeButtons();
		
	}
	public void initializeButtons(){
		flame_thrower_button = new JButton("Buy Flame-Thrower - "+Game_data.Flame_Thrower_Price+"$");
		flame_thrower_button.setBounds(10, 10, 300, 30);
		flame_thrower_button.addMouseListener(new BuyMenu_MouseListener());
		add(flame_thrower_button);
		
		flame_thrower_ammo_button = new JButton("Buy Flame-Thrower Ammo(1000) - "+Game_data.Flame_Thrower_Ammo_Price+"$");
		flame_thrower_ammo_button.setBounds(10, 40, 300, 30);
		flame_thrower_ammo_button.addMouseListener(new BuyMenu_MouseListener());
		add(flame_thrower_ammo_button);
		
		bow_button = new JButton("Buy Bow - "+Game_data.Bow_Price+"$");
		bow_button.setBounds(10, 90, 300, 30);
		bow_button.addMouseListener(new BuyMenu_MouseListener());
		add(bow_button);
		
		arrow_button = new JButton("Buy Arrow(5) - "+Game_data.Arrow_Price+"$");
		arrow_button.setBounds(10, 130, 300, 30);
		arrow_button.addMouseListener(new BuyMenu_MouseListener());
		add(arrow_button);
		
		return_button = new JButton("Return");
		return_button.setBounds(10, 170, 300, 30);
		return_button.addMouseListener(new BuyMenu_MouseListener());
		add(return_button);
	}

	
	class BuyMenu_MouseListener extends MouseAdapter{
		public void mouseClicked(MouseEvent e){
			if(e.getSource() instanceof JButton){
				JButton button = (JButton)e.getSource();
				
				if(button.getText().equals("Buy Flame-Thrower - "+Game_data.Flame_Thrower_Price+"$")){
					if(!Game_data.player.hasFlameThrower && Game_data.player.money>Game_data.Flame_Thrower_Price){
						Game_data.player.hasFlameThrower = true;
						button.setBackground(Color.red);
						Game_data.player.money -= Game_data.Flame_Thrower_Price;
					}
					
				}
				else if(button.getText().equals("Buy Flame-Thrower Ammo(1000) - "+Game_data.Flame_Thrower_Ammo_Price+"$")){
					if(Game_data.player.money>Game_data.Flame_Thrower_Ammo_Price){
						Game_data.player.flameThrowerammo += 1000;
						Game_data.player.money -= Game_data.Flame_Thrower_Ammo_Price;
					}
				}
				else if(button.getText().equals("Buy Bow - "+Game_data.Bow_Price+"$")){
					if(!Game_data.player.hasBow && Game_data.player.money>Game_data.Bow_Price){
						Game_data.player.hasBow = true;
						Game_data.player.money -= Game_data.Bow_Price;
						button.setBackground(Color.red);
					}
				}
				else if(button.getText().equals("Buy Arrow(5) - "+Game_data.Arrow_Price+"$")){
					if(Game_data.player.money>Game_data.Arrow_Price){
						Game_data.player.arrowCount += 5;
						Game_data.player.money -= Game_data.Arrow_Price;
					}
				}
				else if(button.getText().equals("Return")){
					Main_ButtonListener.current_panel = Main.game;
					Main.game.remove(BuyMenu.buymenu);
					Main.game.requestFocusInWindow();
					Main.frame.add(Main.game);
				}
			}
		}
	}
}
