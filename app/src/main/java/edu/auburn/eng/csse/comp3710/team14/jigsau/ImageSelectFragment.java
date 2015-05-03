package edu.auburn.eng.csse.comp3710.team14.jigsau;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class ImageSelectFragment extends Fragment implements View.OnClickListener {

    final private int PICK_IMAGE = 0;
    final private String GRID_SIZE = "gridSize";
    final private String EXTRA_IMAGE = "image";
    String imagePath;
    int sizeSelection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        sizeSelection = bundle.getInt(GRID_SIZE);

        View image_view = inflater.inflate(R.layout.fragment_images, container, false);

        ImageButton image1Btn = (ImageButton)image_view.findViewById(R.id.image1);
        ImageButton image2Btn = (ImageButton)image_view.findViewById(R.id.image2);
        ImageButton image3Btn = (ImageButton)image_view.findViewById(R.id.image3);
        Button galleryBtn = (Button)image_view.findViewById(R.id.gallery_button);

        image1Btn.setOnClickListener(this);
        image2Btn.setOnClickListener(this);
        image3Btn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);

        return image_view;
    }

    @Override
    public void onClick(View v) {

        Intent intent;
        int imageId;

        switch (v.getId()) {
            case R.id.image1:
                intent = new Intent(getActivity(), BoardActivity.class);
                imageId = R.drawable.classic_puzzle;
                intent.putExtra(EXTRA_IMAGE, imageId);
                intent.putExtra(GRID_SIZE, sizeSelection);
                startActivity(intent);
                break;
            case R.id.image2:
                intent = new Intent(getActivity(), BoardActivity.class);
                imageId = R.drawable.samford_hall;
                intent.putExtra(EXTRA_IMAGE, imageId);
                intent.putExtra(GRID_SIZE, sizeSelection);
                startActivity(intent);
                break;
            case R.id.image3:
                intent = new Intent(getActivity(), BoardActivity.class);
                imageId = R.drawable.auburn_logo;
                intent.putExtra(EXTRA_IMAGE, imageId);
                intent.putExtra(GRID_SIZE, sizeSelection);
                startActivity(intent);
                break;
            case R.id.gallery_button:
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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

                Intent intent = new Intent(getActivity(), BoardActivity.class);
                intent.putExtra(EXTRA_IMAGE, imagePath);
                intent.putExtra(GRID_SIZE, sizeSelection);
                startActivity(intent);
            }
        }
    }
}
