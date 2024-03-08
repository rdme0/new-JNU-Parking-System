package com.jnuparkingsystem.dao;

import com.jnuparkingsystem.domain.Car;

public interface ParkingHistoryDao {
    int count() throws Exception;

    int enter(Car car) throws Exception;

    int exit(String carNum) throws Exception;

    int delete(String car_num) throws Exception;

    Car select(String car_num) throws Exception // T selectOne(String statement, Object parameter)
    ;

    int exitVer2(Car car) throws Exception // int update(String statement, Object parameter)
    ;
}
