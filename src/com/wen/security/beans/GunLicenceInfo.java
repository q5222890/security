package com.wen.security.beans;

/**
 * 枪支持有许可信息
 */
public class GunLicenceInfo {
    public String Hold_Gun_ID;// 编号
    public String Police_ID;// 警员UserID
    public String Pass_Port;// 持枪证号
    public int Hold_Type;// 持枪类型
    /*
     * 1：公用（默认） 2：专用
     */
    public String Gun_ID;// 绑定枪号(与枪械基本信息GunInfo中的编号字段Gun_ID相关联，当持枪类型为2时，则该项不能为空)
    public boolean IsForbid;// 是否禁用枪支
    public String Forbid_Reason;// 禁用原因
    public String Forbid_Date;// 禁用时间
    public int Info_State;// 信息状态（持枪证的审批状态？）
    /*
     * 1：已审批 2：新增未审批（默认） 3：修改未审批
     */

    public String Approv_User;// 审批人
    public String Approv_Date;// 审批日期

}
