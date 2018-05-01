FROM ubuntu:16.04
MAINTAINER gpwclark@gmail.com

# install ubuntu prereqs to install nfd
RUN apt-get update && apt-get install -y \
  vim \
  software-properties-common

# install nfd
RUN  add-apt-repository -y ppa:named-data/ppa
RUN  apt-get update
RUN  apt-get install -y ndn-cxx
RUN apt-get update
RUN apt-get install --fix-missing
RUN  apt-get install -y ndncert \
						ndn-tools \
						nfd \
						nlsr \
						libchronosync \
						ndns \
						repo-ng

COPY nfd.conf.sample /etc/ndn/nfd.conf

EXPOSE 6363
EXPOSE 9696
EXPOSE 56363

# Housekeeping
RUN apt-get clean
RUN rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

VOLUME ["/nfd/etc"]

RUN ndnsec-keygen /localhost/operator | ndnsec-install-cert -
#TODO having trouble with nfdc route add, this was stolen from
# nfd-start script. Ideally want to use that script BUT
# with the correct nfd.conf.

#run nfd
ENTRYPOINT ["/usr/bin/nfd"]
CMD ["-c", "/etc/ndn/nfd.conf"]
