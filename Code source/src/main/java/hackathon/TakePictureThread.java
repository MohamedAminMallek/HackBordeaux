package hackathon;


import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.AttributedCharacterIterator;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.github.sarxos.webcam.Webcam;

public class TakePictureThread extends Thread{

	private boolean takes = false;
	private Webcam webcam;
	private long seuil;
	private long date;
	private long timeUp;
	
	public TakePictureThread(Webcam webcam, long seuil)
	{
		super();
		this.webcam = webcam;
		takes = false;
		date = new Date().getTime();
		this.seuil = seuil;
		timeUp = 0;
	}
	
	public boolean isTakes() {
		return takes;
	}

	public void setTakes(boolean takes) {
		this.takes = takes;
	}

	public void run() {
		
		ImageIcon icon,icon2,icon3;
		ImageIcon imageIcon,imageIcon2,imageIcon3;
		
		while(true)
		{
			
			try {
				BufferedImage temp = webcam.getImage();
							
				
				if(takes) {
					String path = "./pictures/"+(Interface.imageID++)+".png";
					File file = new File(path);
					//if(temp!=null)
						ImageIO.write(temp, "PNG", file);
						System.out.println(temp);
					
					Process p = Runtime.getRuntime().exec("python traiter_image.py "+path);
					
					BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String ret = in.readLine();
					System.out.println(ret);
					boolean posture = true;
					boolean distance = true;
					boolean brightness = true;
					if(ret != null)
					{
						posture = ret.split(";")[0].compareTo("True") == 0;
						distance = ret.split(";")[1].compareTo("True") == 0;
						brightness = ret.split(";")[2].compareTo("True") == 0;
						Main.posture.add(posture);
						Main.distance.add(distance);
						Main.brightness.add(brightness);
						long tmp = new Date().getTime();
						timeUp += (tmp - date)/1000;
						if (timeUp >= seuil) {
							ImageIcon iconBreak = new ImageIcon("./assets/takeBreak.jpg");
							JFrame pop = new JFrame("POPUP");
							JOptionPane.showMessageDialog(pop, "","Take a break",0,iconBreak);
							timeUp = 0;
							
							
						}
							
					}

					
					if(!posture) {
						icon = new ImageIcon("./assets/posturewrong.png");
					}else
						icon = new ImageIcon("./assets/posturegood.png");
					
					if(!distance) {
						icon2 = new ImageIcon("./assets/screendistancewrong.png");
					}else
						icon2 = new ImageIcon("./assets/screendistancdgood.png");
					
					if(!brightness) {
						icon3 = new ImageIcon("./assets/badB.jpg");
					}else
						icon3 = new ImageIcon("./assets/luminoqstygood.png");
					
					
					imageIcon = new ImageIcon(icon.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT));
					Interface.labelStatusPosture.setIcon(imageIcon);
					
					imageIcon2 = new ImageIcon(icon2.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT));
					Interface.labelStatusDistance.setIcon(imageIcon2);
					
					imageIcon3 = new ImageIcon(icon3.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT));
					Interface.labelStatusBrightness.setIcon(imageIcon3);
					
					temp = ImageIO.read(new File(path));
					//file.delete();
				}
				
				Interface.imageJpanel.setImage(temp);
				Interface.imageJpanel.invalidate();
				Interface.imageJpanel.repaint();
				
			} catch (Exception e1) 
			{
				System.out.println(e1.getMessage());
				e1.printStackTrace();
			}
			/*
			try {
				this.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
			*/
			date = new Date().getTime();
		}

	}

}
