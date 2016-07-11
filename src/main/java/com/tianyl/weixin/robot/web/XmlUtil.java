package com.tianyl.weixin.robot.web;

public class XmlUtil {

	public static String getValue(String xml, String nodeName) {
		return xml.replaceFirst("[\\s\\S]*<" + nodeName + ">", "").replaceFirst("</" + nodeName + ">[\\s\\S]*", "");
	}

	public static void main(String[] args) {
		String xml = "<error>\r\n<ret>0</ret>\r\n<skey>skey-val</skey>\r\n<wxsid>xxx</wxsid>\r\n</error>";
		System.out.println(getValue(xml, "skey"));
	}

}
