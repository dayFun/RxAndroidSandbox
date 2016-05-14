package com.lighthouse.rxsandbox;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.WidgetObservable;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.validateButton)
    Button validateButton;
    @Bind(R.id.nameEditText)
    EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setBadTimeZone();
        createEditTextObservable();
    }


    private void setBadTimeZone() {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setTimeZone("Europe/London");
    }

    private void createEditTextObservable() {

        WidgetObservable.text(nameEditText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onTextChangeEvent -> {
                            if (checkName(onTextChangeEvent.text().toString())) {
                                validateButton.setEnabled(true);
                            }
                        }
                );
    }

    private Boolean checkName(String enteredName) {
        return enteredName.matches("^[A-Za-z]+\\.[A-Za-z]+");
    }

    @OnClick(R.id.validateButton)
    public void validate() {
        Toast.makeText(MainActivity.this, "Validate Clicked", Toast.LENGTH_SHORT).show();
    }
}
