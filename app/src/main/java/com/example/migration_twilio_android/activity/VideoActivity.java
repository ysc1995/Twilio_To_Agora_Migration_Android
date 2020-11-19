package com.example.migration_twilio_android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.migration_twilio_android.R;
import com.example.migration_twilio_android.dialog.Dialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;


public class VideoActivity extends AppCompatActivity {
    private static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "VideoActivity";

    /*
     * Audio and video tracks can be created with names. This feature is useful for categorizing
     * tracks of participants. For example, if one participant publishes a video track with
     * ScreenCapturer and CameraCapturer with the names "screen" and "camera" respectively then
     * other participants can use RemoteVideoTrack#getName to determine which video track is
     * produced from the other participant's screen or camera.
     */
//    private static final String LOCAL_AUDIO_TRACK_NAME = "mic";
//    private static final String LOCAL_VIDEO_TRACK_NAME = "camera";

    /*
     * You must provide a Twilio Access Token to connect to the Video service
     */
//    private static final String TWILIO_ACCESS_TOKEN = BuildConfig.TWILIO_ACCESS_TOKEN;
//    private static final String ACCESS_TOKEN_SERVER = BuildConfig.TWILIO_ACCESS_TOKEN_SERVER;

    /*
     * Access token used to connect. This field will be set either from the console generated token
     * or the request to the token server.
     */
//    private String accessToken;

    /*
     * A Room represents communication between a local participant and one or more participants.
     */
//    private Room room;
//    private LocalParticipant localParticipant;

    /*
     * AudioCodec and VideoCodec represent the preferred codec for encoding and decoding audio and
     * video.
     */
//    private AudioCodec audioCodec;
//    private VideoCodec videoCodec;

    /*
     * Encoding parameters represent the sender side bandwidth constraints.
     */
//    private EncodingParameters encodingParameters;

    /*
     * A VideoView receives frames from a local or remote video track and renders them
     * to an associated view.
     */
    private SurfaceView primaryVideoView;
    private SurfaceView thumbnailVideoView;
    private RelativeLayout mRemoteContainer;
    private FrameLayout mLocalContainer;

    /*
     * Android shared preferences used for settings
     */
    private SharedPreferences preferences;

    /*
     * Android application UI elements
     */
//    private CameraCapturerCompat cameraCapturerCompat;
//    private LocalAudioTrack localAudioTrack;
//    private LocalVideoTrack localVideoTrack;
    private FloatingActionButton connectActionFab;
    private FloatingActionButton switchCameraActionFab;
    private FloatingActionButton localVideoActionFab;
    private FloatingActionButton muteActionFab;
//    private ProgressBar reconnectingProgressBar;
    private AlertDialog connectDialog;
//    private String remoteParticipantIdentity;

    private RtcEngine mRtcEngine;

    /*
     * Audio management
     */
//    private AudioSwitch audioSwitch;
//    private int savedVolumeControlStream;
//    private MenuItem audioDeviceMenuItem;
//
//    private VideoRenderer localVideoView;
//    private boolean disconnectedFromOnDestroy;
//    private boolean enableAutomaticSubscription;

    private boolean audioEnabled = true;
    private boolean videoEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

//      reconnectingProgressBar = findViewById(R.id.reconnecting_progress_bar);

        connectActionFab = findViewById(R.id.connect_action_fab);
        switchCameraActionFab = findViewById(R.id.switch_camera_action_fab);
        localVideoActionFab = findViewById(R.id.local_video_action_fab);
        muteActionFab = findViewById(R.id.mute_action_fab);
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);

//        /*
//         * Get shared preferences to read settings
//         */
//        preferences = PreferenceManager.getDefaultSharedPreferences(this);
//
//        /*
//         * Setup audio management and set the volume control stream
//         */
//        audioSwitch = new AudioSwitch(getApplicationContext());
//        savedVolumeControlStream = getVolumeControlStream();
//        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        /*
         * Check camera and microphone permissions. Needed in Android M.
         */
        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraAndMicrophone();
        } else {
            initEngine();
//            createAudioAndVideoTracks();
//            setAccessToken();
        }

        /*
         * Set the initial state of the UI
         */
        intializeUI();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_video_activity, menu);
