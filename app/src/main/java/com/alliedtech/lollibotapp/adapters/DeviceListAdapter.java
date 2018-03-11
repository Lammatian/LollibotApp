package com.alliedtech.lollibotapp.adapters;

//Adapter for Listview for devices

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.constraint.solver.widgets.Rectangle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.alliedtech.lollibotapp.R;

import java.util.ArrayList;

public class DeviceListAdapter extends BaseAdapter implements ListAdapter{

    private Context context;
    private ArrayList<String> deviceNames= new ArrayList<>();
    private ArrayList<String> deviceMacAddresses = new ArrayList<>();
    private ArrayList<Integer> deviceSignalStrengths = new ArrayList<>();


    public DeviceListAdapter(ArrayList<String> deviceNames, ArrayList<String> deviceMacAddresses, ArrayList<Integer> deviceSignalStrengths, Context context) {
        this.context = context;
        this.deviceNames = deviceNames;
        this.deviceMacAddresses = deviceMacAddresses;
        this.deviceSignalStrengths = deviceSignalStrengths;
    }

    @Override
    public int getCount() {
        return deviceNames.size();
    }

    @Override
    public Object getItem(int i) {return deviceNames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View tempView = view;
        if (tempView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            tempView = inflater.inflate(R.layout.device_list_row, null);
        }

        TextView deviceTitle = tempView.findViewById(R.id.deviceTitle);
        TextView deviceMac = tempView.findViewById(R.id.deviceMac);
        ImageView deviceSignalImage = tempView.findViewById(R.id.signalImage);

        deviceTitle.setText(deviceNames.get(i));
        deviceMac.setText(deviceMacAddresses.get(i));

        deviceSignalImage.setImageDrawable(getSignalImageCrop(deviceSignalStrengths.get(i),tempView));
        return tempView;
    }
    //Less than -90 = 1 bar, -90 < signal < -80 = 2 bars etc
    private BitmapDrawable getSignalImageCrop(int signalLevel, View v) {
        Bitmap signalImage = BitmapFactory.decodeResource(v.getResources(),R.drawable.signal_strength);

        int width = signalImage.getWidth();
        int height = signalImage.getHeight();

        int newWidth = width/2;
        int newHeight = height/2;

        //Calculate scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight)/height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);

        int x,y;

        if (signalLevel <= -90) {
            x = 0;
            y = 0;
        }
        else if ( (-90< signalLevel) && (signalLevel <= -80 )) {
            x = newWidth;
            y = 0;
        }
        else if ((-80 < signalLevel) && (signalLevel <= -70)) {
            x = 0;
            y = newHeight;
        }
        else {
            x = newWidth;
            y = newHeight;
        }

        Bitmap resizedBitmap = Bitmap.createBitmap(signalImage,x,y,newWidth,newHeight,matrix,true);

        return new BitmapDrawable(v.getResources(),resizedBitmap);
    }
}
