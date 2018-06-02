package com.micropace.ramp.base.common;

/**
 * 控制器基类
 */
public abstract class BaseController {

	public ResponseMsg success() {
	    return new ResponseMsg("success");
    }

    public ResponseMsg success(String message) {
        return new ResponseMsg("success", message);
    }

    public ResponseMsg success(Object data) {
        return new ResponseMsg("success", null, data);
    }

    public ResponseMsg success(String message, Object data) {
        return new ResponseMsg("success", message, data);
    }

    public ResponseMsg error(String messsage) {
	    return new ResponseMsg("error", messsage);
    }

    public ResponseMsg error(String message, Object data) {
        return new ResponseMsg("error", message, data);
    }

    public ResponseMsg error(ErrorMsg errorMsg) {
	    return new ResponseMsg("error", errorMsg.getDesc());
    }

    public ResponseMsg status(String status, String messsage, Object data) {
        return new ResponseMsg(status, messsage, data);
    }
}
