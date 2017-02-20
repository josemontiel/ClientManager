package com.josemontiel.clientmanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.josemontiel.clientmanager.models.Client;
import com.josemontiel.clientmanager.ui.ClientItemDecoration;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

  private Unbinder unbinder;

  @BindView(R.id.client_rv) RecyclerView recyclerView;
  ClientAdapter adapter;

  List<Client> clientList = new ArrayList<>();

  /*
  Callback method. To be called after a Realm Query is done.
  * */
  private RealmChangeListener<RealmResults<Client>> realmChangeListener =
      new RealmChangeListener<RealmResults<Client>>() {
        @Override public void onChange(RealmResults<Client> results) {

          clientList.clear();

          for (int i = 0; i < results.size(); i++) {
            Client client = results.get(i);
            clientList.add(client);
          }

          // Only initialize the RecyclerView and Adapter once. Then just notify the adapter.
          if (adapter == null) {
            adapter = new ClientAdapter(clientList);
            recyclerView.addItemDecoration(new ClientItemDecoration(getContext()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);
          } else {
            adapter.notifyDataSetChanged();
          }
        }
      };

  public MainActivityFragment() {

  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_main, container, false);

    unbinder = ButterKnife.bind(this, view);

    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override public void onResume() {
    super.onResume();

    final Realm realm = Realm.getDefaultInstance();

    realm.where(Client.class).findAllSortedAsync("firstName").addChangeListener(realmChangeListener);
  }

  @Override public void onDestroyView() {
    // Unbind Butterknife to prevent mem leaks.
    unbinder.unbind();

    // Remove Realm listeners
    final Realm realm = Realm.getDefaultInstance();
    realm.removeAllChangeListeners();

    super.onDestroyView();
  }

  class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientHolder> {

    List<Client> clientList;

    ClientAdapter(List<Client> clientList) {
      this.clientList = clientList;
    }

    @Override public ClientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      final View view = LayoutInflater.from(getContext()).inflate(R.layout.item_client, parent, false);

      final ClientHolder clientHolder = new ClientHolder(view);

      clientHolder.rootView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          final int position = clientHolder.getAdapterPosition();

          Client client = getClient(position);

          editClient(client);

        }
      });

      clientHolder.callButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          final int position = clientHolder.getAdapterPosition();

          Client client = getClient(position);

          makeCall(client);
        }
      });



      return clientHolder;
    }

    @Override public void onBindViewHolder(ClientHolder holder, int position) {
      Client client = getClient(position);

      holder.nameTv.setText(client.firstName+" "+client.lastName);
      holder.dobTv.setText(client.dob);
      holder.phoneTv.setText(client.phoneNumber);
      holder.zipTv.setText(String.valueOf(client.zipCode));
    }

    @Override public int getItemCount() {
      return clientList != null ? clientList.size() : 0;
    }

    Client getClient(int position) {
      return clientList.get(position);
    }

    class ClientHolder extends RecyclerView.ViewHolder {

      View rootView;

      @BindView(R.id.item_client_name) TextView nameTv;
      @BindView(R.id.item_client_phone) TextView phoneTv;
      @BindView(R.id.item_client_dob) TextView dobTv;
      @BindView(R.id.item_client_zip) TextView zipTv;
      @BindView(R.id.item_client_call) Button callButton;

      ClientHolder(View itemView) {
        super(itemView);

        this.rootView = itemView;

        // Bind Views.
        ButterKnife.bind(this, itemView);
      }
    }
  }

  /**
   * Initiates the Client Activity with a phone number to find a {@link Client} and edit it
   * @param client {@link Client} existing Client to edit
   */
  private void editClient(Client client) {
    Intent intent = new Intent(getContext(), ClientActivity.class);
    intent.putExtra(ClientActivity.EXTRA_CLIENT_PHONE, client.phoneNumber);

    startActivity(intent);
  }

  /**
   * Opens the Dialer app with the {@link Client#phoneNumber} pre filled.
   * @param client {@link Client} existing Client to call
   */
  private void makeCall(Client client) {
    Intent intent = new Intent(Intent.ACTION_DIAL);
    intent.setData(Uri.parse("tel:"+client.phoneNumber));
    startActivity(intent);
  }
}
