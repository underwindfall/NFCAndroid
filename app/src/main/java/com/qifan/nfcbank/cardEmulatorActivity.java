package com.qifan.nfcbank;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.ReaderCallback;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.qifan.nfcbank.cardEmulation.IsoDepAdapter;
import com.qifan.nfcbank.cardEmulation.IsoDepTransceiver;
import com.qifan.nfcbank.cardEmulation.MyHostApduService;

/**
 * Created by Qifan on 28/11/2018.
 */
public class cardEmulatorActivity extends AppCompatActivity{

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_emulator);

    }

    @Override
    public void onResume() {
        super.onResume();
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cardEmulatorActivity.this, MyHostApduService.class);
                intent.putExtra("ndefMessage", "你是");
                startService(intent);
//                Intent test = new Intent(cardEmulatorActivity.this, MyHostApduService.class);
//                startService(test);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}