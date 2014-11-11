package com.example.user.bola;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;


public class MyActivity extends Activity {

    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChanged(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        LoginButton lb = (LoginButton) findViewById(R.id.fbLogin);
        lb.setPublishPermissions(Arrays.asList("email", "public_profile", "user_friends"));

    }

    @Override
    protected void onResume() {
        super.onResume();

        Session session = Session.getActiveSession();

        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChanged(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    // Methods Facebook
    public void onSessionStateChanged(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            Log.i("Script", "Usuário conectado");
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        TextView nome = (TextView) findViewById(R.id.nome);
                        nome.setText(user.getFirstName() + " " + user.getLastName());

                        TextView email = (TextView) findViewById(R.id.email);
                        email.setText(user.getProperty("email").toString());

                        TextView id = (TextView) findViewById(R.id.id);
                        id.setText(user.getId());

                        ProfilePictureView ppv = (ProfilePictureView) findViewById(R.id.fbImg);
                        ppv.setProfileId(user.getId());

                        getFriends(session);
                    }
                }
            }).executeAsync();
        } else {
            Log.i("Script", "Usuário não conectado");
        }
    }

    public void getFriends(Session session) {
        Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {
            @Override
            public void onCompleted(List<GraphUser> users, Response response) {
                if (users != null) {
                    Log.i("Script", "Friends: " + users.size());
                }

                Log.i("Script", "response: " + response);
            }
        }).executeAsync();
    }
}
