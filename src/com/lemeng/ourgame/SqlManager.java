package com.lemeng.ourgame;

import java.awt.List;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import com.lemeng.*;
public class SqlManager {
	
	private SqlManager() {
		
		SqlHelper.getIntance();
		
	}
	
	public String dealMessage(String str) {
		JSONObject back = new JSONObject();
		JSONObject jb = new JSONObject(str);
		
		String user = null ;
		String token = null ;
		if(jb.has("user")) {
			user = jb.getString("user");		
		}
		if(jb.has("token")) {
			token = jb.getString("token");
		}
		if(user == null || user.length() <1) {
			back.put("date", "没有用户ID");
			back.put("status", 1);
			back.put("time", System.currentTimeMillis());
			back.put("version", SqlHelper.CURRENT_APK_VERSION);
			return back.toString();
		}
		SqlHelper.getIntance().findUser(user);
		JSONArray ar = null;
		if(jb.has("date")) {
			ar =  jb.getJSONArray("date");
		}
		
		if(ar == null || ar.length() < 1) {
			back.put("date", "数据为空");
			back.put("status", 1);
			back.put("time", System.currentTimeMillis());
			back.put("version", SqlHelper.CURRENT_APK_VERSION);
			return back.toString();
		}
	//	System.out.println("ar.length() = "+ar.length());
		ArrayList<SqlDateBean> list = new ArrayList<SqlDateBean>();
		int level = -1;
		boolean isSuccess = true;
		for(int i = 0 ; i< ar.length() ;i++) {
			
			JSONObject jb2 = ar.getJSONObject(i);
			SqlDateBean bean = new SqlDateBean();
			if(jb2.has("action")) {
				bean.actionType = jb2.getLong("action");
			}
			if(bean.actionType == -1) {
				back.put("date", "操作失败");
				back.put("status", 1);
				back.put("time", System.currentTimeMillis());
				back.put("version", SqlHelper.CURRENT_APK_VERSION);
				return back.toString();				
			}			
			if(jb2.has("type")) {
				bean.type =  jb2.getLong("type");
			}
			if(jb2.has("id")) {
				bean.id = jb2.getLong("id");
			}			
			if(jb2.has("extra")) {
				bean.extan = jb2.getString("extra");
			}
			if(jb2.has("goodId")) {
				bean.goodId = jb2.getLong("goodId");
			}
			if(jb2.has("goodtype")) {
				bean.goodType = jb2.getLong("goodtype");
			}
			if(jb2.has("isclean")) {
				bean.isClean = jb2.getLong("isclean");
			}
			
			if(bean.actionType == 5) {
				return  SqlHelper.getIntance().getLocalByUser(user);
			}else if (bean.actionType == 6){
				return  SqlHelper.getIntance().getRankingListLevel(user);
			}else if(bean.actionType == 7) {
				back.put("date", SqlHelper.CURRENT_APK_UPDATE_STR);
				back.put("status", isSuccess?0:1);
				back.put("time", System.currentTimeMillis());
				back.put("ismust",SqlHelper.CURRENT_APK_UPDATE_MUST);
				back.put("version", SqlHelper.CURRENT_APK_VERSION);
				return back.toString();
				
			}
			if(bean.type == SqlDateBean.TYPE_GAME && bean.id == SqlDateBean.GAME_ID_LEVEL) {
				level = Integer.parseInt(bean.extan);
				SqlHelper.getIntance().saveMaxLeve(user, level);
			}else if(bean.type == SqlDateBean.TYPE_GAME && bean.id == SqlDateBean.GAME_ID_PLAYER_NAME) {
				SqlHelper.getIntance().saveUserName(user, bean.extan);
			}else if(bean.type == SqlDateBean.TYPE_GAME && bean.id == SqlDateBean.GAME_ID_VERSION_CODE) {
				int version = Integer.parseInt(bean.extan);
				SqlHelper.getIntance().updateVersion(user,version);
			}else  if(bean.type == SqlDateBean.TYPE_GAME && bean.id == SqlDateBean.GAME_ID_MAX_TIME){
				long maxTime = Long.parseLong(bean.extan);
				if(maxTime >=  System.currentTimeMillis() + 86400000) {
					back.put("date", "时间过期");
					back.put("status", 1);
					back.put("time", System.currentTimeMillis());
					back.put("version", SqlHelper.CURRENT_APK_VERSION);
					return back.toString();
				}
			}
			list.add(bean);
		}
		SqlHelper.getIntance().updateLastTime(user);
	//	System.out.println("list.length() = "+list.size());
		isSuccess = SqlHelper.getIntance().testTransaction(user, list);
		back.put("date", isSuccess?"执行成功":"执行失败");
		back.put("status", isSuccess?0:1);
		back.put("time", System.currentTimeMillis());
		back.put("version", SqlHelper.CURRENT_APK_VERSION);
		return back.toString();
	}
	
	
	private static SqlManager mIntance= new SqlManager();
	
	public static SqlManager getIntance() {
		return mIntance;
	}
}
