package com.tobot.tobot.presenter.BRealize;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tobot.tobot.Listener.ExpressionCallback;
import com.tobot.tobot.Listener.LocalCommandGather;
import com.tobot.tobot.Listener.MainScenarioCallback;
import com.tobot.tobot.Listener.SimpleFrameCallback;
import com.tobot.tobot.MainActivity;
import com.tobot.tobot.R;
import com.tobot.tobot.base.Constants;
import com.tobot.tobot.base.Frequency;
import com.tobot.tobot.function.AssembleFunction;
import com.tobot.tobot.function.QASRFunction;
import com.tobot.tobot.presenter.ICommon.ISceneV;
import com.tobot.tobot.presenter.IPort.IFrame;
import com.tobot.tobot.scene.BaseScene;
import com.tobot.tobot.scene.CustomScenario;
import com.tobot.tobot.utils.AppTools;
import com.tobot.tobot.utils.TobotUtils;
import com.tobot.tobot.utils.bluetoothblock.Ble;
import com.turing123.robotframe.RobotFrameManager;
import com.turing123.robotframe.RobotFramePreparedListener;
import com.turing123.robotframe.RobotFrameShutdownListener;
import com.turing123.robotframe.config.SystemConfig;
import com.turing123.robotframe.event.AppEvent;
import com.turing123.robotframe.function.FunctionManager;
import com.turing123.robotframe.function.IInitialCallback;
import com.turing123.robotframe.function.asr.IASRFunction;
import com.turing123.robotframe.function.cloud.Cloud;
import com.turing123.robotframe.function.expression.Expression;
import com.turing123.robotframe.function.keyin.KeyInputEvent;
import com.turing123.robotframe.function.motor.Motor;
import com.turing123.robotframe.function.tts.ITTSCallback;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.function.wakeup.VoiceWakeUp;
import com.turing123.robotframe.interceptor.StateBuilder;
import com.turing123.robotframe.multimodal.action.Action;
import com.turing123.robotframe.multimodal.action.BodyActionCode;
import com.turing123.robotframe.multimodal.action.EarActionCode;
import com.turing123.robotframe.multimodal.expression.EmojNames;
import com.turing123.robotframe.multimodal.expression.FacialExpression;
import com.turing123.robotframe.scenario.ScenarioManager;

import static com.turing123.robotframe.function.keyin.KeyInputEvent.KEYCODE_HEAD;
import static com.turing123.robotframe.multimodal.action.Action.PRMTYPE_EXECUTION_TIMES;


/**
 * Created by Javen on 2017/12/7.
 */

public class BFrame implements IFrame {

    private static final String TAG = "Javen BFrame";
    private static BFrame mBFarme;
    private Context mContent;
    private ISceneV mISceneV;
    private static MainActivity main;
    private boolean whence;
    private static RobotFrameManager mRobotFrameManager;
    private FunctionManager functionManager;
    private CustomScenario customScenario;
    private static FacialExpression mFacialExpression;
    private static Expression mExpression;
    private static Motor motor;
    private Cloud mCloud;
    private static TTS tts;
    private static ScenarioManager scenarioManager;
    private BScenario mBScenario;
    private BConnect mBConnect;
    private BMonitor mBMonitor;
    private BDormant mBDormant;
    private static BLocal mBLocal;
    private static BBattery mBBattery;
    private static BArmtouch mBArmtouch;
    private BProtect mBProtect;
    private BSensor mBSensor;
    public static boolean replace;


    public static synchronized BFrame instance(ISceneV mISceneV) {
        if (mBFarme == null) {
            mBFarme = new BFrame(mISceneV);
        }
        return mBFarme;
    }

    private BFrame(ISceneV mISceneV) {
        this.mISceneV = mISceneV;
        this.mContent = (Context) mISceneV;
        this.main = (MainActivity) mISceneV;
        onInitiate(false);
    }

