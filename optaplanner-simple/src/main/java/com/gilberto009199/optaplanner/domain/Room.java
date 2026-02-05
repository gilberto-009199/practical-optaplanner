package com.gilberto009199.optaplanner.domain;

public class Room {
	
	private String name;
	
	public Room() {}
	
	public Room(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
