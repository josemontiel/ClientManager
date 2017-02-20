package com.josemontiel.clientmanager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class ClientActivity extends AppCompatActivity {

  public static final int INTENT_CLIENT = 101;
  public static final String EXTRA_CLIENT_PHONE = "extra_phone";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_client);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setNavigationIcon(R.drawable.ic_nav_back_arrow);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
    setSupportActionBar(toolbar);

  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    final int itemId = item.getItemId();

    switch (itemId) {
      case android.R.id.home:
        finish();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
