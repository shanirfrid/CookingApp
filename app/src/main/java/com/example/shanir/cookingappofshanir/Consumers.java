package com.example.shanir.cookingappofshanir;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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

public class Consumers extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    EditText etadd;
    ImageView imageViewadd;
    ListView listView;
    TextView tv;
    Adapter adapter;
    Button btsave,btrecipelist;
    ArrayList<String> items;
    Context context = this;
    FirebaseAuth firebaseAuth;
    private DatabaseReference postRef;
    private  String tableId;
    private UserItems item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumers);
        etadd = (EditText) findViewById(R.id.etwriteconsumers);
        btsave=(Button)findViewById(R.id.btsaveconsumers);
        btrecipelist=(Button)findViewById(R.id.btmovetorecipelist) ;
        imageViewadd = (ImageView) findViewById(R.id.ivconsumersadd);
        imageViewadd.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.listviewconsumers);
        tv=(TextView)findViewById(R.id.tvheadconsumers) ;
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null)
        {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        btsave.setOnClickListener(this);
        btrecipelist.setOnClickListener(this);
        setRefToTables();
        retrieveData();

    }

    // Get Item list from database
    public void retrieveData()
    {
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
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
                setAdapter(item);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    // Adapter to item list
    private void setAdapter(UserItems list) {
        items = list.getItems();
        adapter = new Adapter(this, 0, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("מחיקת מרכיב                        ");
        builder.setMessage("האם אתה מאשר את מחיקת הפריט?" + "\n" + "בחר פה את תשובתך");
        builder.setCancelable(true);
        builder.setNegativeButton("לא מסכים", new DialogListener(position));
        builder.setPositiveButton("מסכים", new DialogListener(position));
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public class DialogListener implements DialogInterface.OnClickListener
    {
        private int position;
        public DialogListener(int position) {
            this.position=position;
        }

        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            if (which == AlertDialog.BUTTON_POSITIVE)
            {
                items.remove(position);
                updateItemsList();
                adapter.notifyDataSetChanged();
                Toast.makeText(context, "הרכיב נמחק",
                        Toast.LENGTH_SHORT).show();
            }
            else if (which == AlertDialog.BUTTON_NEGATIVE)
                Toast.makeText(context, "לא הסכמת למחוק",
                        Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onClick(View v) {
        if (v==imageViewadd)
        {
            String st=etadd.getText().toString().toLowerCase();
            if(st.trim().isEmpty())
            {
                etadd.setError("צריך להכניס מצרך");
                return;
            }

            adapter.add(st);
            adapter.notifyDataSetChanged();
            etadd.setText("");
        }
        else if (v==btsave)
        {
            updateItemsList();
            Toast.makeText(getApplicationContext(),"המצרכים נשמרו" ,Toast.LENGTH_SHORT).show();

        }
        else if (v==btrecipelist)
        {
            Intent intent = new Intent(this, ListOfRecipe.class);
            startActivity(intent);
        }



    }

    private void updateItemsList() {
        item.add(items);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(tableId+"/"+item.getKey());
        database.setValue(item);

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

            case R.id.mnItemListOfRecipes:
                Intent intent2 = new Intent(getBaseContext(), ListOfRecipe.class);
                startActivity(intent2);
                break;

            case R.id.mnItemListofsaverecipes:
                Intent intent6=new Intent(getBaseContext(),ListOfSaveRecipes.class);
                startActivity(intent6);
                break;
            case R.id.mnItemProfile:
                Intent intent7=new Intent(getBaseContext(),Profile.class);
                startActivity(intent7);
                break;

            case R.id.mnItemConsumers:
                Toast.makeText(getApplicationContext(),"אתה נמצא במסך זה" ,Toast.LENGTH_SHORT).show();
                break;



        }

        return true;
    }


    /*
    postRef - Items list
    postRefR - recipes list
     */
    private void setRefToTables()
    {
        String uid=firebaseAuth.getCurrentUser().getUid();
        tableId  = General.RECIPE_ITEM_NAME+"/"+uid;
        postRef= FirebaseDatabase.getInstance().getReference(tableId);

    }

}
