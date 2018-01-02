package com.tobot.tobot.base;

import android.graphics.Path;

import okhttp3.internal.tls.OkHostnameVerifier;

/**
 * Created by Javen on 2017/7/10.
 */

public class Constants {

    // 数据库
    public static final String TOBOT_DB_NAME = "tobot.db";

//    public static String APIKEY = "115dfbdf8b154fb4b268e93e48111d1c";//小巴
//    public static String SERVICE = "98be8637d8fc024e";
    public static String APIKEY = "9001c48ba92646bc91ec274446556e8a";//陈工
    public static String SERVICE = "271196031296486f";
    public static String DEFAULT_VOLUME = "50";//默认音量

    public static String identifying = "TuBaRobot2017";

    /**图巴机器人服务端:IP+端口*/
    public final static String CIM_SERVER_HOST0 = "http://39.108.134.20/";

    public final static String ROBOT_BOUND = CIM_SERVER_HOST0 + "tubarobot/disp/robot/add/";//绑定机器人
    public final static String AMEND_STATUS = CIM_SERVER_HOST0 + "tubarobot/disp/robot/attr/set/";//修改角色属性
    public final static String ACTION_LIST = CIM_SERVER_HOST0 + "tubarobot/disp/robot/action/list/";//动作列表
    public final static String DANCE_LIST = CIM_SERVER_HOST0 + "tubarobot/disp/robot/dance/list/";//舞蹈列表
    public final static String IMAGE_UPLOAD = CIM_SERVER_HOST0 + "tubarobot/disp/robot/image/upload/";//图片上传
    public final static String ANSWER_LIST = CIM_SERVER_HOST0 + "tubarobot/disp/robot/qa/list/";//问答列表

    public final static String Path = "/sdcard/.TuubaResource/tuuba_strings.xml";//文件地址
    public final static String DeviceId = "name=\"robot_DeviceId\"";//设备Id
    public final static String MAC = "name=\"robot_MAC\"";//mac地址
    public final static String Ble_Name = "name=\"robot_Ble_Name\"";//蓝牙名称



    public static final int NOTIFICATION_MSG = 1;
    public static final int START_SUCESS_MSG = 2;
    public static final int START_ERROR_MSG = 3;
    public static final int NET_MSG = 4;
    public static final int AWAIT_DORMANT = 5;
    public static final int AWAIT_AWAKEN = 6;
    public static final int AWAIT_ACTIVE = 7;
    public static final int REPLACE_ASR = 8;

    public static final int SIT_DOWN_AND_SLEEP_DORMANT=34364;

    public static final int CLOSE_AP = 1000;
    public static final int FOR_RESULT = 1001;

    public static final String  SEPARATOR_BETWEEN_PLAYURL_ACTION="#,#";//playUrl 和 bodyActionCode（舞蹈编号） 的分隔符


    public static final String  QVOICE_APPID = "d4aa2ff0-e924-11e7-b526-00163e13c8a2";
    public static final String  QVOICE_KEY = "3b0468a1-c0cd-3313-a648-49cd7210b984";
    public static final String  QVOICE_PATH = "/sdcard/qvoice/cfg";
    public static final String  QVOICE_MIC = "/sdcard/qvoice/mic.wav";
    public static final String  QVOICE_PARAMS = "bfio";//bfasr
//    public static final String  QVOICE = "role=asr;cfg=/sdcard/qvoice/cldasr.bin;use_json=1;min_conf=1.9;timeout=3000";
    public static final String  QVOICE = "role=asr;cfg=/sdcard/qvoice/xasr/cfg;use_bin=0;use_json=1;min_conf=1.9;";//用asr2017.12.27


    public static final String KIND_KEY="kind";


}
