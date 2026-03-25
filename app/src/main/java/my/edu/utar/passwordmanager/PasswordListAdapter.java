package my.edu.utar.passwordmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.List;

public class PasswordListAdapter extends BaseAdapter {

    public interface OnDeleteClickListener {
        void onDeleteClick(PasswordEntry entry);
    }

    public interface OnRowClickListener {
        void onRowClick(PasswordEntry entry);
    }

    private final Context context;
    private final List<PasswordEntry> items;
    private final OnDeleteClickListener onDeleteClickListener;
    private final OnRowClickListener onRowClickListener;
    private boolean showPasswords = false;

    public PasswordListAdapter(Context context, List<PasswordEntry> items,
                               OnDeleteClickListener onDeleteClickListener,
                               OnRowClickListener onRowClickListener) {
        this.context = context;
        this.items = items;
        this.onDeleteClickListener = onDeleteClickListener;
        this.onRowClickListener = onRowClickListener;
    }

    public void setShowPasswords(boolean showPasswords) {
        this.showPasswords = showPasswords;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() { return items.size(); }

    @Override
    public Object getItem(int position) { return items.get(position); }

    @Override
    public long getItemId(int position) { return items.get(position).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.row_password, parent, false);
        }
        TextView txtSite = row.findViewById(R.id.txtSite);
        TextView txtUsername = row.findViewById(R.id.txtUsername);
        TextView txtPassword = row.findViewById(R.id.txtPassword);
        ImageButton btnDelete = row.findViewById(R.id.btnDelete);

        PasswordEntry entry = items.get(position);
        txtSite.setText(entry.getSite());
        txtUsername.setText(entry.getUsername());
        if (showPasswords) {
            txtPassword.setText(entry.getPassword());
        } else {
            txtPassword.setText("••••••••");
        }

        View rowContent = row.findViewById(R.id.rowContent);
        rowContent.setOnClickListener(v -> {
            if (onRowClickListener != null) {
                onRowClickListener.onRowClick(entry);
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(entry);
            }
        });

        return row;
    }
}
