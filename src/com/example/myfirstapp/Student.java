package com.example.myfirstapp;


public class Student{

	/*Obtener por medio de un xml (XStream), al enviar los k lu más cercanos
	 * <students>
	 *  <student>
	 *  	<firstName>Mauro Gabriel</firstName>
	 *  	<lastName>Ponce</lastName>
	 *  	<LU>131445</LU>
	 *  	<encodedImage>haskjdhasiugbqkeqwhekqwbejamnasad...</encodedImage>
	 *  </student>
	 *  ...
	 * </students>*/
	
	private String firstName;
	private String lastName;
	private Integer LU;
	private String encodedImage;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public Integer getLU() {
		return LU;
	}
	public void setLU(Integer lU) {
		LU = lU;
	}
	
	public String getEncodedImage() {
		return encodedImage;
	}
	public void setEncodedImage(String encodedImage) {
		this.encodedImage = encodedImage;
	}
	public String getFullName(){
		return lastName + ", " + firstName;
	}
}
