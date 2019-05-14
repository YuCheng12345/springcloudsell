package com.yc.service;

import com.yc.dataobject.ProductCategory;

import java.util.List;

public interface CategoryService {


    List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList);

}





















