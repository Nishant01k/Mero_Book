package com.example.merobook.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.example.merobook.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PDFViewerActivity extends AppCompatActivity {

    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);

        pdfView = findViewById(R.id.pdfView);

        String filename = getIntent().getStringExtra("filename");
        File file = new File(getExternalFilesDir(null), filename);
        Uri uri = Uri.fromFile(file);
        pdfView.fromUri(uri).load();
    }
}