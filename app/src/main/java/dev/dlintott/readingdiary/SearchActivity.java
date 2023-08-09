package dev.dlintott.readingdiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    dbAdapter db;
    List<ReadingEntry> results;
    RecyclerView resultsList;
    EntryListAdapter resultsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        db = new dbAdapter(this);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            results = db.getEntries(query);
        }
        resultsListAdapter = new EntryListAdapter(this, results);
        resultsList = findViewById(R.id.resultsList);
        resultsList.setHasFixedSize(true);
        resultsList.setLayoutManager(new LinearLayoutManager(this));
        resultsList.setAdapter(resultsListAdapter);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}