package ar.com.idus.www.appcliente.utilities;

import android.content.Context;
import android.graphics.Typeface;
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


public class BasketAdapter extends ArrayAdapter<BodyOrder> {
    private ArrayList<BodyOrder> orders;
    private Context context;
    private LayoutInflater inflater;

    static class ViewHolder {
        TextView txtName;
        TextView txtQuantity;
        TextView txtIdProd;
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
        String total, quantity, price, idProd;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.basket_item, null);
            viewHolder = new BasketAdapter.ViewHolder();
            viewHolder.txtName =  convertView.findViewById(R.id.txtGridName);
            viewHolder.txtName.setTypeface(null, Typeface.BOLD);
            viewHolder.txtQuantity =  convertView.findViewById(R.id.txtGridQuantity);
            viewHolder.txtIdProd =  convertView.findViewById(R.id.txtGridIdProd);
            viewHolder.txtTotal =  convertView.findViewById(R.id.txtGridTotal);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (BasketAdapter.ViewHolder) convertView.getTag();
        }

        BodyOrder order = orders.get(position);

        total = "Subtotal: " + String.format("%.2f", order.getTotal());
        idProd = "Id Producto: " + order.getIdProduct();
        quantity = "Cantidad: " + order.getQuantity() ;
        price =  String.format("%.2f", order.getPrice());

        viewHolder.txtName.setText(order.getName());
        viewHolder.txtIdProd.setText(idProd);
        viewHolder.txtTotal.setText(total);
        viewHolder.txtQuantity.setText(quantity + " x " + price);

        return convertView;
    }
}
