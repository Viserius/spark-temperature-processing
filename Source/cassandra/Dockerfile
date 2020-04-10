FROM cassandra:latest

COPY cassandra-init.sh /cassandra-init.sh
COPY ready-probe.sh /ready-probe.sh
ENTRYPOINT ["/cassandra-init.sh"]
CMD ["cassandra", "-f"]
