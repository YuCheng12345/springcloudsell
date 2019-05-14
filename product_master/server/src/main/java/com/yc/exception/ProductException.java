package com.yc.exception;

import com.yc.enums.ResultEnum;
import lombok.Getter;

@Getter
public class ProductException extends RuntimeException {

    private Integer code;

    public ProductException(ResultEnum resultEnum){
        super(resultEnum.getMessage());

        this.code = resultEnum.getCode();
    }

    public ProductException(Integer code, String message){
        super(message);
        this.code = code;
    }

}
