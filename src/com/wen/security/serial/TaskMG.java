package com.wen.security.serial;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.wen.security.beans.GunCabInfo;

import java.util.ArrayList;
import java.util.Date;

/**
 * 命令发送中心
 * 
 * @author cloud
 * 
 */
public class TaskMG {
    private static final String TAG = "TaskMG";
    /**
     * 设定开锁时间,时间单位为秒,目前设定值为0x0a,最长为0x14
     */
    private final byte OpenLockTimer = 0x14;
    /**
     * 复位命令
     */
    public static final byte RESET = 0x20;
    /**
     * 查询命令
     */
    public static final byte QUERRY = 0x21;
    /**
     * 开锁命令
     */
    public static final byte OPENLOCK = 0x22;
    /**
     * 定义开锁最长时间
     */
    public static final byte OPENLOCK_MAX_TIME = 0x23;
    /**
     * 报警控制
     */
    public static final byte ALARM_CONTROL = 0x25;
    /**
     * 扩展板触点查询命令
     */
    public static final byte EXTEND_QUERRY = 0x26;
    /**
     * 扩展板开锁命令
     */
    public static final byte EXTEND_LOCKOPEN = 0x27;
    /**
     * 扩展板发光管控制命令
     */
    public static final byte EXTEND_LIGHT = 0x28;
    /**
     * 扩展板发光管闪烁控制命令
     */
    public static final byte EXTEND_SPARK = 0x29;
    /**
     * 读扩展板发光管状态命令
     */
    public static final byte READ_EXTEND_SPARK = 0x2A;
    /**
     * 读电子秤测到的重量
     */
    public static final byte READ_WEIGHT = 0x2B;
    /**
     * 读电子秤指定数据
     */
    public static final byte READ_WEIGHT_DATA = 0x2C;
    /**
     * 设置电子秤
     */
    public static final byte SET_WEIGHT = 0x2D;
    /**
     * 设置超时时长//awenw
     */
    public static long OVERTIMER = 60000 * 3;
    private static TaskMG instance;
    /**
     * 用于开锁前记住触点状态，查询枪信息时候比较结果返回 领枪和触点查询的分开,0表示领枪,1表示非法领枪,2表示触点查询
     * 
     * @author awenw 柜号，触点状态
     */
    private static byte[][] CurGuninfo = new byte[3][32];

    public interface OnTaskReceve {
        /**
         * 发送任务的回调函数
         * 
         * @param flag
         *            任务执行成功标志
         * @param ordcmd
         *            命令名称
         * @param receveData
         *            返回数据
         * @param ID
         *            TODO
         */
        void Onreceve(Boolean flag, byte ordcmd, byte[] receveData,
                               String ID);
    }

    private TaskMG() {

        taskList = new ArrayList<TaskObj>();
    }

    public static TaskMG getOne() {
        if (instance == null) {
            instance = new TaskMG();
        }
        return instance;
    }

    private boolean isrun;

    /**
     * 关闭命令中心，停止消息循环
     */
    public void close() {
        isrun = false;
    }

