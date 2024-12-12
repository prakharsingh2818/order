# Stage 1: Build the Scala Play application
#FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1 AS build
#FROM eclipse-temurin:17-jdk-jammy AS build

#RUN apt-get update && \
#    apt-get install -y scala

#RUN curl -sL "https://github.com/sbt/sbt/releases/download/v1.9.1/sbt-1.9.1.tgz" | gunzip | tar -x -C /usr/local
#RUN ln -s /usr/local/sbt/bin/sbt /usr/local/bin/sbt
#RUN chmod 0755 /usr/local/bin/sbt
#RUN mkdir -p .sbt ivy2

# Set the working directory
#WORKDIR /app
# Copy the project files
#COPY . .

# Update dependencies and compile the app
#RUN sbt clean compile
# Package the application (creates a universal package with all dependencies)
#RUN sbt dist

#FROM eclipse-temurin:17-jre-jammy AS runtime
# Set environment variables
#ENV APP_HOME=/app
#WORKDIR $APP_HOME

# Copy the distribution from the build stage
#COPY --from=build /app/target/universal/*.zip ./

# Unzip the application package
#RUN apt-get update && apt-get install -y unzip && \
 #   unzip /app/target/universal/*.zip && \
  #  mv /app/$(ls -d */ | grep order)/* . && \
   # rm -rf *.zip /app/$(ls -d */ | grep order)

#RUN mv /app/order-0.1.0-SNAPSHOT/bin/order .

#RUN apt-get install docker-compose
#RUN ln -s /usr/local/bin/docker-compose /compose/docker-compose


# Expose the default Play port
#EXPOSE 7000
#WORKDIR /app/order-0.1.0-SNAPSHOT
#RUN cd /app/order-0.1.0-SNAPSHOT
# Run the Play application
#CMD ["order", "-Dplay.http.secret.key=your-secret-key", "-Dhttp.port=7000", "-Dhttp.address=0.0.0.0"]


FROM eclipse-temurin:17-jdk-jammy
# Set the working directory inside the container
WORKDIR /opt/play

# Copy the application source code
ADD . /opt/play

# Install sbt from the official repository
RUN apt-get update && apt-get install -y curl gnupg && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823" | apt-key add - && \
    apt-get update && apt-get install -y sbt && \
    apt-get clean

# Build the application
RUN sbt stage

# Expose the default Play application port
EXPOSE 9000

# Define the command to directly run the Play application
CMD ["target/universal/stage/bin/order", "-Dplay.http.secret.key=your-secret-key", "-Dhttp.port=7000", "-Dconfig.resource=application.conf"]
