package com.example.dokaraokeAI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.Objects;


@SuppressLint("StaticFieldLeak")
class ShowProgress extends AsyncTask<String, String, String> {
    private static ProgressBar bar;
    private static TextView label;
    private static AlertDialog.Builder builder;
    private static AlertDialog progressDialog;
    private static TextView labelText;
    private static String string;




    public ShowProgress(AlertDialog pd, String str) {
        progressDialog = pd;
        string = str;
    }


    public void getDialogProgressbar(Context context){
        if (progressDialog == null)
            progressDialog = new AlertDialog.Builder(context, R.style.CustomProgressDialog).create();

        View loadview = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog,null);
        progressDialog.setView(loadview,0,0,0,0);

        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        label = loadview.findViewById(R.id.editTextLabel);
        bar = loadview.findViewById(R.id.progressBar1);
        label.setText(string);
        bar.setProgress(0);
        bar.setMax(100);
        progressDialog.show();
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.getWindow().setLayout(700,600);
    }



    @Override
    protected void onPreExecute() {
        getDialogProgressbar(MainActivity2.getContext());
    //    loadingLabelProgress();
        super.onPreExecute();


    }

    @SuppressLint("WrongThread")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected String doInBackground(String... strings) {
        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            bar.incrementProgressBy(10);
        }
        return "completed";

    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onPostExecute(String s) {
    //    dialogDismiss();
        cancel(true);
        super.onPostExecute("hi");
    }


    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCancelled() {
        try {
            handleCancelMethod();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void handleCancelMethod() throws IOException {
        dialogDismiss();
        cancel(true);

        //finishAffinity();

    }

    public static void dialogDismiss(){
        if (progressDialog != null && progressDialog.isShowing()) {
            bar.setVisibility(View.GONE);
            label.setText("");
            label.setVisibility(View.GONE);
            progressDialog.dismiss();
        }
    }



    private void loadingLabelProgress() {
        final Handler handler = new Handler(Looper.getMainLooper());
        label.setText(label.getText().toString().replaceAll("\\.+$",""));
        Runnable runnable = new Runnable() {

            int count = 0;

            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                count++;

                if (this.count == 1) {
                    label.setText(label.getText().toString().replaceAll("\\.+$",""));
                    label.setText(label.getText() + ".");
                }
                else if (this.count == 2) {
                    label.setText(label.getText().toString().replaceAll("\\.+$",""));
                    label.setText(label.getText() + "..");
                } else if (this.count == 3) {
                    label.setText(label.getText().toString().replaceAll("\\.+$",""));
                    label.setText(label.getText() +"...");

                }

                if (this.count == 3)
                    this.count = 0;

                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 1000);
        label.setText(label.getText().toString().replaceAll("\\.+$",""));

    }
}



