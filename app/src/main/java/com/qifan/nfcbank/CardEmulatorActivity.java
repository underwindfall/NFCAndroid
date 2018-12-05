package com.qifan.nfcbank;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.qifan.nfcbank.cardEmulation.KHostApduService;

/**
 * Created by Qifan on 28/11/2018.
 */
public class CardEmulatorActivity extends AppCompatActivity {

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
            EditText editText = (EditText) findViewById(R.id.editText);

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText())) {
                    Toast.makeText(CardEmulatorActivity.this, getString(R.string.toast_msg), Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(CardEmulatorActivity.this, KHostApduService.class);
                    intent.putExtra("ndefMessage", editText.getText().toString());
                    startService(intent);
                }

            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
    }


}