package edu.auburn.eng.csse.comp3710.team14.jigsau;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MenuFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View menu_view = inflater.inflate(R.layout.fragment_menu, container, false);

        Button startBtn = (Button)menu_view.findViewById(R.id.start_button);
        startBtn.setOnClickListener(this);

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
               transaction.replace(R.id.fragment_menu, imageSelectFragment);
               transaction.addToBackStack(null);

               transaction.commit();
               break;
       }
    }
}
