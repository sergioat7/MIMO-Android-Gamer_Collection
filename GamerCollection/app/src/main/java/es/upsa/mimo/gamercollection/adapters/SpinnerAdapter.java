package es.upsa.mimo.gamercollection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import es.upsa.mimo.gamercollection.R;

public class SpinnerAdapter extends ArrayAdapter {

    //region Private properties
    private final Context context;
    private final List<String> values;
    private final boolean firstOptionEnabled;
    //endregion

    //region Lifecycle methods
    public SpinnerAdapter(Context context, List<String> values, boolean firstOptionEnabled) {
        super(context, 0, values);
        this.context = context;
        this.values = values;
        this.firstOptionEnabled = firstOptionEnabled;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.item_spinner, parent, false);

        String currentValue = values.get(position);

        TextView tvValue = listItem.findViewById(R.id.text_view_value);
        tvValue.setText(currentValue);

        int colorId = position == 0 && !firstOptionEnabled ? R.color.textPrimaryLight : R.color.textPrimary;
        tvValue.setTextColor(ContextCompat.getColor(context, colorId));

        return listItem;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0 || firstOptionEnabled;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {

        TextView tvValue = (TextView) super.getDropDownView(position, convertView, parent);
        int colorId = position == 0 && !firstOptionEnabled ? R.color.textPrimaryLight : R.color.textPrimary;
        tvValue.setTextColor(ContextCompat.getColor(context, colorId));
        return tvValue;
    }
    //endregion
}
