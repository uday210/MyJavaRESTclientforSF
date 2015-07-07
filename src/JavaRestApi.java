import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class JavaRestApi {
	//The connection data
	private static final String clientId ="<clientid>";
	private static final String clientSecret ="<clientsecret>";
	private static final String redirecturi ="https://localhost:8443/RestTest/oauth/_callback";
	private static String tokenUrl =null;
	private static final String environment  ="https://login.salesforce.com";
	private static final String username ="<username>";
	private static final String password ="<password+token>"";
	
	private static String accessToken = null;
	private static String instanceUrl=null;
	
	public static void main(String[] args) {
		System.out.println("------Getting a token--------");
		tokenUrl = environment+"/services/oauth2/token";
		HttpClient httpclient = new HttpClient();
		//create a post method to get accesstoken from above end point
		//tokenurl is the endpoint
		
		PostMethod post = new PostMethod(tokenUrl);
		//add parameters to "post" method
		//those are username password,clientsecret,client id
		
		post.addParameter("grant_type","password");
		post.addParameter("client_id",clientId);
		post.addParameter("client_secret",clientSecret);
		post.addParameter("redirect_uri",redirecturi);
		post.addParameter("username",username);
		post.addParameter("password",password);
		
		try {
			httpclient.executeMethod(post);
			
			JSONObject authResponse = new JSONObject(new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));
			System.out.println("Auth Response :"+authResponse.toString(2));
			
			accessToken = authResponse.getString("access_token");
			instanceUrl=authResponse.getString("instance_url");
			
			System.out.println("GOt access token : "+accessToken);
			System.out.println("GOt instance_url : "+instanceUrl);
			new JavaRestApi().createAccount(instanceUrl, accessToken);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}	
	
	
private String createAccount(String inUrl,String accToken) throws Exception{
			System.out.println("------Creating  a account--------");
			
			HttpClient httpclient = new HttpClient();
			JSONObject account = new JSONObject();
			String accountId="";
			try{
				
				account.put("Name", "udayREST1123");
				account.put("Site", "www.alltricksworld.com");
				account.put("AccountNumber", "inserted from rest");
				account.put("test__c", "rest api is working");
				
				PostMethod post = new PostMethod(inUrl+"/services/data/v33.0/sobjects/Account/");
			    post.setRequestHeader("Authorization","OAuth " + accToken);
			    post.setRequestEntity(new StringRequestEntity(account.toString(),"application/json",null));
			    
			    httpclient.executeMethod(post);
			    System.out.println("HTTP status code "+post.getStatusCode()+" creating account \n");
			 if(post.getStatusCode()==201){
				 try{
					 
					 JSONObject response = new JSONObject(new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));
					 System.out.println("Create response :"+response.toString(2));
					 
					 if(response.getBoolean("success")){
						 accountId = response.getString("id");
						 System.out.println("New Account record id :"+accountId+"\n \n");
						 
					 }
				 }catch(Exception e){
					 e.printStackTrace();
				 }
			 }   
			}catch(Exception e){
				e.printStackTrace();
			}
			System.out.println("------Creating  a account completed--------");
			return accountId;
		}
}