//        audioDeviceMenuItem = menu.findItem(R.id.menu_audio_device);
//
//        /*
//         * Start the audio device selector after the menu is created and update the icon when the
//         * selected audio device changes.
//         */
//        audioSwitch.start((audioDevices, audioDevice) -> {
//            updateAudioDeviceIcon(audioDevice);
//            return Unit.INSTANCE;
//        });
//
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_settings:
//                startActivity(new Intent(this, SettingsActivity.class));
//                return true;
//            case R.id.menu_audio_device:
//                showAudioDevices();
//                return true;
//            default:
//                return false;
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
            boolean cameraAndMicPermissionGranted = true;

            for (int grantResult : grantResults) {
                cameraAndMicPermissionGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
            }

            if (cameraAndMicPermissionGranted) {
                initEngine();
//                createAudioAndVideoTracks();
//                setAccessToken();
            } else {
                Toast.makeText(this,
                        R.string.permissions_needed,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        /*
         * Update preferred audio and video codec in case changed in settings
         */
//        audioCodec = getAudioCodecPreference(SettingsActivity.PREF_AUDIO_CODEC,
//                SettingsActivity.PREF_AUDIO_CODEC_DEFAULT);
//        videoCodec = getVideoCodecPreference(SettingsActivity.PREF_VIDEO_CODEC,
//                SettingsActivity.PREF_VIDEO_CODEC_DEFAULT);
//        enableAutomaticSubscription = getAutomaticSubscriptionPreference(SettingsActivity.PREF_ENABLE_AUTOMATIC_SUBSCRIPTION,
//                SettingsActivity.PREF_ENABLE_AUTOMATIC_SUBSCRIPTION_DEFAULT);
        /*
         * Get latest encoding parameters
         */
//        final EncodingParameters newEncodingParameters = getEncodingParameters();
//
//        /*
//         * If the local video track was released when the app was put in the background, recreate.
//         */
//        if (localVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
//            localVideoTrack = LocalVideoTrack.create(this,
//                    true,
//                    cameraCapturerCompat.getVideoCapturer(),
//                    LOCAL_VIDEO_TRACK_NAME);
//            localVideoTrack.addRenderer(localVideoView);
//
//            /*
//             * If connected to a Room then share the local video track.
//             */
//            if (localParticipant != null) {
//                localParticipant.publishTrack(localVideoTrack);
//
//                /*
//                 * Update encoding parameters if they have changed.
//                 */
//                if (!newEncodingParameters.equals(encodingParameters)) {
//                    localParticipant.setEncodingParameters(newEncodingParameters);
//                }
//            }
//        }
//
//        /*
//         * Update encoding parameters
//         */
//        encodingParameters = newEncodingParameters;
//
//        /*
//         * Update reconnecting UI
//         */
//        if (room != null) {
//            reconnectingProgressBar.setVisibility((room.getState() != Room.State.RECONNECTING) ?
//                    View.GONE :
//                    View.VISIBLE);
//        }
    }

//    @Override
//    protected void onPause() {
//        /*
//         * Release the local video track before going in the background. This ensures that the
//         * camera can be used by other applications while this app is in the background.
//         */
//        if (localVideoTrack != null) {
//            /*
//             * If this local video track is being shared in a Room, unpublish from room before
//             * releasing the video track. Participants will be notified that the track has been
//             * unpublished.
//             */
//            if (localParticipant != null) {
//                localParticipant.unpublishTrack(localVideoTrack);
//            }
//
//            localVideoTrack.release();
//            localVideoTrack = null;
//        }
//        super.onPause();
//    }

    @Override
    protected void onDestroy() {
        /*
         * Tear down audio management and restore previous volume stream
         */
//        audioSwitch.stop();
//        setVolumeControlStream(savedVolumeControlStream);
//
//        /*
//         * Always disconnect from the room before leaving the Activity to
//         * ensure any memory allocated to the Room resource is freed.
//         */
//        if (room != null && room.getState() != Room.State.DISCONNECTED) {
//            room.disconnect();
//            disconnectedFromOnDestroy = true;
//        }
//
//        /*
//         * Release the local audio and video tracks ensuring any memory allocated to audio
//         * or video is freed.
//         */
//        if (localAudioTrack != null) {
//            localAudioTrack.release();
//            localAudioTrack = null;
//        }
//        if (localVideoTrack != null) {
//            localVideoTrack.release();
//            localVideoTrack = null;
//        }

        mRtcEngine.leaveChannel();
        RtcEngine.destroy();
        super.onDestroy();
    }

    private boolean checkPermissionForCameraAndMicrophone() {
        int resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return resultCamera == PackageManager.PERMISSION_GRANTED &&
                resultMic == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this,
                    R.string.permissions_needed,
                    Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    CAMERA_MIC_PERMISSION_REQUEST_CODE);
        }
    }

    private void initEngine() {
        initializeEngine();
        setUpLocalView();

    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEngineEventHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpLocalView() {
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x360, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15, VideoEncoderConfiguration.STANDARD_BITRATE, VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE));

        thumbnailVideoView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalContainer.addView(thumbnailVideoView);
        thumbnailVideoView.setZOrderMediaOverlay(true);
        VideoCanvas localVideoCanvas = new VideoCanvas(thumbnailVideoView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(localVideoCanvas);
        mRtcEngine.startPreview();
    }

    private void joinChannel(String channelName) {
        //set the token to null for testing purpose
        String token = null;
        mRtcEngine.joinChannel(token, channelName, "", 0);
    }

    private void setUpRemoteView(int uid) {
        primaryVideoView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteContainer.addView(primaryVideoView);
        VideoCanvas remoteVideoCanvas = new VideoCanvas(primaryVideoView, VideoCanvas.RENDER_MODE_HIDDEN, uid);
        mRtcEngine.setupRemoteVideo(remoteVideoCanvas);
    }

    private void removeRemoteView() {
        if (primaryVideoView != null) {
            mRemoteContainer.removeView(primaryVideoView);
        }
        primaryVideoView = null;
    }

