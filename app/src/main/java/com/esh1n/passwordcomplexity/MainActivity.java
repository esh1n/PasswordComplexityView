package com.esh1n.passwordcomplexity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.esh1n.passwordcomplexity.widget.PasswordComplexityView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final PasswordComplexityView passwordComplexityView = (PasswordComplexityView) findViewById(R.id.password_complexity_view);
        findViewById(R.id.middle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordComplexityView.
                        setComplexity(PasswordComplexityView.PasswordComplexity.MEDIUM);
            }
        });
        findViewById(R.id.strong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordComplexityView.
                        setComplexity(PasswordComplexityView.PasswordComplexity.STRONG);
            }
        });
        findViewById(R.id.weak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordComplexityView.
                        setComplexity(PasswordComplexityView.PasswordComplexity.WEAK);
            }
        });
    }


}
