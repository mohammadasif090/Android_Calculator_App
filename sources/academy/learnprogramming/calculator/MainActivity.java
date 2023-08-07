package academy.learnprogramming.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/* loaded from: classes.dex */
public class MainActivity extends AppCompatActivity {
    private static final String operationState = "pendingOperation";
    private TextView displayOperation;
    private EditText newNumber;
    private Double operand1 = null;
    private Double operand2 = null;
    private String pendingOperation = "=";
    private EditText result;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(operationState, this.displayOperation.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.displayOperation.setText(savedInstanceState.getString(operationState));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.newNumber = (EditText) findViewById(R.id.newNumber);
        this.result = (EditText) findViewById(R.id.result);
        this.displayOperation = (TextView) findViewById(R.id.operation);
        this.displayOperation.setText("");
        Button button0 = (Button) findViewById(R.id.buttonZero);
        Button button1 = (Button) findViewById(R.id.buttonOne);
        Button button2 = (Button) findViewById(R.id.buttonTwo);
        Button button3 = (Button) findViewById(R.id.buttonThree);
        Button button4 = (Button) findViewById(R.id.buttonFour);
        Button button5 = (Button) findViewById(R.id.buttonFive);
        Button button6 = (Button) findViewById(R.id.buttonSix);
        Button button7 = (Button) findViewById(R.id.buttonSeven);
        Button button8 = (Button) findViewById(R.id.buttonEight);
        Button button9 = (Button) findViewById(R.id.buttonNine);
        Button buttonDot = (Button) findViewById(R.id.buttonDot);
        Button buttonMultiply = (Button) findViewById(R.id.buttonMultiply);
        Button buttonDevide = (Button) findViewById(R.id.buttonDevide);
        Button buttonPlus = (Button) findViewById(R.id.buttonPLus);
        Button buttonMinus = (Button) findViewById(R.id.buttonMinus);
        Button buttonEquals = (Button) findViewById(R.id.buttonEquals);
        Button buttonNeg = (Button) findViewById(R.id.buttonNeg);
        Button buttonClear = (Button) findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() { // from class: academy.learnprogramming.calculator.MainActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                MainActivity.this.newNumber.setText("");
                MainActivity.this.result.setText("");
                MainActivity.this.operand1 = null;
                MainActivity.this.operand2 = null;
                MainActivity.this.pendingOperation = "=";
                MainActivity.this.displayOperation.setText("");
            }
        });
        View.OnClickListener listener = new View.OnClickListener() { // from class: academy.learnprogramming.calculator.MainActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Button b = (Button) v;
                MainActivity.this.newNumber.append(b.getText().toString());
            }
        };
        View.OnClickListener opListener = new View.OnClickListener() { // from class: academy.learnprogramming.calculator.MainActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Button b = (Button) v;
                String op = b.getText().toString();
                String value = MainActivity.this.newNumber.getText().toString();
                try {
                    Double doubleVallue = Double.valueOf(value);
                    MainActivity.this.performOperation(doubleVallue, op);
                } catch (NumberFormatException e) {
                    MainActivity.this.newNumber.setText("");
                }
                MainActivity.this.pendingOperation = op;
                MainActivity.this.displayOperation.setText(MainActivity.this.pendingOperation);
            }
        };
        button0.setOnClickListener(listener);
        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);
        button3.setOnClickListener(listener);
        button4.setOnClickListener(listener);
        button5.setOnClickListener(listener);
        button6.setOnClickListener(listener);
        button7.setOnClickListener(listener);
        button8.setOnClickListener(listener);
        button9.setOnClickListener(listener);
        buttonDot.setOnClickListener(listener);
        buttonMultiply.setOnClickListener(opListener);
        buttonDevide.setOnClickListener(opListener);
        buttonPlus.setOnClickListener(opListener);
        buttonMinus.setOnClickListener(opListener);
        buttonEquals.setOnClickListener(opListener);
        buttonNeg.setOnClickListener(new View.OnClickListener() { // from class: academy.learnprogramming.calculator.MainActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                try {
                    Double doubleValue = Double.valueOf(MainActivity.this.newNumber.getText().toString());
                    MainActivity.this.newNumber.setText(Double.valueOf(doubleValue.doubleValue() * (-1.0d)).toString());
                } catch (NumberFormatException e) {
                    MainActivity.this.newNumber.setText("");
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performOperation(Double value, String operation) {
        if (this.operand1 == null) {
            this.operand1 = value;
        } else {
            this.operand2 = value;
            if (this.pendingOperation.equals("=")) {
                this.pendingOperation = operation;
            }
            String str = this.pendingOperation;
            char c = 65535;
            int hashCode = str.hashCode();
            if (hashCode != 45) {
                if (hashCode != 47) {
                    if (hashCode != 61) {
                        switch (hashCode) {
                            case 42:
                                if (str.equals("*")) {
                                    c = 2;
                                    break;
                                }
                                break;
                            case 43:
                                if (str.equals("+")) {
                                    c = 4;
                                    break;
                                }
                                break;
                        }
                    } else if (str.equals("=")) {
                        c = 0;
                    }
                } else if (str.equals("/")) {
                    c = 1;
                }
            } else if (str.equals("-")) {
                c = 3;
            }
            switch (c) {
                case 0:
                    this.operand1 = this.operand2;
                    break;
                case 1:
                    if (this.operand2.doubleValue() == 0.0d) {
                        this.operand1 = Double.valueOf(0.0d);
                        break;
                    } else {
                        this.operand1 = Double.valueOf(this.operand1.doubleValue() / this.operand2.doubleValue());
                        break;
                    }
                case 2:
                    this.operand1 = Double.valueOf(this.operand1.doubleValue() * this.operand2.doubleValue());
                    break;
                case 3:
                    this.operand1 = Double.valueOf(this.operand1.doubleValue() - this.operand2.doubleValue());
                    break;
                case 4:
                    this.operand1 = Double.valueOf(this.operand1.doubleValue() + this.operand2.doubleValue());
                    break;
            }
        }
        this.result.setText(this.operand1.toString());
        this.newNumber.setText("");
    }
}
