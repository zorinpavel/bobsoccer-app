package ru.bobsoccer;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.bobsoccer.model.User;

public class UserNavigationAdapter extends RecyclerView.Adapter<UserNavigationAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String UserTitles[] = {"Выход",};
    private int UserIcons[] = {
        R.drawable.ic_off,
    };

    private Activity mActivity;
    private User currentUser;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int HolderId;

        ImageView imageView;
        TextView textView;

        ImageView avatar;
        TextView login;
        TextView mail;
        ImageView Level;
        TextView Lev_Points;
        TextView Prev_Points;


        public ViewHolder(View itemView, int ViewType) {
            super(itemView);

            if(ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                HolderId = 1;
            } else {
                avatar = (ImageView) itemView.findViewById(R.id.avatar);
                login = (TextView) itemView.findViewById(R.id.login);
                mail = (TextView) itemView.findViewById(R.id.mail);
                Level = (ImageView) itemView.findViewById(R.id.Level);
                Lev_Points = (TextView) itemView.findViewById(R.id.Lev_Points);
                Prev_Points = (TextView) itemView.findViewById(R.id.Prev_Points);
                HolderId = 0;
            }
        }


    }


    UserNavigationAdapter(Activity context) {
        mActivity = context;
    }

    public void updateData(User _currentUser) {
        currentUser = _currentUser;
        notifyDataSetChanged();
    }

    @Override
    public UserNavigationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_navigation_item, parent, false);
            return new ViewHolder(v, viewType);

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_navigation_header, parent, false);
            return new ViewHolder(v, viewType);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(UserNavigationAdapter.ViewHolder holder, int position) {
        if(holder.HolderId == 1) {
            holder.textView.setText(UserTitles[position - 1]);
            holder.imageView.setImageResource(UserIcons[position -1]);
        } else {
            Picasso.with(mActivity)
                    .load(API.DomainUrl + currentUser.avatar)
                    .error(R.drawable.ic_toolbar_account_circle)
                    .into(holder.avatar);
            holder.login.setText(currentUser.login);
            holder.mail.setText(currentUser.mail);
        }
    }

    @Override
    public int getItemCount() {
        return UserTitles.length + 1;
    }


    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}