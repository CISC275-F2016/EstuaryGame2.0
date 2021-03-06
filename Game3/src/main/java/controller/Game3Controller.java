package controller;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import javax.swing.Timer;

import enums.Waves;
import models.AnimalModelG3;
import models.BeachModel;
import models.ConcretePUModel.ConcPUState;
import models.GridBlock;
import models.SunHurricaneModel;
import models.WallModelAbstract;
import models.WaterModel;
import models.WaveModel;
import models.GabionPUModel.GabPUState;
import view.Game3View;

public class Game3Controller implements KeyListener {
	private boolean gameActive;
	private Game3View view;
	private AnimalModelG3 animal;
	private BeachModel beach;
	private GridBlock sandPatch;
	private WaterModel water;
	private SunHurricaneModel sun;
	private SunHurricaneModel hurricane;
	private Timer timer;
	private long startTime;
	private int updates = 0;
	private int frames = 0;	
	
	
	public Game3Controller() {
		AnimalModelG3 a = new AnimalModelG3();
		a.setLocX(250);
		a.setLocY(250);
		setAnimal(a);
		setBeach(new BeachModel());
		setSandPatch(new GridBlock(beach));
		setWater(new WaterModel());
		view = new Game3View(this);
		
		SunHurricaneModel sun = new SunHurricaneModel(this.view.getTimePanel());
		sun.setInitialPosition(200);
		SunHurricaneModel hurricane = new SunHurricaneModel(this.view.getTimePanel());
		hurricane.setInitialPosition(200);
		setSun(sun);
		setHurricane(hurricane);
		view.addSun();
		view.addHurricane();
		this.startTime();
	}
	
	public void runGame()  {
		this.setGameActive(true);
		startTime = System.currentTimeMillis();
		long lastTime = System.nanoTime();
		final double ammountOfTicks = 60.0;	
		double ns = 1000000000 /ammountOfTicks;
		double delta = 0;
		long timer2 = System.currentTimeMillis();
		this.genWaveTimer();
		Random die = new Random();
		int triggerSpawn = 4;
		while(getgameActive()) {
			long now = System.nanoTime();
			delta += (now-lastTime)/ns;
			lastTime=now;
			if(delta>=1){
				animal.tick();
				view.repaintAll();
				updates++;
				delta--;
			}
			frames++;
			if(System.currentTimeMillis()-timer2>1000){
				timer2 +=1000;
				updates = 0;
				frames = 0;
			}

			if(triggerSpawn == die.nextInt(700000)) {
				if(beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getConcrPU().getIsActive() == false && beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().getIsActive() == false) {
					getBeach().spawnConcrPU(getBeach().generatePPUL());
					getBeach().spawnGabPU(getBeach().generatePPUL());
					this.powerUpSpawned();
				}	
			}
			
			if((beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair()))).getConcrPU().getIsActive() && beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().getIsActive()); {
				this.collisionPowerUps();
			}
			
