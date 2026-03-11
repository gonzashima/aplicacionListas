# ---- Build stage ----
FROM node:20-alpine AS build
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . .
RUN npm run build

# ---- Run stage (nginx sirve el build de React) ----
FROM nginx:alpine

COPY --from=build /app/build /usr/share/nginx/html

# Copiar la config como template — envsubst la procesa al arrancar
COPY nginx.conf /etc/nginx/templates/default.conf.template

EXPOSE 80

# nginx:alpine con la carpeta /etc/nginx/templates/ corre envsubst
# automáticamente antes de iniciar nginx (comportamiento built-in de la imagen oficial)
CMD ["nginx", "-g", "daemon off;"]
