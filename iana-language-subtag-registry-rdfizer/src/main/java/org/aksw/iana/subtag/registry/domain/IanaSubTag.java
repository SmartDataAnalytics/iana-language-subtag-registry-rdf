package org.aksw.iana.subtag.registry.domain;

import org.aksw.jena_sparql_api.mapper.annotation.Iri;
import org.aksw.jena_sparql_api.mapper.annotation.ResourceView;
import org.apache.jena.rdf.model.Resource;

@ResourceView
public interface IanaSubTag
	extends Resource
{
	@Iri("urn:Type")
	String getType();
	IanaSubTag setType(String type);
	
	@Iri("urn:Subtag")
	String getSubtag();
	IanaSubTag setSubtag(String subtag);
}
