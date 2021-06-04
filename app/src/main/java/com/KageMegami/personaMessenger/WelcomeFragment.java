package com.KageMegami.personaMessenger;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class WelcomeFragment extends Fragment {
    ImageView avatar;
    public boolean permissionGanted = false;
    public ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                permissionGanted = isGranted;
            });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        avatar = view.findViewById(R.id.avatar);
        avatar.setOnClickListener(v -> {
            selectImage(getContext());
        });

        ImageView confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_welcomeFragment_to_loadingFragment);
            ((MainActivity)getActivity()).loadData();
        });
    }

    private void selectImage(Context context) {
        if (ContextCompat.checkSelfPermission(getContext(), CAMERA) == PERMISSION_DENIED)
            requestPermissionLauncher.launch(CAMERA);
        if (ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) == PERMISSION_DENIED)
            requestPermissionLauncher.launch(WRITE_EXTERNAL_STORAGE);
        if (ContextCompat.checkSelfPermission(getContext(), CAMERA) == PERMISSION_DENIED || ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) == PERMISSION_DENIED)
            return;;

        //si je veux la photo
        //final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        //sans la photo
        final CharSequence[] options = {"Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            } else if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_CANCELED)
            return;
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK && data != null) {
                    Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                    avatar.setImageBitmap(selectedImage);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage =  data.getData();
                    performCrop(selectedImage);


                    // ci jamais je veux pas crop
              /*  String[] filePathColumn = {MediaStore.Images.ImageColumns.DATA,};
                if (selectedImage != null) {
                    Cursor cursor = ((MainActivity)getActivity()).getContextOfApplication().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor.moveToFirst()) {
                        performCrop(selectedImage);
                        if (Build.VERSION.SDK_INT >= 29) {
                            try (ParcelFileDescriptor pfd = ((MainActivity)getActivity()).getContextOfApplication().getContentResolver().openFileDescriptor(selectedImage, "r")) {
                                if (pfd != null) {
                                    avatar.setImageBitmap(BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor()));
                                }
                            } catch (IOException ex) {}
                        } else {
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            avatar.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        }
                        cursor.close();
                    }
                }*/



                }
                break;
            case 2:
                if(resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap selectedBitmap = extras.getParcelable("data");
                    avatar.setImageBitmap(selectedBitmap);
                    avatar.setScaleType(ImageView.ScaleType.FIT_XY);
                }
                break;
        }
    }

    private void performCrop(Uri contentUri) {
        try {
            //Start Crop Activity
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 2);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException e) {
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}