//    private void createAudioAndVideoTracks() {
//        // Share your microphone
//        localAudioTrack = LocalAudioTrack.create(this, true, LOCAL_AUDIO_TRACK_NAME);
//
//        // Share your camera
//        cameraCapturerCompat = new CameraCapturerCompat(this, getAvailableCameraSource());
//        localVideoTrack = LocalVideoTrack.create(this,
//                true,
//                cameraCapturerCompat.getVideoCapturer(),
//                LOCAL_VIDEO_TRACK_NAME);
//        primaryVideoView.setMirror(true);
//        localVideoTrack.addRenderer(primaryVideoView);
//        localVideoView = primaryVideoView;
//    }

//    private CameraSource getAvailableCameraSource() {
//        return (CameraCapturer.isSourceAvailable(CameraSource.FRONT_CAMERA)) ?
//                (CameraSource.FRONT_CAMERA) :
//                (CameraSource.BACK_CAMERA);
//    }

//    private void setAccessToken() {
//        if (!BuildConfig.USE_TOKEN_SERVER) {
//            /*
//             * OPTION 1 - Generate an access token from the getting started portal
//             * https://www.twilio.com/console/video/dev-tools/testing-tools and add
//             * the variable TWILIO_ACCESS_TOKEN setting it equal to the access token
//             * string in your local.properties file.
//             */
//            this.accessToken = TWILIO_ACCESS_TOKEN;
//        } else {
//            /*
//             * OPTION 2 - Retrieve an access token from your own web app.
//             * Add the variable ACCESS_TOKEN_SERVER assigning it to the url of your
//             * token server and the variable USE_TOKEN_SERVER=true to your
//             * local.properties file.
//             */
//            retrieveAccessTokenfromServer();
//        }
//    }

//    private void connectToRoom(String roomName) {
//        audioSwitch.activate();
//        ConnectOptions.Builder connectOptionsBuilder = new ConnectOptions.Builder(accessToken)
//                .roomName(roomName);
//
//        /*
//         * Add local audio track to connect options to share with participants.
//         */
//        if (localAudioTrack != null) {
//            connectOptionsBuilder
//                    .audioTracks(Collections.singletonList(localAudioTrack));
//        }
//
//        /*
//         * Add local video track to connect options to share with participants.
//         */
//        if (localVideoTrack != null) {
//            connectOptionsBuilder.videoTracks(Collections.singletonList(localVideoTrack));
//        }
//
//        /*
//         * Set the preferred audio and video codec for media.
//         */
//        connectOptionsBuilder.preferAudioCodecs(Collections.singletonList(audioCodec));
//        connectOptionsBuilder.preferVideoCodecs(Collections.singletonList(videoCodec));
//
//        /*
//         * Set the sender side encoding parameters.
//         */
//        connectOptionsBuilder.encodingParameters(encodingParameters);
//
//        /*
//         * Toggles automatic track subscription. If set to false, the LocalParticipant will receive
//         * notifications of track publish events, but will not automatically subscribe to them. If
//         * set to true, the LocalParticipant will automatically subscribe to tracks as they are
//         * published. If unset, the default is true. Note: This feature is only available for Group
//         * Rooms. Toggling the flag in a P2P room does not modify subscription behavior.
//         */
//        connectOptionsBuilder.enableAutomaticSubscription(enableAutomaticSubscription);
//
//        room = Video.connect(this, connectOptionsBuilder.build(), roomListener());
//        setDisconnectAction();
//    }

    /*
     * The initial state when there is no active room.
     */
    private void intializeUI() {
        connectActionFab.setImageDrawable(ContextCompat.getDrawable(this,
                R.drawable.ic_video_call_white_24dp));
        connectActionFab.show();
        connectActionFab.setOnClickListener(connectActionClickListener());
        switchCameraActionFab.show();
        switchCameraActionFab.setOnClickListener(switchCameraClickListener());
        localVideoActionFab.show();
        localVideoActionFab.setOnClickListener(localVideoClickListener());
        muteActionFab.show();
        muteActionFab.setOnClickListener(muteClickListener());
    }

    /*
     * Show the current available audio devices.
     */
