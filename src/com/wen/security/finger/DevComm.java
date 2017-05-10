package com.wen.security.finger;

import android.app.Activity;
import android.content.Context;

import java.util.Arrays;


/**
 * 指纹执行函数和返回值
 */
public class DevComm {

	public	static final int USB_BLOCK_SIZE 		= 65536;
	
	public	static final int GD_RECORD_SIZE			= 498;
	public	static final int GD_MAX_RECORD_COUNT	= 2000;
	public	static final int ID_NOTE_SIZE			= 64;
	public	static final int MODULE_SN_LEN			= 16;

	public static final int CVT_IMAGE_WIDTH			= 256;
	public static final int CVT_IMAGE_HEIGHT		= 256;
	
	private static final int SCSI_TIMEOUT			= 5000; //ms
	private static final int COMM_SLEEP_TIME		= 40;	//ms	
		
	private static final int GD_CMD_PACKET_LEN		= (12);
	private static final int GD_ACK_PACKET_LEN		= (12);	
	private static final int RCM_DATA_OFFSET		= 10;
	
	private static final int SB_OEM_PKT_SIZE		= 12;
	private static final int SB_OEM_HEADER_SIZE		= 2;
	private static final int SB_OEM_DEV_ID_SIZE		= 2;
	private static final int SB_OEM_CHK_SUM_SIZE	= 2;	
	
	/***************************************************************************/
	/***************************************************************************/
	private static final short CMD_PREFIX_CODE		= (short)0xAA55;
	private static final short DATA_PREFIX_CODE		= (short)0xA55A;

	/***************************************************************************
	* System Code (0x0000 ~ 0x001F, 0x0000 : Reserved)
	***************************************************************************/
	private static final short CMD_NONE						= 0x0000;
	private static final short CMD_OPEN_CODE				= 0x0001;
	private static final short CMD_CLOSE_CODE				= 0x0002;
	private static final short CMD_USB_INTERNAL_CHECK_CODE	= 0x0003;
	private static final short CMD_GET_FW_VERSION_CODE		= 0x0004;
	private static final short CMD_ENTER_SLEEP_CODE			= 0x0005;
	private static final short CMD_SET_SECURITY_LEVEL_CODE	= 0x0006;
	private static final short CMD_CHANGE_BAUDRATE_CODE		= 0x0011;
	private static final short CMD_GET_DEVICE_ID_CODE		= 0x0012;
	private static final short CMD_CHANGE_DEVICE_ID_CODE	= 0x0013;
	private static final short CMD_GET_ENROLL_COUNT_CODE	= 0x0020;
	private static final short CMD_CHECK_ENROLLED_CODE		= 0x0021;
	private static final short CMD_ENROLL_START_CODE		= 0x0022;
	private static final short CMD_ENROLL1_CODE				= 0x0023;
	private static final short CMD_ENROLL2_CODE				= 0x0024;
	private static final short CMD_ENROLL3_CODE				= 0x0025;
	private static final short CMD_IS_PRESS_FINGER_CODE		= 0x0026;
	private static final short CMD_GET_ALL_USER_ID_CODE		= 0x0032;
	private static final short CMD_SET_USER_PRIV_CODE		= 0x0033;
	private static final short CMD_DELETE_ID_CODE			= 0x0040;
	private static final short CMD_DELETE_ALL_CODE			= 0x0041;
	private static final short CMD_VERIFY_CODE				= 0x0050;
	private static final short CMD_IDENTIFY_CODE			= 0x0051;
	private static final short CMD_VERIFY_TEMPLATE_CODE		= 0x0052;
	private static final short CMD_IDENTIFY_TEMPLATE_CODE	= 0x0053;
	private static final short CMD_VERIFY_TEMPLATE2_CODE	= 0x0054;
	private static final short CMD_CAPTURE_FINGER_CODE		= 0x0060;
	private static final short CMD_CAPTURE_CANCEL_CODE		= 0x0061;
	private static final short CMD_GET_IMAGE_CODE			= 0x0062;
	private static final short CMD_EXTRACT_TEMPLATE_CODE	= 0x0065;
	private static final short CMD_SET_IMAGE_CODE			= 0x0066;
	private static final short CMD_GET_TEMPLATE_CODE		= 0x0070;
	private static final short CMD_SET_TEMPLATE_CODE		= 0x0071;
	private static final short CMD_AUTO_ADJUST_CODE			= 0x0080;
	private static final short CMD_SET_DUP_CHECK_CODE		= 0x0081;
	private static final short CMD_SET_LED_STATE_CODE		= 0x0082;
	
