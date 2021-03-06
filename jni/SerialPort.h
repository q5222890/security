/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_winplus_serial_utils_SerialPort */

#ifndef _Included_org_winplus_serial_utils_SerialPort
#define _Included_org_winplus_serial_utils_SerialPort
#ifdef __cplusplus
extern "C" {
#endif
// 设置串口参数：波特率，校验位，停止位
JNIEXPORT jboolean JNICALL Java_org_winplus_serial_utils_SerialPort_SetState(JNIEnv *env, jclass thiz,jint dwBaudRate, jint dwByteSize ,
        jint dwParity  , jint dwStopBits  );
/*
 * Class:     org_winplus_serial_utils_SerialPort
 * Method:    open
 * Signature: (Ljava/lang/String;II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_org_winplus_serial_utils_SerialPort_open
  (JNIEnv *, jclass, jstring, jint, jint);

/*
 * Class:     org_winplus_serial_utils_SerialPort
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_winplus_serial_utils_SerialPort_close
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
