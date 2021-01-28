package com.example.lab1;

import androidx.annotation.ColorLong;
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
		boolean isNegative = false;
		int diff = 0;

		for (int i = 0; i < buffer.length(); i++)
		{
			if (buffer.charAt(i) == '(')
			{
				brackets++;
				if (i > 0)
					if (buffer.charAt(i-1) != '+' && buffer.charAt(i-1) != '-' && buffer.charAt(i-1) != '*'
							&& buffer.charAt(i-1) != '/' && buffer.charAt(i-1) != '(' && buffer.charAt(i-1) != 's' && buffer.charAt(i-1) != '(')
						return "MISS";
			}
			else if (buffer.charAt(i) == ')')
			{
				brackets--;
				if (i < buffer.length()-1)
				{
					if (buffer.charAt(i+1) != '+' && buffer.charAt(i+1) != '-' && buffer.charAt(i+1) != '*'
							&& buffer.charAt(i+1) != '/' && buffer.charAt(i+1) != ')')
						return "MISS";
				}
				if (isWasSign) return "MISS";
			}
			else if (buffer.charAt(i) > 47 && buffer.charAt(i) < 58)
			{
				if (isWasSign) diff++;
				isWasSign = false;
			}
			else if (buffer.charAt(i) == 's' || buffer.charAt(i) == 'c')
			{
				if (!isWasSign && i != 0) return "MISS";
			}
			else if (buffer.charAt(i) == '.')
			{
				if (isWasDot) return "MISS";
				isWasDot = true;
			}
			else if (buffer.charAt(i) == '*' || buffer.charAt(i) == '/' || buffer.charAt(i) == '+' || buffer.charAt(i) == '-')
			{
				if (buffer.charAt(i) == '-')
				{
					if (!isWasSign)
					{
						isWasSign = true;
						diff--;

					}
					else if (!isNegative) isNegative = true;
					else return "MISS";
				}
				else if (isWasSign) return "MISS";
				else
				{
					isWasSign = true;
					diff--;
				}
				isWasDot = false;
			}
		}

		if (brackets != 0 || diff != 1)
			return "MISS";

		return calculate(buffer);
	}

	String calculate(String buffer)
	{
		Stack<myCls> stack = new Stack<>();
		Stack<myCls> result = new Stack<>();

		boolean isWasSign = true;

		for (int i = 0, left = -1; i < buffer.length(); i++)
		{
			if (buffer.charAt(i) > 47 && buffer.charAt(i) < 58 || buffer.charAt(i) == '.')
			{
				if (isWasSign) left = i;
				isWasSign = false;
			}
			else if (buffer.charAt(i) == '*' || buffer.charAt(i) == '/' || buffer.charAt(i) == '+' || buffer.charAt(i) == '-')
			{
				if (isWasSign) left = i;
				else {
					result.push(new myCls(Double.parseDouble(buffer.substring(left, i))));
				}
				isWasSign = true;
			}
			else if (buffer.charAt(i) == '(')
			{
				stack.push(new myCls('('));
			}
			else if (buffer.charAt(i) == ')')
			{
				while (stack.peek().sign != '(')
				{

				}
				stack.pop();
			}
			else if (buffer.charAt(i) == 's')
			{

			}
			else if (buffer.charAt(i) == 'c')
			{

			}
		}

		return "TRUE";
	}
}