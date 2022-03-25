FROM openjdk:11

COPY ./build/libs/art_gallery-0.0.1-SNAPSHOT.jar /art_gallery/libs/art_gallery.jar

WORKDIR /hello/libs/

CMD ["java", "-jar","/art_gallery/libs/art_gallery.jar"]