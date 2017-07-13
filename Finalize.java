//package okhttp3.guide;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class Finalize {
OkHttpClient client = new OkHttpClient();
private String run(String url) throws IOException {
	Request request = new Request.Builder()
	        .url(url)
	        .build();

	    try (Response response = client.newCall(request).execute()) {
	      return response.body().string();
	    }
}


public static void search(String keyWord) throws IOException {
	
	    JSONParser parser = new JSONParser();
	    Finalize example = new Finalize();
	    BufferedWriter Repository_Id = new BufferedWriter(new FileWriter("D:/Repositories_Id.txt"));
    	for(int l=0;l<5;l++) {
	        	
	            String response = example.run("https://api.github.com/search/repositories?q=%22"+keyWord+"&page="+l+"per_page=50");
	            //System.out.println(response);
	            Object obj=null;
				try {
					obj = parser.parse(new String(response));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            JSONObject jsonObject =  (JSONObject) obj;
	            JSONArray items = (JSONArray) jsonObject.get("items");
	            
	           for(int i=0;i<items.size();i++){
	        	   
	        	   JSONObject ob = (JSONObject)items.get(i);
	        	   String repo_Id = ob.get("id").toString();
	        	   System.out.println("ID:"+repo_Id);
	        	   Repository_Id.write(repo_Id);
   		     	   Repository_Id.newLine();
	        	   System.out.println("Name:"+ob.get("name"));
	        	   String user_name=(String)ob.get("full_name");
	        	   int j;
	        	   for( j=0;j<user_name.length();j++)
	        	   {
	        		   if(user_name.charAt(j)=='/')break;
	        	   }
	        	   user_name=user_name.substring(0,j);
	        	   System.out.println("Owner:"+user_name);
	        	   System.out.println("Fork:"+ob.get("forks"));
	        	   System.out.println("Starcount:"+ob.get("stargazers_count"));
	        	   System.out.println();
		       }
	           
     }
    	try {
			Repository_Id.close();
		   } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		   }
}
public static void Import (String id) throws IOException
{
       //String line;
	   Finalize example = new Finalize();
       String response = null;
       BufferedWriter pac = new BufferedWriter(new FileWriter("D:/packages.txt"));
	try {
		response = example.run("https://api.github.com/repositories/"+id);
	} catch (IOException e3) {
		// TODO Auto-generated catch block
		e3.printStackTrace();
	}
       JSONParser parser = new JSONParser();
       Object obj = null;
	try {
		obj = parser.parse(response);
	} catch (ParseException e3) {
		// TODO Auto-generated catch block
		e3.printStackTrace();
	}
       JSONObject jobj=(JSONObject)obj;
        
        
        int j;	
        String name=(String)(jobj).get("name");
        System.out.println("Name: "+name);	
        
        String user_name=(String)(jobj).get("full_name");
        for(j=0;j<user_name.length();j++){
        	if(user_name.charAt(j)=='/')break;
        }
        user_name=user_name.substring(0,j);
        System.out.println("Owner: "+user_name);
        
        Finalize example1 = new Finalize();
        String response1 = null;
		try {
			response1 = example1.run("https://api.github.com/repos/"+user_name+"/"+name+"/"+"contents");
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

   Object obj2 = null;
   try {
	obj2 = parser.parse(response1);
   } catch (ParseException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
    }
   JSONArray arr =(JSONArray)obj2;
   Map<String, Integer> packages = new HashMap<String, Integer>();
   for(int i=0;i<arr.size();i++){
	            JSONObject jobj2=(JSONObject)arr.get(i);
	            if(((String)jobj2.get("name")).equals("package.json")){
		        String DownLoad_Url = null;
				try {
					DownLoad_Url = example1.run((String)jobj2.get("download_url"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        JSONParser parse = new JSONParser();
                JSONObject job = new JSONObject();
                try {
					job = (JSONObject)parse.parse(DownLoad_Url);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                JSONObject dev = (JSONObject)job.get("dependencies");
                JSONObject devDep = (JSONObject)job.get("devDependencies");
                for (Object key : dev.keySet()) {
                  String keyStr = (String)key;
                  System.out.println(keyStr+", ");
                  pac.write(keyStr);
                  pac.newLine();
                 if(packages.containsKey(keyStr)){
                     packages.replace(keyStr, packages.get(keyStr)+1);
                 }
                 else{
                     packages.put(keyStr,1);
                 }
                 }
                for (Object key : devDep.keySet()) {
                  String keyStr = (String)key;
                  System.out.print(keyStr+", ");
                  pac.write(keyStr);
                  pac.newLine();
                 if(packages.containsKey(keyStr)){
                    packages.replace(keyStr, packages.get(keyStr)+1);
                      }
                else{
                    packages.put(keyStr,1);
                    }
                 }
                Iterator <Map.Entry<String, Integer>> entries = packages.entrySet().iterator();
                break;
                }   
                 
           }
   pac.close();
}

public static void toppacks() 
{
	//String TodayQuote = "Wear your failure as a badge of honour";
	BufferedReader br=null;
	int read_counter_repository=0;
	try {
		br = new BufferedReader(new FileReader("D:/Repositories_Id.txt"));
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	RandomAccessFile br1 = null;
	try {
		br1 = new RandomAccessFile("D:/packages.txt","rw");
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//br1 = new BufferedReader(new FileReader("D:/packages.txt"));
	Map<String, Integer> hm = new HashMap<String, Integer>();
	String repo_Id="";
	
		try {
			repo_Id = br.readLine();
			read_counter_repository++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	while(repo_Id!=null)
	{
		System.out.println("Repositories_Id  :-"+repo_Id);
	    try {
			br1.seek(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Import(repo_Id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Scanner sss= new Scanner(System.in);
		//sss.next();
		String package_Id="";
		try {
			package_Id = br1.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(package_Id!=null) 
		{
				hm.put(package_Id, hm.containsKey(package_Id) ? hm.get(package_Id) + 1 : 1);
				try {
					package_Id = br1.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if(read_counter_repository<5) {
		      try {
				repo_Id = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
		try {
			br.close();
			br1.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	System.out.println("\n \n HURRAH WE GOT THIS THING AND NOW ---- \n \n");
	
	hm.entrySet().stream()
	   .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	   .limit(10)
	   .forEach(System.out::println);
}
public static void main(String args[]){
		Scanner sc= new Scanner(System.in);
		String keyword=sc.next();
		try {
			search(keyword);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//toppacks();
	    sc.close();
		
	}

}



