	/***************************************************************************
	 * Error Code	  
	***************************************************************************/	
	public static final int NACK_NONE				= 0x1000;	// For PC
	public static final int NACK_TIMEOUT			= 0x1001;
	public static final int NACK_INVALID_BAUDRATE	= 0x1002;
	public static final int NACK_INVALID_POS		= 0x1003;
	public static final int NACK_IS_NOT_USED		= 0x1004;
	public static final int NACK_IS_ALREADY_USED	= 0x1005;
	public static final int NACK_COMM_ERR			= 0x1006;
	public static final int NACK_VERIFY_FAILED		= 0x1007;
	public static final int NACK_IDENTIFY_FAILED	= 0x1008;
	public static final int NACK_DB_IS_FULL			= 0x1009;
	public static final int NACK_DB_IS_EMPTY		= 0x100A;
	public static final int NACK_TURN_ERR			= 0x100B;
	public static final int NACK_BAD_FINGER			= 0x100C;
	public static final int NACK_ENROLL_FAILED		= 0x100D;
	public static final int NACK_IS_NOT_SUPPORTED	= 0x100E;
	public static final int NACK_DEV_ERR			= 0x100F;
	public static final int NACK_CAPTURE_CANCELED	= 0x1010;
	public static final int NACK_INVALID_PARAM		= 0x1011;

	public static final int COMM_ERROR				= 0x1020;	// For PC
	public static final int COMM_PC_TIMEOUT			= 0x1021;	// For PC
	public static final int COMM_CANCELED			= 0x1022;	// For PC
	public static final int ACK_OK					= 0x0000;	// For PC

	/***************************************************************************
	* Acknowledge Code
	***************************************************************************/
	public static final int RES_ACK					= 0x30;
	public static final int RES_NACK				= 0x31;
	
	/***************************************************************************
	* Device ID, Security Level
	***************************************************************************/
	public static final int MIN_USER_ID				= (0);
	public static final int MAX_USER_ID				= (0xFFFF);
	
	public static final int MIN_DEVICE_ID				= 1;
	public static final int MAX_DEVICE_ID				= 255;
	public static final int MIN_SECURITY_LEVEL			= 1;
	public static final int MAX_SECURITY_LEVEL			= 52;
	
	public static final int GD_TEMPLATE_NOT_EMPTY	= 0x01;
	public static final int GD_TEMPLATE_EMPTY		= 0x00;  	
    
	public static final short COMM_DEVICE_ID			= 1;
	
    //--------------- For Usb Communication ------------//
    public int		m_nPacketSize;
    public byte		m_bySrcDeviceID = 1, m_byDstDeviceID = 1;
    //public byte[]	m_abyPacket = new byte[64*1024];
    //public byte[]	m_abyTmpBuf = new byte[USB_BLOCK_SIZE];
    public byte[]	m_abyPacket = new byte[100*1024];
    public byte[]	m_abyTmpBuf = new byte[100*1024];    
    //--------------------------------------------------//

    private final Context mApplicationContext;
    private Activity    m_parentAcitivity;
//    private static final int VID = 0x2009;
//    private static final int PID = 0x7638;
	private static final int VID = 0x2109;
	private static final int PID = 0x7638;
    private UsbController   m_usbBase;

    public DevComm(Activity parentActivity, IUsbConnState usbConnState){
    	
    	DebugManage.DeleteLog();
    	
        m_parentAcitivity = parentActivity;
        mApplicationContext = parentActivity.getApplicationContext();
		//usb控制器
        m_usbBase = new UsbController(parentActivity, usbConnState, VID, PID);
    }

    public boolean IsInit(){
        return m_usbBase.IsInit();
    }

    public boolean  OpenComm(){
        m_usbBase.init();
        return true;
    }

    public boolean  CloseComm(){
        m_usbBase.uninit();
        return true;
    }

