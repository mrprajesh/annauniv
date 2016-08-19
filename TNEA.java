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

public class TNEA {

	//static String folder="TNEA 2016/";
	static String folder="TNEA 2016/";

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
	
	public static void doFormatText(String cid){

		try{
			FileInputStream finstream = new FileInputStream(folder+"/"+ cid+ "_html.txt");
			DataInputStream in = new DataInputStream(finstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			FileWriter foutstream = new FileWriter(folder+"/sql.txt",true);
			BufferedWriter out = new BufferedWriter(foutstream);
			String strLine, s ="-", date="-1", session = "-1";
			for (int i= 0; (strLine = br.readLine()) != null ; i++)   {
				if(strLine.startsWith("<h5")){
					date = strLine.substring(strLine.indexOf("<b>")+3,strLine.indexOf("</b>"));
					//session = 
				}
				else if (strLine.startsWith("<td bgcolor="))
				{	// course CID, BRANCH
					out.write("\n"+cid  );
					s = strLine.substring(strLine.indexOf("<b>")+3,strLine.indexOf("</b>"));	
					out.write(", "+s);
				}else if(strLine.startsWith("<th color=")){
					
					if (strLine.indexOf("FF9900\">") != -1) {
						// OC =	FF9900
						s = strLine.substring(strLine.indexOf("FF9900\">")+8, strLine.indexOf("</th>"));	
						out.write(", "+s);
					}	
					if(strLine.indexOf("00ccff\">") != -1){
						// BCM 
						s = strLine.substring(strLine.indexOf("00ccff\">")+8,strLine.indexOf("</th>"));	
						out.write(", "+s);
					}
					else if(strLine.indexOf("ccff00\">") != -1 ){
						//  BC
						s = strLine.substring(strLine.indexOf("ccff00\">")+8,strLine.indexOf("</th>"));	
						out.write(", "+s);
					}
					else if(strLine.indexOf("993399\">") != -1){
						// MBC
						s = strLine.substring(strLine.indexOf("993399\">")+8,strLine.indexOf("</th>"));	
						out.write(", "+s);
					}
					else if(strLine.indexOf("66ffff\">") != -1){
						// SCA
						s = strLine.substring(strLine.indexOf("66ffff\">")+8,strLine.indexOf("</th>"));	
						out.write(", "+s);
					}
					else if(strLine.indexOf("000099\">") != -1){
						// SC
						s = strLine.substring(strLine.indexOf("000099\">")+8,strLine.indexOf("</th>"));	
						out.write(", "+s);
					}
					else if(strLine.indexOf("ffff00\">")!= -1){
						// ST
						s = strLine.substring(strLine.indexOf("ffff00\">")+8 ,strLine.indexOf("</th>"));	
						out.write(", "+s);
					}
				}else{
					//s = strLine.substring(strLine.indexOf("ffff00\">")+8 ,strLine.indexOf("</th>"));
					//out.write(", "+s);
					System.out.println("else");
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

	public static void doHTML(String cid){
		try{

			FileInputStream finstream = new FileInputStream(folder+"/"+cid+".txt");
			DataInputStream in = new DataInputStream(finstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			FileWriter foutstream = new FileWriter(folder+"/"+cid+"_html.txt");
			BufferedWriter out = new BufferedWriter(foutstream);

			CharSequence a = "img";
			while ((strLine = br.readLine()) != null)   {
				if(strLine.startsWith("<td bgcolor=\"blue\" color=\"yellow\" align=\"left\"") 
				|| strLine.startsWith("<th color=\"white\" align=\"center\" style=") 
				|| strLine.startsWith("<h5") 
				)  {
					//System.out.println (strLine);
					out.write(strLine+"\n");
			}
		}
		in.close();
		out.close();
		System.out.println("ends..dohtml");
		doFormatText(cid);
		}
		catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	
	public static void getSessionData(){
		try{
			FileInputStream fstream = new FileInputStream("coll.txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				while(!getWebpage(strLine));
			}
			//getWebpage("3");  for debugging
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
	
	
	
	
	public static boolean getWebpage(String cid){
		boolean ret = true;
		try {
			//arraytotbarch.pl  
			//arraytot.pl
			//
			String httpsURL = "https://www.annauniv.edu/cgi-bin/vacancy16/arraytot.pl";
			URL myurl = new URL(httpsURL);
			HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
			con.setRequestMethod("POST");
			con.setDoInput(true);
			con.setDoOutput(true);
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("col", cid);
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
			filename = folder+ "/"+cid +".txt";
				
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			
			while ((inputLine = in.readLine()) != null)
			{
				out.write(inputLine.trim()+"\n");
			}
			in.close();
			out.close();
			System.out.println(cid + "\t done");
			doHTML(cid);
		} 
		catch (Exception e) {
			System.out.println("ENDS ERR" + e.toString() + "\t" + cid);	
			ret = false;
		}
		return ret;
	}
}
