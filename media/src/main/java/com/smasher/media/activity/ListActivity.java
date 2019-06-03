package com.smasher.media.activity;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaControllerCompat.TransportControls;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.smasher.media.R;
import com.smasher.media.adapter.MusicListAdapter;
import com.smasher.media.adapter.OnItemClickListener;
import com.smasher.media.helper.MediaBrowserHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author moyu
 */
public class ListActivity extends AppCompatActivity implements OnItemClickListener {

    private static final String TAG = "ListActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(R.id.prepare)
    Button mPrepare;
    @BindView(R.id.load)
    Button mLoad;
    @BindView(R.id.stop)
    Button mStop;
    @BindView(R.id.release)
    Button mRelease;

    @BindView(R.id.previous)
    ImageButton previous;
    @BindView(R.id.play_pause)
    ImageButton playPause;
    @BindView(R.id.next)
    ImageButton next;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private MediaBrowserHelper mMediaBrowserHelper;
    private TransportControls mController;
    private MusicListAdapter mAdapter;
    private MediaBrowserCompat mMediaBrowser;

    private PlaybackStateCompat mState;

    private List<MediaItem> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        initView();
        initMediaBrowser();

    }

    private void initMediaBrowser() {
        mMediaBrowserHelper = new MediaBrowserHelper(this) {
            @Override
            public void connectToSession(MediaSessionCompat.Token token) {
                Log.d(TAG, "connectToSession: ");
                mMediaBrowserHelper.isConnected();
                mMediaBrowserHelper.getRoot();
                connectToSessionImp(token);
            }
        };
        mMediaBrowser = mMediaBrowserHelper.getMediaBrowser();
        mSubscriptionCallback = new MediaBrowserSubscriptionCallback();
    }

    private void initView() {
        mAdapter = new MusicListAdapter(this);
        mAdapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        updateViewState();


    }


    private void updateViewState() {
        boolean isPlaying = isPlaying();
        playPause.setImageResource(isPlaying ? R.drawable.music_pause : R.drawable.music_play);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        mMediaBrowserHelper.connect();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }


    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        mMediaBrowserHelper.disconnect();
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void connectToSessionImp(MediaSessionCompat.Token token) {

        try {
            MediaControllerCompat controller = new MediaControllerCompat(this, token);
            MediaControllerCompat.setMediaController(this, controller);
            controller.registerCallback(new MediaControllerCallback());
            mController = controller.getTransportControls();

            mState = controller.getPlaybackState();
            Log.d(TAG, "connectToSessionImp: init state:" + mState.getState());

            if (mMediaBrowserHelper != null && mMediaBrowserHelper.isConnected()) {
                subscribe("default");
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    @OnClick({R.id.fab, R.id.load, R.id.prepare, R.id.stop, R.id.release,
            R.id.previous, R.id.play_pause, R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab:
                Snackbar snackbar = Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG);
                snackbar.setAction("Action", null);
                snackbar.show();
                break;

            case R.id.load:

                break;
            case R.id.prepare:
                if (mController != null) {
                    mController.prepare();
                }
                break;

            case R.id.play_pause:

                if (mController == null) {
                    return;
                }


                boolean isPlaying = isPlaying();
                if (!isPlaying) {
                    Log.d(TAG, "play:isLoad");
                    mController.play();
                } else {
                    mController.pause();
                }
                break;
            case R.id.next:
                if (mController != null) {
                    mController.skipToNext();
                }
                break;
            case R.id.previous:
                if (mController != null) {
                    mController.skipToPrevious();
                }
                break;
            case R.id.stop:
                break;
            case R.id.release:
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view, int position) {

    }


    private boolean isPlaying() {
        boolean isPlaying = false;
        if (mState != null) {
            isPlaying = mState.getState() == PlaybackStateCompat.STATE_PLAYING;
        }
        return isPlaying;
    }


    private boolean isInitPlay() {
        boolean ok = false;
        if (mState == null) {
            ok = mState.getState() == PlaybackStateCompat.STATE_NONE;
        }
        return ok;
    }


    //region Subscription

    /**
     * MediaBrowserSubscriptionCallback
     */
    public class MediaBrowserSubscriptionCallback extends SubscriptionCallback {

        public static final String TAG = "SubscriptionCallback";

        MediaBrowserSubscriptionCallback() {
            super();
        }


        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
            //加载到的
            Log.d(TAG, "onChildrenLoaded: " + parentId);
            mList = children;
            mAdapter.setData(mList);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaItem> children, @NonNull Bundle options) {
            super.onChildrenLoaded(parentId, children, options);
            Log.d(TAG, "onChildrenLoaded: ");
        }

        @Override
        public void onError(@NonNull String parentId) {
            super.onError(parentId);
            Log.d(TAG, "onError: ");
        }

        @Override
        public void onError(@NonNull String parentId, @NonNull Bundle options) {
            super.onError(parentId, options);
            Log.d(TAG, "onError: ");
        }
    }

    MediaBrowserSubscriptionCallback mSubscriptionCallback;

    private void unsubscribe(String parentId) {
        mMediaBrowser.unsubscribe(parentId);
    }

    private void subscribe(String parentId) {
        unsubscribe(parentId);
        mMediaBrowser.subscribe(parentId, mSubscriptionCallback);
    }

    //endregion


    //region ControllerCallback

    /**
     * MediaControllerCallback
     */
    class MediaControllerCallback extends MediaControllerCompat.Callback {
        public static final String TAG = "ControllerCallback";

        public MediaControllerCallback() {
            super();
        }

        @Override
        public void onSessionReady() {
            super.onSessionReady();
            Log.d(TAG, "onSessionReady: ");
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            Log.d(TAG, "onSessionDestroyed: ");
        }

        @Override
        public void onSessionEvent(@NonNull String event, @Nullable Bundle extras) {
            super.onSessionEvent(event, extras);
            Log.d(TAG, "onSessionEvent: " + event);
        }

        @Override
        public void onPlaybackStateChanged(@Nullable PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            mState = state;
            updateViewState();
            String value = state != null ? String.valueOf(state.getState()) : "";
            Log.d(TAG, "onPlaybackStateChanged " + value);
        }

        @Override
        public void onMetadataChanged(@Nullable MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            if (metadata != null) {
                Log.d(TAG, "onMetadataChanged: " + metadata.getDescription().getTitle());
            } else {
                Log.d(TAG, "onMetadataChanged ");
            }
        }

        @Override
        public void onQueueChanged(@Nullable List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
            if (queue != null) {
                Log.d(TAG, "onQueueChanged: " + queue.size());
            } else {
                Log.d(TAG, "onQueueChanged ");
            }
        }

        @Override
        public void onQueueTitleChanged(@Nullable CharSequence title) {
            super.onQueueTitleChanged(title);
            Log.d(TAG, "onQueueTitleChanged: " + title);
        }

        @Override
        public void onExtrasChanged(@Nullable Bundle extras) {
            super.onExtrasChanged(extras);
            Log.d(TAG, "onExtrasChanged");
        }

        @Override
        public void onAudioInfoChanged(MediaControllerCompat.PlaybackInfo info) {
            super.onAudioInfoChanged(info);
            Log.d(TAG, "onAudioInfoChanged: ");
        }


        @Override
        public void onCaptioningEnabledChanged(boolean enabled) {
            super.onCaptioningEnabledChanged(enabled);
            Log.d(TAG, "onCaptioningEnabledChanged: " + enabled);
        }


        @Override
        public void onRepeatModeChanged(int repeatMode) {
            super.onRepeatModeChanged(repeatMode);
            Log.d(TAG, "onRepeatModeChanged: ");
        }


        @Override
        public void onShuffleModeChanged(int shuffleMode) {
            super.onShuffleModeChanged(shuffleMode);
            Log.d(TAG, "onShuffleModeChanged: ");
        }
    }


    //endregion
}
