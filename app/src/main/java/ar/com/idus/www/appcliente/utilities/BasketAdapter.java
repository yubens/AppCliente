package ar.com.idus.www.appcliente.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

import ar.com.idus.www.appcliente.R;
import ar.com.idus.www.appcliente.models.BodyOrder;


public class BasketAdapter extends ArrayAdapter<BodyOrder> {
    private ArrayList<BodyOrder> orders;
    private Activity context;
    private LayoutInflater inflater;
    private TextView txtTotal;

    static class ViewHolder {
        TextView txtName;
        TextView txtQuantity;
        TextView txtSubtotal;
        TextView txtStringQuantity;
        TextView txtStringTotal;
        TextView txtPrice;
        ImageButton btnAdd;
        ImageButton btnMinus;

    }

    public BasketAdapter(@NonNull Context context, int resource, ArrayList<BodyOrder> orders, TextView txtTotal) {
        super(context, resource, orders);
        this.context = (Activity) context;
        this.orders = orders;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.txtTotal = txtTotal;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        final BasketAdapter.ViewHolder viewHolder;
        String total, quantity, price;

        if (convertView == null) {
            view = inflater.inflate(R.layout.basket_item, null);
            viewHolder = new BasketAdapter.ViewHolder();
            viewHolder.txtName =  view.findViewById(R.id.txtGridName);
            viewHolder.txtName.setTypeface(null, Typeface.BOLD);
            viewHolder.txtQuantity =  view.findViewById(R.id.txtGridQuantity);
            viewHolder.txtPrice =  view.findViewById(R.id.txtGridPrice);
            viewHolder.txtSubtotal =  view.findViewById(R.id.txtGridTotal);
            viewHolder.txtStringQuantity = view.findViewById(R.id.txtQuantityString);
            viewHolder.txtStringTotal = view.findViewById(R.id.txtTotalString);
            viewHolder.btnAdd = view.findViewById(R.id.btnAdd);
            viewHolder.btnMinus = view.findViewById(R.id.btnMinus);

            viewHolder.txtName.setTag(position);
            viewHolder.txtQuantity.setTag(position);
            viewHolder.txtPrice.setTag(position);
            viewHolder.txtSubtotal.setTag(position);
            viewHolder.btnAdd.setTag(position);
            viewHolder.btnMinus.setTag(position);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            ((ViewHolder)view.getTag()).txtName.setTag(position);
            ((ViewHolder)view.getTag()).txtQuantity.setTag(position);
            ((ViewHolder)view.getTag()).txtPrice.setTag(position);
            ((ViewHolder)view.getTag()).txtSubtotal.setTag(position);
            ((ViewHolder)view.getTag()).btnAdd.setTag(position);
            ((ViewHolder)view.getTag()).btnMinus.setTag(position);
        }

        final BodyOrder order = orders.get(position);
        final ViewHolder holder = (ViewHolder) view.getTag();

        holder.btnAdd.setId(position);
        holder.btnMinus.setId(position);
        holder.txtQuantity.setId(position);
        holder.txtSubtotal.setId(position);

        total = String.format("%.2f", order.getTotal());
        quantity = String.valueOf(order.getQuantity());
        price =  String.format("%.2f", order.getPrice());
        holder.txtName.setText(order.getName());
        holder.txtSubtotal.setText(total);
        holder.txtQuantity.setText(quantity);
        holder.txtPrice.setText(price);

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.btnAdd.getId();
                BodyOrder actualBody = orders.get(pos);
                String total, quantity;

                if (actualBody.getUpdatedStock() == 0) {
                    if(!context.isFinishing())
                        Toast.makeText(context, R.string.msgErrStock, Toast.LENGTH_LONG).show();

                    return;
                }

                actualBody.setQuantity(actualBody.getQuantity() + 1);
                actualBody.setUpdatedStock(actualBody.getUpdatedStock() - 1);
                actualBody.setTotal(actualBody.getTotal() + actualBody.getPrice());
                total = String.format("%.2f", actualBody.getTotal());
                quantity = String.valueOf(actualBody.getQuantity());
                holder.txtSubtotal.setText(total);
                holder.txtQuantity.setText(quantity);
                calculateTotal();
            }
        });

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int pos = holder.btnAdd.getId();
                BodyOrder actualBody = orders.get(pos);
                String total, quantity;

                if (actualBody.getQuantity() - 1 == 0) {
                    AlertDialog.Builder alertBuilder;
                    alertBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                    alertBuilder.setMessage(R.string.msgDeleteItem)
                            .setTitle(R.string.alertWarning)
                            .setCancelable(false)
                            .setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton(R.string.btnAccept, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    orders.remove(pos);
                                    calculateTotal();
                                    notifyDataSetChanged();
                                }
                            });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                    return;
                }

                actualBody.setQuantity(actualBody.getQuantity() - 1);
                actualBody.setUpdatedStock(actualBody.getUpdatedStock() + 1);
                actualBody.setTotal(actualBody.getTotal() - actualBody.getPrice());
                total = String.format("%.2f", actualBody.getTotal());
                quantity = String.valueOf(actualBody.getQuantity());
                holder.txtSubtotal.setText(total);
                holder.txtQuantity.setText(quantity);
                calculateTotal();
            }
        });

        return view;
    }

    private void calculateTotal() {
        float aux = 0.0f;
        String totalS;

        for (BodyOrder body: orders) {
            aux = aux + body.getTotal();
        }

        totalS = String.format("%.2f", aux);
        txtTotal.setText(totalS);
    }
}
