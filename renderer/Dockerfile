FROM node:18-bookworm-slim

RUN apt-get update && apt-get install -y \
    chromium \
    --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src/app

ENV PUPPETEER_SKIP_DOWNLOAD=true \
    PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=true \
    PUPPETEER_EXECUTABLE_PATH=/usr/bin/chromium \
    REMOTION_PUPPETEER_EXECUTABLE_PATH=/usr/bin/chromium

COPY package*.json ./

RUN --mount=type=cache,target=/root/.npm \
    npm install --legacy-peer-deps --prefer-offline

COPY . .

EXPOSE 3030

CMD [ "npm", "start" ]
