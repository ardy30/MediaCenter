/**
 * 
 * com.rockchips.iptv.stb.dlna.widget
 * DLNAVideoView.java
 * 
 * 2011-12-17-下午07:01:43
 * Copyright 2011 Huawei Technologies Co., Ltd
 * 
 */
package com.rockchips.mediacenter.portable.hisi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Parcel;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.hisilicon.android.mediaplayer.HiMediaPlayer;
import com.hisilicon.android.mediaplayer.HiMediaPlayerInvoke;
import com.rockchips.mediacenter.portable.IMediaPlayerAdapter;
import com.rockchips.mediacenter.portable.IVideoViewAdapter;
import com.rockchips.mediacenter.portable.bean.AudioInfoOfVideo;
import com.rockchips.mediacenter.portable.bean.FileInfo;
import com.rockchips.mediacenter.portable.bean.SubInfo;
import com.rockchips.mediacenter.portable.listener.OnBufferingUpdateListener;
import com.rockchips.mediacenter.portable.listener.OnCompleteListener;
import com.rockchips.mediacenter.portable.listener.OnErrorListener;
import com.rockchips.mediacenter.portable.listener.OnFastBackwordCompleteListener;
import com.rockchips.mediacenter.portable.listener.OnFastForwardCompleteListener;
import com.rockchips.mediacenter.portable.listener.OnInfoListener;
import com.rockchips.mediacenter.portable.listener.OnPreparedListener;
import com.rockchips.mediacenter.portable.listener.OnSeekCompleteListener;

/**
 * 
 * DLNAHiVideoView
 * 
 * 2013年10月26日 t00181037
 * 
 * @version 1.0.0
 * 
 */
public class HisiVideoViewNoView extends HiVideoViewNoViewBase implements IVideoViewAdapter
{

    private WindowManager mWindowManager;

    private int mDisplayW = 0;

    private int mDisplayH = 0;

    private int mSubtitleNumber;

    private int mExtSubtitleNumber;

    private int mAudioTrackNumber;

    private int mSelectSubtitleId = 0;

    private int mSelectAudioTrackId = 0;

    private List <String> mSubtitleLanguageList;

    private List <String> mExtraSubtitleList;

//    private List <String> mAudioTrackLanguageList;

    private List <String> mAudioFormatList;

    private List <String> mAudioSampleRateList;

    private List <String> mAudioChannelList;

    public String[] mSubFormat = {"ASS", "LRC", "SRT", "SMI", "SUB", "TXT", "PGS", "DVB", "DVD"};

    private HiMediaPlayer  mMediaPlayer = null;

    OnBufferingUpdateListener onBufferingUpdateListener= null;
    OnErrorListener onErrorListener= null;
//    OnInfoListener onInfoListener= null;
//    OnPreparedListener onPreparedListener= null;
//    OnSeekCompleteListener onSeekCompleteListener= null;
    OnCompleteListener onCompleteListener= null;
    
    SurfaceHolder mSH = null;


    /**
     *constructor DLNAVideoView.
     *
     * @param context
     */
    public HisiVideoViewNoView(Context context)
    {
        super(context);
        init(context);
    }
 
    private Context mContext = null;
    private void init(Context context)
    {
        mContext = context;
        if(mAudioManager == null)
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if(mediaplayer == null)
        {
            mediaplayer =  new HisiMediaPlayerAdapter(context);
        }
    }


    /**
     * @param mDisplayH the mDisplayH to set
     */
    public void setDisplayH(int mDisplayH)
    {
        this.mDisplayH = mDisplayH;
    }

    /**
     * mDisplayH
     *
     * @return  the mDisplayH
     * @since   1.0.0
     */

    public int getDisplayH()
    {
        return mDisplayH;
    }

    /**
     * @param mDisplayW the mDisplayW to set
     */
    public void setDisplayW(int mDisplayW)
    {
        this.mDisplayW = mDisplayW;
    }

    /**
     * mDisplayW
     *
     * @return  the mDisplayW
     * @since   1.0.0
     */

    public int getDisplayW()
    {
        return mDisplayW;
    }


    private AudioManager mAudioManager = null;

