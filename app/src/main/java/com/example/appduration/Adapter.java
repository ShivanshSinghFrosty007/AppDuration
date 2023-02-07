package com.example.appduration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Adapter extends ArrayAdapter<Model> {

    public Adapter(Context context, ArrayList<Model> ArrayList) {
        super(context, 0, ArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Model stat = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_view, parent, false);
        }
        TextView name = convertView.findViewById(R.id.name);
        TextView time =  convertView.findViewById(R.id.time);
        ProgressBar bar = convertView.findViewById(R.id.progressBar);

        name.setText(stat.Name);
        time.setText(stat.Time);

        bar.setProgress((int) stat.pernetage);
        bar.setMax(100);

        return convertView;
    }
}
