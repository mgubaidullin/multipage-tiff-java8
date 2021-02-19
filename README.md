# Multipage TIFF splitter and converter to PNG

![Build](https://img.shields.io/badge/Build-Java_8-orange.svg?style=for-the-badge&logo=java)
![Fuse](https://img.shields.io/badge/-Fuse_7.8-orange.svg?style=for-the-badge)
![License](https://img.shields.io/badge/License-Apache-green.svg?style=for-the-badge&logo=apache)

## Build and run locally
Requires Git, Maven 3.6 and Java 8 installed.
```
git clone git@github.com:mgubaidullin/multipage-tiff-java8.git
mvn clean package
java -jar target/multipage-tiff-java8-1.0.jar 
```
## Build and run on OpenShift
Requires OpenShift cluster installer or CRC

1. Build with a standard Java 8 s2i image
```
oc new-build  fabric8/s2i-java~https://github.com/mgubaidullin/multipage-tiff-java8.git --name=multipage-tiff-java8
```
2. Create a ConfigMap from the "small" tif file
```
oc create configmap tifs --from-file=small.tif
```
3. Mount the ConfigMap as a Volume in your Deployment, for example [deployment.yml](deployment.yml)
```
oc apply -f deployment.yml
```
4. Restart the Deployment
5. Watch the logs.
