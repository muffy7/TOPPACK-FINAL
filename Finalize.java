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
	
	
static OkHttpClient client = new OkHttpClient();

//This run method is for the OkHttp Part
public static String run(String url) throws IOException {
	Request request = new Request.Builder()
	        .url(url)
	        .build();

	    try (Response response = client.newCall(request).execute()) {
	      return response.body().string();
	    }
}

//Now from here I have defined all the three methods in same file. First method is search
public static void search(String keyWord) throws IOException {
	
	    JSONParser parser = new JSONParser();
	    BufferedWriter Repository_Id = new BufferedWriter(new FileWriter("D:/Repositories_Id.txt"));
    	//I am limiting my search for 5 pages ..... We can change it from here if we need to. 
	    for(int l=0;l<5;l++) {
	        	
	            String response = run("https://api.github.com/search/repositories?q=%22"+keyWord+"&page="+l+"per_page=30"); //From here we can increase or decrease the results shown per_page value  
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
	        	   //From here I am trying to extract the user_name
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

//Now this is our second method Import which takes repository Id as an input and then uses it to print the packages if package.json file is present 
public static void Import (String id) throws IOException
{
       
       String response = null;
       BufferedWriter pac = new BufferedWriter(new FileWriter("D:/packages.txt"));
	try {
		response = run("https://api.github.com/repositories/"+id);
		if(response.contains("API rate limit exceeded")) {System.out.println("Network- Error: API rate limit exceeded");System.exit(0);}
	} catch (IOException e3) {
		// TODO Auto-generated catch block
		e3.printStackTrace();
	}
       JSONParser parser = new JSONParser();
       Object obj = null;
	try {
		obj = parser.parse(response);
	} catch (ParseException e3) {
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
        
        
        String response1 = null;
		try {
			//This will print the files and directories present inside this directory under the name of User_name
			response1 =run("https://api.github.com/repos/"+user_name+"/"+name+"/"+"contents");
			if(response1.contains("API rate limit exceeded")) {System.out.println("Network- Error: API rate limit exceeded");System.exit(0);}
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace(); 
		}

   Object obj2 = null;
   try {
	obj2 = parser.parse(response1);
   } catch (ParseException e1) {
	e1.printStackTrace();
    }
   JSONArray arr =(JSONArray)obj2;
   Map<String, Integer> packages = new HashMap<String, Integer>();
   for(int i=0;i<arr.size();i++){
	            JSONObject jobj2=(JSONObject)arr.get(i);
	            if(((String)jobj2.get("name")).equals("package.json")){
		        String DownLoad_Url = null;
				try {
					DownLoad_Url = run((String)jobj2.get("download_url"));//This will help us see the contents of package.json 
					if(DownLoad_Url.contains("API rate limit exceeded")) {System.out.println("Network- Error: API rate limit exceeded");System.exit(0);}
				} catch (IOException e) {
					e.printStackTrace();
				}
		        JSONParser parse = new JSONParser();
                JSONObject job = new JSONObject();
                try {
					job = (JSONObject)parse.parse(DownLoad_Url);
				} catch (ParseException e) {
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
                System.out.println("");
                break;
                }   
                 
           }
   pac.close();
}
//This is my third method which will help me print the top ten packages used in the given directories  
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
			br1.seek(0);//For each repository_Id package.txt is updated so we need to go to the starting of the file and for this I will need this
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Import(repo_Id);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		if(read_counter_repository<5) { //This will limit to search for the packages in the first 5 Repository_Id's present in the Repositories.txt. We can change it here if we want but we need to mind the API rate limit
		      try {
				repo_Id = br.readLine();
				read_counter_repository++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
		try {
			br.close();
			br1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	System.out.println("\n \n HURRAH WE GOT THIS THING AND NOW ---- \n \n");
	//This will print the top 10 used packages :)
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
		toppacks();
	    sc.close();
		
	}

}