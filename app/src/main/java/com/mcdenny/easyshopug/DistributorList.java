package com.mcdenny.easyshopug;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mcdenny.easyshopug.Model.Distributor;
import com.mcdenny.easyshopug.ViewHolder.DistributorViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import mehdi.sakout.fancybuttons.FancyButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DistributorList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference distributorList;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Distributor, DistributorViewHolder> adapter;

    //for the add distributor alert dialog
    MaterialEditText addDistName, addDistPhone, addDistEmail, addDistAddress;
    FancyButton selectDistImage, uploadDistImage;
    Distributor newDistributor;
    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/MontserratRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_distributor_list);
        setTitle("Distributor List");

        //init firebase
        database = FirebaseDatabase.getInstance();
        distributorList = database.getReference("Distributor");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FloatingActionButton fab = findViewById(R.id.fab_distributor);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDistributorDialog();
            }
        });

        //inflate the layout
        recyclerView = findViewById(R.id.recycler_distributor);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadDistributorList();
    }

    //adding a new distributor in an alertdialog
    private void showAddDistributorDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(DistributorList.this);
        alertDialog.setTitle("Add new distributor");
        alertDialog.setMessage("Please fill in the information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_distributor_layout, null);

        addDistName = add_menu_layout.findViewById(R.id.add_distributor_name);
        addDistPhone = add_menu_layout.findViewById(R.id.add_distributor_phone);
        addDistEmail = add_menu_layout.findViewById(R.id.add_distributor_email);
        addDistAddress = add_menu_layout.findViewById(R.id.add_distributor_address);
        selectDistImage = add_menu_layout.findViewById(R.id.select_distributor_image);
        uploadDistImage = add_menu_layout.findViewById(R.id.upload_distributor_image);

        //adding onclick event on the buttons
        selectDistImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();//let the user choose the image and save its uri
            }
        });

        uploadDistImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
                validateName();//not to accept empty category name
            }
        });

        alertDialog.setView(add_menu_layout);
        //alertDialog.setIcon(R.drawable.ic_cart_plus);

        //setting the buttons
        alertDialog.setPositiveButton("FINISH", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //Creating a new category
                if (newDistributor != null) {
                    distributorList.push().setValue(newDistributor);
                }
            }
        });

        alertDialog.show();
    }//end of alert dialog

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading....");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Image uploaded succesfully",
                                    Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set a value for a new distributor if image uploaded, and then get the download link
                                    //newDistributor = new Distributor(addDistName.getText().toString(), uri.toString());
                                    newDistributor = new Distributor(
                                            addDistName.getText().toString(),
                                            addDistPhone.getText().toString(),
                                            addDistEmail.getText().toString(),
                                            addDistAddress.getText().toString(),
                                            uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(DistributorList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded" + progress + "%");

                        }
                    });
        }
    }

    private void validateName() {
        if (addDistName.getText().toString().isEmpty()) {
            addDistName.setError("You must fill in this field!");
            addDistName.requestFocus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            saveUri = data.getData();
            selectDistImage.setText("Image selected!");
        }
    }

    private void loadDistributorList() {
        adapter = new FirebaseRecyclerAdapter<Distributor, DistributorViewHolder>(
                Distributor.class,
                R.layout.distributor_list_layout,
                DistributorViewHolder.class,
                distributorList
        ) {
            @Override
            protected void populateViewHolder(DistributorViewHolder viewHolder, Distributor model, int position) {
                viewHolder.distName.setText(model.getName());
                viewHolder.distAddress.setText(model.getAddress());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.distImage);

            }
        };
        adapter.notifyDataSetChanged();//Refresh data if it has been changed
        //Setting the adapter
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Connect with me on facebook via: www.facebook.com/denislucaz");
            startActivity(Intent.createChooser(shareIntent, "Send Invite Via"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
