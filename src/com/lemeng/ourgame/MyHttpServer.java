package com.lemeng.ourgame;

import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;  
import com.sun.net.httpserver.HttpHandler;  
import com.sun.net.httpserver.HttpServer;  
import com.sun.net.httpserver.spi.HttpServerProvider;


public class MyHttpServer {  
    //�������񣬼������Կͻ��˵����� 
	static Map<Thread,Long> mThreadMap = new HashMap<>();
    public static void httpserverService() throws IOException {  
        HttpServerProvider provider = HttpServerProvider.provider();  
       //  HttpServer httpserver =provider.createHttpServer(new InetSocketAddress(8809), 100);//�����˿�6666,��ͬʱ�� ��100������ 
       HttpServer httpserver =provider.createHttpServer(new InetSocketAddress(8889), 100);//�����˿�6666,��ͬʱ�� ��100������ 
        httpserver.createContext("/ourgame", new MyHttpHandler());   
        httpserver.setExecutor(null);  
        httpserver.start();       
        MyDebug.log("server started"); 
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
            	while(true) {
            		Set<Thread> sets =  mThreadMap.keySet();
            		if(sets.size() > 0) {
            			MyDebug.log("����߳���Ϊ"+sets.size()); 
                		long current = System.currentTimeMillis();
                		int count = 0;
                		for(Thread t : sets) {
                			
                			long creatTime = mThreadMap.get(t);
                			if(creatTime >10000+creatTime) {
                				count ++;
                				MyDebug.log("�رմ���߳�"+count); 
                				t.interrupt();
                			}
                		}
            		} 
            		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}            		
            	}

            }
        });
    	t.start();
    }  
    //Http��������  
    static class MyHttpHandler implements HttpHandler {  
        public void handle(HttpExchange httpExchange) throws IOException {       
        	Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                	mThreadMap.put( Thread.currentThread() ,System.currentTimeMillis());
                    String responseMsg = "ok";   //��Ӧ��Ϣ  
                    //InputStream in = httpExchange.getRequestBody(); //��������� 
                    String queryString =  getParm(httpExchange.getRequestBody());  
                  //  queryString = queryString.replaceAll("\n", "");
                    MyDebug.log("request:\n"+queryString);  
                    if(queryString == null || queryString.length() == 0) {
                    	JSONObject back = new JSONObject();
            			back.put("date", "����Ϊ��");
            			back.put("status", 1);
            			back.put("time", System.currentTimeMillis());
            			back.put("version", SqlHelper.CURRENT_APK_VERSION);
                    	responseMsg =back.toString();
                    }else {
                    	responseMsg = SqlManager.getIntance().dealMessage(queryString);
                    }
                    
                    MyDebug.log("responseMsg:\n"+responseMsg);
                    try {
						httpExchange.sendResponseHeaders(200,responseMsg.getBytes().length);
	                    OutputStream out = httpExchange.getResponseBody();  //��������  
	                    out.write(responseMsg.getBytes());  
	                    out.flush();  
//	                    out.close();                  
	                    out = null;
	                       
					} catch (IOException e) {
												
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						mThreadMap.remove(Thread.currentThread());
						httpExchange.close(); 
					}//������Ӧͷ���Լ���Ӧ��Ϣ�ĳ���  

                }
            });
        	t.start();
        		
        	
        }  
    }  
    
    
   class MyThread extends Thread {
	   public int mMyThreadId = -1;
   }
    
    
    public static  Map<String,String> jsonToMap(String str){
    	Map<String,String> result = new HashMap<>();
    	
    	try {
    		JSONObject jb = new JSONObject(str);
        	if(jb.has("user")) {
        		result.put("user", jb.getString("user"));
        	}
        	if(jb.has("orderid")) {
        		result.put("orderid",jb.getInt("orderid")+"");
        	}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}

    	return result;
    }
    

	public static String getParm(InputStream request) {
        InputStreamReader ir = null;
      
        try {
        	
        	ir = new InputStreamReader(request, "UTF-8");
        	
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	e.printStackTrace();	
        	 return null;
        }
        
        char[] line =  new char[1024];
        StringBuilder sb = new StringBuilder();
        try {
            while (ir.read(line) != -1) {
            	MyDebug.log("getParm line :"+new String(line));
            	for(char c : line) {
            		if(c !='\0') {
            			 sb.append(c);
            		}
            	}              
                line = new char[1024];
            }
            ir.close();
            ir = null;
            request.close();
            request = null;
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	if(ir != null ) {
        		try {
        			ir.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					MyDebug.log("ir.close IOException :"+e1.getMessage());
				}
        	}
        	if(request != null) {
        		try {
        			request.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					MyDebug.log("request.close IOException :"+e1.getMessage());
				}
        	}
        	MyDebug.log("getParm IOException:"+e.getMessage());
            return null;
        }
       
        try {
			return URLDecoder.decode(sb.toString(),"utf8") ;
		} catch (UnsupportedEncodingException e) {
			MyDebug.log(" URLDecoder.decode UnsupportedEncodingException:"+e.getMessage());
			// TODO Auto-generated catch block
		}
        return null;
    }
    
    public static Map<String,String> formData2Dic(String formData ) {
        Map<String,String> result = new HashMap<>();
        if(formData== null || formData.trim().length() == 0) {
            return result;
        }
        final String[] items = formData.split("&");
        for(String item: items) {
            String[] keyAndVal = item.split("=");
            if( keyAndVal.length == 2) {
                try{
                    final String key = URLDecoder.decode( keyAndVal[0],"utf8");
                    final String val = URLDecoder.decode( keyAndVal[1],"utf8");
                    result.put(key,val);
                }catch (UnsupportedEncodingException e) {}
            }
        }
        return result;
    }
    
    public static void main(String[] args) throws IOException {  

		 try {
	            // The newInstance() call is a work around for some
	            // broken Java implementations

	            //Class.forName("com.mysql.cj.jdbc.Driver");com.mysql.jdbc.
	            Class.forName("com.mysql.jdbc.Driver");
	            System.out.println("���سɹ�");
	        } catch (Exception ex) {
	        	System.out.println("����ʧ��");
	            // handle the error
	        }
        httpserverService();  
    }  
}  