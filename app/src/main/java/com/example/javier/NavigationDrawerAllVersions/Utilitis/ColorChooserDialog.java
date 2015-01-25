package com.example.javier.NavigationDrawerAllVersions.Utilitis;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.example.javier.NavigationDrawerAllVersions.MainActivity;
import com.example.javier.NavigationDrawerAllVersions.R;
import com.example.javier.NavigationDrawerAllVersions.Settings;

public class ColorChooserDialog extends DialogFragment implements View.OnClickListener {
    CardView cardView1, cardView2, cardView3, cardView4, cardView5, cardView6;
    Button buttonDisagree, buttonAgree;
    View view;
    int currentTheme;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ActivityOptions options;
    Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("THEMES", Context.MODE_PRIVATE);
        currentTheme = sharedPreferences.getInt("THEME", 0);

        view = inflater.inflate(R.layout.theme_dialog, container);
        cardView1 = (CardView) view.findViewById(R.id.card_view1);
        cardView2 = (CardView) view.findViewById(R.id.card_view2);
        cardView3 = (CardView) view.findViewById(R.id.card_view3);
        cardView4 = (CardView) view.findViewById(R.id.card_view4);
        cardView5 = (CardView) view.findViewById(R.id.card_view5);
        cardView6 = (CardView) view.findViewById(R.id.card_view6);
        buttonDisagree = (Button) view.findViewById(R.id.buttonDisagree);
        buttonAgree = (Button) view.findViewById(R.id.buttonAgree);

        cardView1.setOnClickListener(this);
        cardView2.setOnClickListener(this);
        cardView3.setOnClickListener(this);
        cardView4.setOnClickListener(this);
        cardView5.setOnClickListener(this);
        cardView6.setOnClickListener(this);
        buttonDisagree.setOnClickListener(this);
        buttonAgree.setOnClickListener(this);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_view1:
                ((Settings) getActivity()).setThemeFragment(1);
                break;
            case R.id.card_view2:
                ((Settings) getActivity()).setThemeFragment(2);
                break;
            case R.id.card_view3:
                ((Settings) getActivity()).setThemeFragment(3);
                break;
            case R.id.card_view4:
                ((Settings) getActivity()).setThemeFragment(4);
                break;
            case R.id.card_view5:
                ((Settings) getActivity()).setThemeFragment(5);
                break;
            case R.id.card_view6:
                ((Settings) getActivity()).setThemeFragment(6);
                break;
            case R.id.buttonDisagree:
                ((Settings) getActivity()).setThemeFragment(currentTheme);
                getDialog().dismiss();
            case R.id.buttonAgree:
                intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
                break;
        }
    }
}