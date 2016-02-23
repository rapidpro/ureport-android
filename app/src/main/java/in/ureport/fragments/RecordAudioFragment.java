package in.ureport.fragments;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import br.com.ilhasoft.support.tool.bitmap.IOManager;
import in.ureport.R;
import in.ureport.helpers.MediaSelector;
import in.ureport.helpers.TimeFormatter;
import in.ureport.models.Media;

/**
 * Created by john-mac on 2/19/16.
 */
public class RecordAudioFragment extends DialogFragment {

    private static final String TAG = "RecordAudioFragment";

    private static final int MAX_DURATION_MS = 50 * 1000;

    private static final int IDLE_STATUS = 0;
    private static final int RECORDING_STATUS = 1;
    private static final int READY_STATUS = 2;
    private static final int PLAYER_STATUS = 3;

    private static final int PLAYING_STATUS = 0;
    private static final int PAUSED_STATUS = 1;
    public static final int INTERVAL_MILLIS = 100;
    public static final String EXTRA_MEDIA = "media";

    private TextView startTime;
    private TextView endTime;
    private ImageView play;
    private SeekBar progress;
    private TextView mainAction;
    private View loadingContainer;

    private Handler handler;
    private IOManager ioManager;
    private File recordedAudio;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;

    private int recordingStatus = IDLE_STATUS;
    private int playbackStatus = PAUSED_STATUS;
    private int duration = 0;

    private CountDownTimer timer;

    private MediaSelector.OnLoadLocalMediaListener onLoadLocalMediaListener;
    private Media media;

