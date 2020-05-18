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
import java.util.List;
import es.upsa.mimo.gamercollection.R;

public class SpinnerAdapter extends ArrayAdapter {

    private Context mContext;
    private List<String> values;

    public SpinnerAdapter(Context context, List<String> list) {
        super(context, 0 , list);
        mContext = context;
        values = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.spinner_item,parent,false);

        String currentValue = values.get(position);

        TextView tvValue = listItem.findViewById(R.id.text_view_value);
        tvValue.setText(currentValue);

        int colorId = position == 0 ? R.color.color2Light : R.color.color2;
        tvValue.setTextColor(ContextCompat.getColor(mContext, colorId));

        return listItem;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        TextView tvValue = (TextView) super.getDropDownView(position, convertView, parent);
        int colorId = position == 0 ? R.color.color2Light : R.color.color2;
        tvValue.setTextColor(ContextCompat.getColor(mContext, colorId));
        return tvValue;
    }
}