    @Override
    public void onInitiate(boolean whence) {
        this.whence = whence;
        //0. 因为各功能的使用都需要携带使用该功能的场景，所以先创建一个场景，如果脱离场景使用，请使用FailOver 类。
        customScenario = new CustomScenario(mContent);
        //1. 设置对话模式为自动对话，主场景将维护对话的输入和输出。
        try {
            startRobotFramework();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //1. 获取ScenarioManager.
        scenarioManager = new ScenarioManager(mContent);
    }

    @Override
    public RobotFrameManager startRobotFramework() throws Exception {
        // 取得框架实例
        mRobotFrameManager = RobotFrameManager.getInstance(mContent);
        //设置apikey
        mRobotFrameManager.setApiKeyAndSecret(Constants.APIKEY, Constants.SERVICE);
        // 设置框架聊天模式
        setChatMode(SystemConfig.CHAT_MODE_AUTO);
        // 设置状态机工作模式。查看API Ref以了解更多关于框架工作模式的信息
        int state = new StateBuilder(StateBuilder.DefaultMode).build();
        // prepare（）这个方法必须在你做任何事情之前被调用
        mRobotFrameManager.prepare(state, new RobotFramePreparedListener() {

            @Override
            public void onPrepared() {
                // 激活
                frameHandle.sendEmptyMessage(Constants.REPLACE_ASR);
                mRobotFrameManager.start();
                // 可选的控制场景
                // mRobotFrameManager.toLostScenario();
                // 回到默认场景
                // mRobotFrameManager.backMainScenario();
                frameHandle.sendEmptyMessage(Constants.START_SUCESS_MSG);
            }

            @Override
            public void onError(String errorMsg) {
                // error occurred, check errorMsg and have all error fixed
                Message message = Message.obtain();
                message.what = Constants.START_ERROR_MSG;
                message.obj = errorMsg;
                frameHandle.sendMessage(message);
            }
        });
        return mRobotFrameManager;
    }

    private Handler frameHandle = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.START_ERROR_MSG://框架加载失败
                    mBConnect.isLoad(false);
                    mBConnect.shunt();
                    main.FrameLoadFailure();
                    Log.e(TAG, "start error ⊙﹏⊙b\n" + msg.obj);
                    break;
                case Constants.REPLACE_ASR:
                    replaceFunction();
                    break;
                case Constants.START_SUCESS_MSG:
                    Log.e(TAG, "⊙_⊙  框架加载成功");
                    mBConnect.isLoad(true);
                    //运行TTS
                    onTTS();
                    //初始化功能
                    onFunction();
                    //调度
                    onAssemble();
                    //进入次场景
                    onMinorscene();
                    //通知
                    onNotification();
                    //手臂触摸
                    onBArmtouch();
                    //休眠
                    onDormant();
                    //唤醒
                    onRouse();
                    //本地命令
                    onLocal();
                    //电量
                    onBattery();
                    //自我保护
                    onBProtect();
                    //注册监听器
                    onBSensor();
                    //加载成功
                    main.FrameLoadSuccess(whence);

                    break;
                default:
                    break;
            }
        }
    };

    public void setConnectState(BConnect mBConnect){
        this.mBConnect = mBConnect;
    }

    // TTS的使用
    private void onTTS() {
        tts = new TTS(main, new BaseScene(main, "os.sys.chat"));
    }

    // 初始化功能
    private void onFunction() {
        functionManager = new FunctionManager(main);
        motor = new Motor(main, new CustomScenario(main));
        mCloud = new Cloud(main, new MainScenarioCallback());
        mFacialExpression = new FacialExpression();
        mFacialExpression.displayMode = FacialExpression.DISPLAY_MODE_PROTOCOL_PREDEFINED;
        mFacialExpression.executeMode = Action.MODE_COVER;
        mFacialExpression.eyeParams.put(PRMTYPE_EXECUTION_TIMES, 1);
        mExpression = new Expression(main, new BaseScene(main, "os.sys.chat"));
    }

    private void onAssemble(){
        //1. 创建Assemble Function 实例。
        final AssembleFunction assembleFunction = new AssembleFunction(main);
        //2. 初始化
        assembleFunction.init(new IInitialCallback() {
            @Override
            public void onSuccess() {
                //3. 初始化成功后将assemble function加入RobotFrame.
                //3.1 获取Function 的管理类
                //3.2 调用addFunction, 将assembleFunction加入系统
                functionManager.addFunction(assembleFunction);
            }

            @Override
            public void onError(String s) {

            }
        });
    }

    //替换asr
    private void replaceFunction(){
        //1. 创建自定义Function 实例。
        final QASRFunction mQASRFunction = new QASRFunction(main);
        //2. 初始化自定义的Function.
        mQASRFunction.initASR(new IInitialCallback() {
            @Override
            public void onSuccess() {
                //3. 初始化成功后将自定义function加入RobotFrame.
                //3.1 获取Function 的管理类
                FunctionManager functionManager = new FunctionManager(main);
                //3.2 调用replaceFunction 替换系统中type相同的默认function（本示例替换asr）.
                boolean replaced = functionManager.replaceFunction(mQASRFunction);
                Log.i(TAG,"替换成功:" + replaced);
                replace = replaced;
            }

            @Override
            public void onError(String errorMessage) {
                Log.i(TAG,"替换失败:"+errorMessage);
            }
        });
    }

    // 进入次场景
    private void onMinorscene() {
        mBScenario = new BScenario(main);
    }

    // 监听
    private void onNotification() {
        mBMonitor = new BMonitor(main);
    }

    // 休眠
    private void onDormant() {
        mBDormant = new BDormant(main);
    }

    // 本地命令
    private void onLocal() { mBLocal = BLocal.instance(main); }

    // 电量检测
    private void onBattery() {
        mBBattery = new BBattery(main);
    }

    // 手臂触摸
    private void onBArmtouch() {
        mBArmtouch = new BArmtouch(main);
    }

    // 自我保护机制
    private void onBProtect() {
        mBProtect = new BProtect(main);
    }

    // 传感器监听
    private void onBSensor() {
        mBSensor = new BSensor(main);
    }

    // 唤醒功能
    private void onRouse() {
        VoiceWakeUp mVoiceWakeUp = new VoiceWakeUp(main, customScenario);
        mVoiceWakeUp.configWakeUp("/sdcard/.TuringResource/system/WakeUp.bin");
//            mVoiceWakeUp.configWakeUp("assets/WakeUp.bin");
    }

    // ASR提醒音
    public static void hint() {
        if (replace){
            Frequency.start("/sdcard/.TuringResource/audio/asr_prompt_tone.mp3");
        }else if(scenarioManager == null){
            //1. 获取ScenarioManager.
            scenarioManager = new ScenarioManager(main);
            //2. 设置开关，true 为开， false 为关。
            scenarioManager.switchDefaultChatAsrPrompt(true, false);
        }else{
            scenarioManager.switchDefaultChatAsrPrompt(true, false);
        }
    }





    public static RobotFrameManager getRobotFrameManager() {
        if (TobotUtils.isNotEmpty(mRobotFrameManager)) {
            return mRobotFrameManager;
        } else {
            return null;
        }
    }

    public static BLocal getmBLocal(){
        if (TobotUtils.isNotEmpty(mBBattery)) {
            return mBLocal;
        } else {
            return null;
        }
    }

    public static BBattery getBBattery(){
        if (TobotUtils.isNotEmpty(mBBattery)) {
            return mBBattery;
        } else {
            return null;
        }
    }

    public static BArmtouch getBArmtouch(){
        if (TobotUtils.isNotEmpty(mBBattery)) {
            return mBArmtouch;
        } else {
            return null;
        }
    }

    //选择asr
    public void choiceFunctionProcessor(int type){
        functionManager.choiceFunctionProcessor(AppEvent.FUNC_TYPE_ASR,type);
    }

    public void resetFunction(){
        functionManager.resetFunction(AppEvent.FUNC_TYPE_ASR);
    }

    //下发动作
    public static void outAction(int code){
        motor.doAction(Action.buildBodyAction(code,PRMTYPE_EXECUTION_TIMES,1),new SimpleFrameCallback());
    }

    public static void outAction(int code,int type){
        motor.doAction(Action.buildBodyAction(code,type,1),new SimpleFrameCallback());
    }

    public static void outAction(int code,int type,int value){
        motor.doAction(Action.buildBodyAction(code,type,value),new SimpleFrameCallback());
    }

    //下发耳部灯圈
    public static void Ear(int code){
        motor.doAction(Action.buildEarAction(code,80,1),new SimpleFrameCallback());
    }

    public static void Ear(int code,int pleasantness){
        motor.doAction(Action.buildEarAction(code,80,pleasantness),new SimpleFrameCallback());
    }

    public static void Ear(int code,int brightness,int pleasantness){
        motor.doAction(Action.buildEarAction(code,brightness,pleasantness),new SimpleFrameCallback());
    }

    //脱离主场景
    public static void shutChat(){
        mRobotFrameManager.toLostScenario();
    }

    //回到主场景
    public static void disparkChat(){
        mRobotFrameManager.backMainScenario();
    }

    //下发表情
    public static void Facial(String facial){
        mFacialExpression.emoj = facial;
        mExpression.showExpression(mFacialExpression, new ExpressionCallback());
    }

    //执行tts
    public static void TTS(String voice){
        tts.speak(voice,ittsCallback);
    }


     static ITTSCallback ittsCallback = new ITTSCallback() {

        @Override
        public void onStart(String s) {
            Log.i(TAG ,"开始语音播报TTS:"+s);
            main.Interrupt(true);
        }

        @Override
        public void onPaused() {
            main.Interrupt(false);
        }

        @Override
        public void onResumed() {
            main.Interrupt(true);
        }

        @Override
        public void onCompleted() {
//            if(anewConnect){
//                Log("异常断网进入重连");
//                anewConnect = false;
//                mBConnect.shunt();//重新联网 -- 需要考虑是否要直接启动还是摸头三秒启动
//            }
            //自主休眠禁止唤醒
//            try {
//                if (mScoffEntity.getDormant()) {
//                    Log("进入自主休眠10分钟" + isInterrupt);
//                    motor.doAction(Action.buildBodyAction(BodyActionCode.ACTION_8, PRMTYPE_EXECUTION_TIMES, 1), new SimpleFrameCallback());
//                    //注意： 若要唤醒机器人，可调用wakeup,或者使用语言唤醒词唤醒。
//                    mRobotFrameManager.sleep();
//                    //5.2 命令执行完成后需明确告诉框架，命令处理结束，否则无法继续进行主对话流程。
//                    new LocalCommandGather().onComplete();
//                    awakenTimer.schedule(new AwakenTimerTask(), 600000);//休眠10分钟
//                    isOFF_HINT = true;
//                    isNotWakeup = false;//禁止打断
//                }
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
            main.Interrupt(false);
        }

        @Override
        public void onError(String s) {
            Log.i(TAG ,"TTS错误"+s);
        }
    };

    //进入睡眠
    public static void FallAsleep(){
        if (TobotUtils.isNotEmpty(mRobotFrameManager)){
            Ear(EarActionCode.EAR_MOTIONCODE_1);//待机效果
            mRobotFrameManager.sleep();
            //5.2 命令执行完成后需明确告诉框架，命令处理结束，否则无法继续进行主对话流程。
            new LocalCommandGather().onComplete();
        }
    }

    public static void Wakeup(){
        if (TobotUtils.isNotEmpty(mRobotFrameManager)) {
            Ear(EarActionCode.EAR_MOTIONCODE_4);//启动效果
            mRobotFrameManager.wakeup();
        }
    }

    //设置聊天模式
    public static void setChatMode(int mode){
        mRobotFrameManager.setChatMode(mode);
    }

    //打断
    public static void Interrupt(){
        mRobotFrameManager.interrupt(SystemConfig.INTERRUPT_TYPE_TOUCH, null);
    }

    //通知关机
    public static void shutDown(){
        mRobotFrameManager.shutDown(new RobotFrameShutdownListener() {

            @Override
            public void onShutDown() {
                Log.i(TAG,"已通知关机");
            }

            @Override
            public void onError(String s) {
                Log.i(TAG,"通知关机 onError" + s);
            }
        });
    }




}
