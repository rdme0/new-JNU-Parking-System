package com.jnuparkingsystem.dao;

import com.jnuparkingsystem.domain.Car;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ParkingRegularTicketDaoImpl implements ParkingRegularTicketDao {
    @Autowired
    private SqlSession session;
    private static String namespace = "com.jnuparkingsystem.dao.ParkingMapper.";

    @Override
    public int count() throws Exception { //카운트
        return session.selectOne(namespace+"countTicket");
    }

    @Override
    public int issue(String car_num) throws Exception { //티켓 발급
        return session.insert(namespace+"issueTicket", car_num);
    }

    @Override
    public int delete(String car_num) throws Exception { //티켓 없애버리기
        return session.delete(namespace+"deleteTicket", car_num);
    }

    @Override
    public Car select(String car_num) throws Exception { //선택
        return session.selectOne(namespace + "selectTicket", car_num);
    }

    @Override
    public boolean haveRegularTicket(String car_num) throws Exception { //정기주차권 있는지 여부
        return session.selectOne(namespace + "haveTicket", car_num);
    }

    @Override
    public int update(Car car) throws Exception { //업데이트
        return session.update(namespace+"updateTicket", car);
    }
}
