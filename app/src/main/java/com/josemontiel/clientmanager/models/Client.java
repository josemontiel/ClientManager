package com.josemontiel.clientmanager.models;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jose on 2/20/17.
 */

public class Client extends RealmObject {

  public String firstName;
  public String lastName;

  @Index
  public String phoneNumber;

  public String dob;
  public int zipCode;

}
