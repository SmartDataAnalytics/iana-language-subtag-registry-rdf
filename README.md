# iana-language-subtag-registry-rdfizer

The [IANA language subtag registry](http://www.iana.org/assignments/language-subtag-registry/language-subtag-registry) is a resource that
lists valid values for use in the components of BCP47 language tags. The format is a set of records with key-value pairs.


This repo contains two independent but related Java/Maven projects.

* The RDFizer itself which is just a single class
* A resource project which just contains the generated dataset

## RDFization
The core RDFization is 'raw' - each record is represented by a blank node,
each key is turned into an IRI of the form <urn:key> and the value becomes a literal.
This way no information is lost and the alignment of the raw predicates to existing ontologies can
be accomplished by means of post processing with rdf tooling.

```turtle
[ <urn:File-Date>  "2021-02-16" ] .

[ <urn:Added>        "2009-07-29" ;
  <urn:Description>  "Potawatomi" ;
  <urn:Subtag>       "pot" ;
  <urn:Type>         "language"
] .

```

