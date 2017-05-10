package com.wen.security.serial;

import com.wen.security.serial.TaskMG.OnTaskReceve;

/**
 * 命令对象
 */
public class TaskObj {
    public static final int ONECE = 0;
    public static final int ALWAYS = 1;
    /**
     * 命令接受者
     */
    public OnTaskReceve rec;
    /**
     * 命令名称
     */
    public byte ordcmd;
    /**
     * 命令辅助数据
     */
    public byte[]  sndbuf;
    /**
     * 返回结果数据
     */
    public byte[] databuf;
    /**
     * 任务优先级:0执行一次，1一直执行
     */
    public int level = ONECE;
    /**
     * 任务标识唯一
     */
    public String ID;
}
