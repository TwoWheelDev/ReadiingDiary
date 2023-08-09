package dev.dlintott.readingdiary;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    dbAdapter db;
    RecyclerView entriesList;
    EntryListAdapter entryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new dbAdapter(this);

        FloatingActionButton addEntry = findViewById(R.id.addNewEntry);
        addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddEditEntry.class);
                startActivityForResult(intent, 0);
            }
        });

        entriesList = findViewById(R.id.entriesList);
        List<ReadingEntry> entries = db.getEntries();
        entryListAdapter = new EntryListAdapter(this, entries);
        entriesList.setHasFixedSize(true);
        entriesList.setLayoutManager(new LinearLayoutManager(this));
        entriesList.setAdapter(entryListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ReadingEntry result = (ReadingEntry) data.getSerializableExtra("entry");
        int position = data.getIntExtra("position", 0);
        switch (resultCode) {
            case 2: // New Entry
                entryListAdapter.addEntry(position, result);
                entryListAdapter.notifyItemInserted(position);
                break;
            case 3: //Updated Entry
                entryListAdapter.notifyItemChanged(position, result);
                break;
            case 4: // Deleted Entry
                entryListAdapter.deleteEntry(position);
                entryListAdapter.notifyItemRemoved(position);
                break;
        }
        entriesList.scrollToPosition(0);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.searchmenu, menu);

        // Setup the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }
}