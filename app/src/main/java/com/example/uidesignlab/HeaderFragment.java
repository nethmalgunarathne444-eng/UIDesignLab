package com.example.uidesignlab;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
public class HeaderFragment extends Fragment {
    private static final String TAG = "LifecycleTracker";
    // The interface: the Activity must write this method


    public interface OnHeaderInteractionListener {
        void onButtonClicked(String data);

        void onClearClicked();
    }
    private OnHeaderInteractionListener mListener;
    public HeaderFragment() {
        // A Fragment needs an empty constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "Header onAttach");
        if (context instanceof OnHeaderInteractionListener) {
            mListener = (OnHeaderInteractionListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnHeaderInteractionListener");
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Header onCreateView");
        View view = inflater.inflate(R.layout.fragment_header, container, false);
        Button btnHeader = view.findViewById(R.id.btnHeader);
        btnHeader.setOnClickListener(v ->
                mListener.onButtonClicked("Hello from Header!"));

        Button btnHeader2 = view.findViewById(R.id.btnHeader2);
        btnHeader2.setOnClickListener(v ->
                mListener.onClearClicked());
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "Header onDestroyView");
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}