package remix.myplayer.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.RenderScript;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import remix.myplayer.R;
import remix.myplayer.activities.AudioHolderActivity;
import remix.myplayer.ui.LrcView;
import remix.myplayer.utils.LrcInfo;
import remix.myplayer.utils.MP3Info;
import remix.myplayer.utils.SearchLRC;
import remix.myplayer.utils.Utility;

/**
 * Created by Remix on 2015/12/2.
 */
public class LrcFragment extends Fragment {
    private static int UPDATE_LRC = 1;
    private static int NO_LRC = 2;
    private static int NO_NETWORK = 3;
    private MP3Info mInfo;
    private LrcView mTextLrc;
    private LinkedList<LrcInfo> mTextList;
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == UPDATE_LRC)
            {
                if(mTextList != null && mTextLrc != null)
                {
                    mTextLrc.setText("");
                    mTextLrc.mlrcList = mTextList;
                }
            }
            else if (msg.what == NO_LRC)
            {
                mTextLrc.mlrcList = null;
                mTextLrc.setText("");
                mTextLrc.setText("暂无歌词");
            }
            else if (msg.what == NO_NETWORK)
            {
                mTextLrc.mlrcList = null;
                mTextLrc.setText("");
                mTextLrc.setText("请检查网络连接");
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lrc,container,false);
        mInfo = (MP3Info)getArguments().getSerializable("MP3Info");
        mTextLrc = (LrcView)rootView.findViewById(R.id.lrc_text);
        UpdateLrc(mInfo);
        return rootView;
    }

    public void UpdateLrc(MP3Info mp3Info)
    {
        if(mp3Info == null)
            return;
        new DownloadThread(mp3Info.getDisplayname(),mp3Info.getArtist()).start();
    }

    class DownloadThread extends Thread
    {
        private String mName;
        private String mArtist;
        public DownloadThread(String name,String artist)
        {
            mName = name;
            mArtist = artist;
        }
        @Override
        public void run() {
            if(!Utility.isNetWorkConnected()) {
                mHandler.sendEmptyMessage(NO_NETWORK);
                return;
            }
            SearchLRC searchLRC = new SearchLRC(mName,mArtist);
            mTextList = searchLRC.fetchLyric();
            if(mTextList == null) {
                mHandler.sendEmptyMessage(NO_LRC);
                return;
            }
            AudioHolderActivity activity = (AudioHolderActivity)getActivity();
            mHandler.sendEmptyMessage(UPDATE_LRC);
        }

    }

//    class DownloadTask extends AsyncTask<String[],Integer,TreeMap<Integer,String>>
//    {
//        @Override
//        protected TreeMap<Integer,String> doInBackground(String[]... params) {
//            if(mInfo == null)
//                return null;
//            String name = params[0][0];
//            String artist = params[0][1];
//            SearchLRC searchLRC = new SearchLRC(name,artist);
//            return searchLRC.fetchLyric();
//        }
//        @Override
//        protected void onPostExecute(TreeMap<Integer,String> strings) {
//            mTextMap = strings;
//            if(mTextMap != null && mTextMap.size() > 0)
//                mHandler.sendEmptyMessage(UPDATE_LRC);
//        }
//    }


}