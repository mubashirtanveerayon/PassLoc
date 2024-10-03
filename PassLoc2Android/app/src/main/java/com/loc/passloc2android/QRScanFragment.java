package com.loc.passloc2android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.Result;
import com.loc.service.passloc.generator.qr.QRCodeGenerator;

import java.util.ArrayList;


public class QRScanFragment extends Fragment implements DecodeCallback,View.OnClickListener,Runnable {
    CodeScanner codeScanner;

    CodeScannerView scannerView;

    boolean prompted = false;

    View view;

    Button scanNextButton;
    ImageButton resetButton;

    TextView scannedTextView;
    ArrayList<String> data;

    String text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_qr_scan, container, false);

        scannerView = view.findViewById(R.id.codeScannerView);
        codeScanner = new CodeScanner(getActivity(),scannerView);
        codeScanner.setDecodeCallback(this);


        scanNextButton = view.findViewById(R.id.scanNextButton);
        resetButton = view.findViewById(R.id.resetButton);
        scannedTextView = view.findViewById(R.id.scannedTextView);
        data = new ArrayList<>();


        return view;
    }

    public void onResume(){
        super.onResume();

        data.clear();
        scannedTextView.setText("Chunk scanned: "+data.size());
        scanNextButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

    }

    @Override
    public void onDecoded(@NonNull Result result) {


        text = result.getText();
        getActivity().runOnUiThread(this);



    }
    @Override
    public void run() {

        if(text == null)
            return;
        if(isChunk(text)){

            codeScanner.stopPreview();
            data.add(text);

            scannedTextView.setText("Chunk scanned: "+data.size());

            Toast.makeText(getActivity(),"Proceed to the next QR code.",Toast.LENGTH_LONG).show();
        }else{
            restartCodeScanner();
        }
    }


    public ArrayList<String> getChunks(){
        return new ArrayList<>(data);
    }


    public static boolean isChunk(String text){
        if(text == null)
            return false;

        String[] parts = text.split(QRCodeGenerator.CHUNK_DATA_SEPARATOR);

        if(parts.length != 3)
            return false;

        try{
            Integer.parseInt(parts[1]);
        }catch(NumberFormatException ex){
            return false;
        }

        return true;
    }


    public void restartCodeScanner(){
        codeScanner.stopPreview();
        if(!codeScanner.isPreviewActive()){

            codeScanner.startPreview();



            if(!prompted){
                prompted=true;
                Snackbar.make(view, "Don't see the camera? Give permission and scan again.", Snackbar.LENGTH_LONG)
                        .setAction("Give permission", this)
                        .show();
            }





        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.scanNextButton){

            restartCodeScanner();

        }else if(view.getId() == R.id.resetButton){
            data.clear();
            scannedTextView.setText("Chunk scanned: 0");
        }else{
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package",getActivity(). getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}