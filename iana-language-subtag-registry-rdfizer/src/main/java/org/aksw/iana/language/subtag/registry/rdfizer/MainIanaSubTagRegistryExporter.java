package org.aksw.iana.language.subtag.registry.rdfizer;

import org.aksw.iana.subtag.registry.domain.IanaSubTag;
import org.aksw.jenax.reprogen.core.JenaPluginUtils;
import org.aksw.jenax.sparql.query.rx.RDFDataMgrEx;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

public class MainIanaSubTagRegistryExporter {


    public static void main(String[] args) throws Exception {
        JenaPluginUtils.registerResourceClasses(IanaSubTag.class);

        //URL url = new URL("http://www.iana.org/assignments/language-subtag-registry/language-subtag-registry");

        Model model = IanaLanguageSubtagRegistryRdfizer.rdfize("http://www.iana.org/assignments/language-subtag-registry/language-subtag-registry");

        Model validated = RDFDataMgrEx.printParseRoundtrip(model, RDFFormat.TURTLE, null);

        RDFDataMgr.write(System.out, validated, RDFFormat.TURTLE_PRETTY);
    }

}
