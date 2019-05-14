package com.yc.dataobject;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@DynamicInsert
@Data    //不用写getter,setter,toString方法了
public class ProductCategory {

    @Id   //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //自增，没有括号里的会报错
    private Integer categoryId;

    private String categoryName;

    private Integer categoryType;

    private Date createTime;

    private Date updateTime;


}
