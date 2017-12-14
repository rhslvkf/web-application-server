# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* Stream을 필요에 따라 read할 수 있는 단위를 결정할 수 있게 여러 method를 제공하고 있다.
HTTP request의 경우에는 line별로 읽고, HTTP response body의 경우에는 byte 전체를 읽는다.
Stream을 상황에 따라 적절하게 read하는 게 중요한 것 같다.

### 요구사항 2 - get 방식으로 회원가입
* 

### 요구사항 3 - post 방식으로 회원가입
* 

### 요구사항 4 - redirect 방식으로 이동
* 웹브라우저는 이전 요청 정보를 유지하고 있기 때문에 새로고침을 하게 되면 이전 요청 정보를 재요청하게 된다.

`HTTP status code : 200`

서버로 요청한 정보 유지

`HTTP status code : 302`

response에 Location값을 넣어 보내면 웹브라우저에서 서버로 해당 Location으로 재요청하게 된다.
따라서, Location값이 이전 요청 정보가 된다.

회원 가입같은 로직의 경우 302 HTTP status code를 사용해서 반드시 리다이렉트를 해주어야 한다.
라이브러리나 프레임워크에서 리다이렉트를 사용할 경우 내부적으로 302 상태 코드를 이용하는 것으로 생각하면 된다.

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
