package organization;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmate.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import medicine.MedicineAdapter;
import medicine.ReceiveMedicine;
import medicine.RequestMedicine;
import medicine.SearchMedicine;
import models.MedicineClass;

import models.OrganizationClass;

public class OrganizatonListPage extends AppCompatActivity {
    EditText nameorganization;
    Button list;
    RecyclerView recyclerView1;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private OrganizationAdapter adapter;
    private CollectionReference organizationReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizaton_list_page);

        nameorganization = findViewById(R.id.editTextTextPersonName16);
        list = findViewById(R.id.button9);
        organizationReference = db.collection("organization");
        recyclerView1 = findViewById(R.id.org_recycler_view);

        setUpRecyclerView();
        nameorganization.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                Query query = organizationReference;

                FirestoreRecyclerOptions<OrganizationClass> options1 = new FirestoreRecyclerOptions.Builder<OrganizationClass>()
                        .setQuery(query, OrganizationClass.class)
                        .build();
                adapter = new OrganizationAdapter(options1);

            }
        });


    }

    private void setUpRecyclerView() {

        Query denemQuery = organizationReference;

        Query query = organizationReference;
        System.out.println(nameorganization.getText().toString().trim());

        if (nameorganization.getText().toString().trim().equals(null) || nameorganization.getText().toString().trim().equals("")) {

            FirestoreRecyclerOptions<OrganizationClass> options1 = new FirestoreRecyclerOptions.Builder<OrganizationClass>()
                    .setQuery(denemQuery, OrganizationClass.class)
                    .build();
            adapter = new OrganizationAdapter(options1);


        } else {

            FirestoreRecyclerOptions<OrganizationClass> options2 = new FirestoreRecyclerOptions.Builder<OrganizationClass>()
                    .setQuery(query, OrganizationClass.class)
                    .build();
            adapter = new OrganizationAdapter(options2);
        }

        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setAdapter(adapter);
    }
  
 private void setUpRecyclerViewSearch() {

        Query query = organizationReference.whereEqualTo("manager", "SaglikOcagi").orderBy("manager");
        System.out.println(nameorganization.getText().toString().trim());
        FirestoreRecyclerOptions<OrganizationClass> options2 = new FirestoreRecyclerOptions.Builder<OrganizationClass>()
                .setQuery(query, OrganizationClass.class)
                .build();
        adapter = new OrganizationAdapter(options2);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
        recyclerView1.setAdapter(adapter);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Information");
        alert.setMessage("Institution not found!");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "kurum yok", Toast.LENGTH_LONG).show();
            }

        });
        alert.create().show();


    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();

    }


    public void listOrganizationClick(View view) {
        if (nameorganization.getText().toString().trim().equals(null) || nameorganization.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), "BOS ARAMA DA YAPMAZSIN", Toast.LENGTH_LONG).show();
        } else {
//            setUpRecyclerViewSearch();
            System.out.println("denemeeeee");
        }
    }
}
