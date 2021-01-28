package com.example.lab1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity
{
	private TextView txt;
	private String buffer = "";
	ArrayList<myCls> arr = new ArrayList<>();
	private Double memoryValue = 0d;
	private boolean isExistsMemory = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		txt = findViewById(R.id.result);
	}



	public void clickFunction(View view) {
		String chr = ((Button) view).getText().toString();

		switch (chr)
		{
			case "MC":
				memoryValue = 0d;
				isExistsMemory = false;
				break;
			case "M+":
				memoryValue += Double.parseDouble(0+buffer);
				isExistsMemory = true;
				break;
			case "M-":
				memoryValue -= Double.parseDouble(0+buffer);
				isExistsMemory = true;
				break;
			case "MR":
				if (isExistsMemory)
					buffer = "" + memoryValue;
				break;
			case "C":
				buffer = "";
				break;
			case "CLR":
				if (buffer.length() > 1) buffer = buffer.substring(0, buffer.length() - 1);
				else buffer = "";
				break;
			case "SIN":
				buffer += "s";
				break;
			case "COS":
				buffer += "c";
				break;
			case "=":
				buffer = result(buffer);
				break;
			default:
				buffer += chr;
				break;
		}

		txt.setText(buffer);
	}

	String result(String buffer)
	{
		int brackets = 0;
		boolean isWasSign = true;
		boolean isWasDot = false;

		for (int i = 0; i < buffer.length(); i++)
		{
			if (buffer.charAt(i) == '(')
			{
				brackets++;
				if (i > 0)
					if (buffer.charAt(i-1) != '+' && buffer.charAt(i-1) != '-' && buffer.charAt(i-1) != '*'
							&& buffer.charAt(i-1) != '/' && buffer.charAt(i-1) != '(')
						return "MISS";
			}
			else if (buffer.charAt(i) == ')')
			{
				brackets--;
				if (i < buffer.length()-1)
					if (buffer.charAt(i+1) != '+' && buffer.charAt(i+1) != '-' && buffer.charAt(i+1) != '*'
							&& buffer.charAt(i+1) != '/' && buffer.charAt(i+1) != ')')
						return "MISS";

			}
//			else if (buffer.charAt(i) > 47 && buffer.charAt(i) < 58)
		}

		if (brackets != 0)
			return "MISS";

		return "";
	}
}