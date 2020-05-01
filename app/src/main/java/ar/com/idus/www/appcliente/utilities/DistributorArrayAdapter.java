package ar.com.idus.www.appcliente.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ar.com.idus.www.appcliente.R;
import ar.com.idus.www.appcliente.models.Distributor;

public class DistributorArrayAdapter extends ArrayAdapter<Distributor> {
    private ArrayList<Distributor> distributors;
    private Context context;
    private  LayoutInflater inflater;

    public DistributorArrayAdapter(@NonNull Context context, int resource, ArrayList<Distributor> distributors) {
        super(context, resource, distributors);
        this.context = context;
        this.distributors = distributors;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.dist_item_list, null);
            viewHolder = new ViewHolder();
            viewHolder.txtName =  convertView.findViewById(R.id.txtDistName);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Distributor distributor =distributors.get(position);
        viewHolder.txtName.setText(distributor.getName());

        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
    }
}
