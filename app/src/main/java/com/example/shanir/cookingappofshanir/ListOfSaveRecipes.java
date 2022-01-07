package com.example.shanir.cookingappofshanir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.shanir.cookingappofshanir.Admin.General;
import com.example.shanir.cookingappofshanir.classs.Adapter;
import com.example.shanir.cookingappofshanir.classs.UserItems;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListOfSaveRecipes extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    ListView listView;
    Adapter adapter;
    String name="";
    ArrayList<String> items;
    FirebaseAuth firebaseAuth;
     DatabaseReference postRef;
    private String tableSave;
    private UserItems item;
    Button btsave;
    String lastselected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_save_recipes);
        btsave=(Button)findViewById(R.id.btsavelistsave) ;
        listView = (ListView) findViewById(R.id.lvsaverecipes);
        btsave.setOnClickListener(this);;
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null)
        {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        setRefToTables();
        retrieveData();
    }

    // Get Item list from database
    public void retrieveData()
    {
        postRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    item = postSnapshot.getValue(UserItems.class);
                    item.setUid(firebaseAuth.getCurrentUser().getUid());
                    item.setKey(postSnapshot.getKey());
                }
                if(item==null)
                {
                    item = new UserItems();
                    item.setUid(firebaseAuth.getCurrentUser().getUid());
                    item.setKey(dataSnapshot.getKey());
                }

                Intent intent=getIntent();
                if (intent.getExtras() != null)
                {
                    name = intent.getExtras().getString("nameofrecipedetails");
                    setAdapter(name);
                }
                else {
                    setAdapterItem(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setAdapterItem(UserItems list) {
        items = list.getItems();
        adapter = new Adapter(this, 0, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }


    // Adapter to item list
    public void setAdapter(String name1) {

        if(!item.getItems().contains(name1))
            item.add(name1);
        adapter = new Adapter(this, 0, item.getItems());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public void updateItemsList() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(tableSave+"/"+item.getKey());
        database.setValue(item);
    }
    private void setRefToTables()
    {
        String uid=firebaseAuth.getCurrentUser().getUid();
        tableSave  = General.SAVE_RECIPES+"/"+uid;
        postRef= FirebaseDatabase.getInstance().getReference(tableSave);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;

            case R.id.mnItemConsumers:
                Intent intent2 = new Intent(getBaseContext(), Consumers.class);
                startActivity(intent2);
                break;
            case R.id.mnItemListOfRecipes:
                Intent intent3=new Intent(getBaseContext(),ListOfRecipe.class);
                startActivity(intent3);
                break;

            case R.id.mnItemProfile:
                Intent intent6=new Intent(getBaseContext(),Profile.class);
                startActivity(intent6);
                break;
            case R.id.mnItemListofsaverecipes:
                Toast.makeText(getApplicationContext(),"אתה נמצא במסך זה" ,Toast.LENGTH_SHORT).show();
                break;





        }

        return true;
    }

    @Override
    public void onClick(View v) {
        if (v==btsave)
        {
            updateItemsList();
            // item.getItems().remove(name);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i=new Intent(getApplicationContext(), DetailsOnRecipe.class);
        lastselected=(String) adapter.getItem(position);
        i.putExtra("detailslistofsaverecipe",lastselected);
        i.putExtra("numlistofsverecipe",0);
        startActivity(i);
    }
}
