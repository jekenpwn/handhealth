package com.hins.smartband.model;

public class Tb_device_m {
	private String name;
	private String address;
	public Tb_device_m(){
		super();
	}
	public Tb_device_m(String name,String address){
		super();
		this.name=name;
		this.address=address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	

}
