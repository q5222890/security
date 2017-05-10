package com.wen.security.finger;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.widget.ImageView;
import android.widget.TextView;

import com.wen.security.utils.ToastUtil;

import java.io.IOException;
import java.util.Arrays;

/**
 * 指纹管理中心
 * 
 * @author cloud
 * 
 */
public class FingerMG {
    private static final String TAG = "FingerMG";
    private static FingerMG instance;
    private static boolean FingerIsOpen;
    Activity myactivity;

    private FingerMG() {
        FingerIsOpen = false;
    }

    public static FingerMG getOne() {
        if (instance == null) {
            instance = new FingerMG();
        }
        return instance;
    }

    public static final short USER_PRIV = 0;

    // final int IMAGE_WIDTH = 152;
    // final int IMAGE_HEIGHT = 200;
    final String TEMPLATE_PATH = "sdcard/template.bin";

    /** Called when the activity is first created. */
    private static DevComm m_usbComm;

    int m_nParam,
            m_nImgWidth,
            m_nImgHeight,
            m_nMaxFpCount,
            m_nSecLevel,
            m_nDupCheck,
            m_nUserID;
    long m_nPassedTime;
    byte[] m_binImage, m_bmpImage;
    String m_strPost;
    boolean m_bCancel, m_bConCapture;

    public void init(Activity activity) {
        m_nMaxFpCount = 2000;
        m_binImage = new byte[1024 * 100];
        m_bmpImage = new byte[1024 * 100];
        myactivity = activity;
        // OnOpenDeviceBtn();
    }

