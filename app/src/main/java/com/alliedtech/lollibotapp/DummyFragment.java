package com.alliedtech.lollibotapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class DummyFragment extends Fragment {
    private View view;

    private String title;//String for tab title

    private RecyclerView recyclerView;

    public DummyFragment() {
        Bundle bundle = this.getArguments();

        if (bundle != null)
            this.title = bundle.getString("title");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dummy_fragment, container, false);

        setRecyclerView();
        return view;

    }
    //Setting recycler view
    private void setRecyclerView() {

        recyclerView = view
                .findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView
                .setLayoutManager(new LinearLayoutManager(getActivity()));//Linear Items


        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            arrayList.add(title+" Items " + i);//Adding items to recycler view
        }

        RecyclerView_Adapter adapter = new RecyclerView_Adapter(getActivity(), arrayList);
        recyclerView.setAdapter(adapter);// set adapter on recyclerview
    }
}