//    private void showAudioDevices() {
//        AudioDevice selectedDevice = audioSwitch.getSelectedAudioDevice();
//        List<AudioDevice> availableAudioDevices = audioSwitch.getAvailableAudioDevices();
//
//        if (selectedDevice != null) {
//            int selectedDeviceIndex = availableAudioDevices.indexOf(selectedDevice);
//
//            ArrayList<String> audioDeviceNames = new ArrayList<>();
//            for (AudioDevice a : availableAudioDevices) {
//                audioDeviceNames.add(a.getName());
//            }
//
//            new AlertDialog.Builder(this)
//                    .setTitle(R.string.room_screen_select_device)
//                    .setSingleChoiceItems(
//                            audioDeviceNames.toArray(new CharSequence[0]),
//                            selectedDeviceIndex,
//                            (dialog, index) -> {
//                                dialog.dismiss();
//                                AudioDevice selectedAudioDevice = availableAudioDevices.get(index);
//                                updateAudioDeviceIcon(selectedAudioDevice);
//                                audioSwitch.selectDevice(selectedAudioDevice);
//                            }).create().show();
//        }
//    }

    /*
     * Update the menu icon based on the currently selected audio device.
     */
//    private void updateAudioDeviceIcon(AudioDevice selectedAudioDevice) {
//        int audioDeviceMenuIcon = R.drawable.ic_phonelink_ring_white_24dp;
//
//        if (selectedAudioDevice instanceof BluetoothHeadset) {
//            audioDeviceMenuIcon = R.drawable.ic_bluetooth_white_24dp;
//        } else if (selectedAudioDevice instanceof WiredHeadset) {
//            audioDeviceMenuIcon = R.drawable.ic_headset_mic_white_24dp;
//        } else if (selectedAudioDevice instanceof Earpiece) {
//            audioDeviceMenuIcon = R.drawable.ic_phonelink_ring_white_24dp;
//        } else if (selectedAudioDevice instanceof Speakerphone) {
//            audioDeviceMenuIcon = R.drawable.ic_volume_up_white_24dp;
//        }
//
//        audioDeviceMenuItem.setIcon(audioDeviceMenuIcon);
//    }

    /*
     * Get the preferred audio codec from shared preferences
     */
//    private AudioCodec getAudioCodecPreference(String key, String defaultValue) {
//        final String audioCodecName = preferences.getString(key, defaultValue);
//
//        switch (audioCodecName) {
//            case IsacCodec.NAME:
//                return new IsacCodec();
//            case OpusCodec.NAME:
//                return new OpusCodec();
//            case PcmaCodec.NAME:
//                return new PcmaCodec();
//            case PcmuCodec.NAME:
//                return new PcmuCodec();
//            case G722Codec.NAME:
//                return new G722Codec();
//            default:
//                return new OpusCodec();
//        }
//    }

    /*
     * Get the preferred video codec from shared preferences
     */
//    private VideoCodec getVideoCodecPreference(String key, String defaultValue) {
//        final String videoCodecName = preferences.getString(key, defaultValue);
//
//        switch (videoCodecName) {
//            case Vp8Codec.NAME:
//                boolean simulcast = preferences.getBoolean(SettingsActivity.PREF_VP8_SIMULCAST,
//                        SettingsActivity.PREF_VP8_SIMULCAST_DEFAULT);
//                return new Vp8Codec(simulcast);
//            case H264Codec.NAME:
//                return new H264Codec();
//            case Vp9Codec.NAME:
//                return new Vp9Codec();
//            default:
//                return new Vp8Codec();
//        }
//    }

//    private boolean getAutomaticSubscriptionPreference(String key, boolean defaultValue) {
//        return preferences.getBoolean(key, defaultValue);
//    }

//    private EncodingParameters getEncodingParameters() {
//        final int maxAudioBitrate = Integer.parseInt(
//                preferences.getString(SettingsActivity.PREF_SENDER_MAX_AUDIO_BITRATE,
//                        SettingsActivity.PREF_SENDER_MAX_AUDIO_BITRATE_DEFAULT));
//        final int maxVideoBitrate = Integer.parseInt(
//                preferences.getString(SettingsActivity.PREF_SENDER_MAX_VIDEO_BITRATE,
//                        SettingsActivity.PREF_SENDER_MAX_VIDEO_BITRATE_DEFAULT));
//
//        return new EncodingParameters(maxAudioBitrate, maxVideoBitrate);
//    }

    /*
     * The actions performed during disconnect.
     */
    private void setDisconnectAction() {
        connectActionFab.setImageDrawable(ContextCompat.getDrawable(this,
                R.drawable.ic_call_end_white_24px));
        connectActionFab.show();
        connectActionFab.setOnClickListener(disconnectClickListener());
    }

    /*
     * Creates an connect UI dialog
     */
    private void showConnectDialog() {
        EditText roomEditText = new EditText(this);
        connectDialog = Dialog.createConnectDialog(roomEditText,
                connectClickListener(roomEditText),
                cancelConnectDialogClickListener(),
                this);
        connectDialog.show();
    }

    /*
     * Called when remote participant joins the room
     */
    @SuppressLint("SetTextI18n")