    /**
     * 打开指纹
     * @throws IOException
     */
    public void OnOpenDeviceBtn() throws IOException {
        int[] w_nVersion = new int[1];
        try {
            if (FingerIsOpen)
                OnCloseDeviceBtn();// awenw//如果已经打开了，就先关闭再打开
        } catch(Exception e){
            e.printStackTrace();
        }
        try {

            if (m_usbComm != null)
                m_usbComm = null;
            if (m_usbComm == null) {
                m_usbComm = new DevComm(myactivity, m_IConnectionHandler);
            }
        }  catch(Exception e){
            e.printStackTrace();
        }
        try {
            if (m_usbComm != null) {
                if (!m_usbComm.IsInit()) {
                    if (m_usbComm.OpenComm()) {
                        //
                    } else {
                        // m_txtStatus.setText("Failed init usb!");
                    }
                } else {
                    if (m_usbComm.Run_Open() == DevComm.ACK_OK) {
                        if (m_usbComm.Run_GetFWVersion(w_nVersion) == DevComm.ACK_OK) {
                            // m_txtStatus.setText(String.format("Open Success!\r\nFW Version : 0x%x",
                            // (long)w_nVersion[0]));
                            // m_txtStatus.setText("准备就绪！！");
                            // EnableCtrl(true);
                            // m_btnOpenDevice.setEnabled(false);
                            // m_btnCloseDevice.setEnabled(true);
                            FingerIsOpen = true;
                        }
                        // else
                        // m_txtStatus.setText("Can not connect to device!");
                    }
                    // else
                    // m_txtStatus.setText("Can not connect to device!");
                }
            }
        }  catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 关闭指纹设备
     */
    public void OnCloseDeviceBtn() {
        m_usbComm.CloseComm();
        FingerIsOpen = false;
        // System.exit(0);
    }

    /**
     * 指纹识别结果线程
     */
    Runnable runShowStatus = new Runnable() {
        public void run() {
            if (m_strPost.equals("f_ok")) {
                if (iFingerStatus != null) {
                    iFingerStatus.didVerify(user_id, true);
                    // m_txtStatus.setText("验证成功");
                    return;
                }
            }
            if (m_strPost.equals("f_error")) {
                if (iFingerStatus != null) {
                    iFingerStatus.didVerify(user_id, false);
                    m_txtStatus.setText("验证失败,请重试");
                    return;
                }
            }
            m_txtStatus.setText(m_strPost);
        }
    };

    /**
     * 取消识别指纹
     */
    public void OnCancelBtn() {
        m_bCancel = true;
    }

    public String id = "";

    /**
     * 查看用户id
     * @return
     */
    public boolean CheckUserID() {

        // str = m_editUserID.getText().toString();

        if (id == "" || id == null) {
            // m_txtStatus.setText("Please input user id");
            return false;
        }

        try {
            m_nUserID = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            // m_txtStatus.setText("Please input correct user id(1~" +
            // m_nMaxFpCount + ")");
            return false;
        }

        if (m_nUserID > DevComm.MAX_USER_ID || m_nUserID < DevComm.MIN_USER_ID) {
            // m_txtStatus.setText(String.format("Please input correct user id(%d ~ %d)",
            // DevComm.MIN_USER_ID, DevComm.MAX_USER_ID));
            return false;
        }

        return true;
    }

    /**
     * 捕获指纹
     * @return
     */
    private int CaptureFinger() {
        int w_nRet;
        while (true) {

            if (m_bCancel)
                return DevComm.COMM_CANCELED;

            w_nRet = m_usbComm.Run_CaptureFinger(0);

            if (w_nRet == DevComm.COMM_ERROR)
                return w_nRet;

            if (w_nRet == DevComm.ACK_OK)
                break;
        }

        return DevComm.ACK_OK;
    }

    ImageView m_FpImageViewer = null;
    TextView m_txtStatus = null;

    /**
     * 注册指纹
      * @param image
     * @param txt
     */
    public void OnEnrollBtn(ImageView image, TextView txt) {
        m_FpImageViewer = image;
        m_txtStatus = txt;
        if (!m_usbComm.IsInit())
            return;

        if (!CheckUserID())
            return;

        // EnableCtrl(false);
        // m_btnCloseDevice.setEnabled(false);
        // m_btnCancel.setEnabled(true);
        m_bCancel = false;

        new Thread(new Runnable() {
            int w_nRet, w_nEnrollStep = 0;
            int[] w_nWidth = new int[1];
            int[] w_nHeight = new int[1];

            @Override
            public void run() {

                // . Check if user is exist
                w_nRet = m_usbComm
                        .Run_EnrollStart((short) m_nUserID, USER_PRIV);

                if (w_nRet != DevComm.ACK_OK) {
                    m_strPost = GetErrorMsg(w_nRet, m_nUserID);
                    m_FpImageViewer.post(runShowStatus);
                    // m_FpImageViewer.post(runEnableCtrl);
                    return;
                }

                w_nEnrollStep = 0;

                while (w_nEnrollStep < 3) {
                    m_strPost = String.format("Input finger #%d!",
                            w_nEnrollStep + 1);
                    m_FpImageViewer.post(runShowStatus);

                    // Capture
                    w_nRet = CaptureFinger();

                    if (w_nRet != DevComm.ACK_OK) {
                        m_strPost = GetErrorMsg(w_nRet, 0);
                        // m_FpImageViewer.post(runShowStatus);
                        // m_FpImageViewer.post(runEnableCtrl);
                        return;
                    }

                    m_strPost = "Release your finger.";
                    m_FpImageViewer.post(runShowStatus);

                    // Up Captured Image
                    w_nRet = m_usbComm.Run_GetImage(m_binImage);

                    if (w_nRet != DevComm.ACK_OK) {
                        m_strPost = GetErrorMsg(w_nRet, 0);
                        m_FpImageViewer.post(runShowStatus);
                        // m_FpImageViewer.post(runEnableCtrl);
                        return;
                    }

                    m_nImgWidth = DevComm.CVT_IMAGE_WIDTH;
                    m_nImgHeight = DevComm.CVT_IMAGE_HEIGHT;
                    m_FpImageViewer.post(runDrawImage);

                    // enroll
                    if (w_nEnrollStep == 0)
                        w_nRet = m_usbComm.Run_Enroll1();
                    else if (w_nEnrollStep == 1)
                        w_nRet = m_usbComm.Run_Enroll2();
                    else if (w_nEnrollStep == 2)
                        w_nRet = m_usbComm.Run_Enroll3();

                    if (w_nRet != DevComm.ACK_OK) {
                        if (w_nRet == DevComm.NACK_BAD_FINGER) {
                            m_strPost = "Bad quaility.\r\nPlease press finger again.";
                            m_FpImageViewer.post(runShowStatus);
                            SystemClock.sleep(1000);
                        } else {
                            m_strPost = GetErrorMsg(w_nRet, 0);
                            m_FpImageViewer.post(runShowStatus);
                            // m_FpImageViewer.post(runEnableCtrl);
                            return;
                        }
                    } else
                        w_nEnrollStep++;
                }

                m_strPost = "Enroll Success";
                m_FpImageViewer.post(runShowStatus);
                // m_FpImageViewer.post(runEnableCtrl);
            }
        }).start();
    }



    /**
     * 开始验证指纹
     * 
     * @param image
     * @param txt
     * @param iFingerStatus
     */
    public void OnIdentifyBtn(ImageView image, TextView txt,
            IFingerStatus iFingerStatus) {
        this.iFingerStatus = iFingerStatus;
        m_FpImageViewer = image;
        m_txtStatus = txt;
        if (!m_usbComm.IsInit())
            return;
        try {
            OnOpenDeviceBtn();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // EnableCtrl(false);
        // m_btnCloseDevice.setEnabled(false);
        // m_btnCancel.setEnabled(true);

        m_bCancel = false;

        // m_strPost = "";

        new Thread(new Runnable() {
            int w_nRet;
            long w_nTime;
            short[] w_wUserID = new short[1];

            @Override
            public void run() {

                while (true) {
                    /*
                     * if (m_strPost.isEmpty()) m_strPost =
                     * "Input your finger."; else m_strPost = m_strPost +
                     * "\r\nInput your finger.";
                     * m_FpImageViewer.post(runShowStatus);
                     */

                    // Capture
                    w_nRet = CaptureFinger();

                    if (w_nRet != DevComm.ACK_OK) {
                        m_strPost = GetErrorMsg(w_nRet, 0);
                        m_FpImageViewer.post(runShowStatus);
                        // m_FpImageViewer.post(runEnableCtrl);
                        return;
                    }

                    // m_strPost = "Release your finger.";
                    // m_FpImageViewer.post(runShowStatus);

                    // Up Cpatured Image
                    w_nRet = m_usbComm.Run_GetImage(m_binImage);

                    if (w_nRet != DevComm.ACK_OK) {
                        m_strPost = GetErrorMsg(w_nRet, 0);
                        m_FpImageViewer.post(runShowStatus);
                        // m_FpImageViewer.post(runEnableCtrl);
                        return;
                    }

                    m_nImgWidth = DevComm.CVT_IMAGE_WIDTH;
                    m_nImgHeight = DevComm.CVT_IMAGE_HEIGHT;
                    m_FpImageViewer.post(runDrawImage);

                    // Identify
                    w_nTime = SystemClock.elapsedRealtime();
                    w_nRet = m_usbComm.Run_Identify(w_wUserID);
                    w_nTime = SystemClock.elapsedRealtime() - w_nTime;

                    if (w_nRet != DevComm.ACK_OK) {
                        m_strPost = GetErrorMsg(w_nRet, 0);
                        m_FpImageViewer.post(runShowStatus);

                        if (w_nRet == DevComm.COMM_ERROR) {
                            // m_FpImageViewer.post(runEnableCtrl);
                            return;
                        }
                        continue;
                    }
                    m_strPost = "f_ok";
                    /**
                     * 获取指纹id,需要转换为用户ID//awenw
                     */
                    user_id = (int)(w_wUserID[0] & 0x0000FFFF);
                    // m_strPost =
                    // String.format("ID = %d : Identify OK\r\nMatch Time = %dms",
                    // ((int)w_wUserID[0] & 0x0000FFFF) , w_nTime);
                    m_FpImageViewer.post(runShowStatus);
                }
            }
        }).start();
    }

    /**
     * 删除所有指纹
     */
    public void OnDeleteAllBtn(){
        int  w_nRet;
        
        if (!m_usbComm.IsInit())
            return;

        w_nRet = m_usbComm.Run_DeleteAll();
        
        if (w_nRet != DevComm.ACK_OK)
        {
           // m_txtStatus.setText(GetErrorMsg(w_nRet, 0));
            return;
        }

    }

    /**
     * 设置模版
     * @param fingerID
     * @param w_pTemplate
     */
    public void OnSetTemplate(short fingerID,byte[] w_pTemplate) {
        int w_nRet;
        
        // Check USB Connection
        if (!m_usbComm.IsInit())
            return;

        // Check User ID
        if (!CheckUserID())
            return;

        // Set Template
        w_nRet = m_usbComm.Run_SetTemplate(fingerID, USER_PRIV,
                w_pTemplate);

        // Check Return Value
        if (w_nRet != DevComm.ACK_OK) {
          //  m_txtStatus.setText(GetErrorMsg(w_nRet, 0));
            return;
        }

//        m_txtStatus.setText(String.format(
//                "Result : Set Template Success.\r\nUserID = %d", m_nUserID));
    }

    int user_id = 0;
    IFingerStatus iFingerStatus;

    /**
     * 验证指纹
     * @param image
     * @param txt
     * @param iFingerStatus
     */
    public void OnVerifyBtn(ImageView image, TextView txt,
            IFingerStatus iFingerStatus) {
        this.iFingerStatus = iFingerStatus;
        m_FpImageViewer = image;
        m_txtStatus = txt;

        if (!m_usbComm.IsInit())
            return;

        if (!CheckUserID())
            return;

        // EnableCtrl(false);
        // m_btnCloseDevice.setEnabled(false);
        // m_btnCancel.setEnabled(true);
        m_bCancel = false;

        new Thread(new Runnable() {
            int w_nRet;
            long w_nTime;

            @Override
            public void run() {

                // Check enrolled
                w_nRet = m_usbComm.Run_CheckEnrolled((short) m_nUserID);

                if (w_nRet != DevComm.ACK_OK) {
                    m_strPost = GetErrorMsg(w_nRet, m_nUserID);
                    m_FpImageViewer.post(runShowStatus);
                    // m_FpImageViewer.post(runEnableCtrl);
                    return;
                }

                // m_strPost = String.format("请管理员按下手指");
                // m_FpImageViewer.post(runShowStatus);

                // Capture
                w_nRet = CaptureFinger();

                if (w_nRet != DevComm.ACK_OK) {
                    m_strPost = GetErrorMsg(w_nRet, 0);
                    m_FpImageViewer.post(runShowStatus);
                    // m_FpImageViewer.post(runEnableCtrl);
                    return;
                }

                // m_strPost = "Release your finger.";
                // m_FpImageViewer.post(runShowStatus);

                // Up Captured Image
                w_nRet = m_usbComm.Run_GetImage(m_binImage);

                if (w_nRet != DevComm.ACK_OK) {
                    m_strPost = GetErrorMsg(w_nRet, 0);
                    m_FpImageViewer.post(runShowStatus);
                    // m_FpImageViewer.post(runEnableCtrl);
                    return;
                }

                m_nImgWidth = DevComm.CVT_IMAGE_WIDTH;
                m_nImgHeight = DevComm.CVT_IMAGE_HEIGHT;
                m_FpImageViewer.post(runDrawImage);

                // Verify
                w_nTime = SystemClock.elapsedRealtime();
                w_nRet = m_usbComm.Run_Verify((short) m_nUserID);
                w_nTime = SystemClock.elapsedRealtime() - w_nTime;

                if (w_nRet != DevComm.ACK_OK) {
                    m_strPost = GetErrorMsg(w_nRet, m_nUserID);
                    m_FpImageViewer.post(runShowStatus);
                    // m_FpImageViewer.post(runEnableCtrl);
                    return;
                }
                m_strPost = "f_ok";
                // m_strPost =
                // String.format("ID = %d : Verify OK\r\nMatch Time = %dms",
                // m_nUserID,w_nTime);
                m_FpImageViewer.post(runShowStatus);
                // m_FpImageViewer.post(runEnableCtrl);
            }
        }).start();
    }

    /**
     * 指纹验证接口
     */
    public interface IFingerStatus {
        void didVerify(int id, boolean success);
    }

    /**
     * 打开指纹设备
     */
    void OpenDevice() {
        int[] w_nVersion = new int[1];

        if (m_usbComm.Run_Open() == DevComm.ACK_OK) {
            if (m_usbComm.Run_GetFWVersion(w_nVersion) == DevComm.ACK_OK) {
                // m_txtStatus.setText(String.format("准备就绪", w_nVersion[0]));
                // EnableCtrl(true);
                // m_btnOpenDevice.setEnabled(false);
                // m_btnCloseDevice.setEnabled(true);
            }
            // else
            // m_txtStatus.setText("Can not connect to device!");
        }
        // else
        // m_txtStatus.setText("Can not connect to device!");
    }

    /**
     * usb连接状态
     */
    private final IUsbConnState m_IConnectionHandler = new IUsbConnState() {
        @Override
        public void onUsbConnected() {
            OpenDevice();  //usb连接 开启指纹设备
        }

        @Override
        public void onUsbPermissionDenied() {
            //没有USB权限
//             m_txtStatus.setText("Permission denied!");
            ToastUtil.showShort("Permission denied!");
        }

        @Override
        public void onDeviceNotFound() {
//             m_txtStatus.setText("Can not find usb device!");
            ToastUtil.showShort("Can not find usb device!");
        }
    };

    /**
     * 获取错误信息
     * @param nErrorCode
     * @param nPos
     * @return
     */
    private String GetErrorMsg(int nErrorCode, int nPos) {
        String str = new String("");

        if (nErrorCode > DevComm.NACK_NONE) {
            switch (nErrorCode) {
            case DevComm.NACK_TIMEOUT:
                str = "Time Out!";  //超时
                break;
            case DevComm.NACK_INVALID_BAUDRATE:
                str = "Invalid baudrate";  //波特率无效
                break;
            case DevComm.NACK_INVALID_POS:
                str = "Invalid ID";
                break;
            case DevComm.NACK_IS_NOT_USED:
                str = String.format("ID = %d: is not used!", nPos);
                break;
            case DevComm.NACK_IS_ALREADY_USED:
                str = String.format("ID = %d: is already used!", nPos);
                break;
            case DevComm.NACK_COMM_ERR:
                str = "Communication error!";  //通讯异常
                break;
            case DevComm.NACK_VERIFY_FAILED:
                str = String.format("ID = %d: Verify Fail!", nPos);
                break;
            case DevComm.NACK_IDENTIFY_FAILED:
                str = "Identify fail!";
                break;
            case DevComm.NACK_DB_IS_FULL:
                str = "Database is full";
                break;
            case DevComm.NACK_DB_IS_EMPTY:
                str = "Database is empty";
                break;
            case DevComm.NACK_TURN_ERR:
                str = "The order of enrollment is incorrect!";
                break;
            case DevComm.NACK_BAD_FINGER:
                str = "Bad finger!";
                break;
            case DevComm.NACK_ENROLL_FAILED:
                str = "The enrollment is failed!";
                break;
            case DevComm.NACK_IS_NOT_SUPPORTED:
                str = "The command is not supported";
                break;
            case DevComm.NACK_DEV_ERR:
                str = "The device error!";
                break;
            case DevComm.NACK_CAPTURE_CANCELED:
                str = "Canceled!";
                break;
            case DevComm.NACK_INVALID_PARAM:
                str = "Invalid Parameter!";
                break;
            case DevComm.COMM_ERROR:
                str = "Can not connect to device!";
                break;
            case DevComm.COMM_PC_TIMEOUT:
                str = "Time Out!";
                break;
            case DevComm.COMM_CANCELED:
                str = "Canceled.";
                break;
            default:
                str = "Unknown Error";
                break;
            }
        } else if (nErrorCode < DevComm.GD_MAX_RECORD_COUNT) {
            str = String.format("Duplicated with %d ID!", nErrorCode);
        } else {
            str = "Unknown Error2";
        }

        return "f_error";
    }

    /**
     * 绘制指纹图片线程
     */
    Runnable runDrawImage = new Runnable() {
        public void run() {
            int nSize;

            MakeBMPBuf(m_binImage, m_bmpImage, m_nImgWidth, m_nImgHeight);

            if ((m_nImgWidth % 4) != 0)
                nSize = m_nImgWidth + (4 - (m_nImgWidth % 4));
            else
                nSize = m_nImgWidth;

            nSize = 1078 + nSize * m_nImgHeight;

            // DebugManage.WriteBmp(m_bmpImage, nSize);

            Bitmap image = BitmapFactory.decodeByteArray(m_bmpImage, 0, nSize);

            m_FpImageViewer.setImageBitmap(image);
        }
    };

    /**
     * 开辟图片缓冲区
     * @param Input
     * @param Output
     * @param iImageX
     * @param iImageY
     */
    private void MakeBMPBuf(byte[] Input, byte[] Output, int iImageX,
            int iImageY) {

        byte[] w_bTemp = new byte[4];
        byte[] head = new byte[1078];
        byte[] head2 = {
        /***************************/
                // file header
                0x42, 0x4d,// file type
                // 0x36,0x6c,0x01,0x00, //file size***
                0x0, 0x0, 0x0, 0x00, // file size***
                0x00, 0x00, // reserved
                0x00, 0x00,// reserved
                0x36, 0x4, 0x00, 0x00,// head byte***
                /***************************/
                // infoheader
                0x28, 0x00, 0x00, 0x00,// struct size

                // 0x00,0x01,0x00,0x00,//map width***
                0x00, 0x00, 0x0, 0x00,// map width***
                // 0x68,0x01,0x00,0x00,//map height***
                0x00, 0x00, 0x00, 0x00,// map height***

                0x01, 0x00,// must be 1
                0x08, 0x00,// color count***
                0x00, 0x00, 0x00, 0x00, // compression
                // 0x00,0x68,0x01,0x00,//data size***
                0x00, 0x00, 0x00, 0x00,// data size***
                0x00, 0x00, 0x00, 0x00, // dpix
                0x00, 0x00, 0x00, 0x00, // dpiy
                0x00, 0x00, 0x00, 0x00,// color used
                0x00, 0x00, 0x00, 0x00,// color important
        };

        int i, j, num, iImageStep;

        Arrays.fill(w_bTemp, (byte) 0);

        System.arraycopy(head2, 0, head, 0, head2.length);

        if ((iImageX % 4) != 0)
            iImageStep = iImageX + (4 - (iImageX % 4));
        else
            iImageStep = iImageX;

        num = iImageX;
        head[18] = (byte) (num & (byte) 0xFF);
        num = num >> 8;
        head[19] = (byte) (num & (byte) 0xFF);
        num = num >> 8;
        head[20] = (byte) (num & (byte) 0xFF);
        num = num >> 8;
        head[21] = (byte) (num & (byte) 0xFF);

        num = iImageY;
        head[22] = (byte) (num & (byte) 0xFF);
        num = num >> 8;
        head[23] = (byte) (num & (byte) 0xFF);
        num = num >> 8;
        head[24] = (byte) (num & (byte) 0xFF);
        num = num >> 8;
        head[25] = (byte) (num & (byte) 0xFF);

        j = 0;
        for (i = 54; i < 1078; i = i + 4) {
            head[i] = head[i + 1] = head[i + 2] = (byte) j;
            head[i + 3] = 0;
            j++;
        }

        System.arraycopy(head, 0, Output, 0, 1078);

        if (iImageStep == iImageX) {
            for (i = 0; i < iImageY; i++) {
                System.arraycopy(Input, i * iImageX, Output,
                        1078 + i * iImageX, iImageX);
            }
        } else {
            iImageStep = iImageStep - iImageX;

            for (i = 0; i < iImageY; i++) {
                System.arraycopy(Input, i * iImageX, Output, 1078 + i
                        * (iImageX + iImageStep), iImageX);
                System.arraycopy(w_bTemp, 0, Output, 1078 + i
                        * (iImageX + iImageStep) + iImageX, iImageStep);
            }
        }
    }
}
