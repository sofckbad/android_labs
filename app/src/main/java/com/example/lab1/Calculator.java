package com.example.lab1;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.mariuszgromada.math.mxparser.Expression;

public class Calculator extends Fragment {
	private EditText txt;
	private String buffer = "";
	private Double memoryValue = 0d;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_calculator, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		txt = getActivity().findViewById(R.id.result);
		txt.setInputType(0x00000000);
	}

	public void calculator_buttons(View view) {
		String chr = ((Button) view).getText().toString();

		switch (chr) {
			case "MC":
				memoryValue = 0d;
				break;
			case "M+":
				try {
					if (buffer.length() != 0)
						memoryValue += Double.parseDouble(buffer);
				} catch (NumberFormatException ignored) { }
				break;
			case "M-":
				try {
					if (buffer.length() != 0)
						memoryValue -= Double.parseDouble(buffer);
				} catch (NumberFormatException ignored) { }
				break;
			case "MR":
				long l = Math.round(memoryValue);
				buffer = (memoryValue == l)? ""+l:memoryValue.toString();
				txt.setText(buffer);
				txt.setSelection(txt.length());
				break;
			case "C":
				buffer = "";
				txt.setText(buffer);
				txt.setSelection(txt.length());
				break;
			case "R":
				int a = txt.getSelectionStart();
				if (a == 0)
				{
					txt.setSelection(txt.length());
					break;
				}
				buffer = buffer.substring(0, a-1) + buffer.substring(a);
				txt.setText(buffer);
				txt.setSelection(a-1);
				break;
			case "=":
				String s = result();
				buffer = (s.equals("NaN")?"ОШИБКА":s);
				txt.setText(buffer);
				txt.setSelection(txt.length());
				break;
			case "SIN":
				addText("sin(");
				break;
			case "COS":
				addText("cos(");
				break;
			default:
				addText(chr);
				break;
		}
	}

	void addText(String forInput) {
		buffer = buffer.substring(0, txt.getSelectionStart()) + forInput + buffer.substring(txt.getSelectionEnd());
		int i = txt.getSelectionStart();
		txt.setText(buffer);
		txt.setSelection(i+forInput.length());
	}

	String result() {
		Expression e = new Expression(buffer);
		double dbl = e.calculate();
		if (Math.round(dbl) == dbl)
			return "" + Math.round(dbl);
		return Double.toString(dbl);
	}
}