//    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {
//        /*
//         * This app only displays video for one additional participant per Room
//         */
//        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
//            Snackbar.make(connectActionFab,
//                    "Multiple participants are not currently support in this UI",
//                    Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
//            return;
//        }
//        remoteParticipantIdentity = remoteParticipant.getIdentity();
//
//        /*
//         * Add remote participant renderer
//         */
//        if (remoteParticipant.getRemoteVideoTracks().size() > 0) {
//            RemoteVideoTrackPublication remoteVideoTrackPublication =
//                    remoteParticipant.getRemoteVideoTracks().get(0);
//
//            /*
//             * Only render video tracks that are subscribed to
//             */
//            if (remoteVideoTrackPublication.isTrackSubscribed()) {
//                addRemoteParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
//            }
//        }
//
//        /*
//         * Start listening for participant events
//         */
//        remoteParticipant.setListener(remoteParticipantListener());
//    }

//    /*
//     * Set primary view as renderer for participant video track
//     */
//    private void addRemoteParticipantVideo(VideoTrack videoTrack) {
//        moveLocalVideoToThumbnailView();
//        primaryVideoView.setMirror(false);
//        videoTrack.addRenderer(primaryVideoView);
//    }

//    private void moveLocalVideoToThumbnailView() {
//        if (thumbnailVideoView.getVisibility() == View.GONE) {
//            thumbnailVideoView.setVisibility(View.VISIBLE);
//            localVideoTrack.removeRenderer(primaryVideoView);
//            localVideoTrack.addRenderer(thumbnailVideoView);
//            localVideoView = thumbnailVideoView;
//            thumbnailVideoView.setMirror(cameraCapturerCompat.getCameraSource() ==
//                    CameraSource.FRONT_CAMERA);
//        }
//    }

    /*
     * Called when remote participant leaves the room
     */
//    @SuppressLint("SetTextI18n")
//    private void removeRemoteParticipant(RemoteParticipant remoteParticipant) {
//        if (!remoteParticipant.getIdentity().equals(remoteParticipantIdentity)) {
//            return;
//        }
//
//        /*
//         * Remove remote participant renderer
//         */
//        if (!remoteParticipant.getRemoteVideoTracks().isEmpty()) {
//            RemoteVideoTrackPublication remoteVideoTrackPublication =
//                    remoteParticipant.getRemoteVideoTracks().get(0);
//
//            /*
//             * Remove video only if subscribed to participant track
//             */
//            if (remoteVideoTrackPublication.isTrackSubscribed()) {
//                removeParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
//            }
//        }
//        moveLocalVideoToPrimaryView();
//    }

//    private void removeParticipantVideo(VideoTrack videoTrack) {
//        videoTrack.removeRenderer(primaryVideoView);
//    }

//    private void moveLocalVideoToPrimaryView() {
//        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
//            thumbnailVideoView.setVisibility(View.GONE);
//            if (localVideoTrack != null) {
//                localVideoTrack.removeRenderer(thumbnailVideoView);
//                localVideoTrack.addRenderer(primaryVideoView);
//            }
//            localVideoView = primaryVideoView;
//            primaryVideoView.setMirror(cameraCapturerCompat.getCameraSource() ==
//                    CameraSource.FRONT_CAMERA);
//        }
//    }

    /*
     * Room events listener
     */
//    @SuppressLint("SetTextI18n")
//    private Room.Listener roomListener() {
//        return new Room.Listener() {
//            @Override
//            public void onConnected(Room room) {
//                localParticipant = room.getLocalParticipant();
//                setTitle(room.getName());
//
//                for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
//                    addRemoteParticipant(remoteParticipant);
//                    break;
//                }
//            }
//
//            @Override
//            public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {
//                reconnectingProgressBar.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onReconnected(@NonNull Room room) {
//                reconnectingProgressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onConnectFailure(Room room, TwilioException e) {
//                audioSwitch.deactivate();
//                intializeUI();
//            }
//
//            @Override
//            public void onDisconnected(Room room, TwilioException e) {
//                localParticipant = null;
//                reconnectingProgressBar.setVisibility(View.GONE);
//                VideoActivity.this.room = null;
//                // Only reinitialize the UI if disconnect was not called from onDestroy()
//                if (!disconnectedFromOnDestroy) {
//                    audioSwitch.deactivate();
//                    intializeUI();
//                    moveLocalVideoToPrimaryView();
//                }
//            }
//
//            @Override
//            public void onParticipantConnected(Room room, RemoteParticipant remoteParticipant) {
//                addRemoteParticipant(remoteParticipant);
//
//            }
//
//            @Override
//            public void onParticipantDisconnected(Room room, RemoteParticipant remoteParticipant) {
//                removeRemoteParticipant(remoteParticipant);
//            }
//
//            @Override
//            public void onRecordingStarted(Room room) {
//                /*
//                 * Indicates when media shared to a Room is being recorded. Note that
//                 * recording is only available in our Group Rooms developer preview.
//                 */
//                Log.d(TAG, "onRecordingStarted");
//            }
//
//            @Override
//            public void onRecordingStopped(Room room) {
//                /*
//                 * Indicates when media shared to a Room is no longer being recorded. Note that
//                 * recording is only available in our Group Rooms developer preview.
//                 */
//                Log.d(TAG, "onRecordingStopped");
//            }
//        };
//    }

