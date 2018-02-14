package com.gamedemo.utils;

import android.content.Context;

import com.gamedemo.SingleTon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class TextFileMannager {
	
	private static String data="", p = "";
	
	public TextFileMannager(){
		
	}

    public static String loadFromResource(Context ctx,  int resource){
	     final InputStream rr = ctx.getResources().openRawResource(resource);
	     try {
			final BufferedReader e0 = new BufferedReader(new InputStreamReader(rr, "UTF-8"));
			try {
				while ((p = e0.readLine()) != null){ 
					data = data + p; 
                }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     return data;
    }
    
    public String getData(){
    	return data.replace("null", "");
    }
    
    public static String loadFromFile(File file){
	    	try {
	    	    @SuppressWarnings("resource")
				BufferedReader br = new BufferedReader(new FileReader(file));
	    	    String line;
	    	    while ((line = br.readLine()) != null) {
	    	    	data = data+line;
	    	    }
	    	}
	    	catch (IOException e) {
	    	    e.printStackTrace();
	    	}
	    	return data;
    }
	
    public static String get(InputStream is){
    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));  
        StringBuilder sb = new StringBuilder();  

        String line = null;  
        try {  
            while ((line = reader.readLine()) != null) {  
                sb.append(line + "\n");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                is.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
        return  "{\"Results\":" + sb.substring(11, sb.length()-2) + "}\n" ;
    }

    public static File generateNoteOnSD(String sFileName, String sBody){
        //sBody = sBody.trim();
        sBody = sBody.replace("\n", "").replace("\r", "");
        try{
            File root = SingleTon.getCacheCarpet();
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, sFileName);
            FileWriter writer = new FileWriter(file);
            writer.append(sBody);
            writer.flush();
            writer.close();
            return root;
        }
        catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

}
