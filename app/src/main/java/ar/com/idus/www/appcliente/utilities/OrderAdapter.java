package ar.com.idus.www.appcliente.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import ar.com.idus.www.appcliente.R;
import ar.com.idus.www.appcliente.models.BodyOrder;
import ar.com.idus.www.appcliente.models.Product;

public class OrderAdapter extends ArrayAdapter<Product> {
    private Activity context;
    private ArrayList<Product> productList;
    private LayoutInflater inflater;
    private Product product;
    ArrayList<BodyOrder> listOrder;
    BodyOrder body;

    static class ViewHolder {
        TextView txtItemName;
        TextView txtItemStock;
        TextView txtItemMultiple;
        TextView txtItemPrice;
        TextView txtItemTotal;
        TextView txtItemQuantity;
        TextView txtQuantityString;
        TextView txtTotalString;
        ImageView imgItem;
        ImageButton btnAddItem;
    }


    public OrderAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Product> productList, ArrayList<BodyOrder> listOrder) {
        super(context, resource, productList);
        this.context = (Activity) context;
        this.productList = productList;
        this.listOrder = listOrder;

//        this.inflater = (LayoutInflater) context.getin;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        final OrderAdapter.ViewHolder viewHolder;
        float price;
        String priceString = "", multiple = "", stock, aux;

        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.order_item, null);

            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.order_item, null);

            viewHolder = new ViewHolder();

            viewHolder.btnAddItem = view.findViewById(R.id.btnAddItem);
            viewHolder.imgItem = view.findViewById(R.id.imgItem);
            viewHolder.txtItemName = view.findViewById(R.id.txtItemName);
            viewHolder.txtItemName.setTypeface(null, Typeface.BOLD);
            viewHolder.txtItemStock = view.findViewById(R.id.txtItemStock);
            viewHolder.txtItemMultiple = view.findViewById(R.id.txtItemMultiple);
            viewHolder.txtItemPrice = view.findViewById(R.id.txtItemPrice);
            viewHolder.txtItemPrice.setTypeface(null, Typeface.BOLD);
            viewHolder.txtItemTotal = view.findViewById(R.id.txtItemTotal);
            viewHolder.txtItemQuantity = view.findViewById(R.id.txtItemQuantity);
            viewHolder.txtQuantityString = view.findViewById(R.id.txtQuantityString);
            viewHolder.txtTotalString = view.findViewById(R.id.txtTotalString);

            viewHolder.btnAddItem.setTag(position);
            viewHolder.imgItem.setTag(position);
            viewHolder.txtItemName.setTag(position);
            viewHolder.txtItemStock.setTag(position);
            viewHolder.txtItemMultiple.setTag(position);
            viewHolder.txtItemPrice.setTag(position);
            viewHolder.txtItemTotal.setTag(position);
            viewHolder.txtItemQuantity.setTag(position);
            viewHolder.txtQuantityString.setTag(position);
            viewHolder.txtTotalString.setTag(position);
            view.setTag(viewHolder);

        } else {
//            viewHolder = (OrderAdapter.ViewHolder) convertView.getTag();
            view = convertView;
            ((ViewHolder)view.getTag()).btnAddItem.setTag(position);
            ((ViewHolder)view.getTag()).imgItem.setTag(position);
            ((ViewHolder)view.getTag()).txtItemName.setTag(position);
            ((ViewHolder)view.getTag()).txtItemStock.setTag(position);
            ((ViewHolder)view.getTag()).txtItemMultiple.setTag(position);
            ((ViewHolder)view.getTag()).txtItemPrice.setTag(position);
            ((ViewHolder)view.getTag()).txtItemTotal.setTag(position);
            ((ViewHolder)view.getTag()).txtItemQuantity.setTag(position);
            ((ViewHolder)view.getTag()).txtQuantityString.setTag(position);
            ((ViewHolder)view.getTag()).txtTotalString.setTag(position);

        }

        product = productList.get(position);

        final ViewHolder holder = (ViewHolder) view.getTag();

        holder.btnAddItem.setId(position);
        holder.txtItemQuantity.setId(position);
        holder.txtItemTotal.setId(position);

        loadImage(holder.imgItem);

        holder.txtItemName.setText(product.getName());
        multiple = (product.getMultiple().equals("0") ?  "1" : product.getMultiple()) + context.getString(R.string.txtMultiple);
        holder.txtItemMultiple.setText(multiple);
        stock = Integer.valueOf(product.getStock()) > 0 ? context.getString(R.string.avilableProd) : context.getString(R.string.notAvailableProd) ;
        holder.txtItemStock.setText(stock);

        if(product.getOfferPrice() != null && !product.getOfferPrice().isEmpty() && !product.getOfferPrice().equals("0"))
            aux = product.getOfferPrice();
        else if (product.getListPrice02() != null && !product.getListPrice02().isEmpty() && !product.getListPrice02().equals("0"))
            aux = product.getListPrice02();
        else if (product.getListPrice01() != null && !product.getListPrice01().isEmpty() && !product.getListPrice01().equals("0"))
            aux = product.getListPrice01();
        else if (product.getListPrice00() != null && !product.getListPrice00().isEmpty() && !product.getListPrice00().equals("0"))
            aux = product.getListPrice00();
        else if ((product.getSalePrice00() != null && !product.getSalePrice00().isEmpty() && !product.getSalePrice00().equals("0")))
            aux = product.getSalePrice00();
        else
            aux = "0";

        price = Utilities.roundNumber(aux);
        priceString = String.format("%.2f", price);
        product.setRealPrice(price);//
        holder.txtItemPrice.setText(priceString);

