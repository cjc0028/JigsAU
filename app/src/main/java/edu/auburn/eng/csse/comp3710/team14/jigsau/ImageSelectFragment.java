package edu.auburn.eng.csse.comp3710.team14.jigsau;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class ImageSelectFragment extends Fragment implements View.OnClickListener {

    final private int PICK_IMAGE = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View menu_view = inflater.inflate(R.layout.fragment_images, container, false);

        Button galleryBtn = (Button)menu_view.findViewById(R.id.gallery_button);
        galleryBtn.setOnClickListener(this);

        return menu_view;
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

        getActivity().setContentView(R.layout.fragment_game);
        ImageView image = (ImageView)getActivity().findViewById(R.id.image);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver()
                        .query(selectedImageUri, projection, null, null, null);

                try {
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(projection[0]));
                    image.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                    cursor.close();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

               /* GameFragment gameFragment = new GameFragment();
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_images, gameFragment);
                transaction.addToBackStack(null);

                transaction.commit();*/
            }
        }
    }
}
