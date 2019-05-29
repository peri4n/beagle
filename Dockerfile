############################
# Start of the build stage #
############################
FROM node:8 as build-stage

# Create app directory
WORKDIR /usr/src/build

# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied
COPY package*.json ./
RUN npm install

# Copy sources
COPY app ./app/

# Copy Webpack config
COPY webpack.config.js ./

# Copy Babel config
COPY .babelrc ./

# Build the UI
ARG commit_hash
ENV COMMIT_HASH=$commit_hash
RUN npm run build

############################
# Start of the run stage   #
############################
FROM nginx:1.15
COPY --from=build-stage /usr/src/build/dist /usr/share/nginx/html
