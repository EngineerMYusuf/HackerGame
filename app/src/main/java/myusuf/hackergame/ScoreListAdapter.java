package myusuf.hackergame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoreListAdapter extends ArrayAdapter<Integer> {

        private final Context context;
        private ArrayList<Integer> scores = new ArrayList<>();

    public ScoreListAdapter(Context context, ArrayList<Integer> values) {
        super(context, R.layout.scorelayout, values);
        this.context = context;
        this.scores = values;
    }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        Integer d = scores.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View scoreView = inflater.inflate(R.layout.scorelayout, parent, false);
        TextView textView = (TextView) scoreView.findViewById(R.id.scoreLBL);

        String str = (position + 1) + "th Score: " + d;
        textView.setText(str);
        return scoreView;
    }
}
