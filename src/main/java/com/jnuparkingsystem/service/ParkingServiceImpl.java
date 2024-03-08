package com.jnuparkingsystem.service;

import com.jnuparkingsystem.dao.ParkingCarDao;
import com.jnuparkingsystem.dao.ParkingHistoryDao;
import com.jnuparkingsystem.dao.ParkingRegularTicketDao;
import com.jnuparkingsystem.domain.Car;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ParkingServiceImpl implements ParkingService {
    @Autowired
    ParkingRegularTicketDao parkingRegularTicketDao;
    @Autowired
    ParkingCarDao parkingCarDao;
    @Autowired
    ParkingHistoryDao parkingHistoryDao;
    private final int MAX = 3; //주차장 최대 자리 수
    private final long STANDARDFEE = 1000; //기본 요금

    @Override
    public int getMax() {
        return MAX;
    }

    @Override
    public long getStandardFee() {
        return STANDARDFEE;
    }

    @Override
    @Transactional
    public int enterCar(Car car) throws Exception {
        if (parkingCarDao.count() >= MAX) //주차공간 꽉찰 때 -1를 반환
            return -1;

        document_RegularTicket(car); //정기 주차권 여부를 판별해서 car 객체에 기록

        Date date = new Date();
        car.setEnterDate(date);

        parkingCarDao.enter(car);
        parkingHistoryDao.enter(car);

        return 1;
    }

    @Override
    @Transactional
    public long exitCarPhase1(Car car) throws Exception {

        if (!enteredBefore(car.getCarNumber())) //입차하지 않았을때 -1 반환
            return -1;

        Car car2 = parkingCarDao.select(car.getCarNumber());
        Date date = new Date();

        long beforeTime = car2.getEnterDate().getTime();
        long afterTime = date.getTime();

        long diff = (afterTime - beforeTime) / 1000; //입차 출차 초단위 차이 계산

        long parkingFee = calculateParkingFee(diff); //주차 요금

        car.setFee(parkingFee); //주차요금 기록
        car.setEnterDate(car2.getEnterDate());
        car.setHaveRegularParkingTicket(car2.getHaveRegularParkingTicket());


        return parkingFee;
    }

    @Override
    public boolean exitCarPhase2(Car car) throws Exception {

        if (car.getPaidFee() - car.getFee() < 0) //돈이 부족할때 -1 반환
            return false;

        else {
            Date date = new Date();
            car.setExitDate(date); //출차한 시각 기록
            car.setChangeFee(car.getPaidFee() - car.getFee()); //거스름돈 기록
        }

        //car를 db에 기록
        parkingCarDao.exit(car.getCarNumber());
        parkingHistoryDao.exitVer2(car);

        return true;
    }

    @Override
    public void document_RegularTicket(Car car) throws Exception {
        if (parkingRegularTicketDao.haveRegularTicket(car.getCarNumber()))
            car.setHaveRegularParkingTicket(true);
        else
            car.setHaveRegularParkingTicket(false);
    }

    @Override
    public boolean enteredBefore(String carNum) throws Exception {
        return parkingCarDao.enteredBefore(carNum);
    }

    @Override
    public int countParking() throws Exception {
        return parkingCarDao.count();
    }

    @Override
    public long calculateParkingFee(long seconds) {
        long standardFee = getStandardFee();

        if (seconds <= 1800)
            return 0;

        long additionalFee = ((seconds - 1800) / 600) * 200;

        return standardFee + additionalFee;
    }
}


