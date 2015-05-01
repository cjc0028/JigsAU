package edu.auburn.eng.csse.comp3710.team14.jigsau;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

public class MenuFragment extends Fragment implements View.OnClickListener {
    final static Integer[] gridSizes = {4,5,6,7,8};
    private boolean isMuted = false;
    AudioManager audioManager;
    ImageButton volumeBtn;
    int sizeSelection;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        View menu_view = inflater.inflate(R.layout.fragment_menu, container, false);
        final Spinner boardSizeSpin = (Spinner)menu_view.findViewById(R.id.spinner);
        final ArrayAdapter<Integer> sizeAdapter;
        sizeAdapter = new ArrayAdapter<Integer>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, gridSizes);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boardSizeSpin.setAdapter(sizeAdapter);
        boardSizeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sizeSelection = sizeAdapter.getItem(boardSizeSpin.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button startBtn = (Button)menu_view.findViewById(R.id.start_button);
        startBtn.setOnClickListener(this);

        volumeBtn = (ImageButton)menu_view.findViewById(R.id.volume_button);
        volumeBtn.setOnClickListener(this);
        isMuted();

        return menu_view;
    }

    @Override
    public void onClick(View v) {

       switch(v.getId())
       {
           case R.id.start_button:
               ImageSelectFragment imageSelectFragment = new ImageSelectFragment();
               FragmentTransaction transaction
                       = getActivity().getFragmentManager().beginTransaction();
               Bundle bundle = new Bundle();
               bundle.putInt("gridSize", sizeSelection);
               imageSelectFragment.setArguments(bundle);
               transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                                               R.anim.slide_in_left, R.anim.slide_out_right);
               transaction.replace(R.id.fragment_container, imageSelectFragment);
               transaction.addToBackStack(null);

               transaction.commit();
               break;
           case R.id.volume_button:
               if (!isMuted) {
                   audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                   volumeBtn.setBackgroundResource(R.drawable.volume_mute);
                   isMuted = true;
               }
               else {
                   audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                   volumeBtn.setBackgroundResource(R.drawable.volume_on);
                   isMuted = false;
               }
               break;
       }
    }

    public void isMuted() {
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
            volumeBtn.setBackgroundResource(R.drawable.volume_mute);
            isMuted = true;
        }
    }
}
