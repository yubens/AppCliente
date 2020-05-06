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

import ar.com.idus.www.appcliente.R;
import ar.com.idus.www.appcliente.models.BodyOrder;
import ar.com.idus.www.appcliente.models.Distributor;


public class BasketAdapter extends ArrayAdapter<BodyOrder> {
    private ArrayList<BodyOrder> orders;
    private Context context;
    private LayoutInflater inflater;

    static class ViewHolder {
        TextView txtName;
        TextView txtQuantity;
        TextView txtPrice;
        TextView txtTotal;
    }

    public BasketAdapter(@NonNull Context context, int resource, ArrayList<BodyOrder> orders) {
        super(context, resource, orders);
        this.context = context;
        this.orders = orders;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final BasketAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.basket_item, null);
            viewHolder = new BasketAdapter.ViewHolder();
            viewHolder.txtName =  convertView.findViewById(R.id.txtGridName);
            viewHolder.txtQuantity =  convertView.findViewById(R.id.txtGridQuantity);
            viewHolder.txtPrice =  convertView.findViewById(R.id.txtGridPrice);
            viewHolder.txtTotal =  convertView.findViewById(R.id.txtGridTotal);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (BasketAdapter.ViewHolder) convertView.getTag();
        }

        BodyOrder order = orders.get(position);
        viewHolder.txtName.setText(order.getName());
        viewHolder.txtPrice.setText(order.getPrice());
        viewHolder.txtTotal.setText(order.getPrice());
        viewHolder.txtQuantity.setText(order.getQuantity());

        return convertView;
    }
}
