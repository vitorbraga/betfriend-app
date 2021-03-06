package br.com.betfriend.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

        mTabHost.addTab(mTabHost.newTabSpec("fragmentb").setIndicator(getActivity().getString(R.string.tab_pending)),
                PendingTabFragment.class, bundle);
        mTabHost.addTab(mTabHost.newTabSpec("fragmentc").setIndicator(getActivity().getString(R.string.tab_finished)),
                FinishedTabFragment.class, bundle);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.drawer_history));
    }
}
