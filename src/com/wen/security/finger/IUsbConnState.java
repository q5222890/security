package com.wen.security.finger;

public interface IUsbConnState {
	/**
	 * USB已连接
	 */
    void onUsbConnected();

	/**
	 * 没有USB权限
	 */
	void onUsbPermissionDenied();

	/**
	 * 设备没找到
	 */
	void onDeviceNotFound();
}
