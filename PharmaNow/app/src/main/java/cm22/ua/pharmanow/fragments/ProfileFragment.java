package cm22.ua.pharmanow.fragments;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cm22.ua.pharmanow.activity.LoginActivity;
import cm22.ua.pharmanow.R;
import cm22.ua.pharmanow.activity.SignupActivity;


public class ProfileFragment extends Fragment{

    private Button changeEmail;
    private Button changePassword;
    private Button sendEmail;
    private Button remove;
    private EditText oldEmail, newEmail, password, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        View root = inflater.inflate(R.layout.profile_activity, container, false);
        //  more UI from your activity ....

        //setContentView(R.layout.profile_activity);


        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = firebaseAuth -> {
            FirebaseUser user1 = firebaseAuth.getCurrentUser();
            if (user1 == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(getActivity(), LoginActivity.class));
                //finish();
            }
        };

        Button btnChangeEmail = root.findViewById(R.id.change_email_button);
        Button btnChangePassword = root.findViewById(R.id.change_password_button);
        Button btnSendResetEmail = root.findViewById(R.id.sending_pass_reset_button);
        Button btnRemoveUser = root.findViewById(R.id.remove_user_button);
        changeEmail = root.findViewById(R.id.changeEmail);
        changePassword = root.findViewById(R.id.changePass);
        sendEmail = root.findViewById(R.id.send);
        remove = root.findViewById(R.id.remove);
        Button signOut = root.findViewById(R.id.sign_out);

        oldEmail = root.findViewById(R.id.old_email);
        newEmail = root.findViewById(R.id.new_email);
        password = root.findViewById(R.id.password);
        newPassword = root.findViewById(R.id.newPassword);

        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);

        progressBar = root.findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnChangeEmail.setOnClickListener(v -> {
            oldEmail.setVisibility(View.GONE);
            newEmail.setVisibility(View.VISIBLE);
            password.setVisibility(View.GONE);
            newPassword.setVisibility(View.GONE);
            changeEmail.setVisibility(View.VISIBLE);
            changePassword.setVisibility(View.GONE);
            sendEmail.setVisibility(View.GONE);
            remove.setVisibility(View.GONE);
        });

        changeEmail.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if (user != null && !newEmail.getText().toString().trim().equals("")) {
                user.updateEmail(newEmail.getText().toString().trim())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                signOut();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getActivity(), "Failed to update email!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            } else if (newEmail.getText().toString().trim().equals("")) {
                newEmail.setError("Enter email");
                progressBar.setVisibility(View.GONE);
            }
        });

        btnChangePassword.setOnClickListener(v -> {
            oldEmail.setVisibility(View.GONE);
            newEmail.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            newPassword.setVisibility(View.VISIBLE);
            changeEmail.setVisibility(View.GONE);
            changePassword.setVisibility(View.VISIBLE);
            sendEmail.setVisibility(View.GONE);
            remove.setVisibility(View.GONE);
        });

        changePassword.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if (user != null && !newPassword.getText().toString().trim().equals("")) {
                if (newPassword.getText().toString().trim().length() < 6) {
                    newPassword.setError("Password too short, enter minimum 6 characters");
                    progressBar.setVisibility(View.GONE);
                } else {
                    user.updatePassword(newPassword.getText().toString().trim())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                    signOut();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(getActivity(), "Failed to update password!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
            } else if (newPassword.getText().toString().trim().equals("")) {
                newPassword.setError("Enter password");
                progressBar.setVisibility(View.GONE);
            }
        });

        btnSendResetEmail.setOnClickListener(v -> {
            oldEmail.setVisibility(View.VISIBLE);
            newEmail.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            newPassword.setVisibility(View.GONE);
            changeEmail.setVisibility(View.GONE);
            changePassword.setVisibility(View.GONE);
            sendEmail.setVisibility(View.VISIBLE);
            remove.setVisibility(View.GONE);
        });

        sendEmail.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if (!oldEmail.getText().toString().trim().equals("")) {
                auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getActivity(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            } else {
                oldEmail.setError("Enter email");
                progressBar.setVisibility(View.GONE);
            }
        });

        btnRemoveUser.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if (user != null) {
                user.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), SignupActivity.class));
                                //finish();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getActivity(), "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });

        signOut.setOnClickListener(v -> signOut());
        return root;
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }


}