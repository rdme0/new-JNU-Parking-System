package com.jnuparkingsystem.controller;

import java.beans.PropertyEditorSupport;
import java.util.Date;

import javax.validation.Valid;

import com.jnuparkingsystem.domain.Car;
import com.jnuparkingsystem.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping({"/", "/parking"})
public class ParkingController {

    @Autowired
    ParkingService parkingService;



    @RequestMapping("/")
    public String index( Model m) throws Exception {
        try {
            m.addAttribute("count", parkingService.availableParkingSpaces());
            return "index";
        } catch (Exception e){
            e.printStackTrace();
            return "index";
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(new CarValidator());

        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            public String getAsText() {
                Date date = (Date) getValue();
                // date가 null이면 비어있는 문자열을 반환
                if (date == null)
                    return "";

                // date가 있는 경우에는 milliseconds를 문자열로 변환하여 반환
                return Long.toString(date.getTime());
            }

            public void setAsText(String value) {
                // value가 비어있거나 null이면 null을 설정하고 종료
                if (value == null || value.isEmpty()) {
                    setValue(null);
                    return;
                }

                // value가 있는 경우에는 milliseconds로 변환하여 Date 객체를 설정
                long milliseconds = Long.parseLong(value);
                setValue(new Date(milliseconds));
            }
        });
    }

    @GetMapping("/enter")
    public String enter_car() {
        return "enter";
    }

    @PostMapping("/enter")
    public String enter_car(@Valid Car car, BindingResult result, Model m, RedirectAttributes redirectAttributes) {

        // Car 객체를 검증한 결과 에러가 있으면, 리다이렉트
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "입차를 실패하였습니다. 올바른 번호를 입력해주세요.");
            System.out.println("입차 검증 실패");
            return "redirect:/parking/enter";
        }

        //서비스
        try {
            int retService = parkingService.enterCar(car);
            m.addAttribute("count", parkingService.availableParkingSpaces());

            if(retService == -1){
                m.addAttribute("message", "주차장이 꽉 찼습니다.");
                return "index";
            }

        } catch (Exception e) {

            e.printStackTrace();

            if (e instanceof DuplicateKeyException) {
                redirectAttributes.addFlashAttribute("message", "이미 입차한 차량입니다.");
                return "redirect:/parking/enter";
            }

            m.addAttribute("message", "확인되지 않은 에러입니다. 관리자에게 연락주시기 바랍니다.");
            return "index";

        }

        m.addAttribute("message", "입차가 완료 되었습니다.");
        return "index";
    }

    @GetMapping("/exit")
    public String exit_car() {
        return "exit";
    }


    @PostMapping("/exit")
    public String exitCarPhase1(@Valid Car car, BindingResult result, Model m, RedirectAttributes redirectAttributes) {

        // Car 객체를 검증한 결과 에러가 있으면, 리다이렉트
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "출차를 실패하였습니다. 올바른 번호를 입력해주세요.");
            System.out.println("출차 검증 실패");
            return "redirect:/parking/exit";
        }

        try {
            long parkingFee = parkingService.exitCarPhase1(car);

            System.out.println(parkingFee);
            System.out.println("carasdf=" + car);

            if (parkingFee == -1) {
                redirectAttributes.addFlashAttribute("message", car.getCarNumber() + "은(는) 입차했던 차가 아닙니다.");
                return "redirect:/parking/exit";
            }

            m.addAttribute(car);

        } catch (Exception e) {
            e.printStackTrace();
            return "exitCarError";
        }


        return "exitPay";

    }

    @PostMapping("/exit2")
    public String exitCarPhase2(@Valid Car car, BindingResult result, Model m, RedirectAttributes redirectAttributes) {
        System.out.println("result=" + result);

        // Car 객체를 검증한 결과 에러가 있으면, 리다이렉트
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "출차를 실패하였습니다. 올바른 번호를 입력해주세요.");
            System.out.println("출차 검증 실패");
            return "redirect:/parking/exit";
        }

        try {
            System.out.println("car = " + car);
            if (!parkingService.exitCarPhase2(car)) {
                //잔액 부족
                redirectAttributes.addFlashAttribute("message", "잔액이 부족합니다. 다시 시도해주세요.");
                System.out.println("잔액부족");
                return "redirect:/parking/exit";
            }
            m.addAttribute("count", parkingService.availableParkingSpaces());
        } catch (Exception e) {

            e.printStackTrace();
            return "exitCarError";

        }
        System.out.println("car=" + car);
        m.addAttribute("message", "출차가 완료 되었습니다. 거스름돈 : " + car.getChangeFee() + "원");
        return "index";

    }


}

