package com.example.bigfilefinder3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ir.androidexception.filepicker.dialog.DirectoryPickerDialog;

public class MainActivity extends AppCompatActivity {

    EditText myETNumberOfRequestFiles;
    ArrayList<String> mySearchFolderPaths = new ArrayList<String>();
    ArrayAdapter<String> mySearchFolderPathsAdapter;
    FileManager myFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 109);

        myETNumberOfRequestFiles = (EditText) findViewById(R.id.number_ET);
        setUpListeners();
        myFileManager = new FileManager((String folderPath) -> showNotificationAboutSearchingInNextFolder(folderPath));
    }

    private void setUpListeners() {
        Button btnFilePicker = findViewById(R.id.pickFile_BTN);
        btnFilePicker.setOnClickListener(clickOnFilePickerListener);

        Button btnFindBig = findViewById(R.id.findBig_BTN);
        btnFindBig.setOnClickListener(clickOnFindBigFileListener);

        ListView lvSearchFolderPaths = findViewById(R.id.listItemLV);
        lvSearchFolderPaths.setOnItemLongClickListener(itemLongClickListener);

        Button btnPlus = findViewById(R.id.plus_BTN);
        btnPlus.setOnClickListener(clickListener);

        Button btnMinus = findViewById(R.id.minus_BTN);
        btnMinus.setOnClickListener(clickListener);

        mySearchFolderPathsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mySearchFolderPaths);
        lvSearchFolderPaths.setAdapter(mySearchFolderPathsAdapter);
    }

    private View.OnClickListener clickOnFilePickerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openFolderPicker();
        }
    };

    private View.OnClickListener clickOnFindBigFileListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int numberOfRequestFiles = Integer.parseInt(myETNumberOfRequestFiles.getText().toString());
            ArrayList<String> resultFilePaths = myFileManager.getNBiggestSortedFiles(mySearchFolderPaths, numberOfRequestFiles);

            Intent startIntent = new Intent(getApplicationContext(), ResultsActivity.class);
            startIntent.putExtra("com.example.bigfilefinder3.ResultFilePaths", resultFilePaths);
            startActivity(startIntent);
        }
    };

    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int item, long l) {
            removeSearchFolderWithApproval(item, view);
            return false;
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.plus_BTN:
                    increaseNumberOfRequestFiles();
                    break;
                case R.id.minus_BTN:
                    decreaseNumberOfRequestFiles();
                    break;
            }
        }
    };

    private void increaseNumberOfRequestFiles() {
        if (myETNumberOfRequestFiles.getText() == null && myETNumberOfRequestFiles.getText().toString().isEmpty()) {
            return;
        }
        int numberOfRequiredResultFiles = Integer.parseInt(myETNumberOfRequestFiles.getText().toString());
        myETNumberOfRequestFiles.setText(String.valueOf(++numberOfRequiredResultFiles));
    }

    private void decreaseNumberOfRequestFiles() {
        if (myETNumberOfRequestFiles.getText() == null && myETNumberOfRequestFiles.getText().toString().isEmpty()) {
            return;
        }
        int numberOfRequiredResultFiles = Integer.parseInt(myETNumberOfRequestFiles.getText().toString());
        myETNumberOfRequestFiles.setText(String.valueOf(--numberOfRequiredResultFiles));
    }

    private void removeSearchFolderWithApproval(int clickedItemIndex, View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int result) {
                if (result == DialogInterface.BUTTON_POSITIVE) {
                    mySearchFolderPaths.remove(clickedItemIndex);
                    mySearchFolderPathsAdapter.notifyDataSetChanged();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Do you want to delete clicked directory path?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void openFolderPicker() {
        DirectoryPickerDialog directoryPickerDialog = new DirectoryPickerDialog(this,
                () -> {
                },
                files -> {
                    mySearchFolderPaths.add(files[0].getPath());
                    mySearchFolderPathsAdapter.notifyDataSetChanged();
                }
        );
        directoryPickerDialog.show();
    }

    private void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            MainActivity.this,
                            new String[]{permission},
                            requestCode);
        }
    }

    private void showNotificationAboutSearchingInNextFolder(String folderPath) {
        showUiNotification(folderPath);
        showSystemNotification(folderPath);
    }

    private void showUiNotification(String folderPath) {
        String msg = "Started searching in folder: " + folderPath;
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showSystemNotification(String folderPath) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Notification")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Started searching in folder")
                .setContentText(folderPath)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOnlyAlertOnce(true);
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1, builder.build());
    }
}