package com.lemeng.ourgame;

public class NetDataBean {
	public long actionType;//1为更新，2为删除，3为轮回清空，4为全部清空
    public long type;
    public long id;
    public String extan;
    public long goodId;
    public long goodType;// 1为不使用，2为装备，3为卡组，4未不是物品
    public long isClean;// 1为清除，2为不清除
}