    public void start()
    {
        super.start();
        if(mAudioManager == null)
        {
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }

         mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
    }

    public void pause()
    {
        super.pause();
        if(mAudioManager != null)
        {
            mAudioManager.abandonAudioFocus(mAudioFocusListener);
        }
    }

    public void stopPlayback()
    {
        mIsPrepared = false;
        mMediaPlayer = null;
        
        super.stopPlayback();
        if(mAudioManager != null)
        {
            mAudioManager.abandonAudioFocus(mAudioFocusListener);
        }
    }

    public void resume() 
    {
        super.resume();
        mAudioManager.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
    }


    /**Mender:l00174030;Reason:from android2.2 **/
    public boolean isSeeking = false;

    private final String TAG = "DLNAVideoView";

    public void isSeeking(boolean b)
    {
        isSeeking = b;
    }

    public boolean isSeeking()
    {
        return isSeeking;
    }

    private OnInfoListener mOnInfoListener;
    private HiMediaPlayer.OnInfoListener mInfoListener = new HiMediaPlayer.OnInfoListener()
    {
        public boolean onInfo(HiMediaPlayer mp, int what, int extra)
        {
            if (mOnInfoListener != null)
            {
                return mOnInfoListener.onInfo(getmediaPlayerAdapter(), what, extra);
            }
            return true;
        }
    };
    
    
    public void setOnInfoListener(OnInfoListener l)
    {
        mOnInfoListener = l;
        super.setOnInfoListener(mInfoListener);
    }

    private OnSeekCompleteListener mOnSeekCompleteListener;
    OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener()
    {

        public void onSeekComplete(IMediaPlayerAdapter mp)
        {
            // TODO Auto-generated method stub
            //          mCurrentState = STATE_PLAYBACK_COMPLETED;
            //          mTargetState = STATE_PLAYBACK_COMPLETED;
            Log.e(TAG, "seekTo  is complete---->" + isSeeking);
            //            if (mMediaController != null)
            //            {
            //                mMediaController.hide();
            //            }
            if (mOnSeekCompleteListener != null)
            {
                mOnSeekCompleteListener.onSeekComplete(mp);
            }
            isSeeking(false);
            Log.e(TAG, "seekTo  is complete  posistion---->" + getCurrentPosition());

        }
    };
    private HiMediaPlayer.OnSeekCompleteListener mseekListener = new HiMediaPlayer.OnSeekCompleteListener()
    {
        public void onSeekComplete(HiMediaPlayer arg0)
        {
            if (mOnSeekCompleteListener != null)
            {
                mOnSeekCompleteListener.onSeekComplete(getmediaPlayerAdapter());
            }
        }
    };
    public void setOnSeekCompleteListener(OnSeekCompleteListener l)
    {
        mOnSeekCompleteListener = l;
    }

    private OnCompleteListener mOnCompleteListener;
    private HiMediaPlayer.OnCompletionListener mcompleteListener = new HiMediaPlayer.OnCompletionListener()
    {
        public void onCompletion(HiMediaPlayer arg0)
        {
            if (mOnCompleteListener != null)
            {
                mOnCompleteListener.onCompletion(getmediaPlayerAdapter());
            }
        }
    };
    public void setOnCompletionListener(OnCompleteListener l)
    {
        mOnCompleteListener = l;
    }

    
    
    /**
     * @return 返回 mSubtitleNumber
     */
    public int getSubtitleNumber()
    {
        return mSubtitleNumber;
    }

    /**
     * @param 对mSubtitleNumber进行赋值
     */
    public void setSubtitleNumber(int mSubtitleNumber)
    {
        this.mSubtitleNumber = mSubtitleNumber;
    }

    /**
     * @return 返回 mExtSubtitleNumber
     */
    public int getExtSubtitleNumber()
    {
        return mExtSubtitleNumber;
    }

    /**
     * @param 对mExtSubtitleNumber进行赋值
     */
    public void setExtSubtitleNumber(int mExtSubtitleNumber)
    {
        this.mExtSubtitleNumber = mExtSubtitleNumber;
    }

    /**
     * @return 返回 mAudioTrackNumber
     */
    public int getAudioTrackNumber()
    {
        return mAudioTrackNumber;
    }

