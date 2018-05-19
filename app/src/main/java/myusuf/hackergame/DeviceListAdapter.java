package myusuf.hackergame;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceListAdapter extends ArrayAdapter<mDevice> {
    private final Context context;
    private ArrayList<mDevice> devices = new ArrayList<>();

    public DeviceListAdapter(Context context, ArrayList<mDevice> values) {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.devices = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mDevice d = devices.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);

        String str = "Name: " + d.getResult().getDevice().getName() + " RSSI: " + d.getRssi();
        textView.setText(str);
        if (d.isInfected()){
            imageView.setImageResource(R.drawable.red);
        } else {
            imageView.setImageResource(R.drawable.yellow);
        }
        return rowView;
    }
}
