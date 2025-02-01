# Use the official Nginx base image
FROM nginx:alpine

# Copy a custom nginx.conf to override default
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80 for the proxy
EXPOSE 80

# Run Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]
