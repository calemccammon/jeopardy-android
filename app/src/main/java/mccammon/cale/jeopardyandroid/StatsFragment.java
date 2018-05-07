package mccammon.cale.jeopardyandroid;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import static mccammon.cale.jeopardyandroid.ClueActivity.getLifetimeScore;

//StatsFragment provides the UI and logic for the Stats page. It gets the Session Score and
//Lifetime Score from the ClueActivity and populates the table layout.
public class StatsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats,
                container, false);
        TextView sessionView = view.findViewById(R.id.sessionText);
        sessionView.setText(String.valueOf(ClueActivity.getSessionScore()));
        TextView lifetimeView = view.findViewById(R.id.lifetimeText);
        lifetimeView.setText(String.valueOf(getLifetimeScore(getActivity())));
        return view;
    }
}