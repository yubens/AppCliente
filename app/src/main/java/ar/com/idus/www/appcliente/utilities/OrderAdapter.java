package ar.com.idus.www.appcliente.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
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

import java.io.InputStream;
import java.util.ArrayList;

import ar.com.idus.www.appcliente.MainActivity;
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                System.out.println("Error " + e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


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
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        final OrderAdapter.ViewHolder viewHolder;
        float price;
        String priceString, multiple, stock, aux;

        if (convertView == null) {
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
        holder.imgItem.setId(position);

        String url;

        if ((holder.imgItem.getId()) % 2 == 0)
            url = "https://i.pinimg.com/originals/f7/02/24/f702246874366e13eafb879746655ed7.jpg";
        else
            url = "https://http2.mlstatic.com/bandera-escudo-club-boca-juniors-2mtsx120-viene-bien-D_NQ_NP_726893-MLA40857773831_022020-F.jpg";

//        loadImage(holder.imgItem, url);

        holder.txtItemName.setText(product.getName());
        multiple = product.getMultiple() + context.getString(R.string.txtMultiple);
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
                int multiple;

                body = new BodyOrder();
                int pos = holder.btnAddItem.getId();
                productChosen = productList.get(pos);

                if (Integer.valueOf(productChosen.getStock()) == 0) {
                    if(!context.isFinishing())
                        Toast.makeText(context, R.string.msgErrOutStock, Toast.LENGTH_LONG).show();

                    return;
                }

                multiple = Integer.valueOf(productChosen.getMultiple());

                body.setName(productChosen.getName());
                body.setIdProduct(productChosen.getIdProduct());
                body.setPrice(productChosen.getRealPrice());
                body.setQuantity(multiple);
                body.setMultiple(multiple);
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
            }
        });

        return view;
    }

    private void loadImage(ImageView imageView, String url) {
        new DownloadImageTask(imageView)
                .execute(url);
    }

    private boolean updateBody(Product productChosen) {
        int stock = Integer.valueOf(productChosen.getStock());
        int multiple = Integer.valueOf(productChosen.getMultiple());

        if (listOrder.isEmpty()) {
            body.setUpdatedStock(stock - multiple);
            return false;
        }

        body.setUpdatedStock(stock - multiple);

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
