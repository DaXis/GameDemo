package com.gamedemo.utils;

import android.util.Log;

import com.gamedemo.SingleTon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ParseNote {
	
	public String url, body, imgLvl;
	public int id;
	
	public ParseNote(String url, boolean onLine, int id){
		this.url = url;
		this.id = id;
		//Log.v("url ----->", "" + url);
		if(onLine){
			if(url.contains("atomix.vg"))
				initParse();
			else if(url.contains("levelup.com"))
				initLvParse();
		} else {
			if(url.contains("atomix.vg"))
				initParseOffline(id);
			else if(url.contains("levelup.com"))
				initLvParseOffline(id);
		}
	}

	public boolean initParseOffline(int id){
		final Document doc;
		String html = TextFileMannager.loadFromFile(new File(SingleTon.getCacheCarpet(), ""+id));
		//Log.d("atomix html --->", ""+html.length());
		doc = Jsoup.parse(html);
		final Elements e0 = doc.select("div.twelve");
		if(!e0.isEmpty()){
			String aux1 = e0.get(2).html().replace("'","''");
			//reparseNote(aux1);
			for(int i = 0; i < e0.size(); i++){
				body = body+e0.get(i).toString();
			}
		}
		return true;
	}

	public boolean initParse(){
		final Document doc;
		try {
			doc = Jsoup.connect(url).get();

			if(!new File(SingleTon.getCacheCarpet(), ""+id).exists())
				TextFileMannager.generateNoteOnSD("" + id, doc.html());

			final Elements e0 = doc.select("div.twelve");
			if(!e0.isEmpty()){
				String aux1 = e0.get(2).html().replace("'","''");
				//reparseNote(aux1);
				for(int i = 0; i < e0.size(); i++){
					body = body+e0.get(i).toString();
				}
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

	public boolean initLvParseOffline(int id){
		final Document doc;
		String html = TextFileMannager.loadFromFile(new File(SingleTon.getCacheCarpet(), ""+id));
		//Log.d("atomix html --->", ""+html.length());
		doc = Jsoup.parse(html);
		final Elements e0 = doc.select("#content");
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
				//Log.d("youtube "+i, e1.get(i).attr("src").toString());
			}
		}
		imgLvl = getLvImage(doc);
		return true;
	}

	public boolean initLvParse(){
		final Document doc;
		try {
			doc = Jsoup.connect(url).get();

			if(!new File(SingleTon.getCacheCarpet(), ""+id).exists())
				TextFileMannager.generateNoteOnSD("" + id, doc.html());

			final Elements e0 = doc.select("#content");
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
					//Log.d("youtube "+i, e1.get(i).attr("src").toString());
				}
			}
			imgLvl = getLvImage(doc);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getLvImage(Document doc){
		//final Document doc;
		//try {
			//doc = Jsoup.connect(url).get();
			String url = null;
			final Elements e0 = doc.select("div.header_info_Desktop");
			if(!e0.isEmpty()){
				for(int i = 0; i < e0.size(); i++){
					Document doc0 = Jsoup.parse(e0.get(i).html());
					Elements e1 = doc0.select("img");
					if(e1.size() > 0){
						url = getRealUrl(e1.get(0).attr("style").toString());
						//Log.v("img url 1 --->", "" + url);
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
							//Log.v("img url 2 --->", "" + url);
							imgLvl = url;
						}
					}
				}
			}

			return url;
		/*} catch (IOException e) {
			e.printStackTrace();
			return url;
		}*/
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
