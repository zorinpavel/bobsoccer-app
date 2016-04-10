package ru.bobsoccer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class TabFragment extends Fragment {

    private final String TAG = "TabFragment";

    private static String DATA_TYPE = "DATA_TYPE";

    private boolean fragmentResume = false;
    private boolean fragmentVisible = false;
    private boolean fragmentOnCreated = false;

    private RecyclerAdapter recyclerAdapter;

    public static TabFragment newInstance(int position) {
        Bundle frameArguments = new Bundle();

        frameArguments.putInt(DATA_TYPE, position);

        TabFragment fragment = new TabFragment();
        fragment.setArguments(frameArguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerAdapter = new RecyclerAdapter(getContext());
        recyclerView.setAdapter(recyclerAdapter);

        if (!fragmentResume && fragmentVisible) //only when first time fragment is created
            GetItemsList();

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;
            GetItemsList();
        } else if (isVisibleToUser) { // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
        } else if(fragmentOnCreated) { // only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
        }
    }

    public void GetItemsList() {
        Map<String, String> params = new HashMap<>();
        params.put("fragmentDataType", String.valueOf(getArguments().getInt(DATA_TYPE) + 1));

        new API(getActivity(), new API.ApiResponse() {
            @Override
            public void onTaskCompleted(JSONObject resultObj) {
                JSONArray resultItems;
                try {
                    resultItems = resultObj.getJSONArray("Items");
                    for (int i = 0; i < resultItems.length(); i++) {
                        recyclerAdapter.addItemsToList(new Blog(resultItems.getJSONObject(i)));
                    }
                    recyclerAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, "GET", "Blog", "GetItemsList")
                .requestParams(params)
                .execute();
    }

}