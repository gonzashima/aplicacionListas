# ---- Build stage ----
FROM node:20-alpine AS build
WORKDIR /app

ARG REACT_APP_BACKEND_URL
ENV REACT_APP_BACKEND_URL=$REACT_APP_BACKEND_URL

COPY package.json ./
RUN npm install
COPY . .
RUN npm run build

# ---- Run stage (serve sirve los archivos estáticos) ----
FROM node:20-alpine
RUN npm install -g serve
COPY --from=build /app/build /app/build
EXPOSE 3000
CMD ["serve", "-s", "/app/build", "-l", "3000"]
