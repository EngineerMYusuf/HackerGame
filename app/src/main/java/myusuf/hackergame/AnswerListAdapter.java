package myusuf.hackergame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AnswerListAdapter extends ArrayAdapter<answers> {
    private final Context context;
    private ArrayList<answers> answers = new ArrayList<>();

    public AnswerListAdapter(Context context, ArrayList<answers> values) {
        super(context, R.layout.rowlayout, values);
        this.context = context;
        this.answers = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        answers d = answers.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.answerlayout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);

        textView.setText(d.getText());
        if (d.isChosen()){
            imageView.setImageResource(R.drawable.checkbox);
        } else {
            imageView.setImageResource(R.drawable.checkboxunchecked);
        }
        return rowView;
    }
}