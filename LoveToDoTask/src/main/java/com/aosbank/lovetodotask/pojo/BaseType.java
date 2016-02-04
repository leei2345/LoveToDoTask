package com.aosbank.lovetodotask.pojo;

public enum BaseType {
	mcl15(15),
	mcl13(13),
	;
	
	public int num;
	private BaseType (int num) {
		this.num = num;
	}
	
}