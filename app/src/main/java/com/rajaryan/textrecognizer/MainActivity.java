package com.rajaryan.textrecognizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;


import org.w3c.dom.Text;

import java.io.IOError;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView text;
    ImageView image;
    Bitmap imageBitmap;
    Button capture,detect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    text=findViewById(R.id.text);
    image=findViewById(R.id.image);
    capture=findViewById(R.id.capture);
    detect=findViewById(R.id.detect);
    capture.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dispatchTakePictureIntent();
            text.setText("");
        }
    });
    detect.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            detectTextFromImage();
        }

    });


    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
        }
    }
    private void detectTextFromImage() {
        if(imageBitmap!=null){
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
            FirebaseVisionTextDetector firebaseVisionTextRecognizer= FirebaseVision.getInstance().getVisionTextDetector();
            firebaseVisionTextRecognizer.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    displayText(firebaseVisionText);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_LONG).show();
                }
            });
        }


    }

    private void displayText(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blocks=firebaseVisionText.getBlocks();
        if(blocks.size()==0){
            Toast.makeText(MainActivity.this,"Nothing Found",Toast.LENGTH_SHORT).show();
        }
        else {
            for(FirebaseVisionText.Block blook: firebaseVisionText.getBlocks()){
                String text1=blook.getText();
                text.setText(text1);
            }
        }
    }
}
