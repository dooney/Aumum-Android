package com.aumum.app.mobile.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;

import com.aumum.app.mobile.R;
import com.aumum.app.mobile.utils.Ln;
import com.aumum.app.mobile.utils.SafeAsyncTask;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.VoiceMessageBody;
import com.github.kevinsawicki.wishlist.Toaster;

import java.io.File;

import retrofit.RetrofitError;

/**
 * Created by Administrator on 6/12/2014.
 */

public class VoicePlayClickListener {

    private Activity activity;
    private EMMessage message;
    private ImageView voiceImage;
    private AnimationDrawable voiceAnimation;
    private MediaPlayer mediaPlayer;
    private ImageView sentFailedImage;

    private static VoicePlayClickListener listener;
    private VoicePlayClickListener(Activity activity) {
        this.activity = activity;
    }

    public static VoicePlayClickListener getInstance(Activity activity) {
        if (listener == null) {
            listener = new VoicePlayClickListener(activity);
        }
        return listener;
    }

    public void stopPlayVoice() {
        if (voiceAnimation != null) {
            voiceAnimation.stop();
        }
        if (voiceImage != null && message.direct == EMMessage.Direct.RECEIVE) {
            voiceImage.setImageResource(R.drawable.chat_received_voice_playing);
        } else {
            voiceImage.setImageResource(R.drawable.chat_sent_voice_playing);
        }
        // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playVoice(String filePath) {
        if (!(new File(filePath).exists())) {
            return;
        }
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

        mediaPlayer = new MediaPlayer();
        if (EMChatManager.getInstance().getChatOptions().getUseSpeaker()) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } else {
            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    stopPlayVoice(); // stop animation
                }

            });
            mediaPlayer.start();
            showAnimation();

            // 如果是接收的消息
            if (message.direct == EMMessage.Direct.RECEIVE) {
                try {
                    if (!message.isAcked) {
                        message.isAcked = true;
                        // 告知对方已读这条消息
                        if (message.getChatType() != EMMessage.ChatType.GroupChat) {
                            EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                        }
                    }
                } catch (Exception e) {
                    message.isAcked = false;
                }
                if (!message.isListened() && sentFailedImage != null && sentFailedImage.getVisibility() == View.VISIBLE) {
                    // 隐藏自己未播放这条语音消息的标志
                    sentFailedImage.setVisibility(View.INVISIBLE);
                    EMChatManager.getInstance().setMessageListened(message);
                }
            }
        } catch (Exception e) {
            Ln.e(e);
        }
    }

    // show the voice playing animation
    private void showAnimation() {
        // play voice, and start animation
        if (message.direct == EMMessage.Direct.RECEIVE) {
            voiceImage.setImageResource(R.anim.voice_received);
        } else {
            voiceImage.setImageResource(R.anim.voice_sent);
        }
        voiceAnimation = (AnimationDrawable) voiceImage.getDrawable();
        voiceAnimation.start();
    }

    public void onPlay(EMMessage emMessage, View view) {
        if (mediaPlayer != null) {
            stopPlayVoice();
            if (message == emMessage) {
                return;
            }
        }
        this.message = emMessage;
        this.voiceImage = (ImageView) view.findViewById(R.id.image_voice);
        this.sentFailedImage = (ImageView) view.findViewById(R.id.image_sent_failed);

        VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
        if (message.direct == EMMessage.Direct.SEND) {
            // for sent msg, we will try to play the voice file directly
            playVoice(voiceBody.getLocalUrl());
        } else {
            if (message.status == EMMessage.Status.SUCCESS) {
                File file = new File(voiceBody.getLocalUrl());
                if (file.exists() && file.isFile()) {
                    playVoice(voiceBody.getLocalUrl());
                } else {
                    Ln.e("file not exist");
                }
            } else if (message.status == EMMessage.Status.INPROGRESS) {
                Toaster.showShort(activity, R.string.info_downloading_voice);
            } else if (message.status == EMMessage.Status.FAIL) {
                Toaster.showShort(activity, R.string.info_downloading_voice);
                SafeAsyncTask<Boolean> task = new SafeAsyncTask<Boolean>() {
                    public Boolean call() throws Exception {
                        EMChatManager.getInstance().asyncFetchMessage(message);
                        return true;
                    }

                    @Override
                    protected void onException(final Exception e) throws RuntimeException {
                        if(!(e instanceof RetrofitError)) {
                            final Throwable cause = e.getCause() != null ? e.getCause() : e;
                            if(cause != null) {
                                Toaster.showShort(activity, cause.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onSuccess(final Boolean success) {
                    }
                };
                task.execute();
            }
        }
    }
}
