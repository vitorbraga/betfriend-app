package br.com.betfriend.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.betfriend.R;

public class HistoryFragment extends Fragment {

    private FragmentTabHost mTabHost;

    //Mandatory Constructor
    public HistoryFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history,container, false);

        mTabHost = (FragmentTabHost) rootView.findViewById(R.id.tabhost);

        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator("Pendentes"),
                PendingTabFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentc").setIndicator("Finalizadas"),
                FinishedTabFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentd").setIndicator("Todas"),
                PendingTabFragment.class, null);

        return rootView;
    }
}
