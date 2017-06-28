package jeken.com.handhealth.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017-06-27.
 */

public abstract class BaseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;

        if (getContentView() instanceof View){
            view = getView();
        }else if (getContentView() instanceof Integer){
            view = inflater.inflate((Integer) getContentView(),null);
        }else {
            view =  null;
        }
        init(view);
        return view;
    }
    public abstract Object getContentView();
    public abstract void init(View view);
}
