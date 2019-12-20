FROM postgres

ENV POSTGRES_USER example
ENV POSTGRES_PASSWORD example
ENV POSTGRES_DB orders

COPY init.sql /docker-entrypoint-initdb.d/
