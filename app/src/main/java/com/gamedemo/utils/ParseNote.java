package com.gamedemo.utils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class ParseNote {
	
	private String url, body, imgLvl;
	
	public ParseNote(String url){
		this.url = url;
		//Log.v("url ----->", "" + url);
		if(url.contains("atomix.vg"))
			initParse();
		else if(url.contains("levelup.com"))
			initLvParse();
	}

	public boolean initParse(){
		final Document doc;
		try {
			doc = Jsoup.connect(url).get();
			final Elements e0 = doc.select("div.twelve");
			if(!e0.isEmpty()){
				String aux1 = e0.get(2).html().replace("'","''");
				reparseNote(aux1);
				/*for(int i = 0; i < e0.size(); i++){
                    String aux1 = e0.get(i).html().replace("'","''");
					reparseNote(aux1, i);
				}*/
			}

            return true;
		} catch (IOException e) {
			e.printStackTrace();
            return false;
		}
	}

	private void reparseNote(String html){
		Log.d("html ", html);
		final Document doc;
		doc = Jsoup.parse(html);

	}

	public boolean initLvParse(){
		final Document doc;
		try {
			doc = Jsoup.connect(url).get();
			final Elements e0 = doc.select("div.content");
			final Elements e1 = doc.select("div.content iframe.youtubeVideo");
			if(!e0.isEmpty()){
				body = "";
				for(int i = 0; i < e0.size(); i++){
					//Log.d("body", e0.get(i).toString());
					body = body+e0.get(i).toString();
				}
			}
			if(!e1.isEmpty()){
				for(int i = 0; i < e1.size(); i++){
					Log.d("youtube", e1.get(i).attr("src").toString());
				}
			}
			getLvImage();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getLvImage(){
		final Document doc;
		try {
			doc = Jsoup.connect(url).get();
			String url = null;
			final Elements e0 = doc.select("div.header_info_Desktop");
			if(!e0.isEmpty()){
				for(int i = 0; i < e0.size(); i++){
					Document doc0 = Jsoup.parse(e0.get(i).html());
					Elements e1 = doc0.select("img");
					if(e1.size() > 0){
						url = getRealUrl(e1.get(0).attr("style").toString());
						//Log.v("img url ----->", "" + url);
						imgLvl = url;
					}
				}
			}

			if(url == null){
				final Elements e1 = doc.select("div.header_wrapper");
				if(!e1.isEmpty()) {
					for (int i = 0; i < e1.size(); i++) {
						Document doc0 = Jsoup.parse(e1.get(i).html());
						Elements e2 = doc0.select("header");
						if (e2.size() > 0) {
							url = getRealUrl(e2.get(0).attr("style").toString());
							//Log.v("img url ----->", "" + url);
							imgLvl = url;
						}
					}
				}
			}

			return url;
		} catch (IOException e) {
			e.printStackTrace();
			return url;
		}
	}

	private String getRealUrl(String clear){
		clear = clear.replace("background: url('","");
		clear = clear.replace("'); background-size: cover; background-position: center;","");
		return clear;
	}

	public String getBody(){
		return body;
	}

	public String getImgLvl(){
		return imgLvl;
	}

}