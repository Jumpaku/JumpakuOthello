FROM gradle:5.4.0-jre8-alpine

WORKDIR /home/app/
COPY ./ /home/app/

CMD [ "ash" ]