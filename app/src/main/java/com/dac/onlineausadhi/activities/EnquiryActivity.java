package com.dac.onlineausadhi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dac.onlineausadhi.onlineaushadhilin.R;

public class EnquiryActivity extends AppCompatActivity {
    private EditText enquiry;
    private Button send;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry);
        enquiry = (EditText) findViewById(R.id.enquiry);
        send = (Button) findViewById(R.id.send);
        toolbar = (Toolbar) findViewById(R.id.enquiryToolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Enquire About");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(Color.WHITE);
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Enquiry To Online Aushadhi ");
                intent.putExtra(Intent.EXTRA_TEXT, enquiry.getText().toString());
                intent.setData(Uri.parse("mailto:care.onlineaushadhi@gmail.com")); // or just "mailto:" for blank

                try {
                    startActivity(Intent.createChooser(intent, "Send email using..."));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(EnquiryActivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
                }// this will make such that when user returns to your app, your app is displayed, instead of the email app.

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
