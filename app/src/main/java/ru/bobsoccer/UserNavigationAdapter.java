package ru.bobsoccer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class UserNavigationAdapter extends RecyclerView.Adapter<UserNavigationAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private String mTitles[];
    private int mIcons[];

    private String name;
    private int profile;
    private String email;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        int HolderId;

        TextView textView;
        ImageView imageView;
        ImageView profile;
        TextView Name;
        TextView email;


        public ViewHolder(View itemView, int ViewType) {
            super(itemView);

            if(ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                HolderId = 1;
            } else {
                Name = (TextView) itemView.findViewById(R.id.name);
                email = (TextView) itemView.findViewById(R.id.email);
                profile = (ImageView) itemView.findViewById(R.id.circleView);
                HolderId = 0;
            }
        }


    }


    UserNavigationAdapter(String Titles[], int Icons[], String Name, String Email, int Profile) {
        mTitles = Titles;
        mIcons = Icons;
        name = Name;
        email = Email;
        profile = Profile;
    }


    @Override
    public UserNavigationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_navigation_item, parent, false);
            return new ViewHolder(v, viewType);

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_navigation_header, parent, false);
            return new ViewHolder(v,viewType);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(UserNavigationAdapter.ViewHolder holder, int position) {
        if(holder.HolderId == 1) {
            holder.textView.setText(mTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position -1]);
        } else {
            holder.profile.setImageResource(profile);           // Similarly we set the resources for header view
            holder.Name.setText(name);
            holder.email.setText(email);
        }
    }

    @Override
    public int getItemCount() {
        return mTitles.length + 1;
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