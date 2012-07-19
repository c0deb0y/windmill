package com.prezerak.windmill.model;


//Just a data transfer object
public class Wind {	
	public float direction=0;
	public char reference='R'; //allowed values (R) "Relative", (T) "True"
	public float vel=0;
	public char units='M'; //allowed values (N) "Knots," (K) "Km per Hour," and (M) "Meters per Second
	public long timeMills;
}
