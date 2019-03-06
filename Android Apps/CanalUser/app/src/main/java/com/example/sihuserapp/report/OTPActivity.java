package com.example.sihuserapp.report;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sihuserapp.Interfaces.OTPInterface;
import com.example.sihuserapp.OTPUtils;
import com.example.sihuserapp.Objects.OTPBody;
import com.example.sihuserapp.Objects.OTPResponse;
import com.example.sihuserapp.Objects.VerifyOTPBody;
import com.example.sihuserapp.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity {

    OTPInterface mAPIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        initializeVariables();
    }

    private void initializeVariables() {

        final EditText phone = findViewById(R.id.editText);
        final EditText otp = findViewById(R.id.editText2);
        final ConstraintLayout constraintLayout = findViewById(R.id.constraint);
        final Button generateOtp = findViewById(R.id.button);
        mAPIService = OTPUtils.getAPIService();
        Button verifyOtp = findViewById(R.id.button2);
        generateOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phone.getText().toString();
                if (phoneNumber.length() != 10) {
                    Toast.makeText(getApplicationContext(), "Enter a Valid Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                OTPBody body = new OTPBody("+91"+phoneNumber);
                mAPIService.sendOTP(body).enqueue(new Callback<OTPResponse>() {
                    @Override
                    public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                        if (response.isSuccessful()) {
                            generateOtp.setVisibility(View.GONE);
                            constraintLayout.setVisibility(View.VISIBLE);
                        } else
                            Toast.makeText(getApplicationContext(), "There seems to be a problem, please try again", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<OTPResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Looks like we can't reach the server! Try Again", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phone.getText().toString();
                String otpText = otp.getText().toString().trim();
                VerifyOTPBody body = new VerifyOTPBody("+91"+phoneNumber, otpText);
                mAPIService.verifyOTP(body).enqueue(new Callback<OTPResponse>() {
                    @Override
                    public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                        if (response.isSuccessful()) {
                            Log.v("TAG",response.body().getData());
                            if (response.body().getData().equals("matched")) {

                                Intent intent = new Intent(OTPActivity.this, PhoneReportActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "The OTP does not match!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<OTPResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Looks like a server error", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }
}
