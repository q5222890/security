package com.wen.security.serial;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.winplus.serial.utils.SerialPort;
import org.winplus.serial.utils.SerialPortFinder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Date;

public class SerialMG {
    private static final String TAG = "SerialMG";
    public static byte[] TaskOrder = {0x0, 0x1};// 锁状态,默认没有第二把锁//awenw
    public static byte[][] CabDoor = new byte[30][2];// 最多支持３０个柜子
    public static long[] TaskOpentime = {0, 0};// awenw//获取开锁时间点;
    public static byte[][] SubCabChudian = new byte[16][3];// 触点状态//awenw
    public static long cmdcount = 0x0;
    public static long errcount = 0x0;
    /**
     * 下标0表示枪初始状态,1枪触点状态,2锁触点状态
     */
    public static byte[] chudian = new byte[3];// 用于枪状态报警
    //    private String path = "/dev/ttySAC3"; //友坚
    private String path = "/dev/ttyS3";  //瑞芯微
    //    private String path = "/dev/ttyAMA3";  //九鼎
//      private String path = "/dev/ttySAC0"; //
    private int baudrate = 9600; //波特率

    public SerialMG() {
        init();
    }

    public void init() {
        try {
            mSerialPort = getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            chudian[0] = 0x2;
        } catch (Exception e) {
            // DisplayError(R.string.error_security);
        }
    }

