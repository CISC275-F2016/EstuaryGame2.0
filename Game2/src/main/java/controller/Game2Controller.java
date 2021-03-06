package controller;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;



import models.AlgaeEaterModel;
import models.AlgaeModel;
import models.AnimalModelG2;
import models.WaterModelG2;

import view.Game2View;

public class Game2Controller {
	private boolean gameActive;
	private Game2View view;
	private AnimalModelG2 animal;
	private AlgaeEaterModel algaeEater;
	private AlgaeModel algae;
	private Collection<AlgaeModel> algaeList = new ArrayList<AlgaeModel>();
	private WaterModelG2 water;
	long spawnTime=0;
	int numMissed = 0;
	long startTime;
	int updates = 0;
	int frames = 0;
	
	int spawnDelay = 2000; //in milliseconds
	boolean isStorming = false;
	
	public Game2Controller() {
		animal = new AnimalModelG2();
		water = new WaterModelG2();
		algae = new AlgaeModel();
		algaeEater = new AlgaeEaterModel();
		view = new Game2View(this);
		view.addController(this);
		
	}

	
	
	public void startGame() {
		gameActive = true;
		startTime = System.currentTimeMillis();
		long lastTime = System.nanoTime();
		final double ammountOfTicks = 60.0;	
		double ns = 1000000000 /ammountOfTicks;
		double delta = 0;
		
		long stormTimer = System.currentTimeMillis();
		
		while(gameActive){
			long now = System.nanoTime();
			delta += (now-lastTime)/ns;
			lastTime=now;
			if(delta>=1){
				animal.tick();
				view.repaintFrame();
				updates++;
				delta--;
			}
			
			frames++;
			collisionDetection();
			
			if(System.currentTimeMillis()-stormTimer>10000){
				stormTimer+=10000;
				if(getStormStatus()==true){
					deactivateStorm();
				}
				else{
					activateStorm();
				}
				
			}
			
			
			
			
			if(algaeList.size()<algae.getMaxAlgae()){
				if(System.currentTimeMillis()>=spawnTime+getSpawnDelay()){
				spawnAlgae();
				spawnTime = System.currentTimeMillis();
				
				}
			}
		}
	}
	
	
	public boolean getStormStatus(){
		return isStorming;
	}
	public void activateStorm(){
	
		isStorming = true;
		setSpawnDelay(getSpawnDelay()-1500);
	}
	public void deactivateStorm(){
		
		isStorming = false;
		setSpawnDelay(getSpawnDelay()+1500);
	}
	public int getSpawnDelay(){
		return spawnDelay;
	}
	public void setSpawnDelay(int delay){
		spawnDelay = delay;
	}
	
	public long getGameTime(){
		return (System.currentTimeMillis()-startTime)/1000;
	}
	
	public void addNumMissed(){
		numMissed++;
	}
	public int getNumMissed(){
		return numMissed;
	}
	
	public void spawnAlgae(){
		AlgaeModel newAlgae = new AlgaeModel();
		newAlgae.spawnAlgaeModel();
		algaeList.add(newAlgae);
	}
	
	public Collection<AlgaeModel> getAlgaeList() {
		return algaeList;
	}

	public boolean getgameActive() {
		return gameActive;
	}
	
	public void setGameActive(boolean active) {
		gameActive = active; 
	}
	
	public boolean collisionOccured(AnimalModelG2 animal, AlgaeModel algae){
		
		Rectangle algae_rect = new Rectangle(algae.getLocX(), algae.getLocY(), algae.getWidth(), algae.getHeight());
		Rectangle animal_rect = new Rectangle(animal.getLocX(), animal.getY(), animal.getWidth(), animal.getHeight());
		
		if(animal_rect.getBounds().intersects(algae_rect)){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean shallowWaterCollision(AlgaeModel algae){
		if(algae.getLocX()<=0){
			return true;
		}
		return false;
	}
	
	public void collisionDetection(){
		
		Collection<AlgaeModel> algaeList = getAlgaeList();
		
		Iterator<AlgaeModel> it = algaeList.iterator();
		
		while(it.hasNext()){
			
			AlgaeModel tmp = it.next();
			
			if(tmp.isActive()){
				if(collisionOccured(animal, tmp)){
					tmp.eaten();
					
				}
				if(shallowWaterCollision(tmp)){
					tmp.eaten();
					addNumMissed();
				}	
			}
		}		
	}

	public AnimalModelG2 getAnimal() {
		return animal;
	}

	public void setAnimal(AnimalModelG2 animal) {
		this.animal = animal;
	}

	

	public WaterModelG2 getWater() {
		return water;
	}

	public void setWater(WaterModelG2 water) {
		this.water = water;
	}
	
	public AlgaeModel getAlgae() {
		return algae;
	}

	public void setAlgae(AlgaeModel algae) {
		this.algae = algae;
	}
	public AlgaeEaterModel getAlgaeEater() {
		return algaeEater;
	}

	public void setAlgaeEater(AlgaeEaterModel ae) {
		this.algaeEater = ae;
	}

	public AnimalModelG2 getAnimalModelG2() {
		return this.animal;
	}



	

	
}