package ru.bobsoccer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private final String TAG = "RecyclerAdapter";

    private ArrayList<Blog> listItems;
    private Blog blogItem;
    private Context mContext;

    public RecyclerAdapter(Context c) {
        super();
        mContext = c;
        listItems = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_list_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        blogItem = getItem(position);

        viewHolder.Blo_HeaderView.setText(blogItem.Blo_Header);
        viewHolder.Blo_AnounceView.setText(blogItem.Blo_Anounce);

        Picasso.with(mContext)
                .load(API.DomainUrl + String.valueOf(blogItem.Blo_Avatar))
                .placeholder(R.drawable.ic_account_circle_dark_48dp)
                .resize(Utils.dpToPx(mContext, 48), Utils.dpToPx(mContext, 48))
                .transform(new CircleTransform())
                .into(viewHolder.Blo_AvatarView);

        viewHolder.Blo_LoginView.setText(blogItem.Blo_Login);
        viewHolder.Blo_DateView.setText(blogItem.Blo_Date);

        viewHolder.buttonForwardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Blog clickItem = getItem(position);
                Intent intent = new Intent(mContext, ItemActivity.class);
                intent.putExtra("Blo_Code", clickItem.Blo_Code);
                mContext.startActivity(intent);
            }
        });
    }

    public Blog getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void addItemsToList(Blog Item) {
        listItems.add(Item);
    }

    public void clearItemsList() {
        listItems.clear();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView Blo_HeaderView;
        public TextView Blo_AnounceView;
        public ImageView Blo_AvatarView;
        public TextView Blo_LoginView;
        public TextView Blo_DateView;
        public Button buttonForwardView;

        public ViewHolder(View itemView) {
            super(itemView);

            Blo_HeaderView = (TextView)itemView.findViewById(R.id.Blo_Header);
            Blo_AnounceView = (TextView)itemView.findViewById(R.id.Blo_Anounce);
            Blo_AvatarView = (ImageView)itemView.findViewById(R.id.Blo_Avatar);
            Blo_LoginView = (TextView)itemView.findViewById(R.id.Blo_Login);
            Blo_DateView = (TextView)itemView.findViewById(R.id.Blo_Date);
            buttonForwardView = (Button)itemView.findViewById(R.id.button_forward);

        }
    }

}
