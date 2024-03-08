package com.jnuparkingsystem.dao;

import com.jnuparkingsystem.domain.Car;

public interface ParkingRegularTicketDao {
    int count() throws Exception;

    int issue(String car_num) throws Exception;

    int delete(String car_num) throws Exception;

    Car select(String car_num) throws Exception;

    boolean haveRegularTicket(String car_num) throws Exception;

    int update(Car car) throws Exception;
}
