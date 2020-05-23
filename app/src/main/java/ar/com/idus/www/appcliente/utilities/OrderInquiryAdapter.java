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
import ar.com.idus.www.appcliente.models.OrderState;

public class OrderInquiryAdapter extends ArrayAdapter<OrderState> {
    private ArrayList<OrderState> orders;
    private Context context;
    private LayoutInflater inflater;

    static class ViewHolder {
        TextView txtVoucher;
        TextView txtDateState;
        TextView txtTotal;

    }

    public OrderInquiryAdapter(@NonNull Context context, int resource, ArrayList<OrderState> orders) {
        super(context, resource, orders);
        this.context = context;
        this.orders = orders;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final OrderInquiryAdapter.ViewHolder viewHolder;
        String total, voucher, dateState;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.order_inquiry_item, null);
            viewHolder = new OrderInquiryAdapter.ViewHolder();
            viewHolder.txtVoucher =  convertView.findViewById(R.id.txtGridVoucher);
            viewHolder.txtVoucher.setTypeface(null, Typeface.BOLD);
            viewHolder.txtDateState =  convertView.findViewById(R.id.txtGridDateState);
            viewHolder.txtTotal =  convertView.findViewById(R.id.txtGridOrderTotal);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (OrderInquiryAdapter.ViewHolder) convertView.getTag();
        }

        OrderState order = orders.get(position);
        float f;

        if (order.getTot_order() != null)
            f = Float.parseFloat(order.getTot_order());
        else
            f = 0f;

        total = "Total $ " + String.format("%.2f", f);
        voucher = "Comprobante: \n" + order.getId_order();
        dateState = "Fecha: " + order.getDate_order() + "    Estado: " + order.getState();


        viewHolder.txtVoucher.setText(voucher);
        viewHolder.txtDateState.setText(dateState);
        viewHolder.txtTotal.setText(total);

        return convertView;
    }
}
