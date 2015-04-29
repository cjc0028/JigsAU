package edu.auburn.eng.csse.comp3710.team14.jigsau;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GameFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View game_view = inflater.inflate(R.layout.fragment_game, container, false);

        return game_view;
    }
}