			this.collisionTile();
			this.view.repaintAll();
			
		}
	
	}

	
	
	
	ActionListener powerUpSpawnTimerListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			beach.removeGabPU(beach.findPairInGrid(beach.getGabPair()));
			beach.removeConcrPU(beach.findPairInGrid(beach.getConcPair()));
			
			beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().setPickedUp(false);
			beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().setIsActive(false);
			if(!beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getWater().isActive()) {
				beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).setVacant(true);
			}
			
			
			beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getConcrPU().setActive(false);
			beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getConcrPU().setPickedUp(false);
			if(!beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getWater().isActive()) {
				beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).setVacant(true);
			}
			
			
			Object time = e.getSource();
			Timer myTime = (Timer) time;
			myTime.stop();
		}
	};
	
	ActionListener powerUpWallTimerListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
				
			if (beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().getWallState() == GabPUState.WALL) {
				beach.removeGabPU(beach.findPairInGrid(beach.getGabPair()));
				beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().setPickedUp(false);
				beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().setIsActive(false);
				if(!beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getWater().isActive()) {
					beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).setVacant(true);
				}
				
				System.out.println("Wall Timer stopped");
			}
			else {
				beach.removeConcrPU(beach.findPairInGrid(beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getConcrPU().getLocation()));
				beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getConcrPU().setActive(false);
				beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getConcrPU().setPickedUp(false);
				if(!beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getWater().isActive()) {
					beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).setVacant(true);
				}
				
				System.out.println("Wall Timer stopped");
			}
			Object time = e.getSource();
			Timer myTime = (Timer) time;
			myTime.stop();
		}
	};
	
	
	
	
	//Duration for which power-up is available to be picked up
	public void powerUpSpawned() {
		timer = new Timer(3000, powerUpSpawnTimerListener);
		System.out.println("Gabion is at: (" + beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().getViewLocation().getX() +", " + beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().getViewLocation().getY() + ")");
		System.out.println("Concrete is at:(" + beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getConcrPU().getViewLocation().getX() +", " + beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getConcrPU().getViewLocation().getY() + ")");
		
		timer.setRepeats(true);
		timer.start();
		System.out.println("Spawn timer started");
	}
	
	
	
	
	//Duration for which power-up is in wall form
	public void powerUpPickedUp() {
		if (beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().getWallState() == GabPUState.WALL) { 
			timer = new Timer(5000, powerUpWallTimerListener);
		}
		else {
			timer = new Timer(1000, powerUpWallTimerListener);
		}
		timer.setRepeats(true);
		timer.start();
		System.out.println("Wall timer started");
	}
	
	public void collisionPowerUps(){
		if ((beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getConcrPU().getIsActive()) & beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getConcrPU().isPickedUp() == false) {
			if (animal.getBounds().contains(beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getConcrPU().getBounds())) {
				System.out.println("Intersection between concrete and animal");
				timer.stop();
				beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getConcrPU().setPickedUp(true);
				beach.removeGabPU(beach.findPairInGrid(beach.getGabPair()));
				beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().setIsActive(false);
				beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).setVacant(true);
				
				beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getConcrPU().setPickedUp(true);
				this.powerUpPickedUp();
				return;
			}
		}
		if((beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().getIsActive()) &  beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().isPickedUp() == false) {
			if (animal.getBounds().contains(beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().getBounds())) {
				System.out.println("Intersection between gab and animal");
				timer.stop();
				beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().setPickedUp(true);
				beach.removeConcrPU(beach.findPairInGrid(beach.getConcPair()));
				beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).getConcrPU().setActive(false);
				beach.getBeachGrid().get(beach.findPairInGrid(beach.getConcPair())).setVacant(true);
				
				beach.getBeachGrid().get(beach.findPairInGrid(beach.getGabPair())).getGabPU().setPickedUp(true);
				this.powerUpPickedUp();
				return;
			}
		}
	}
	
	ActionListener gameTimerListener = new ActionListener() {
		public int timeElapsed;

		@Override
		public void actionPerformed(ActionEvent e) {
			Timer t = (Timer) e.getSource();
			timeElapsed += t.getDelay();
			if (timeElapsed < 120000) {
				sun.move();
				hurricane.move();
			}
			else {
				gameActive = false;
				timer.stop();
			}
		}
	};
	
	
	
	public void startTime() {
		timer = new Timer(220, gameTimerListener);
		
		timer.setRepeats(true);
		timer.start();
	}

	//Can change later for different levels of difficulty
	ActionListener genWaveTimer = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			Timer t = (Timer) e.getSource();
			if(!gameActive) {
				t.stop();
			}
			else {
				view.generateWaveCluster();
			}
		}
	};
	
	public void genWaveTimer() {
		Timer waveTimer = new Timer(4000, genWaveTimer);
		
		waveTimer.setRepeats(true);
		waveTimer.start();
	}
	
	public void collisionTile() {
		/*Collection<GridBlock> sandPatches = this.getBeach().getBeachGrid().values();
		for(GridBlock gb : sandPatches) {
			//System.out.println("Location gb: " + "("+gb.getLocation().getX() + ","+gb.getLocation().getY() +")");
			//System.out.println("Location animal: " + "(" + animal.getBounds().getX() + "," + animal.getBounds().getY() + ")");
			if(this.getAnimal().getBounds().intersects(gb.getBounds())) {
				if(gb.getWater().isActive()) {
					System.out.println("It's a hit!");
					this.getAnimal().setSpeedX(0);
					this.getAnimal().setSpeedY(0);
				}
			}
		}*/
	}

	public void collisionDetectionLoop(){
		
	}
	
	
	
	public void stopTime() {
		
	}

	
	public boolean getgameActive() {
		return gameActive;
	}
	public void setGameActive(boolean active) {
		gameActive = active; 

}

	public int setTime(int i) {
		return i;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public AnimalModelG3 getAnimal() {
		return animal;
	}

	public void setAnimal(AnimalModelG3 animal) {
		this.animal = animal;
	}


	public WaterModel getWater() {
		return water;
	}

	public void setWater(WaterModel water) {
		this.water = water;
	}

	public BeachModel getBeach() {
		return beach;
	}

	public void setBeach(BeachModel beach) {
		this.beach = beach;
	}
	
	public GridBlock getSandPatch() {
		return sandPatch;
	}

	public void setSandPatch(GridBlock sandPatch) {
		this.sandPatch = sandPatch;
	}

	public SunHurricaneModel getSun() {
		return sun;
	}

	public void setSun(SunHurricaneModel sun) {
		this.sun = sun;
	}

	public SunHurricaneModel getHurricane() {
		return hurricane;
	}

	public void setHurricane(SunHurricaneModel hurricane) {
		this.hurricane = hurricane;
	}
}
