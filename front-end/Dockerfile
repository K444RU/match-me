FROM node:18-alpine AS build
WORKDIR /app

# 1. Accept the arguments
ARG VITE_GEOAPI_BASE_URL
ARG VITE_GEOAPI_KEY
ARG VITE_MAX_AVATAR_SIZE_MB

# 2. Assign them to environment variables inside the build container
ENV VITE_GEOAPI_BASE_URL=$VITE_GEOAPI_BASE_URL
ENV VITE_GEOAPI_KEY=$VITE_GEOAPI_KEY
ENV VITE_MAX_AVATAR_SIZE_MB=$VITE_MAX_AVATAR_SIZE_MB

# Then do install
COPY package*.json ./
RUN npm install

COPY . .

# 3. Vite sees them at build time (due to the ENV lines)
RUN npm run build

# Production stage
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
