package com.gaming.community.flexster.phoneAuth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import com.gaming.community.flexster.menu.PrivacyActivity;
import com.google.android.material.snackbar.Snackbar;
import com.hbb20.CountryCodePicker;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.emailAuth.LoginActivity;

import org.w3c.dom.Text;

import java.security.Policy;

public class GenerateOTPActivity extends AppCompatActivity {

    TextView forgot;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_o_t_p);

        forgot = findViewById(R.id.forgot);

        //Back
        findViewById(R.id.imageView).setOnClickListener(v -> onBackPressed());

        //Text
        //findViewById(R.id.forgot).setOnClickListener(v -> startActivity(new Intent(GenerateOTPActivity.this, LoginActivity.class)));

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GenerateOTPActivity.this, PrivacyActivity.class));
            }
        });

        //EditText
        EditText phone = findViewById(R.id.phone);

        //CCP
        CountryCodePicker ccp = findViewById(R.id.code);
        String code = ccp.getSelectedCountryCode();
        phone.setText("+"+code);

        //Button
        findViewById(R.id.login).setOnClickListener(v -> {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            String mPhone = phone.getText().toString().trim();
            if (mPhone.isEmpty()){
                Snackbar.make(v,"Enter your phone number", Snackbar.LENGTH_LONG).show();
                findViewById(R.id.progressBar).setVisibility(View.GONE);
            }else {
                Intent intent = new Intent(GenerateOTPActivity.this, VerifyOTPActivity.class);
                intent.putExtra("phonenumber", mPhone);
                startActivity(intent);
                findViewById(R.id.progressBar).setVisibility(View.GONE);
            }
        });
    }
}