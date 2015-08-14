package com.titanium.imagehosting;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

      }
    public void onClickGal(View v)
    {
    	Intent i=new Intent("android.intent.action.FIRST");
    	startActivity(i);
    }

    public void onClickCap(View v)
    {
    	Intent i=new Intent("android.intent.action.SECOND");
    	startActivity(i);
    }
    public void onClickDown(View v)
    {
    	Intent i=new Intent("android.intent.action.THIRD");
    	startActivity(i);
    }

}
