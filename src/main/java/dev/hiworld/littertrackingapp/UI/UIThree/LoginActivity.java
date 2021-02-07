package dev.hiworld.littertrackingapp.UI.UIThree;

import androidx.appcompat.app.AppCompatActivity;
import dev.hiworld.littertrackingapp.R;
import dev.hiworld.littertrackingapp.UI.UIThree.*;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class LoginActivity extends AppCompatActivity {

    // Globals
    private GoogleSignInClient SignIn;
    private int RC_SIGN_IN = 69;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure Login
        GoogleSignInOptions SignOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        // Get Login
        GoogleSignInClient SignIn = GoogleSignIn.getClient(this, SignOptions);
        Intent signInIntent = SignIn.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // Got to home page
            Intent intent = new Intent(this, HomeActvity.class);
            startActivity(intent);
        }
    }
}