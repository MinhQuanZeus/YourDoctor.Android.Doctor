package com.yd.yourdoctorpartnerandroid.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.adapters.NewsAdapter;
import com.yd.yourdoctorpartnerandroid.models.New;
import com.yd.yourdoctorpartnerandroid.networks.xmlNewsService.XMLController;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    RecyclerView rvNews;
    ImageView ivLogoNews;
    private List<New> newList = new ArrayList<>();
    private NewsAdapter newsAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rvNews =  view.findViewById(R.id.rv_news);
        ivLogoNews = view.findViewById(R.id.iv_logo_news);
        ivLogoNews.setImageResource(R.drawable.vnpresslogo);
        setnewList(rvNews);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setnewList(RecyclerView rvNews) {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                getContext(),
                2,
                LinearLayoutManager.VERTICAL,
                false
        );
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (position % 3 == 0 ? 2 : 1);
            }
        });
        new XMLController(getContext(), rvNews, gridLayoutManager).execute(getResources().getString(R.string.url_newPaper));


    }

}
