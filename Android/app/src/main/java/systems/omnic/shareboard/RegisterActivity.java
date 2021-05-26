package systems.omnic.shareboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Entered Method");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView iconView = findViewById(R.id.regIconView);
        Button cancel = findViewById(R.id.reg_cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate: Cancel Button selected");
                finish();
            }
        });

        Button register = findViewById(R.id.reg_register_btn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate: Register Button selected");
                register();
            }
        });
    }

    public void register(){
        Log.d(TAG, "register: Method entered");

        EditText username = findViewById(R.id.regEditUsername);
        EditText password = findViewById(R.id.regEditPassword);
        EditText passwordConf = findViewById(R.id.regEditPassword2);
        EditText email = findViewById(R.id.regEditEmail);
        TextView viewErrMsg = findViewById(R.id.regViewErrMsg);

        String errorMessage = getResources().getString(R.string.register_error_message);
        if (username.getText().toString().equals("") || username.getText().toString().contains("[^\\w]"))
            viewErrMsg.setText(errorMessage);
        else if (password.getText().toString().equals("") || passwordConf.getText().toString().equals("") ||
                password.getText().toString().contains("[^\\w]") || !password.getText().toString().equals(passwordConf.getText().toString()))
            viewErrMsg.setText(errorMessage);
        else if (email.getText().toString().equals("") || !email.getText().toString().contains("@"))
            viewErrMsg.setText(errorMessage);
    }
}