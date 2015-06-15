package com.keyboard;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.keyboard.bean.EmoticonBean;
import com.keyboard.utils.EmoticonsKeyboardBuilder;
import com.keyboard.utils.Utils;
import com.keyboard.view.AutoHeightLayout;
import com.keyboard.view.EmoticonsEditText;
import com.keyboard.view.EmoticonsIndicatorView;
import com.keyboard.view.EmoticonsPageView;
import com.keyboard.view.EmoticonsToolBarView;
import com.keyboard.view.I.IEmoticonsKeyboard;
import com.keyboard.view.I.IView;
import com.keyboard.view.R;

public class XhsEmoticonsSendBoxBar extends AutoHeightLayout
        implements IEmoticonsKeyboard, View.OnClickListener {

    public static int FUNC_CHILDVIEW_EMOTICON = 0;
    public int mChildViewPosition = -1;

    private EmoticonsPageView mEmoticonsPageView;
    private EmoticonsIndicatorView mEmoticonsIndicatorView;
    private EmoticonsToolBarView mEmoticonsToolBarView;

    private EmoticonsEditText et_content;
    private RelativeLayout rl_input;
    private LinearLayout ly_foot_func;
    private ImageView btn_face;
    private Button btn_send;

    public XhsEmoticonsSendBoxBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_sendboxbar, this);
        initView();
    }

    private void initView() {
        mEmoticonsPageView = (EmoticonsPageView) findViewById(R.id.view_epv);
        mEmoticonsIndicatorView = (EmoticonsIndicatorView) findViewById(R.id.view_eiv);
        mEmoticonsToolBarView = (EmoticonsToolBarView) findViewById(R.id.view_etv);

        rl_input = (RelativeLayout) findViewById(R.id.rl_input);
        ly_foot_func = (LinearLayout) findViewById(R.id.ly_foot_func);
        btn_face = (ImageView) findViewById(R.id.btn_face);
        btn_send = (Button) findViewById(R.id.btn_send);
        et_content = (EmoticonsEditText) findViewById(R.id.et_chat);

        setAutoHeightLayoutView(ly_foot_func);
        btn_face.setOnClickListener(this);
        btn_send.setOnClickListener(this);

        mEmoticonsPageView.setOnIndicatorListener(new EmoticonsPageView.OnEmoticonsPageViewListener() {
            @Override
            public void emoticonsPageViewInitFinish(int count) {
                mEmoticonsIndicatorView.init(count);
            }

            @Override
            public void emoticonsPageViewCountChanged(int count) {
                mEmoticonsIndicatorView.setIndicatorCount(count);
            }

            @Override
            public void playTo(int position) {
                mEmoticonsIndicatorView.playTo(position);
            }

            @Override
            public void playBy(int oldPosition, int newPosition) {
                mEmoticonsIndicatorView.playBy(oldPosition, newPosition);
            }
        });

        mEmoticonsPageView.setIViewListener(new IView() {
            @Override
            public void onItemClick(EmoticonBean bean) {
                if (et_content != null) {
                    et_content.setFocusable(true);
                    et_content.setFocusableInTouchMode(true);
                    et_content.requestFocus();

                    // 删除
                    if (bean.getEventType() == EmoticonBean.FACE_TYPE_DEL) {
                        int action = KeyEvent.ACTION_DOWN;
                        int code = KeyEvent.KEYCODE_DEL;
                        KeyEvent event = new KeyEvent(action, code);
                        et_content.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                        return;
                    }
                    // 用户自定义
                    else if (bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                        return;
                    }

                    int index = et_content.getSelectionStart();
                    Editable editable = et_content.getEditableText();
                    if (index < 0) {
                        editable.append(bean.getContent());
                    } else {
                        editable.insert(index, bean.getContent());
                    }
                }
            }

            @Override
            public void onItemDisplay(EmoticonBean bean) { }

            @Override
            public void onPageChangeTo(int position) {
                mEmoticonsToolBarView.setToolBtnSelect(position);
            }
        });

        mEmoticonsToolBarView.setOnToolBarItemClickListener(new EmoticonsToolBarView.OnToolBarItemClickListener() {
            @Override
            public void onToolBarItemClick(int position) {
                mEmoticonsPageView.setPageSelect(position);
            }
        });

        et_content.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!et_content.isFocused()) {
                    et_content.setFocusable(true);
                    et_content.setFocusableInTouchMode(true);
                }
                return false;
            }
        });
        et_content.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    setEditableState(true);
                } else {
                    setEditableState(false);
                }
            }
        });
        et_content.setOnTextChangedInterface(new EmoticonsEditText.OnTextChangedInterface() {
            @Override
            public void onTextChanged(CharSequence arg0) {
                String str = arg0.toString();
                btn_send.setEnabled(!TextUtils.isEmpty(str));
            }
        });
    }

    private void setEditableState(boolean b) {
        if (b) {
            et_content.setFocusable(true);
            et_content.setFocusableInTouchMode(true);
            et_content.requestFocus();
            rl_input.setBackgroundResource(R.drawable.input_bg_sel);
        } else {
            et_content.setFocusable(false);
            et_content.setFocusableInTouchMode(false);
            rl_input.setBackgroundResource(R.drawable.input_bg_nor);
        }
    }

    @Override
    public void setBuilder(EmoticonsKeyboardBuilder builder) {
        mEmoticonsPageView.setBuilder(builder);
        mEmoticonsToolBarView.setBuilder(builder);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (ly_foot_func != null && ly_foot_func.isShown()) {
                    hideAutoView();
                    btn_face.setImageResource(R.drawable.icon_face_normal);
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_face) {
            switch (mKeyboardState){
                case KEYBOARD_STATE_NONE:
                case KEYBOARD_STATE_BOTH:
                    show(FUNC_CHILDVIEW_EMOTICON);
                    btn_face.setImageResource(R.drawable.icon_face_pop);
                    showAutoView();
                    Utils.closeSoftKeyboard(mContext);
                    break;
                case KEYBOARD_STATE_FUNC:
                    if(mChildViewPosition == FUNC_CHILDVIEW_EMOTICON){
                        btn_face.setImageResource(R.drawable.icon_face_normal);
                        setEditableState(true);
                        Utils.openSoftKeyboard(et_content);
                    }
                    else {
                        show(FUNC_CHILDVIEW_EMOTICON);
                        btn_face.setImageResource(R.drawable.icon_face_pop);
                    }
                    break;
            }
        }
        else if (id == R.id.btn_send) {
            if(mKeyBoardBarViewListener != null){
                mKeyBoardBarViewListener.OnSendBtnClick(et_content.getText().toString());
            }
        }
    }

    public void add(View view){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ly_foot_func.addView(view,params);
    }

    public void show(int position){
        int childCount = ly_foot_func.getChildCount();
        if(position < childCount){
            for(int i = 0 ; i < childCount ; i++){
                if(i == position){
                    ly_foot_func.getChildAt(i).setVisibility(VISIBLE);
                    mChildViewPosition  = i;
                } else{
                    ly_foot_func.getChildAt(i).setVisibility(GONE);
                }
            }
        }
        if(mKeyBoardBarViewListener != null){
            mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState, -1);
        }
    }

    public void setSendText(String hintText) {
        et_content.setText(hintText);
        et_content.setSelection(hintText.length());
        Utils.openSoftKeyboard(et_content);
    }

    public void reset() {
        et_content.setText("");
        hideAutoView();
    }

    public void showInput() {
        setEditableState(true);
        Utils.openSoftKeyboard(et_content);
    }

    @Override
    public void OnSoftPop(final int height) {
        super.OnSoftPop(height);
        btn_face.setImageResource(R.drawable.icon_face_normal);
    }

    @Override
    public void OnSoftChangeHeight(int height) {
        super.OnSoftChangeHeight(height);
        if(mKeyBoardBarViewListener != null){
            mKeyBoardBarViewListener.OnKeyBoardStateChange(mKeyboardState,height);
        }
    }

    KeyBoardBarViewListener mKeyBoardBarViewListener;
    public void setOnKeyBoardBarViewListener(KeyBoardBarViewListener l) { this.mKeyBoardBarViewListener = l; }

    public interface KeyBoardBarViewListener {
        public void OnKeyBoardStateChange(int state, int height);

        public void OnSendBtnClick(String msg);
    }
}
