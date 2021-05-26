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

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView iconView = findViewById(R.id.loginIconView);
        Button register = findViewById(R.id.login_register_btn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate: Register Button selected");
                TextView viewErrMsg = findViewById(R.id.loginViewErrMsg);
                viewErrMsg.setText("");
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Button login = findViewById(R.id.login_login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate: Login Button selected");
                login();
            }
        });
    }

    public void login(){
        Log.d(TAG, "login: Method entered");

        EditText username = findViewById(R.id.loginEditUser);
        EditText password = findViewById(R.id.loginEditPw);
        TextView viewErrMsg = findViewById(R.id.loginViewErrMsg);

        if (username.getText().toString().equals("") || username.getText().toString().contains("[^\\w]") ||
                password.getText().toString().equals("") || password.getText().toString().contains("[^\\w]")){
            viewErrMsg.setText(getResources().getString(R.string.login_error_message));
            return;
        }
    }

    @Override
    public void onBackPressed(){
        Log.d(TAG, "onBackPressed: Method entered");
    }
}