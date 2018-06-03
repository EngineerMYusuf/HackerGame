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
        TextView textName = (TextView) rowView.findViewById(R.id.labelName);
        TextView textRSSI = (TextView) rowView.findViewById(R.id.labelRSSI);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);

        String str = "Node " + d.getResult().getDevice().getAddress().substring(0,2);
        textName.setText(str);
        str = "Signal Strength: " + (d.getRssi() + 100);
        textRSSI.setText(str);
        if (d.isInfected()){
            imageView.setImageResource(R.drawable.red);
        } else {
            imageView.setImageResource(R.drawable.yellow);
        }
        return rowView;
    }
}
