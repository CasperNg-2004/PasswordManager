package my.edu.utar.passwordmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PasswordDbHelper dbHelper;
    private ListView listView;
    private PasswordListAdapter adapter;
    private ImageButton btnAdd;
    private ImageButton btnSort;
    private SearchView searchView;
    private boolean sortAsc = true;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);
        setTitle("My Passwords");

        dbHelper = new PasswordDbHelper(this);
        listView = findViewById(R.id.listPasswords);
        btnAdd = findViewById(R.id.btnAdd);
        btnSort = findViewById(R.id.btnSort);
        searchView = findViewById(R.id.searchView);

        btnAdd.setOnClickListener(v -> openEdit(null));
        btnSort.setOnClickListener(v -> {
            sortAsc = !sortAsc;
            loadData();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query == null ? "" : query;
                loadData();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText == null ? "" : newText;
                loadData();
                return true;
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (adapter != null) {
                PasswordEntry entry = (PasswordEntry) adapter.getItem(position);
                openView(entry);
            }
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            PasswordEntry entry = (PasswordEntry) adapter.getItem(position);
            confirmDelete(entry);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<PasswordEntry> entries = dbHelper.getEntries(currentQuery, sortAsc);
        adapter = new PasswordListAdapter(this, entries, this::confirmDelete, this::openView);
        listView.setAdapter(adapter);
    }

    private void openView(PasswordEntry entry) {
        if (entry == null) return;
        Intent intent = new Intent(MainActivity.this, ViewPasswordActivity.class);
        intent.putExtra("id", entry.getId());
        intent.putExtra("site", entry.getSite());
        intent.putExtra("username", entry.getUsername());
        intent.putExtra("password", entry.getPassword());
        intent.putExtra("extraPin", entry.getExtraPin());
        intent.putExtra("challengeQuestion", entry.getChallengeQuestion());
        intent.putExtra("challengeAnswer", entry.getChallengeAnswer());
        startActivity(intent);
    }

    private void openEdit(PasswordEntry entry) {
        Intent intent = new Intent(MainActivity.this, EditPasswordActivity.class);
        if (entry != null) {
            intent.putExtra("id", entry.getId());
            intent.putExtra("site", entry.getSite());
            intent.putExtra("username", entry.getUsername());
            intent.putExtra("password", entry.getPassword());
            intent.putExtra("extraPin", entry.getExtraPin());
            intent.putExtra("challengeQuestion", entry.getChallengeQuestion());
            intent.putExtra("challengeAnswer", entry.getChallengeAnswer());
        }
        startActivity(intent);
    }

    private void confirmDelete(PasswordEntry entry) {
        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this password?")
                .setPositiveButton("OK", (dialog, which) -> showSecondConfirm(entry))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showSecondConfirm(PasswordEntry entry) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm delete")
                .setMessage("This cannot be undone. Delete \"" + entry.getSite() + "\" anyway?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteEntry(entry.getId());
                    Toast.makeText(MainActivity.this, "Deleted.", Toast.LENGTH_SHORT).show();
                    loadData();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}