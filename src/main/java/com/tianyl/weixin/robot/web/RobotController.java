package com.tianyl.weixin.robot.web;

import com.tianyl.core.mvc.annotation.Controller;

@Controller("/robot")
public class RobotController {

	public Object m1() {
		return "OK";
	}

	public void login() {

	}

	public static void main(String[] args) {
		RobotController controller = new RobotController();
		controller.login();
	}

}
