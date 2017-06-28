package com.hins.smartband.model;

public class Tb_bind_device_m {
	private String nameOne;
	private String addressOne,addressTwo;
	
	public Tb_bind_device_m(){
		super();
	}
	public Tb_bind_device_m(String nameone,String addressOne,String addressTwo){
		super();
		this.nameOne=nameone;
		this.addressOne=addressOne;
		this.addressTwo=addressTwo;
	}
	public String getNameOne() {
		return nameOne;
	}
	public void setNameOne(String nameOne) {
		this.nameOne = nameOne;
	}
	public String getAddressOne() {
		return addressOne;
	}
	public void setAddressOne(String addressOne) {
		this.addressOne = addressOne;
	}
	public String getAddressTwo() {
		return addressTwo;
	}
	public void setAddressTwo(String addressTwo) {
		this.addressTwo = addressTwo;
	}
	

}
