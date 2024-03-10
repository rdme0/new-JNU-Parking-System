package com.jnuparkingsystem.service;

import com.jnuparkingsystem.domain.Car;
import org.springframework.transaction.annotation.Transactional;

public interface ParkingService {
    int getMax();

    long getStandardFee();
    @Transactional
    public int availableParkingSpaces() throws Exception;
    @Transactional
    int enterCar(Car car) throws Exception;

    @Transactional
    long exitCarPhase1(Car car) throws Exception;

    boolean exitCarPhase2(Car car) throws Exception;

    void document_RegularTicket(Car car) throws Exception;

    boolean enteredBefore(String carNum) throws Exception;

    int countParking() throws Exception;

    long calculateParkingFee(long seconds);
}
