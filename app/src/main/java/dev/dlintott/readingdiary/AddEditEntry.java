package dev.dlintott.readingdiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEditEntry extends AppCompatActivity {
    dbAdapter db;
    boolean isEdit;
    int position;
    LinearLayout isbnWrapper;
    TextInputLayout isbnTextLayout;
    TextInputEditText title, date, time, pgFrom, pgTo, childComment, parentComment, isbn;
    ImageButton searchButton;
    ImageView bookImage;
    Calendar entryDateTime;
    String bookImageURL;
    RatingBar rating;


    ReadingEntry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_entry);
        db = new dbAdapter(this);
        isbn = findViewById(R.id.editText_ISBN);
        isbnWrapper = findViewById(R.id.isbnWrapper);
        isbnTextLayout = findViewById(R.id.isbnTextInputLayout);
        searchButton = findViewById(R.id.isbnSearchButton);
        bookImage = findViewById(R.id.bookImage);
        title = findViewById(R.id.editText_bookTitle);
        date = findViewById(R.id.editText_date);
        time = findViewById(R.id.editText_time);
        pgFrom = findViewById(R.id.editText_pagesFrom);
        pgTo = findViewById(R.id.editText_pagesTo);
        rating = findViewById(R.id.ratingBar);
        childComment = findViewById(R.id.editText_childComments);
        parentComment = findViewById(R.id.editText_parentComments);
        entryDateTime = Calendar.getInstance();

        isEdit = getIntent().getBooleanExtra("edit", false);

        if (isEdit) {
            setTitle("Edit entry");
            isbnWrapper.setVisibility(View.GONE);
            try {
                long _uid = getIntent().getLongExtra("entryId", -1);
                Log.d("AddEditEntry", "Attempting to retrieve record id " + _uid);
                position = getIntent().getIntExtra("position", -1);
                entry = db.getEntryById(_uid);

                title.setText(entry.getTitle());
                pgFrom.setText(String.valueOf(entry.getPageFrom()));
                pgTo.setText(String.valueOf(entry.getPageTo()));
                rating.setRating(entry.getRating());
                childComment.setText(entry.getChildComment());
                parentComment.setText(entry.getParentComment());
                entryDateTime.setTime(new Date(entry.getDate()));

                if (entry.getBookImage() != null) {
                    bookImageURL = entry.getBookImage();
                    showBookImage();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Failed to retrieve record", Toast.LENGTH_SHORT).show();
                Log.d("AddEditEntry", e.getMessage());
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        }

        setupDateField();
        setupTimeField();
        setupISBNSearchTextEdit();
        setupISBNSearchButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addeditmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (isEdit) {
                updateEntry();
            } else {
                addEntry();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    private void showBookImage() {
        Glide.with(AddEditEntry.this)
                .load(bookImageURL)
                .error(R.drawable.image_error)
                .into(bookImage);
        bookImage.setVisibility(View.VISIBLE);
    }

    private void setupISBNSearchTextEdit() {
        isbn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                if (length == 13 || length == 10) {
                    isbnTextLayout.setErrorEnabled(false);
                    searchButton.setEnabled(true);
                } else {

                    Drawable icon = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_error);
                    isbnTextLayout.setErrorEnabled(true);
                    isbnTextLayout.setError("ISBN must by 10 or 13 characters");
                    isbnTextLayout.setErrorIconDrawable(icon);
                    searchButton.setEnabled(false);
                }
            }
        });
    }

    private void setupISBNSearchButton() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(AddEditEntry.this);
                String baseURL = "https://www.googleapis.com/books/v1/volumes?q=isbn";
                String apiKey = "AIzaSyBPnVZoS9xlPC7f6ZC8iCnrA2E-XgFwoys";
                String url = String.format("%s:%s&key=%s", baseURL, isbn.getText(), apiKey);

                JsonObjectRequest request = new JsonObjectRequest(url, response -> {
                    JSONObject item;
                    String bookTitle;

                    try {
                        if (response.getInt("totalItems") > 0) {
                            item = (JSONObject) response.getJSONArray("items").get(0);
                            bookTitle = item.getJSONObject("volumeInfo").getString("title");
                            bookImageURL = item.getJSONObject("volumeInfo")
                                    .getJSONObject("imageLinks")
                                    .getString("thumbnail")
                                    .replace("http://", "https://");

                            title.setText(bookTitle);

                            showBookImage();
                        } else {
                            Toast.makeText(AddEditEntry.this, "No books found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                        error -> Toast.makeText(AddEditEntry.this,
                        "Failed to retrieve book information\n\n" + error.getMessage(),
                        Toast.LENGTH_LONG).show());

                // Add the request to the RequestQueue.
                queue.add(request);
            }
        });
    }

    private void setupDateField() {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance();
        date.setText(dateFormat.format(entryDateTime.getTime()));

        date.setOnClickListener(view -> {
            DatePickerDialog dpd = new DatePickerDialog(
                    AddEditEntry.this,
                    (datePicker, year, month, day) -> {
                        entryDateTime.set(year, month, day);
                        date.setText(dateFormat.format(entryDateTime.getTime()));
                    },
                    entryDateTime.get(Calendar.YEAR),
                    entryDateTime.get(Calendar.MONTH),
                    entryDateTime.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show();
        });
    }

    private void setupTimeField() {
        DateFormat timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
        time.setText(timeFormat.format(entryDateTime.getTime()));
        time.setOnClickListener(view -> {
            TimePickerDialog tpd = new TimePickerDialog(
                    AddEditEntry.this,
                    (timePicker, hour, minutes) -> {
                        entryDateTime.set(Calendar.HOUR_OF_DAY, hour);
                        entryDateTime.set(Calendar.MINUTE, minutes);
                        time.setText(timeFormat.format(entryDateTime.getTime()));
                    },
                    entryDateTime.get(Calendar.HOUR_OF_DAY),
                    entryDateTime.get(Calendar.MINUTE),
                    true
            );
            tpd.show();
        });
    }

    private ReadingEntry getReadingEntry() {
        return new ReadingEntry(
                title.getText().toString(),
                entryDateTime.getTimeInMillis(),
                Integer.parseInt(pgFrom.getText().toString()),
                Integer.parseInt(pgTo.getText().toString()),
                rating.getRating(),
                childComment.getText().toString(),
                parentComment.getText().toString(),
                bookImageURL);
    }

    private void updateEntry() {
        entry.setTitle(title.getText().toString());
        entry.setDate(entryDateTime.getTimeInMillis());
        entry.setPageFrom(Integer.parseInt(pgFrom.getText().toString()));
        entry.setPageTo(Integer.parseInt(pgTo.getText().toString()));
        entry.setRating(rating.getRating());
        entry.setChildComment(childComment.getText().toString());
        entry.setParentComment(parentComment.getText().toString());
        entry.setBookImage(bookImageURL);


        if (db.updateEntry(entry) != 1) {
            Toast.makeText(this, "Update entry unsuccessful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Update entry successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("entry", entry);
            intent.putExtra("position", position);
            setResult(3, intent);
            finish();
        }
    }

    private void addEntry() {
        entry = getReadingEntry();
        long id = db.insertData(entry);
        if (id <= 0) {
            Toast.makeText(this, "Add entry unsuccessful", Toast.LENGTH_SHORT).show();
        } else {
            entry.setId(id);
            Toast.makeText(this, "Add entry successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("entry", entry);
            setResult(2, intent);
            finish();
        }
    }
}