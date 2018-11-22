package com.marakana.android.yamba;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class StatusFragment extends Fragment implements OnClickListener {
    private static final String TAG = "StatusFragment";
    private EditText editStatus;
    private Button buttonTweet;
    private TextView textCount;
    private int defaultTextColor;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.fragment_status, container, false);
        editStatus = (EditText) view.findViewById(R.id.editStatus);
        buttonTweet = (Button) view.findViewById(R.id.buttonTweet);
        textCount = (TextView) view.findViewById(R.id.textCount);
        buttonTweet.setOnClickListener(this);
        defaultTextColor = textCount.getTextColors().getDefaultColor();
        editStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int count = 140 - editStatus.length();
                textCount.setText(Integer.toString(count));
                textCount.setTextColor(Color.GREEN);
                if (count < 10)
                    textCount.setTextColor(Color.RED);
                else
                    textCount.setTextColor(defaultTextColor);
            }
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start, int count,
                                          int after) {
            }
            @Override
            public void onTextChanged(CharSequence s,
                                      int start, int before,
                                      int count) {
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        String status = editStatus.getText().toString();
        Log.d(TAG, "onClicked with status: " + status);
        new PostTask().execute(status);
    }

    private final class PostTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String username = prefs.getString("username", "");
                String password = prefs.getString("password", "");

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    getActivity().startActivity(
                            new Intent(getActivity(), SettingsActivity.class));
                    return "Please update your username and password";
                }

                YambaClient cloud = new YambaClient("student", "password", "http://yamba.newcircle.com/api");
                cloud.postStatus(params[0]);
            } catch (YambaClientException e) {
                e.printStackTrace();
                return "failed";
            }
            return "Successfully posted";
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(StatusFragment.this.getActivity(),
                    result, Toast.LENGTH_LONG).show();
        }
    }
}

