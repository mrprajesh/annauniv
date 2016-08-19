//package tnea;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.net.ssl.HttpsURLConnection;

public class BarchData {

	static String folder="Barch 2016/";

	public static void main(String[] args) {
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd/HH_mm");
		Date dateobj = new Date();
		String[] s = df.format(dateobj).split("/");
		String date = s[0];
        File dir = new File(folder+date);
        dir.mkdir();
		String ses = s[1];
		folder += date + "/" + ses;
        dir = new File(folder);
        dir.mkdir();
		System.out.println(date);
        getSessionData();
        System.out.println(df.format(dateobj));
	}
	
	public static void doFormatText(String rollnum){

		try{
			FileInputStream finstream = new FileInputStream(folder+"/"+ rollnum+ "_html.txt");
			DataInputStream in = new DataInputStream(finstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			FileWriter foutstream = new FileWriter(folder+"/arch2016.txt",true);
			BufferedWriter out = new BufferedWriter(foutstream);
			String strLine, s ="-", dateSession ="-1";
			out.write("\n"+rollnum);
			for (int i= 0; (strLine = br.readLine()) != null ; i++)   {
				
				if(strLine.indexOf("style117") != -1 ){
					s = strLine.substring(strLine.indexOf("style117\">")+10, strLine.indexOf("</"));	
					out.write(", "+s.trim());
				}		
				else if (strLine.indexOf("&nbsp;") != -1){
					s = strLine.substring(strLine.indexOf("&nbsp;")+6, strLine.indexOf("</") );	
					out.write(", "+s.trim());
				}else{
					s = ",ALL";
				}	
				//System.out.println(s);
			}
			in.close();
			out.close();
		}catch (Exception e){//Catch exception if any
			//System.err.println("Error: " + e.rintStacktrace());
			e.printStackTrace();
		}
	}

	public static void doHTML(String rollnum){
		try{

			FileInputStream finstream = new FileInputStream(folder+"/"+rollnum+".txt");
			DataInputStream in = new DataInputStream(finstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			FileWriter foutstream = new FileWriter(folder+"/"+rollnum+"_html.txt");
			BufferedWriter out = new BufferedWriter(foutstream);

			CharSequence a = "img";
			while ((strLine = br.readLine()) != null)   {
				if(
					strLine.indexOf("2\" class=\"style113\"") !=-1 
				||	strLine.indexOf("0\" class=\"style113\"") !=-1 
				|| 	strLine.indexOf("style117\"") !=-1 
				|| 	strLine.indexOf("style118\"") !=-1 
				
				)  {
					//System.out.println (strLine);
					out.write(strLine+"\n");
			}
		}
		in.close();
		out.close();
		System.out.println("ends..dohtml");
		doFormatText(rollnum);
		}
		catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	
	public static void getSessionData(){
		try{
			FileInputStream fstream = new FileInputStream("barch_colleges.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int i = 1;
			for (i=10050; i < 15000 ; i++)  {
				strLine = Integer.toString(i);
				while(!getWebpage(strLine));
			}
			//getWebpage("3"); //  for debugging
			in.close();
		}
		catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
	
	
	
	
	public static boolean getWebpage(String rollnum){
		boolean ret = true;
		try {
			///cgi-bin/vacancy16/distbarch.pl
			
			String httpsURL = "https://www.annauniv.edu/cgi-bin/rankarch16/varisai2016.pl";
			URL myurl = new URL(httpsURL);
			HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
			con.setRequestMethod("POST");
			con.setDoInput(true);
			con.setDoOutput(true);
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("regno", rollnum);
			param.put("ans", "103");
			param.put("val", "103");
			OutputStream os = con.getOutputStream();
			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
			writer.write(getPostDataString(param));
			
			writer.flush();
			writer.close();
			os.close();
			InputStream ins = con.getInputStream();
			InputStreamReader isr = new InputStreamReader(ins);
			BufferedReader in = new BufferedReader(isr);
			
			String inputLine;
			String filename = "-";
			filename = folder+ "/"+rollnum +".txt";
				
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			
			while ((inputLine = in.readLine()) != null)
			{
				out.write(inputLine.trim()+"\n");
			}
			in.close();
			out.close();
			System.out.println(rollnum + "\t done");
			doHTML(rollnum);
		} 
		catch (Exception e) {
			System.out.println("ENDS ERR" + e.toString() + "\t" + rollnum);	
			ret = false;
		}
		return ret;
	}
}
