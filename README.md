java를 사용하여 작업을 할 때 '동시성' 문제를 마주칠 수 있는데 이를 해결하기 위해 java에서 어떤 기능을 제공하는지 찾게되었다.

해당 문제가 발생하는 이유부터 알아보았다.

## 1. 공유자원 문제


Effective JAVA 책에서 '동시성' 챕터를 보면 첫 번째 아이템으로 이런 문구가 적혀있다.

"공유중인 가변 데이터는 동기화해 사용해라"


공유중인 가변 데이터를 '공유 자원(혹은 객체)'라고 표현해 보겠다.   
공유 자원이란건 무엇인가? 멀티 스레드 환경에서 서로 공유하는 자원을 의미한다.

![image](https://github.com/user-attachments/assets/60d63b98-a234-4881-b504-99b5ca3dfbd5)

문제는 공유자원에 대한 스레드간 경합이 발생하는데 이를 '경쟁 상태(race condition)'이라고 한다.  
그리고 그런 문제가 발생할 수 있는 영역을 '임계영역'이라고 한다.

그래서 '스레드 동기화'는 경쟁 상태를 해결하는 방법이고 이는 다른 말로 '가시성'과 '원자성'을 챙긴다고도 한다.

"멀티 스레드를 구성하다 보니 비가시성, 비원자성 문제가 발생했다는 의미"

둘에 대해 알아보자.

## 2. 가시성

코드로 좀 쉽게 설명을 해보겠다.

![image](https://github.com/user-attachments/assets/47c4b026-c0a1-4f0e-82b4-b7633f9a1cdc)

① 메모리에 초기값이 false인 변수(visible)를 하나 저장  
② 변수가 true로 변경될 때 까지 감지하는 스레드(DetectVariable)를 실행시키고  
③ 메인 메서드에서는 5초 뒤에 변수(visible)를 true로 변경  
④ 스레드(DetectVariable)는 변수(visible)가 true가 됨을 알 수 있을까?

![image](https://github.com/user-attachments/assets/b72e05d7-259d-4b7c-8c39-8914e8f553ff)

정답은 x. 스레드는 변경된 변수의 값을 제대로 바라보지 않고 있기에 종료가 되지 않게 된다.  
왜 제대로 보지 못하는 걸까?

![image](https://github.com/user-attachments/assets/52514bae-6a5a-40cd-ae19-736719c62d22)

변수는 하드웨어의 main memory에 적재가 된다.  
Thread는 동작하는 시점에 하나의 cpu(core가 더 정확한 의미)를 점유하고 동작을 하는데  
선언한 변수의 값이 memory가 아닌 cpu cache에도 저장을 한다. 시간 단축을 위해,,,

![image](https://github.com/user-attachments/assets/e15cd98d-3f38-4d8a-9a74-56aa5fe525c4)

하지만 이때 main thread가 값을 변경하여 main memory의 값으로 옮길지라도  
thread1은 cache에 있는 값을 계속 보게 되는 것이다.  

따라서 '비 가시성'이라는 의미는 memory에 있는 값을 제대로 보지 못한다는 의미이다.

## 3. volatile을 통한 가시성 확보

Java 에서는 volatile이란 변수를 지원하는데 변수 선언시 앞에 적어주기만 하면 된다.

![image](https://github.com/user-attachments/assets/65478312-cbe1-4397-945f-b6ef4a9a243b)

volatile은 선언된 변수를 cpu cache가 아닌 main memory에 저장한다는 의미이다.

![image](https://github.com/user-attachments/assets/65b8797e-2eb1-4c4f-b620-696e18fa7566)

따라서 스레드는 성공적으로 변수의 변경을 보게된다.   이렇게 volatile은 가시성을 챙길 수 있게 한다.

하지만 앞에서 말했듯이 가시성을 챙긴다고 '동기화'를 완벽하게 할 수 있는 것이 아니다.

## 4. 원자성

원자성은 공유 자원에 대한 작업의 단위가 더이상 쪼갤 수 없는 하나의 연산인 것처럼 동작하는 것을 말한다.

사실 우리가 보기엔 하나의 과정 처럼 보이는 '프로그램 언어적 표현'은 기계(cpu)가 읽을 때는 여러 과정으로 나눠지게 된다.

![image](https://github.com/user-attachments/assets/becf7a15-7f49-4238-824e-a956cf6c239c)

volatile 변수여도 읽기(read) → 연산하기(modify) → 저장하기(write) 와 같은 여러 instruction이 수행될 것  
이 instruction은 다른 Thread, cpu의 개입이 있을 수 없는 원자 단위의 연산이 된다.

하지만 instruction사이에 다른 Thread가 공유 자원을 접근하는 문제가 발생할 수 있게 된다.  
이렇게 우리가 다루고자 하는 연산을 원자 단위로 만드는 것은 복잡하고 어려운 일이다.

그래서 개발자는 동기화 처리(원자성과 가시성을 챙기는)를 통해 Thread 안정성을 확보해야 한다.  

## 5. 내가 구현에 사용했던 것

비즈니스 로직에서 발생할 수 있는 동시성 문제에 대한 상황은 다음과 같았다.

"동시에 여러 요청이 들어오더라도 순서대로 (혹은 한번에 하나의 요청씩만) 제어될 수 있도록 리팩토링"

처음엔 포인트 충전/이용에 대한 비즈니스 로직 전체를 synchronized 하는 방법을 생각했다.  
하지만 이는 A라는 사람에게 충전 요청이 들어오면 B라는 사람에게 충전 요청이 들어와도 서비스 로직을 진행할 수 없는 상황이 만들어졌다.

서로 다른 사람의 계정에 대해 동시성 문제를 고려하는 것은 옳은 방향이 아니었다.

따라서 각각의 사람에 대해 따로 동시성 문제를 적용했다.  
ConcurrentHashMap과 Lock을 통해 위와 같은 상황에 대해 Thread-safe한 구현을 하게 되었다.