package com.example.sqlitecrudoperation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class EditRecordActivity extends AppCompatActivity {

    ImageView ivImage;
    EditText etName, etAge, etPhone;
    MaterialButton btnSave;
    ActionBar actionBar;

    private static final int CAMERA_REQUEST_CODE  = 100;
    private static final int STORAGE_REQUEST_CODE  = 101;

    private static final int IMAGE_PICK_CAMERA_CODE  = 102;
    private static final int IMAGE_PICK_GALLERY_CODE  = 103;

    private String[] cameraPermission;
    private String[] storagePermission;

    private Uri imageUri;

    private String id, name, age, phone, addTimeStamp, updateTimeStamp;
    private boolean editMode = false;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Update Record");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        etName = findViewById(R.id.personName);
        etAge = findViewById(R.id.personAge);
        etPhone = findViewById(R.id.personPhone);
        ivImage = findViewById(R.id.personImage);
        btnSave = findViewById(R.id.btn_save);


        Intent intent = getIntent();
        editMode = intent.getBooleanExtra("editMode",editMode);
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        age = intent.getStringExtra("age");
        phone = intent.getStringExtra("phone");
        imageUri = Uri.parse(intent.getStringExtra("image"));
        addTimeStamp = intent.getStringExtra("add_time");
        updateTimeStamp = intent.getStringExtra("update_time");

        if (editMode){
            etName.setText(name);
            etAge.setText(age);
            etPhone.setText(phone);
            ivImage.setImageURI(imageUri);

        }

        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        dbHelper = new DatabaseHelper(this);

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePickDialog();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (checkValidation()){
                   getData();
                   Toast.makeText(EditRecordActivity.this, "Record Update " , Toast.LENGTH_SHORT).show();
                   startActivity(new Intent(EditRecordActivity.this,MainActivity.class));
                   finish();
               }
            }
        });
    }

    private void getData() {
        name = ""+ etName.getText().toString().trim();
        age = ""+ etAge.getText().toString().trim();
        phone = ""+ etPhone.getText().toString().trim();


        if (editMode){
            String newUpdateTime = ""+System.currentTimeMillis();

            dbHelper.updateInfo(
                    ""+id,
                    ""+name,
                    ""+age,
                    ""+phone,
                    ""+imageUri,
                    ""+newUpdateTime,
                    ""+newUpdateTime
            );
        }
    }

    private boolean checkValidation(){

        if (imageUri == null){
            Toast.makeText(this, "Image Required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etName.getText().toString().isEmpty()){
            Toast.makeText(this, "Name Required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etName.getText().toString().length() < 3){
            Toast.makeText(this, "name should be 3 character", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etAge.getText().toString().isEmpty()){
            Toast.makeText(this, "Age Required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPhone.getText().toString().isEmpty()){
            Toast.makeText(this, "Phone No. Required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPhone.getText().toString().length() < 10 ){
            Toast.makeText(this, "Phone No. should be 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void imagePickDialog() {
        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select For Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0){

                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }

                }
                else if (i == 1){
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }else {
                        pickFromStorage();
                    }
                }

            }
        });

        builder.create().show();
    }

    private void pickFromStorage() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Image title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "description");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }

    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return result && result1;

    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0){

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(this, "Camera Permission Required", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:

                if (grantResults.length > 0){

                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickFromStorage();
                    }else {
                        Toast.makeText(this, "Gallery Permission Required", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK){

            if (requestCode == IMAGE_PICK_GALLERY_CODE){

                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
            }

            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK){

                    Uri resultUri = result.getUri();
                    imageUri = resultUri;
                    ivImage.setImageURI(resultUri);
                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception error = result.getError();
                    Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}