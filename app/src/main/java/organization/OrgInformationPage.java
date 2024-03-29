package organization;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pharmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import homepage.HomePageOrg;

public class OrgInformationPage extends AppCompatActivity {
    EditText OrgNameText, OrgContactText, OrgAddressText;
    Button updateInfo;
    String userId;
    ProgressBar progressBar;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_information_page);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        OrgNameText = findViewById(R.id.OrgNameText);
        OrgContactText = findViewById(R.id.OrgContactText);
        OrgAddressText = findViewById(R.id.OrgAddressText);
        updateInfo = findViewById(R.id.OrgSubmit);
        progressBar = findViewById(R.id.orgInfoProgressBar);
        progressBar.setVisibility(View.GONE);

        userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("organization").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                OrgNameText.setText(value.getString("organizationName"));
                OrgContactText.setText(value.getString("contact"));
                OrgAddressText.setText(value.getString("city"));

            }
        });

    }

//    public void submitOrgInfoClick(View view) {
//        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//        String orgtext = OrgNameText.getText().toString();
//        String orgaddresstext = OrgAddressText.getText().toString();
//        String orgcontact = OrgContactText.getText().toString();
//
//        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                .setDisplayName(orgtext)
//                .build();
//
//        firebaseUser.updateProfile(profileUpdates)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            System.out.println("Task Successful");
//                        }
//                    }
//                });
//
//        String email = firebaseUser.getEmail();
//        String id = firebaseUser.getUid();
//        OrganizationClass organizationClass = new OrganizationClass(orgaddresstext, email, orgtext, orgcontact, null);
//        HashMap<String, Object> postUserData = new HashMap<>();
//
//        postUserData.put("organizatonName", organizationClass.getOrganizationName());
//        postUserData.put("contact", organizationClass.getContact());
//        postUserData.put("city", organizationClass.getCity());
//        postUserData.put("email", organizationClass.getEmail());
//
//        firebaseFirestore.collection("organization").document(id).update(postUserData);
//
//
//    }

    public void UpdateOrgInfo(View view) {
        updateInfo.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userEmail = firebaseUser.getEmail();

        String orgName = OrgNameText.getText().toString();
        String orgAdress = OrgAddressText.getText().toString();
        String orgContact = OrgContactText.getText().toString();

        HashMap<String, Object> postData = new HashMap<>();
        postData.put("organizationName", orgName);
        postData.put("city", orgAdress);
        postData.put("contact", orgContact);

        firebaseFirestore.collection("organization").document(userId).update(postData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(OrgInformationPage.this, "Profile Updated Successfully ", Toast.LENGTH_LONG).show();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(orgName)
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
                    Intent intent = new Intent(OrgInformationPage.this, HomePageOrg.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(OrgInformationPage.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}