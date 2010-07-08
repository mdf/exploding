package com.littlebighead.exploding;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
//import android.widget.EditText;
import android.widget.TextView;

//public class CommunityPropsDialog {
public class CommunityPropsDialog extends Dialog {

    public interface ReadyListener {
        public void ready(String name);
    }

    private String name;
    private ReadyListener readyListener;
    TextView etName;

    public CommunityPropsDialog(Context context, String name,
            ReadyListener readyListener) {
        super(context);
        this.name = name;
        this.readyListener = readyListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_attrib_dialogue);
        //setTitle("Enter your Name ");
        Button buttonOK = (Button) findViewById(R.id.dismiss_member_props);
        buttonOK.setOnClickListener(new OKListener());
        etName = (TextView) findViewById(R.id.name_textview);
    }

    private class OKListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            readyListener.ready(String.valueOf(etName.getText()));
            CommunityPropsDialog.this.dismiss();
        }
    }

}
