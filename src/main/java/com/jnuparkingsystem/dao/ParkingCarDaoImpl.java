package com.jnuparkingsystem.dao;
import com.jnuparkingsystem.domain.Car;
import org.apache.ibatis.session.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class ParkingCarDaoImpl implements ParkingCarDao {
    @Autowired
    private SqlSession session;
    private static String namespace = "com.jnuparkingsystem.dao.ParkingMapper.";

    @Override
    public int count() throws Exception { //카운트
        return session.selectOne(namespace+"count");
    }

    @Override
    public int enter(Car car) throws Exception { //입차
        return session.insert(namespace+"enter", car);
    }

    @Override
    public int exit(String carNumber) throws Exception { //출차
        return session.delete(namespace+"exit", carNumber);
    }

    @Override
    public boolean enteredBefore(String carNumber) throws Exception { //출차 이전에 입차 한적이 있는지
        return session.selectOne(namespace + "enteredBefore", carNumber);
    }

    @Override
    public Car select(String carNumber) throws Exception { //선택
        return session.selectOne(namespace + "select", carNumber);
    }

    @Override
    public int update(Car car) throws Exception { //업데이트
        return session.update(namespace+"update", car);
    }
}