package medicine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmate.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import models.MedicineClass;

public class SearchMedicine extends AppCompatActivity {

    public static EditText resultsearcheview;
    public static EditText barcode;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    ImageButton scantosearch;
    EditText name;
    Button searchButton, request;
    RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private MedicineAdapter adapter;
    private CollectionReference medicineReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_medicine);
        barcode = findViewById(R.id.editTextTextPersonName14);
        name = findViewById(R.id.editTextTextPersonName13);
        searchButton = findViewById(R.id.button4);
        medicineReference = db.collection("medicine");
        recyclerView = findViewById(R.id.medicine_recycler_view);
        scantosearch = findViewById(R.id.imageButtonsearch);
        request = findViewById(R.id.request);
        request.setVisibility(View.INVISIBLE);


        scantosearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BarcodeScanner.class));
            }
        });


        barcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

                Query query = medicineReference
                        .whereEqualTo("barcodeNumber", barcode.getText().toString().trim()).orderBy("barcodeNumber");

                FirestoreRecyclerOptions<MedicineClass> options1 = new FirestoreRecyclerOptions.Builder<MedicineClass>()
                        .setQuery(query, MedicineClass.class)
                        .build();
                adapter.updateOptions(options1);
            }
        });
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {

        Query denemQuery = medicineReference.orderBy("barcodeNumber");

        Query query = medicineReference.whereEqualTo("barcodeNumber", barcode.getText().toString().trim()).orderBy("barcodeNumber");
        if (barcode.getText().toString().trim().equals(null) || barcode.getText().toString().trim().equals("")) {

            FirestoreRecyclerOptions<MedicineClass> options1 = new FirestoreRecyclerOptions.Builder<MedicineClass>()
                    .setQuery(denemQuery, MedicineClass.class)
                    .build();
            adapter = new MedicineAdapter(options1);
            adapter.setOnItemClickListener(new MedicineAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    MedicineClass medicineClass = documentSnapshot.toObject(MedicineClass.class);
                    String id = documentSnapshot.getId();
                    Intent intent = new Intent(SearchMedicine.this, ReceiveMedicine.class);
                    firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String recUserID = firebaseUser.getUid();
                    intent.putExtra("nameOfMedicine", medicineClass.getNameOfMedicine());
                    intent.putExtra("barcodeNumber", medicineClass.getBarcodeNumber());
//                    intent.putExtra("quantity", medicineClass.getQuantity().toString());
                    intent.putExtra("expirationdate", medicineClass.getExpirationdate());
                    intent.putExtra("userID", recUserID);
                    startActivity(intent);


                }
            });
        } else {

            FirestoreRecyclerOptions<MedicineClass> options2 = new FirestoreRecyclerOptions.Builder<MedicineClass>()
                    .setQuery(query, MedicineClass.class)
                    .build();
            adapter = new MedicineAdapter(options2);
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setUpRecyclerViewSearch() {

        Query query = medicineReference.whereEqualTo("barcodeNumber", "08699565523578").orderBy("barcodeNumber");
        System.out.println(barcode.getText().toString().trim());
        FirestoreRecyclerOptions<MedicineClass> options2 = new FirestoreRecyclerOptions.Builder<MedicineClass>()
                .setQuery(query, MedicineClass.class)
                .build();
        adapter = new MedicineAdapter(options2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        request.setVisibility(View.VISIBLE);

        Toast.makeText(getApplicationContext(), "There are no such a medicine", Toast.LENGTH_LONG).show();
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


    public void searchMedicineClick(View view) {

        if (barcode.getText().toString().trim().equals(null) || barcode.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter a valid Barcode Number", Toast.LENGTH_LONG).show();
        } else {
            setUpRecyclerViewSearch();
        }
    }


    public void goRequest(View view) {
        Intent intent = new Intent(SearchMedicine.this, RequestMedicine.class);
        startActivity(intent);
    }
}