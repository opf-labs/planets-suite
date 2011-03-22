#
# This script installs everything that is currently packaging properly
# although unit tests are not yet executed.
#

MVN='clean install -Dmaven.test.skip=true'

cd core-services; mvn $MVN; cd ..
cd core-techreg; mvn $MVN; cd ..
cd core-utils; mvn $MVN; cd ..
cd framework-generic; mvn $MVN; cd ..
cd framework-admin; mvn $MVN; cd ..
# cd framework-selfreg; mvn $MVN; cd ..
# cd framework-servreg; mvn $MVN; cd ..
# cd framework-storage; mvn $MVN; cd ..
# cd framework-techreg; mvn $MVN; cd ..
# cd framework-users; mvn $MVN; cd ..
# cd framework-wdt; mvn $MVN; cd ..
# cd framework-wee; mvn $MVN; cd ..
# cd services-droid; mvn $MVN; cd ..
cd services-javadigest; mvn $MVN; cd ..
cd services-javase; mvn $MVN; cd ..
cd services-jhove; mvn $MVN; cd ..
cd services-jtidy; mvn $MVN; cd ..
# cd services-libtiff; mvn $MVN; cd ..
# cd services-metadata; mvn $MVN; cd ..
cd services-odf; mvn $MVN; cd ..
cd services-pdfbox; mvn $MVN; cd ..
cd services-sanselan; mvn $MVN; cd ..
cd services-simple; mvn $MVN; cd ..
# cd webapp-plato; mvn $MVN; cd ..
# cd webapp-testbed; mvn $MVN; cd ..

