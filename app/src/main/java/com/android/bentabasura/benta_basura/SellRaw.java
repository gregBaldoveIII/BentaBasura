package com.android.bentabasura.benta_basura;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class SellRaw extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Intent profilePage, buyCrafted, buyRaw, sellCrafted, sellRaw,notificationsPage,homePage,cartPage,historyPage;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Menu navMenu;
    private Button uploadbtn,takePhotobtn;
    private ImageView imageView;
    private static final int Gallery_Intent = 100;
    Uri imageUri;
    EditText trashName,trashDesc,trashQty,trashPrice,trashCategory,sellerContact;
    Button Submittrash;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    String userid;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_raw);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        profilePage = new Intent(SellRaw.this, MyProfile.class);
        buyCrafted = new Intent(SellRaw.this, BuyCrafted.class);
        buyRaw = new Intent(SellRaw.this, BuyRaw.class);
        sellCrafted = new Intent(SellRaw.this, SellCrafted.class);
        sellRaw = new Intent(SellRaw.this, SellRaw.class);
        notificationsPage = new Intent(SellRaw.this, Notifications.class);
        homePage = new Intent(SellRaw.this,Home.class);
        cartPage = new Intent(SellRaw.this,Cart.class);
        historyPage = new Intent(SellRaw.this,History.class);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navMenu = navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(this);

        uploadbtn = (Button)findViewById(R.id.uploadbtn);
        uploadbtn.setOnClickListener(this);
        uploadbtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_btn));
        takePhotobtn = (Button) findViewById(R.id.takePhotobtn);
        takePhotobtn.setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.UploadImageView);

        checkFilePermissions();

        //------------------------------------------------------------
        trashName = (EditText) findViewById(R.id.trashName);
        trashDesc = (EditText) findViewById(R.id.trashDesc);
        trashPrice = (EditText) findViewById(R.id.trashPrice);
        trashQty = (EditText) findViewById(R.id.trashQty);
        trashCategory = (EditText) findViewById(R.id.trashCategory);
        sellerContact = (EditText) findViewById(R.id.sellerContact);

        Submittrash = (Button) findViewById(R.id.Submittrash);
        Submittrash.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.notifications:
                startActivity(notificationsPage);
                break;
            case R.id.cart:
                startActivity(cartPage);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_account:
                startActivity(profilePage);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.buy:
                if(navMenu.findItem(R.id.buy).getTitle().equals("Buy                                        +")) {
                    navMenu.findItem(R.id.buy_crafted).setVisible(true);
                    navMenu.findItem(R.id.buy_raw).setVisible(true);
                    navMenu.findItem(R.id.buy).setTitle("Buy                                        -");
                }
                else{
                    navMenu.findItem(R.id.buy_crafted).setVisible(false);
                    navMenu.findItem(R.id.buy_raw).setVisible(false);
                    navMenu.findItem(R.id.buy).setTitle("Buy                                        +");
                }
                break;
            case R.id.buy_crafted:
                startActivity(buyCrafted);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.buy_raw:
                startActivity(buyRaw);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.sell:
                if(navMenu.findItem(R.id.sell).getTitle().equals("Sell                                        +")) {
                    navMenu.findItem(R.id.sell_crafted).setVisible(true);
                    navMenu.findItem(R.id.sell_raw).setVisible(true);
                    navMenu.findItem(R.id.sell).setTitle("Sell                                        -");
                }
                else{
                    navMenu.findItem(R.id.sell_crafted).setVisible(false);
                    navMenu.findItem(R.id.sell_raw).setVisible(false);
                    navMenu.findItem(R.id.sell).setTitle("Sell                                        +");
                }
                break;
            case R.id.sell_crafted:
                startActivity(sellCrafted);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.sell_raw:
                startActivity(sellRaw);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.home:
                startActivity(homePage);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.history:
                startActivity(historyPage);
                drawer.closeDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uploadbtn:
                onGallery();
                break;
            case R.id.takePhotobtn:
                onCamera();
                break;
            case R.id.Submittrash:
                onUpload();
                break;
        }
    }
    protected void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode == Gallery_Intent)
            {
                imageUri = data.getData();
                //imageView.setImageResource(imageUri);
                InputStream inputStream;
                try
                {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(image);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
        /*super.onActivityResult(requestCode,resultCode,data);
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);*/

    }
    private void onCamera()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,0);
    }
    private void onGallery()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        Uri data = Uri.parse(pictureDirectoryPath);
        photoPickerIntent.setDataAndType(data, "image/*");
        startActivityForResult(photoPickerIntent, Gallery_Intent);

        /*Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Gallery_Intent);*/
    }
    public void checkFilePermissions()
    {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            int permissionCheck = SellRaw.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += SellRaw.this.checkSelfPermission("Manifest.permission.READ_WRITE_STORAGE");
            if(permissionCheck != 0)
            {
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE},permissionCheck);
            }
        }
        else
        {
            Log.d("SellCrafted","checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
    public void onUpload()
    {
        progressDialog.setMessage("Uploading your Trash...");
        progressDialog.show();

        user = firebaseAuth.getCurrentUser();
        userid = user.getUid();
        Trash newTrash = new Trash(trashName.getText().toString(),trashQty.getText().toString(),trashPrice.getText().toString(),trashDesc.getText().toString(),trashCategory.getText().toString(),sellerContact.getText().toString(),userid,"TBD");
        databaseReference.child("Trash").push().setValue(newTrash);

        StorageReference path = storageReference.child("Products").child("Trash").child(userid).child(trashName.getText().toString());
        path.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                showMessage("Product Uploaded Successfully");
                progressDialog.dismiss();
                startActivity(homePage);
            }
        });

    }

    public void showMessage(String message){
        Toast.makeText(getApplicationContext(), message , Toast.LENGTH_LONG).show();
    }
}