//    @SuppressLint("SetTextI18n")
//    private RemoteParticipant.Listener remoteParticipantListener() {
//        return new RemoteParticipant.Listener() {
//            @Override
//            public void onAudioTrackPublished(RemoteParticipant remoteParticipant,
//                                              RemoteAudioTrackPublication remoteAudioTrackPublication) {
//                Log.i(TAG, String.format("onAudioTrackPublished: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
//                                "subscribed=%b, name=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteAudioTrackPublication.getTrackSid(),
//                        remoteAudioTrackPublication.isTrackEnabled(),
//                        remoteAudioTrackPublication.isTrackSubscribed(),
//                        remoteAudioTrackPublication.getTrackName()));
//            }
//
//            @Override
//            public void onAudioTrackUnpublished(RemoteParticipant remoteParticipant,
//                                                RemoteAudioTrackPublication remoteAudioTrackPublication) {
//                Log.i(TAG, String.format("onAudioTrackUnpublished: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
//                                "subscribed=%b, name=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteAudioTrackPublication.getTrackSid(),
//                        remoteAudioTrackPublication.isTrackEnabled(),
//                        remoteAudioTrackPublication.isTrackSubscribed(),
//                        remoteAudioTrackPublication.getTrackName()));
//            }
//
//            @Override
//            public void onDataTrackPublished(RemoteParticipant remoteParticipant,
//                                             RemoteDataTrackPublication remoteDataTrackPublication) {
//                Log.i(TAG, String.format("onDataTrackPublished: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
//                                "subscribed=%b, name=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteDataTrackPublication.getTrackSid(),
//                        remoteDataTrackPublication.isTrackEnabled(),
//                        remoteDataTrackPublication.isTrackSubscribed(),
//                        remoteDataTrackPublication.getTrackName()));
//            }
//
//            @Override
//            public void onDataTrackUnpublished(RemoteParticipant remoteParticipant,
//                                               RemoteDataTrackPublication remoteDataTrackPublication) {
//                Log.i(TAG, String.format("onDataTrackUnpublished: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
//                                "subscribed=%b, name=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteDataTrackPublication.getTrackSid(),
//                        remoteDataTrackPublication.isTrackEnabled(),
//                        remoteDataTrackPublication.isTrackSubscribed(),
//                        remoteDataTrackPublication.getTrackName()));
//            }
//
//            @Override
//            public void onVideoTrackPublished(RemoteParticipant remoteParticipant,
//                                              RemoteVideoTrackPublication remoteVideoTrackPublication) {
//                Log.i(TAG, String.format("onVideoTrackPublished: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
//                                "subscribed=%b, name=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteVideoTrackPublication.getTrackSid(),
//                        remoteVideoTrackPublication.isTrackEnabled(),
//                        remoteVideoTrackPublication.isTrackSubscribed(),
//                        remoteVideoTrackPublication.getTrackName()));
//            }
//
//            @Override
//            public void onVideoTrackUnpublished(RemoteParticipant remoteParticipant,
//                                                RemoteVideoTrackPublication remoteVideoTrackPublication) {
//                Log.i(TAG, String.format("onVideoTrackUnpublished: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
//                                "subscribed=%b, name=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteVideoTrackPublication.getTrackSid(),
//                        remoteVideoTrackPublication.isTrackEnabled(),
//                        remoteVideoTrackPublication.isTrackSubscribed(),
//                        remoteVideoTrackPublication.getTrackName()));
//            }
//
//            @Override
//            public void onAudioTrackSubscribed(RemoteParticipant remoteParticipant,
//                                               RemoteAudioTrackPublication remoteAudioTrackPublication,
//                                               RemoteAudioTrack remoteAudioTrack) {
//                Log.i(TAG, String.format("onAudioTrackSubscribed: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteAudioTrack.isEnabled(),
//                        remoteAudioTrack.isPlaybackEnabled(),
//                        remoteAudioTrack.getName()));
//            }
//
//            @Override
//            public void onAudioTrackUnsubscribed(RemoteParticipant remoteParticipant,
//                                                 RemoteAudioTrackPublication remoteAudioTrackPublication,
//                                                 RemoteAudioTrack remoteAudioTrack) {
//                Log.i(TAG, String.format("onAudioTrackUnsubscribed: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteAudioTrack.isEnabled(),
//                        remoteAudioTrack.isPlaybackEnabled(),
//                        remoteAudioTrack.getName()));
//            }
//
//            @Override
//            public void onAudioTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
//                                                       RemoteAudioTrackPublication remoteAudioTrackPublication,
//                                                       TwilioException twilioException) {
//                Log.i(TAG, String.format("onAudioTrackSubscriptionFailed: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteAudioTrackPublication: sid=%b, name=%s]" +
//                                "[TwilioException: code=%d, message=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteAudioTrackPublication.getTrackSid(),
//                        remoteAudioTrackPublication.getTrackName(),
//                        twilioException.getCode(),
//                        twilioException.getMessage()));
//            }
//
//            @Override
//            public void onDataTrackSubscribed(RemoteParticipant remoteParticipant,
//                                              RemoteDataTrackPublication remoteDataTrackPublication,
//                                              RemoteDataTrack remoteDataTrack) {
//                Log.i(TAG, String.format("onDataTrackSubscribed: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteDataTrack: enabled=%b, name=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteDataTrack.isEnabled(),
//                        remoteDataTrack.getName()));
//            }
//
//            @Override
//            public void onDataTrackUnsubscribed(RemoteParticipant remoteParticipant,
//                                                RemoteDataTrackPublication remoteDataTrackPublication,
//                                                RemoteDataTrack remoteDataTrack) {
//                Log.i(TAG, String.format("onDataTrackUnsubscribed: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteDataTrack: enabled=%b, name=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteDataTrack.isEnabled(),
//                        remoteDataTrack.getName()));
//            }
//
//            @Override
//            public void onDataTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
//                                                      RemoteDataTrackPublication remoteDataTrackPublication,
//                                                      TwilioException twilioException) {
//                Log.i(TAG, String.format("onDataTrackSubscriptionFailed: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteDataTrackPublication: sid=%b, name=%s]" +
//                                "[TwilioException: code=%d, message=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteDataTrackPublication.getTrackSid(),
//                        remoteDataTrackPublication.getTrackName(),
//                        twilioException.getCode(),
//                        twilioException.getMessage()));
//            }
//
//            @Override
//            public void onVideoTrackSubscribed(RemoteParticipant remoteParticipant,
//                                               RemoteVideoTrackPublication remoteVideoTrackPublication,
//                                               RemoteVideoTrack remoteVideoTrack) {
//                Log.i(TAG, String.format("onVideoTrackSubscribed: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteVideoTrack: enabled=%b, name=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteVideoTrack.isEnabled(),
//                        remoteVideoTrack.getName()));
//                addRemoteParticipantVideo(remoteVideoTrack);
//            }
//
//            @Override
//            public void onVideoTrackUnsubscribed(RemoteParticipant remoteParticipant,
//                                                 RemoteVideoTrackPublication remoteVideoTrackPublication,
//                                                 RemoteVideoTrack remoteVideoTrack) {
//                Log.i(TAG, String.format("onVideoTrackUnsubscribed: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteVideoTrack: enabled=%b, name=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteVideoTrack.isEnabled(),
//                        remoteVideoTrack.getName()));
//                removeParticipantVideo(remoteVideoTrack);
//            }
//
//            @Override
//            public void onVideoTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
//                                                       RemoteVideoTrackPublication remoteVideoTrackPublication,
//                                                       TwilioException twilioException) {
//                Log.i(TAG, String.format("onVideoTrackSubscriptionFailed: " +
//                                "[RemoteParticipant: identity=%s], " +
//                                "[RemoteVideoTrackPublication: sid=%b, name=%s]" +
//                                "[TwilioException: code=%d, message=%s]",
//                        remoteParticipant.getIdentity(),
//                        remoteVideoTrackPublication.getTrackSid(),
//                        remoteVideoTrackPublication.getTrackName(),
//                        twilioException.getCode(),
//                        twilioException.getMessage()));
//                Snackbar.make(connectActionFab,
//                        String.format("Failed to subscribe to %s video track",
//                                remoteParticipant.getIdentity()),
//                        Snackbar.LENGTH_LONG)
//                        .show();
//            }
//
//            @Override
//            public void onAudioTrackEnabled(RemoteParticipant remoteParticipant,
//                                            RemoteAudioTrackPublication remoteAudioTrackPublication) {
//
//            }
//
//            @Override
//            public void onAudioTrackDisabled(RemoteParticipant remoteParticipant,
//                                             RemoteAudioTrackPublication remoteAudioTrackPublication) {
//
//            }
//
//            @Override
//            public void onVideoTrackEnabled(RemoteParticipant remoteParticipant,
//                                            RemoteVideoTrackPublication remoteVideoTrackPublication) {
//
//            }
//
//            @Override
//            public void onVideoTrackDisabled(RemoteParticipant remoteParticipant,
//                                             RemoteVideoTrackPublication remoteVideoTrackPublication) {
//
//            }
//        };
//    }

    private DialogInterface.OnClickListener connectClickListener(final EditText roomEditText) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*
                 * Connect to room
                 */
