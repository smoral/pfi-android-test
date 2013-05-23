package com.example.myfirstapp;

@SuppressWarnings("rawtypes")
public class IndexDistance implements Comparable{

	private int index;
	private double distance;
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public IndexDistance(int index, double distance) {
		super();
		this.index = index;
		this.distance = distance;
	}
	@Override
	public int compareTo(Object o) {
		// +1 si this > 0
		IndexDistance obj = (IndexDistance)o;
		if(this.getDistance() > obj.getDistance())
			return 1;
		else if(this.getDistance() < obj.getDistance())
			return -1;
		else
			return 0;
	}
	@Override
	public boolean equals(Object obj) {
		IndexDistance other = (IndexDistance)obj;
		
		return this.getDistance() == other.getDistance()
			&& this.getIndex() == other.getIndex();
	}
	@Override
	public int hashCode() {
		return getIndex();
	}
}
