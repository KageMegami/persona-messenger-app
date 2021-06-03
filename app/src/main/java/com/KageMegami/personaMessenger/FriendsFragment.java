package com.KageMegami.personaMessenger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FriendsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set bottom bar actions
        BottomBarFragment bottom = (BottomBarFragment)getChildFragmentManager().getFragments().get(0);
        bottom.getView().findViewById(R.id.chats).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_friendsFragment_to_homeFragment);
        });
    }
}