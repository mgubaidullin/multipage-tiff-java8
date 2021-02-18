# Multipage TIFF splitter and converter to PNG

![Build](https://img.shields.io/badge/Build-Java_8-orange.svg?style=for-the-badge&logo=java)
![Fuse](https://img.shields.io/badge/-Fuse_7.8-orange.svg?style=for-the-badge)
![License](https://img.shields.io/badge/License-Apache-green.svg?style=for-the-badge&logo=apache)

## Build and run
Requires Git, Maven 3.6 and Java 8 installed.
```
git clone git@github.com:mgubaidullin/multipage-tiff-java8.git
mvn clean package
java -jar target/multipage-tiff-java8-1.0.jar 
```

## Testing on OpenShift

1. Build with a standard Java 8 s2i image.
2. Create a ConfigMap from the "small" tif file. `oc create configmap tifs --from-file=small.tif`
3. Mount the ConfigMap as a Volume in your Deployment, for example:
```
kind: Deployment
apiVersion: apps/v1
metadata:
  name: multipage-tiff-java-8
  namespace: tiff-test
  labels:
    app: multipage-tiff-java-8
spec:
  replicas: 1
  selector:
    matchLabels:
      app: multipage-tiff-java-8
  template:
    metadata:
      creationTimestamp: null
      labels:
        app: multipage-tiff-java-8
        deploymentconfig: multipage-tiff-java-8
    spec:
      volumes:
        - name: config-volume
          configMap:
            name: tifs
            defaultMode: 420
      containers:
        - name: multipage-tiff-java-8
          image: ...
          ports:
            - containerPort: 8080
              protocol: TCP
          resources: {}
          volumeMounts:
            - name: config-volume
              mountPath: /data/tifs
...
```
4. Restart the Deployment
5. Watch the logs.
