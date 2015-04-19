package com.example.deema.claculator1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import java.text.DecimalFormat;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView display;
    private Boolean userIsStillWriting = false;
    private Calculate calculate;
    private static final String DIGITS = "0123456789.";

    DecimalFormat df = new DecimalFormat("@###########");

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // hide the status bar and other OS-level chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculate = new Calculate();
        display = (TextView) findViewById(R.id.textView1);

        df.setMinimumFractionDigits(0);
        df.setMinimumIntegerDigits(1);
        df.setMaximumIntegerDigits(8);

        findViewById(R.id.button0).setOnClickListener(this);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);
        findViewById(R.id.buttonAdd).setOnClickListener(this);
        findViewById(R.id.buttonSubtract).setOnClickListener(this);
        findViewById(R.id.buttonMultiply).setOnClickListener(this);
        findViewById(R.id.buttonDivide).setOnClickListener(this);
        findViewById(R.id.buttonToggleSign).setOnClickListener(this);
        findViewById(R.id.buttonEquals).setOnClickListener(this);
        findViewById(R.id.buttonClear).setOnClickListener(this);
        findViewById(R.id.buttonClearMemory).setOnClickListener(this);
        findViewById(R.id.buttonAddToMemory).setOnClickListener(this);
        findViewById(R.id.buttonRecallMemory).setOnClickListener(this);
        Button backspace=(Button)findViewById(R.id.back);
        backspace.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String str= display.getText().toString();
                if (str.length() >1 ) {
                    str = str.substring(0, str.length() - 1);
                    display.setText(str);
                }
                else if (str.length() <=1 ) {
                    display.setText("0");
                }
            }

        });
    }

    @Override
    public void onClick(View v) {

        String buttonPressed = ((Button) v).getText().toString();

        if (DIGITS.contains(buttonPressed)) {
            // digit was pressed
            if (userIsStillWriting) {
                if (buttonPressed.equals(".") && display.getText().toString().contains(".")) {
                    // ERROR PREVENTION
                    // Eliminate entering multiple decimals
                } else {
                    display.append(buttonPressed);
                }

            } else {

                if (buttonPressed.equals(".")) {
                    // ERROR PREVENTION
                    // This will avoid error if only the decimal is hit before an operator, by placing a leading zero
                    // before the decimal
                    display.setText(0 + buttonPressed);
                } else {
                    display.setText(buttonPressed);
                }

                userIsStillWriting = true;
            }

        } else {
            // operation was pressed
            if (userIsStillWriting) {

                calculate.setOperand(Double.parseDouble(display.getText().toString()));
                userIsStillWriting = false;
            }

            calculate.performOperation(buttonPressed);
            display.setText(df.format(calculate.getResult()));

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save variables on screen orientation change
        outState.putDouble("OPERAND", calculate.getResult());
        outState.putDouble("MEMORY", calculate.getMemory());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore variables on screen orientation change
        calculate.setOperand(savedInstanceState.getDouble("OPERAND"));
        calculate.setMemory(savedInstanceState.getDouble("MEMORY"));
        display.setText(df.format(calculate.getResult()));
    }
    public class Calculate {
        private double mOperand;
        private double mWaitingOperand;
        private String mWaitingOperator;
        private double mCalculatorMemory;

        // operator types
        public static final String ADD = "+";
        public static final String SUBTRACT = "-";
        public static final String MULTIPLY = "*";
        public static final String DIVIDE = "/";
        public static final String CLEAR = "C" ;
        public static final String CLEARMEMORY = "MC";
        public static final String ADDTOMEMORY = "M+";
        public static final String RECALLMEMORY = "MR";
        public static final String TOGGLESIGN = "N";

        // constructor
        public Calculate() {
            // initialize variables upon start
            mOperand = 0;
            mWaitingOperand = 0;
            mWaitingOperator = "";
            mCalculatorMemory = 0;
        }

        public void setOperand(double operand) {
            mOperand = operand;
        }

        public double getResult() {
            return mOperand;
        }

        // used on screen orientation change
        public void setMemory(double calculatorMemory) {
            mCalculatorMemory = calculatorMemory;
        }

        // used on screen orientation change
        public double getMemory() {
            return mCalculatorMemory;
        }

        public String toString() {
            return Double.toString(mOperand);
        }

        protected double performOperation(String operator) {

            if (operator.equals(CLEAR)) {
                mOperand = 0;
                mWaitingOperator = "";
                mWaitingOperand = 0;
                // mCalculatorMemory = 0;
            } else if (operator.equals(CLEARMEMORY)) {
                mCalculatorMemory = 0;
            } else if (operator.equals(ADDTOMEMORY)) {
                mCalculatorMemory = mCalculatorMemory + mOperand;
                mOperand = 0;
                mWaitingOperator = "";
                mWaitingOperand = 0;

            }  else if (operator.equals(RECALLMEMORY)) {
                mOperand = mCalculatorMemory;

            } else if (operator.equals(TOGGLESIGN)) {
                mOperand = -mOperand;
            }  else {
                performWaitingOperation();
                mWaitingOperator = operator;
                mWaitingOperand = mOperand;
            }

            return mOperand;
        }

        protected void performWaitingOperation() {

            if (mWaitingOperator.equals(ADD)) {
                mOperand = mWaitingOperand + mOperand;
            } else if (mWaitingOperator.equals(SUBTRACT)) {
                mOperand = mWaitingOperand - mOperand;
            } else if (mWaitingOperator.equals(MULTIPLY)) {
                mOperand = mWaitingOperand * mOperand;
            } else if (mWaitingOperator.equals(DIVIDE)) {
                if (mOperand != 0) {
                    mOperand = mWaitingOperand / mOperand;
                }
            }

        }
    }

}