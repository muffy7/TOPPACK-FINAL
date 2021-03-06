import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.util.Timer;
import java.util.*;

public class Finalize {

	static int flag = 0;
	static OkHttpClient client = new OkHttpClient();
	static Map<String, Integer> packages = new HashMap<String, Integer>();

	/*
	 * This method Run if for OkHttp. We take an input as a http url and then store
	 * the returned message in our response variable Then return the body part of
	 * the response in String format
	 */
	public static String run(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();

		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		} catch (java.net.UnknownHostException e) {
			System.out.println("Please Check your Internet Connection");
			return "";
		}

	}

	// Now from here I have defined all the three methods in same file. First method
	// is search
	public static void search(String keyWord) throws IOException {
		JSONParser parser = new JSONParser();
		BufferedWriter Repository_Id = new BufferedWriter(new FileWriter("D:/Repositories_Id.txt"));
		/*
		 * I am limiting my search for 5 pages ..... We can change it from here if we
		 * need to.
		 */
		for (int l = 0; l < 5; l++) {
			// From below part we can increase or decrease the result shown per_page value
			String response = run(
					"https://api.github.com/search/repositories?q=%22" + keyWord + "&page=" + l + "per_page=30");
			if (response.equals("")) {
				System.exit(0);
			}
			Object obj = null;
			try {
				obj = parser.parse(new String(response));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray items = (JSONArray) jsonObject.get("items");

			for (int i = 0; i < items.size(); i++) {

				JSONObject ob = (JSONObject) items.get(i);
				String repo_Id = ob.get("id").toString();
				System.out.println("ID:" + repo_Id);
				Repository_Id.write(repo_Id);
				Repository_Id.newLine();/*
										 * We are writing the Repository_Id in a file and then try to use it for
										 * "toppacks" method defined below
										 */
				System.out.println("Name:" + ob.get("name"));
				String user_name = (String) ob.get("full_name");
				int j;
				// From here I am trying to extract the user_name from the fullName present in
				// the response variable
				for (j = 0; j < user_name.length(); j++) {
					if (user_name.charAt(j) == '/')
						break;
				}
				user_name = user_name.substring(0, j);
				System.out.println("Owner:" + user_name);
				System.out.println("Fork:" + ob.get("forks"));
				System.out.println("Starcount:" + ob.get("stargazers_count"));
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

	/*
	 * This method Import which takes repository Id as an input and then uses it to
	 * print the packages if package.json file is present
	 */
	public static void Import(String id) throws IOException {

		String response = null;
		BufferedWriter pac = new BufferedWriter(new FileWriter("D:/packages.txt"));
		try {
			response = run("https://api.github.com/repositories/" + id);
			if (response.equals("")) {
				System.exit(0);
			}
			if (response.contains("API rate limit exceeded")) {
				System.out.println("Network- Error: API rate limit exceeded");
				System.exit(0);
			}
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
		JSONObject jacob = (JSONObject) obj;
		int j;
		String name = (String) (jacob).get("name");
		System.out.println("Name: " + name);

		String user_name = (String) (jacob).get("full_name");
		for (j = 0; j < user_name.length(); j++) {
			if (user_name.charAt(j) == '/')
				break;
		}
		user_name = user_name.substring(0, j);
		System.out.println("Owner: " + user_name);
		String response1 = null;
		try {
			/*
			 * This will print the files and directories present inside the root directory
			 */
			response1 = run("https://api.github.com/repos/" + user_name + "/" + name + "/" + "contents");
			if (response1.equals("")) {
				System.exit(0);
			}
			if (response1.contains("API rate limit exceeded")) {
				System.out.println("Network- Error: API rate limit exceeded");
				System.exit(0);
			}
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
		JSONArray arr = (JSONArray) obj2;

		for (int i = 0; i < arr.size(); i++) {

			JSONObject jobj2 = (JSONObject) arr.get(i);
			if (((String) jobj2.get("name")).equals("package.json")) {
				String DownLoad_Url = null;
				try {
					DownLoad_Url = run((String) jobj2.get("download_url"));// This will help us see the contents of
																			// package.json
					if (response.equals("")) {
						System.exit(0);
					}
					if (DownLoad_Url.contains("API rate limit exceeded")) {
						System.out.println("Network- Error: API rate limit exceeded");
						System.exit(0);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				JSONParser parse = new JSONParser();
				JSONObject job = new JSONObject();
				try {
					job = (JSONObject) parse.parse(DownLoad_Url);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				JSONObject dev = (JSONObject) job.get("dependencies");
				JSONObject devDep = (JSONObject) job.get("devDependencies");
				try {
					for (Object key : dev.keySet()) {

						String keyStr = (String) key;
						if (flag == 0)
							System.out.print(keyStr + ", ");
						pac.write(keyStr);
						pac.newLine();
						if (packages.containsKey(keyStr)) {
							packages.replace(keyStr, packages.get(keyStr) + 1);
						} else {
							packages.put(keyStr, 1);
						}

					}
				} catch (NullPointerException e) {

				}
				try {
					for (Object key : devDep.keySet()) {

						String keyStr = (String) key;
						if (flag == 0)
							System.out.print(keyStr + ", ");
						pac.write(keyStr);
						pac.newLine();
						if (packages.containsKey(keyStr)) {
							packages.replace(keyStr, packages.get(keyStr) + 1);
						} else {
							packages.put(keyStr, 1);

						}
					}
				} catch (NullPointerException e) {

				}

				break;
			}

		}
		pac.close();

	}

	/*
	 * This method toppacks will help me print the Top ten packs used in the
	 * package.json file of the repositories whose repositories_Id's are stored in
	 * the Repositories_Id.txt file. To search a keyword and see the results for the
	 * top ten packages we need to run a search method in main method and then use
	 * toppacks to perform it's function. We can also integrate it in one method but
	 * for simplicity and extendibilty I prefer to use it as different files
	 */
	public static void toppacks() throws IOException {
		// String TodayQuote = "Wear your failure as a badge of honour";

		flag = 1;

		BufferedReader br = null;
		int read_counter_repository = 0;
		try {
			br = new BufferedReader(new FileReader("D:/Repositories_Id.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RandomAccessFile br1 = null;
		try {
			br1 = new RandomAccessFile("D:/packages.txt", "rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// br1 = new BufferedReader(new FileReader("D:/packages.txt"));

		String repo_Id = "";
		try {
			repo_Id = br.readLine();
			read_counter_repository++;
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (repo_Id != null) {

			System.out.println("Repositories_Id  :-" + repo_Id);
			try {
				br1.seek(0);// For each repository_Id package.txt is updated so we need to go to the
							// starting of the file and for this I will need this
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Import(repo_Id);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (read_counter_repository < 5) { /*
												 * This will limit to search for the packages in the first 5
												 * Repository_Id's present in the Repositories.txt. We can change it
												 * here if we want but we need to mind the API rate limit
												 */
				try {
					repo_Id = br.readLine();
					read_counter_repository++;

				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				break;
			}

		}
		try {
			br.close();
			br1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("\n \n HURRAH WE GOT THIS THING AND NOW ---- \n \n");
		// This will print the top 10 used packages :)
		packages.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).limit(10)
				.forEach(System.out::println);
		flag = 0;
	}

	public static void main(String args[]) throws IOException {
		Scanner sc = new Scanner(System.in);
		String keyword;
		int option;

		do {
			System.out.println("Menu");
			System.out.println("1.Search");
			System.out.println("2.Import");
			System.out.println("3.TopPacks");
			System.out.println("0.Exit");
			option = sc.nextInt();
			if (option == 1) {
				System.out.println("Enter keyword :");
				keyword = sc.next();
				search(keyword);
			} else if (option == 2) {
				System.out.println("Enter ID :");
				keyword = sc.next();
				Import(keyword);
			} else if (option == 3) {
				toppacks();
			}

		} while (option != 0);

		sc.close();

	}
}