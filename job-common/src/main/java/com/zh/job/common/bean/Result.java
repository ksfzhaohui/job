package com.zh.job.common.bean;

/**
 * 执行器结果
 * 
 * @author zhaohui
 * 
 */
public class Result {

	private int code;
	private String msg;

	public Result() {

	}

	public Result(int code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
