package com.tianyl.weixin.robot.web;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tianyl.core.util.webClient.RequestResult;
import com.tianyl.weixin.robot.exception.WeiXinException;
import com.tianyl.weixin.robot.util.WebUtil;

public class WeiXin {

	private String uuid;

	private String appid = "wx782c26e4c19acffb";

	private String lang = "zh_CN";

	private String redirectUri;

	private String passTicket;

	private String skey;

	private String sid;

	private String uin;

	private String deviceID = "e293825688623105";

	private JSONObject baseRequest = new JSONObject();

	private JSONObject weixinInit;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void start() {
		uuid = createUUID();
		printQR();
		while (true) {
			boolean flag = waitForLogin("1");
			if (flag) {
				flag = waitForLogin("0");
				if (flag) {
					break;
				}
			}
		}
		doLogin();
		wxinit();
		logout();
	}

	private void logout() {
		try {
			Thread.sleep(1000 * 60);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxlogout?redirect=1&type=0&skey=" + skey;
		Map<String, String> param = new HashMap<String, String>();
		param.put("sid", sid);
		param.put("uin", uin);
		WebUtil.getUrlResponse(url, param, null, false);
	}

	private void wxinit() {
		String url = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxinit?pass_ticket=" + passTicket + "&r=" + getTime();
		JSONObject json = new JSONObject();
		json.put("BaseRequest", baseRequest);
		RequestResult result = WebUtil.getUrlResponse(url, json, false);
		if (!result.isOk()) {
			throw new WeiXinException("weixin init error");
		} else {
			weixinInit = JSON.parseObject(result.getResultStr());
			checkResponse(weixinInit, "weixin init");
		}
	}

	private void checkResponse(JSONObject json, String msg) {
		JSONObject obj = json.getJSONObject("BaseResponse");
		boolean isError = false;
		if (obj != null) {
			if (!Integer.valueOf(0).equals(obj.getInteger("Ret"))) {
				isError = true;
			}
		} else {
			isError = true;
		}
		if (isError) {
			throw new WeiXinException(msg);
		}
	}

	private void doLogin() {
		RequestResult result = WebUtil.getUrlResponse(redirectUri, null, null, true);
		if (!result.isOk()) {
			System.out.println("登录失败");
			throw new WeiXinException("登录失败");
		} else {
			String resultStr = result.getResultStr();
			System.out.println("doLogin");
			System.out.println(resultStr);
			passTicket = XmlUtil.getValue(resultStr, "pass_ticket");
			skey = XmlUtil.getValue(resultStr, "skey");
			uin = XmlUtil.getValue(resultStr, "wxuin");
			sid = XmlUtil.getValue(resultStr, "wxsid");
			baseRequest = new JSONObject();
			baseRequest.put("Uin", uin);
			baseRequest.put("Sid", sid);
			baseRequest.put("Skey", skey);
			baseRequest.put("DeviceID", deviceID);
		}
	}

	private boolean waitForLogin(String tip) {
		String url = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login?tip=" + tip + "&uuid=" + uuid + "&_=" + getTime();
		RequestResult result = WebUtil.getUrlResponse(url, null, null, true);
		String resultStr = result.getResultStr().trim();
		if (resultStr.startsWith("window.code=408")) {
			System.out.println("登录超时");
			return false;
		} else if (resultStr.startsWith("window.code=201")) {
			System.out.println("扫描成功");
			return true;
		} else if (resultStr.startsWith("window.code=200")) {
			System.out.println("确认登录");
			redirectUri = resultStr.replaceFirst("[\\s\\S]*window.redirect_uri=\"", "").replaceFirst("\";", "") + "&fun=new";
		} else {
			System.out.println("登录异常:" + resultStr);
			throw new WeiXinException("登录异常");
		}
		return true;
	}

	private void printQR() {
		System.out.println("http://www.wandoujia.com/qr?c=https://login.weixin.qq.com/l/" + uuid);
	}

	private String createUUID() {
		String uuidUrl = "https://login.weixin.qq.com/jslogin";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("appid", appid);
		paramMap.put("fun", "new");
		paramMap.put("lang", lang);
		paramMap.put("_", getTime());
		RequestResult result = WebUtil.getUrlResponse(uuidUrl, paramMap, null, false);
		String resultStr = result.getResultStr();
		String uuid = resultStr.replaceFirst(".+window.QRLogin.uuid = \"", "").replaceFirst("\";", "");
		return uuid;
	}

	private String getTime() {
		return System.currentTimeMillis() / 1000 + "";
	}

	private void test(String resultStr) {
		System.out.println(resultStr);
		if (resultStr.startsWith("window.code=408")) {
			System.out.println("登录超时");
		} else if (resultStr.startsWith("window.code=201")) {
			System.out.println("扫描成功");
		} else if (resultStr.startsWith("window.code=200")) {
			System.out.println("确认登录");
			redirectUri = resultStr.replaceFirst("[\\s\\S]*window.redirect_uri=\"", "").replaceFirst("\";", "");
		} else {
			System.out.println("登录异常:" + resultStr);
		}
	}

	public static void main(String[] args) {
		WeiXin wx = new WeiXin();
		wx.start();
	}
}
