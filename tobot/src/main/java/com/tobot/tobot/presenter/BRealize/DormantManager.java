package com.tobot.tobot.presenter.BRealize;

import android.os.Message;
import android.util.Log;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by YF-04 on 2017/12/26.
 */

public class DormantManager {
    private static final String TAG = "IDormant";

    public static final int DORMANT_TYPE_STRAIGHT_TO_SLEEP=23;
    public static final int DORMANT_TYPE_SIT_DOWN_AND_SLEEP=46;
    public static final int DORMANT_TYPE_LIE_DOWN_AND_SLEEP=53;

    private static int type;

    /**
     * 坐下休息（休眠）：站着休眠10分钟不唤醒 之后就坐下休息了
     */
    private static Timer sitDownAndSleepTimer=new Timer(true);

    private static android.os.Handler mHandler=new android.os.Handler();

    private  SitDownAndSleepTimeTask sitDownAndSleepTimeTask;


    public static int getType() {
        return type;
    }

    public static void setType(int type) {
        DormantManager.type = type;
    }

    /**
     * 创建任务：站着休眠N分钟不唤醒 ,进入坐下休息（休眠）
     */
    public    void sitDownAndSleepTrigger(){
        Log.d(TAG, "DormantManager sitDownAndSleepTrigger: ");
        if (sitDownAndSleepTimeTask==null){
            sitDownAndSleepTimeTask=new SitDownAndSleepTimeTask();
        }

        sitDownAndSleepTimer.schedule(sitDownAndSleepTimeTask,1*60*1000);//N分钟之后

    }

    public void cancelSitDownAndSleepTrigger(){
        Log.d(TAG, "DormantManager cancelSitDownAndSleepTrigger: ");
//        if (sitDownAndSleepTimer!=null){
//            sitDownAndSleepTimer.cancel();
//        }
//        sitDownAndSleepTimer=new Timer();
        sitDownAndSleepTimeTask.cancel();
    }

    public static Timer getSitDownAndSleepTimer() {
        return sitDownAndSleepTimer;
    }

    public static void setSitDownAndSleepTimer(Timer sitDownAndSleepTimer) {
        DormantManager.sitDownAndSleepTimer = sitDownAndSleepTimer;
    }

    class SitDownAndSleepTimeTask extends TimerTask {

        @Override
        public void run() {
            Log.d(TAG, "SitDownAndSleepTimeTask run: ");
            Log.d(TAG, "站着休眠10分钟不唤醒  进入坐下休息（休眠）");
            //唤醒
            BFrame.Wakeup(3);
            //发送动作、表情等等
            //休眠
            SitDownAndSleep sitDownAndSleep=new SitDownAndSleep();
            sitDownAndSleep.dormant();
        }
    }
    

}
