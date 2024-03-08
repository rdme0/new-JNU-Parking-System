package com.jnuparkingsystem.dao;

import com.jnuparkingsystem.domain.Car;

public interface ParkingCarDao {
    int count() throws Exception;

    int enter(Car car) throws Exception;

    int exit(String car_num) throws Exception;

    boolean enteredBefore(String car_num) throws Exception;

    Car select(String car_num) throws Exception;

    int update(Car car) throws Exception;
}