    /**
     * @param 对mAudioTrackNumber进行赋值
     */
    public void setAudioTrackNumber(int mAudioTrackNumber)
    {
        this.mAudioTrackNumber = mAudioTrackNumber;
    }

    /**
     * @return 返回 mSelectSubtitleId
     */
    public int getSelectSubtitleId()
    {
        return mSelectSubtitleId;
    }

    /**
     * @param 对mSelectSubtitleId进行赋值
     */
    public void setSelectSubtitleId(int mSelectSubtitleId)
    {
        this.mSelectSubtitleId = mSelectSubtitleId;
    }

    /**
     * @return 返回 mSelectAudioTrackId
     */
    public int getSelectAudioTrackId()
    {
        return mSelectAudioTrackId;
    }

    /**
     * @param 对mSelectAudioTrackId进行赋值
     */
    public void setSelectAudioTrackId(int mSelectAudioTrackId)
    {
        this.mSelectAudioTrackId = mSelectAudioTrackId;
    }

    /**
     * @return 返回 mSubtitleLanguageList
     */
    public List <String> getSubtitleLanguageList()
    {
        return mSubtitleLanguageList;
    }

    /**
     * @param 对mSubtitleLanguageList进行赋值
     */
    public void setSubtitleLanguageList(List <String> mSubtitleLanguageList)
    {
        this.mSubtitleLanguageList = mSubtitleLanguageList;
    }

    /**
     * @return 返回 mExtraSubtitleList
     */
    public List <String> getExtraSubtitleList()
    {
        return mExtraSubtitleList;
    }

    /**
     * @param 对mExtraSubtitleList进行赋值
     */
    public void setExtraSubtitleList(List <String> mExtraSubtitleList)
    {
        this.mExtraSubtitleList = mExtraSubtitleList;
    }

    /**
     * @return 返回 mAudioTrackLanguageList
     */
    public List <String> getAudioTrackLanguageList()
    {
//        return mAudioTrackLanguageList;
        return null;
    }

    /**
     * @return 返回 mAudioFormatList
     */
    public List <String> getAudioFormatList()
    {
        return mAudioFormatList;
    }

    /**
     * @param 对mAudioFormatList进行赋值
     */
    public void setAudioFormatList(List <String> mAudioFormatList)
    {
        this.mAudioFormatList = mAudioFormatList;
    }

    /**
     * @return 返回 mAudioSampleRateList
     */
    public List <String> getAudioSampleRateList()
    {
        return mAudioSampleRateList;
    }

    /**
     * @param 对mAudioSampleRateList进行赋值
     */
    public void setAudioSampleRateList(List <String> mAudioSampleRateList)
    {
        this.mAudioSampleRateList = mAudioSampleRateList;
    }

    /**
     * @return 返回 mAudioChannelList
     */
    public List <String> getAudioChannelList()
    {
        return mAudioChannelList;
    }

    /**
     * @param 对mAudioChannelList进行赋值
     */
    public void setAudioChannelList(List <String> mAudioChannelList)
    {
        this.mAudioChannelList = mAudioChannelList;
    }

    /**end**/

    private boolean mPausedByTransientLossOfFocus = false;

    private OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
//            if (mAudioManager != null) {
//
//                mAudioManager.abandonAudioFocus(this);
//                return;
//            }    
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    mPausedByTransientLossOfFocus = true;
                    HisiVideoViewNoView.super.pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mPausedByTransientLossOfFocus = true;
                    HisiVideoViewNoView.super.pause();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (mPausedByTransientLossOfFocus) {
                        mPausedByTransientLossOfFocus = false;
                        HisiVideoViewNoView.super.start();
                    }    
                    break;
            }    
            //updatePlayPause();
        }                                                                                                              
    };
