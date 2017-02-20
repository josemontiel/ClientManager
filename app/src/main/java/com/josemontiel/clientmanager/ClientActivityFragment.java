package com.josemontiel.clientmanager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.josemontiel.clientmanager.models.Client;
import io.realm.Realm;
import java.util.Calendar;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class ClientActivityFragment extends Fragment implements TextWatcher,
    DatePickerDialog.OnDateSetListener{

  private Unbinder unbinder;

  Client client;

  @BindView(R.id.form_client_first) EditText firstNameEt;
  @BindView(R.id.form_client_last) EditText lastNameEt;
  @BindView(R.id.form_client_phone) EditText phoneEt;
  @BindView(R.id.form_client_dob) EditText dobEt;
  @BindView(R.id.form_client_zip) EditText zipEt;
  @BindView(R.id.form_save_btn) Button saveButton;

  String firstName;
  String lastName;
  String phone;
  String dob;
  String zip;

  public ClientActivityFragment() {
  }



  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_client, container, false);

    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setHasOptionsMenu(true);

    setClient();

    initForm();

    saveButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        String firstName = firstNameEt.getText().toString();
        String lastName = lastNameEt.getText().toString();
        String phone = phoneEt.getText().toString();
        String dob = dobEt.getText().toString();
        String zip = String.valueOf(zipEt.getText().toString());

        final Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        if (client == null) {

          final Client match =
              realm.where(Client.class).equalTo("phoneNumber", phone).findFirst();

          if (match != null) {
            Toast.makeText(getContext(), R.string.error_already_exists, Toast.LENGTH_SHORT).show();
            realm.cancelTransaction();
            return;
          }

          client = new Client();
          client.phoneNumber = phone;
          client.firstName = firstName;
          client.lastName = lastName;
          client.dob = dob;
          client.zipCode = Integer.parseInt(zip);
          client = realm.copyToRealm(client);
        } else {
          client.phoneNumber = phone;
          client.firstName = firstName;
          client.lastName = lastName;
          client.dob = dob;
          client.zipCode = Integer.parseInt(zip);
        }

        realm.commitTransaction();



        finishWithResultOk();
      }
    });
  }

  void finishWithResultOk() {
    final FragmentActivity activity = getActivity();
    activity.setResult(Activity.RESULT_OK);
    activity.finish();
  }

  void initForm() {
    firstNameEt.addTextChangedListener(this);
    lastNameEt.addTextChangedListener(this);
    phoneEt.addTextChangedListener(this);
    phoneEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    zipEt.addTextChangedListener(this);
    dobEt.addTextChangedListener(this);
    dobEt.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        openDatePicker();
      }
    });
  }

  void setClient() {
    final Intent intent = getActivity().getIntent();

    final Bundle extras = intent.getExtras();

    if (extras != null && extras.containsKey(ClientActivity.EXTRA_CLIENT_PHONE)) {
      String phoneExtra = extras.getString(ClientActivity.EXTRA_CLIENT_PHONE);

      final Realm realm = Realm.getDefaultInstance();

      final Client client = realm.where(Client.class).equalTo("phoneNumber", phoneExtra).findFirst();
      if (client != null) {
        this.client = client;

        firstName = this.client.firstName;
        lastName = this.client.lastName;
        phone = this.client.phoneNumber;
        dob = this.client.dob;
        zip = String.valueOf(this.client.zipCode);

        firstNameEt.setText(firstName);
        lastNameEt.setText(lastName);
        phoneEt.setText(phone);
        dobEt.setText(dob);
        zipEt.setText(zip);

        getActivity().invalidateOptionsMenu();

        getActivity().setTitle(this.client.firstName + " " + this.client.lastName);
      }
    } else {
      getActivity().setTitle(R.string.title_add_client);
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    if (client != null) {
      inflater.inflate(R.menu.menu_client, menu);
    }

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case android.R.id.home:
        getActivity().finish();
        break;
      case R.id.action_delete:
        deleteClient();
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  void deleteClient() {
    final Realm realm = Realm.getDefaultInstance();
    realm.beginTransaction();
    client.deleteFromRealm();
    realm.commitTransaction();

    finishWithResultOk();
  }

  @Override public void onDestroyView() {
    unbinder.unbind();
    super.onDestroyView();
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

  }

  @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

  }

  @Override public void afterTextChanged(Editable s) {
    validateForm();
  }

  void validateForm() {
    boolean hasChanged =
        changed(firstNameEt, firstName)
        || changed(lastNameEt, lastName)
        || changed(phoneEt, phone)
        || changed(dobEt, dob)
        || changed(zipEt, zip);


    boolean isValid =
        valid(firstNameEt)
            && valid(lastNameEt)
            && valid(phoneEt)
            && valid(dobEt)
            && valid(zipEt);

    saveButton.setVisibility(hasChanged && isValid ? View.VISIBLE : View.INVISIBLE);
  }

  private boolean changed(EditText et, String previous) {
    final String value = et.getText().toString();
    return !value.equals(previous) && !value.isEmpty();
  }

  private boolean valid(EditText et) {
    final String value = et.getText().toString();
    return !value.isEmpty();
  }

  void openDatePicker() {
    final DatePickerFragment datePickerFragment = new DatePickerFragment();
    datePickerFragment.show(getFragmentManager(), DatePickerFragment.TAG);
  }

  public void onDateSet(DatePicker view, int year, int month, int day) {
    // Do something with the date chosen by the user
    dobEt.setText(String.format(Locale.US, "%02d", (month+1))+"/"+day+"/"+year);
  }

  public static class DatePickerFragment extends DialogFragment {
    public static final String TAG = "fragment_date_picker";

    DatePickerDialog.OnDateSetListener listener;

    @Override public void onAttach(Context context) {
      super.onAttach(context);

      final Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_client);
      if (fragment != null && fragment instanceof DatePickerDialog.OnDateSetListener) {
        listener = (DatePickerDialog.OnDateSetListener) fragment;
      }
    }

    @Override public int getTheme() {
      return R.style.DatePicker;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

      setStyle(STYLE_NORMAL, R.style.DatePicker);

      // Use the current date as the default date in the picker
      final Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 1991);

      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH);
      int day = c.get(Calendar.DAY_OF_MONTH);

      // Create a new instance of DatePickerDialog and return it
      return new DatePickerDialog(getActivity(), listener, year, month, day);
    }


  }
}
