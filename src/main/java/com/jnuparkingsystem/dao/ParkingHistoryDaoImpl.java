package com.jnuparkingsystem.dao;
import com.jnuparkingsystem.domain.Car;
import org.apache.ibatis.session.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Repository
public class ParkingHistoryDaoImpl implements ParkingHistoryDao {
    @Autowired
    private SqlSession session;
    private static String namespace = "com.jnuparkingsystem.dao.ParkingMapper.";

    @Override
    public int count() throws Exception {
        return session.selectOne(namespace+"countHis");
    }
    @Override
    public int enter(Car car) throws Exception { //입차
        return session.insert(namespace+"enterHis", car);
    }
    @Override
    public int exit(String carNumber) throws Exception { //출차
        return session.update(namespace+"exitHis", carNumber);
    }

    @Override
    public int delete(String carNumber) throws Exception { //삭제
        return session.delete(namespace+"deleteHis", carNumber);
    }

    @Override
    public Car select(String carNumber) throws Exception { //선택
        return session.selectOne(namespace + "selectHis", carNumber);
    }

    @Override
    public int exitVer2(Car car) throws Exception { //출차 버전 2
        return session.update(namespace+"exitHis2", car);
    } // int update(String statement, Object parameter)
}