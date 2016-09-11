package br.com.betfriend.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import br.com.betfriend.R;

public class SettingsFragment extends Fragment {

    private Button mDisconnectAccountButton, mRemoveAccountButton;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);

        mDisconnectAccountButton = (Button) view.findViewById(R.id.disconnect_account_button);
        mRemoveAccountButton = (Button) view.findViewById(R.id.remove_account_button);

        mDisconnectAccountButton.setOnClickListener(disconnectAccount);
        mRemoveAccountButton.setOnClickListener(removeAccount);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private View.OnClickListener disconnectAccount = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), "Disconnect account", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener removeAccount = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), "Remove account", Toast.LENGTH_SHORT).show();
        }
    };

}
