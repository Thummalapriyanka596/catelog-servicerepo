package com.example.catalog.Exception;

public class ProductNotFoundException extends RuntimeException
{
public ProductNotFoundException(String msg)
{
    super(msg);
}
}
