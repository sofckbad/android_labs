package com.example.lab1;

public class myCls
{
	public double val = 0;
	public boolean isSign = false;
	public char sign = 0;

	public myCls(double val)
	{
		this.val = val;
	}
	public myCls(char sign)
	{
		this.sign = sign;
		this.isSign = true;
	}
}