    /*
     * 发送命令到控制板
     *
     *
     * / /* * Convert byte[] to hex
     * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src byte[] data
     *
     * @return hex string
     */
    //@SuppressWarnings("unused")
    public Boolean execorder(Handler mHandler, TaskObj taskObj, byte bordaddr) {
        byte ordcmd = taskObj.ordcmd;  //命令名称
        byte[] databuf = taskObj.sndbuf; //命令辅助数据
        int size = 0;
        int ordlength = 5;
        int rsvlength = 5;
        boolean isChange = false;
        try {
            if (mSerialPort != null) {
                // mSerialPort.SetState(9600, 8, 'n', 1);
                byte[] buffer = {bordaddr};
                byte[] rsvbuffer2 = new byte[64];
                mOutputStream.write(buffer);
                mOutputStream.flush();
                byte[] rsvbuffer = new byte[64];
                Thread.sleep(60);
                /**
                 * 如果要判断是否接受完成，只有设置结束标识，或作其他特殊的处理。
                 */
                size = mInputStream.read(rsvbuffer, 0, 64);
                if (size > 0) {
                    if (ordcmd > 0x1f)
                        ordcmd = (byte) (ordcmd - 0x20);
                    // onDataReceived(rsvbuffer, size);
                    // mReception.setText("接收数据:"+bytesToHexString(rsvbuffer,
                    // size)+"\n" + mReception.getText());
                    // mSerialPort.SetState(9600, 8, 'S', 1);
                    switch (ordcmd) {
                        case 0:// 复位
                            ordlength = 1;
                            break;
                        case 1:// 查询
                            ordlength = 1;
                            rsvlength = 12;
                            break;
                        case 2:// 开大门锁,锁号,保持时间
                            TaskOrder[0] = 0x1;
                            TaskOpentime[0] = (new Date()).getTime();
                            ordlength = 3;
                            rsvlength = 7;
                            break;
                        case 3:// 定义开锁时间,锁号,保持时间
                            ordlength = 3;
                            rsvlength = 6;
                            break;
                        case 5:// 报警控制,0关闭,1打开
                            ordlength = 2;

                            break;
                        case 6:// 扩展板查询,扩展板序号
                            ordlength = 2;
                            rsvlength = 8;
                            break;
                        case 7:// 扩展板开锁,序号,锁状态,加锁时间
                            SubCabChudian[0][0] = databuf[1];// 记录当前锁的开启状态//awenw
                            ordlength = 4;
                            rsvlength = 7;
                            break;
                        case 8:// 扩展板发光,红灯,绿灯
                            ordlength = 33;
                            rsvlength = 6;
                            break;
                        case 9:// 扩展板闪光,红灯,绿灯
                            ordlength = 33;
                            rsvlength = 6;
                            break;
                        case 12:// 读电子 秤值,秤编号
                            ordlength = 2;
                            break;
                    }
                    byte[] buffer2 = new byte[ordlength + 4];
                    buffer2[0] = (byte) (bordaddr);
                    buffer2[1] = (byte) (0x0);
                    buffer2[2] = (byte) (ordlength);
                    buffer2[3] = (byte) (ordcmd + 0x20);
                    buffer2[ordlength + 3] = (byte) (buffer2[0] ^ buffer2[2] ^ buffer2[3]);
                    for (int i = 0; i < ordlength - 1; i++) {
                        buffer2[i + 4] = databuf[i];
                        buffer2[ordlength + 3] = (byte) (buffer2[ordlength + 3] ^ buffer2[i + 4]);
                    }
                    mOutputStream.write(buffer2);
                    mOutputStream.flush();
                    Thread.sleep(60);
                    /**
                     * 如果要判断是否接受完成，只有设置结束标识，或作其他特殊的处理。
                     */
                    size = mInputStream.read(rsvbuffer2, 0, rsvlength + 4);
                    if (size > 0 && size < rsvlength) //如果没有读到足够字节,再读一次
                    {
                        Thread.sleep(50);
                        size += mInputStream.read(rsvbuffer2, size, rsvlength + 4 - size);
                    }
                    if (size == rsvlength) {
                        byte bytecrc = 0;
                        for (int index = 0; index < size - 1; index++) {
                            bytecrc = (byte) (bytecrc ^ rsvbuffer2[index]);
                        }
                        if (bytecrc == rsvbuffer2[size - 1]) {
                            isChange = true;// onDataReceived(rsvbuffer2, size);
                            // rsvbuffer2[size]=(byte) size;
                            // mReception.setText("接收数据:"+bytesToHexString(rsvbuffer2,
                            // size)+"\n" + mReception.getText());
                            if (ordcmd == 0x1)// 查询命令
                            {
                                if ((rsvbuffer2[4] & 0x10) == 0x10)//改变门状态
                                    CabDoor[bordaddr][0] = 1;
                                else
                                    CabDoor[bordaddr][0] = 0;
                                if ((rsvbuffer2[4] & 0x20) == 0x20)
                                    CabDoor[bordaddr][1] = 1;
                                else
                                    CabDoor[bordaddr][1] = 0;
                                Log.d("SerialMG", String.format("comd 0x%s is OK!rsvbuffer is :%s!\n",
                                        Integer.toHexString(taskObj.ordcmd & 0xff),
                                        bytesToHexString(rsvbuffer2, rsvlength)));
                            } else if (ordcmd == 0x6)// 触点查询
                            {
                                SubCabChudian[0][2] = (byte) (~rsvbuffer2[6]);// awenw//记录锁触点
                                SubCabChudian[0][1] = (byte) (~rsvbuffer2[5]);//记录枪触点
                            }
                            if (taskObj.rec != null) {
                                taskObj.databuf = rsvbuffer2;
                                Message msg = mHandler.obtainMessage();
                                msg.obj = taskObj;
                                msg.what = TaskMG.SUCCESS;
                                mHandler.sendMessage(msg);
                            }
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            //		sendError(mHandler, taskObj);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //		sendError(mHandler, taskObj);
        } finally {
            if (isChange == false) {
//                sendError(mHandler, taskObj);  //先把错误信息注释
            }
        }
        return true;
    }

    /**
     * 发送错误消息
     *
     * @param mHandler
     * @param taskObj
     */
    public void sendError(Handler mHandler, TaskObj taskObj) {
        if (taskObj.rec != null) {
            if (taskObj.ordcmd == 0x26) {
                String str = bytesToHexString(taskObj.sndbuf, taskObj.sndbuf.length);
//                Log.e(TAG,"str: "+str);
                Log.d(TAG, String.format("comd 0x26 hase error!sndbuf%s!\n", str));
            } else {
                String str2 = Integer.toHexString(taskObj.ordcmd & 0xff);
//                Log.e(TAG,"str2: "+str2);
                Log.d(TAG, String.format("comd 0x%s hase error!\n", str2));
            }
            // taskObj.databuf = rsvbuffer2;
            Message msg = mHandler.obtainMessage();
            msg.obj = taskObj;
            msg.what = TaskMG.ERROR;
            mHandler.sendMessage(msg);
        }
    }

    /**
     * byte[]转十六进制字符串
     *
     * @param src
     * @param size
     * @return
     */
    public static String bytesToHexString(byte[] src, int size) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (size == 0 || src == null || src.length < size) {
            return "";
        }
        for (int i = 0; i < size; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public void close() {
        /*
         * if (mReadThread != null) mReadThread.interrupt();
		 */
        closeSerialPort();
        mSerialPort = null;
    }

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;

    public SerialPort getSerialPort() throws SecurityException, IOException,
            InvalidParameterException {
        if (mSerialPort == null) {
            /* Read serial port parameters */
            // SharedPreferences sp =
            // getSharedPreferences("android_serialport_api.sample_preferences",
            // MODE_PRIVATE);
            // String path = sp.getString("DEVICE", "");
            // int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

            // int baudrate = 921600;

			/* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    protected OutputStream mOutputStream;
    protected InputStream mInputStream;
    // private ReadThread mReadThread;

	/*
     * private class ReadThread extends Thread {
	 *
	 * @Override public void run() { super.run(); while (!isInterrupted()) { int
	 * size; try { byte[] buffer = new byte[1024]; if (mInputStream == null)
	 * return;
	 *//**
     * 如果要判断是否接受完成，只有设置结束标识，或作其他特殊的处理。
     */
    /*
	 * size = mInputStream.read(buffer); if (size > 0) {
	 * 
	 * // onDataReceived(buffer, size); } } catch (IOException e) {
	 * e.printStackTrace(); return; } } } }
	 */

    // protected abstract void onDataReceived(final byte[] buffer, final int
    // size);
}
