package models;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import enums.Direction;

public class AnimalModelG3 extends AnimalModelAbstract{
	private HashMap<Direction,ArrayList<BufferedImage>> animations;
	private int health;

	private int emptyHanded;
	private boolean isDead;
	private int height;
	private int width;


	
	public AnimalModelG3() {
		this.setHeight(100);
		this.setWidth(100);
	}
	@Override
	public void healthUp() {
		
		
	}

	@Override
	public void healthDown() {
		this.setHealth(0);
	}

	@Override
	public void pickUp() {
		
	}

	public HashMap<Direction,ArrayList<BufferedImage>> getAnimations() {
		return animations;
	}

	public void setAnimations(HashMap<Direction,ArrayList<BufferedImage>> animations) {
		this.animations = animations;
	}
	
	public void setHealth(int health){
		this.health = health;
	}
	
	public int getHealth(){
		return health;
	}
	
	@Override
	public void move() {
		System.out.println("Animal is at: (" + this.getLocX() + "," + this.getLocY());
		// TODO Auto-generated method stub
		switch(this.getCurrDir()){
			case NORTH:
				this.setLocY(this.getLocY() - 5);
				break;
			case SOUTH:
				this.setLocY(this.getLocY() + 5);
				break;
			case EAST:
				this.setLocX(this.getLocX() + 5);
				break;
			case WEST:
				this.setLocX(this.getLocX() - 5);
				break;
			case NORTH_EAST:
				this.setLocX(this.getLocX() + 5);
				this.setLocY(this.getLocY() - 5);
				break;
			case NORTH_WEST:
				this.setLocX(this.getLocX() - 5);
				this.setLocY(this.getLocY() - 5);
				break;
			case SOUTH_EAST:
				this.setLocX(this.getLocX() + 5);
				this.setLocY(this.getLocY() + 5);
				break;
			case SOUTH_WEST:
				this.setLocX(this.getLocX() - 5);
				this.setLocY(this.getLocY() + 5);
				break;
		}
		
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public Rectangle getBounds() {
		return (new Rectangle(this.getLocX(),this.getLocY(),20,20));
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
}
