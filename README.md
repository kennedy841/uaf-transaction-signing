# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/maven-plugin/)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)


####verify challenge

```
{
  "challengeAnswer": {
    "pocketCode": 0
  },
  "jwkKey": {
    "ellipticCurve": "secp256r1",
    "keyType": "EC",
    "x": "108626610445227267021216753531011922029775994809025001798123780253931362059287",
    "y": "13775089719733830772307592798135791007264440289750994841598233847637616238615"
  }
}
```

create docker image using google maven docker plugin

```
$ ./mvnw com.google.cloud.tools:jib-maven-plugin:dockerBuild -Dimage=snapoli/uaf-api-image

docker run -p 8080:8080 -t snapoli/uaf-api-image

```

import into kubernetes secret the docker config json with credential to private api

```

kubectl create secret generic regcred \
    --from-file=.dockerconfigjson=/Users/salvatore/.docker/config.json \
    --type=kubernetes.io/dockerconfigjson

 kubectl get secrets regcred -o yaml
 kubectl get secret regcred --output="jsonpath={.data.\.dockerconfigjson}" | base64 --decode

```

create secret from command line
```
kubectl create secret docker-registry regcred --docker-server=<your-registry-server> --docker-username=<your-name> --docker-password=<your-pword> --docker-email=<your-email>

```