//    
//    
//    public List <String> getInternalSubtitleLanguageInfoList()
//    {
//        if ((mMediaPlayer != null) && mIsPrepared)
//        {
//            Parcel _Request = Parcel.obtain();
//            Parcel _Reply = Parcel.obtain();
//
//            _Request.writeInt(103);
//
//            if (mMediaPlayer.invoke(_Request, _Reply) != 0)
//            {
//                _Request.recycle();
//                _Reply.recycle();
//                return null;
//            }
//
//            List <String> _LanguageList = new ArrayList <String>();
//
//            // for get
//            _Reply.readInt();
//            int _Num = _Reply.readInt();
//            String _Language = "";
//            String _SubFormat = "";
//            int _IsExt = 0;
//
//            for (int i = 0; i < _Num; i++)
//            {
//                _Reply.readInt();
//                _IsExt = _Reply.readInt();
//                _Language = _Reply.readString();
//                _SubFormat = mSubFormat[_Reply.readInt()];
//                if (_Language == null  || _Language.equals("-"))
//                {
//                    _Language = "";
//                }
//                if (_IsExt == 0)
//                {
//                    _LanguageList.add(_SubFormat);
//                    _LanguageList.add(_Language);
//                }
//            }
//
//            _Request.recycle();
//            _Reply.recycle();
//
//            return _LanguageList;
//        }
//
//        return new ArrayList <String>();
//    }
//
////获取外部字幕列表
//    public List <String> getExtSubtitleLanguageInfoList()
//    {
//        if ((mMediaPlayer != null) && mIsPrepared)
//        {
//            Parcel _Request = Parcel.obtain();
//            Parcel _Reply = Parcel.obtain();
//
//            _Request.writeInt(103);
//
//            if (mMediaPlayer.invoke(_Request, _Reply) != 0)
//            {
//                _Request.recycle();
//                _Reply.recycle();
//                return null;
//            }
//
//            List <String> _LanguageList = new ArrayList <String>();
//
//            // for get
//            _Reply.readInt();
//            int _Num = _Reply.readInt();
//            String _Language = "";
//            String _SubFormat = "";
//            int _IsExt = 0;
//
//            for (int i = 0; i < _Num; i++)
//            {
//                _Reply.readInt();
//                _IsExt = _Reply.readInt();
//                _Language = _Reply.readString();
//                _SubFormat = mSubFormat[_Reply.readInt()];
//                if (_Language == null || _Language.equals("-"))
//                {
//                    _Language = "";
//                }
//                if (_IsExt == 1)
//                {
//                    _LanguageList.add(_SubFormat);
//                    _LanguageList.add(_Language);
//                }
//            }
//
//            _Request.recycle();
//            _Reply.recycle();
//
//            return _LanguageList;
//        }
//
//        return new ArrayList <String>();
//    }
//    
//    //获取当前播放subid
//    public int getSubtitleId()
//    {
//        if ((mMediaPlayer != null) && mIsPrepared)
//        {
//            Parcel Request = Parcel.obtain();
//            Parcel Reply = Parcel.obtain();
//
//            Request.writeInt(102);
//
//            if (mMediaPlayer.invoke(Request, Reply) != 0)
//            {
//                Request.recycle();
//                Reply.recycle();
//                return -1;
//            }
//            Reply.readInt();
//            int Result = Reply.readInt();
//
//            Request.recycle();
//            Reply.recycle();
//
//            return Result;
//        }
//
//        return -1;
//    }
//
//    public int setSubtitleId(int pSubtitleId)
//    {
//        if ((mMediaPlayer != null) && mIsPrepared)
//        {
//            return mMediaPlayer.setSubTrack(pSubtitleId);
//        }
//
//        return -1;
//    }
//
//    public int setSubtitlePath(String pPath)
//    {
//        if ((mMediaPlayer != null) && mIsPrepared)
//        {
//            return mMediaPlayer.setSubPath(pPath);
//        }
//
//        return -1;
//    }
        
