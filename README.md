# new JNU Parking System
- 제작 기간 : 2024.03.04 ~ <br>
- 제작 인원 : 1명

### 개발 환경
- Java 11
- IntelliJ 2023.3
- Apache Tomcat 9.0.84
- Spring Framework 5.0.7
- MySQL
- MyBatis<br><br>


- HTML5 & CSS3
- JavaScript

### 프로젝트 구조 및 기능
  #### - 전체적인 흐름

유스케이스 다이어그램(아직 30% 정도 구현 안됨)
![KakaoTalk_20240301_165519560](https://github.com/rdme0/new-JNU-Parking-System/assets/71927381/1abc965e-2566-4d0a-864c-a9a4db8804e7)

그림으로 그려보기
![P1-3](https://github.com/rdme0/new-JNU-Parking-System/assets/71927381/ba21285e-9c3a-4064-9b5c-1d5c81f522b2)

사용 영상

https://github.com/rdme0/new-JNU-Parking-System/assets/71927381/cbe85e7d-223d-4f23-8dee-166daa628035

### 기능 1 : 주차 가능 공간 개수 카운팅

<details>
  <summary> 기능 1 구현 코드 펼치기 </summary>
  
 - ##### Controller
   ```java
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
   ```
   서비스로부터 주차 가능 공간 개수를 리턴받아 모델에 넣고 메인화면에 쓰일 index.jsp 를 반환
 - ##### Service
   ```java
   private final int MAX = 5; //주차장 최대 자리 수

   ... //생략
   
    @Override
    public int countParking() throws Exception {
        return parkingCarDao.count();
    }
   

   @Transactional
    public int availableParkingSpaces() throws Exception {
        return getMax() - countParking();
    }
   ```
  ParkingCarDao로 부터 현재 주차중인 공간을 반환받고, 이를 이용해 남은 자리를 연산하여 컨트롤러에 반환
   
 - ##### Repository
   ##### ParkingCarDao
   ```java
   @Override
    public int count() throws Exception { //카운트
        return session.selectOne(namespace+"count");
    }

   ```
   ##### MyBatis Mapping
   ```xml
   <!-- realtime parking table-->
    <select id="count" resultType="int">
        SELECT count(*) FROM parking
    </select>
   ```
   Parking 테이블에서 현재 주차중인 공간을 반환한 것을 그대로 반환
</details>

### 기능 2 : 입차하기

  #### 2-1 : myproject/parking/enter getMapping을 postMapping으로 돌리기

<details>


<summary>기능 2-1 구현 코드 펼치기</summary>

  
  ```java
@GetMapping("/enter")
    public String enter_car() {
        return "enter";
    }

@PostMapping("/enter")
    public String enter_car(@Valid Car car, BindingResult result, Model m, RedirectAttributes redirectAttributes) {

...//생략


```

  URI를 myproject/parking/enter으로 입력받았을 때 enter.jsp를 돌려줘서 Post 방식으로 요청 받게 끔 함


</details>

  #### 2-2 : CarDto 검증 - 차량 번호

<details>


  
<summary>기능 2-2 구현 코드 펼치기</summary>


- #### Controller
``` java
// Car 객체를 검증한 결과 에러가 있으면, 리다이렉트
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "입차를 실패하였습니다. 올바른 번호를 입력해주세요.");
            System.out.println("입차 검증 실패");
            return "redirect:/parking/enter";
        }
```
  
  
  - #### CarValidator 일부
  ```java
public static boolean carNumCheck(String carNumber){
        Pattern pattern1 = Pattern.compile("^\\d{2}[가-힣]\\d{4}$");
        Matcher matcher1 = pattern1.matcher(carNumber);

        Pattern pattern2 = Pattern.compile("^\\d{3}[가-힣]\\d{4}$");
        Matcher matcher2 = pattern2.matcher(carNumber);

        return (matcher1.find() && carNumber.length() == 7) || (matcher2.find() && carNumber.length() == 8);
    }
```


이후 PostMapping으로 Car 객체를 입력 받을 때 정규식을 이용하여 car.carNumber의 유효성 검사를 수행하고<br> 잘못된 값으로 판명났을 때 알림창과 함께 리다이렉트

</details>


#### 2-3 : 본격적인 입차 로직


<details>


<summary>기능 2-3 구현 코드 펼치기</summary>

- #### Controller

``` java
// ...
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
```
parkingService의 enterCar 메서드를 사용하는데, 반환 값을 보고 주차장이 꽉 찼는지 판단하고, <br>
catch문에서는 이미 주차한 차량을 또 주차할 경우인 예외를 예상하고 있고 이럴 경우 리다이렉트

- #### Service

```java
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
```


  document_RegularTicket 메서드는 ParkingRegularTicketDao을 사용하여 해당 차량이 정기 주차권을 가지고 있는지 여부를 판단하여 car Dto에 기록함 <br>
  이후 입차 시각을 car Dto에 기록하고 car Dto를 ParkingCarDao와 parkingHistoryDao에 전달함

  - #### Repository

##### ParkingRegularTicketDao.haveRegularTicket
```java
    @Override
    public boolean haveRegularTicket(String car_num) throws Exception { //정기주차권 있는지 여부
        return session.selectOne(namespace + "haveTicket", car_num);
    }

```

##### MyBatis Mapping
``` xml
<select id="haveTicket" parameterType="String" resultType="boolean">
    SELECT EXISTS (
    SELECT 1 FROM regular_parking_ticket WHERE carNumber = #{carNumber}
    )
    </select>
```
regular_parking_ticket 테이블에서 특정 차 번호가 있는지 여부를 반환한 것을 그대로 반환


##### parkingCarDao.enter
```java
  @Override
    public int enter(Car car) throws Exception { //입차
        return session.insert(namespace+"enter", car);
    }
```

```xml
<insert id="enter" parameterType="Car">
        INSERT INTO parking
            (carNumber, enterDate, haveRegularParkingTicket)
        VALUES
            (#{carNumber}, #{enterDate, jdbcType=TIMESTAMP}, #{haveRegularParkingTicket, jdbcType=BOOLEAN})
    </insert>

```

##### ParkingHistoryDao.enter
```java
    @Override
    public int enter(Car car) throws Exception { //입차
        return session.insert(namespace+"enterHis", car);
    }
```

```xml
<insert id="enterHis" parameterType="Car">
        INSERT INTO parking_history
            (carNumber, enterDate, haveRegularParkingTicket)
        VALUES
            (#{carNumber}, #{enterDate, jdbcType=TIMESTAMP}, #{haveRegularParkingTicket, jdbcType=BOOLEAN})
    </insert>
```
parking과 parking_history 테이블에 차량 번호, 입차 일시, 정기 주차권 여부를 기록

</details>
   

### 기능 3 : 출차하기 페이즈 1

#### 3-1 : myproject/parking/exit getMapping을 postMapping으로 돌리기

<details>


<summary>기능 3-1 구현 코드 펼치기</summary>

```java
@GetMapping("/exit")
    public String exit_car() {
        return "exit";
    }


    @PostMapping("/exit")
    public String exitCarPhase1(@Valid Car car, BindingResult result, Model m, RedirectAttributes redirectAttributes) {
```
2-1과 동일한 로직


</details>

#### 3-2 : CarDto 검증 - 차량 번호 & 지불한 값

<details>


<summary>기능 3-2 구현 코드 펼치기</summary>


##### Controller
```java
 @PostMapping("/exit")
    public String exitCarPhase1(@Valid Car car, BindingResult result, Model m, RedirectAttributes redirectAttributes) {

        // Car 객체를 검증한 결과 에러가 있으면, 리다이렉트
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "출차를 실패하였습니다. 올바른 번호를 입력해주세요.");
            System.out.println("출차 검증 실패");
            return "redirect:/parking/exit";
        }
```


##### CarValidator
2-2와 같은 차량 번호 검증 메서드를 사용하고, 지불한 돈이 올바른 0이상 정수인지 검증함


</details>


#### 3-3 본격적인 출차 페이즈 1 로직

<details>


<summary>기능 3-3 구현 코드 펼치기</summary>


 ##### Controller
 ``` java
 @PostMapping("/exit")
    public String exitCarPhase1(@Valid Car car, BindingResult result, Model m, RedirectAttributes redirectAttributes) {

        //...생략

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
```
parkingService로부터 주차요금을 반환 받되, -1을 받았을 경우 입차했던 차가 아니라는 알림과 함께 리다이렉트<br>
-1이 아닐 경우 모델에 car객체를 담고 exitPay.jsp를 반환

##### exitPay.jsp view
```jsp
<div class="container">
<form:form action="/myproject/parking/exit2" method="post" modelAttribute="car">
    <div class="title">주차비 ${car.fee}원 입니다</div>
    <input class="input-field" type="text" id="paidFee" name="paidFee" placeholder="0이상 정수로 기입하시오" oninput="checkInt()">
    <div id="message"></div>
    <button type="submit" id="submitButton" disabled>지불하기</button>
    <input type="hidden" id="carNumber" name="carNumber" value="${car.carNumber}">
    <input type="hidden" id="fee" name="fee" value="${car.fee}">
    <input type="hidden" id="enterDate" name="enterDate" value="${car.enterDate.getTime()}">
    <input type="hidden" id="haveRegularParkingTicket" name="haveRegularParkingTicket" value="${car.haveRegularParkingTicket}">
```
정산 요금을 출력하고 car객체를 다시 post 방식으로 출차 페이즈 2에 요청


##### Serivce
```java
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
```
enteredBefore 메서드는 2-3의 document_RegularTicket 메서드랑 같은 로직을 사용<br>
car2 인스턴스를 생성하여 parkingCarDao에서 온 car 타입 반환값을 참조<br>
이후 주차요금을 계산하여 car에 입차시각, 출차시각, 주차요금을 기록

##### Repository
- ##### ParkingCarDao

```java
@Override
    public Car select(String carNumber) throws Exception { //선택
        return session.selectOne(namespace + "select", carNumber);
    }
```

carNumber를 키 값으로 하는 데이터를 반환한 값을 그대로 반환

</details>


### 기능 4 : 출차하기 페이즈 2



3-3에서 알 수 있듯이, Cardto 파라미터로 넘어옴
  #### 4-1 : CarDto 검증 - 차량 번호 & 지불한 값

<details>


<summary>기능 4-1 구현 코드 펼치기</summary>
  
##### Controller
```java
 @PostMapping("/exit")
    public String exitCarPhase1(@Valid Car car, BindingResult result, Model m, RedirectAttributes redirectAttributes) {

        // Car 객체를 검증한 결과 에러가 있으면, 리다이렉트
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("message", "출차를 실패하였습니다. 올바른 번호를 입력해주세요.");
            System.out.println("출차 검증 실패");
            return "redirect:/parking/exit";
        }
```


##### CarValidator
```java
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
//생략
...
```
2-2와 같은 차량 번호 검증 메서드를 사용하고, 지불한 돈이 올바른 0이상 정수인지 검증함

</details>

#### 4-2 : 본격적인 출차하기 페이즈 2 로직

<details>


<summary>기능 4-2 구현 코드 펼치기</summary>

##### Controller
```java
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

```
parkingService를 이용하여 반환한 값이 false일때 잔액부족으로 리다이렉트<br>
true일때는 출차가 완료되었다는 알림창과 함께 index.jsp인 메인으로 반환

##### Service
```java
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
```
carDto에 출차한 시간을 기록하고 parkingCarDao에는 carNumber를, parkingHistoryDao에는 carDto를 전달

##### Repository
##### parkingCarDao
```java
@Override
    public int exit(String carNumber) throws Exception { //출차
        return session.delete(namespace+"exit", carNumber);
    }
```

##### MyBatis Mapping
```xml
<delete id="exit" parameterType="String">
        DELETE FROM parking WHERE carNumber = #{carNumber}
</delete>

```
parking 테이블에서 carNumber를 key로 하는 데이터를 삭제 함


##### parkingHistoryDao
```java
@Override
    public int exitVer2(Car car) throws Exception { //출차 버전 2
        return session.update(namespace+"exitHis2", car);
    } 
```

##### MyBatis Mapping
```xml
<update id="exitHis2" parameterType="Car">
        UPDATE parking_history
        SET exitDate = #{exitDate, jdbcType=TIMESTAMP}, fee = #{fee}, paidFee = #{paidFee}, changeFee = #{changeFee}
        WHERE carNumber = #{carNumber}
    </update>
```
반면에 parking_history 테이블에서는 carDto로 업데이트 함

</details>






 
