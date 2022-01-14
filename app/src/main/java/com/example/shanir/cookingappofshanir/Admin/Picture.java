package com.example.shanir.cookingappofshanir.Admin;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.classs.FileHelper;

import java.io.File;
import java.io.IOException;


public class Picture extends AppCompatActivity implements View.OnClickListener  {
    TextView tvhead;
    final String PIC_FILE_NAME="userpic";

    Button bttakenow, btchose, btsave;
    ImageView imageView;
    Uri uriProfileImage;
    int codetakepicutre = 270;
    int CHOOSE_IMAGE = 400;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        imageView = (ImageView) findViewById(R.id.ivrecipe);
        btsave = (Button) findViewById(R.id.btmove);
        tvhead = (TextView) findViewById(R.id.tvheadpicture);
        bttakenow = (Button) findViewById(R.id.bttakepicture);
        btchose = (Button) findViewById(R.id.btchoosepicturefromgalery);
        bttakenow.setOnClickListener(this);
        btchose.setOnClickListener(this);
        btsave.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if (v == bttakenow)
        {
            if (imageView.getDrawable() == null)
            {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, codetakepicutre);

            } else
                {
                Toast.makeText(this, "כבר יש תמונה למתכון זה", Toast.LENGTH_SHORT).show();
            }

        } else if (v == btchose)
        {
            if (imageView.getDrawable() == null)
                showImageChooser();
            else {
                Toast.makeText(this, "כבר יש תמונה למתכון זה", Toast.LENGTH_SHORT).show();
            }
        } else if (v == btsave)
        {
            if (imageView.getDrawable() == null)
            {
                Toast.makeText(this, "תוסיף תמונה בבקשה", Toast.LENGTH_SHORT).show();

            }
            else if (imageView.getDrawable() != null)
                {
                Toast.makeText(this, "התמונה נשמרה בהצלחה", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent();
                intent1.putExtra("uri", uriProfileImage.toString());

                setResult(RESULT_OK, intent1);
                finish();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK) {
            uriProfileImage = data.getData();
            Log.d("dd", "onActivityResult: " + uriProfileImage);

            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == codetakepicutre && resultCode == RESULT_OK) // back from camera
        {

            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            FileHelper.saveBitmapToFile(bitmap,Picture.this,PIC_FILE_NAME);
            File tmpFile=new File(getFilesDir()+"/"+PIC_FILE_NAME);
            uriProfileImage= Uri.fromFile(tmpFile);
            Log.d("dd", "onActivityResult: " + uriProfileImage);







        }
    }


    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "תבחר תמונה"), CHOOSE_IMAGE);


    }
}