    /************************************************************************/
    /************************************************************************/
    int	Run_Open()
    {
    	boolean	w_bRet;
    	    	
    	InitCmdPacket(CMD_OPEN_CODE, COMM_DEVICE_ID, 0);
    	
    	w_bRet = USB_SendPacket(CMD_OPEN_CODE);
    	
    	if(!w_bRet)
    		return COMM_ERROR;
    	
    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();
    	
    	return ACK_OK;
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_Close()
    {
    	boolean	w_bRet;
    	
    	InitCmdPacket(CMD_OPEN_CODE, COMM_DEVICE_ID, 0);

    	w_bRet = USB_SendPacket(CMD_CLOSE_CODE);
    	
    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();
    	
    	return ACK_OK;
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_UsbIternalCheck(int p_nParamIndex, int[] p_pnParamValue)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_USB_INTERNAL_CHECK_CODE, COMM_DEVICE_ID, 0);

    	w_bRet = USB_SendPacket(CMD_USB_INTERNAL_CHECK_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	if (GetResParameterVal() != 0x55) //#define USB_DEVICE_MARK 0x55
    		return COMM_ERROR;

    	return ACK_OK;
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_GetFWVersion(int[] p_pnFWVersion)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_GET_FW_VERSION_CODE, COMM_DEVICE_ID, 0);

    	w_bRet = USB_SendPacket(CMD_GET_FW_VERSION_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	p_pnFWVersion[0] = GetResParameterVal();

    	return ACK_OK;
    }    
    /************************************************************************/
    /************************************************************************/
    int	Run_SetSecurityLevel(int p_nNewLevel, int[] p_pnOldLevel)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_SET_SECURITY_LEVEL_CODE, COMM_DEVICE_ID, p_nNewLevel);

    	w_bRet = USB_SendPacket(CMD_SET_SECURITY_LEVEL_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	p_pnOldLevel[0] =  GetResParameterVal();

    	return ACK_OK;    	
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_ChangeBaudrate(int p_nBaudrateIdx)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_CHANGE_BAUDRATE_CODE, COMM_DEVICE_ID, p_nBaudrateIdx);

