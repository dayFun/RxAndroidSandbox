package com.lighthouse.convertasynctask;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_activity_layout);
        TextView text = (TextView) findViewById(R.id.main_message);

        subscription = getGistObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Gist>() {
                    @Override
                    public void onCompleted() {
                        Snackbar.make(mainLayout, "Observable OnComplete", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage(), e);
                    }

                    @Override
                    public void onNext(Gist gist) {
                        StringBuilder sb = new StringBuilder();

                        // Output
                        for (Map.Entry<String, GistFile> entry : gist.files.entrySet()) {
                            sb.append(entry.getKey());
                            sb.append(" - ");
                            sb.append("Length of file ");
                            sb.append(entry.getValue().content.length());
                            sb.append("\n");
                        }

                        if (text != null) {
                            text.setText(sb.toString());
                        }

                    }
                });
    }


    @Nullable
    private Gist getGist() throws IOException {
        OkHttpClient client = new OkHttpClient();
        // Go get this Gist: https://gist.github.com/donnfelker/db72a05cc03ef523ee74
        // via the GitHub API
        Request request = new Request.Builder()
                .url("https://api.github.com/gists/db72a05cc03ef523ee74")
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            Gist gist = new Gson().fromJson(response.body().charStream(), Gist.class);
            return gist;
        }

        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public Observable<Gist> getGistObservable() {
        return Observable.defer(() -> {
            try {
                return Observable.just(getGist());
            } catch (IOException e) {
                return null;
            }
        });
    }

    public static void showCustomDialog(String title, String message, Context activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

}