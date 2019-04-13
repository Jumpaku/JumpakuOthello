FROM node:10.7.0-alpine

WORKDIR /home/app

COPY ./package.json /home/app
RUN npm install -y

ENTRYPOINT [ "npm" ]
CMD [ "start" ]