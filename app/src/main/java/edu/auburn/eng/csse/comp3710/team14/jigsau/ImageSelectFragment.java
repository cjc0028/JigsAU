package edu.auburn.eng.csse.comp3710.team14.jigsau;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ImageSelectFragment extends Fragment implements View.OnClickListener {

    final private int PICK_IMAGE = 0;
    //ImageView image;
    String imagePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View image_view = inflater.inflate(R.layout.fragment_images, container, false);

        Button galleryBtn = (Button)image_view.findViewById(R.id.gallery_button);
        galleryBtn.setOnClickListener(this);

        return image_view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.gallery_button:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //getActivity().setContentView(R.layout.fragment_game);
        //image = (ImageView)getActivity().findViewById(R.id.image);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver()
                        .query(selectedImageUri, projection, null, null, null);

                try {
                    cursor.moveToFirst();
                    imagePath = cursor.getString(cursor.getColumnIndex(projection[0]));
                    cursor.close();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                GameFragment gameFragment = new GameFragment();
                Bundle bundle = new Bundle();
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                bundle.putString("imagePath", imagePath);
                gameFragment.setArguments(bundle);
                transaction.replace(R.id.fragment_container, gameFragment);
                Log.i("transaction", "Fragment replaced");
                transaction.addToBackStack(null);
                Log.i("transaction", "Added to Backstack");
                transaction.commit();
                Log.i("transaction", "Fragment committed");
            }
        }
    }
}
