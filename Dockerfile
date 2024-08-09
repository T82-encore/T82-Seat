#base image
FROM openjdk:17-slim
#내 파일에서 이미지로 복사 해오기
COPY ./build/libs/*T.jar app.jar
RUN ["java" ,"-version"]
#ENV profiles prod

#docker run 시 실행할 명령어
CMD ["java","-jar","-Dspring.profiles.active=${profiles}","app.jar"]
EXPOSE 8080 9090