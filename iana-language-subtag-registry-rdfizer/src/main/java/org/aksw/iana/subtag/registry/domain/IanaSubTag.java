package org.aksw.iana.subtag.registry.domain;

import org.aksw.jenax.annotation.reprogen.Iri;
import org.aksw.jenax.annotation.reprogen.ResourceView;
import org.apache.jena.rdf.model.Resource;

@ResourceView
public interface IanaSubTag
    extends Resource
{
    // TODO Change String to LocalDate
    @Iri("urn:x-key:Added")
    String getAdded();
    IanaSubTag setAdded(String added);

    // TODO Change String to LocalDate
    @Iri("urn:x-key:Comments")
    String setComments();
    IanaSubTag getComments(String comments);

    @Iri("urn:x-key:Deprecated")
    String getDeprecated();
    IanaSubTag setDeprecated(String deprecated);

    @Iri("urn:x-key:Description")
    String getDescription();
    IanaSubTag setDescription(String description);

    @Iri("urn:x-key:Subtag")
    String getSubtag();
    IanaSubTag setSubtag(String subtag);

    @Iri("urn:x-key:Type")
    String getType();
    IanaSubTag setType(String type);

    @Iri("urn:x-key:Preferred-Value")
    String getPreferredValue();
    IanaSubTag setPreferredValue(String preferredValue);

    @Iri("urn:x-key:Prefix")
    String getPrefix();
    IanaSubTag setPrefix(String prefix);

    @Iri("urn:x-key:MacroLanguage")
    String getMacroLanguage();
    IanaSubTag setMacroLanguage(String macroLanguage);
}