//                VideoActivity.this.connectToRoom(roomEditText.getText().toString());
                VideoActivity.this.joinChannel(roomEditText.getText().toString());
                setDisconnectAction();
            }
        };
    }

    private View.OnClickListener disconnectClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Disconnect from room
                 */
//                if (room != null) {
//                    room.disconnect();
//                }
                mRtcEngine.leaveChannel();
                removeRemoteView();
                VideoActivity.this.intializeUI();
            }
        };
    }

    private View.OnClickListener connectActionClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoActivity.this.showConnectDialog();
            }
        };
    }

    private DialogInterface.OnClickListener cancelConnectDialogClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VideoActivity.this.intializeUI();
                connectDialog.dismiss();
            }
        };
    }

    private View.OnClickListener switchCameraClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (cameraCapturerCompat != null) {
//                    CameraSource cameraSource = cameraCapturerCompat.getCameraSource();
//                    cameraCapturerCompat.switchCamera();
//                    if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
//                        thumbnailVideoView.setMirror(cameraSource == CameraSource.BACK_CAMERA);
//                    } else {
//                        primaryVideoView.setMirror(cameraSource == CameraSource.BACK_CAMERA);
//                    }
//                }
                mRtcEngine.switchCamera();
            }
        };
    }

    private View.OnClickListener localVideoClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Enable/disable the local video track
                 */
