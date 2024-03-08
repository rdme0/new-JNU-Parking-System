package com.jnuparkingsystem.controller;

import com.jnuparkingsystem.domain.Car;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CarValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Car.class.isAssignableFrom(clazz); // clazz가 Car 또는 그 자손인지 확인
    }

    @Override
    public void validate(Object target, Errors errors) {
        System.out.println("CarValidator.validate() is called");
        Car car = (Car) target;

        // 차량 번호 유효성 검사
        String carNumber = car.getCarNumber();
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "carNumber", "required");
        if (!carNumCheck(carNumber))
            errors.rejectValue("carNumber", "invalid_car_number");

        // 지불한 돈 유효성 검사
        Long paidFee = car.getPaidFee();
        if (paidFee != null && paidFee < 0)
            errors.rejectValue("paidFee", "invalid_paid_fee");
    }

    public static boolean carNumCheck(String carNumber){
        Pattern pattern1 = Pattern.compile("^\\d{2}[가-힣]\\d{4}$");
        Matcher matcher1 = pattern1.matcher(carNumber);

        Pattern pattern2 = Pattern.compile("^\\d{3}[가-힣]\\d{4}$");
        Matcher matcher2 = pattern2.matcher(carNumber);

        return (matcher1.find() && carNumber.length() == 7) || (matcher2.find() && carNumber.length() == 8);
    }
}