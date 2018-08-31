package com.zh.job.common;

import com.zh.job.common.bean.Result;

public interface IJobHandler {

	/** success */
	public static final Result SUCCESS = new Result(200, null);
	/** fail */
	public static final Result FAIL = new Result(500, null);
	/** fail timeout */
	public static final Result FAIL_TIMEOUT = new Result(502, null);

	public Result execute(String param) throws Exception;

}
