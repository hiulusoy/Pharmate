package signup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import com.example.pharmate.Loadingbar;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

import com.example.pharmate.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import fragments.Choose;

import medicine.UploadMedicine;

import location.LocationTracker;
import models.OrganizationClass;


public class SignUpOrg extends AppCompatActivity {
    AwesomeValidation awesomeValidation;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private LocationTracker locationTracker;

    Button signUpClickBtn;
    CardView cardView;
    EditText emailText, passwordText, OrgNameText, OrgContactText, OrgAddressText, confirmpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_org);

        signUpClickBtn = findViewById(R.id.signUpClickBtn);
        cardView = findViewById(R.id.cardview);
        final Loadingbar loadingbar = new Loadingbar(SignUpOrg.this);

        signUpClickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardView.setVisibility(View.GONE);

                    }
                }, 5000);
            }
        });
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        OrgNameText = findViewById(R.id.OrgNameText);
        OrgContactText = findViewById(R.id.OrgContactText);
        OrgAddressText = findViewById(R.id.OrgAddressText);
        emailText = findViewById(R.id.signUpEmail);
        passwordText = findViewById(R.id.signUpPasswrd);
        confirmpassword = findViewById(R.id.signUpPassword);

        awesomeValidation.addValidation(SignUpOrg.this, R.id.OrgNameText, "[a-zA-Z\\s]+", R.string.nameerror);
        awesomeValidation.addValidation(SignUpOrg.this, R.id.signUpPasswrd, "[a-zA-Z\\d\\!@#.\\$%&\\*]{8,}", R.string.passworderror);
        awesomeValidation.addValidation(SignUpOrg.this, R.id.signUpPassword, "[a-zA-Z\\d\\!@#.\\$%&\\*]{8,}", R.string.passworderror);
        awesomeValidation.addValidation(SignUpOrg.this, R.id.signUpEmail, android.util.Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(SignUpOrg.this, R.id.OrgContactText, RegexTemplate.TELEPHONE, R.string.mobileerror);


    }

    public void signUp(View view) {
        if (awesomeValidation.validate()) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            String orgtext = OrgNameText.getText().toString();
            String orgaddresstext = OrgAddressText.getText().toString();
            String orgcontact = OrgContactText.getText().toString();
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(orgtext + orgaddresstext)
                                                        .build();
                                                firebaseUser.updateProfile(profileUpdates)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    System.out.println("Task Successful");
                                                                }
                                                            }
                                                        });

                                                String id = firebaseUser.getUid();
                                                LatLng organizationLocation = getLocation();
                                                GeoPoint location = new GeoPoint(organizationLocation.latitude, organizationLocation.longitude);

                                                OrganizationClass organizationClass = new OrganizationClass(orgaddresstext, email, orgtext, orgcontact, location);
                                                HashMap<String, Object> postUserData = new HashMap<>();

                                                postUserData.put("organizationName", organizationClass.getOrganizationName());
                                                postUserData.put("contact", organizationClass.getContact());
                                                postUserData.put("city", organizationClass.getCity());
                                                postUserData.put("location", organizationClass.getLocation());
                                                postUserData.put("email", organizationClass.getEmail());

                                                firebaseFirestore.collection("organization").document(id).set(postUserData);
                                                Toast.makeText(SignUpOrg.this, "please check your email", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(SignUpOrg.this, Choose.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(SignUpOrg.this, task.getException().getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpOrg.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });


        }
    }

    public LatLng getLocation() {
        locationTracker = new LocationTracker(SignUpOrg.this);
        LatLng location;
        if (locationTracker.canGetLocation()) {
            location = new LatLng(locationTracker.getLatitude(), locationTracker.getLongitude());
            System.out.println(location);
            return location;
        } else {
            locationTracker.showSettingsAlert();
            return null;
        }
    }

}