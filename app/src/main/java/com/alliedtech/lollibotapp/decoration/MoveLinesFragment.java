package com.alliedtech.lollibotapp.decoration;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.alliedtech.lollibotapp.R;

/**
 * Created by mateusz on 27/02/18.
 */

public class MoveLinesFragment extends Fragment {

    private View view;
    private NumberPicker np;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.movelines_fragment, container, false);

        np = view.findViewById(R.id.line_count);
        np.setMinValue(0);
        np.setMaxValue(5);

        return view;
    }
}
