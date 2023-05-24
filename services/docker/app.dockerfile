FROM eclipse-temurin:17.0.6_10-jre
# Add custom user and setup home directory
RUN adduser --shell /bin/true --uid 1000 --disabled-password --home /app-home app-user \
  && chown -R app-user /app-home \
  && chmod 700 /app-home
WORKDIR /app-home
USER app-user
ADD ./app-bundle.tar ./
WORKDIR /app-home/app-bundle
CMD ./bin/app