package com.lemeng.ourgame;

public class SqlDateBean {
    public static final  long GOOD_TYPE_BACKPACK = 1;
    public static final  long GOOD_TYPE_ZHUANGBEI = 2;
    public static final  long GOOD_TYPE_CARD = 3;
    public static final  long GOOD_TYPE_NOGOOD = 4;

    public static final  long CLEAR = 1;
    public static final  long CLEAR_NO = 2;

    public static final  long DEFAULT_GOOD_ID = -1;
    
    public static final  long GAME_ID_LEVEL = 1;
    public static final  long GAME_ID_HERO = 2;
    public static final  long GAME_ID_AUTO = 3;
    public static final  long GAME_ID_LUNHUI = 4;
    public static final  long GAME_ID_MOJING = 5;
    public static final  long GAME_ID_TIME = 6;
//    public static final  long GAME_ID_GUIDE = 7;
    public static final  long GAME_ID_POINT_PLAYER = 8;
    public static final  long GAME_ID_POINT_BACKPACK = 9;
    public static final  long GAME_ID_POINT_LUNHUI = 10;
    public static final  long GAME_ID_POINT_CARD = 11;
    public static final  long GAME_ID_FRIST_START = 12;
    public static final  long GAME_ID_NO_LUNHUI = 13;
    public static final  long GAME_ID_IS_VOICE = 14;
    public static final  long GAME_ID_LUNHUI_DEAL = 15;
    public static final  long GAME_ID_GOOD_MAXID = 16;
    public static final  long GAME_ID_IS_NET = 17;
    public static final  long GAME_ID_MAX_TIME = 20;
    public static final  long GAME_ID_PLAYER_NAME = 21;
    public static final  long GAME_ID_VERSION_CODE = 25;

    public static final  long TYPE_GAME = 1;
    public static final  long TYPE_GOOD = 2;
    public static final  long TYPE_BOOK = 3;
    public static final  long TYPE_CARD = 4;
    public static final  long TYPE_ZHUANGBEI = 5;
    public static final  long TYPE_LUNHUI = 6;
    public static final  long TYPE_DROP = 7;
    public static final  long TYPE_GUIDE = 8;
    
	
    public long type = -1;
    public long id = -1;
    public String extan = "-1";
    public String token  = null;
    public long goodId = DEFAULT_GOOD_ID;
    public long goodType = GOOD_TYPE_NOGOOD;// 1为不使用，2为装备，3为卡组
    public long isClean = 1;// 1为清除，2为不清除
    public long actionType = -1;//1为更新，2为删除，3为轮回清空，4为全部清空 5为拉取服务器存档 
}
