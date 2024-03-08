package com.jnuparkingsystem.domain;

import java.util.Date;
import java.util.Objects;

public class Car { //DTO
    private String carNumber; //차 번호
    private Date enterDate; //입차 일시
    private Date exitDate; //출차 일시
    private boolean haveRegularParkingTicket; //정기 주차권 가지고 있는지
    private long fee;
    private long paidFee;
    private long changeFee; //거스름돈



    public Car(){}



    public Car(String carNumber, Date enterDate, Date exitDate, boolean have_RegularParkingTicket) {
        this.carNumber = carNumber;
        this.enterDate = enterDate;
        this.exitDate = exitDate;
        this.haveRegularParkingTicket = have_RegularParkingTicket;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public Date getEnterDate() {
        return enterDate;
    }

    public void setEnterDate() {
        this.enterDate = new Date();
    }
    public void setEnterDate(Date date) {
        this.enterDate = date;
    }

    public Date getExitDate() {
        return exitDate;
    }

    public void setExitDate() {
        this.exitDate = new Date();
    }

    public void setExitDate(Date date) {this.exitDate = date;}

    public boolean getHaveRegularParkingTicket() {
        return haveRegularParkingTicket;
    }

    public void setHaveRegularParkingTicket(boolean haveRegularParkingTicket) {
        this.haveRegularParkingTicket = haveRegularParkingTicket;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public long getPaidFee() {
        return paidFee;
    }

    public void setPaidFee(long paidFee) {
        this.paidFee = paidFee;
    }

    public long getChangeFee() {
        return changeFee;
    }

    public void setChangeFee(long changeFee) {
        this.changeFee = changeFee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(carNumber, car.carNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carNumber);
    }

    @Override
    public String toString() {
        return "Car{" +
                "carNumber='" + carNumber + '\'' +
                ", enterDate=" + enterDate +
                ", exitDate=" + exitDate +
                ", haveRegularParkingTicket=" + haveRegularParkingTicket +
                ", fee=" + fee +
                ", paidFee=" + paidFee +
                ", changeFee=" + changeFee +
                '}';
    }


}