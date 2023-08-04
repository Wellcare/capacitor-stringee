# PREPARE NODE_MODULES IN PRODUCTION MODE
FROM alvrme/alpine-android:android-33-jdk17 as mobile-builder
RUN apk update \ 
    && apk upgrade --available \
    && apk --no-cache --virtual add git curl python3 py3-pip make g++ gcc nodejs npm
ARG GIT_TOKEN
ENV GIT_TOKEN=$GIT_TOKEN
RUN npm config set //npm.pkg.github.com/:_authToken='${GIT_TOKEN}'
RUN npm set @wellcare:registry https://npm.pkg.github.com
RUN npm config list
RUN npm install -g yarn 
WORKDIR /usr/src/app

# step 1: install dependency
COPY package.json ./
RUN yarn install --silent

# step 2: build
COPY . .
COPY .env.build .env
ARG GIT_TAG
ENV GIT_TAG=$GIT_TAG
RUN yarn build

# step 3: TESTS
RUN yarn verify:web
RUN yarn run verify:android
# RUN yarn verify:ios
RUN node build.js

# step 4: release and publish
FROM mhealthvn/node-builder:master as publisher
COPY --from=mobile-builder /usr/src/app/lib lib
COPY package.json .
RUN yarn publish:package