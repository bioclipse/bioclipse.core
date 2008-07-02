===========================
== Simple Petclinic Demo ==
===========================

1. MOTIVATION

As the name implies, this is a simple demo that illustrates how Petclinic 
can be split into several modules. While this demo has been used at various
presentations it is not very friendly. Work is underway to provide a more
comprehesive sample to migrate entire Petclinic to OSGi.

The demo contains 4 maven projects:

* petclinic-sample-contract
which contains some DAO interfaces that can be reused throughout the project

* petclinic-sample-jdbc
which contains JDBC implementations for the DAO interfaces mentioned in the
bundle above

* petclinic-sample-hsqldb
a HSQLDB data source

* petclinic-sample-mysql
a MYSQL data source


This sample doesn't contain any tests and it meant to provide swappable components
(such as different data sources) to an existing application. Once deployed, the user
can shutdown one data source and enable a new one - through Spring-DM the changes
are going to be applied automatically to the existing application which continues
to run.

2. BUILD AND DEPLOYMENT

This directory contains the source files.
For building, Maven 2 and JDK 1.4 are required.