package io.github.sudhansubarik.mothersrecipes.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.sudhansubarik.mothersrecipes.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RiceAndBiryaniFragment extends Fragment {

    public RiceAndBiryaniFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rice_and_biryani, container, false);
    }
}