//                if (localVideoTrack != null) {
//                    boolean enable = !localVideoTrack.isEnabled();
//                    localVideoTrack.enable(enable);
//                }

                int icon;
                mRtcEngine.muteLocalVideoStream(videoEnabled);
                videoEnabled = !videoEnabled;
                if (videoEnabled) {
                    icon = R.drawable.ic_videocam_white_24dp;
                    switchCameraActionFab.show();
                } else {
                    icon = R.drawable.ic_videocam_off_black_24dp;
                    switchCameraActionFab.hide();
                }
                localVideoActionFab.setImageDrawable(
                            ContextCompat.getDrawable(VideoActivity.this, icon));
            }
        };
    }

    private View.OnClickListener muteClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Enable/disable the local audio track. The results of this operation are
                 * signaled to other Participants in the same Room. When an audio track is
                 * disabled, the audio is muted.
                 */
//                if (localAudioTrack != null) {
//                    boolean enable = !localAudioTrack.isEnabled();
//                    localAudioTrack.enable(enable);
//                }

                mRtcEngine.muteLocalAudioStream(audioEnabled);
                audioEnabled = !audioEnabled;
                int icon = audioEnabled ?
                        R.drawable.ic_mic_white_24dp : R.drawable.ic_mic_off_black_24dp;
                muteActionFab.setImageDrawable(ContextCompat.getDrawable(
                        VideoActivity.this, icon));
            }
        };
    }

//    private void retrieveAccessTokenfromServer() {
//        Ion.with(this)
//                .load(String.format("%s?identity=%s", ACCESS_TOKEN_SERVER,
//                        UUID.randomUUID().toString()))
//                .asString()
//                .setCallback((e, token) -> {
//                    if (e == null) {
//                        VideoActivity.this.accessToken = token;
//                    } else {
//                        Toast.makeText(VideoActivity.this,
//                                R.string.error_retrieving_access_token, Toast.LENGTH_LONG)
//                                .show();
//                    }
//                });
//    }

    private IRtcEngineEventHandler mRtcEngineEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onJoinChannelSuccess(final String s, int i, int i1) {
            super.onJoinChannelSuccess(s, i, i1);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VideoActivity.this, "Join channel success: " + s, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onRemoteVideoStateChanged(final int uid, int state, int reason, int elapsed) {
            super.onRemoteVideoStateChanged(uid, state, reason, elapsed);
            if (state == Constants.REMOTE_VIDEO_STATE_DECODING) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setUpRemoteView(uid);
                    }
                });
            }
            if (state == Constants.REMOTE_VIDEO_STATE_STOPPED) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        removeRemoteView();
                    }
                });
            }
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    removeRemoteView();
                }
            });
        }
    };
}
