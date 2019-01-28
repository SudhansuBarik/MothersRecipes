package io.github.sudhansubarik.mothersrecipes;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import io.github.sudhansubarik.mothersrecipes.activity.AccountActivity;
import io.github.sudhansubarik.mothersrecipes.activity.HelpCentreActivity;
import io.github.sudhansubarik.mothersrecipes.firebase.LoginActivity;
import io.github.sudhansubarik.mothersrecipes.firebase.UserInformation;
import io.github.sudhansubarik.mothersrecipes.fragments.BreadsFragment;
import io.github.sudhansubarik.mothersrecipes.fragments.DessertsFragment;
import io.github.sudhansubarik.mothersrecipes.fragments.HomeFragment;
import io.github.sudhansubarik.mothersrecipes.fragments.NorthIndianFragment;
import io.github.sudhansubarik.mothersrecipes.fragments.RiceAndBiryaniFragment;
import io.github.sudhansubarik.mothersrecipes.fragments.SouthIndiaFragment;
import io.github.sudhansubarik.mothersrecipes.fragments.StartersFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_BREADS = "breads";
    private static final String TAG_STARTERS = "starters";
    private static final String TAG_RICE_AND_BIRYANI = "rice &amp; biryani";
    private static final String TAG_NORTH_INDIAN = "north indian";
    private static final String TAG_SOUTH_INDIAN = "south indian";
    private static final String TAG_DESSERTS = "desserts";

    // index to identify current nav menu item
    public static int navItemIndex = 0;
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    private TextView loginSignUpTextView, nameTextView, emailTextView;
    private ImageView profileImageView;
    private Handler handler;
    private Intent intent;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        databaseReference = firebaseDatabase.getReference("users");
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();


//        authListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                user = firebaseAuth.getCurrentUser();
//                if (user == null) {
//                    // user auth state is changed - user is null
//                    // make the SignUp|Login TextView visible and name and email TextView invisible
//                    loginSignUpTextView.setVisibility(View.VISIBLE);
//                    nameTextView.setVisibility(View.GONE);
//                    emailTextView.setVisibility(View.GONE);
//                }
//            }
//        };

        handler = new Handler();

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Navigation view header
        final View navHeader = navigationView.getHeaderView(0);
        loginSignUpTextView = navHeader.findViewById(R.id.login_signUp_textView);
        nameTextView = navHeader.findViewById(R.id.name_textView);
        emailTextView = navHeader.findViewById(R.id.email_textView);
        profileImageView = navHeader.findViewById(R.id.profile_imageView);

        // If the user is not logged in then display login/signUp option in NavHeader
        if (auth.getCurrentUser() == null) {
            loginSignUpTextView.setVisibility(View.VISIBLE);
            nameTextView.setVisibility(View.GONE);
            emailTextView.setVisibility(View.GONE);

            loginSignUpTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            });
        }

        loadNavHeader();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadMainFragment();
        }
    }

    /***
     * Load navigation menu header information
     * like name, email ID
     */
    private void loadNavHeader() {

        if (user != null) {
            databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);

                    // Check for null
                    if (userInformation == null) {
                        Log.e(TAG, "User data is null!");
                    } else {
                        // Display user information
                        nameTextView.setText(userInformation.name);
                        emailTextView.setText(userInformation.email);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.e(TAG, "Failed to read user", databaseError.toException());
                }
            });
        }

        profileImageView.setImageResource(R.mipmap.ic_launcher);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    @SuppressLint("RestrictedApi")
    private void loadMainFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getMainFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.content_main, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            handler.post(mPendingRunnable);
        }
        //Closing drawer on item click
        drawer.closeDrawers();
        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getMainFragment() {
        switch (navItemIndex) {
            case 0:
                // home fragment
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // breads fragment
                BreadsFragment breadsFragment = new BreadsFragment();
                return breadsFragment;
            case 2:
                // starters fragment
                StartersFragment startersFragment = new StartersFragment();
                return startersFragment;
            case 3:
                // Rice and Biryani fragment
                RiceAndBiryaniFragment riceAndBiryaniFragment = new RiceAndBiryaniFragment();
                return riceAndBiryaniFragment;
            case 4:
                // north indian fragment
                NorthIndianFragment northIndianFragment = new NorthIndianFragment();
                return northIndianFragment;
            case 5:
                // south indian fragment
                SouthIndiaFragment southIndiaFragment = new SouthIndiaFragment();
                return southIndiaFragment;
            case 6:
                // desserts fragment
                DessertsFragment dessertsFragment = new DessertsFragment();
                return dessertsFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void myAccount() {
        if (user == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Info");
            builder.setMessage("Please Login to continue");
            builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    auth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadMainFragment();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
        } else if (id == R.id.nav_breads) {
            navItemIndex = 1;
            CURRENT_TAG = TAG_BREADS;
        } else if (id == R.id.nav_starters) {
            navItemIndex = 2;
            CURRENT_TAG = TAG_STARTERS;
        } else if (id == R.id.nav_rice) {
            navItemIndex = 3;
            CURRENT_TAG = TAG_RICE_AND_BIRYANI;
        } else if (id == R.id.nav_north_indian) {
            navItemIndex = 4;
            CURRENT_TAG = TAG_NORTH_INDIAN;
        } else if (id == R.id.nav_south_indian) {
            navItemIndex = 5;
            CURRENT_TAG = TAG_SOUTH_INDIAN;
        } else if (id == R.id.nav_desserts) {
            navItemIndex = 6;
            CURRENT_TAG = TAG_DESSERTS;
        } else if (id == R.id.nav_account) {
            myAccount();
        } else if (id == R.id.nav_help_centre) {
            intent = new Intent(this, HelpCentreActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Hey check out MedKlick at: https://play.google.com/store/apps/details?id=com.google.android.apps.plus");
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        }

        loadMainFragment();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
