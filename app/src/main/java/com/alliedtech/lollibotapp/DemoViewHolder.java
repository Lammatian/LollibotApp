package com.alliedtech.lollibotapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public abstract class DemoViewHolder extends RecyclerView.ViewHolder {

    public TextView title;

    public DemoViewHolder(View view) {
        super(view);


        this.title = view.findViewById(R.id.cardTitle);

    }
}