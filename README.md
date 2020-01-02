# spectre-graphql-maven-plugin
[![Build Status](https://travis-ci.org/patrickp89/spectre-graphql-maven-plugin.svg?branch=master)](https://travis-ci.org/patrickp89/spectre-graphql-maven-plugin)

A GraphQL Java type generator.

## What?
This Maven plugin lets you generate Java classes for your GraphQL documents. It will generate the POJOs for all the
GraphQL types in your GraphQL queries and (in the future hopefully) some lightweight client code for accessing the GraphQL API.

## Why?
Is there really a need for yet another GraphQL tool? I think, there is: I found the existing GraphQL code generation
solutions for Java/Kotlin/... to be rather unsatisfying.

## How?
This project is work in progress! Right now the primary goal is to generate POJOs for GraphQL introspection results. Just
like good ol' [WSDL](https://en.wikipedia.org/wiki/Web_Services_Description_Language) the result of a
[GraphQL introspection query](https://graphql.org/learn/introspection/) is a handy interface description.
