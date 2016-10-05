package br.com.betfriend.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.betfriend.R;
import br.com.betfriend.model.UserDataDTO;

public class HistoryFragment extends Fragment {

    private FragmentTabHost mTabHost;

    private UserDataDTO mUserData;

    //Mandatory Constructor
    public HistoryFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history,container, false);

        mUserData = (UserDataDTO) getArguments().getSerializable("USER_DATA_EXTRA");

        Bundle bundle = new Bundle();
        bundle.putSerializable("USER_DATA_EXTRA", mUserData);

        mTabHost = (FragmentTabHost) rootView.findViewById(R.id.tabhost);

        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator("Em aberto"),
                PendingTabFragment.class, bundle);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentc").setIndicator("Finalizadas"),
                FinishedTabFragment.class, bundle);

        return rootView;
    }
}