//    public int enableSubtitle(int enable)
//    {
//        if ((mMediaPlayer != null) && mIsPrepared)
//        {
//            return mMediaPlayer.enableSubtitle(enable);
//        }
//        return -1;
//        
//    }
    
    
    
    //视频原始大小
    private int videoOrigHeight = 0;
    private int videoOrigWidth = 0;
    
    private List<AudioInfoOfVideo> audioinfos = new ArrayList<AudioInfoOfVideo>();
    private List<SubInfo> subinfos = new ArrayList<SubInfo>();
    
    private boolean mIsPrepared = false;
    private OnPreparedListener mCustomPrepareListener = null;
    
    private HisiMediaPlayerAdapter mediaplayer = null;
    HiMediaPlayer.OnPreparedListener mPreparedListener = new HiMediaPlayer.OnPreparedListener()
    {
        public void onPrepared(HiMediaPlayer mp)
        {
            mIsPrepared = true;
            mMediaPlayer = mp;
            mediaplayer.setMediaPlayer(mp);
            setmediaPlayerAdapter(mediaplayer);
            
            mp.setOnBufferingUpdateListener(mbufferingListener);
            mp.setOnErrorListener(mOnErrorListener);
            mp.setOnCompletionListener(mcompleteListener);
            mp.setOnSeekCompleteListener(mseekListener);
            mp.setOnInfoListener(mInfoListener);
            mp.setOnFastBackwordCompleteListener(mFB);
            mp.setFastForwardCompleteListener(mFF);
            
            //获取音轨 字幕信息
            setAudioinfos(getmediaPlayerAdapter().getAudioInfos());
            if(getAudioinfos() == null)
            {
                Log.e(TAG, "get Audio Infos failed, return null");
            }
            
            int iret = getmediaPlayerAdapter().getSubInfos(subinfos);
            if(iret != 0)
            {
                Log.e(TAG, "get Subinfos failed, return null");
            }
            
            videoOrigHeight = getVideoHeight();
            videoOrigWidth = getVideoWidth();
                        
            if(mSH != null)
            {
                mSH.setFixedSize(getVideoWidth(), getVideoHeight());
                mp.setDisplay(mSH);
            }
            mCustomPrepareListener.onPrepared(getmediaPlayerAdapter());
            
        }
    };

    
    public void setOnPreparedListener(OnPreparedListener l)
    {
        mCustomPrepareListener = l;
        super.setOnPreparedListener(mPreparedListener);
    }

    private boolean isReady()
    {
        if(mMediaPlayer != null && mIsPrepared)
        {
            return true;
        }
        
        return false;
    }
    
    @Override
    public int getCurrentSoundId()
    {
        if(!isReady())
        {
            return -1;
        }
        
        return getmediaPlayerAdapter().getCurrentSndId();
    }

    @Override
    public int getCurrentSudId()
    {
        if(!isReady())
        {
            return -1;
        }
        
        return getmediaPlayerAdapter().getCurrentSubId();
    }

    @Override
    public IMediaPlayerAdapter getmediaPlayerAdapter()
    {
        return mediaplayer;
    }

    @Override
    public OnBufferingUpdateListener getonBufferingUpdateListener()
    {
        return onBufferingUpdateListener;
        
    }

    @Override
    public OnErrorListener getonErrorListener()
    {
        return onErrorListener;
    }

    @Override
    public OnInfoListener getonInfoListener()
    {
        return mOnInfoListener;
    }

    @Override
    public OnPreparedListener getonPreparedListener()
    {
        return mCustomPrepareListener;
    }

    @Override
    public OnSeekCompleteListener getonSeekCompleteListener()
    {
        return mOnSeekCompleteListener;
    }

    @Override
    public List<SubInfo> getSubtitleList()
    {
         return subinfos;
    }

    @Override
    public int getVideoHeight()
    {
        if(!isReady())
        {
            return -1;
        }
        
        return getmediaPlayerAdapter().getVideoHeight();
    }

    @Override
    public int getVideoWidth()
    {
        if(!isReady())
        {
            return -1;
        }
        
        return getmediaPlayerAdapter().getVideoWidth();
    }

    @Override
    public boolean isSubtitleShowing()
    {
        if(!isReady())
        {
            return false;
        }
        
        return getmediaPlayerAdapter().isSubtitleShowing();
    }


    @Override
    public void setmediaPlayerAdapter(IMediaPlayerAdapter newVal)
    {
        mediaplayer = (HisiMediaPlayerAdapter)newVal;
    }

    private HiMediaPlayer.OnBufferingUpdateListener mbufferingListener = new HiMediaPlayer.OnBufferingUpdateListener()
    {
        @Override
        public void onBufferingUpdate(HiMediaPlayer arg0, int percent)
        {
            if (onBufferingUpdateListener != null)
            {
                onBufferingUpdateListener.onBufferingUpdate(getmediaPlayerAdapter(), percent);
            }
        }
    };
    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener newVal)
    {
        onBufferingUpdateListener = newVal;
    }

    @Override
    public void setOnErrorListener(OnErrorListener newVal)
    {
        onErrorListener = newVal;
        super.setOnErrorListener(mOnErrorListener);
    }

    private com.hisilicon.android.mediaplayer.HiMediaPlayer.OnErrorListener mOnErrorListener = new HiMediaPlayer.OnErrorListener()
    {
        
        @Override
        public boolean onError(HiMediaPlayer mp, int what, int extra)
        {
            if(onErrorListener != null)
            {
                if(getmediaPlayerAdapter() == null)
                {
                    mediaplayer.setMediaPlayer(mp);
                    setmediaPlayerAdapter(mediaplayer);
                }
                
                onErrorListener.onError(getmediaPlayerAdapter(), what, extra);
            }
            return false;
        }
    };
    
    private com.hisilicon.android.mediaplayer.HiMediaPlayer.OnFastForwardCompleteListener mFF = new HiMediaPlayer.OnFastForwardCompleteListener()
    {
        
        @Override
        public void onFastForwardComplete(HiMediaPlayer mp)
        {
            if(mOnFastForwardCompleteListener != null)
            {
                if(getmediaPlayerAdapter() == null)
                {
                    mediaplayer.setMediaPlayer(mp);
                    setmediaPlayerAdapter(mediaplayer);
                }
                
                mOnFastForwardCompleteListener.onFastForwardComplete(getmediaPlayerAdapter());
            }
        }

    };
    
    private com.hisilicon.android.mediaplayer.HiMediaPlayer.OnFastBackwordCompleteListener mFB = new HiMediaPlayer.OnFastBackwordCompleteListener()
    {
        
        @Override
        public void onFastBackwordComplete(HiMediaPlayer mp)
        {
            if(mOnFastBackwordCompleteListener != null)
            {
                if(getmediaPlayerAdapter() == null)
                {
                    mediaplayer.setMediaPlayer(mp);
                    setmediaPlayerAdapter(mediaplayer);
                }
                
                mOnFastBackwordCompleteListener.onFastBackwordComplete(getmediaPlayerAdapter());
            }
        }

    };
    
    
    @Override
    public void setOutRange(int left, int top, int w, int h)
    {
        if(!isReady())
        {
            return ;
        }
        
        int i = getmediaPlayerAdapter().setScreenOutRange(left, top, w, h);
        
        Log.d(TAG, "setOutRange return :"+i);
    }

    @Override
    public void setSoundId(int id)
    {
        if(!isReady())
        {
            return ;
        }
        
        int i = getmediaPlayerAdapter().setSoundId(id);
        
        Log.d(TAG, "setSoundId return :"+i);
    }

    @Override
    public void setSubId(int id)
    {
        if(!isReady())
        {
            return ;
        }
        
        int i = getmediaPlayerAdapter().setSubId(id);
        
        Log.d(TAG, "setSubId return :"+i);
    }

    @Override
    public void showSubtitle(boolean show)
    {
        if(!isReady())
        {
            return ;
        }
        
        int flag = show?0:1;
        
        int i = getmediaPlayerAdapter().enableSubtitle(flag);
        
        Log.d(TAG, "showSubtitle return :"+i);
        
    }

    /**
     * @return 返回 audioinfos
     */
    public List<AudioInfoOfVideo> getAudioinfos()
    {
        return audioinfos;
    }

    /**
     * @param 对audioinfos进行赋值
     */
    public void setAudioinfos(List<AudioInfoOfVideo> audioinfos)
    {
        this.audioinfos = audioinfos;
        
    }

    
    /**
     *设置ｓｕｒｆａｃｅ宽高 
     * <功能详细描述>
     * @param w
     * @param h
     * @return 成功　返回０；否则失败
     * @see [类、类#方法、类#成员]
     */
    public int setScreenScale(int w, int h)
    {
        if(!isReady())
        {
            return -1;
        }
        
        return mediaplayer.setScreenScale(w, h);
    }

    @Override
    public void setSubSurfaceHolder(SurfaceHolder sh)
    {
        mSH = sh;
    }
    
    private OnFastForwardCompleteListener mOnFastForwardCompleteListener = null;
    private OnFastBackwordCompleteListener mOnFastBackwordCompleteListener = null;
    @Override
    public void setOnFastForwardCompleteListener(OnFastForwardCompleteListener l)
    {
        mOnFastForwardCompleteListener = l;
    }

    @Override
    public void setOnBackForwardCompleteListener(OnFastBackwordCompleteListener l)
    {
        mOnFastBackwordCompleteListener = l;
    }

    public int setSpeed(int i)
    {
        if(!isReady())
        {
            return -1;
        }
        
       return mediaplayer.setSpeed(i);
    }
    
    public FileInfo getFileInfo()
    {
        if(!isReady())
        {
            Log.e(TAG, "!isReady()");
            return null;
        }
        
        Parcel requestParcel = Parcel.obtain();// Parcel allocation
        // parameter
        requestParcel.writeInt(HiMediaPlayerInvoke.CMD_GET_FILE_INFO);
        Parcel replyParcel = Parcel.obtain();
        mediaplayer.invoke(requestParcel, replyParcel); // invoke
        
        //返回值
        int ret = replyParcel.readInt();
        
        if(ret != 0)
        {
            Log.e(TAG, "ret != 0)");
            replyParcel.recycle();
            requestParcel.recycle();
            return null;
        }
        
        FileInfo ai = new FileInfo();
        
        //video format
        int videoformat = replyParcel.readInt();
        int audioformat = replyParcel.readInt();
        long size = replyParcel.readLong();
        ai.size = size;
        Log.d(TAG, "size :"+size+"B");
        
        String tmp = replyParcel.readString();
        ai.album = tmp;
        Log.d(TAG, "album :"+tmp);
        
        tmp = replyParcel.readString();
        ai.title = tmp;
        Log.d(TAG, "title :"+tmp);
        
        tmp = replyParcel.readString();
        ai.artist = tmp;
        Log.d(TAG, "artist :"+tmp);
        
        tmp = replyParcel.readString();
        ai.genre = tmp;
        Log.d(TAG, "genre :"+tmp);
        
        tmp = replyParcel.readString();
        ai.year = tmp;
        Log.d(TAG, "year :"+tmp);
//        861      * 1，  int类型，命令执行结果                                                                                 
//        862      * 2，  unsigned int类型，当前播放视频编码格式                                                                
//        863      * 3，  unsigned int类型，音频编码格式                                                                        
//        864      * 4，  long long类型，文件大小                                                                               
//        865      * 5，  String16类型，metadata信息（pszAlbum，pszTitle，pszArtist，pszGenre，pszYear）  
        
        replyParcel.recycle();
        requestParcel.recycle();
        
        return ai;
        
    }

    /* BEGIN: Added by r00178559 for AR-0000698413 2014/02/13 */
    @Override
    public boolean setAudioChannelMode(int channelMode)
    {
        return getmediaPlayerAdapter().setAudioChannelMode(channelMode);
    }
    /* END: Added by r00178559 for AR-0000698413 2014/02/13 */


    /* BEGIN: Added by c00224451 for  AR-0000698413 外挂字幕  2014/2/24 */
    @Override
    public int setSubPath(String path)
    {
        return getmediaPlayerAdapter().setSubPath(path);
    }
    /* BEGIN: Added by c00224451 for  AR-0000698413 外挂字幕  2014/2/24 */
    
    @Override
    public int getBufferSizeStatus()
    {
        // TODO Auto-generated method stub
        if(!isReady())
        {
            return -1;
        }
        return mediaplayer.getBufferSizeStatus();
    }

    @Override
    public int getBufferTimeStatus()
    {
        // TODO Auto-generated method stub
        if(!isReady())
        {
            return -1;
        }
        return mediaplayer.getBufferTimeStatus();
    }

	@Override
    public boolean isDolbyEnabled()
    {
    	return getmediaPlayerAdapter().isDolbyEnabled();
    }
	@Override
    public AudioInfoOfVideo getCurrentAudioinfos()
    {
    	return null;
    }
}