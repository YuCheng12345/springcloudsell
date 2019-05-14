package com.yc.service.impl;

import com.yc.dataobject.ProductCategory;
import com.yc.repository.ProductCategoryRepository;
import com.yc.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private ProductCategoryRepository reprository;


    @Override
    public List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList) {
        return reprository.findByCategoryTypeIn(categoryTypeList);
    }

}