    public static RecordAudioFragment newInstance(Media media) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_MEDIA, media);

        RecordAudioFragment fragment = new RecordAudioFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_audio, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupObjects();
        setupView(view);
        setupDataIfExists();
    }

    private void setupDataIfExists() {
        Bundle args = getArguments();
        if(args != null && args.containsKey(EXTRA_MEDIA)) {
            media = args.getParcelable(EXTRA_MEDIA);
            switchRecordingStatus(PLAYER_STATUS);
        }
    }

    private void setupView(View view) {
        loadingContainer = view.findViewById(R.id.loadingContainer);

        startTime = (TextView) view.findViewById(R.id.startTime);
        startTime.setText(TimeFormatter.getDurationString(0));

        endTime = (TextView) view.findViewById(R.id.endTime);
        endTime.setText(TimeFormatter.getDurationStringFromMillis(MAX_DURATION_MS));

        progress = (SeekBar) view.findViewById(R.id.progress);
        progress.setMax(MAX_DURATION_MS);
        progress.setOnSeekBarChangeListener(onSeekBarChangeListener);

        play = (ImageView) view.findViewById(R.id.play);
        play.setOnClickListener(onPlayClickListener);

        mainAction = (TextView) view.findViewById(R.id.mainAction);
        mainAction.setOnClickListener(onMainClickListener);

        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> dismiss());
    }

    private void setupObjects() {
        handler = new Handler();
        ioManager = new IOManager(getContext());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        handler.removeCallbacks(playRunnable);

        if(mediaRecorder != null && recordingStatus == RECORDING_STATUS) {
            stopRecording();
        }

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private View.OnClickListener onMainClickListener = view -> {
        try {
            switch(recordingStatus) {
                case IDLE_STATUS:
                    recordedAudio = ioManager.createAudioFilePath();
                    startRecording();
                    break;
                case RECORDING_STATUS:
                    stopRecording();
                    break;
                case READY_STATUS:
                    if(onLoadLocalMediaListener != null)
                        onLoadLocalMediaListener.onLoadAudio(Uri.fromFile(recordedAudio), duration + 1);
                    dismiss();
            }
        } catch (Exception exception) {
            displayError(R.string.error_message_audio);
            Log.e(TAG, "onViewCreated: ", exception);
        }
    };

    private void stopRecording() {
        switchRecordingStatus(READY_STATUS);

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordedAudio.getAbsolutePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setMaxDuration(MAX_DURATION_MS);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();

            mediaRecorder.setOnInfoListener((mediaRecorder, what, extra) -> {
                if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    stopRecording();
                }
            });
            switchRecordingStatus(RECORDING_STATUS);
        } catch (IOException exception) {
            displayError(R.string.error_message_mic);
            Log.e(TAG, "prepare() failed", exception);
        }
    }

    private void switchRecordingStatus(int status) {
        recordingStatus = status;
        switch (status) {
            case IDLE_STATUS:
                mainAction.setText(R.string.title_button_record);
                break;
            case RECORDING_STATUS:
                duration = 0;
                startProgressTimer(MAX_DURATION_MS);
                mainAction.setText(R.string.title_button_stop);
                break;
            case READY_STATUS:
                timer.cancel();
                play.setVisibility(View.VISIBLE);
                mainAction.setText(R.string.send);
                prepareToPlay();
                break;
            case PLAYER_STATUS:
                mainAction.setVisibility(View.GONE);
                switchPlaybackStatus(PLAYING_STATUS);
                loadAudio();
        }
    }

    private void prepareToPlay() {
        switchPlaybackStatus(PAUSED_STATUS);

        progress.setProgress(0);
        startTime.setText(TimeFormatter.getDurationString(0));
        endTime.setText(TimeFormatter.getDurationString(duration));
    }

    private void startProgressTimer(final int milliseconds) {
        if(timer != null)
            timer.cancel();

        timer = new CountDownTimer(milliseconds, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                duration++;
                int timeElapsed = milliseconds - (int)millisUntilFinished;
                int timeElapsedSeconds = timeElapsed/1000;

                if(timeElapsedSeconds != duration) {
                    duration = timeElapsedSeconds;
                    startTime.setText(TimeFormatter.getDurationString(duration));
                }
                progress.setProgress(timeElapsed);
            }
            @Override
            public void onFinish() {}
        }.start();
    }

    private void switchPlaybackStatus(int status) {
        playbackStatus = status;
        play.setVisibility(View.VISIBLE);

        switch(status) {
            case PLAYING_STATUS:
                play.setImageResource(R.drawable.ic_pause_blue_36dp);
                break;
            case PAUSED_STATUS:
                play.setImageResource(R.drawable.ic_play_arrow_blue_36dp);
        }
    }

    private void displayError(@StringRes int errorMessage) {
        switchRecordingStatus(IDLE_STATUS);
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    public void setOnLoadLocalMediaListener(MediaSelector.OnLoadLocalMediaListener onLoadLocalMediaListener) {
        this.onLoadLocalMediaListener = onLoadLocalMediaListener;
    }

    private View.OnClickListener onPlayClickListener = view -> {
        switch(playbackStatus) {
            case PLAYING_STATUS:
                mediaPlayer.pause();
                switchPlaybackStatus(PAUSED_STATUS);
                break;
            case PAUSED_STATUS:
                loadAudio();
                switchPlaybackStatus(PLAYING_STATUS);
        }
    };

    private void loadAudio() {
        if(mediaPlayer != null) {
            mediaPlayer.start();
            handler.post(playRunnable);
        } else {
            mediaPlayer = new MediaPlayer();
            try {
                if(recordingStatus == PLAYER_STATUS)
                    mediaPlayer.setDataSource(media.getUrl());
                else
                    mediaPlayer.setDataSource(recordedAudio.getAbsolutePath());
                mediaPlayer.prepareAsync();

                loadingContainer.setVisibility(View.VISIBLE);
                mediaPlayer.setOnPreparedListener(mediaPlayer -> startPlay());
            } catch (IOException exception) {
                Toast.makeText(getContext(), R.string.error_message_audio_play, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "prepare() failed");
            }
        }
    }

    private void startPlay() {
        startTime.setText(TimeFormatter.getDurationString(0));
        endTime.setText(TimeFormatter.getDurationStringFromMillis(mediaPlayer.getDuration()));
        progress.setMax(mediaPlayer.getDuration());
        loadingContainer.setVisibility(View.GONE);

        handler.post(playRunnable);
        mediaPlayer.start();
    }

    private Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            if(mediaPlayer.isPlaying()) {
                startTime.setText(TimeFormatter.getDurationStringFromMillis(mediaPlayer.getCurrentPosition()));
                progress.setProgress(mediaPlayer.getCurrentPosition());
            }

            if(playbackStatus == PLAYING_STATUS) {
                if(mediaPlayer.getCurrentPosition() != mediaPlayer.getDuration())
                    handler.postDelayed(this, INTERVAL_MILLIS);
                else
                    prepareToPlay();
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(playbackStatus == PLAYING_STATUS
                            ? progress : mediaPlayer.getCurrentPosition());
                } else {
                    seekBar.setProgress(duration);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };
}
