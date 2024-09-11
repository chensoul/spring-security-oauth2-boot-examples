/*
 * The MIT License
 *
 *  Copyright (c) 2021, wesine.com.cn
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.chensoul.oauth2.common.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 包装器业务异常类实现
 */
@Getter
public class BusinessException extends RuntimeException implements ResultCodeAware {

  private ResultCodeAware resultCodeAware;

  private String message;

  public BusinessException(String message) {
    super(message);
    this.message = message;
    this.resultCodeAware = ResultCode.INTERNAL_ERROR;
  }

  public BusinessException(String message, Throwable e) {
    super(message, e);
    this.message = message;
    this.resultCodeAware = ResultCode.INTERNAL_ERROR;
  }

  public BusinessException(String message, int code) {
    super(message);
    this.message = message;
    this.resultCodeAware = ResultCode.fromCode(code);
  }

  public BusinessException(String message, int code, Throwable e) {
    super(message, e);
    this.message = message;
    this.resultCodeAware = ResultCode.fromCode(code);
  }


  //直接接收EmBusinessError的传参用于构造业务异常
  public BusinessException(ResultCodeAware resultCodeAware) {
    this.message = resultCodeAware.getMessage();
    this.resultCodeAware = resultCodeAware;
  }

  //接收自定义errorMsg的方式构造业务异常
  public BusinessException(ResultCodeAware resultCodeAware, String message) {
    super(message);
    this.resultCodeAware = resultCodeAware;
    this.message = message;
  }

  public BusinessException(ResultCodeAware resultCodeAware, Throwable cause) {
    super(cause);
    this.resultCodeAware = resultCodeAware;
  }

  @Override
  public int getCode() {
    return this.resultCodeAware.getCode();
  }

  @Override
  public String getMessage() {
    if (StringUtils.isNotBlank(message)) {
      return message;
    }
    return this.resultCodeAware.getMessage();
  }

  public ResultCodeAware getResultCodeAware() {
    return resultCodeAware;
  }
}
