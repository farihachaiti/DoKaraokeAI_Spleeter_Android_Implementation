package com.example.dokaraokeAI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class MainActivity2 extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 2;
    private static final String[] REQUEST_PERMISSION_CODES = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private Button btn_download, pickFile, extractAudio, extractVocal;
    private EditText getUrl;
    private TextInputLayout til;
    @SuppressLint("StaticFieldLeak")
    private static String url;
    private static final int PICKFILE_RESULT_CODE = 1;
    //private Bundle savedInstanceSt;
    public static AlertDialog progressDialog;
    @SuppressLint("StaticFieldLeak")
    private static MainActivity2 context;
    private long fileDownloadedId = -1;
    private DownloadManager dm;
    private Boolean isDownloaded;
    private Boolean isPicked;
    private File path;
    SplitVocal vocal = null;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.loadLibrary( "mobileffmpeg" );
        //savedInstanceSt = savedInstanceState;
        setContentView(R.layout.activity_main2);
        context = this;
        btn_download = findViewById(R.id.download_btn);
        pickFile = findViewById(R.id.pick_file);
        extractAudio = findViewById(R.id.extract_audio);
        extractVocal = findViewById(R.id.extract_vocal);
        getUrl = findViewById(R.id.editTextTLink);
        til = (TextInputLayout) getUrl.getParent().getParent();
        isDownloaded = false;
        isPicked = false;
        this.path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/doKaraokeAI/result/");
        MyApp.isInMultiWindowMode(MainActivity2.this);



        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        registerReceiver(onNotificationClick,
                new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));


        int permissionCheck = ContextCompat.checkSelfPermission(this, REQUEST_PERMISSION_CODES[0]);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, REQUEST_PERMISSION_CODES[1]);


        if ((permissionCheck != PackageManager.PERMISSION_GRANTED) && (permissionCheck2 != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, REQUEST_PERMISSION_CODES, REQUEST_PERMISSIONS);
        } else {

            doAction();

        }
    }



    public static void changeActivity(Context mContext) {
        Intent intent = new Intent(mContext, MainActivity3.class);
        mContext.startActivity(intent);
    }


    public static Context getContext() {
        return context;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed(){
        finishAffinity();
        super.onBackPressed();
    }


    protected void onDestroy() {
        unregisterReceiver(onComplete);
        unregisterReceiver(onNotificationClick);
        finishAffinity();
        super.onDestroy();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finishAffinity();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                doAction();
            }
        }
    }




    @SuppressLint("Range")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
                Uri uri = data.getData();

                assert uri != null;
                String uriString = uri.toString();

                String displayName = null;

                if (uriString.startsWith("content://")) {
                    Log.d("myTag011",uri.toString());
                    try (Cursor cursor = this.getContentResolver().query(uri, null, null, null, null)) {
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            cursor.close();
                        }
                    }
                }

            else if (uriString.startsWith("file://")) {
                    String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns._ID, MediaStore.MediaColumns.TITLE};
                    String id = Objects.requireNonNull(uri.getLastPathSegment()).split(":")[1];
                    try (Cursor cursor = this.getContentResolver().query(uri, projection, projection[1]+'='+id, null, null)) {

                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndexOrThrow(projection[2]));
                            int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                            url  = cursor.getString(columnIndex);
                            if(url == null) {
                                url = uri.getPath();
                            }
                            cursor.close();
                        }
                    }


                }
                try {
                    url = MyApp.getFilePath(getContext(),uri);
                    assert url != null;
                    Log.d("myTag012",url);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                getUrl.setText(displayName);
                isDownloaded = false;
                isPicked = true;


            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void doAction() {
        btn_download.setOnClickListener(v -> {
            if (getUrl.getText().toString().trim().equalsIgnoreCase("")) {
                til.setError("This field can not be blank");
            } else if (!URLUtil.isValidUrl(getUrl.getText().toString().trim())) {
                til.setError("Please put a valid download url");
            } else {
                til.setErrorEnabled(false);
                til.setError(null);
                url = getUrl.getText().toString().trim();

                try {
                    url = MyApp.getmedia(url);
                } catch (UnsupportedEncodingException unsupportedEncodingException) {
                    unsupportedEncodingException.printStackTrace();
                }
                if (url != null
                        && (url.contains("://youtu.be/") || url.contains("youtube.com/watch?v="))) {
                    getYoutubeDownloadUrl(url);

                } else {
                    download();
                }
            }
        });

        pickFile.setOnClickListener(view -> pickMedia());

        extractAudio.setOnClickListener(view -> {
            if (getUrl.getText().toString().trim().equalsIgnoreCase("")) {
                til.setErrorEnabled(true);
                til.setError("This field can not be blank");
            } else if (!URLUtil.isValidUrl(getUrl.getText().toString().trim()) && !getUrl.getText().toString().trim().endsWith(".mp4")) {
                til.setErrorEnabled(true);
                til.setError("Please put a valid download url or pick a file");
            } else if (!isDownloaded && !isPicked) {
                til.setErrorEnabled(true);
                til.setError("Please download or pick an mp4 file first");
            } else {
                til.setErrorEnabled(false);
                til.setError(null);
                //setProgressBarVisibility(true);
                ExtractAudio extractAudio = new ExtractAudio(url, progressDialog, isDownloaded);
                extractAudio.execute();

            }
        });


        extractVocal.setOnClickListener(v -> {
            if (getUrl.getText().toString().trim().equalsIgnoreCase("")) {
                til.setErrorEnabled(true);
                til.setError("This field can not be blank");
            } else if (!URLUtil.isValidUrl(getUrl.getText().toString().trim()) && !getUrl.getText().toString().trim().endsWith(".mp3")) {
                til.setErrorEnabled(true);
                til.setError("Please put a valid download url or pick a file");
            } else if (!isDownloaded && !isPicked) {
                til.setErrorEnabled(true);
                til.setError("Please download or pick an mp4 file first");
            } else {
                til.setErrorEnabled(false);
                til.setError(null);

                try {
                    vocal = new SplitVocal(url,context,progressDialog);
                    vocal.execute();
                    //vocal.playMusic(true);


                } catch (WavFileException | IOException | FileFormatNotSupportedException e) {
                    e.printStackTrace();
                }
            }


        });

    }


    @SuppressLint("ResourceAsColor")
    private void pickMedia(){
        CharSequence[] media_array = {"Video","Audio"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.getContext());
                builder.setTitle(R.string.pick_media)
                        .setItems(media_array, (dialog, which) -> {
                            if(which == 0){
                                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                                chooseFile.setType("video/*");
                                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                                startActivityIfNeeded(chooseFile, PICKFILE_RESULT_CODE);
                            }
                            else if(which == 1) {
                                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                                chooseFile.setType("audio/*");
                                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                                startActivityIfNeeded(chooseFile, PICKFILE_RESULT_CODE);
                            }
                                Log.d("myTag", String.valueOf(which));

                        })
                        .setIcon(R.mipmap.app_icon_round)
                        .setNegativeButton("Cancel", (dialog, i) -> dialog.dismiss());

         final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(R.color.Negative_button_alertdialog));

         alertDialog.show();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                String helpMessage = "Please use mp3 file only for audio \n" +
                        "Or mp4 file only for video";
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Instruction")
                        .setMessage(helpMessage)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setCancelable(true)
                        .show();

                return true;

            case R.id.action_files:
                // User chose the "Settings" item, show the app settings UI...
               /* Fragment fragment= new FragmentOne();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();*/
               changeActivity(context);

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }




    private void download() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast toast = Toast.makeText(getApplicationContext(), "Downloading File...", Toast.LENGTH_SHORT);
            toast.show();
        });
        Log.d("myTag0", url);

        downloadfile(url, context);


    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void downloadfile(String url, final Activity activity) {

        ShowProgress progress = new ShowProgress(progressDialog, "File Downloading...");
        progress.execute();
        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(url));
        String videoName = URLUtil.guessFileName(url, null, null);
        request1.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS+"/doKaraoke/", videoName);
        request1.setTitle(videoName);
        request1.setMimeType(MyApp.getMimeFromFileName(videoName));
        request1.allowScanningByMediaScanner();
        request1.setVisibleInDownloadsUi(true);
        request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        dm = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        assert dm != null;
        fileDownloadedId = dm.enqueue(request1);

    }




    @SuppressLint("StaticFieldLeak")
    private void getYoutubeDownloadUrl(String youtubeLink) {
        new YouTubeExtractor(this) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {

                List<Integer> iTags = Arrays.asList(22, 137, 18);
                for (Integer iTag : iTags) {

                    YtFile ytFile = ytFiles.get(iTag);

                    if (ytFile != null) {

                       url = ytFile.getUrl().trim();
                        download();

                    }

                }

            }
        }.extract(youtubeLink, true, false);

    }





    final BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {
            ShowProgress.dialogDismiss();
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast toast = Toast.makeText(getApplicationContext(), "File Download Complete!", Toast.LENGTH_SHORT);
                toast.show();
            });
            String action = intent.getAction();
            File mFile;
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == fileDownloadedId) {
                    Cursor c = dm.query(new DownloadManager.Query().setFilterById(fileDownloadedId));
                    if (c != null) {
                        c.moveToFirst();
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        try {
                            if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    @SuppressLint("Range") String fileUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                    mFile = new File(Objects.requireNonNull(Uri.parse(fileUri).getPath()));
                                }
                                else {
                                    @SuppressLint("Range") String filePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                                    mFile = new File(String.valueOf(Uri.parse(filePath)));
                                    Log.d("myTagdwn","working");
                                }
                                url = mFile.getAbsolutePath();
                                isDownloaded = true;
                                isPicked = false;
                            }
                        } catch (Exception e) {
                            Log.e("error", "Could not open the downloaded file");
                        }
                    }
                    assert c != null;
                    c.close();
                }

            }
        }
    };

    BroadcastReceiver onNotificationClick = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast toast = Toast.makeText(getApplicationContext(), "Downloading Video...", Toast.LENGTH_SHORT);
                toast.show();
            });

        }

    };


}

