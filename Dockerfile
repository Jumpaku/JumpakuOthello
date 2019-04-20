FROM openjdk:8u201-jdk-alpine3.9

COPY ./web/ /home/app/web/
COPY ./api/ /home/app/api/

RUN apk add --no-cache --virtual web-dependency npm

WORKDIR /home/app/web/
RUN npm install && npm run build
RUN cp -r /home/app/web/dist /home/app/api/src/main/resources/

WORKDIR /home/app/api/
RUN ./gradlew build

RUN apk del --purge web-dependency
RUN rm /home/app/web/* -rf

ENTRYPOINT [ "/home/app/api/gradlew" ]
CMD [ "run" ]