package dev.hiworld.littertrackingapp.UI.UIThree.HomeFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import dev.hiworld.littertrackingapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFrag extends Fragment {

    // Globals
    private int RC_SIGN_IN = 69;

    public AccountFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFrag newInstance(String param1, String param2) {
        AccountFrag fragment = new AccountFrag();
        return fragment;
    }

    private void LogOn(GoogleSignInClient SignIn) {
        // Log on
        Intent signInIntent = SignIn.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void LogOff(GoogleSignInClient SignIn) {
        SignIn.signOut()
            .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    GoToHome();
                }
            });

    }

    private void RevokeAcess(GoogleSignInClient SignIn) {
        // Toast
        Toast.makeText(getActivity(), getString(R.string.info_generic_hold), Toast.LENGTH_SHORT).show();

        SignIn.revokeAccess()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        GoToHome();
                    }
                });
    }

    private void GoToHome() {
        // Go to home fragemnt
        NavDirections action = AccountFragDirections.actionSettingsFragToMappyFrag();
        Navigation.findNavController(getActivity(), R.id.Frag).navigate(action);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Configure Login
        GoogleSignInOptions SignOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        // Get Instant Login
        boolean isInstant = AccountFragArgs.fromBundle(getArguments()).getInstaLogin();

        // Get Login
        GoogleSignInClient SignIn = GoogleSignIn.getClient(getActivity(), SignOptions);

        // Get Components
        SignInButton LogOn = getView().findViewById(R.id.SignIn);
        Button LogOff = getView().findViewById(R.id.LogOff);
        Button ClearData = getView().findViewById(R.id.ClearData);
        TextView LFWarning = getView().findViewById(R.id.LFWarn);
        TextView Title = getView().findViewById(R.id.WelcomeMsg);

        // Check if user is logged in
        GoogleSignInAccount Account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (Account == null) {
            // Set Enabled
            LogOff.setEnabled(false);
            ClearData.setEnabled(false);

            // Set title
            Title.setText(getString(R.string.label_account_default));

            // Set Warning
            LFWarning.setVisibility(View.VISIBLE);
        } else {
            // Set Enabled
            LogOn.setEnabled(false);

            // Set title
            Title.setText(Account.getDisplayName());

            // Set Warning
            LFWarning.setVisibility(View.GONE);
        }

        // Set Onclick Listeners
        LogOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOn(SignIn);
            }
        });

        ClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RevokeAcess(SignIn);
            }
        });

        LogOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOff(SignIn);
            }
        });

        // Instantly login
        if (isInstant) {
            LogOn(SignIn);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // Got to home page
            GoToHome();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }
}