# Ssaemsung
**음성 기록 및 자동 요약 어플리케이션**

![GitHub last commit](https://img.shields.io/github/last-commit/CSID-DGU/2021-2-OSSP1-Ssaemsung-4)
![Most used language](https://img.shields.io/github/languages/top/CSID-DGU/2021-2-OSSP1-Ssaemsung-4)



## Contents
* [About Team](#about-team)


## About Team


|이름|학과|역할|
|------|---|---|
|[조현준](https://github.com/chohj1111) [![](https://img.shields.io/badge/Github-chohj1111-blue?style=flat-square&logo=Github)](https://github.com/chohj1111)|컴퓨터공학전공|팀장 / 텍스트 요약 모델 구현 및 서버 배포|
|[조봉민](https://github.com/BongMinJo) [![](https://img.shields.io/badge/Github-BongMinJo-blue?style=flat-square&logo=Github)](https://github.com/BongMinJo)|멀티미디어공학과|안드로이드 App UI 및 기능 개발|
|[박예찬](https://github.com/eric9883) [![](https://img.shields.io/badge/Github-eric9883-blue?style=flat-square&logo=Github)](https://github.com/eric9883)|컴퓨터공학전공| 텍스트 요약 모델 구현 및 서버 배포|
|[전휘준](https://github.com/MatthewJeon) [![](https://img.shields.io/badge/Github-MatthewJeon-blue?style=flat-square&logo=Github)](https://github.com/MatthewJeon)|컴퓨터공학전공|STT & 화자구분 서버 배포|




## 개발 환경 및 Dependency 
**Android**

<a href="https://www.java.com/en/"><img src="https://img.shields.io/badge/-Java 11-007396?style=flat&logo=Java"></a> <a href="https://developer.android.com/"><img src="https://img.shields.io/badge/-Android 10-3DDC84?style=flat&logo=Android"></a> <br>

**Speech-to-text & 화자구분**

<img src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=flat-square&logo=spring-boot"> <img src="https://img.shields.io/badge/Google_Cloud_Platform-4285F4?style=flat-square&logo=google">

**텍스트 요약**

<img src="https://img.shields.io/badge/DJANGO-REST-ff1709?style=flat-square&logo=DJANGO"> <img src="https://img.shields.io/badge/huggingface-F5C518?style=flat-square"> <img src="https://img.shields.io/badge/Google_Cloud_Platform-4285F4?style=flat-square&logo=google">



## 프로젝트 구조도 및 기능

**프로젝트 구조도**

<img width="80%" src="https://user-images.githubusercontent.com/73880543/146599584-d1766579-2c7a-4f14-bf59-277a649aa58b.png"/>

<br><br>

**Speech-to-text & 화자구분**

<img width="80%" src="https://user-images.githubusercontent.com/73880543/146599862-fc8e3cfb-2094-4cde-bfed-88ad3dede41f.png"/>



![Stt 프로세스](https://user-images.githubusercontent.com/73880543/146600257-b40b2030-f53e-4c4f-922d-4092fce5a69c.png)
1. wav 파일 POST 요청
2. Google Cloud Storage에 파일 업로드
3. Speech API 호출하여 STT&화자구분 진행 후 JSON 형식으로 결과 출력
4. 업로드된 파일 삭제

<br>
**어플로 전송되는 JSON contents**

<img width="80%" src="https://user-images.githubusercontent.com/73880543/146600688-7637835b-0a32-44fb-91e2-56f949962603.png"/>

<br>





**Text Summarization**

<img width="80%" src="https://user-images.githubusercontent.com/73880543/146600153-0c7f0d27-2649-4ba8-baa9-40b4c8d91764.png"/>
<br>

## APP


**메인 화면**

<img width="250" src="https://user-images.githubusercontent.com/73880543/146601321-174f310a-0bb7-4c86-8551-73fa942ccac7.png"/> <img width="250" src="https://user-images.githubusercontent.com/73880543/146601324-d7020e81-58ef-4475-b454-1eabbce07bb4.png"/>

**실제 구동 캡쳐**

녹음기능<br>

<img width="250" src="https://user-images.githubusercontent.com/73880543/146601518-dc073928-2869-4df0-9aa8-3fa89fcd4647.png"/>

녹음파일 불러오기<br>

<img width="250" src="https://user-images.githubusercontent.com/73880543/146601516-264619c6-7182-41e0-a509-42118cffd0f7.png"/>


녹음본 텍스트화<br>
<img width="250" src="https://user-images.githubusercontent.com/73880543/146601522-371b8aa6-7c40-4438-8c2e-5ace80f282cd.png"/>
<img width="250" src="https://user-images.githubusercontent.com/73880543/146601512-eb832999-2d13-4dc2-83f0-42824e7534ad.png"/>
<img width="250" src="https://user-images.githubusercontent.com/73880543/146601513-107b1b62-7bd0-4605-8290-64e76ba6acab.png"/>


텍스트 요약 <br>
<img width="250" src="https://user-images.githubusercontent.com/73880543/146601519-03fa556f-20dd-4268-acbd-ce3273e98965.png"/>

