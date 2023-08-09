package dev.dlintott.readingdiary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewEntry extends AppCompatActivity {
    int position;
    TextView title, pages, childComment, parentComment, dateTime;
    ImageView bookImage;
    RatingBar rating;
    ReadingEntry entry;
    dbAdapter db;
    DateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.ENGLISH);

        long _uid = getIntent().getLongExtra("entryId", -1);
        position = getIntent().getIntExtra("position", -1);

        title = findViewById(R.id.viewBookTitle);
        pages = findViewById(R.id.viewPagesRead);
        childComment = findViewById(R.id.viewChildComments);
        parentComment = findViewById(R.id.viewParentComments);
        dateTime = findViewById(R.id.viewDateTime);
        bookImage = findViewById(R.id.viewBookImage);
        rating = findViewById(R.id.viewRating);

        db = new dbAdapter(this);
        try {
            entry = db.getEntryById(_uid);
            title.setText(entry.getTitle());
            pages.setText(String.format(Locale.ENGLISH, "Read from %d to %d", entry.getPageFrom(), entry.getPageTo()));
            rating.setRating(entry.getRating());
            childComment.setText(entry.getChildComment());
            parentComment.setText(entry.getParentComment());
            dateTime.setText(sdf.format(entry.getDate()));

            if (entry.getBookImage() != null) {
                Glide.with(this)
                        .load(entry.getBookImage())
                        .error(R.drawable.image_error)
                        .into(bookImage);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to retrieve record", Toast.LENGTH_SHORT).show();
            Log.d("AddEditEntry", e.getMessage());
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        intent.putExtra("position", position);

        switch (item.getItemId()) {
            case (R.id.action_delete):
                handleDelete(intent);
                return true;
            case (R.id.action_edit):
                handleEdit(intent);
                return true;
            case (R.id.action_share):
                handleShare(intent);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 3) { //Updated Entry
            setResult(resultCode, data);
            finish();
        }
    }

    private void handleDelete(Intent intent) {
        try {
            if (db.deleteEntryById(entry.getId()) == 1) {
                setResult(4, intent);
                finish();
            } else {
                Toast.makeText(this, "Unable to delete record", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error deleting record", Toast.LENGTH_SHORT).show();
            Log.d("ViewEntry", e.getMessage());
        }
    }

    private void handleEdit(Intent intent) {
        intent.setClass(this, AddEditEntry.class);
        intent.putExtra("entryId", entry.getId());
        intent.putExtra("edit", true);
        startActivityForResult(intent, 0);
    }

    private void handleShare(Intent intent) {
        String[] emails = {"readingdiary@theschool.test"};
        Uri sendTo = Uri.parse("mailto:");

        String dateTime = sdf.format(entry.getDate());
        String subject = String.format("Reading Diary Entry", dateTime);
        String body = String.format(Locale.ENGLISH,
                "Date: %s\n" +
                "Book Title: %s\n" +
                "From page: %d\n" +
                "To page: %d\n" +
                "Rating: %f\n" +
                "Child's Comment: %s\n" +
                "Parent's Comment: %s",
                dateTime, entry.getTitle(), entry.getPageFrom(), entry.getPageTo(),
                entry.getRating(), entry.getChildComment(), entry.getParentComment());

        intent.setAction(Intent.ACTION_SENDTO);
        intent.setData(sendTo);
        intent.putExtra(Intent.EXTRA_EMAIL, emails);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}