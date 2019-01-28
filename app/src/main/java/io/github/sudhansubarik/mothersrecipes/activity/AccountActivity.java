package io.github.sudhansubarik.mothersrecipes.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.sudhansubarik.mothersrecipes.MainActivity;
import io.github.sudhansubarik.mothersrecipes.R;
import io.github.sudhansubarik.mothersrecipes.firebase.LoginActivity;

public class AccountActivity extends AppCompatActivity {

    @BindView(R.id.my_profile_textView)
    TextView profile;
    @BindView(R.id.my_recipes_textView)
    TextView recipes;
    @BindView(R.id.change_password_textView)
    TextView changePassword;
    @BindView(R.id.sign_out_textView)
    TextView signOut;
    @BindView(R.id.account_activity_progressBar)
    ProgressBar progressBar;

    private FirebaseAuth auth;
    private String emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();

        if (user != null) {
            emailId = user.getEmail();
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        recipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMyRecipes();
            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChangePassword();
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSignOut();
            }
        });
    }

    private void viewMyRecipes() {
        Intent intent = new Intent(this, MyRecipeActivity.class);
        startActivity(intent);
    }

    private void setChangePassword() {
        progressBar.setVisibility(View.VISIBLE);
        if (!emailId.equals("")) {
            auth.sendPasswordResetEmail(emailId)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AccountActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(AccountActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    private void setSignOut() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
        builder.setTitle("Info");
        builder.setMessage("Do you want to logout ??");
        builder.setPositiveButton("Take me away!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                auth.signOut();
                startActivity(new Intent(AccountActivity.this, MainActivity.class));
                Toast.makeText(AccountActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
