package com.tianyl.weixin.robot.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.tianyl.core.util.io.IOUtils;
import com.tianyl.core.util.webClient.RequestResult;

public class WebUtil {

	static {
		System.setProperty("jsse.enableSNIExtension", "false");
	}

	public static RequestResult getUrlResponse(String url,
			Map<String, String> paramMap, Map<String, String> cookieMap,
			boolean isGet) {
		RequestResult rr = new RequestResult();
		if (paramMap == null) {
			paramMap = new HashMap<String, String>();
		}
		if (cookieMap == null) {
			cookieMap = new HashMap<String, String>();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) (new URL(url).openConnection());
			conn.setConnectTimeout(30 * 1000);
			conn.setReadTimeout(30 * 1000);
			if (!paramMap.isEmpty()) {
				conn.setDoOutput(true);
			}
			conn.setDoInput(true);
			conn.setRequestMethod(isGet ? "GET" : "POST");
			conn.setUseCaches(false);
			// 仅对当前请求自动重定向
			conn.setInstanceFollowRedirects(true);
			// header 设置编码
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:42.0) Gecko/20100101 Firefox/42.0");
			if (!cookieMap.isEmpty()) {
				String cookie = "";
				for (String key : cookieMap.keySet()) {
					cookie += key + "=" + cookieMap.get(key) + "; ";
				}
				cookie = cookie.substring(0, cookie.length() - 2);
				conn.setRequestProperty("Cookie", cookie);
			}
			// 连接
			conn.connect();
			writeParameters(conn, paramMap);
			rr.setResponseCode(conn.getResponseCode());
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				rr.setOk(false);
				if (conn.getErrorStream() != null) {
					rr.setResultBytes(IOUtils.toByteArray(conn.getErrorStream()));
				}
			} else {
				rr.setOk(true);
				rr.setResultBytes(IOUtils.toByteArray(conn.getInputStream()));
			}
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rr;
	}

	private static void writeParameters(HttpURLConnection conn,
			Map<String, String> map) throws IOException {
		if (map == null || map.isEmpty()) {
			return;
		}
		String content = "";
		Set<String> keySet = map.keySet();
		int i = 0;
		for (String key : keySet) {
			String val = map.get(key);
			content += (i == 0 ? "" : "&") + key + "="
					+ URLEncoder.encode(val, "utf-8");
			i++;
		}
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes(content);
		out.flush();
		out.close();
	}

	public static RequestResult getUrlResponse(String url, JSONObject json, boolean isGet) {
		RequestResult rr = new RequestResult();
		try {
			HttpURLConnection conn = (HttpURLConnection) (new URL(url).openConnection());
			conn.setConnectTimeout(30 * 1000);
			conn.setReadTimeout(30 * 1000);
			if (json != null) {
				conn.setDoOutput(true);
			}
			conn.setDoInput(true);
			conn.setRequestMethod(isGet ? "GET" : "POST");
			conn.setUseCaches(false);
			// 仅对当前请求自动重定向
			conn.setInstanceFollowRedirects(true);
			// header 设置编码
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:42.0) Gecko/20100101 Firefox/42.0");
			// 连接
			conn.connect();
			writeParameters(conn, json);
			rr.setResponseCode(conn.getResponseCode());
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				rr.setOk(false);
				rr.setResultBytes(IOUtils.toByteArray(conn.getErrorStream()));
			} else {
				rr.setOk(true);
				rr.setResultBytes(IOUtils.toByteArray(conn.getInputStream()));
			}
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rr;
	}

	private static void writeParameters(HttpURLConnection conn, JSONObject json) throws IOException {
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		out.writeBytes(json.toJSONString());
		out.flush();
		out.close();
	}

}