    	w_bRet = USB_SendPacket(CMD_CHANGE_BAUDRATE_CODE);
    	
    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	return ACK_OK;
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_GetDeviceID(int[] p_pnDeviceID)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_GET_DEVICE_ID_CODE, COMM_DEVICE_ID, 0);

    	w_bRet = USB_SendPacket(CMD_GET_DEVICE_ID_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	p_pnDeviceID[0] = GetResParameterVal();

    	return ACK_OK;
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_ChangeDeviceID(int p_nDeviceID)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_CHANGE_DEVICE_ID_CODE, COMM_DEVICE_ID, p_nDeviceID);

    	w_bRet = USB_SendPacket(CMD_CHANGE_DEVICE_ID_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	return ACK_OK;
    }
    /************************************************************************/
    /************************************************************************/
    int Run_GetEnrollCount(int[] p_pnEnrollCount)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_GET_ENROLL_COUNT_CODE, COMM_DEVICE_ID, 0);

    	w_bRet = USB_SendPacket(CMD_GET_ENROLL_COUNT_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	p_pnEnrollCount[0] = GetResParameterVal();

    	return ACK_OK;
    }
	/************************************************************************
	 * 检查是否被注册
	 */
    /************************************************************************/
    int Run_CheckEnrolled(short p_wID)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_CHECK_ENROLLED_CODE, COMM_DEVICE_ID, p_wID);

    	w_bRet = USB_SendPacket(CMD_CHECK_ENROLLED_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	return ACK_OK;
    }
	/************************************************************************
	 * 开始注册
	 */
    /************************************************************************/
    int Run_EnrollStart(short p_wID, short p_wPriv)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_ENROLL_START_CODE, COMM_DEVICE_ID, MAKELONG(p_wID, p_wPriv));

    	w_bRet = USB_SendPacket(CMD_ENROLL_START_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	return ACK_OK;
    }
	/************************************************************************
	 * 注册1
	 */
    /************************************************************************/
    int	Run_Enroll1()
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_ENROLL1_CODE, COMM_DEVICE_ID, 1);

    	w_bRet = USB_SendPacket(CMD_ENROLL1_CODE);
    	
    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	return ACK_OK;
    }
	/************************************************************************
	 * 注册2
	 */
    /************************************************************************/
    int Run_Enroll2()
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_ENROLL2_CODE, COMM_DEVICE_ID, 2);

    	w_bRet = USB_SendPacket(CMD_ENROLL2_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	return ACK_OK;
    }
	/************************************************************************
	 * 注册3
	 */
    /************************************************************************/
    int	Run_Enroll3()
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_ENROLL3_CODE, COMM_DEVICE_ID, 3);

    	w_bRet = USB_SendPacket(CMD_ENROLL3_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	return ACK_OK;
    }
    /************************************************************************/
    /************************************************************************/
    int Run_IsPressFinger(byte[] p_pbyState)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_IS_PRESS_FINGER_CODE, COMM_DEVICE_ID, 0);

    	w_bRet = USB_SendPacket(CMD_IS_PRESS_FINGER_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	p_pbyState[0] = (byte)GetResParameterVal();

    	return ACK_OK;
    }
    /************************************************************************/
    /************************************************************************/
    int Run_DeleteID(short p_wID)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_DELETE_ID_CODE, COMM_DEVICE_ID, p_wID);

    	w_bRet = USB_SendPacket(CMD_DELETE_ID_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	return ACK_OK;	
    }
	/************************************************************************
	 * 清除所有
	 */
    /************************************************************************/
    int	Run_DeleteAll()
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_DELETE_ALL_CODE, COMM_DEVICE_ID, 0);

    	w_bRet = USB_SendPacket(CMD_DELETE_ID_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	return ACK_OK;	
    }
	/************************************************************************
	 *验证
	 *
    /************************************************************************/
    int	Run_Verify(short p_wUserID)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_VERIFY_CODE, COMM_DEVICE_ID, p_wUserID);

    	w_bRet = USB_SendPacket(CMD_VERIFY_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	return ACK_OK;	
    }
	/************************************************************************
	 * 识别指纹
	 */
    /************************************************************************/
    int	Run_Identify(short[] p_pwMatchedUserID)
    {
    	boolean	w_bRet;
    	
    	InitCmdPacket(CMD_IDENTIFY_CODE, COMM_DEVICE_ID, 0);
    	
    	w_bRet = USB_SendPacket(CMD_IDENTIFY_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;
    	
    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();
    	
    	p_pwMatchedUserID[0] = (short)GetResParameterVal();

    	return ACK_OK;	
    }
	/************************************************************************
	 * 捕获指纹
	 */
    /************************************************************************/
    int	Run_CaptureFinger(int	p_nTimeOut)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_CAPTURE_FINGER_CODE, COMM_DEVICE_ID, p_nTimeOut);

    	w_bRet = USB_SendPacket(CMD_CAPTURE_FINGER_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	return ACK_OK;	
    }
	/************************************************************************
	 * 获取图像
	 */
    /************************************************************************/
    int	Run_GetImage(byte[] p_pImageBuf)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_GET_IMAGE_CODE, COMM_DEVICE_ID, 0);

    	w_bRet = USB_SendPacket(CMD_GET_IMAGE_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;
    	
    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	// Receive Image Data
    	w_bRet = USB_ReceiveDataPacket(CMD_GET_IMAGE_CODE, CVT_IMAGE_WIDTH*CVT_IMAGE_HEIGHT);

    	if (!w_bRet)
    		return COMM_ERROR;

    	System.arraycopy(m_abyPacket, 4, p_pImageBuf, 0, 256*256);

    	return ACK_OK;
    }

    /************************************************************************
	 *  自动调整
	 */
    /************************************************************************/
    int	Run_AutoAdjust()
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_AUTO_ADJUST_CODE, COMM_DEVICE_ID, 0);

    	w_bRet = USB_SendPacket(CMD_AUTO_ADJUST_CODE);
    	
    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	return ACK_OK;
    }

    /************************************************************************
	 * 获取模版
	 *************************************************************************/
    int Run_GetTemplate(short p_nUserID, byte[] p_pTemplateBuf)
    {
        boolean w_bRet;

        // Make Packet
        InitCmdPacket(CMD_GET_TEMPLATE_CODE, COMM_DEVICE_ID, p_nUserID);

        // Send Packet
        w_bRet = USB_SendPacket(CMD_GET_TEMPLATE_CODE);

        // Check Return Value
        if (!w_bRet)
            return COMM_ERROR;
        if (GetResResponseVal() != RES_ACK)
            return GetResParameterVal();

        // Receive Template Data
        w_bRet = USB_ReceiveDataPacket(CMD_GET_TEMPLATE_CODE, GD_RECORD_SIZE);

        // Check Return Value
        if (!w_bRet)
            return COMM_ERROR;

        // Set Template
        System.arraycopy(m_abyPacket, 4, p_pTemplateBuf, 0, GD_RECORD_SIZE);

        // Return
        return ACK_OK;
    }
    
    /************************************************************************/
	/**
	 * 设置模版
	 */
    /************************************************************************/
    int Run_SetTemplate(short p_nUserID, short p_nPriv, byte[] p_pTemplateBuf)
    {
        boolean w_bRet;

        // Make Packet
        InitCmdPacket(CMD_SET_TEMPLATE_CODE, COMM_DEVICE_ID, MAKELONG(p_nUserID, p_nPriv));

        // Send Packet
        w_bRet = USB_SendPacket(CMD_SET_TEMPLATE_CODE);

        // Check Return Value
        if (!w_bRet)
            return COMM_ERROR;
        if (GetResResponseVal() != RES_ACK)
            return GetResParameterVal();

        // Send Template Data
        InitDataPacket(CMD_SET_TEMPLATE_CODE, COMM_DEVICE_ID, p_pTemplateBuf, GD_RECORD_SIZE);
        w_bRet = USB_SendDataPacket(CMD_SET_TEMPLATE_CODE);

        // Check Return Value
        if (!w_bRet)
            return COMM_ERROR;
        if (GetResResponseVal() != RES_ACK)
            return GetResParameterVal();

        // Return
        return ACK_OK;
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_SetDupCheck(int p_nNewDupCheck, int[] p_pnOldDupCheck)
    {
    	boolean	w_bRet;

    	InitCmdPacket(CMD_SET_DUP_CHECK_CODE, COMM_DEVICE_ID, p_nNewDupCheck);

    	w_bRet = USB_SendPacket(CMD_SET_DUP_CHECK_CODE);

    	if (!w_bRet)
    		return COMM_ERROR;

    	if (GetResResponseVal() != RES_ACK)
    		return GetResParameterVal();

    	p_pnOldDupCheck[0] = GetResParameterVal();

    	return ACK_OK;
    }   
              
    public  boolean GetDeviceInformation(String[] deviceInfo)
    {
        int[]  w_nRecvLen = new int[1];
        byte[] w_abyPCCmd = new byte[6];
        byte[] w_abyData = new byte[32];

        String  w_strTmp;
        boolean w_bRet;

        Arrays.fill(w_abyPCCmd, (byte) 0);

        w_abyPCCmd[2] = 0x04;

        w_bRet = SendPackage(w_abyPCCmd, w_abyData);

        //Toast.makeText(mApplicationContext, "GetDeviceInformation, SendPackage ret = " + w_bRet, Toast.LENGTH_SHORT).show();

        if (!w_bRet)
        {
            return  false;
        }

        w_bRet = RecvPackage(w_abyData, w_nRecvLen);

        //Toast.makeText(mApplicationContext, "GetDeviceInformation, RecvPackage : " + w_bRet, Toast.LENGTH_SHORT).show();

        if (!w_bRet)
        {
            return  false;
        }

        w_strTmp = new String(w_abyData);
        deviceInfo[0] = w_strTmp;

        //Toast.makeText(mApplicationContext, "GetDeviceInformation, Recv Data : " + w_strTmp, Toast.LENGTH_SHORT).show();

        return true;
    }
    
    
    
    private  boolean SendPackage(byte[] pPCCmd, byte[] pData)
    {
        int    nDataLen;

        pPCCmd[0] = (byte)0xEF;
        pPCCmd[1] = 0x01;

        nDataLen = (int)((int)((pPCCmd[5] << 8) & 0x0000FF00) | (int)(pPCCmd[4] & 0x000000FF));

        return m_usbBase.UsbSCSIWrite(pPCCmd, 6, pData, nDataLen, 5000);
    }

    private  boolean RecvPackage(byte[] pData, int[] pLevRen)
    {
        int    w_nLen;
        byte[] w_abyPCCmd = new byte[6];
        byte[] w_abyRespond = new byte[4];
        boolean w_bRet;

        w_abyPCCmd[0] = (byte)0xEF;
        w_abyPCCmd[1] = 0x02;
        w_abyPCCmd[2] = 0;
        w_abyPCCmd[3] = 0;
        w_abyPCCmd[4] = 0;
        w_abyPCCmd[5] = 0;

        // receive status
        w_bRet = m_usbBase.UsbSCSIRead(w_abyPCCmd, 6, w_abyRespond, 4, 5000);

        if (!w_bRet)
            return false;

        // receive data
        //w_nLen = (int)((w_abyRespond[3] << 8) | w_abyRespond[2]);
        w_nLen = (int)((int)((w_abyRespond[3] << 8) & 0x0000FF00) | (int)(w_abyRespond[2] & 0x000000FF));

        if (w_nLen > 0)
        {
            //w_nTime = SystemClock.elapsedRealtime();

            w_abyPCCmd[1] = 0x03;
            w_bRet = m_usbBase.UsbSCSIRead(w_abyPCCmd, 6, pData, w_nLen, 5000);

            //w_nTime = SystemClock.elapsedRealtime() - w_nTime;

            if (!w_bRet)
                return false;

            pLevRen[0] = w_nLen;
        }

        return  true;
    }
    
    /***************************************************************************
     * Get 
    ***************************************************************************/   
    private short GetResResponseVal()
    {
    	return (short)((int)((m_abyPacket[9] << 8) & 0x0000FF00) | (int)(m_abyPacket[8] & 0x000000FF));
    }
    
    private int GetResParameterVal()
    {
    	return ((int)((m_abyPacket[7] << 24) & 0xFF000000) |
    			(int)((m_abyPacket[6] << 16) & 0x00FF0000) |
    			(int)((m_abyPacket[5] << 8) & 0x0000FF00) | 
    			(int)(m_abyPacket[4] & 0x000000FF));
    }
    
    /***************************************************************************
     * Get 2bytes packet checksum(pDataPkt[0] + pDataPkt[1] + ....)
    ***************************************************************************/
    private short CalcChkSumOfPkt(byte[] pDataPkt, int nSize)
    {
        int     i, nChkSum = 0;

        for(i=0;i<nSize;i++)
        {
            if ((int)pDataPkt[i] < 0)
                nChkSum = nChkSum + ((int)pDataPkt[i] + 256);
            else
                nChkSum = nChkSum + pDataPkt[i];
        }

        return (short)nChkSum;
    }
    
    /***************************************************************************
     * Make Command Packet
    ***************************************************************************/
    void InitCmdPacket(short wCMDCode, short wDeviceID, int nParam)
    {
    	short	w_wCheckSum;

    	memset(m_abyPacket, (byte)0, GD_CMD_PACKET_LEN);
    	        
    	// Prefix
    	m_abyPacket[0] = (byte)(CMD_PREFIX_CODE & 0xFF);
    	m_abyPacket[1] = (byte)((CMD_PREFIX_CODE >> 8) & 0xFF);
    	
    	// Device ID
    	m_abyPacket[2] = (byte)(wDeviceID & 0xFF);
    	m_abyPacket[3] = (byte)((wDeviceID >> 8) & 0xFF);
    	
    	// Parameter
    	m_abyPacket[4] = (byte)(nParam & 0xFF);
    	m_abyPacket[5] = (byte)((nParam >> 8) & 0xFF);
    	m_abyPacket[6] = (byte)((nParam >> 16) & 0xFF);
    	m_abyPacket[7] = (byte)((nParam >> 24) & 0xFF);
    	    	
    	// Command Code
    	m_abyPacket[8] = (byte)(wCMDCode & 0xFF);
    	m_abyPacket[9] = (byte)((wCMDCode >> 8) & 0xFF);    	

    	w_wCheckSum = CalcChkSumOfPkt(m_abyPacket, GD_CMD_PACKET_LEN-2);

    	// CheckSum
    	m_abyPacket[10] = (byte)(w_wCheckSum & 0xFF);
    	m_abyPacket[11] = (byte)((w_wCheckSum >> 8) & 0xFF);
    	
    	m_nPacketSize = GD_CMD_PACKET_LEN;
    }
    /***************************************************************************
     * Make Data Packet
    ***************************************************************************/
    void	InitDataPacket(short wCMDCode, short wDeviceID, byte[] pbyData, int nDataLen)
    {
    	short	w_wCheckSum;

    	//g_pCmdPacket->m_wPrefix = CMD_DATA_PREFIX_CODE;
    	m_abyPacket[0] = (byte)(DATA_PREFIX_CODE & 0xFF);
    	m_abyPacket[1] = (byte)((DATA_PREFIX_CODE >> 8) & 0xFF);

    	// Device ID
    	m_abyPacket[2] = (byte)(wDeviceID & 0xFF);
    	m_abyPacket[3] = (byte)((wDeviceID >> 8) & 0xFF);
    	   	
    	//memcpy(&g_pCmdPacket->m_abyData[0], p_pbyData, p_wDataLen);
    	System.arraycopy(pbyData, 0, m_abyPacket, 4, nDataLen);

    	//. Set checksum
    	w_wCheckSum = CalcChkSumOfPkt(m_abyPacket, nDataLen + 4);

    	m_abyPacket[nDataLen+4] = (byte)(w_wCheckSum & 0xFF);
    	m_abyPacket[nDataLen+5] = (byte)((w_wCheckSum >> 8) & 0xFF);

    	m_nPacketSize = nDataLen + 6;
    }
    
    /***************************************************************************
     * Check Packet
    ***************************************************************************/
    boolean CheckReceive(byte[] pbyPacket, int nPacketLen, short wPrefix)
    {
    	short			w_wCalcCheckSum, w_wCheckSum, w_wTmp;
    	    	
    	//. Check prefix code
    	w_wTmp = (short)((int)((pbyPacket[1] << 8) & 0x0000FF00) | (int)(pbyPacket[0] & 0x000000FF));
            	
     	if (wPrefix != w_wTmp)
     	{
     		return false;
     	}
     	
    	//. Check checksum
    	w_wCheckSum = (short)((int)((pbyPacket[nPacketLen-1] << 8) & 0x0000FF00) | (int)(pbyPacket[nPacketLen-2] & 0x000000FF));
    	    	
    	w_wCalcCheckSum = CalcChkSumOfPkt(pbyPacket, nPacketLen-2);        	
    	
    	if (w_wCheckSum != w_wCalcCheckSum)
    	{
    		return false;
    	}
    	    	    	    	
    	return true;
    }
    
    //--------------------------- Send, Receive Communication Packet Functions ---------------------//
    /***************************************************************************/
    /***************************************************************************/    
    private  boolean USB_SendPacket(short wCMD)
    {
    	int		nSentBytes;
   	
    	nSentBytes = Usb_SendData(m_abyPacket, m_nPacketSize, SCSI_TIMEOUT);
    	
    	if (nSentBytes != m_nPacketSize)
    	{    		
    		return false;
    	}
    	
    	return USB_ReceiveAck(wCMD); 	
    }
    
    /***************************************************************************/
    /***************************************************************************/        
    private boolean USB_ReceiveAck(short wCMD)
    {
    	int		nReceivedBytes;
    	int		w_nTimeOut = SCSI_TIMEOUT;
    	   	
    	nReceivedBytes = Usb_RecvData(m_abyPacket, GD_ACK_PACKET_LEN, w_nTimeOut);
    	if( nReceivedBytes != GD_ACK_PACKET_LEN )
    	{
    		return false;
    	}
   	
    	m_nPacketSize = GD_ACK_PACKET_LEN;	
    	
    	if( !CheckReceive(m_abyPacket, GD_ACK_PACKET_LEN, CMD_PREFIX_CODE) )
    	{
    		return false;
    	}
    	    		
    	return true;
    }
        
    /***************************************************************************/
    /***************************************************************************/
    boolean USB_SendDataPacket(short wCMD)
    {
    	int		nSentBytes;

    	System.arraycopy(m_abyPacket, 0, m_abyTmpBuf, 0, SB_OEM_HEADER_SIZE+SB_OEM_DEV_ID_SIZE);    	
    	nSentBytes = Usb_SendData(m_abyTmpBuf, SB_OEM_HEADER_SIZE+SB_OEM_DEV_ID_SIZE, SCSI_TIMEOUT );
    	if( nSentBytes != SB_OEM_HEADER_SIZE+SB_OEM_DEV_ID_SIZE )
    		return false;

    	System.arraycopy(m_abyPacket, SB_OEM_HEADER_SIZE+SB_OEM_DEV_ID_SIZE, m_abyTmpBuf, 0, m_nPacketSize-6);
    	nSentBytes = Usb_SendData(m_abyTmpBuf, m_nPacketSize-6, SCSI_TIMEOUT );
    	if( nSentBytes != m_nPacketSize-6 )
    		return false;

    	System.arraycopy(m_abyPacket, m_nPacketSize-SB_OEM_CHK_SUM_SIZE, m_abyTmpBuf, 0, SB_OEM_CHK_SUM_SIZE);
    	nSentBytes = Usb_SendData(m_abyTmpBuf, SB_OEM_CHK_SUM_SIZE, SCSI_TIMEOUT );
    	if( nSentBytes != SB_OEM_CHK_SUM_SIZE )
    		return false;
    	
    	return USB_ReceiveAck(wCMD);
    }
    /***************************************************************************/
    /***************************************************************************/
    boolean USB_ReceiveDataPacket(short wCMD, int p_nDataLen)
    {
    	int		nReceivedBytes;
    	
    	nReceivedBytes = Usb_RecvData(m_abyTmpBuf, SB_OEM_HEADER_SIZE+SB_OEM_DEV_ID_SIZE, SCSI_TIMEOUT);
    	if( nReceivedBytes != SB_OEM_HEADER_SIZE+SB_OEM_DEV_ID_SIZE )
    		return false;    	
    	System.arraycopy(m_abyTmpBuf, 0, m_abyPacket, 0, SB_OEM_HEADER_SIZE+SB_OEM_DEV_ID_SIZE);
    	    	
    	nReceivedBytes = Usb_RecvData(m_abyTmpBuf, p_nDataLen, SCSI_TIMEOUT);
    	if( nReceivedBytes != p_nDataLen )
    		return false;
    	System.arraycopy(m_abyTmpBuf, 0, m_abyPacket, SB_OEM_HEADER_SIZE+SB_OEM_DEV_ID_SIZE, p_nDataLen);
    	    	
    	nReceivedBytes = Usb_RecvData(m_abyTmpBuf, SB_OEM_CHK_SUM_SIZE, SCSI_TIMEOUT);
    	if( nReceivedBytes != SB_OEM_CHK_SUM_SIZE )
    		return false;
    	System.arraycopy(m_abyTmpBuf, 0, m_abyPacket, SB_OEM_HEADER_SIZE+SB_OEM_DEV_ID_SIZE+p_nDataLen, SB_OEM_CHK_SUM_SIZE);
    	    	
    	if(!CheckReceive(m_abyPacket, p_nDataLen+6, DATA_PREFIX_CODE ))
    		return false;

    	return true;    	
    }
    
    int Usb_SendData(byte[] pBuf, int nSize, int nTimeOut)
    {
    	return SCSI_Operation(pBuf, nSize, nTimeOut, false);
    }

    int Usb_RecvData(byte[] pBuf, int nSize, int nTimeOut)
    {
    	return SCSI_Operation(pBuf, nSize, nTimeOut, true);
    }

    int SCSI_Operation(byte[] pBuf, int nSize, int nTimeOut, boolean bRead)
    {
    	int nSizeTemp = nSize, nSizeReal, nCopyOff;

    	nCopyOff = 0;
    	   	
    	while(nSizeTemp > 0)
    	{    		
    		nSizeReal = nSizeTemp;
    		if (nSizeReal > USB_BLOCK_SIZE)
    			nSizeReal = USB_BLOCK_SIZE;

    		if (bRead)
    		{
    			if (OperationInternal(m_abyTmpBuf, nSizeReal, nTimeOut, bRead) != nSizeReal)
    				break;
    		
    			System.arraycopy(m_abyTmpBuf, 0, pBuf, nCopyOff, nSizeReal);
    		}
    		else
    		{
    			System.arraycopy(pBuf, nCopyOff, m_abyTmpBuf, 0, nSizeReal);
    			
    			if (OperationInternal(m_abyTmpBuf, nSizeReal, nTimeOut, bRead) != nSizeReal)
    				break;
    		}
    		
    		nSizeTemp -= nSizeReal;    		
    		
    		nCopyOff = nCopyOff + nSizeReal;
    	}
    	
    	if (nSizeTemp == 0)
    		return nSize;
    	
    	return 0;
    }
    
    int OperationInternal(byte[] pBuf, int nSize, int nTimeOut, boolean bRead)
    {
    	byte[] btCDB = new byte[8];
    	
    	memset(btCDB, (byte)0, 8);
    	    	
    	if (bRead)
    	{    		
    		btCDB[0] = (byte)0xEF; btCDB[1] = (byte)0xFF;
    		btCDB[4] = (byte)m_nPacketSize;
    		
    		if (!m_usbBase.UsbSCSIRead(btCDB, 8, pBuf, nSize, nTimeOut ) )
    		{
    			return 0;
    		}
    	}
    	else
    	{    		
    		btCDB[0] = (byte)0xEF; btCDB[1] = (byte)0xFE;
    		btCDB[4] = (byte)m_nPacketSize;
    		
    		if (!m_usbBase.UsbSCSIWrite(btCDB, 8, pBuf, nSize, nTimeOut ) )
    		{
    			return 0;
    		}
    	}    		
    	    	
    	return nSize;
    }
    
    /***************************************************************************/
    /***************************************************************************/
    boolean USB_ReceiveRawData(byte[] pBuffer, int nDataLen)
    {
    	byte[] btCDB = new byte[8];
    	
    	memset(btCDB, (byte)0, 8);
    	
    	btCDB[0] = (byte)0xEF; btCDB[1] = 0x14;
    	
    	if (!m_usbBase.UsbSCSIRead(btCDB, 8, pBuffer, nDataLen, SCSI_TIMEOUT ) )
    		return false;

    	return true;
    }
    
    private boolean memcmp(byte[] p1, byte[] p2, int nLen)
    {
    	int		i;
    	
    	for (i=0; i<nLen; i++)
    	{
    		if (p1[i] != p2[i])
    			return false;
    	}
    	
    	return true;
    }
    
    private void memset(byte[] p1, byte nValue, int nLen)
    {
    	Arrays.fill(p1, 0, nLen, nValue);
    }
    
    private void memcpy(byte[] p1, byte nValue, int nLen)
    {
    	Arrays.fill(p1, 0, nLen, nValue);
    }
    
    private short MAKEWORD(byte low, byte high)
    {
    	short s;
    	s = (short)((int)((high << 8) & 0x0000FF00) | (int)(low & 0x000000FF));
    	return s;
    }

    private int MAKELONG(short low, short high)
    {
    	int s;
    	s = (int)((int)((high << 16) & 0xFFFF0000) | (int)(low & 0x000000FFFF));
    	return s;
    }
    
    private byte LOBYTE(short s)
    {
    	return (byte)(s & 0xFF); 
    }
    
    private byte HIBYTE(short s)
    {
    	return (byte)((s >> 8) & 0xFF);
    }
}