//        holder.txtItemTotal.setText("");
//        holder.txtItemQuantity.setText("");
//        holder.txtTotalString.setText("");
//        holder.txtQuantityString.setText("");
        holder.txtItemTotal.setVisibility(View.GONE);
        holder.txtItemQuantity.setVisibility(View.GONE);
        holder.txtTotalString.setVisibility(View.GONE);
        holder.txtQuantityString.setVisibility(View.GONE);


        if (!listOrder.isEmpty()) {
            for (BodyOrder order : listOrder) {
                if (order.getIdProduct().equals(product.getIdProduct())) {
                    holder.txtItemTotal.setVisibility(View.VISIBLE);
                    holder.txtItemQuantity.setVisibility(View.VISIBLE);
                    holder.txtTotalString.setVisibility(View.VISIBLE);
                    holder.txtQuantityString.setVisibility(View.VISIBLE);
                    holder.txtTotalString.setText(R.string.txtTotal);
                    holder.txtQuantityString.setText(R.string.txtUnits);
                    holder.txtItemTotal.setText(String.format("%.2f", order.getTotal()));
                    holder.txtItemQuantity.setText(String.valueOf(order.getQuantity()));
                    break;
                }
            }
        }



        holder.btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product productChosen;

                body = new BodyOrder();
                int pos = holder.btnAddItem.getId();
                productChosen = productList.get(pos);

                if (Integer.valueOf(productChosen.getStock()) == 0) {
//                    Utilities.showMsg(context.getString(R.string.msgErrOutStock), context);
                    if(!context.isFinishing())
                        Toast.makeText(context, R.string.msgErrOutStock, Toast.LENGTH_LONG).show();

                    return;
                }

                body.setName(productChosen.getName());
                body.setIdProduct(productChosen.getIdProduct());
                body.setPrice(productChosen.getRealPrice());
                body.setQuantity(1);
                body.setTotal(body.getQuantity() * body.getPrice());

                if (!updateBody(productChosen))
                    listOrder.add(body);

                holder.txtItemTotal.setVisibility(View.VISIBLE);
                holder.txtItemQuantity.setVisibility(View.VISIBLE);
                holder.txtTotalString.setVisibility(View.VISIBLE);
                holder.txtQuantityString.setVisibility(View.VISIBLE);

                holder.txtItemTotal.setText(String.format("%.2f", body.getTotal()));
                holder.txtItemQuantity.setText(String.valueOf(body.getQuantity()));
                holder.txtTotalString.setText(R.string.txtTotal);
                holder.txtQuantityString.setText(R.string.txtUnits);

//                int multiple, quantity, stock;
//                float total;
//
//                if (chosenProduct == null) {
//                    showMsg(getString(R.string.msgErrChosenProd));
//                    return;
//                }
//
//                if (editQuantity.getText().toString().isEmpty()) {
//                    showMsg(getString(R.string.msgErrEmptyQuantity));
//                    return;
//                }
//
//                multiple = Integer.valueOf(chosenProduct.getMultiple());
//
//                if (multiple == 0)
//                    multiple = 1;
//
//                quantity = Integer.valueOf(editQuantity.getText().toString());
//                stock = Integer.valueOf(chosenProduct.getStock());
//
//                if ((quantity % multiple) != 0) {
//                    showMsg(getString(R.string.msgErrMultiple));
//                    return;
//                }
//
//                if (quantity > stock) {
//                    showMsg(getString(R.string.msgErrStock));
//                    return;
//                }
//
//                bodyOrder = new BodyOrder();
//                bodyOrder.setIdProduct(chosenProduct.getIdProduct());
//                bodyOrder.setPrice(chosenProduct.getRealPrice());
//                bodyOrder.setQuantityString(String.valueOf(quantity));
//                bodyOrder.setIdItem(String.valueOf(itemOrder++));
//                bodyOrder.setName(chosenProduct.getName());
//                total = chosenProduct.getRealPrice() * quantity;
//                bodyOrder.setTotal(total);
//                listOrder.add(bodyOrder);
//                showMsg(getString(R.string.msgSuccAddProd));
//                cleanUp();
//
            }
        });



//        if (!listOrder.isEmpty()) {
//            for (BodyOrder order : listOrder) {
//                if (order.getIdProduct().equals(product.getIdProduct())) {
//                    holder.txtTotalString.setText(R.string.txtSubtotal);
//                    holder.txtQuantityString.setText(R.string.txtUnits);
//                    holder.txtItemTotal.setText(String.format("%.2f", order.getTotal()));
//                    holder.txtItemQuantity.setText(String.valueOf(order.getQuantity()));
//                }
//            }
//        }

//
//        viewHolder.txtItemQuantity.setId(position);
//        viewHolder.txtItemTotal.setId(position);



        return view;
    }

    private void loadImage(ImageView imageView) {
    }

    private boolean updateBody(Product productChosen) {
        int stock = Integer.valueOf(productChosen.getStock());

        if (listOrder.isEmpty()) {
            body.setUpdatedStock(stock - 1);
            return false;
        }

        body.setUpdatedStock(stock - 1);

        for (BodyOrder item: listOrder) {
            if (item.getIdProduct().equals(body.getIdProduct())) {

                int quantity = body.getQuantity() + item.getQuantity();

                if (quantity > stock) {
                    if(!context.isFinishing())
                        Toast.makeText(context, R.string.msgErrStock, Toast.LENGTH_LONG).show();

                    body.setQuantity(item.getQuantity());
                    body.setTotal(item.getTotal());
                    return true;
                }

                item.setTotal(body.getTotal() + item.getTotal());
                item.setQuantity(body.getQuantity() + item.getQuantity());
                item.setUpdatedStock(stock - quantity);
                body.setQuantity(item.getQuantity());
                body.setTotal(item.getTotal());
                body.setUpdatedStock(stock - quantity);
                return true;
            }
        }

        return false;
    }
}