    /**
     * 启动命令中心，开始消息循环
     */
    public void init() {
        serialMG = new SerialMG();
        new Thread(new Runnable() {
            @Override
            public void run() {
                isrun = true;
                while (isrun) {

                    try {
                        // 普通任务执行时查询任务不执行
                        if (taskList.size() > 0) {

                            for (int i = 0; i < taskList.size(); i++) {
                                TaskObj taskObj = taskList.get(i);
                                if (taskObj.level == TaskObj.ONECE) {
                                    serialMG.execorder(mHandler, taskObj,
                                            (byte) 0x01);
                                    remove(taskObj);
                                } else if (taskObj.level == TaskObj.ALWAYS) {
                                    serialMG.execorder(mHandler, taskObj,
                                            (byte) 0x01);
                                    Thread.sleep(600);
                                }
                            }
                        }
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                while (isrun) {
//                    try {
//                        Log.v(TAG, "--------");
//                        DataFunc.watchTimeOutGunBack(context);
//                        Thread.sleep(DataFunc.gunbacktimeout);
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }).start();
    }

    SerialMG serialMG;
    /**
     * 任务队列
     */
    private ArrayList<TaskObj> taskList;

    /**
     * 添加一个任务
     * 
     * @param ordcmd
     *            命令名称
     * @param databuf
     *            命令数据
     * @param source
     *            为null则无回调
     */
    private void addTask(byte ordcmd, byte[] databuf, OnTaskReceve source) {
        TaskObj temp = new TaskObj();
        temp.rec = source;
        temp.ordcmd = ordcmd;
        temp.sndbuf = databuf;
        add(temp);
    }

    /**
     * 添加一个持续任务，（持续命令id不能为空)
     * 
     * @param ordcmd
     *            TODO
     * @param databuf
     *            TODO
     * @param source
     */
    private void addAlwaysTask(byte ordcmd, byte[] databuf, String ID,
            OnTaskReceve source) {
        if (ID != null) {
            TaskObj temp = new TaskObj();
            temp.level = TaskObj.ALWAYS;
            temp.ID = ID;
            temp.sndbuf = databuf;
            temp.rec = source;
            temp.ordcmd = ordcmd;
            // temp.databuf = databuf;
            add(temp);
        }

    }

    /**
     * 移除一个持续任务byID(唯一标识)
     * 
     * @param ID
     */
    public void removeAlwaysTask(String ID) {
        if (ID != null) {
            for (int i = 0; i < taskList.size(); i++) {
                TaskObj taskObj = taskList.get(i);
                if (taskObj.ID != null && taskObj.ID.equals(ID)) {
                    remove(taskObj);
                }
            }

        }

    }

    public static final int ERROR = 11;
    public static final int SUCCESS = 12;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TaskObj info = (TaskObj) msg.obj;
            switch (msg.what) {
            case ERROR:
                info.rec.Onreceve(false, info.ordcmd, info.databuf, info.ID);
                break;
            case SUCCESS:
                info.rec.Onreceve(true, info.ordcmd, info.databuf, info.ID);
                break;
            }
        }
    };

    private synchronized void add(TaskObj obj) {
        taskList.add(obj);
    }

    private synchronized void remove(TaskObj obj) {
        taskList.remove(obj);
    }

    public interface ITemper {
        /**
         * 
         * @param id
         * @param temper
         *            温度（摄氏度）
         */
        void onTemper(String id, String temper);
    }

    /**
     * 物品状态 接口
     * 
     * @author cloud
     * 
     */
    public interface IStoreObjectStatus {
        /**
         * 
         * @param id
         * @param addr
         *            物品的地址
         */
        void onStatus(String id, String addr);
    }

    /**
     * 门的状态
     */
    public interface IDoorStatus {
        /**
         * 
         * @param id
         * @param isOpen
         *            <code>false</code>全关上了
         */
        void onStatus(String id, Boolean isOpen);
    }

    /**
     * 子柜物件的状态
     */
    public interface ISubStoreObjectStatus {
        /**
         * 
         * @param id
         * @param addr
         *            物品的地址
         * @param num
         *            物品的数量
         */
        void onStatus(String id, String addr, String num);
    }

    /**
     *  枪支状态
     */
    public interface IGunStatus {
        /**
         * 
         * @param id
         * @param cabAddr
         *            柜子地址
         * @param subCabAddr
         *            子柜子地址
         * @param nolist
         *            枪序列号数组
         */
        void onStatus(String id, String cabAddr, String subCabAddr,
                               ArrayList<Integer> nolist);
    }

    // ************************************************以下自己填************************************************************

    /**
     * 注册温度监听报警
     * 
     * @param id
     *            注册者id
     * @param source
     *            注册者
     */
    public void registerTemper(String id, final ITemper source) {
        if (source != null) {

            byte ordcmd = 0x21;// awenw
            byte databuf[] = null;
            // 以上自己填
            addAlwaysTask(ordcmd, databuf, id, new OnTaskReceve() {

                @Override
                public void Onreceve(Boolean flag, byte ordcmd,
                        byte[] receveData, String ID) {
                    if (flag) {// awenw
                        // TODO Auto-generated method stub
                        String temper = "20";
                        if (receveData[9] == 0x0) {
                            temper = String.valueOf(receveData[10]);
                            // 当温度超过预警时调下面方法
                            // if (receveData[10] > 50)
                        }
                        if ((byte) (receveData[4] & 0x8) == 0x0) {
                            temper = "1" + temper;// 电池
                        } else
                            temper = "0" + temper;// 市电
                        if ((receveData[4] & 0x10) == 0x10) // awenw

                        {// 门已经打开
                            if (SerialMG.TaskOrder[0] < 0x1) {
                                String addr = "0";
                                // 当门非法开了 才能调下面方法
                                temper = "1" + temper;// 非法开门
                            } else {
                                SerialMG.TaskOrder[0] = 0x2;
                                if (SerialMG.TaskOpentime[0] == 0) {
                                    SerialMG.TaskOpentime[0] = (new Date())
                                            .getTime();
                                    temper = "0" + temper;// 没有超时
                                } else if ((new Date()).getTime()
                                        - SerialMG.TaskOpentime[0] > OVERTIMER) {
                                    // 超时开门，调用超时报警
                                    temper = "2" + temper;// 超时开门
                                } else
                                    temper = "0" + temper;// 没有超时

                            }
                        } else {
                            if (SerialMG.TaskOrder[0] > 0x1)
                                SerialMG.TaskOrder[0] = 0x0;
                            temper = "0" + temper;// 门没有开
                        }
                        Log.i(TAG,"temper: "+temper);
                        source.onTemper(ID, temper);
                    }
                }
            });
        }
    }

    /**
     * 注册非正常手段开门监听报警(没有登录验证指纹门开了,没有发开锁指令，如果检测到门开了，就报异常开门报警（非正常手段开门）)
     * 
     * @param id
     *            注册者id
     * @param source
     *            注册者
     */
    public void registerBadOpen(String id, final IStoreObjectStatus source) {
        if (source != null) {

            byte ordcmd = 0x21;// awenw
            byte databuf[] = null;//
            // 以上自己填
            addAlwaysTask(ordcmd, databuf, id, new OnTaskReceve() {

                @Override
                public void Onreceve(Boolean flag, byte ordcmd,
                        byte[] receveData, String ID) {
                    // TODO Auto-generated method stub
                    // 如果成功返回,就处理返回结果
                    if (flag == true) {// awenw
                        if ((receveData[4] & 0x10) == 0x10) // awenw

                        {
                            if (SerialMG.TaskOrder[0] < 0x1) {
                                String addr = "0";
                                // 当门非法开了 才能调下面方法
                                source.onStatus(ID, addr);
                            } else {
                                SerialMG.TaskOrder[0] = 0x2;
                                if (SerialMG.TaskOpentime[0] == 0)
                                    SerialMG.TaskOpentime[0] = (new Date())
                                            .getTime();
                                else if ((new Date()).getTime()
                                        - SerialMG.TaskOpentime[0] > OVERTIMER) {
                                    // 超时开门，调用超时报警

                                }

                            }
                        } else {
                            if (SerialMG.TaskOrder[0] > 0x1)
                                SerialMG.TaskOrder[0] = 0x0;
                        }
                        // 如果成功返回,就处理返回结果
                        if ((receveData[4] & 0x20) == 0x20) // awenw
                        {
                            if (SerialMG.TaskOrder[1] < 0x1) {
                                String addr = "1";
                                // 当门非法开了 才能调下面方法
                                source.onStatus(ID, addr);
                            } else {
                                SerialMG.TaskOrder[1] = 0x2;
                                if (SerialMG.TaskOpentime[0] == 0)
                                    SerialMG.TaskOpentime[0] = (new Date())
                                            .getTime();
                                else if ((new Date()).getTime()
                                        - SerialMG.TaskOpentime[0] > OVERTIMER) {
                                    // 超时开门，调用超时报警

                                }

                            }
                        } else {
                            if (SerialMG.TaskOrder[1] > 0x1)
                                SerialMG.TaskOrder[1] = 0x0;
                        }
                    }
                }
            });
        }
    }

    /**
     * 把字节转换为二进制位//awenw
     * 
     * @param b
     * @return
     */
    public String getBinaryStrFromByte2(byte b) {
        String result = "";
        byte a = b;
        for (int i = 0; i < 8; i++) {
            result = result + (a & 1);
            a = (byte) (a >> 1);
        }
        return result;
    }

    /**
     * 注册非正常手段领枪报警(如果没有打开指定枪锁，检测到枪锁开了，就报异常领枪)
     * 
     * @param id
     *            注册者id
     * @param source
     *            注册者
     */
    public void registerBadGetGun(String id, final IStoreObjectStatus source) {
        if (source != null) {

            byte ordcmd = EXTEND_QUERRY;// awenw
            byte databuf[] = { 0x1 };// 这儿只查第一块板，实际应该根据触点是否变化来查
            // 以上自己填
            addAlwaysTask(ordcmd, databuf, id, new OnTaskReceve() {

                @Override
                public void Onreceve(Boolean flag, byte ordcmd,
                        byte[] receveData, String ID) {
                    // TODO Auto-generated method stub
                    byte gunchang = ChudianChang();
                    // fill
                    if (gunchang != 0
                            && CurGuninfo[1][0] != SerialMG.SubCabChudian[0][1])// 枪触点变化了
                    { // 有枪支被非法打开
                      // 自己控制
                        CurGuninfo[1][0] = SerialMG.SubCabChudian[0][1];// 记录上次状态
                        if ((SerialMG.SubCabChudian[0][0] | gunchang) != SerialMG.SubCabChudian[0][0]) {
                            String addr = getBinaryStrFromByte2(gunchang); // 改变的枪支才显示出来
                            // 当枪锁非法开了 才能调下面方法
                            source.onStatus(ID, addr);
                        }
                    }
                }
            });
        }
    }

    /**
     * 注册非正常手段领子弹报警
     * 
     * @param id
     *            注册者id
     * @param source
     *            注册者
     */
    public void registerBadGetAmmo(String id, final IStoreObjectStatus source) {
        if (SerialMG.TaskOrder[1] > 0x5 && source != null) {// awenw//非正常领枪和领弹用同一个方法，根据子柜类型不同来区分

            byte ordcmd = 0x21;
            byte databuf[] = null;
            // 以上自己填
            addAlwaysTask(ordcmd, databuf, id, new OnTaskReceve() {

                @Override
                public void Onreceve(Boolean flag, byte ordcmd,
                        byte[] receveData, String ID) {
                    // TODO Auto-generated method stub

                    // 自己控制
                    String addr = "0";
                    // 当锁非法开了 才能调下面方法
                    // source.onStatus(ID, addr);
                }
            });
        }
    }

    /**
     * 注册非正常手段领弹夹报警
     * 
     * @param id
     *            注册者id
     * @param source
     *            注册者
     */
    public void registerBadGetAmmoSets(String id,
            final IStoreObjectStatus source) {
        if (SerialMG.TaskOrder[1] > 0x5 && source != null) {// awenw//非正常领枪和领弹用同一个方法，根据子柜类型不同来区分
            byte ordcmd = 0x21;
            byte databuf[] = null;
            // 以上自己填
            addAlwaysTask(ordcmd, databuf, id, new OnTaskReceve() {

                @Override
                public void Onreceve(Boolean flag, byte ordcmd,
                        byte[] receveData, String ID) {
                    // TODO Auto-generated method stub

                    // 自己控制
                    String addr = "0";
                    // 当锁非法开了 才能调下面方法
                    // source.onStatus(ID, addr);
                }
            });
        }
    }

    /**
     * 超时开门监听(开门超时了)
     * 
     * @param id
     *            注册者id
     * @param source
     *            注册者
     */
    public void registerTimeOutOpen(String id, final IStoreObjectStatus source) {
        if (source != null) {

            byte ordcmd = QUERRY;
            byte databuf[] = null;
            // 以上自己填
            addAlwaysTask(ordcmd, databuf, id, new OnTaskReceve() {

                @Override
                public void Onreceve(Boolean flag, byte ordcmd,
                        byte[] receveData, String ID) {
                    // TODO Auto-generated method stub
                    if (flag == true) {// awenw
                        if (SerialMG.CabDoor[1][0] == 1
                                && SerialMG.TaskOrder[0] > 0x0) {
                            SerialMG.TaskOrder[0] = 0x2;
                            if (SerialMG.TaskOpentime[0] == 0)
                                SerialMG.TaskOpentime[0] = (new Date())
                                        .getTime();
                            else if ((new Date()).getTime()
                                    - SerialMG.TaskOpentime[0] > OVERTIMER) {
                                // 超时开门，调用超时报警
                                String addr = "0";
                                source.onStatus(ID, addr);
                            }
                        }
                    } else if (SerialMG.CabDoor[1][0] == 0) {
                        if (SerialMG.TaskOrder[0] > 0x1)
                            SerialMG.TaskOrder[0] = 0x0;

                    }
                }
            });
        }
    }

    /**
     * 触点变化监听
     * 
     * @param id
     *            注册者id
     * @param source
     *            注册者
     */
    public void registerZHUDian(String id, final IStoreObjectStatus source) {
        if (SerialMG.chudian[1] > 0x0 && source != null) {
            byte ordcmd = EXTEND_QUERRY;// awenw
            byte databuf[] = { 0x1 };
            // 以上自己填
            addAlwaysTask(ordcmd, databuf, id, new OnTaskReceve() {

                @Override
                public void Onreceve(Boolean flag, byte ordcmd,
                        byte[] receveData, String ID) {
                    // TODO Auto-generated method stub
                    if (flag)// awenw
                    {
                        // SerialMG.SubCabChudian[0][0] = (byte)
                        // (~receveData[5]);
                        String addr = getBinaryStrFromByte2(receveData[5]); // 返回触点状态
                        if (SerialMG.chudian[0] == 0x1)
                            source.onStatus(ID, addr);
                        else if (SerialMG.chudian[0] == 0x2) {
                            CurGuninfo[0][0x1] = (byte) (~receveData[5]);// 记录当前触点
                            CurGuninfo[0][0] = CurGuninfo[0][0x1];
                        }
                    }
                }
            });
        }
    }

    /**
     * 记录旧触点状态
     */
    public static void RememberStatus() {
        CurGuninfo[0][0x1] = SerialMG.SubCabChudian[0][1];
        CurGuninfo[0][0x0] = CurGuninfo[0][0x1];
        CurGuninfo[1][0x0] = CurGuninfo[0][0x1];
        CurGuninfo[2][0x0] = CurGuninfo[0][0x1];
    }

    /**
     * 检查触点变化
     * 
     * @return 返回变化后的二进制值
     */
    private byte ChudianChang() {
        byte changes = 0x0;
        if (SerialMG.chudian[0] == 0x1) {// 更新锁状态为新的状态
            changes = (byte) (CurGuninfo[0][0x1] ^ SerialMG.SubCabChudian[0][1]);
        } else {// awenw//第一次检查触点，不算异常开门
            SerialMG.chudian[0] = 0x1;
            CurGuninfo[0][0x1] = SerialMG.SubCabChudian[0][1];// 记录当前触点状态
            CurGuninfo[0][0] = SerialMG.SubCabChudian[0][0];// 记录上次触点状态
            CurGuninfo[1][0] = SerialMG.SubCabChudian[0][0];// 记录上次触点状态
            CurGuninfo[2][0] = SerialMG.SubCabChudian[0][0];// 记录上次触点状态
        }
        return changes;
    }

    /**
     * 监听领枪服务
     * 
     * @param id
     *            注册者id
     * @param source
     *            注册者
     */
    public void registerGetGun(String id, final IStoreObjectStatus source) {
        if (source != null) {

            byte ordcmd = EXTEND_QUERRY;// awenw
            byte databuf[] = { 0x1 };
            // 以上自己填
            addAlwaysTask(ordcmd, databuf, id, new OnTaskReceve() {

                @Override
                public void Onreceve(Boolean flag, byte ordcmd,
                        byte[] receveData, String ID) {
                    // TODO Auto-generated method stub
                    String addr = getBinaryStrFromByte2(ChudianChang()); // 返回触点变化状态
                    source.onStatus(ID, addr);
                }
            });
        }
    }

    /**
     * 监听领弹服务
     * 
     * @param id
     *            注册者id
     * @param source
     *            注册者
     */
    public void registerGetGunAmmo(String id, final ISubStoreObjectStatus source) {
        if (SerialMG.TaskOrder[1] > 0x5 && source != null) {

            byte ordcmd = 0x21;
            byte databuf[] = null;
            // 以上自己填
            addAlwaysTask(ordcmd, databuf, id, new OnTaskReceve() {

                @Override
                public void Onreceve(Boolean flag, byte ordcmd,
                        byte[] receveData, String ID) {
                    // TODO Auto-generated method stub

                    // 自己控制
                    String addr = "0"; // 弹的地址
                    String num = "10"; // 弹的数量
                    // 当领弹完成 才能调下面方法
                    // source.onStatus(ID, addr, num);
                }
            });
        }
    }

    /**
     * 监听领弹夹服务
     * 
     * @param id
     *            注册者id
     * @param source
     *            注册者
     */
    public void registerGetGunSet(String id, final ISubStoreObjectStatus source) {
        if (SerialMG.TaskOrder[1] > 0x5 && source != null) {

            byte ordcmd = 0x21;
            byte databuf[] = null;
            // 以上自己填
            addAlwaysTask(ordcmd, databuf, id, new OnTaskReceve() {

                @Override
                public void Onreceve(Boolean flag, byte ordcmd,
                        byte[] receveData, String ID) {
                    // TODO Auto-generated method stub

                    // 自己控制
                    String addr = "0"; // 弹夹的地址
                    String num = "10"; // 弹夹的数量
                    // 当领弹完成 才能调下面方法
                    // source.onStatus(ID, addr, num);
                }
            });
        }
    }

    /**
     * 开柜子锁
     * 
     * @param find_gunCabInfolist
     *            柜子地址编号列表
     * @param source
     */
    public void taskOpenCabLock(ArrayList<GunCabInfo> find_gunCabInfolist,
            final IStoreObjectStatus source) {
        byte ordcmd = 0x22;// awenw
        byte[] databuf = { 0x0, OpenLockTimer };
        // 以上自己填
        addTask(ordcmd, databuf, new OnTaskReceve() {

            @Override
            public void Onreceve(Boolean flag, byte ordcmd, byte[] receveData,
                    String ID) {
                // TODO Auto-generated method stub
                if (flag)// awenw
                {
                    String addr = "0"; // 门的地址
                    // 自己控制
                    source.onStatus(ID, addr);
                }
            }
        });
    }

    /**
     * 开子柜子锁
     * 
     * @param find_Sub_Cabs
     *            子柜子列表
     * @param source
     */
    /*
     * public void taskOpenSubCabLock(ArrayList<SubCabInfo> find_Sub_Cabs) { if
     * (SerialMG.TaskOrder[1] > 0x5) {// awenw
     * 
     * byte ordcmd = 0; byte databuf[] = null; // 以上自己填 addTask(ordcmd, databuf,
     * new OnTaskReceve() {
     * 
     * @Override public void Onreceve(Boolean flag, byte ordcmd, byte[]
     * receveData, String ID) { // TODO Auto-generated method stub // 自己控制 } });
     * } }
     */

    /**
     * 开物件锁
     * 
     * @param find_Store_Objects
     *            物件列表 类型有 1、 枪 2、 子弹弹药3 弹夹
     * @param source
     */
    /*
     * public void taskOpenStoreObjectLock( ArrayList<StoreObjectInfo>
     * find_Store_Objects) {
     * 
     * byte ordcmd = 0x27; byte databuf[] = { 0x1, 0x1, 0x14 }; // 以上自己填
     * addTask(ordcmd, databuf, new OnTaskReceve() {
     * 
     * @Override public void Onreceve(Boolean flag, byte ordcmd, byte[]
     * receveData, String ID) { // TODO Auto-generated method stub // 自己控制 } });
     * }
     */

    /**
     * 打开报警
     * 
     * 
     */
    public void taskOpenAlarm() {

        byte ordcmd = ALARM_CONTROL;// awenw
        byte databuf[] = { 0x1 };
        // 以上自己填
        addTask(ordcmd, databuf, new OnTaskReceve() {

            @Override
            public void Onreceve(Boolean flag, byte ordcmd, byte[] receveData,
                    String ID) {
                // TODO Auto-generated method stub
                // 自己控制
            }
        });
    }

    /**
     * 关闭报警
     * 
     */
    public void taskCloseAlarm() {

        byte ordcmd = ALARM_CONTROL;// awenw
        byte databuf[] = { 0x0 };
        // 以上自己填
        addTask(ordcmd, databuf, new OnTaskReceve() {

            @Override
            public void Onreceve(Boolean flag, byte ordcmd, byte[] receveData,
                    String ID) {
                // TODO Auto-generated method stub
                // 自己控制
            }
        });
    }

    /**
     * 开锁 new
     * 
     * @param cabAddr
     *            柜子地址
     * @param subcabAddr
     *            子柜子地址
     * @param Sub_Sequence
     *            物件装载在子柜内的序列位置（子弹的话这个项的值为NULL）子地址列表
     * @param source
     *            回调
     */
    public void taskOpenLock(String cabAddr, String subcabAddr,
            byte[] Sub_Sequence, final IStoreObjectStatus source) {
        /**
         * 亮指示灯
         */
        byte[] sndbuf = new byte[32];
        for (byte j = 0; j < 16; j++) {
            sndbuf[j * 2] = (byte) 0xff;// 绿灯
            sndbuf[j * 2 + 1] = 0x0;// 红灯
        }
        addTask((byte) 0x28, sndbuf, new OnTaskReceve() {
            @Override
            public void Onreceve(Boolean flag, byte ordcmd, byte[] receveData,
                    String ID) {
                // 不关心返回值
            }
        });
        // 亮指示灯结束
        byte ordcmd = EXTEND_LOCKOPEN;// awenw
        byte databuf[] = { 0x1, 0x0, OpenLockTimer };// 目前只处理第一块扩展板，以后再转换成完整的程序
        // 以上自己填
        // int subindex = Integer.valueOf(subcabAddr).intValue() - 1;

        for (int i = 0; i < Sub_Sequence.length; i++) {
            int k = 1;
            for (int j = 1; j < Sub_Sequence[i]; j++)
                k *= 2;
            databuf[1] += k;
        }

        addTask(ordcmd, databuf, new OnTaskReceve() {

            @Override
            public void Onreceve(Boolean flag, byte ordcmd, byte[] receveData,
                    String ID) {
                // TODO Auto-generated method stub
                if (flag)// awenw
                {
                    String addr = String.valueOf(receveData[5]); // 门的地址
                    // 自己控制
                    source.onStatus(ID, addr);
                }
            }
        });
    }

    /**
     * 监听领枪服务
     * 
     * @param id
     *            注册者id
     * @param source
     *            注册者
     */
    public void registerGetGunsChange(String id, final IGunStatus source) {
        if (source != null) {

            byte ordcmd = EXTEND_QUERRY;// awenw
            byte databuf[] = { 0x1 };
            // 以上自己填
            addAlwaysTask(ordcmd, databuf, id, new OnTaskReceve() {

                @Override
                public void Onreceve(Boolean flag, byte ordcmd,
                        byte[] receveData, String ID) {
                    // TODO Auto-generated method stub
                    /*
                     * ArrayList<Integer> nolist1 = new ArrayList<Integer>();
                     * nolist1.add(1); nolist1.add(2); source.onStatus(ID, "1",
                     * "1", nolist1);
                     */

                    byte gunchang = ChudianChang();
                    // fill
                    if (gunchang != 0
                            && CurGuninfo[0][0] != SerialMG.SubCabChudian[0][1])// 枪触点变化了
                    {
                        // 把字节变成序号
                        CurGuninfo[0][0] = SerialMG.SubCabChudian[0][1];// 保存上次触点状态
                        ArrayList<Integer> nolist = new ArrayList<Integer>();
                        byte index1 = 1;
                        for (int i = 1; i < 9; i++) {
                            if ((index1 & gunchang) == index1) {
                                nolist.add(i);
                                index1 *= 2;
                            }
                        }
                        Log.d("TaskMG", String.format("检测到领还枪信息 %d \n",
                                0xff & gunchang));
                        String cabAddr = "1";// 柜子地址
                        String subCabAddr = "1";// 子柜子地址
                        source.onStatus(ID, cabAddr, subCabAddr, nolist);
                    }
                }
            });
        }
    }

    public void registerIsDoorOpen(String id, final IDoorStatus source) {
        if (source != null) {
            byte ordcmd = QUERRY;
            byte databuf[] = { 0x1 };
            // 以上自己填
            addAlwaysTask(ordcmd, databuf, id, new OnTaskReceve() {

                @Override
                public void Onreceve(Boolean flag, byte ordcmd,
                        byte[] receveData, String ID) {
                    // TODO Auto-generated method stub
                    // source.onStatus(ID, true);
                    if (flag)// awenw
                    {
                        if (SerialMG.CabDoor[0x1][0] == 1)
                            source.onStatus(ID, true);
                        else
                            source.onStatus(ID, false);
                    }
                }
            });
        }
    }

}
