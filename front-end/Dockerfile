# 1. Build stage
FROM node:18-alpine AS build
WORKDIR /app

# Copy package.json and lock file first
COPY package*.json ./
RUN npm install

# Copy the rest of the source code and build
COPY . .
RUN npm run build

# 2. Production stage
FROM nginx:alpine
# Copy build artifacts from 'build' stage
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
