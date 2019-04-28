package com.lemeng.ourgame;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class SqlHelper {
	
	//static final String TABLE_DATE= "_date";
	static final String TABLE_NET= "_net";
	static final int CURRENT_VERSION = 4900;
	static final int CURRENT_APK_VERSION = 50;
	static final int CURRENT_APK_UPDATE_MUST = 1;
	static final String CURRENT_APK_UPDATE_STR = "亲爱的勇士您好，最新版本已经发布，为了您的游戏体验，请尽快前往TAPTAP进行更新。";
    static String CREATE_SQL = "("+
           "TYPE          INT ," +
           "ID            INT ," +
           "EXTAN         TEXT ,"+
           "GOODID        INT ," +
           "GOODTYPE      INT ," +
           "ISCLENAN      INT "+
           ")charset=utf8;";

	Connection conn = null;
	Statement stmt = null;
	private SqlHelper() {
	    try {
			conn =DriverManager.getConnection("jdbc:mysql://localhost:3306/ourgame?serverTimezone=GMT%2B8&characterEncoding=utf-8","root","zsbin149");
			stmt = conn.createStatement();
			System.out.println("数据库连接成功");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("数据库连接失败");
			e.printStackTrace();
		}	
	}
	
	public void findUser(String user) {
		
		String commPath = "select * from user_tb2 WHERE  user='" +user+"'";
		ResultSet  rs = null;
	    try {
	    	int count = 0;
	    	rs = stmt.executeQuery(commPath);
            while(rs.next()){
            	
            	count ++;                                        
            }     
            rs.close();
            rs = null;
            if(count >0) {
 //           	System.out.println("已存在用户："+user);
            	return;
            }else {
            	long time = System.currentTimeMillis();
            	commPath = "INSERT INTO user_tb2 (user,name,level,time,register,luihui,lasttime,version) VALUES (\""+user+"\",\"***\",-100,-1,"+ time+",0,"+time+",-1);";
            	stmt.execute(commPath);
            	
           // 	System.out.println("用户表插入成功："+user);
           // 	creatTabel(user+TABLE_DATE);
           // 	creatTabel(user+TABLE_NET);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
        	if(rs != null) {
        		try {
					rs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
        	System.out.println("执行："+commPath +"失败");
            e.printStackTrace();
        } 		
	}
	
	public void saveMaxLeve(String user,int level) {
		
		String commPath = "select * from user_tb2 WHERE  user='" +user+"'";
    	ResultSet rs = null;
    	int currentLevel = -1;
    	int version = -1;
		try {
			rs = stmt.executeQuery(commPath);
	        while(rs.next()){
	        	currentLevel = rs.getInt("level");
	        } 
	        rs.close();
	        rs = null;
			if(level > currentLevel) {
				long time = System.currentTimeMillis();
	            commPath = "UPDATE user_tb2 SET level=" +level+",time="+time+" WHERE user='"+user+"'";
	            stmt.execute(commPath);
			}
		} catch (SQLException e) {
        	if(rs != null) {
        		try {
					rs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
			MyDebug.log("执行："+commPath+"出错");
			e.printStackTrace();
		}	
	}
	
	public void updateVersion(String user,int version) {
		
		String commPath = null;
		try {
			commPath = "update user_tb2 set version="+version+" WHERE user='"+user+"'";
			stmt.execute(commPath);
			commPath = "update user_tb2 set level=-100 WHERE user='"+user+"'";
			stmt.execute(commPath);
		} catch (SQLException e) {
			
			MyDebug.log("执行："+commPath+"出错");
			e.printStackTrace();
		}	
		
	}
	
	public void addLuihui(String user) {
		
		String commPath = "update user_tb2 set luihui=luihui+1 WHERE user='"+user+"'";
		try {
			stmt.execute(commPath);
		} catch (SQLException e) {
			
			MyDebug.log("执行："+commPath+"出错");
			e.printStackTrace();
		}	
	}
	public void updateLastTime(String user) {
		
		String commPath = "update user_tb2 set lasttime="+System.currentTimeMillis()+" WHERE user='"+user+"'";
		try {
			stmt.execute(commPath);
		} catch (SQLException e) {
			
			MyDebug.log("执行："+commPath+"出错");
			e.printStackTrace();
		}	
	}
	
	
	public void saveUserName(String user,String  name) {
		
		String commPath = "select * from user_tb2 WHERE  user='" +user+"'";
		try {
			commPath = "UPDATE user_tb2 SET name='" +name+"' WHERE user='"+user+"'";
            stmt.execute(commPath);
		} catch (SQLException e) {
			
			MyDebug.log("执行："+commPath+"出错"  );
			e.printStackTrace();
		}

		
	}
	
    private String getInsertComm(String user,SqlDateBean data)
    {
    	String commandString = "INSERT INTO user_detail VALUES (";
        commandString +=  data.type;
        commandString += "," + data.id;
        commandString += "," + "\"" + data.extan + "\"";
        commandString += "," + data.goodId;
        commandString += "," + data.goodType;
        commandString += "," + data.isClean;
        commandString += "," + "\"" + user + "\""+")";
        return commandString;
    }
    
    
    public boolean testTransaction(String user,ArrayList<SqlDateBean> list) {
    	MyDebug.log(">>>>>>>>>>>>>>>>>>>>>>>>>事务执行:开始执行");
    	if(list == null || list.size() == 0) {
    		return false;    		
    	}
    	ArrayList<String> strList = new ArrayList<String>();
    	for(int i = 0 ;i< list.size(); i++) {
    		SqlDateBean bean = list.get(i);
    		String str = null;
			if(bean.actionType == 1) {
				str = SqlHelper.getIntance().updateDateInfo(user, bean);
			}else if(bean.actionType == 2) {
				str = SqlHelper.getIntance().deleteGood(user, bean);
			}else if(bean.actionType == 3) {
				str = SqlHelper.getIntance().deleteLuihui(user);
			}else if(bean.actionType == 4) {
				str = SqlHelper.getIntance().clearTableAll(user);
			}
			if(str == null || str.length() == 0) {
				return false;
			}
			strList.add(str);
    	}
    	Statement statement = null;
         try
         {
        	 
             conn.setAutoCommit(false); //开启事务，禁止自动提交

             statement = conn.createStatement();
             for(String str : strList) {           	 
            	 statement.addBatch(str);
             }
            
             statement.executeBatch();
             
             conn.commit(); //执行成功，提交事务
             statement.close();
             statement = null;
             MyDebug.log("事务执行:执行成功<<<<<<<<<<<<<<<<<<<<<<<");
         }
         catch (Exception e)
         {
             try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				MyDebug.log("事务执行:执行失败<<<<<<<<<<<<<<<<<<<<<<<");
				e1.printStackTrace();
			} //发生异常，事务回滚
            if(statement != null) {
            	try {
					statement.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
             
             return false;
         }
    	
    	
    	return true;
    }
    
    public String getLocalByUser(String user) {
    	ResultSet  rs = null;
	    try {
	    	JSONArray ar = new JSONArray();
	    	String sql =  "select * from user_detail where USER=\"" + user + "\"";
	    	MyDebug.log("执行:"+ sql);
	    	rs = stmt.executeQuery(sql);
	    	long id = -1;
        	long type = -1;
            while(rs.next()){
            	JSONObject jb = new JSONObject();
            	id = rs.getLong("ID");
            	type = rs.getLong("TYPE");
            	if(id==SqlDateBean.GAME_ID_MAX_TIME   && type ==SqlDateBean.TYPE_GAME ) {
            		continue;
            	}
               // SqlDateBean date = new SqlDateBean();
                jb.put("type",type);
                jb.put("id", id);
                jb.put("extan", rs.getString("EXTAN"));
                jb.put("goodId", rs.getLong("GOODID"));
                jb.put("goodType", rs.getLong("GOODTYPE"));
                jb.put("isClean", rs.getLong("ISCLENAN"));
               // date.type =  rs.getLong("TYPE");//reader.GetInt64(reader.GetOrdinal("TYPE"));
               // date.id = rs.getLong("ID");//reader.GetInt64(reader.GetOrdinal("ID"));
               // date.extan =rs.getString("EXTAN");// reader.GetString(reader.GetOrdinal("EXTAN"));
               // date.goodId = rs.getLong("GOODID");//reader.GetInt64(reader.GetOrdinal("GOODID"));
               // date.goodType = rs.getLong("GOODTYPE");//eader.GetInt64(reader.GetOrdinal("GOODTYPE"));
               // date.isClean = rs.getLong("ISCLENAN");//reader.GetInt64(reader.GetOrdinal("ISCLENAN"));

            	ar.put(jb);                                        
            } 
            rs.close();
            rs = null;
        	JSONObject js = new JSONObject();
        	js.put("date", ar);
        	js.put("status", 0);
        	js.put("time", System.currentTimeMillis());
        	js.put("version", SqlHelper.CURRENT_APK_VERSION);
        	return js.toString();
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
        	if(rs != null) {
        		try {
					rs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
            e.printStackTrace();
        } 
    	return "error";
    }
    
    public String getRankingListLevel(String user) {
    	ResultSet  rs  = null;
	    try {
	    	JSONArray ar = new JSONArray();
	    	String sql =  "select * from user_tb2  where version>="+CURRENT_VERSION+" order by level desc,time asc  limit 0,1000";
	    	rs= stmt.executeQuery(sql);
	    	int count = 0;
	    	boolean findUser = false;
	    	JSONObject userJs = null;
            while(rs.next()){
            	JSONObject jb = new JSONObject();
               // SqlDateBean date = new SqlDateBean();
            	String user2 = rs.getString("user");      
            	int level = rs.getInt("level");
            	if(level == -100) {
            		level = -10;
            	}
            	count++;
            	
            	if(user.equals(user2)) {
            		findUser = true;
            		userJs = new JSONObject();
            		userJs.put("user", user2);
            		userJs.put("name", rs.getString("name"));
            		userJs.put("level", level);
            		userJs.put("time", rs.getLong("time"));
            		userJs.put("index", count);
            	}
            	if(count <= 100) {
            		jb.put("user", user2);
                    jb.put("name", rs.getString("name"));
                    jb.put("level",level);
                    jb.put("time", rs.getLong("time"));
                    jb.put("index", count);
            		ar.put(jb);             		
            	}
            	
            	if(count > 100 &&  findUser) {
            		break;
            	}       	
            }      
            rs.close();
            rs = null;
            if(userJs != null) {
            	ar.put(userJs);        	
            }else {
            	sql =  "select * from user_tb2 where USER=\""+user + "\"";
            	rs = stmt.executeQuery(sql);
            	while(rs.next()){
                	int level = rs.getInt("level");
                	if(level == -100) {
                		level = -10;
                	}
            		userJs = new JSONObject();
            		userJs.put("user", user);
            		userJs.put("name", rs.getString("name"));
            		userJs.put("level", level);
            		userJs.put("time", rs.getLong("time"));
            		userJs.put("index", -1);
            		break;
            	}
            	rs.close();
            	rs = null;
        		ar.put(userJs);          	
            }
        	JSONObject js = new JSONObject();
        	js.put("date", ar);
        	js.put("status", 0);
        	js.put("time", System.currentTimeMillis());
        	js.put("version", SqlHelper.CURRENT_APK_VERSION);
        	return js.toString();
            
        } catch (SQLException e) {
        	if(rs != null) {
        		try {
					rs.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    	return "error";
    }
    
    
	public String updateDateInfo(String user,SqlDateBean data) {
		String commPath  = null;
        if (data.type != SqlDateBean.TYPE_GOOD)
        {

            commPath = "select * from  user_detail WHERE  ID=" + data.id + " AND TYPE="+ data.type+" AND USER=\"" + user + "\""; //SELECT* FROM Persons WHERE firstname = 'Thomas' OR lastname = 'Carter'
            
            ResultSet rs = null;
			try {
				rs = stmt.executeQuery(commPath);
	            ResultSetMetaData rsmd;
	            int count =0;
	            while(rs.next()) { 
	            	count++; 
	            }
	            rs.close();
	            rs = null;
	       //     System.out.println("查询："+commPath +" 数目"+count );
	            if (count < 1)
	            {
	                commPath = getInsertComm(user,data);
	            }
	            else
	            {
	                commPath = "UPDATE user_detail SET EXTAN=\"" + data.extan+"\"";
	                commPath += " WHERE TYPE=" + data.type + " AND ID=" + data.id+" AND USER=\"" + user + "\"";
	            }
	           // boolean success =  stmt.execute(commPath);
	         //   System.out.println("插入："+commPath  );	            
	            //return success;
			} catch (SQLException e) {
	        	if(rs != null) {
	        		try {
						rs.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	        	}
				MyDebug.log("执行"+commPath +" 失败" );
				e.printStackTrace();
				
				return null;
			}

        }
        else {

        	commPath = "select * from  user_detail WHERE  GOODID=" + data.goodId+" AND USER=\"" + user + "\"";
            ResultSet rs = null;
			try {
				rs = stmt.executeQuery(commPath);
	            ResultSetMetaData rsmd;
	            int count =0;
	            while(rs.next()) { 
	            	count++; 
	            }
	            rs.close();
	            rs = null;
	    //        System.out.println("查询："+commPath +" 数目"+count );
	            if (count < 1)
	            {
	                commPath = getInsertComm(user,data);
	            }
	            else
	            {
	                commPath = "UPDATE user_detail SET EXTAN=\"" + data.extan + "\""+", GOODTYPE="+data.goodType;
	                commPath += " WHERE GOODID=" + data.goodId+" AND USER=\"" + user + "\"";
	            }
	          //  return stmt.execute(commPath);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
	        	if(rs != null) {
	        		try {
						rs.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	        	}
				MyDebug.log("执行"+commPath +" 失败" );
				e.printStackTrace();
				return null;
			}
        }
        return commPath;
	}

	
	public String clearTableAll(String user) {
		String commPath =   "DELETE FROM user_detail WHERE USER=\"" + user + "\""; 
		MyDebug.log("事务执行:"+commPath);
    	return commPath;
		
	}
	
	public String deleteLuihui(String user) {
		addLuihui(user);
		String commPath =  "DELETE FROM user_detail WHERE ISCLENAN =" + 1+" AND USER=\"" + user + "\"";
		MyDebug.log("事务执行:"+commPath);
    	return commPath;

		
	}
	
    public String  deleteGood(String user,SqlDateBean data)
    {
    	 String commPath = "";
    	 if (data.type != SqlDateBean.TYPE_GOOD && data.type != -1) {
    		 commPath =  "DELETE FROM user_detail WHERE TYPE="+data.type +" AND ID="+data.id +" AND USER=\"" + user + "\"";
    		 
    	 }else {
    		 commPath =  "DELETE FROM user_detail WHERE GOODID=" + data.goodId+" AND USER=\"" + user + "\"";    		 
    	 }
		
		MyDebug.log("事务执行:"+commPath);
    	return commPath;
    }
	
	private void creatTabel(String table) {
		try {
		//	stmt = conn.createStatement();
			if(0 == stmt.executeLargeUpdate("CREATE TABLE "+table+CREATE_SQL)){
				System.out.println("成功创建表！");
			}
			else
			{
				System.out.println("创建表失败！");
		    }
			//stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	private static SqlHelper mIntance= new SqlHelper();
	
	public static SqlHelper getIntance() {
		return mIntance;
	}
}
