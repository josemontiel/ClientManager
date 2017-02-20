package com.josemontiel.clientmanager;

import android.app.Application;
import com.josemontiel.clientmanager.models.Client;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by Jose on 2/20/17.
 */

public class ClientManagerApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();

    Realm.init(this);

    RealmConfiguration config = new RealmConfiguration.Builder()
        .schemaVersion(3)
        .migration(new RealmMigration() {
          @Override public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

          }
        })
        .build();



    Realm.setDefaultConfiguration(config);
  }
}
