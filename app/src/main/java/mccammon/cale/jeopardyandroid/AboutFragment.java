package mccammon.cale.jeopardyandroid;

import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//AboutFragment provides information about the application in the About section.
public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about,
                container, false);
        Spanned htmlText = Html.fromHtml(getString(R.string.aboutText));
        TextView aboutView = view.findViewById(R.id.aboutBody);
        aboutView.setText(htmlText);
        aboutView.setMovementMethod(LinkMovementMethod.getInstance());

        //Get version number.
        try {
            String versionName = "v" + getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), 0).versionName;
            TextView versionView = view.findViewById(R.id.aboutVersion);
            versionView.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return view;
    }
}