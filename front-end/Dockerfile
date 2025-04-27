FROM node:20-alpine AS build
WORKDIR /app

# Declare build arguments received from docker-compose
ARG VITE_GEOAPI_BASE_URL
ARG VITE_GEOAPI_KEY
ARG VITE_MAX_AVATAR_SIZE_MB
ARG VITE_API_BASE_URL
ARG VITE_WS_URL

# Set them as environment variables for the build process
# Otherwise, Vite can't access them, they are undefined and
# bing bang bosh you debug for 12 hours why websocket is not fucking working
ENV VITE_GEOAPI_BASE_URL=$VITE_GEOAPI_BASE_URL
ENV VITE_GEOAPI_KEY=$VITE_GEOAPI_KEY
ENV VITE_MAX_AVATAR_SIZE_MB=$VITE_MAX_AVATAR_SIZE_MB
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
ENV VITE_WS_URL=$VITE_WS_URL

RUN npm install -g pnpm
COPY package*.json .
RUN pnpm install
COPY . .
RUN pnpm build

# Production stage
FROM alpine:3.20 AS final
COPY --from=build /app/dist /usr/share/nginx/html
WORKDIR /usr/share/nginx/html
